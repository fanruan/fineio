package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.edit.ByteEditBuffer;
import com.fineio.io.edit.CharEditBuffer;
import com.fineio.io.edit.DoubleEditBuffer;
import com.fineio.io.edit.FloatEditBuffer;
import com.fineio.io.edit.IntEditBuffer;
import com.fineio.io.edit.LongEditBuffer;
import com.fineio.io.edit.ShortEditBuffer;
import com.fineio.io.file.EditModel;
import com.fineio.storage.Connector;

import java.net.URI;

public final class DirectEditIOFile<T extends Buffer> extends DirectIOFile<T> {
    public static final EditModel<ByteBuffer> BYTE;
    public static final EditModel<DoubleBuffer> DOUBLE;
    public static final EditModel<LongBuffer> LONG;
    public static final EditModel<IntBuffer> INT;
    public static final EditModel<FloatBuffer> FLOAT;
    public static final EditModel<CharBuffer> CHAR;
    public static final EditModel<ShortBuffer> SHORT;

    static {
        BYTE = ByteEditBuffer.MODEL;
        DOUBLE = DoubleEditBuffer.MODEL;
        LONG = LongEditBuffer.MODEL;
        INT = IntEditBuffer.MODEL;
        FLOAT = FloatEditBuffer.MODEL;
        CHAR = CharEditBuffer.MODEL;
        SHORT = ShortEditBuffer.MODEL;
    }

    DirectEditIOFile(final Connector connector, final URI uri, final EditModel<T> editModel) {
        super(connector, uri, editModel);
    }

    public static final <E extends Buffer> DirectEditIOFile<E> createFineIO(final Connector connector, final URI uri, final EditModel<E> editModel) {
        return new DirectEditIOFile<E>(connector, uri, editModel);
    }
}
