package com.fineio.io.edit;

import com.fineio.base.Maths;
import com.fineio.exception.BlockNotFoundException;
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


    protected EditBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }


    private final  void loadData(){
        synchronized (this) {
            if (load) {
                return;
            }
            InputStream is = connector.read(block);
            if (is == null) {
                throw new BlockNotFoundException("block:" + block.toString() + " not found!");
            }
            try {
                int max_byte_len = max_size << getLengthOffset();
                byte[] bytes = new byte[max_byte_len];
                int off = 0;
                int len = 0;
                while ((len = is.read(bytes, off, max_byte_len - off)) > 0) {
                    off+=len;
                }
                int max_position = off >> getLengthOffset();
                int offset = Maths.log2(max_position);
                if(max_position > (1 << offset)){
                    offset++;
                }
                //TODO cache部分要做内存限制等处理  这部分与写共享内存，不考虑边写边释放问题
                address = MemoryUtils.allocate(1 << offset << getLengthOffset());
                MemoryUtils.copyMemory(bytes, address, off);
                load = true;
                setCurrentCapacity(offset);
            } catch (IOException e) {
                throw new BlockNotFoundException("block:" + block.toString() + " not found!");
            }
        }
    }
}
