package com.fineio.v3.file.impl.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.impl.IntDirectBuf;
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
@PrepareForTest({IntReadFile.class})
public class IntReadFileTest {

    @Test
    public void getInt() throws Exception {
        IntReadFile rf = spy(new IntReadFile(mock(FileBlock.class), mock(Connector.class)));

        IntDirectBuffer buf = mock(IntDirectBuffer.class);
        doReturn(buf).when(rf).getBuffer(0);

        when(buf.getInt(0)).thenReturn(1);

        assertEquals(1, rf.getInt(0));
        verifyPrivate(rf).invoke("ensureOpen");
        verifyPrivate(rf).invoke("checkPos", 0L);
    }

    @Test
    public void newDirectBuf() throws Exception {
        IntReadFile rf = new IntReadFile(mock(FileBlock.class), mock(Connector.class));

        FileBlock fileBlock = mock(FileBlock.class);
        IntDirectBuf buf = mock(IntDirectBuf.class);
        whenNew(IntDirectBuf.class).withArguments(1L, 16, fileBlock, 16, FileMode.READ).thenReturn(buf);

        assertEquals(buf, rf.newDirectBuf(1L, 16, fileBlock));
    }
}