package com.fineio.test.cache;

import com.fineio.FineIO;
import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.FileCloseException;
import com.fineio.exception.MemorySetException;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.MemoryUtils;
import com.fineio.test.io.MemoryLeakTest;
import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * Created by daniel on 2017/3/6.
 */
public class CacheManagerTest extends TestCase {


    private class TestBuffer implements Buffer {
        private volatile boolean access = false;
        final int cap = 1024;
        protected long address = 0;
        protected volatile boolean close = false;

        TestBuffer(){
            CacheManager.getInstance().registerBuffer(this);
        }

        public boolean full() {
            return true;
        }

        public void write() {
            synchronized (this) {
                address = CacheManager.getInstance().allocateRead(this, cap);
            }
        }

        public void force() {
            clear();
        }

        public void clear() {
            synchronized (this) {
                if (close) {
                    return;
                }
                MemoryUtils.free(address);
                CacheManager.getInstance().clearBufferMemory((Buffer)this);
                CacheManager.getInstance().releaseBuffer(this, true);
                close = true;
            }
        }


        public LEVEL getLevel() {
            return LEVEL.READ;
        }

        public boolean recentAccess() {
            return access;
        }

        public void resetAccess() {
            access = false;
        }

        public int getAllocateSize() {
            return cap;
        }

        @Override
        public int getByteSize() {
            return cap;
        }
    }

    public class TestBuffer2 extends TestBuffer {

        private volatile  boolean load = false;

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
                CacheManager.getInstance().clearBufferMemory((Buffer)this);
                load = false;
                CacheManager.getInstance().releaseBuffer(this, false);
            }
        }
    }




    public void testCache(){
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
        boolean exp = false;
        try {
            buffer.write();
        } catch (FileCloseException e){
            exp = true;
        }
        assertTrue(exp);
        TestBuffer buffer2 = new TestBuffer();
        buffer2.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        TestBuffer buffer3 = new TestBuffer();
        buffer3.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);

        exp = false;
        try {
            buffer2.write();
        } catch (FileCloseException e){
            exp = true;
        }
        assertTrue(exp);




        TestBuffer2 b2 = new TestBuffer2();
        b2.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        TestBuffer2 b3= new TestBuffer2();
        b3.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        b2.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        b3.write();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 1024);
        b3.clear();
        assertEquals(CacheManager.getInstance().getCurrentMemorySize(), 0);
        exp = false;
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
        MemoryLeakTest.assertZeroMemory();
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
}
