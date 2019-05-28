package com.fineio.v3.file.impl.write;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
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
@PrepareForTest({DoubleWriteFile.class})
public class DoubleWriteFileTest {

    @Test
    public void putDouble() throws Exception {
        DoubleWriteFile wf = spy(DoubleWriteFile.ofSync(mock(FileBlock.class), mock(Connector.class)));
        DoubleDirectBuffer buf = mock(DoubleDirectBuffer.class);
        doReturn(buf).when(wf).getBuffer(0);
        doNothing().when(wf).syncBufIfNeed(anyInt());

        wf.putDouble(0, 0);

        verifyPrivate(wf).invoke("ensureOpen");
        verifyPrivate(wf).invoke("checkPos", 0L);
        verify(wf).syncBufIfNeed(0);
        verify(buf).putDouble(0, 0);
    }

    @Test
    public void getBuffer() throws Exception {
        ConcurrentHashMap<Object, Object> buffers = spy(new ConcurrentHashMap<>());
        whenNew(ConcurrentHashMap.class).withNoArguments().thenReturn(buffers);
        DoubleDirectBuf buf = mock(DoubleDirectBuf.class);
        FileBlock fileBlock = mock(FileBlock.class);
        FileBlock childFileBlock = mock(FileBlock.class);
        whenNew(FileBlock.class).withArguments(fileBlock.getPath(), "0").thenReturn(childFileBlock);
        whenNew(DoubleDirectBuf.class).withArguments(childFileBlock, 1 << -3, FileMode.WRITE).thenReturn(buf);

        DoubleWriteFile wf = DoubleWriteFile.ofSync(fileBlock, mock(Connector.class));

        assertEquals(buf, wf.getBuffer(0));

        verify(buffers).computeIfAbsent(eq(0), any(Function.class));
    }
}