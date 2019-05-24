package com.fineio.v3.file.impl;

import com.fineio.accessor.FileMode;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.write.WriteFile;
import com.fineio.v3.memory.MemoryManager;
import com.fineio.v3.memory.MemoryUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author yee
 */
abstract class AppendFile<WF extends WriteFile<B>, B extends DirectBuffer> implements IAppendFile<B> {
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
        FileBlock fileBlock = new FileBlock(writeFile.fileBlock.getPath(), LAST_POS);
        if (connector.exists(fileBlock)) {
            try (InputStream input = connector.read(fileBlock)) {
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
        FileBlock lastFileBlock = new FileBlock(writeFile.fileBlock.getPath(), String.valueOf(nthBuf));
        if (writeFile.connector.exists(lastFileBlock)) {
            Long address = null;
            int avail = 0;
            try (InputStream input = new BufferedInputStream(writeFile.connector.read(lastFileBlock))) {
                avail = input.available();
                address = MemoryManager.INSTANCE.allocate(avail, FileMode.WRITE);
                long ptr = address;
                byte[] bytes = new byte[1024];
                for (int read; (read = input.read(bytes)) != -1; ptr += read) {
                    MemoryUtils.copyMemory(bytes, ptr, read);
                }
                writeFile.buffers.put(nthBuf, newDirectBuf(address, (int) ((ptr - address) >> writeFile.offset.getOffset()), lastFileBlock));
            } catch (Throwable e) {
                if (address != null) {
                    MemoryManager.INSTANCE.release(address, avail, FileMode.WRITE);
                }
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    abstract B newDirectBuf(long address, int size, FileBlock fileBlock);

    @Override
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
            writeFile.connector.write(new FileBlock(writeFile.fileBlock.getPath(), LAST_POS), bytes);
        } catch (IOException e) {
            FineIOLoggers.getLogger().error(e);
        }
    }
}