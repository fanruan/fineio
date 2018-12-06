package com.fineio.test.io;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v1.FineIO;
import com.fineio.v1.cache.CacheLinkedMap;
import com.fineio.v1.cache.CacheManager;
import com.fineio.v1.io.FloatBuffer;
import com.fineio.v1.io.file.IOFile;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;

/**
 * Created by daniel on 2017/3/27.
 */
public class MemoryLeakTest extends TestCase {


    public static void assertZeroMemory(){
        assertEquals(FineIO.getCurrentMemorySize(), 0);
        assertEquals(FineIO.getCurrentReadMemorySize(), 0);
        assertEquals(FineIO.getCurrentWriteMemorySize(), 0);
        assertEquals(FineIO.getReadWaitCount(), 0);
        assertEquals(FineIO.getWriteWaitCount(), 0);
        assEmptyMap("edit", CacheManager.getInstance());
        assEmptyMap("read", CacheManager.getInstance());
        assEmptyMap("write", CacheManager.getInstance());
    }


    public static void assertZeroMemory(CacheManager manager){
        assertEquals(manager.getCurrentMemorySize(), 0);
        assertEquals(manager.getReadSize(), 0);
        assertEquals(manager.getWriteSize(), 0);
        assertEquals(manager.getReadWaitCount(), 0);
        assertEquals(manager.getWriteWaitCount(), 0);
        assEmptyMap("edit", manager);
        assEmptyMap("read", manager);
        assEmptyMap("write", manager);
    }

    protected static void assEmptyMap(String field, CacheManager manager) {
        try {
            Field edit = CacheManager.class.getDeclaredField(field);
            edit.setAccessible(true);
            CacheLinkedMap editMap = (CacheLinkedMap) edit.get(manager);
            Field map = CacheLinkedMap.class.getDeclaredField("indexMap");
            map.setAccessible(true);
            Map resultMap = (Map) map.get(editMap);
            assertTrue(resultMap.isEmpty());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void warning() {
        System.out.println("这个测试用例抛错正常，测试的各种异常情况");
    }


    public void testMemoryLeak() throws Exception {
        warning();
        assertZeroMemory();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() throws Throwable {
                 throw new IOException("e");
            }
        }).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() throws Throwable {
                throw new IOException("e");
            }
        }).anyTimes();
        control.replay();
        URI uri = new URI("A");
        IOFile file = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_DOUBLE);
        FineIO.put(file, 10d);
        file.close();
        assertZeroMemory();
    }


    public void testMemoryLeak2() throws Exception {
        warning();
        assertZeroMemory();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        control.replay();
        URI uri = new URI("A");
        IOFile file = FineIO.createIOFile(connector, uri, FineIO.MODEL.WRITE_DOUBLE);
        FineIO.put(file, 10d);
        file.close();
        assertZeroMemory();
    }


    public void testMemoryLeak3() throws Exception {
        warning();
        assertZeroMemory();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        control.replay();
        URI uri = new URI("A");
        IOFile<FloatBuffer> file = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_FLOAT);
        FineIO.put(file, 10f);
        file.close();
        assertZeroMemory();
    }


    public void testMemoryLeak4() throws Exception {
        warning();
        assertZeroMemory();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        control.replay();
        URI uri = new URI("A");
        IOFile<FloatBuffer> file = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_FLOAT);
        FineIO.put(file, 10f);
        CacheManager m = CacheManager.getInstance();
        CacheManager.clear();
        assertZeroMemory(m);
        file.close();
        assertZeroMemory();
    }


    public void testMemoryLeak5() throws Exception {
        warning();
        assertZeroMemory();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        control.replay();
        URI uri = new URI("A");
        IOFile<FloatBuffer> file = FineIO.createIOFile(connector, uri, FineIO.MODEL.EDIT_FLOAT);
        FineIO.put(file, 0, 10f);
        assertEquals(FineIO.getFloat(file, 0), 10f);
        FineIO.put(file, 10000000, 20f);
        assertEquals(FineIO.getFloat(file, 10000000), 20f);
        FineIO.put(file, 0, 30f);
        assertEquals(FineIO.getFloat(file, 0), 30f);
        FineIO.put(file, 10000000, 50f);
        assertEquals(FineIO.getFloat(file, 10000000), 50f);
        FineIO.put(file, 20000000, 60f);
        assertEquals(FineIO.getFloat(file, 20000000), 60f);
        CacheManager m = CacheManager.getInstance();
        CacheManager.clear();
        assertZeroMemory(m);
        file.close();
        assertZeroMemory();
    }
}
