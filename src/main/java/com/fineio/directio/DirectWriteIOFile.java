package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.WriteModel;
import com.fineio.io.write.ByteWriteBuffer;
import com.fineio.io.write.CharWriteBuffer;
import com.fineio.io.write.DoubleWriteBuffer;
import com.fineio.io.write.FloatWriteBuffer;
import com.fineio.io.write.IntWriteBuffer;
import com.fineio.io.write.LongWriteBuffer;
import com.fineio.io.write.ShortWriteBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

public final class DirectWriteIOFile<T extends Buffer> extends DirectIOFile<T> {
    public static final WriteModel<ByteBuffer> BYTE;
    public static final WriteModel<DoubleBuffer> DOUBLE;
    public static final WriteModel<LongBuffer> LONG;
    public static final WriteModel<IntBuffer> INT;
    public static final WriteModel<FloatBuffer> FLOAT;
    public static final WriteModel<CharBuffer> CHAR;
    public static final WriteModel<ShortBuffer> SHORT;

    static {
        BYTE = ByteWriteBuffer.MODEL;
        DOUBLE = DoubleWriteBuffer.MODEL;
        LONG = LongWriteBuffer.MODEL;
        INT = IntWriteBuffer.MODEL;
        FLOAT = FloatWriteBuffer.MODEL;
        CHAR = CharWriteBuffer.MODEL;
        SHORT = ShortWriteBuffer.MODEL;
    }

    DirectWriteIOFile(final Connector connector, final URI uri, final WriteModel<T> writeModel) {
        super(connector, uri, writeModel);
    }

    public static final <E extends Buffer> DirectWriteIOFile<E> createFineIO(final Connector connector, final URI uri, final WriteModel<E> writeModel) {
        return new DirectWriteIOFile<E>(connector, uri, writeModel);
    }
}
