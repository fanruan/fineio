package com.fineio.file;

import com.fineio.io.write.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class WriteIOFile<T extends Write> extends IOFile<T> {

    WriteIOFile(Connector connector, URI uri, Class<T> clazz){
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
    public static final <E extends Write> WriteIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new WriteIOFile<E>(connector, uri, clazz);
    }

    public static void put(IOFile<DoubleWriteBuffer> file, int p, double d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<ByteWriteBuffer> file, int p, byte d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<CharWriteBuffer> file, int p, char d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<FloatWriteBuffer> file, int p, float d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<LongWriteBuffer> file, int p, long d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<IntWriteBuffer> file, int p, int d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<ShortWriteBuffer> file, int p, short d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

}
