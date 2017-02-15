package com.fineio.test.file;

import com.fineio.FineIO;
import com.fineio.file.FineIOFile;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.lang.reflect.Field;
import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public class FineWriteIOTest extends TestCase {


    public void testConstruct() throws Exception {
        IMocksControl control = EasyMock.createControl();
        Connector connector = control.createMock(Connector.class);
        URI u = new URI("");
        long size = 61217293;
        connector.getBlockSize();
        EasyMock.expectLastCall().andReturn(size).anyTimes();
        control.replay();
        FineIOFile file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE);
        Field field = FineIOFile.class.getDeclaredField("block_size");
        field.setAccessible(true);
        assertEquals(size, ((Long)field.get(file)).longValue());
    }
}
