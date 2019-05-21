package com.fineio.v3.file.impl.write;

import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * @author anchore
 * @date 2019/5/16
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WriteFile.class})
public class WriteFileTest {

    @Test
    public void delete() {
        WriteFile wf = mock(WriteFile.class);
        doCallRealMethod().when(wf).delete();

        Connector connector = mock(Connector.class);
        setInternalState(wf, "connector", connector);
        FileKey fileKey = mock(FileKey.class);
        setInternalState(wf, "fileKey", fileKey);

        wf.delete();

        verify(connector).delete(fileKey);
    }

    @Test
    public void syncBufIfNeed() throws Exception {
        WriteFile wf = mock(WriteFile.class);
        doCallRealMethod().when(wf).syncBufIfNeed(anyInt());

        setInternalState(wf, "curBuf", -1);
        ConcurrentMap<?, ?> buffers = mock(ConcurrentMap.class);
        setInternalState(wf, "buffers", buffers);
        setInternalState(wf, "connector", mock(Connector.class));

        wf.syncBufIfNeed(0);
        verifyZeroInteractions(buffers);
        assertEquals(0, (int) getInternalState(wf, "curBuf"));

        DirectBuffer buf = mock(DirectBuffer.class);
        doReturn(buf).when(wf, "getBuffer", 0);
        doNothing().when(wf, "syncBuf", buf);

        wf.syncBufIfNeed(1);
        verifyPrivate(wf).invoke("syncBuf", buf);
        verify(buffers).remove(0);
        assertEquals(1, (int) getInternalState(wf, "curBuf"));
    }

    @Test
    public void close() throws Exception {
        WriteFile wf = mock(WriteFile.class);
        doCallRealMethod().when(wf).close();

        DirectBuffer buf = mock(DirectBuffer.class);
        ConcurrentMap<Integer, DirectBuffer> buffers;
        buffers = spy(new ConcurrentHashMap<>(Collections.singletonMap(0, buf)));
        setInternalState(wf, "buffers", buffers);
        AtomicBoolean closed = spy(new AtomicBoolean(false));
        setInternalState(wf, "closed", closed);
        setInternalState(wf, "connector", mock(Connector.class));

        wf.close();

        assertTrue(buffers.isEmpty());

        wf.close();

        verifyPrivate(wf).invoke("syncBuf", buf);
    }
}