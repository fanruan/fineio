package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.buffer.impl.safe.SafeIntDirectBuf;
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
@PrepareForTest({IntReadFile.class})
public class IntReadFileTest {

    @Test
    public void getInt() throws Exception {
        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 2);
        IntReadFile rf = spy(new IntReadFile(mock(FileBlock.class), connector));
        setInternalState(rf, "buffers", new IntDirectBuffer[1]);

        IntDirectBuffer buf = mock(IntDirectBuffer.class);
        when(buf.getInt(0)).thenReturn(1);
        doReturn(buf).when(rf).loadBuffer(anyInt());

        assertEquals(1, rf.getInt(0));

        try {
            rf.getInt(1);
            fail();
        } catch (BufferAcquireFailedException ignore) {
        }

        verifyPrivate(rf, times(2)).invoke("ensureOpen");
    }

    @Test
    public void newDirectBuf() throws Exception {
        IntReadFile rf = new IntReadFile(mock(FileBlock.class), mock(Connector.class));

        FileBlock fileBlock = mock(FileBlock.class);
        IntDirectBuf buf = mock(IntDirectBuf.class);
        whenNew(IntDirectBuf.class).withArguments(1L, 16, fileBlock, 16).thenReturn(buf);
        SafeIntDirectBuf safeBuf = mock(SafeIntDirectBuf.class);
        whenNew(SafeIntDirectBuf.class).withArguments(buf).thenReturn(safeBuf);

        assertEquals(safeBuf, rf.newDirectBuf(1L, 16, fileBlock));
    }
}