package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.buffer.impl.safe.SafeDoubleDirectBuf;
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
@PrepareForTest({DoubleReadFile.class})
public class DoubleReadFileTest {

    @Test
    public void getDouble() throws Exception {
        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 3);
        DoubleReadFile rf = spy(new DoubleReadFile(mock(FileBlock.class), connector));
        setInternalState(rf, "buffers", new DoubleDirectBuffer[1]);

        DoubleDirectBuffer buf = mock(DoubleDirectBuffer.class);
        when(buf.getDouble(0)).thenReturn(1D);
        doReturn(buf).when(rf).loadBuffer(anyInt());

        assertEquals(1, rf.getDouble(0), 0);

        try {
            rf.getDouble(1);
            fail();
        } catch (BufferAcquireFailedException ignore) {
        }

        verifyPrivate(rf, times(2)).invoke("ensureOpen");
    }

    @Test
    public void newDirectBuf() throws Exception {
        DoubleReadFile rf = new DoubleReadFile(mock(FileBlock.class), mock(Connector.class));

        FileBlock fileBlock = mock(FileBlock.class);
        DoubleDirectBuf buf = mock(DoubleDirectBuf.class);
        whenNew(DoubleDirectBuf.class).withArguments(1L, 16, fileBlock, 16).thenReturn(buf);
        SafeDoubleDirectBuf safeBuf = mock(SafeDoubleDirectBuf.class);
        whenNew(SafeDoubleDirectBuf.class).withArguments(buf).thenReturn(safeBuf);

        assertEquals(safeBuf, rf.newDirectBuf(1L, 16, fileBlock));
    }
}