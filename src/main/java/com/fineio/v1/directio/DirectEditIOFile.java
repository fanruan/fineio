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
import com.fineio.v1.io.edit.ByteEditBuffer;
import com.fineio.v1.io.edit.CharEditBuffer;
import com.fineio.v1.io.edit.DoubleEditBuffer;
import com.fineio.v1.io.edit.FloatEditBuffer;
import com.fineio.v1.io.edit.IntEditBuffer;
import com.fineio.v1.io.edit.LongEditBuffer;
import com.fineio.v1.io.edit.ShortEditBuffer;
import com.fineio.v1.io.file.EditModel;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class DirectEditIOFile<T extends Buffer> extends DirectIOFile<T> {

    public static final EditModel<ByteBuffer> BYTE = ByteEditBuffer.MODEL;

    public static final EditModel<DoubleBuffer> DOUBLE = DoubleEditBuffer.MODEL;

    public static final EditModel<LongBuffer> LONG = LongEditBuffer.MODEL;

    public static final EditModel<IntBuffer> INT = IntEditBuffer.MODEL;

    public static final EditModel<FloatBuffer> FLOAT = FloatEditBuffer.MODEL;

    public static final EditModel<CharBuffer> CHAR = CharEditBuffer.MODEL;

    public static final EditModel<ShortBuffer> SHORT = ShortEditBuffer.MODEL;

    DirectEditIOFile(Connector connector, URI uri, EditModel<T> model){
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
    public static final <E extends Buffer> DirectEditIOFile<E> createFineIO(Connector connector, URI uri, EditModel<E> model){
        return  new DirectEditIOFile<E>(connector, uri, model);
    }


}
