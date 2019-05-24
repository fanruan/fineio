package com.fineio.v3.file.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.impl.ByteDirectBuf;
import com.fineio.v3.file.impl.write.ByteWriteFile;
import com.fineio.v3.memory.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * @author anchore
 * @date 2019/5/20
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ByteAppendFile.class})
public class ByteAppendFileTest {

    @Test
    public void putByte() throws Exception {
        ByteAppendFile af = mock(ByteAppendFile.class);
        ByteWriteFile wf = mock(ByteWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "putByte", anyByte());

        af.putByte((byte) 1);

        verify(wf).putByte(0, (byte) 1);
    }

    @Test
    public void newDirectBuf() throws Exception {
        ByteAppendFile af = mock(ByteAppendFile.class);
        ByteWriteFile wf = mock(ByteWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "newDirectBuf", anyLong(), anyInt(), any(FileBlock.class));

        setInternalState(wf, "connector", mock(Connector.class));
        setInternalState(wf, "offset", Offset.BYTE);

        FileBlock fileBlock = mock(FileBlock.class);
        ByteDirectBuf buf = mock(ByteDirectBuf.class);
        whenNew(ByteDirectBuf.class).withArguments(1L, 1, fileBlock, 1, FileMode.WRITE).thenReturn(buf);

        assertEquals(buf, af.newDirectBuf(1, 1, fileBlock));
    }
}