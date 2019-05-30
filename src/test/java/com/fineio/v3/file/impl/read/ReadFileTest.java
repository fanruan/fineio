package com.fineio.v3.file.impl.read;

import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.memory.MemoryManager;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.memory.allocator.BaseMemoryAllocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MemoryUtils.class})
public class ReadFileTest {

    @Test
    public void loadBuffer() throws Exception {
        ReadFile<?> rf = mock(ReadFile.class, CALLS_REAL_METHODS);

        setInternalState(rf, "fileBlock", mock(FileBlock.class));
        Connector connector = mock(Connector.class);
        setInternalState(rf, "connector", connector);

        InputStream input = mock(InputStream.class, CALLS_REAL_METHODS);
        when(connector.read(any(FileBlock.class))).thenReturn(input);

        when(input.available()).thenReturn(1);

        BaseMemoryAllocator allocator = mock(BaseMemoryAllocator.class);
        setInternalState(MemoryManager.INSTANCE, "allocator", allocator);
        when(allocator.allocate(1, FileMode.READ.getCondition())).thenReturn(1L);

        when(input.read()).thenReturn(1, -1);

        setInternalState(rf, "offset", Offset.BYTE);

        mockStatic(MemoryUtils.class);

        invokeMethod(rf, "loadBuffer", 0);

        verifyStatic(MemoryUtils.class);
        byte[] bytes = ByteBuffer.allocate(1).put((byte) 1).array();
        MemoryUtils.copyMemory(bytes, 1, 1);

        verify(rf).newDirectBuf(eq(1L), eq(1), any(FileBlock.class));

        when(input.read()).thenReturn(1, -1);

        doThrow(new Error()).when(MemoryUtils.class);
        MemoryUtils.copyMemory(any(byte[].class), anyLong(), anyLong());

        invokeMethod(rf, "loadBuffer", 0);

        verify(allocator).release(1, 1, FileMode.READ.getCondition());
    }

    @Test
    public void close() {
        ReadFile<?> rf = mock(ReadFile.class, CALLS_REAL_METHODS);
        AtomicBoolean closed = spy(new AtomicBoolean(false));
        setInternalState(rf, "closed", closed);

        rf.close();
        assertTrue(closed.get());
    }
}