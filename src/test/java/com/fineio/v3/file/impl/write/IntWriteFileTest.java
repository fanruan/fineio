package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.type.FileMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/5/17
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IntWriteFile.class})
public class IntWriteFileTest {

    @Test
    public void putInt() throws Exception {
        IntWriteFile wf = spy(IntWriteFile.ofSync(mock(FileKey.class), mock(Connector.class)));
        IntDirectBuffer buf = mock(IntDirectBuffer.class);
        doReturn(buf).when(wf).getBuffer(0);
        doNothing().when(wf).syncBufIfNeed(anyInt());

        wf.putInt(0, 0);

        verifyPrivate(wf).invoke("ensureOpen");
        verifyPrivate(wf).invoke("checkPos", 0L);
        verify(wf).syncBufIfNeed(0);
        verify(buf).putInt(0, 0);
    }

    @Test
    public void getBuffer() throws Exception {
        ConcurrentHashMap<Object, Object> buffers = spy(new ConcurrentHashMap<>());
        whenNew(ConcurrentHashMap.class).withNoArguments().thenReturn(buffers);
        IntDirectBuf buf = mock(IntDirectBuf.class);
        FileKey fileKey = mock(FileKey.class);
        FileKey childFileKey = mock(FileKey.class);
        whenNew(FileKey.class).withArguments(fileKey.getPath(), "0").thenReturn(childFileKey);
        whenNew(IntDirectBuf.class).withArguments(childFileKey, 1 << -2, FileMode.WRITE).thenReturn(buf);

        IntWriteFile wf = IntWriteFile.ofSync(fileKey, mock(Connector.class));

        assertEquals(buf, wf.getBuffer(0));

        verify(buffers).computeIfAbsent(eq(0), any(Function.class));
    }
}