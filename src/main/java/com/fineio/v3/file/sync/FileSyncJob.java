package com.fineio.v3.file.sync;

import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.BufferCache;

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
        try (DirectBuffer buf = buffer;
             InputStream input = new BufferedInputStream(new DirectMemoryInputStream(buf.getAddress(), buf.getSizeInBytes()))) {
            connector.write(buf.getFileBlock(), input);
        } catch (Throwable e) {
            FineIOLoggers.getLogger().error(e);
        } finally {
            // 通知cache该块已过期，重新load
            BufferCache.get().invalidate(buffer.getFileBlock());
        }
    }

//    /**
//     * @deprecated 实际效果不理想，disable了
//     */
//    @Deprecated
//    private void transferOrInvalidate() {
//        try {
//            // TODO: 2019/4/17 anchore 或者直接把buffer加到cache，写后读的场景会更快吧
//            MemoryManager.INSTANCE.transferWriteToRead(buffer.getAddress(), buffer.getSizeInBytes());
//            BufferCache.get().put(buffer.getFileBlock(), makeSafe(buffer));
//        } catch (Throwable e) {
//            buffer.close();
//            // TODO: 2019/4/15 anchore 写完要通知cache该块已过期，重新load
//            BufferCache.get().invalidate(buffer.getFileBlock());
//
//            FineIOLoggers.getLogger().error(e);
//        }
//    }
//
//    private DirectBuffer makeSafe(DirectBuffer buf) {
//        if (buf instanceof ByteDirectBuffer) {
//            return new SafeByteDirectBuf((ByteDirectBuffer) buf);
//        }
//        if (buf instanceof IntDirectBuffer) {
//            return new SafeIntDirectBuf((IntDirectBuffer) buf);
//        }
//        if (buf instanceof LongDirectBuffer) {
//            return new SafeLongDirectBuf((LongDirectBuffer) buf);
//        }
//        if (buf instanceof DoubleDirectBuffer) {
//            return new SafeDoubleDirectBuf((DoubleDirectBuffer) buf);
//        }
//        throw new IllegalArgumentException(String.format("cannot make safe buffer of %s", buf));
//    }

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