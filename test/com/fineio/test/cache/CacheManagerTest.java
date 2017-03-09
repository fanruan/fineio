package com.fineio.test.cache;

import com.fineio.cache.CacheManager;
import com.fineio.cache.LEVEL;
import com.fineio.exception.FileCloseException;
import com.fineio.exception.MemorySetException;
import com.fineio.io.Buffer;
import com.fineio.memory.MemoryConf;
import com.fineio.memory.MemoryUtils;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Field;

/**
 * Created by daniel on 2017/3/6.
 */
public class CacheManagerTest extends TestCase {


    private class TestBuffer implements Buffer {
        private volatile boolean access = false;
        final int cap = 1024;
        private long address = 0;

        TestBuffer(){
            CacheManager.getInstance().registerBuffer(this);
        }

        public boolean full() {
            return true;
        }

        public void write() {
            address =  CacheManager.getInstance().allocateRead(this, cap);
        }

        public void force() {
            clear();
        }

        public void clear() {
            MemoryUtils.free(address);
            CacheManager.getInstance().releaseBuffer(this, true);
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

    public void testCache(){
        CacheManager.clear();
        TestBuffer buffer = new TestBuffer();
        try {
            Field f = MemoryConf.class.getDeclaredField("max_size");
            f.setAccessible(true);
            f.set(null, 1030);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            MemoryConf.setTotalMemSize(MemoryConf.getMaxMemSizeForSet() - 1);
        } catch (MemorySetException e) {
            e.printStackTrace();
        }

    }
}
