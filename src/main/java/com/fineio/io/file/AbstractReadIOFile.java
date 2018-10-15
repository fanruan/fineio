package com.fineio.io.file;

import com.fineio.base.Bits;
import com.fineio.io.Buffer;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

abstract class AbstractReadIOFile<T extends Buffer> extends IOFile<T> {
    AbstractReadIOFile(final Connector connector, final URI uri, final AbstractFileModel<T> abstractFileModel) {
        super(connector, uri, abstractFileModel);
        this.readHeader(abstractFileModel.offset());
    }

    private void readHeader(final byte b) {
        InputStream read = null;
        try {
            read = this.connector.read(this.createHeadBlock());
            if (read == null) {
            }
            final byte[] array = new byte[9];
            read.read(array);
            int n = 0;
            this.createBufferArray(Bits.getInt(array, n));
            n += 8;
            this.block_size_offset = (byte) (array[n] - b);
        } catch (Throwable t) {
            this.block_size_offset = (byte) (this.connector.getBlockOffset() - b);
        } finally {
            if (null != read) {
                try {
                    read.close();
                } catch (IOException ex) {
                }
            }
        }
        this.single_block_len = (1L << this.block_size_offset) - 1L;
    }
}
