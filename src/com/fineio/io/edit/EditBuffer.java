package com.fineio.io.edit;

import com.fineio.base.Maths;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.file.FileBlock;
import com.fineio.io.write.WriteBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daniel on 2017/2/20.
 */
public abstract class EditBuffer extends WriteBuffer implements Edit {

    private volatile boolean load = false;

    protected volatile boolean changed = false;


    protected EditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    public boolean hasChanged() {
        return changed;
    }

    private final  void loadData(){
        synchronized (this) {
            if (load) {
                return;
            }
            InputStream is = connector.read(block);
            int max_byte_len = max_size << getLengthOffset();
            byte[] bytes = new byte[max_byte_len];
            int off = 0;
            int len = 0;
            if (is != null) {
                try {
                    while ((len = is.read(bytes, off, max_byte_len - off)) > 0) {
                        off += len;
                    }
                } catch (IOException e) {
                }
            }
            int max_position = off >> getLengthOffset();
            int offset = Maths.log2(max_position);
            if(max_position > (1 << offset)){
                offset++;
            }
            //TODO cache部分要做内存限制等处理  这部分与写共享内存，不考虑边写边释放问题
            len = 1 << offset << getLengthOffset();
            address = MemoryUtils.allocate(len);
            MemoryUtils.copyMemory(bytes, address, off);
            MemoryUtils.fill0(address + off, len - off);
            load = true;
            this.max_position = max_position;
            setCurrentCapacity(offset);
        }
    }

    protected void ensureCapacity(int position){
        if(!load) {
            loadData();
        }
        super.ensureCapacity(position);
    }

    protected final void checkIndex(int p) {
        if (ir(p)){
            return;
        }
        lc(p);
    }

    private final boolean ir(int p){
        return p > -1 && p < current_max_size;
    }

    private final void lc(int p) {
        synchronized (this) {
            if (load) {
                if (ir(p)){
                    return;
                }
                throw new BufferIndexOutOfBoundsException(p);
            } else {
                ll(p);
            }
        }
    }

    private final void ll(int p) {
        loadData();
        checkIndex(p);
    }
}
