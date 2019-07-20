package com.fineio.v3.file.impl.write;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.getInternalState;

/**
 * @author anchore
 * @date 2019/5/17
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DoubleWriteFile.class})
public class DoubleWriteFileTest {

    @Test
    public void putDouble() throws Exception {
        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 4);
        DoubleWriteFile wf = spy(DoubleWriteFile.ofSync(mock(FileBlock.class), connector));
        doNothing().when(wf).syncBufIfNeed(anyInt());
        doAnswer(invocation -> {
            DoubleDirectBuffer[] buffers = getInternalState(wf, "buffers");
            int nthBuf = invocation.getArgument(0);
            buffers[nthBuf] = mock(DoubleDirectBuffer.class);
            buffers[nthBuf].putDouble(invocation.getArgument(1), invocation.getArgument(2));
            return null;
        }).when(wf, "newAndPut", anyInt(), anyInt(), anyDouble());

        DoubleDirectBuffer[] buffers = getInternalState(wf, "buffers");
        wf.putDouble(0, 0);
        verify(buffers[0]).putDouble(0, 0);

        wf.putDouble(2, 0);
        DoubleDirectBuffer[] grownBuffers = getInternalState(wf, "buffers");
        verify(grownBuffers[1]).putDouble(0, 0);

        try {
            wf.putDouble(-1, 0);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }

        verifyPrivate(wf, times(3)).invoke("ensureOpen");
        verify(wf).syncBufIfNeed(0);
        verify(wf).syncBufIfNeed(1);
        verify(wf).syncBufIfNeed(-1 >> 1);
    }
}