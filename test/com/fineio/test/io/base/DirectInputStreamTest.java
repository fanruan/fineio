package com.fineio.test.io.base;

import com.fineio.io.base.DirectInputStream;
import com.fineio.memory.MemoryUtils;
import junit.framework.TestCase;

import java.io.*;
import java.lang.reflect.Constructor;

/**
 * Created by daniel on 2017/2/23.
 */
public class DirectInputStreamTest extends TestCase{

    public void testInputStream() throws Exception {
        Constructor<DirectInputStream> c = DirectInputStream.class.getDeclaredConstructor(long.class, int.class);
        c.setAccessible(true);
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long s = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, s);
        DirectInputStream inputStream = c.newInstance(s, len);
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

        inputStream = c.newInstance(s, len);
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
        inputStream = c.newInstance(s, len);
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
