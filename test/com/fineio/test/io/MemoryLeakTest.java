package com.fineio.test.io;

import com.fineio.FineIO;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import com.fineio.test.file.FineWriteIOTest;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.ibex.nestedvm.Runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

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
    }


    public void testMemoryLeak() throws Exception {
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
        assertZeroMemory();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(InputStream.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() throws Throwable {
                throw new RuntimeException("e");
            }
        }).anyTimes();
        connector.write(EasyMock.anyObject(FileBlock.class), EasyMock.anyObject(byte[].class));
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() throws Throwable {
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
}
