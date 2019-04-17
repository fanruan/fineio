package com.fineio.v3.file.sync;

import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.connector.Connector;

import java.io.BufferedInputStream;
import java.io.IOException;
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
            connector.write(input, buffer.getFileKey());
            // TODO: 2019/4/15 anchore 写完要通知cache该块已过期，重新load
            // TODO: 2019/4/17 anchore 或者直接把buffer加到cache，写后读的场景会更快吧
        } catch (IOException e) {
            FineIOLoggers.getLogger().error(e);
            // TODO: 2019/4/15 anchore 失败了尝试多写几次？
            buffer.close();
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
        return Objects.equals(buffer.getFileKey(), that.buffer.getFileKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer.getFileKey());
    }
}