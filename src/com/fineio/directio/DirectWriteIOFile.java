package com.fineio.directio;

import com.fineio.io.*;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.WriteModel;
import com.fineio.io.write.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class DirectWriteIOFile<T extends Buffer> extends DirectIOFile<T> {

    public static final WriteModel<ByteBuffer> BYTE = ByteWriteBuffer.MODEL;

    public static final WriteModel<DoubleBuffer> DOUBLE = DoubleWriteBuffer.MODEL;

    public static final WriteModel<LongBuffer> LONG = LongWriteBuffer.MODEL;

    public static final WriteModel<IntBuffer> INT = IntWriteBuffer.MODEL;

    public static final WriteModel<FloatBuffer> FLOAT = FloatWriteBuffer.MODEL;

    public static final WriteModel<CharBuffer> CHAR = CharWriteBuffer.MODEL;

    public static final WriteModel<ShortBuffer> SHORT = ShortWriteBuffer.MODEL;

    DirectWriteIOFile(Connector connector, URI uri, WriteModel<T> model){
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
    public static final <E extends Buffer> DirectWriteIOFile<E> createFineIO(Connector connector, URI uri, WriteModel<E> model){
        return  new DirectWriteIOFile<E>(connector, uri, model);
    }


}
