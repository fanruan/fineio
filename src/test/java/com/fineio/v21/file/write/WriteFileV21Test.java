//package com.fineio.v21.file.write;
//import com.fineio.FineIO;
//import com.fineio.io.IntBuffer;
//import com.fineio.io.file.AppendIOFile;
//import com.fineio.io.file.ReadIOFile;
//import com.fineio.v21.file.read.FileConnector;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * @author yee
// * @date 2019/9/12
// */
//public class WriteFileV21Test {
//
//    @Test
//    public void testWrite() throws IOException, ExecutionException, InterruptedException {
//        FineIO.start();
//        final FileConnector connector = new FileConnector();
//        ExecutorService service = Executors.newFixedThreadPool(100);
//        List<Future> futures = new ArrayList<Future>();
//        for (int i = 0; i < 100; i++) {
//            final int finalI = i;
//            Future future = service.submit(new Runnable() {
//                @Override
//                public void run() {
//                    AppendIOFile<IntBuffer> file = FineIO.createIOFile(connector, URI.create("v21"+ finalI), FineIO.MODEL.APPEND_INT, true);
//                    long sum = 0, sum1 = 0;
//                    final long start = System.currentTimeMillis();
//                    for (int i = 0; i < 100000000; i++) {
//                        FineIO.put(file, i);
//                    }
//                    System.out.println(System.currentTimeMillis() - start);
//                    assertEquals(sum, sum1);
//                    file.close();
//                }
//            });
//            futures.add(future);
//        }
//
//        for (Future future : futures) {
//            future.get();
//        }
//
//    }
//}