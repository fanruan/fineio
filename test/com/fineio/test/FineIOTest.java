package com.fineio.test;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.file.*;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public class FineIOTest extends TestCase {

    public void testCreateReadIOFile() throws Exception {

        long len = (long)(Math.random() * 10000000000d);
        byte[] res = new byte[16];
        Bits.putLong(res, 0, len);
        Bits.putLong(res, 8, len * 2);
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
        FineIOFile file = FineIO.createReadIOFile(connector , u);
        assertTrue(file instanceof FineReadIOFile);
    }

    public void testCreateWriteIOFile() throws Exception {
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        FineIOFile file = FineIO.createWriteIOFile(connector, u);
        assertTrue(file instanceof FineWriteIOFile);
    }
}
