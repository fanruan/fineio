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
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AppendFile.class, MemoryUtils.class})
public class AppendFileTest {
    @Test
    public void initLastPos() throws Exception {
        AppendFile af = mock(AppendFile.class);
        WriteFile wf = mock(WriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "initLastPos");

        Connector connector = mock(Connector.class);
        setInternalState(wf, "connector", connector);
        setInternalState(wf, "fileBlock", mock(FileBlock.class));

        when(connector.exists(any(FileBlock.class))).thenReturn(false, true);

        invokeMethod(af, "initLastPos");

        verify(connector, never()).read(any(FileBlock.class));
        assertEquals(0, (int) getInternalState(af, "lastPos"));

        InputStream input = mock(InputStream.class);
        when(connector.read(any(FileBlock.class))).thenReturn(input);

        when(input.read(any(byte[].class))).thenReturn(1).thenThrow(IOException.class)
                .thenAnswer((Answer<Integer>) invocationOnMock -> {
                    byte[] bytes = invocationOnMock.getArgument(0);
                    ByteBuffer.wrap(bytes).putInt(1);
                    return 4;
                });

        invokeMethod(af, "initLastPos");

        assertEquals(0, (int) getInternalState(af, "lastPos"));

        invokeMethod(af, "initLastPos");

        assertEquals(0, (int) getInternalState(af, "lastPos"));

        invokeMethod(af, "initLastPos");

        assertEquals(1, (int) getInternalState(af, "lastPos"));
    }

    @Test
    public void initLastBuf() throws Exception {
        AppendFile af = mock(AppendFile.class);
        doCallRealMethod().when(af, "initLastBuf");

        WriteFile wf = mock(WriteFile.class);
        setInternalState(af, "writeFile", wf);
        when(wf.nthVal(anyLong())).thenReturn(0, 1);

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

        ConcurrentMap<Integer, DirectBuffer> buffers = mock(ConcurrentMap.class);
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

        verify(buffers).put(0, buf);

        doThrow(new IOException()).when(input).read();
        WriteMemoryAllocator reAllocator = spy(Whitebox.<WriteMemoryAllocator>getInternalState(MemoryManager.INSTANCE, "reAllocator"));
        setInternalState(MemoryManager.INSTANCE, "reAllocator", reAllocator);

        invokeMethod(af, "initLastBuf");
        // throwable
        verify(reAllocator).release(anyLong(), eq(1L), eq(FileMode.WRITE.getCondition()));
    }

    @Test
    public void close() throws Exception {
        AppendFile af = mock(AppendFile.class);
        WriteFile wf = mock(WriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af).close();

        af.close();

        verify(wf).close();
        verifyPrivate(af).invoke("writeLastPos");
    }

    @Test
    public void writeLastPos() throws Exception {
        AppendFile af = mock(AppendFile.class);
        WriteFile wf = mock(WriteFile.class);
        setInternalState(af, "writeFile", wf);
        doCallRealMethod().when(af, "writeLastPos");

        setInternalState(wf, "fileBlock", mock(FileBlock.class));

        Connector connector = mock(Connector.class);
        setInternalState(wf, "connector", connector);

        invokeMethod(af, "writeLastPos");

        verify(connector).write(any(FileBlock.class), eq(new byte[4]));
    }
}