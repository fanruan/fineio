package com.fineio.v3.buffer.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/5/16
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MemoryUtils.class, BaseDirectBuffer.class})
public class IntDirectBufTest {

    @Test
    public void putInt() throws Exception {
        spy(BaseDirectBuffer.class);
        doReturn(1L).when(BaseDirectBuffer.class, "allocate", 16, Offset.INT, FileMode.WRITE);

        IntDirectBuf buf = spy(new IntDirectBuf(mock(FileBlock.class), 1024, FileMode.WRITE));
        mockStatic(MemoryUtils.class);

        buf.putInt(0, 0);

        verify(buf).ensureOpen();
        verify(buf).ensureCap(0);
        verify(buf).checkPos(0);
        verifyStatic(MemoryUtils.class);
        MemoryUtils.put(1, 0, 0);
        verify(buf).updateSize(0);
    }

    @Test
    public void getInt() {
        IntDirectBuf buf = spy(new IntDirectBuf(1, 16, mock(FileBlock.class), 1024));
        mockStatic(MemoryUtils.class);
        when(MemoryUtils.getInt(1, 0)).thenReturn(1);

        assertEquals(1, buf.getInt(0), 0);

        verify(buf).ensureOpen();
        verify(buf).checkPos(0);
    }
}