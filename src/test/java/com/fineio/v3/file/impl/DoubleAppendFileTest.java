package com.fineio.v3.file.impl;

import com.fineio.v3.buffer.impl.DoubleDirectBuf;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.write.DoubleWriteFile;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
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
@PrepareForTest({DoubleAppendFile.class})
public class DoubleAppendFileTest {

    @Test
    public void putDouble() throws Exception {
        DoubleAppendFile af = mock(DoubleAppendFile.class);
        DoubleWriteFile wf = mock(DoubleWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "putDouble", anyDouble());

        af.putDouble(1);

        verify(wf).putDouble(0, 1);
    }

    @Test
    public void newDirectBuf() throws Exception {
        DoubleAppendFile af = mock(DoubleAppendFile.class);
        DoubleWriteFile wf = mock(DoubleWriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "newDirectBuf", anyLong(), anyInt(), any(FileKey.class));

        setInternalState(wf, "connector", mock(Connector.class));
        setInternalState(wf, "offset", Offset.DOUBLE);

        FileKey fileKey = mock(FileKey.class);
        DoubleDirectBuf buf = mock(DoubleDirectBuf.class);
        whenNew(DoubleDirectBuf.class).withArguments(1L, 1, fileKey, 1 << -3, FileMode.WRITE).thenReturn(buf);

        assertEquals(buf, af.newDirectBuf(1, 1, fileKey));
    }
}