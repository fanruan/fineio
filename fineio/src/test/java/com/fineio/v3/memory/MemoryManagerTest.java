package com.fineio.v3.memory;

import com.fineio.FineIO;
import com.fineio.accessor.FileMode;
import com.fineio.logger.FineIOLogger;
import com.fineio.memory.MemoryHelper;
import com.fineio.v3.exception.OutOfDirectMemoryException;
import com.fineio.v3.memory.allocator.BaseMemoryAllocator;
import com.fineio.v3.memory.allocator.WriteMemoryAllocator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2019-05-13
 */
public class MemoryManagerTest {

    @BeforeClass
    public static void beforeAll() {
        MemoryManager.INSTANCE.clear();
    }

    @Test
    public void allocateRead() throws NoSuchFieldException, IllegalAccessException, OutOfDirectMemoryException {
        long allocate = MemoryManager.INSTANCE.allocate(1024, FileMode.READ);
        Field allocator = MemoryManager.class.getDeclaredField("allocator");
        allocator.setAccessible(true);
        BaseMemoryAllocator o = (BaseMemoryAllocator) allocator.get(MemoryManager.INSTANCE);
        assertEquals(1024, o.getMemory());
        MemoryManager.INSTANCE.release(allocate, 1024);
        assertEquals(0, o.getMemory());
    }

    @Test
    public void allocateWrite() throws NoSuchFieldException, IllegalAccessException, OutOfDirectMemoryException {
        long allocate = MemoryManager.INSTANCE.allocate(1024, FileMode.WRITE);
        Field allocator = MemoryManager.class.getDeclaredField("reAllocator");
        allocator.setAccessible(true);
        WriteMemoryAllocator o = (WriteMemoryAllocator) allocator.get(MemoryManager.INSTANCE);
        assertEquals(1024, o.getMemory());
        allocate = MemoryManager.INSTANCE.allocate(allocate, 1024, 2048);
        assertEquals(2048, o.getMemory());
        MemoryManager.INSTANCE.release(allocate, 2048);
        assertEquals(0, o.getMemory());
    }

    @Test
    public void threads() throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        FineIO.setLogger(FineIOLogger.DEFAULT, true);
        long maxMemory = MemoryHelper.getMaxMemory();
        double maxMemorySize = maxMemory * 0.6;
        long size = (long) (maxMemorySize / 10 + 1024);

        ExecutorService service = Executors.newFixedThreadPool(10);
        Queue<Future> list = new ArrayBlockingQueue<>(11);
        for (int i = 0; i < 11; i++) {
            Future<?> submit = service.submit(new TestRunnable(size, FileMode.READ));
            list.offer(submit);
        }
        Future poll = null;
        while ((poll = list.poll()) != null) {
            poll.get();
        }
        Field allocator = MemoryManager.class.getDeclaredField("allocator");
        allocator.setAccessible(true);
        BaseMemoryAllocator o = (BaseMemoryAllocator) allocator.get(MemoryManager.INSTANCE);
        assertEquals(0, o.getMemory());
        double maxWrite = maxMemory * 0.2;
        size = (long) (maxWrite / 10 + 1024);
        for (int i = 0; i < 11; i++) {
            Future<?> submit = service.submit(new TestRunnable(size, FileMode.WRITE));
            list.offer(submit);
        }
        while ((poll = list.poll()) != null) {
            poll.get();
        }
        allocator = MemoryManager.class.getDeclaredField("reAllocator");
        allocator.setAccessible(true);
        o = (BaseMemoryAllocator) allocator.get(MemoryManager.INSTANCE);
        assertEquals(0, o.getMemory());
    }

    class TestRunnable implements Runnable {

        private long size;
        private FileMode mode;

        public TestRunnable(long size, FileMode mode) {
            this.size = size;
            this.mode = mode;
        }

        @Override
        public void run() {
            long address = 0;
            try {
                address = MemoryManager.INSTANCE.allocate(size, mode);
            } catch (OutOfDirectMemoryException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MemoryManager.INSTANCE.release(address, size);
        }
    }
}