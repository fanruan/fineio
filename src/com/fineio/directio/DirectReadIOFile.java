package com.fineio.directio;

import com.fineio.io.*;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.ReadModel;
import com.fineio.io.read.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/4/25.
 */
public class DirectReadIOFile<T extends Buffer> extends DirectIOFile<T> {

    public static final ReadModel<ByteBuffer> BYTE = ByteReadBuffer.MODEL;

    public static final ReadModel<DoubleBuffer> DOUBLE = DoubleReadBuffer.MODEL;

    public static final ReadModel<LongBuffer> LONG = LongReadBuffer.MODEL;

    public static final ReadModel<IntBuffer> INT = IntReadBuffer.MODEL;

    public static final ReadModel<FloatBuffer> FLOAT = FloatReadBuffer.MODEL;

    public static final ReadModel<CharBuffer> CHAR = CharReadBuffer.MODEL;

    public static final ReadModel<ShortBuffer> SHORT = ShortReadBuffer.MODEL;

    private DirectReadIOFile(Connector connector, URI uri, ReadModel<T> model) {
        super(connector, uri, model);
    }



    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param model 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends Buffer> DirectReadIOFile<E> createFineIO(Connector connector, URI uri, ReadModel<E> model){
        return  new DirectReadIOFile<E>(connector, uri, model);
    }



}
