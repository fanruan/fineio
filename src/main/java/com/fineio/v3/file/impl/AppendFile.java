package com.fineio.v3.file.impl;

import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.write.WriteFile;
import com.fineio.v3.memory.MemoryUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author yee
 */
abstract class AppendFile<WF extends WriteFile<B>, B extends DirectBuffer> {
    private static final String LAST_POS = "last_pos";

    int lastPos;

    final WF writeFile;

    AppendFile(WF writeFile) {
        this.writeFile = writeFile;
        initLastPos();
        initLastBuf();
    }

    private void initLastPos() {
        Connector connector = writeFile.connector;
        FileKey fileKey = new FileKey(writeFile.fileKey.getPath(), LAST_POS);
        if (connector.exists(fileKey)) {
            try (InputStream input = connector.read(fileKey)) {
                byte[] bytes = new byte[4];
                if (input.read(bytes) == bytes.length) {
                    lastPos = ByteBuffer.wrap(bytes).getInt();
                }
            } catch (IOException e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    private void initLastBuf() {
        int nthVal = writeFile.nthVal(lastPos);
        if (nthVal == 0) {
            // 此buf没数据，不用读connector
            return;
        }
        int nthBuf = writeFile.nthBuf(lastPos);
        FileKey lastPosFileKey = new FileKey(writeFile.fileKey.getPath(), String.valueOf(nthBuf));
        if (writeFile.connector.exists(lastPosFileKey)) {
            Long address = null;
            try (InputStream input = new BufferedInputStream(writeFile.connector.read(lastPosFileKey))) {
                int avail = input.available();
                address = MemoryUtils.allocate(avail);
                long ptr = address;
                byte[] bytes = new byte[1024];
                for (int read; (read = input.read(bytes)) != -1; ptr += read) {
                    MemoryUtils.copyMemory(bytes, ptr, read);
                }
                writeFile.buffers.put(nthBuf, newDirectBuf(address, (int) ((ptr - address) >> writeFile.offset.getOffset()), lastPosFileKey));
            } catch (Throwable e) {
                if (address != null) {
                    MemoryUtils.free(address);
                }
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    abstract B newDirectBuf(long address, int size, FileKey fileKey);

    public void close() {
        try {
            writeFile.close();
        } finally {
            writeLastPos();
        }
    }

    private void writeLastPos() {
        byte[] bytes = ByteBuffer.allocate(4).putInt(lastPos).array();
        try {
            writeFile.connector.write(bytes, new FileKey(writeFile.fileKey.getPath(), LAST_POS));
        } catch (IOException e) {
            FineIOLoggers.getLogger().error(e);
        }
    }
}