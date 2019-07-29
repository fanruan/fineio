package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.BufferAcquireFailedException;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.buffer.impl.guard.SafeByteDirectBuf;
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
@PrepareForTest({ByteReadFile.class})
public class ByteReadFileTest {

    @Test
    public void getByte() throws Exception {
        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 0);
        ByteReadFile rf = spy(new ByteReadFile(mock(FileBlock.class), connector));
        setInternalState(rf, "buffers", new ByteDirectBuffer[1]);

        ByteDirectBuffer buf = mock(ByteDirectBuffer.class);
        when(buf.getByte(0)).thenReturn((byte) 1);
        doReturn(buf).when(rf).loadBuffer(anyInt());

        assertEquals(1, rf.getByte(0), 0);

        try {
            rf.getByte(1);
            fail();
        } catch (BufferAcquireFailedException ignore) {
        }

        verifyPrivate(rf, times(2)).invoke("ensureOpen");
    }

    @Test
    public void newDirectBuf() throws Exception {
        ByteReadFile rf = new ByteReadFile(mock(FileBlock.class), mock(Connector.class));

        FileBlock fileBlock = mock(FileBlock.class);
        ByteDirectBuf buf = mock(ByteDirectBuf.class);
        whenNew(ByteDirectBuf.class).withArguments(1L, 16, fileBlock, 16).thenReturn(buf);
        SafeByteDirectBuf safeBuf = mock(SafeByteDirectBuf.class);
        whenNew(SafeByteDirectBuf.class).withArguments(buf).thenReturn(safeBuf);

        assertEquals(safeBuf, rf.newDirectBuf(1L, 16, fileBlock));
    }
}