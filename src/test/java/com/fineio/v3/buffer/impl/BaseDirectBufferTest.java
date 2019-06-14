package com.fineio.v3.buffer.impl;

import com.fineio.MockFinal;
import com.fineio.accessor.FileMode;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.buffer.BufferAllocateFailedException;
import com.fineio.v3.buffer.BufferClosedException;
import com.fineio.v3.buffer.BufferOutOfBoundException;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.MemoryManager;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.memory.allocator.BaseMemoryAllocator;
import com.fineio.v3.memory.allocator.WriteMemoryAllocator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * @author anchore
 * @date 2019/5/15
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BaseDirectBuffer.class, MemoryManager.class})
public class BaseDirectBufferTest {

    @Rule
    public MockFinal mockFinal = new MockFinal();

    @Test
    public void ensureOpen() {
        DirectBuffer buf = new DirectBuffer(1, 16, mock(FileBlock.class), Offset.BYTE, 1024);
        // to be closed
        buf.ensureOpen();

        Whitebox.<AtomicBoolean>getInternalState(buf, "closed").set(true);

        try {
            // closed, ensure throws
            buf.ensureOpen();
            fail();
        } catch (BufferClosedException ignore) {
        }
    }

    @Test
    public void ensureCap() throws Exception {
        WriteMemoryAllocator reAllocator = mock(WriteMemoryAllocator.class);
        setInternalState(MemoryManager.INSTANCE, "reAllocator", reAllocator);

        DirectBuffer buf = new DirectBuffer(mock(FileBlock.class), Offset.BYTE, 1024, FileMode.WRITE);
        // 256 -> 512
        buf.ensureCap(256);

        assertEquals(512, (int) Whitebox.getInternalState(buf, "cap"));

        when(reAllocator.reallocate(0, 512, 1024, FileMode.WRITE.getCondition())).thenThrow(new OutOfDirectMemoryException(""));
        try {
            // max cap exceeds, grow up to max cap
            buf.ensureCap(1024);
            // allocation will fail
            fail();
        } catch (BufferAllocateFailedException ignore) {
        }

        assertEquals(512, (int) Whitebox.getInternalState(buf, "cap"));
    }

    @Test
    public void checkPos() {
        DirectBuffer buf = new DirectBuffer(1, 16, mock(FileBlock.class), Offset.BYTE, 1024);

        buf.checkPos(1);
        try {
            buf.checkPos(-1);
        } catch (BufferOutOfBoundException ignore) {
        }
        try {
            buf.checkPos(16);
        } catch (BufferOutOfBoundException ignore) {
        }
    }

    @Test
    public void updatePos() {
        DirectBuffer buf = new DirectBuffer(1, 16, mock(FileBlock.class), Offset.BYTE, 1024);

        buf.updateSize(16);
        assertEquals(17, (int) Whitebox.getInternalState(buf, "size"));

        buf.updateSize(0);
        assertEquals(17, (int) Whitebox.getInternalState(buf, "size"));
    }

    @Test
    public void getAddress() throws Exception {
        assertEquals(1, new DirectBuffer(1, 16, mock(FileBlock.class), Offset.BYTE, 1024).getAddress());

        WriteMemoryAllocator reAllocator = mock(WriteMemoryAllocator.class);
        setInternalState(MemoryManager.INSTANCE, "reAllocator", reAllocator);
        when(reAllocator.allocate(16, FileMode.WRITE.getCondition())).thenReturn(1L);

        assertEquals(1, new DirectBuffer(mock(FileBlock.class), Offset.BYTE, 1024, FileMode.WRITE).getAddress());
    }

    @Test
    public void getSizeInBytes() {
        assertEquals(16, new DirectBuffer(1, 16, mock(FileBlock.class), Offset.BYTE, 1024).getSizeInBytes());
    }

    @Test
    public void getFileBlock() {
        FileBlock fileBlock = mock(FileBlock.class);
        assertEquals(fileBlock, new DirectBuffer(1, 16, fileBlock, Offset.BYTE, 1024).getFileBlock());
    }

    @Test
    public void close() throws Exception {
        AtomicBoolean closed = spy(new AtomicBoolean(false));
        whenNew(AtomicBoolean.class).withArguments(false).thenReturn(closed);

        BaseMemoryAllocator allocator = mock(BaseMemoryAllocator.class);
        setInternalState(MemoryManager.INSTANCE, "allocator", allocator);
        setInternalState(MemoryManager.INSTANCE, "memoryMode", new ConcurrentHashMap<>(Collections.singletonMap(1L, FileMode.READ)));

        DirectBuffer buf = new DirectBuffer(1, 16, mock(FileBlock.class), Offset.BYTE, 1024);
        buf.close();
        buf.close();

        verify(allocator).release(1, 16, FileMode.READ.getCondition());
        verify(closed, atLeast(2)).compareAndSet(false, true);
    }

    static class DirectBuffer extends BaseDirectBuffer {
        DirectBuffer(FileBlock fileBlock, Offset offset, int maxCap, FileMode fileMode) {
            super(fileBlock, offset, maxCap, fileMode);
        }

        DirectBuffer(long address, int cap, FileBlock fileBlock, Offset offset, int maxCap) {
            super(address, cap, fileBlock, offset, maxCap);
        }
    }
}