package com.fineio.test.file.writer;

import com.fineio.file.writer.DirectInputStream;
import com.fineio.memory.MemoryUtils;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by daniel on 2017/2/23.
 */
public class DirectInputStreamTest extends TestCase{

    public void testInputStream() throws IOException {
        byte[] bytes = createRandomByte();
        int len = bytes.length;
        long s = MemoryUtils.allocate(len);
        MemoryUtils.copyMemory(bytes, s);
        DirectInputStream inputStream = new DirectInputStream(s, len);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] temp = new byte[10];
        int x = 0;
        while((x = inputStream.read(temp))> 0) {
            out.write(temp, 0, x);
        }
        out.flush();
        byte[] res = out.toByteArray();
        for(int i = 0; i< bytes.length; i++) {
            assertEquals(bytes[i], res[i]);
        }

        inputStream = new DirectInputStream(s, len);
        out = new ByteArrayOutputStream();

        temp = new byte[len];
        x = 0;
        while((x = inputStream.read(temp))> 0) {
            out.write(temp, 0, x);
        }
        out.flush();
        res = out.toByteArray();
        for(int i = 0; i< bytes.length; i++) {
            assertEquals(bytes[i], res[i]);
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
