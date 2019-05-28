package com.fineio.v3.buffer.impl;

import com.fineio.accessor.FileMode;
import com.fineio.io.Buffer.Listener;
import com.fineio.io.ByteBuffer;
import com.fineio.io.ByteBuffer.ByteReadBuffer;
import com.fineio.io.ByteBuffer.ByteWriteBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/5/21
 */
public class ByteBufferBenchmark {

    @Benchmark
    public void seqRead(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.buf.getByte(i);
        }
    }

    @Benchmark
    public void seqReadOld(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.oldBuf.get(i);
        }
    }

    @Benchmark
    public void randomRead(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.buf.getByte(buf.randomReadIndex[i]);
        }
    }

    @Benchmark
    public void randomReadOld(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.oldBuf.get(buf.randomReadIndex[i]);
        }
    }

    @State(Scope.Thread)
    public static class ReadBuf {
        int cap = 1024;
        Random random = new Random();

        ByteDirectBuffer buf;
        ByteReadBuffer oldBuf;

        int[] randomReadIndex = random.ints(0, cap).distinct().limit(cap).toArray();

        @Setup
        public void setupOnTrial() throws IOException {
            int sizeInBytes = cap << Offset.BYTE.getOffset();
            buf = new ByteDirectBuf(MemoryUtils.allocate(sizeInBytes), cap, mock(FileBlock.class), cap);

            byte[] bytes = new byte[1024];
            for (int i = 0; i < cap; i++) {
                byte b = (byte) random.nextInt(255);
                buf.putByte(i, b);
                bytes[i] = b;
            }

            Connector connector = mock(Connector.class);
            when(connector.read(any(FileBlock.class))).thenReturn(new ByteArrayInputStream(bytes));
            oldBuf = new ByteBuffer(connector, URI.create(""), false, mock(Listener.class)).asRead();
            // 预载数据
            oldBuf.get(0);
            oldBuf.get(cap - 1);
        }

        @TearDown
        public void tearDownOnTrial() {
            buf.close();
            oldBuf.close();
        }
    }

    @Benchmark
    public void seqWrite(WriteBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.buf.putByte(i, (byte) 1);
        }
    }

    @Benchmark
    public void randomWrite(WriteBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.buf.putByte(buf.randomWriteIndex[i], (byte) 1);
        }
    }

    @Benchmark
    public void seqWriteOld(WriteBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.oldBuf.put(i, (byte) 1);
        }
    }

    @Benchmark
    public void randomWriteOld(WriteBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.oldBuf.put(buf.randomWriteIndex[i], (byte) 1);
        }
    }

    @State(Scope.Thread)
    public static class WriteBuf {
        int cap = 1024;
        Random random = new Random();

        ByteDirectBuffer buf;
        ByteWriteBuffer oldBuf;

        int[] randomWriteIndex = new int[cap];

        @Setup
        public void setupOnTrial() {
            AtomicInteger idx = new AtomicInteger(0);
            // 避免出现一开始容量就增加到最大，在每个grow区间内随机写入，既有随机又有grow
            for (int left = 0, right = 16; right <= cap; left = right, right <<= 1) {
                random.ints(left, right).distinct().limit(right - left).forEach(i -> randomWriteIndex[idx.getAndIncrement()] = i);
            }
        }

        @Setup(Level.Invocation)
        public void setupOnInvocation() {
            buf = new ByteDirectBuf(mock(FileBlock.class), cap, FileMode.WRITE);

            oldBuf = new ByteBuffer(mock(Connector.class), new FileBlock(null), 10, false, mock(Listener.class)).asWrite();
        }

        @TearDown(Level.Invocation)
        public void tearDownOnInvocation() {
            buf.close();
            oldBuf.close();
        }
    }

    public static void main(String[] args) throws Exception {
        jmhBenchmark();
    }

    private static void jmhBenchmark() throws RunnerException {
        String className = ByteBufferBenchmark.class.getSimpleName();
        new Runner(new OptionsBuilder()
                .include(className)
                .forks(1)
                .warmupIterations(1)
                .threads(Threads.MAX)
                .shouldFailOnError(true)
                .timeUnit(TimeUnit.MILLISECONDS)
                .result(System.getProperty("user.dir") + "/jmh_report_" + className)
                .resultFormat(ResultFormatType.JSON)
                .build()).run();
    }

    private static void manualBenchmark() throws IOException {
        ReadBuf readBuf = new ReadBuf();
        readBuf.setupOnTrial();

        ByteBufferBenchmark benchmark = new ByteBufferBenchmark();
        int n = 10000000;

        CompletableFuture.runAsync(() -> {
            System.out.println("1 " + System.currentTimeMillis());
            for (int i = 0; i < n; i++) {
                benchmark.randomRead(readBuf);
            }
            System.out.println("1 " + System.currentTimeMillis());
        });

        CompletableFuture.runAsync(() -> {
            System.out.println("2 " + System.currentTimeMillis());
            for (int i = 0; i < n; i++) {
                benchmark.randomReadOld(readBuf);
            }
            System.out.println("2 " + System.currentTimeMillis());
        });
    }
}
