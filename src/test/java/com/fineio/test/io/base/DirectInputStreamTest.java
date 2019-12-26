package com.fineio.test.io.base;

import com.fineio.exception.StreamCloseException;
import com.fineio.io.base.Checker;
import com.fineio.io.base.DirectInputStream;
import com.fineio.memory.MemoryUtils;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by daniel on 2017/2/23.
 */
@Ignore
public class DirectInputStreamTest extends TestCase{


    public void testException() throws  Exception {

        Constructor<DirectInputStream> c = DirectInputStream.class.getDeclaredConstructor(long.class, int.class, Checker.class);
        c.setAccessible(true);
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long s = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, s);
        final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        DirectInputStream is = c.newInstance(s, len, new Checker() {
            public boolean check() {
                return atomicBoolean.get();
            }
        });
        boolean exp = false;
        try {
            is.read();
        } catch (StreamCloseException e){
            exp = true;
        }
        assertFalse(exp);
        atomicBoolean.set(false);
        try {
            is.read();
        } catch (StreamCloseException e){
            exp = true;
        }
        assertTrue(exp);

    }

    public void testInputStream() throws Exception {
        Constructor<DirectInputStream> c = DirectInputStream.class.getDeclaredConstructor(long.class, int.class, Checker.class);
        c.setAccessible(true);
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long s = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, s);
        DirectInputStream inputStream = c.newInstance(s, len, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] temp = new byte[10];
        int x = 0;
        while ((x = inputStream.read(temp)) > 0) {
            out.write(temp, 0, x);
        }
        out.flush();
        byte[] res = out.toByteArray();
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], res[i]);
        }

        inputStream = c.newInstance(s, len, new Checker() {
            public boolean check() {
                return true;
            }
        });
        out = new ByteArrayOutputStream();

        temp = new byte[len];
        x = 0;
        while ((x = inputStream.read(temp)) > 0) {
            out.write(temp, 0, x);
        }
        out.flush();
        res = out.toByteArray();
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], res[i]);
        }
        inputStream = c.newInstance(s, len, null);
        assertEquals(inputStream.size(), bytes.length);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        while (true) {
            String line = reader.readLine();
            String line2 = reader2.readLine();
            assertEquals(line, line2);
            if (line == null && line2==null) break;

        }
    }

    private byte[] createRandomByte(){
        int len = (int) (Math.random()*1000);
        byte[] arrays = new byte[len];
        for(int i = 0; i< len; i++){
            arrays[i] =  (byte)(Double.doubleToLongBits(Math.random() * 100000000000d));
        }
        return arrays;
    }
}
