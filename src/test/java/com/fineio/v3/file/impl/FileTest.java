package com.fineio.v3.file.impl;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.file.FileClosedException;
import com.fineio.v3.memory.Offset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * @author anchore
 * @date 2019/5/16
 */
@RunWith(MockitoJUnitRunner.class)
public class FileTest {
    @Mock
    Connector connector;

    File<?> f = mock(File.class, Mockito.CALLS_REAL_METHODS);

    @Before
    public void setUp() {
        setInternalState(f, "fileBlock", mock(FileBlock.class));
        setInternalState(f, "connector", connector);
        setInternalState(f, "blockOffset", (byte) 10);
    }

    @Test
    public void nthBuf() {
        setInternalState(f, "offset", Offset.BYTE);

        assertEquals(0, f.nthBuf(1));
        assertEquals(1, f.nthBuf(1024));
    }

    @Test
    public void nthVal() {
        setInternalState(f, "offset", Offset.INT);

        assertEquals(1, f.nthVal(1));
        assertEquals(128, f.nthVal(128));
        assertEquals(1, f.nthVal(257));
    }

    @Test
    public void ensureOpen() {
        setInternalState(f, "closed", new AtomicBoolean(false));

        // to be closed
        f.ensureOpen();

        Whitebox.<AtomicBoolean>getInternalState(f, "closed").set(true);
        setInternalState(f, "fileBlock", mock(FileBlock.class));

        try {
            // closed, ensure throws
            f.ensureOpen();
            fail();
        } catch (FileClosedException ignore) {
        }
    }

//    @Test
//    public void exists() {
//        when(connector.exists(argThat(new ArgumentMatcher<Object>() {
//            @Override
//            public boolean matches(Object arg) {
//                return arg.getName().equals(File.META);
//            }
//        }))).thenReturn(true, false);
//
//        assertThat(f.exists()).isTrue();
//        assertThat(f.exists()).isFalse();
//    }

//    @Test
//    public void writeMeta() throws IOException {
//        setInternalState(f, "offset", Offset.BYTE);
//        File.writeMeta(f, 1);
//
//        verify(connector).write(argThat(arg -> arg.getName().equals(File.META)), eq(new byte[]{0xA, 0, 0, 0, 1}));
//    }
//
//    @Test
//    public void initMetaAndGetLastPos() throws IOException {
//        setInternalState(f, "offset", Offset.BYTE);
//        when(connector.exists(any(FileBlock.class))).thenReturn(false, true);
//
//        assertThat(File.initMetaAndGetLastPos(f)).isZero();
//
//        when(connector.read(argThat(arg -> arg.getName().equals(File.META))))
//                .thenReturn(new ByteArrayInputStream(new byte[]{0xA, 0, 0, 0, 1}))
//                .thenThrow(new IOException())
//                .thenReturn(new ByteArrayInputStream(new byte[1]));
//
//        assertThat(File.initMetaAndGetLastPos(f)).isEqualTo(1);
//        assertThat(f.blockOffset).isEqualTo((byte) 0xA);
//        assertThat(File.initMetaAndGetLastPos(f)).isZero();
//        assertThat(File.initMetaAndGetLastPos(f)).isZero();
//    }
}