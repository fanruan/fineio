package com.fineio.v3.file.sync;

import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.BufferCache;
import com.fineio.v3.memory.MemoryManager;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author anchore
 * @date 2019/4/15
 */
public class FileSyncJob implements Runnable {
    private final DirectBuffer buffer;

    private final Connector connector;

    public FileSyncJob(DirectBuffer buffer, Connector connector) {
        this.buffer = buffer;
        this.connector = connector;
    }

    @Override
    public void run() {
        try (InputStream input = new BufferedInputStream(new DirectMemoryInputStream(buffer.getAddress(), buffer.getSizeInBytes()))) {
            connector.write(buffer.getFileBlock(), input);

            transferOrInvalidate();
        } catch (Throwable e) {
            FineIOLoggers.getLogger().error(e);
            // TODO: 2019/4/15 anchore 失败了尝试多写几次？
            buffer.close();
        }
    }

    private void transferOrInvalidate() {
        try {
            // TODO: 2019/4/17 anchore 或者直接把buffer加到cache，写后读的场景会更快吧
            MemoryManager.INSTANCE.transferWriteToRead(buffer.getAddress(), buffer.getSizeInBytes());
            BufferCache.get().put(buffer.getFileBlock(), buffer);
        } catch (Throwable e) {
            // TODO: 2019/4/15 anchore 写完要通知cache该块已过期，重新load
            BufferCache.get().invalidate(buffer.getFileBlock());

            FineIOLoggers.getLogger().error(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileSyncJob that = (FileSyncJob) o;
        return Objects.equals(buffer.getFileBlock(), that.buffer.getFileBlock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer.getFileBlock());
    }
}