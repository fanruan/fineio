package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.type.FileMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ByteReadFile.class})
public class ByteReadFileTest {

    @Test
    public void getByte() throws Exception {
        ByteReadFile rf = spy(new ByteReadFile(mock(FileBlock.class), mock(Connector.class)));

        ByteDirectBuffer buf = mock(ByteDirectBuffer.class);
        doReturn(buf).when(rf).getBuffer(0);

        when(buf.getByte(0)).thenReturn((byte) 1);

        assertEquals(1, rf.getByte(0));
        verifyPrivate(rf).invoke("ensureOpen");
        verifyPrivate(rf).invoke("checkPos", 0L);
    }

    @Test
    public void newDirectBuf() throws Exception {
        ByteReadFile rf = new ByteReadFile(mock(FileBlock.class), mock(Connector.class));

        FileBlock fileBlock = mock(FileBlock.class);
        ByteDirectBuf buf = mock(ByteDirectBuf.class);
        whenNew(ByteDirectBuf.class).withArguments(1L, 16, fileBlock, 16, FileMode.READ).thenReturn(buf);

        assertEquals(buf, rf.newDirectBuf(1L, 16, fileBlock));
    }
}