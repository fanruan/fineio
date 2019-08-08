package com.fineio.v3.buffer.impl;

import com.fineio.io.Buffer.Listener;
import com.fineio.io.ByteBuffer;
import com.fineio.io.ByteBuffer.ByteReadBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.ByteDirectBuffer;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/5/21
 */
public class ByteBufferConcurrentBenchmark {

    public static void main(String[] args) throws Exception {
        jmhBenchmark();
    }

    private static void jmhBenchmark() throws RunnerException {
        String className = ByteBufferConcurrentBenchmark.class.getSimpleName();
        new Runner(new OptionsBuilder()
                .include(className)
                .forks(1)
                .warmupIterations(1)
                .threads(Threads.MAX)
                .shouldFailOnError(true)
                .timeUnit(TimeUnit.MILLISECONDS)
                .syncIterations(false)
                .result(System.getProperty("user.dir") + "/jmh_report_" + className)
                .resultFormat(ResultFormatType.JSON)
                .build()).run();
    }

    @Benchmark
    public void seqRead(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.buf.getByte(i);
        }
    }

    @Benchmark
    public void randomRead(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.buf.getByte(buf.randomReadIndex[i]);
        }
    }

    @Benchmark
    public void seqReadOld(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.oldBuf.get(i);
        }
    }

    @Benchmark
    public void randomReadOld(ReadBuf buf) {
        for (int i = 0; i < buf.cap; i++) {
            buf.oldBuf.get(buf.randomReadIndex[i]);
        }
    }

    @State(Scope.Benchmark)
    public static class ReadBuf {
        int cap = 1024;
        Random random = new Random();

        ByteDirectBuffer buf;
        ByteReadBuffer oldBuf;

        int[] randomReadIndex = random.ints(0, cap).distinct().limit(cap).toArray();

        Blackhole bh;

        @Setup
        public void setupOnTrial(Blackhole bh) throws IOException {
            this.bh = bh;

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
}
