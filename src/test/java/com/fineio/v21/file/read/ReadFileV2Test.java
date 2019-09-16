package com.fineio.v21.file.read;

import com.fineio.FineIO;
import com.fineio.io.LongBuffer;
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
    @Ignore
    public void getInt() throws IOException, ExecutionException, InterruptedException {
        final FileConnector connector = new FileConnector();
//        final WriteIOFile<LongBuffer> file = WriteIOFile.createFile(connector, URI.create("/Users/yee/testLongHead/"), MemoryConstants.OFFSET_LONG);
//        for (int i = 0; i < 100000000; i++) {
//            file.put(i, (long) i);
//        }
//        file.close();
        ExecutorService service = Executors.newFixedThreadPool(10);
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 200; i++) {
            Future future = service.submit(new Runnable() {
                @Override
                public void run() {
                    final ReadIOFile<LongBuffer> ioFile = FineIO.createIOFile(connector, URI.create("/Users/yee/testLongHead/"), FineIO.MODEL.READ_LONG);
                    long sum = 0, sum1 = 0;
                    final long start = System.currentTimeMillis();
                    for (int i = 0; i < 100000000; i++) {
                        sum += FineIO.getLong(ioFile, i);
                        sum1 += i;
                    }
                    System.out.println(System.currentTimeMillis() - start);
                    assertEquals(sum, sum1);
                    ioFile.close();
                }
            });
            futures.add(future);
        }

        for (Future future : futures) {
            future.get();
        }

    }
}