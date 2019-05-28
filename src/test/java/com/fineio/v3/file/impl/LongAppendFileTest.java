package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.impl.LongDirectBuf;
import com.fineio.v3.file.impl.write.LongWriteFile;
import com.fineio.v3.memory.Offset;
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
@PrepareForTest({LongAppendFile.class})
public class LongAppendFileTest {

    @Test
    public void putLong() throws Exception {
        LongAppendFile af = mock(LongAppendFile.class);
        LongWriteFile wf = mock(LongWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "putLong", anyLong());

        af.putLong(1);

        verify(wf).putLong(0, 1);
    }

    @Test
    public void newDirectBuf() throws Exception {
        LongAppendFile af = mock(LongAppendFile.class);
        LongWriteFile wf = mock(LongWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "newDirectBuf", anyLong(), anyInt(), any(FileBlock.class));

        setInternalState(wf, "connector", mock(Connector.class));
        setInternalState(wf, "offset", Offset.LONG);

        FileBlock fileBlock = mock(FileBlock.class);
        LongDirectBuf buf = mock(LongDirectBuf.class);
        whenNew(LongDirectBuf.class).withArguments(1L, 1, fileBlock, 1 << -3).thenReturn(buf);

        assertEquals(buf, af.newDirectBuf(1, 1, fileBlock));
    }
}