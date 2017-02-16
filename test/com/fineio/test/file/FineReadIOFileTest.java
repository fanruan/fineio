package com.fineio.test.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.FineReadIOFile;
import com.fineio.io.read.IntReadBuffer;
import com.fineio.io.read.LongReadBuffer;
import com.fineio.storage.Connector;
import com.fr.third.org.apache.poi.hssf.record.formula.functions.Int;
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


    public void testConstruct() throws Exception{
        for(int i = 0 ;i < 100; i++){
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
            FineReadIOFile.createFineIO(connector, u, LongReadBuffer.class);
        } catch (BlockNotFoundException e){
            exp = true;
        }
        assertTrue(exp);

    }

    public void constructTest() throws Exception {
        int len =  (int)(Math.random() * 100d);
        byte[] res = new byte[16];
        Bits.putInt(res, 0, len * 2);
        res[8] =  (byte) len;
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
        FineReadIOFile<LongReadBuffer> file = FineReadIOFile.createFineIO(connector, u, LongReadBuffer.class);
        Field lenField = FineReadIOFile.class.getSuperclass().getDeclaredField("blocks");
        Field blockSizeField =  FineReadIOFile.class.getSuperclass().getDeclaredField("block_size_offset");
        lenField.setAccessible(true);
        blockSizeField.setAccessible(true);
        assertEquals(file.getPath(), u.getPath());
        assertEquals(len * 2, ((Integer)lenField.get(file)).intValue());
        assertEquals(len - LongReadBuffer.OFFSET, ((Byte)blockSizeField.get(file)).byteValue());
        FineReadIOFile<IntReadBuffer> ifile = FineReadIOFile.createFineIO(connector, u, IntReadBuffer.class);
        lenField = FineReadIOFile.class.getSuperclass().getDeclaredField("blocks");
        blockSizeField =  FineReadIOFile.class.getSuperclass().getDeclaredField("block_size_offset");
        lenField.setAccessible(true);
        blockSizeField.setAccessible(true);
        assertEquals(ifile.getPath(), u.getPath());
        assertEquals(len * 2, ((Integer)lenField.get(ifile)).intValue());
        assertEquals(len - IntReadBuffer.OFFSET, ((Byte)blockSizeField.get(ifile)).byteValue());
    }
}
