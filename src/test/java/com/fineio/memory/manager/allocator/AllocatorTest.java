package com.fineio.memory.manager.allocator;

import com.fineio.FineIO;
import com.fineio.memory.MemoryUtils;
import com.fineio.memory.manager.allocator.impl.BaseMemoryAllocator;
import com.fineio.memory.manager.deallocator.DeAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.memory.manager.obj.ReAllocateMemoryObject;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2018/10/2
 */
public class AllocatorTest {

    private static final String TEMPLATE = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.PageMaker including versions of Lorem Ipsum.";
    private static final DeAllocator READ = BaseDeAllocator.Builder.READ.build();
    private static final DeAllocator WRITE = BaseDeAllocator.Builder.WRITE.build();

    @Test
    public void allocateRead() {
        byte[] bytes = TEMPLATE.getBytes();
        Allocator allocator = BaseMemoryAllocator.Builder.BLOCK.build(new ByteArrayInputStream(bytes), bytes.length);
        MemoryObject memoryObject = allocator.allocate();
        assertEquals(bytes.length, memoryObject.getAllocateSize());
        assertEquals(bytes.length, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length, FineIO.getCurrentReadMemorySize());
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getByte(memoryObject.getAddress(), i));
        }
        READ.deAllocate(memoryObject);
        assertEquals(0, FineIO.getCurrentMemorySize());
        assertEquals(0, FineIO.getCurrentReadMemorySize());
        allocator = BaseMemoryAllocator.Builder.BLOCK.build(new ByteArrayInputStream(bytes));
        memoryObject = allocator.allocate();
        assertEquals(bytes.length, memoryObject.getAllocateSize());
        assertEquals(bytes.length, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length, FineIO.getCurrentReadMemorySize());
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getByte(memoryObject.getAddress(), i));
        }
        READ.deAllocate(memoryObject);
        assertEquals(0, FineIO.getCurrentMemorySize());
        assertEquals(0, FineIO.getCurrentReadMemorySize());
    }

    @Test
    public void allocateWrite() {
        byte[] bytes = TEMPLATE.getBytes();
        Allocator allocator = BaseMemoryAllocator.Builder.BLOCK.build(bytes.length);
        MemoryObject memoryObject = allocator.allocate();
        assertEquals(bytes.length, memoryObject.getAllocateSize());
        assertEquals(bytes.length, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length, FineIO.getCurrentWriteMemorySize());
        for (int i = 0; i < bytes.length; i++) {
            MemoryUtils.put(memoryObject.getAddress(), i, bytes[i]);
        }
        byte[] read = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            read[i] = MemoryUtils.getByte(memoryObject.getAddress(), i);
        }
        WRITE.deAllocate(memoryObject);
        assertEquals(0, FineIO.getCurrentMemorySize());
        assertEquals(0, FineIO.getCurrentReadMemorySize());
        assertEquals(TEMPLATE, new String(read));
    }

    @Test
    public void allocateDirectRead() {
        byte[] bytes = TEMPLATE.getBytes();
        Allocator allocator = BaseMemoryAllocator.Builder.DIRECT.build(new ByteArrayInputStream(bytes), bytes.length);
        MemoryObject memoryObject = allocator.allocate();
        assertEquals(bytes.length, memoryObject.getAllocateSize());
        assertEquals(bytes.length, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length, FineIO.getCurrentReadMemorySize());
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getByte(memoryObject.getAddress(), i));
        }
        READ.deAllocate(memoryObject);
        assertEquals(0, FineIO.getCurrentMemorySize());
        assertEquals(0, FineIO.getCurrentReadMemorySize());
        allocator = BaseMemoryAllocator.Builder.DIRECT.build(new ByteArrayInputStream(bytes));
        memoryObject = allocator.allocate();
        assertEquals(bytes.length, memoryObject.getAllocateSize());
        assertEquals(bytes.length, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length, FineIO.getCurrentReadMemorySize());
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], MemoryUtils.getByte(memoryObject.getAddress(), i));
        }
        READ.deAllocate(memoryObject);
        assertEquals(0, FineIO.getCurrentMemorySize());
        assertEquals(0, FineIO.getCurrentReadMemorySize());
    }

    @Test
    public void allocateDirectWrite() {
        byte[] bytes = TEMPLATE.getBytes();
        Allocator allocator = BaseMemoryAllocator.Builder.DIRECT.build(bytes.length);
        MemoryObject memoryObject = allocator.allocate();
        assertEquals(bytes.length, memoryObject.getAllocateSize());
        assertEquals(bytes.length, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length, FineIO.getCurrentWriteMemorySize());
        for (int i = 0; i < bytes.length; i++) {
            MemoryUtils.put(memoryObject.getAddress(), i, bytes[i]);
        }
        byte[] read = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            read[i] = MemoryUtils.getByte(memoryObject.getAddress(), i);
        }
        WRITE.deAllocate(memoryObject);
        assertEquals(0, FineIO.getCurrentMemorySize());
        assertEquals(0, FineIO.getCurrentReadMemorySize());
        assertEquals(TEMPLATE, new String(read));
    }

    @Test
    public void reAllocate() {
        byte[] bytes = TEMPLATE.getBytes();
        Allocator allocator = BaseMemoryAllocator.Builder.BLOCK.build(bytes.length);
        MemoryObject memoryObject = allocator.allocate();
        assertEquals(bytes.length, memoryObject.getAllocateSize());
        assertEquals(bytes.length, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length, FineIO.getCurrentWriteMemorySize());
        for (int i = 0; i < bytes.length; i++) {
            MemoryUtils.put(memoryObject.getAddress(), i, bytes[i]);
        }
        allocator = BaseMemoryAllocator.Builder.BLOCK.build(memoryObject, bytes.length + bytes.length);
        memoryObject = allocator.allocate();
        assertEquals(bytes.length, ((ReAllocateMemoryObject) memoryObject).getIncrementSize());
        assertEquals(bytes.length << 1, FineIO.getCurrentMemorySize());
        assertEquals(bytes.length << 1, FineIO.getCurrentWriteMemorySize());
        for (int i = bytes.length; i < (bytes.length << 1); i++) {
            MemoryUtils.put(memoryObject.getAddress(), i, bytes[i - bytes.length]);
        }
        byte[] read = new byte[bytes.length << 1];
        for (int i = 0; i < (bytes.length << 1); i++) {
            read[i] = MemoryUtils.getByte(memoryObject.getAddress(), i);
        }
        WRITE.deAllocate(memoryObject);
        assertEquals(0, FineIO.getCurrentMemorySize());
        assertEquals(0, FineIO.getCurrentReadMemorySize());
        assertEquals(TEMPLATE + TEMPLATE, new String(read));
    }
}