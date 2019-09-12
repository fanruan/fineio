package com.fineio.v2_1.file.read;

import com.fineio.memory.MemoryConstants;
import com.fineio.v2_1.connector.FileConnector;
import com.fineio.v2_1.unsafe.IntUnsafeBuf;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author yee
 * @date 2019/9/12
 */
public class ReadIOFileTest {

    @Test
    public void getInt() throws IOException, ExecutionException, InterruptedException {
        final FileConnector connector = new FileConnector();
//        final WriteIOFile<IntUnsafeBuf> file = WriteIOFile.createFile(connector, URI.create("/Users/yee/testLongHead/"), MemoryConstants.OFFSET_INT);
//        for (int i = 0; i < 100000000; i++) {
//            file.put(i, i);
//        }
//        file.close();
        ExecutorService service = Executors.newFixedThreadPool(100);
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < 200; i++) {
            Future future = service.submit(new Runnable() {
                @Override
                public void run() {
                    final ReadIOFile<IntUnsafeBuf> readFile = ReadIOFile.createFile(connector, URI.create("/Users/yee/testLongHead/"), MemoryConstants.OFFSET_INT);
                    long sum = 0, sum1 = 0;
                    final long start = System.currentTimeMillis();
                    for (int i = 0; i < 100000000; i++) {
                        sum += readFile.getInt(i);
//                        sum1 += i;
                    }
//                    System.out.println(System.currentTimeMillis() - start);
//                    assertEquals(sum, sum1);
                    try {
                        readFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            futures.add(future);
        }

        for (Future future : futures) {
            future.get();
        }

    }
}