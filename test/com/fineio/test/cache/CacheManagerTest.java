package com.fineio.test.cache;

import com.fineio.FineIO;
import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.FileCloseException;
import com.fineio.exception.MemorySetException;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.MemoryUtils;
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
        private volatile boolean close = false;

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
    }

    public class TestBuffer2 extends TestBuffer {
        public void clear() {
            MemoryUtils.free(address);
            CacheManager.getInstance().releaseBuffer(this, false);
        }
    }




    public void testCache(){
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

        try {
            MemoryConf.setTotalMemSize(FineIO.getMaxMemSizeForSet() - 1);
        } catch (MemorySetException e) {
            e.printStackTrace();
        }

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
