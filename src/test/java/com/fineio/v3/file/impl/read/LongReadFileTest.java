package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.buffer.impl.guard.SafeLongDirectBuf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LongReadFile.class})
public class LongReadFileTest {

    @Test
    public void getLong() throws Exception {
        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 3);
        LongReadFile rf = spy(new LongReadFile(mock(FileBlock.class), connector));
        setInternalState(rf, "buffers", new LongDirectBuffer[1]);

        LongDirectBuffer buf = mock(LongDirectBuffer.class);
        when(buf.getLong(0)).thenReturn(1L);
        doReturn(buf).when(rf).loadBuffer(anyInt());

        assertEquals(1, rf.getLong(0));

        try {
            rf.getLong(1);
            fail();
        } catch (BufferAcquireFailedException ignore) {
        }

        verifyPrivate(rf, times(2)).invoke("ensureOpen");
    }

    @Test
    public void newDirectBuf() throws Exception {
        LongReadFile rf = new LongReadFile(mock(FileBlock.class), mock(Connector.class));

        FileBlock fileBlock = mock(FileBlock.class);
        LongDirectBuf buf = mock(LongDirectBuf.class);
        whenNew(LongDirectBuf.class).withArguments(1L, 16, fileBlock, 16).thenReturn(buf);
        SafeLongDirectBuf safeBuf = mock(SafeLongDirectBuf.class);
        whenNew(SafeLongDirectBuf.class).withArguments(buf).thenReturn(safeBuf);

        assertEquals(safeBuf, rf.newDirectBuf(1L, 16, fileBlock));
    }
}