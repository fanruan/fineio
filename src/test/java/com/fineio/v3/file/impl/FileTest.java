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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
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
        when(connector.getBlockOffset()).thenReturn((byte) 10);
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

    @Test
    public void exists() {
        when(connector.exists(argThat(arg -> arg.getName().equals("last_pos")))).thenReturn(true, false);

        assertThat(f.exists()).isTrue();
        assertThat(f.exists()).isFalse();
    }

    @Test
    public void writeLastPos() throws IOException {
        File.writeLastPos(f, 1);

        verify(connector).write(argThat(arg -> arg.getName().equals("last_pos")), eq(new byte[]{0, 0, 0, 1}));
    }

    @Test
    public void getLastPos() throws IOException {
        when(connector.exists(any(FileBlock.class))).thenReturn(false, true);

        assertThat(File.getLastPos(f)).isZero();

        when(connector.read(argThat(arg -> arg.getName().equals("last_pos"))))
                .thenReturn(new ByteArrayInputStream(new byte[]{0, 0, 0, 1}))
                .thenThrow(new IOException())
                .thenReturn(new ByteArrayInputStream(new byte[1]));

        assertThat(File.getLastPos(f)).isEqualTo(1);
        assertThat(File.getLastPos(f)).isZero();
        assertThat(File.getLastPos(f)).isZero();
    }
}