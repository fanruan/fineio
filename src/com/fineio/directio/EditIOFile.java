package com.fineio.io.file;

import com.fineio.io.*;
import com.fineio.io.edit.*;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class EditIOFile<T extends Buffer> extends AbstractReadIOFile<T> {

    public static final EditModel<ByteBuffer> BYTE = ByteEditBuffer.MODEL;

    public static final EditModel<DoubleBuffer> DOUBLE = DoubleEditBuffer.MODEL;

    public static final EditModel<LongBuffer> LONG = LongEditBuffer.MODEL;

    public static final EditModel<IntBuffer> INT = IntEditBuffer.MODEL;

    public static final EditModel<FloatBuffer> FLOAT = FloatEditBuffer.MODEL;

    public static final EditModel<CharBuffer> CHAR = CharEditBuffer.MODEL;

    public static final EditModel<ShortBuffer> SHORT = ShortEditBuffer.MODEL;

    EditIOFile(Connector connector, URI uri, EditModel<T> model){
        super(connector, uri, model);
    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param model 子类型
     * @param <E> 继承EditBuffer的子类型
     * @return
     */
    public static final <E extends Buffer> EditIOFile<E> createFineIO(Connector connector, URI uri, EditModel<E> model){
        return  new EditIOFile<E>(connector, uri, model);
    }


}
