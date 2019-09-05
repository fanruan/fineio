package com.fineio.v3.file.sync;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.BufferCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedInputStream;
import java.io.IOException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/9/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BufferCache.class})
public class FileSyncJobTest {

    @Test
    public void run() throws Exception {
        final DirectBuffer buffer = mock(DirectBuffer.class);
        final Connector connector = mock(Connector.class);
        final FileSyncJob job = new FileSyncJob(buffer, connector);

        final BufferCache bufferCache = mock(BufferCache.class);
        mockStatic(BufferCache.class);
        when(BufferCache.get()).thenReturn(bufferCache);

        final FileBlock fb = mock(FileBlock.class);
        when(buffer.getFileBlock()).thenReturn(fb);
        // 正常sync
        doNothing().when(connector).write(eq(fb), any(BufferedInputStream.class));
        job.run();
        // sync失败
        doThrow(new IOException()).when(connector).write(eq(fb), any(BufferedInputStream.class));
        job.run();

        verify(bufferCache, times(2)).invalidate(fb);
    }
}