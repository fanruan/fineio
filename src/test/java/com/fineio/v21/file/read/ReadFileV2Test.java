package com.fineio.v21.file.read;

import com.fineio.FineIO;
import com.fineio.io.IntBuffer;
import com.fineio.io.file.AppendIOFile;
import com.fineio.io.file.ReadIOFile;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2019/9/12
 */
public class ReadFileV2Test {

    @Test
    public void testRead() throws IOException, ExecutionException, InterruptedException {
        FineIO.start();
        final FileConnector connector = new FileConnector();
        ExecutorService service = Executors.newFixedThreadPool(1);
        for (int j = 0; j < 10000;j++){
            for (int i = 0; i < 10; i++) {
                final int finalI = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        ReadIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create("v21" + finalI), FineIO.MODEL.READ_INT, true);
                        int sum = 0;
                        for (int i = 0; i < 100000000; i++) {
                            sum += FineIO.getInt(file, i);
                        }
                        System.out.println(sum);
                        file.close();
                    }
                });
            }
        }
        Thread.sleep(10000000);

    }
}