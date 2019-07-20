package com.fineio.v3.file.impl.write;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.ByteDirectBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyByte;
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
@PrepareForTest({ByteWriteFile.class})
public class ByteWriteFileTest {

    @Test
    public void putByte() throws Exception {
        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 1);
        ByteWriteFile wf = spy(ByteWriteFile.ofSync(mock(FileBlock.class), connector));
        doNothing().when(wf).syncBufIfNeed(anyInt());
        doAnswer(invocation -> {
            ByteDirectBuffer[] buffers = getInternalState(wf, "buffers");
            int nthBuf = invocation.getArgument(0);
            buffers[nthBuf] = mock(ByteDirectBuffer.class);
            buffers[nthBuf].putByte(invocation.getArgument(1), invocation.getArgument(2));
            return null;
        }).when(wf, "newAndPut", anyInt(), anyInt(), anyByte());

        ByteDirectBuffer[] buffers = getInternalState(wf, "buffers");
        wf.putByte(0, (byte) 0);
        verify(buffers[0]).putByte(0, (byte) 0);

        wf.putByte(2, (byte) 0);
        ByteDirectBuffer[] grownBuffers = getInternalState(wf, "buffers");
        verify(grownBuffers[1]).putByte(0, (byte) 0);

        try {
            wf.putByte(-1, (byte) 0);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }

        verifyPrivate(wf, times(3)).invoke("ensureOpen");
        verify(wf).syncBufIfNeed(0);
        verify(wf).syncBufIfNeed(1);
        verify(wf).syncBufIfNeed(-1 >> 1);
    }
}