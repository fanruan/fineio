package com.fineio.test.file;

import com.fineio.FineIO;
import com.fineio.file.IOFile;
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
        byte size = 26;
        connector.getBlockOffset();
        EasyMock.expectLastCall().andReturn(size).anyTimes();
        control.replay();
        IOFile file = FineIO.createIOFile(connector, u, FineIO.MODEL.WRITE_BYTE);
        Field field = IOFile.class.getDeclaredField("block_size_offset");
        field.setAccessible(true);
        assertEquals(size, ((Byte)field.get(file)).byteValue());
    }
}
