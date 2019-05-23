package com.fineio.accessor;

import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.accessor.store.IConnector;
import com.fineio.java.JavaVersion;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.buffer.DoubleDirectBuffer;
import com.fineio.v3.buffer.IntDirectBuffer;
import com.fineio.v3.buffer.LongDirectBuffer;
import com.fineio.v3.connector.Connector;
import com.fineio.v3.file.FileKey;
import com.fineio.v3.file.impl.File;
import com.fineio.v3.file.impl.read.ByteReadFile;
import com.fineio.v3.file.impl.read.DoubleReadFile;
import com.fineio.v3.file.impl.read.IntReadFile;
import com.fineio.v3.file.impl.read.LongReadFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * @author yee
 * @date 2019-05-23
 */
@PrepareForTest({JavaVersion.class})
@RunWith(PowerMockRunner.class)
public class FineIOAccessorTest {

    @Test
    public void createFile() {
        PowerMockito.mockStatic(JavaVersion.class);
        Mockito.when(JavaVersion.isOverJava8()).thenReturn(true);
        IConnector mock = Mockito.mock(Connector.class);

        IFile<ByteBuf> file = FineIOAccessor.INSTANCE.createFile(mock, URI.create("0"), BaseModel.ofByte());
        assertTrue(file instanceof File);
    }

    @Test
    public void put() throws IOException {
        PowerMockito.mockStatic(JavaVersion.class);
        Mockito.when(JavaVersion.isOverJava8()).thenReturn(true);
        IConnector mock = Mockito.mock(Connector.class);
        IFile<ByteBuf> file = FineIOAccessor.INSTANCE.createFile(mock, URI.create("0"), BaseModel.ofByte().asAppend());
        assertTrue(file instanceof IAppendFile);
        FineIOAccessor.INSTANCE.put((IAppendFile<ByteBuf>) file, (byte) 0);
        file.close();
        Mockito.verify(mock).write(Mockito.any(Block.class), Mockito.any(InputStream.class));
    }

    @Test
    public void put1() throws IOException {
        PowerMockito.mockStatic(JavaVersion.class);
        Mockito.when(JavaVersion.isOverJava8()).thenReturn(true);
        IConnector mock = Mockito.mock(Connector.class);
        IFile<ByteBuf> file = FineIOAccessor.INSTANCE.createFile(mock, URI.create("0"), BaseModel.ofByte().asWrite());
        assertTrue(file instanceof IWriteFile);
        FineIOAccessor.INSTANCE.put((IWriteFile<ByteBuf>) file, 0, (byte) 0);
        file.close();
        Mockito.verify(mock).write(Mockito.any(Block.class), Mockito.any(InputStream.class));
    }

    @Test
    public void getByte() throws Exception {
        ByteReadFile rf = spy(new ByteReadFile(mock(FileKey.class), mock(Connector.class)));

        ByteDirectBuffer buf = Mockito.mock(ByteDirectBuffer.class);
        PowerMockito.doReturn(buf).when(rf, File.class.getDeclaredMethod("getBuffer", int.class)).withArguments(0);

        Mockito.when(buf.getByte(0)).thenReturn((byte) 1);

        assertEquals(1, FineIOAccessor.INSTANCE.getByte(rf, 0));
        PowerMockito.verifyPrivate(rf).invoke("ensureOpen");
        PowerMockito.verifyPrivate(rf).invoke("checkPos", 0L);
    }

    @Test
    public void getLong() throws Exception {
        LongReadFile rf = spy(new LongReadFile(mock(FileKey.class), mock(Connector.class)));

        LongDirectBuffer buf = Mockito.mock(LongDirectBuffer.class);
        PowerMockito.doReturn(buf).when(rf, File.class.getDeclaredMethod("getBuffer", int.class)).withArguments(0);

        Mockito.when(buf.getLong(0)).thenReturn(1L);

        assertEquals(1, FineIOAccessor.INSTANCE.getLong(rf, 0));
        PowerMockito.verifyPrivate(rf).invoke("ensureOpen");
        PowerMockito.verifyPrivate(rf).invoke("checkPos", 0L);
    }

    @Test
    public void getInt() throws Exception {
        IntReadFile rf = spy(new IntReadFile(mock(FileKey.class), mock(Connector.class)));

        IntDirectBuffer buf = Mockito.mock(IntDirectBuffer.class);
        PowerMockito.doReturn(buf).when(rf, File.class.getDeclaredMethod("getBuffer", int.class)).withArguments(0);

        Mockito.when(buf.getInt(0)).thenReturn(1);

        assertEquals(1, FineIOAccessor.INSTANCE.getInt(rf, 0));
        PowerMockito.verifyPrivate(rf).invoke("ensureOpen");
        PowerMockito.verifyPrivate(rf).invoke("checkPos", 0L);
    }

    @Test
    public void getDouble() throws Exception {
        DoubleReadFile rf = spy(new DoubleReadFile(mock(FileKey.class), mock(Connector.class)));

        DoubleDirectBuffer buf = Mockito.mock(DoubleDirectBuffer.class);
        PowerMockito.doReturn(buf).when(rf, File.class.getDeclaredMethod("getBuffer", int.class)).withArguments(0);

        Mockito.when(buf.getDouble(0)).thenReturn(1D);

        assertEquals(1D, FineIOAccessor.INSTANCE.getDouble(rf, 0), 0.01);
        PowerMockito.verifyPrivate(rf).invoke("ensureOpen");
        PowerMockito.verifyPrivate(rf).invoke("checkPos", 0L);
    }
}