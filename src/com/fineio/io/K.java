package com.fineio.io;

import com.fineio.base.Bits;
import com.fineio.file.FileBlock;
import com.fineio.io.read.ByteReadBuffer;
import com.fineio.io.read.DoubleReadBuffer;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.storage.Connector;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.net.URI;

/**
 * Created by daniel on 2017/2/13.
 */
public class K {


    private static  byte[] createRandomByte(){
        int len = 1 << 30;
        byte[] arrays = new byte[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (byte)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }

    public static void main(String[] args) throws Exception {
        byte[] bytes  = createRandomByte();
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, "0");
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(bytes).anyTimes();
        control.replay();
        byteTest(bytes, connector, block);
        intTest(bytes, connector, block);
        doubleTest(bytes, connector, block);
    }

    private static void doubleTest(byte[] bytes, Connector connector, FileBlock block) {
        long t = System.currentTimeMillis();
        DoubleReadBuffer db = new DoubleReadBuffer(connector, block);
        double d = 0;
        for(int k = 0, klen = (bytes.length >> 3); k < klen; k++){
            d +=db.getDouble(k);
        }
        System.out.println( "first get double:"+(System.currentTimeMillis() - t) +"ms result："+ d);
        t = System.currentTimeMillis();
        d = 0;
        for(int k = 0, klen = (bytes.length >> 3); k < klen; k++){
            d +=db.getDouble(k);
        }
        System.out.println( "second get double:"+(System.currentTimeMillis() - t)+"ms result："+ d);
    }

    private static void intTest(byte[] bytes, Connector connector, FileBlock block) {
        long t = System.currentTimeMillis();
        IntReadBuffer ib = new IntReadBuffer(connector, block);
        int v = 0;
        for(int k = 0, klen = (bytes.length >> 2); k < klen; k++){
            v +=ib.getInt(k);
        }
        System.out.println( "first get int:"+(System.currentTimeMillis() - t) +"ms result："+ v);
        t = System.currentTimeMillis();
        v = 0;
        for(int k = 0, klen = (bytes.length >> 2); k < klen; k++){
            v +=ib.getInt(k);
        }
        System.out.println( "second get int:"+(System.currentTimeMillis() - t)+"ms result："+ v);
        ib.clear();
    }

    private static void byteTest(byte[] bytes, Connector connector, FileBlock block) {
        ByteReadBuffer buffer = new ByteReadBuffer(connector, block);
        long t = System.currentTimeMillis();
        byte r = 0;
        for(int i = 0; i < bytes.length; i++){
            r += buffer.get(i);
        }
        System.out.println("first get byte:" +(System.currentTimeMillis() - t) +"ms result："+ r);
        t = System.currentTimeMillis();
        r = 0;
        for(int i = 0; i < bytes.length; i++){
            r += buffer.get(i);
        }
        System.out.println( "second get byte:"+(System.currentTimeMillis() - t) +"ms result："+ r);
        buffer.clear();
    }
}
