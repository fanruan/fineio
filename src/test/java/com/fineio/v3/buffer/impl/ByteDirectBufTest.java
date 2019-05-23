package com.fineio.v3.buffer.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;
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
public class ByteDirectBufTest {

    @Test
    public void putByte() throws Exception {
        spy(BaseDirectBuffer.class);
        doReturn(1L).when(BaseDirectBuffer.class, "allocate", 16, Offset.BYTE, FileMode.WRITE);

        ByteDirectBuf buf = spy(new ByteDirectBuf(mock(FileBlock.class), 1024, FileMode.WRITE));
        mockStatic(MemoryUtils.class);

        buf.putByte(0, (byte) 0);

        verify(buf).ensureOpen();
        verify(buf).ensureCap(0);
        verify(buf).checkPos(0);
        verifyStatic(MemoryUtils.class);
        MemoryUtils.put(1, 0, (byte) 0);
        verify(buf).updateSize(0);
    }

    @Test
    public void getByte() {
        ByteDirectBuf buf = spy(new ByteDirectBuf(1, 16, mock(FileBlock.class), 1024, FileMode.READ));
        mockStatic(MemoryUtils.class);
        when(MemoryUtils.getByte(1, 0)).thenReturn((byte) 1);

        assertEquals(1, buf.getByte(0));

        verify(buf).ensureOpen();
        verify(buf).checkPos(0);
    }
}