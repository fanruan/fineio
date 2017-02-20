package com.fineio.file;

import com.fineio.io.write.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class FineWriteIOFile<T extends Write> extends  FineIOFile<T> {

    FineWriteIOFile(Connector connector, URI uri, Class<T> clazz){
        super(connector, uri, clazz);
        this.block_size_offset = (byte) (connector.getBlockOffset() - getOffset());
        single_block_len = (1L << block_size_offset) - 1;
    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends Write> FineWriteIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new FineWriteIOFile<E>(connector, uri, clazz);
    }

    public static void put(FineIOFile<DoubleWriteBuffer> file, int p, double d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(FineIOFile<ByteWriteBuffer> file, int p, byte d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(FineIOFile<CharWriteBuffer> file, int p, char d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(FineIOFile<FloatWriteBuffer> file, int p, float d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(FineIOFile<LongWriteBuffer> file, int p, long d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(FineIOFile<IntWriteBuffer> file, int p, int d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(FineIOFile<ShortWriteBuffer> file, int p, short d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

}
