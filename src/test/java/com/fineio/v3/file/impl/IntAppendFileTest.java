package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.impl.IntDirectBuf;
import com.fineio.v3.file.impl.write.IntWriteFile;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
@PrepareForTest({IntAppendFile.class})
public class IntAppendFileTest {

    @Test
    public void putInt() throws Exception {
        IntAppendFile af = mock(IntAppendFile.class);
        IntWriteFile wf = mock(IntWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "putInt", anyInt());

        af.putInt(1);

        verify(wf).putInt(0, 1);
    }

    @Test
    public void newDirectBuf() throws Exception {
        IntAppendFile af = mock(IntAppendFile.class);
        IntWriteFile wf = mock(IntWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "newDirectBuf", anyLong(), anyInt(), any(FileBlock.class));

        setInternalState(wf, "connector", mock(Connector.class));
        setInternalState(wf, "offset", Offset.INT);

        FileBlock FileBlock = mock(FileBlock.class);
        IntDirectBuf buf = mock(IntDirectBuf.class);
        whenNew(IntDirectBuf.class).withArguments(1L, 1, FileBlock, 1 << -2, FileMode.WRITE).thenReturn(buf);

        assertEquals(buf, af.newDirectBuf(1, 1, FileBlock));
    }
}