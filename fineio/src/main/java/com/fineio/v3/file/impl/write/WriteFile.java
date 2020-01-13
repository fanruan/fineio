package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.File;
import com.fineio.v3.file.sync.FileSync;
import com.fineio.v3.file.sync.FileSyncJob;
import com.fineio.v3.memory.MemoryManager;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author yee
 */
public abstract class WriteFile<B extends DirectBuffer> extends File<B> implements IWriteFile<B>, com.fineio.accessor.file.IFile<B> {
    private final boolean asyncWrite;
    private int curBuf = -1;
    private long lastPos;

    WriteFile(FileBlock fileBlock, Offset offset, Connector connector, boolean asyncWrite) {
        super(fileBlock, offset, connector);
        this.asyncWrite = asyncWrite;

        lastPos = initMetaAndGetLastPos();
    }

    void initLastBuf() {
        if (nthVal(lastPos) == 0) {
            // 此buf没数据，不用读connector
            return;
        }
        int nthBuf = nthBuf(lastPos);
        FileBlock lastFileBlock = new FileBlock(fileBlock.getPath(), String.valueOf(nthBuf));
        if (connector.exists(lastFileBlock)) {
            Long address = null;
            int size = 0;
            try (InputStream input = new BufferedInputStream(connector.read(lastFileBlock));
                 ByteArrayOutputStream byteOutput = new ByteArrayOutputStream()) {
                IOUtils.copyBinaryTo(input, byteOutput);
                size = byteOutput.size();
                address = MemoryManager.INSTANCE.allocate(size, FileMode.WRITE);
                MemoryUtils.copyMemory(byteOutput.toByteArray(), address, size);

                growBuffers(nthBuf);
                buffers[nthBuf] = newDirectBuf(address, size >> offset.getOffset(), lastFileBlock, 1 << (blockOffset - offset.getOffset()));
            } catch (Throwable e) {
                if (address != null) {
                    MemoryManager.INSTANCE.release(address, size);
                }
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    abstract B newDirectBuf(long address, int size, FileBlock fileBlock, int maxCap);

    /**
     * 给append file的后门
     *
     * @param nthBuf 第n个buf
     */
    void growBuffers(int nthBuf) {
        if (nthBuf >= buffers.length) {
            buffers = Arrays.copyOf(buffers, nthBuf + 16);
        }
    }

    void updateLastPos(long pos) {
        if (pos >= lastPos) {
            lastPos = pos + 1;
        }
    }

    void syncBufIfNeed(int nthBuf) {
        if (curBuf == -1) {
            curBuf = nthBuf;
        } else if (curBuf != nthBuf && nthBuf >= 0) {
            syncBuf(buffers[curBuf]);
            buffers[curBuf] = null;

            curBuf = nthBuf;
        }
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            syncBufs();
            writeMeta(lastPos);
        }
    }

    private void syncBufs() {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i] != null) {
                try {
                    syncBuf(buffers[i]);
                    buffers[i] = null;
                } catch (Exception e) {
                    FineIOLoggers.getLogger().error(e);
                }
            }
        }
    }

    private void syncBuf(B buf) {
        FileSyncJob job = new FileSyncJob(buf, connector);
        if (asyncWrite) {
            FileSync.get().submit(job);
        } else {
            try {
                job.run();
            } catch (Exception e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    void delete() {
        connector.delete(fileBlock);
    }
}