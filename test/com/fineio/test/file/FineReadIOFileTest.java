package com.fineio.test.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.FineReadIOFile;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public class FineReadIOFileTest extends TestCase {

    private long[] createRandomLong(){
        int len = (int) (Math.random()*1000);
        long[] arrays = new long[len];
        for(int i = 0; i< len; i++){
            arrays[i] = Double.doubleToLongBits(Math.random() * 100000000000d);
        }
        return arrays;
    }

    public void testConstruct() throws Exception{
        for(int i = 0 ;i < 10000; i++){
            constructTest();
        }
    }

    public void testException() throws Exception{
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Field head = FileConstants.class.getDeclaredField("HEAD");
        head.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, head.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(null).anyTimes();
        control.replay();
        boolean exp = false;
        try {
            Constructor<FineReadIOFile> cons = FineReadIOFile.class.getDeclaredConstructor(Connector.class, URI.class);
            cons.setAccessible(true);
            cons.newInstance(connector, u);
        } catch (BlockNotFoundException e){
            exp = true;
        } catch (InvocationTargetException e) {
            if(e.getTargetException() instanceof  BlockNotFoundException){
                exp = true;
            }
        }
        assertTrue(exp);

    }

    public void constructTest() throws Exception {
        long[] length = createRandomLong();
        int len = length.length;
        byte[] res = new byte[16];
        Bits.putLong(res, 0, (long)len);
        Bits.putLong(res, 8, (long)len * 2);
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        Field head = FileConstants.class.getDeclaredField("HEAD");
        head.setAccessible(true);
        Constructor<FileBlock> constructor = FileBlock.class.getDeclaredConstructor(URI.class, String.class);
        constructor.setAccessible(true);
        FileBlock block = constructor.newInstance(u, head.get(null));
        EasyMock.expect(connector.read(EasyMock.eq(block))).andReturn(res).anyTimes();
        control.replay();
        Constructor<FineReadIOFile> cons = FineReadIOFile.class.getDeclaredConstructor(Connector.class, URI.class);
        cons.setAccessible(true);
        FineReadIOFile file = cons.newInstance(connector, u);
        Field lenField = FineReadIOFile.class.getSuperclass().getDeclaredField("blocks");
        Field blockSizeField =  FineReadIOFile.class.getSuperclass().getDeclaredField("block_size");
        lenField.setAccessible(true);
        blockSizeField.setAccessible(true);
        assertEquals(file.getPath(), u.getPath());
        assertEquals(len, ((Long)lenField.get(file)).longValue());
        assertEquals(len * 2, ((Long)blockSizeField.get(file)).longValue());
    }
}
