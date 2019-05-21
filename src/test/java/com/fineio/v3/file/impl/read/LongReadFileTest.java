package com.fineio.v3.file.impl.read;

import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
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
@PrepareForTest({LongReadFile.class})
public class LongReadFileTest {

    @Test
    public void getLong() throws Exception {
        LongReadFile rf = spy(new LongReadFile(mock(FileKey.class), mock(Connector.class)));

        LongDirectBuffer buf = mock(LongDirectBuffer.class);
        doReturn(buf).when(rf).getBuffer(0);

        when(buf.getLong(0)).thenReturn(1L);

        assertEquals(1, rf.getLong(0));
        verifyPrivate(rf).invoke("ensureOpen");
        verifyPrivate(rf).invoke("checkPos", 0L);
    }

    @Test
    public void newDirectBuf() throws Exception {
        LongReadFile rf = new LongReadFile(mock(FileKey.class), mock(Connector.class));

        FileKey fileKey = mock(FileKey.class);
        LongDirectBuf buf = mock(LongDirectBuf.class);
        whenNew(LongDirectBuf.class).withArguments(1L, 16, fileKey, 16, FileMode.READ).thenReturn(buf);

        assertEquals(buf, rf.newDirectBuf(1L, 16, fileKey));
    }
}