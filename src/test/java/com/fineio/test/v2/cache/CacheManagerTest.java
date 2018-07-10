package com.fineio.test.v2.cache;

import com.fineio.FineIO;
import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.MemorySetException;
import com.fineio.io.write.WriteOnlyBuffer;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.MemoryUtils;
import com.fineio.test.v2.io.MemoryLeakTest;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/3/6.
 */
public class CacheManagerTest extends TestCase {


    public void testCache() {
        CacheManager.clear();
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
        setMemory(1030);
        TestBuffer buffer = new TestBuffer();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 0);
        buffer.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        buffer.clear();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 0);

        TestBuffer buffer2 = new TestBuffer();
        buffer2.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        TestBuffer buffer3 = new TestBuffer();
        buffer3.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 2048);
        buffer2.clear();
        buffer3.clear();

        TestBuffer2 b2 = new TestBuffer2();
        b2.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        b2.clear();
        TestBuffer2 b3 = new TestBuffer2();
        b3.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        b3.clear();
        b2.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        b2.clear();
        b3.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        b3.clear();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 0);
        boolean exp = false;
        try {
            MemoryConf.setTotalMemSize(FineIO.getMaxMemSizeForSet() - 1);
        } catch (MemorySetException e) {
            exp = true;
        }
        assertFalse(exp);
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
        CacheManager.clear();
        MemoryLeakTest.assertZeroMemory(CacheManager.getInstance());
    }

    private void setMemory(int size) {
        CacheManager.clear();
        try {
            Field f = MemoryConf.class.getDeclaredField("max_size");
            f.setAccessible(true);
            f.set(null, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TestBuffer implements WriteOnlyBuffer {
        final int cap = 1024;
        protected long address = 0;
        protected volatile boolean close = false;
        private volatile boolean access = false;

        TestBuffer() {
        }

        public boolean full() {
            return true;
        }

        public void write() {
            synchronized (this) {
                address = CacheManager.getInstance().allocateRead(cap);
            }
        }

        public void force() {
            clear();
        }

        @Override
        public void forceAndClear() {

        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public boolean needClear() {
            return false;
        }

        public void closeWithOutSync() {
            clear();
        }

        public void clear() {
            synchronized (this) {
                if (close) {
                    return;
                }
                MemoryUtils.free(address);
                CacheManager.getInstance().returnMemory(this, LEVEL.READABLE);
                close = true;
            }
        }


        public LEVEL getLevel() {
            return LEVEL.READABLE;
        }

        public boolean recentAccess() {
            return access;
        }

        public void resetAccess() {
            access = false;
        }

        @Override
        public long getAddress() {
            return 0;
        }

        @Override
        public int getMaxSize() {
            return 0;
        }

        public int getAllocateSize() {
            return cap;
        }

        @Override
        public boolean isDirect() {
            return false;
        }

        @Override
        public int getOffset() {
            return 0;
        }

        @Override
        public void close() {

        }

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public int getByteSize() {
            return cap;
        }

        @Override
        public int getLength() {
            return cap;
        }

        @Override
        public URI getUri() {
            return null;
        }
    }

    public class TestBuffer2 extends TestBuffer {

        private volatile boolean load = false;

        public void write() {
            super.write();
            load = true;
        }

        public void clear() {
            synchronized (this) {
                if (!load) {
                    return;
                }
                MemoryUtils.free(address);
                CacheManager.getInstance().returnMemory(this, LEVEL.READABLE);
                load = false;
            }
        }
    }
}
