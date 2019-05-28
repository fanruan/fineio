package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.impl.DoubleDirectBuf;
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
@PrepareForTest({DoubleReadFile.class})
public class DoubleReadFileTest {

    @Test
    public void getDouble() throws Exception {
        DoubleReadFile rf = spy(new DoubleReadFile(mock(FileBlock.class), mock(Connector.class)));

        DoubleDirectBuffer buf = mock(DoubleDirectBuffer.class);
        doReturn(buf).when(rf).getBuffer(0);

        when(buf.getDouble(0)).thenReturn(1D);

        assertEquals(1, rf.getDouble(0), 0);
        verifyPrivate(rf).invoke("ensureOpen");
        verifyPrivate(rf).invoke("checkPos", 0L);
    }

    @Test
    public void newDirectBuf() throws Exception {
        DoubleReadFile rf = new DoubleReadFile(mock(FileBlock.class), mock(Connector.class));

        FileBlock fileBlock = mock(FileBlock.class);
        DoubleDirectBuf buf = mock(DoubleDirectBuf.class);
        whenNew(DoubleDirectBuf.class).withArguments(1L, 16, fileBlock, 16).thenReturn(buf);

        assertEquals(buf, rf.newDirectBuf(1L, 16, fileBlock));
    }
}