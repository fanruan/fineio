package com.fineio.v2.io.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v2.io.Buffer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public abstract class BaseReadIOFileV2<B extends Buffer> extends IOFileV2<B> {
    BaseReadIOFileV2(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
        try {
            readHeader(model.offset());
        } catch (BlockNotFoundException e) {
            this.block_size_offset = (byte) (connector.getBlockOffset() - model.offset());
        }
    }


    protected void readHeader(byte offset) {
        InputStream is = null;
        FileBlock head = createHeadBlock();
        try {
            is = this.connector.read(head);
            if (is == null) {
                //throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
            }
            byte[] header = new byte[9];
            is.read(header);
            initBufferArray(offset, header);
            HEAD_MAP.put(head, header);
        } catch (Throwable e) {
            byte[] header = HEAD_MAP.get(head);
            if (null != header) {
                initBufferArray(offset, header);
                return;
            }
            throw new BlockNotFoundException("block:" + uri.toString() + " not found!");
//            this.block_size_offset = (byte) (connector.getBlockOffset() - offset);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            single_block_len = (1L << block_size_offset) - 1;
        }
    }

    private void initBufferArray(byte offset, byte[] header) {
        int p = 0;
        createBufferArray(Bits.getInt(header, p));
        //先空个long的位置
        p += IOFileV2.STEP_LEN;
        block_size_offset = (byte) (header[p] - offset);
    }
}
