package com.fineio.v3.file.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.write.WriteFile;
import com.fineio.v3.memory.MemoryManager;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.memory.allocator.WriteMemoryAllocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AppendFile.class, MemoryUtils.class})
public class AppendFileTest {

    @Test
    public void initLastBuf() throws Exception {
        AppendFile af = mock(AppendFile.class);
        doCallRealMethod().when(af, "initLastBuf");

        WriteFile wf = mock(WriteFile.class);
        setInternalState(af, "writeFile", wf);
        when(wf.nthVal(anyInt())).thenReturn(0, 1);

        FileBlock fileBlock = mock(FileBlock.class);
        setInternalState(wf, "fileBlock", fileBlock);

        Connector connector = mock(Connector.class);
        setInternalState(wf, "connector", connector);

        when(connector.exists(any(FileBlock.class))).thenReturn(false, true);
        InputStream input = mock(InputStream.class, CALLS_REAL_METHODS);
        when(connector.read(any(FileBlock.class))).thenReturn(input);

        when(input.available()).thenReturn(1);
        when(input.read()).thenReturn(1, -1);

        mockStatic(MemoryUtils.class);
        doNothing().when(MemoryUtils.class);
        MemoryUtils.copyMemory(any(byte[].class), anyLong(), eq(1));

        setInternalState(wf, "offset", Offset.BYTE);

        DirectBuffer[] buffers = new DirectBuffer[1];
        setInternalState(wf, "buffers", buffers);

        DirectBuffer buf = mock(DirectBuffer.class);
        doReturn(buf).when(af).newDirectBuf(anyLong(), eq(1), any(FileBlock.class));

        invokeMethod(af, "initLastBuf");
        // nthVal == 0
        PowerMockito.verifyZeroInteractions(connector);

        invokeMethod(af, "initLastBuf");
        // not exists
        verify(connector, never()).read(any(FileBlock.class));

        invokeMethod(af, "initLastBuf");

        assertThat(buffers[0]).isEqualTo(buf);

        when(input.read()).thenReturn(1, -1);

        doThrow(new Error()).when(MemoryUtils.class);
        MemoryUtils.copyMemory(any(byte[].class), anyLong(), anyLong());

        WriteMemoryAllocator reAllocator = spy(Whitebox.<WriteMemoryAllocator>getInternalState(MemoryManager.INSTANCE, "reAllocator"));
        setInternalState(MemoryManager.INSTANCE, "reAllocator", reAllocator);

        invokeMethod(af, "initLastBuf");
        // throwable
        verify(reAllocator).release(eq(0L), eq(1L), eq(FileMode.WRITE));
    }
}