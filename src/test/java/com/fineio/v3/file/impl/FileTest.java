package com.fineio.v3.file.impl;

import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.memory.Offset;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * @author anchore
 * @date 2019/5/16
 */
public class FileTest {

    @Test
    public void nthBuf() {
        File<?> f = mock(File.class, Mockito.CALLS_REAL_METHODS);

        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 10);
        setInternalState(f, "connector", connector);

        setInternalState(f, "offset", Offset.BYTE);

        assertEquals(0, f.nthBuf(1));
        assertEquals(1, f.nthBuf(1024));
    }

    @Test
    public void nthVal() {
        File<?> f = mock(File.class, Mockito.CALLS_REAL_METHODS);

        Connector connector = mock(Connector.class);
        when(connector.getBlockOffset()).thenReturn((byte) 10);
        setInternalState(f, "connector", connector);

        setInternalState(f, "offset", Offset.INT);

        assertEquals(1, f.nthVal(1));
        assertEquals(128, f.nthVal(128));
        assertEquals(1, f.nthVal(257));
    }

    @Test
    public void ensureOpen() {
        File<?> f = mock(File.class, Mockito.CALLS_REAL_METHODS);
        setInternalState(f, "closed", new AtomicBoolean(false));

        // to be closed
        f.ensureOpen();

        Whitebox.<AtomicBoolean>getInternalState(f, "closed").set(true);
        setInternalState(f, "fileKey", mock(FileKey.class));

        try {
            // closed, ensure throws
            f.ensureOpen();
            fail();
        } catch (FileClosedException ignore) {
        }
    }

    @Test
    public void checkPos() {
        File<?> f = mock(File.class, Mockito.CALLS_REAL_METHODS);
        f.checkPos(0);
        try {
            f.checkPos(-1);
            fail();
        } catch (IllegalArgumentException ignore) {
        }
    }
}