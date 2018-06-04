package com.fineio.v1.directio;

import com.fineio.storage.Connector;
import com.fineio.v1.io.Buffer;
import com.fineio.v1.io.ByteBuffer;
import com.fineio.v1.io.CharBuffer;
import com.fineio.v1.io.DoubleBuffer;
import com.fineio.v1.io.FloatBuffer;
import com.fineio.v1.io.IntBuffer;
import com.fineio.v1.io.LongBuffer;
import com.fineio.v1.io.ShortBuffer;
import com.fineio.v1.io.file.ReadModel;
import com.fineio.v1.io.read.ByteReadBuffer;
import com.fineio.v1.io.read.CharReadBuffer;
import com.fineio.v1.io.read.DoubleReadBuffer;
import com.fineio.v1.io.read.FloatReadBuffer;
import com.fineio.v1.io.read.IntReadBuffer;
import com.fineio.v1.io.read.LongReadBuffer;
import com.fineio.v1.io.read.ShortReadBuffer;

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
