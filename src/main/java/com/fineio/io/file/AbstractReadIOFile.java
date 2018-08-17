package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.io.Buffer;
import com.fineio.io.FileModel;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by daniel on 2017/2/20.
 */
abstract class AbstractReadIOFile<T extends Buffer> extends IOFile<T> {


    AbstractReadIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        readHeader(model.offset());
    }


    private void readHeader(byte offset) {
        InputStream is = null;
        try {
            is = this.connector.read(createHeadBlock());
            if (is == null) {
                //throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
            }
            byte[] header = new byte[9];
            is.read(header);
            int p = 0;
            createBufferArray(Bits.getInt(header, p));
            //先空个long的位置
            p += MemoryConstants.STEP_LONG;
            block_size_offset = (byte) (header[p] - offset);
        } catch (Throwable e) {
            // throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
            this.block_size_offset = (byte) (connector.getBlockOffset() - offset);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        single_block_len = (1L << block_size_offset) - 1;
    }

    /**
     * 删除操作
     *
     * @return
     */
    public boolean delete() {
        synchronized (this) {
            boolean delete = connector.delete(createHeadBlock());
            if (buffers != null) {
                for (int i = 0; i < buffers.length; i++) {
                    //内存泄露
                    if (!released && buffers[i] != null && null != buffers[i].get()) {
                        buffers[i].get().closeWithOutSync();
                        buffers[i] = null;
                    }
                    boolean v = connector.delete(createIndexBlock(i));
                    if (delete) {
                        delete = v;
                    }
                }
            }
            boolean v = connector.delete(new FileBlock(uri));
            if (delete) {
                delete = v;
            }
            URI parentURI = uri;
            while (null != (parentURI = connector.deleteParent(new FileBlock(parentURI)))) ;
            released = true;
            return delete;
        }
    }
}
