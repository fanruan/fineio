package com.fineio.v3.file.impl.write;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.IntDirectBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.fail;
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
@PrepareForTest({IntWriteFile.class})
public class IntWriteFileTest {

    @Test
    public void putInt() throws Exception {
        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 3);
        final IntWriteFile wf = spy(IntWriteFile.ofSync(mock(FileBlock.class), connector));
        doNothing().when(wf).syncBufIfNeed(anyInt());
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                IntDirectBuffer[] buffers = getInternalState(wf, "buffers");
                int nthBuf = invocation.getArgument(0);
                buffers[nthBuf] = mock(IntDirectBuffer.class);
                buffers[nthBuf].putInt(invocation.<Integer>getArgument(1), invocation.<Integer>getArgument(2));
                return null;
            }
        }).when(wf, "newAndPut", anyInt(), anyInt(), anyInt());

        IntDirectBuffer[] buffers = getInternalState(wf, "buffers");
        wf.putInt(0, 0);
        verify(buffers[0]).putInt(0, 0);

        wf.putInt(2, 0);
        IntDirectBuffer[] grownBuffers = getInternalState(wf, "buffers");
        verify(grownBuffers[1]).putInt(0, 0);

        try {
            wf.putInt(-1, 0);
            fail();
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }

        verifyPrivate(wf, times(3)).invoke("ensureOpen");
        verify(wf).syncBufIfNeed(0);
        verify(wf).syncBufIfNeed(1);
        verify(wf).syncBufIfNeed(-1 >> 1);
    }
}