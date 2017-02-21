package com.fineio.test.io.edit;

import com.fineio.FineIO;
import com.fineio.base.Bits;
import com.fineio.file.FileBlock;
import com.fineio.file.FileConstants;
import com.fineio.file.IOFile;
import com.fineio.io.edit.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * Created by daniel on 2017/2/21.
 */
public class EditBufferTest extends TestCase {

    public void  testOffSet() throws  Exception {
        int len = (int)(Math.random() * 100d);
        final byte[] res = new byte[16];
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
        EasyMock.expect(connector.read(EasyMock.eq(block))).andAnswer(new IAnswer<InputStream>() {
            @Override
            public InputStream answer() throws Throwable {
                return new ByteArrayInputStream(res);
            }
        }).anyTimes();
        EasyMock.expect(connector.getBlockOffset()).andReturn((byte)22).anyTimes();
        control.replay();
        IOFile editIOFile = FineIO.createIOFile(connector, u, FineIO.MODEL.EDIT_LONG);
        ByteEditBuffer byteEditBuffer = getEditBuffer(editIOFile, ByteEditBuffer.class );
        Method method = ByteEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        int v = (Integer) method.invoke(byteEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_BYTE);
        DoubleEditBuffer doubleEditBuffer = getEditBuffer(editIOFile, DoubleEditBuffer.class);
        method = DoubleEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(doubleEditBuffer);
        LongEditBuffer longEditBuffer = getEditBuffer(editIOFile, LongEditBuffer.class );
        method = LongEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(longEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_LONG);
        IntEditBuffer intEditBuffer = getEditBuffer(editIOFile, IntEditBuffer.class );
        method = IntEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(intEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_INT);
        CharEditBuffer charEditBuffer = getEditBuffer(editIOFile, CharEditBuffer.class );
        method = CharEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(charEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_CHAR);
        FloatEditBuffer floatEditBuffer = getEditBuffer(editIOFile, FloatEditBuffer.class );
        method = FloatEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(floatEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_FLOAT);

        ShortEditBuffer shortEditBuffer = getEditBuffer(editIOFile, ShortEditBuffer.class );
        method = ShortEditBuffer.class.getDeclaredMethod("getLengthOffset");
        method.setAccessible(true);
        v = (Integer) method.invoke(shortEditBuffer);
        assertEquals(v, MemoryConstants.OFFSET_SHORT);

    }

    private static <T extends EditBuffer> T getEditBuffer(IOFile<EditBuffer> EditIOFile, Class<T> clazz) {
        try {
            Method method = IOFile.class.getDeclaredMethod("createBuffer", Class.class, int.class);
            method.setAccessible(true);
            return (T) method.invoke(EditIOFile, clazz, 0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
