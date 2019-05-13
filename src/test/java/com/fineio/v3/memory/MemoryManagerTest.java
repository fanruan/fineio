package com.fineio.v3.memory;

import com.fineio.v3.memory.allocator.BaseMemoryAllocator;
import com.fineio.v3.memory.allocator.WriteMemoryAllocator;
import com.fineio.v3.type.FileMode;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2019-05-13
 */
public class MemoryManagerTest {

    @Test
    public void allocateRead() throws NoSuchFieldException, IllegalAccessException {
        long allocate = MemoryManager.INSTANCE.allocate(1024, FileMode.READ);
        Field allocator = MemoryManager.class.getDeclaredField("allocator");
        allocator.setAccessible(true);
        BaseMemoryAllocator o = (BaseMemoryAllocator) allocator.get(MemoryManager.INSTANCE);
        assertEquals(1024, o.getMemory());
        MemoryManager.INSTANCE.release(allocate, 1024, FileMode.READ);
        assertEquals(0, o.getMemory());
    }

    @Test
    public void allocateWrite() throws NoSuchFieldException, IllegalAccessException {
        long allocate = MemoryManager.INSTANCE.allocate(1024, FileMode.WRITE);
        Field allocator = MemoryManager.class.getDeclaredField("reAllocator");
        allocator.setAccessible(true);
        WriteMemoryAllocator o = (WriteMemoryAllocator) allocator.get(MemoryManager.INSTANCE);
        assertEquals(1024, o.getMemory());
        allocate = MemoryManager.INSTANCE.allocate(allocate, 1024, 2048);
        assertEquals(2048, o.getMemory());
        MemoryManager.INSTANCE.release(allocate, 2048, FileMode.WRITE);
        assertEquals(0, o.getMemory());
    }

    @Test
    public void threads() {

    }
}