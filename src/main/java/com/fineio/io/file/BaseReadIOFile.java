package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public abstract class BaseReadIOFile<B extends Buffer> extends IOFile<B> {
    BaseReadIOFile(Connector connector, URI uri, FileModel model) {
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
            p += IOFile.STEP_LEN;
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
}
