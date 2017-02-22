package com.fineio.file;

import com.fineio.io.edit.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class EditIOFile<T extends Edit> extends AbstractReadIOFile<T> {

    EditIOFile(Connector connector, URI uri, Class<T> clazz){
        super(connector, uri, clazz);
    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承EditBuffer的子类型
     * @return
     */
    public static final <E extends Edit> EditIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new EditIOFile<E>(connector, uri, clazz);
    }

    public static void put(IOFile<DoubleEditBuffer> file, long p, double d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<ByteEditBuffer> file, long p, byte d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<CharEditBuffer> file, long p, char d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<FloatEditBuffer> file, long p, float d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<LongEditBuffer> file, long p, long d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<IntEditBuffer> file, long p, int d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public static void put(IOFile<ShortEditBuffer> file, long p, short d) {
        file.getBuffer(file.checkBuffer(file.gi(p))).put(file.gp(p), d);
    }

    public final static long getLong(IOFile<LongEditBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static int getInt(IOFile<IntEditBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }


    public final static int getChar(IOFile<CharEditBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static double getDouble(IOFile<DoubleEditBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static float getFloat(IOFile<FloatEditBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static byte getByte(IOFile<ByteEditBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static short getShort(IOFile<ShortEditBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

}
