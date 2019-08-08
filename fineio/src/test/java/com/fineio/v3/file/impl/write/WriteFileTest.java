package com.fineio.v3.file.impl.write;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * @author anchore
 * @date 2019/5/16
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WriteFile.class})
public class WriteFileTest {
    @Mock
    Connector connector;

    DirectBuffer[] buffers;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    WriteFile<?> wf;

    @Before
    public void setUp() {
        setInternalState(wf, "connector", connector);
        buffers = new DirectBuffer[]{mock(DirectBuffer.class)};
        setInternalState(wf, "buffers", buffers);
    }

    @Test
    public void growBufferCache() {
        wf.growBuffers(-1);
        assertThat(getInternalState(wf, DirectBuffer[].class)).isSameAs(buffers);

        wf.growBuffers(0);
        assertThat(getInternalState(wf, DirectBuffer[].class)).isSameAs(buffers);

        wf.growBuffers(3);
        assertThat(getInternalState(wf, DirectBuffer[].class)).isNotSameAs(buffers).hasSize(19);
    }

    @Test
    public void syncBufIfNeed() throws Exception {
        setInternalState(wf, "curBuf", -1);

        wf.syncBufIfNeed(0);
        assertThat((int) getInternalState(wf, "curBuf")).isZero();
        assertThat(buffers[0]).isNotNull();

        wf.syncBufIfNeed(0);
        assertThat((int) getInternalState(wf, "curBuf")).isZero();
        assertThat(buffers[0]).isNotNull();

        wf.syncBufIfNeed(-1);
        assertThat((int) getInternalState(wf, "curBuf")).isZero();
        assertThat(buffers[0]).isNotNull();

        doNothing().when(wf, "syncBuf", any(DirectBuffer.class));

        DirectBuffer toBeSync = buffers[0];
        wf.syncBufIfNeed(1);
        verifyPrivate(wf).invoke("syncBuf", toBeSync);
        assertThat((int) getInternalState(wf, "curBuf")).isEqualTo(1);
        assertThat(buffers[0]).isNull();
    }

    @Test
    public void delete() {
        FileBlock fileBlock = mock(FileBlock.class);
        setInternalState(wf, "fileBlock", fileBlock);

        wf.delete();

        verify(connector).delete(fileBlock);
    }
}