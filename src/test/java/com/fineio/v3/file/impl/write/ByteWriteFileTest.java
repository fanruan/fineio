package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
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
@PrepareForTest({ByteWriteFile.class})
public class ByteWriteFileTest {

    @Test
    public void putByte() throws Exception {
        ByteWriteFile wf = spy(ByteWriteFile.ofSync(mock(FileKey.class), mock(Connector.class)));
        ByteDirectBuffer buf = mock(ByteDirectBuffer.class);
        doReturn(buf).when(wf).getBuffer(0);
        doNothing().when(wf).syncBufIfNeed(anyInt());

        wf.putByte(0, (byte) 0);

        verifyPrivate(wf).invoke("ensureOpen");
        verifyPrivate(wf).invoke("checkPos", 0L);
        verify(wf).syncBufIfNeed(0);
        verify(buf).putByte(0, (byte) 0);
    }

    @Test
    public void getBuffer() throws Exception {
        ConcurrentHashMap<Object, Object> buffers = spy(new ConcurrentHashMap<>());
        whenNew(ConcurrentHashMap.class).withNoArguments().thenReturn(buffers);
        ByteDirectBuf buf = mock(ByteDirectBuf.class);
        FileKey fileKey = mock(FileKey.class);
        FileKey childFileKey = mock(FileKey.class);
        whenNew(FileKey.class).withArguments(fileKey.getPath(), "0").thenReturn(childFileKey);
        whenNew(ByteDirectBuf.class).withArguments(childFileKey, 1, FileMode.WRITE).thenReturn(buf);

        ByteWriteFile wf = ByteWriteFile.ofSync(fileKey, mock(Connector.class));

        assertEquals(buf, wf.getBuffer(0));

        verify(buffers).computeIfAbsent(eq(0), any(Function.class));
    }
}