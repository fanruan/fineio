package com.fineio.v2.cache.pool;

import com.fineio.v2.io.AbstractBuffer;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;

/**
 * @author yee
 * @date 2018/5/31
 */
public enum PoolMode {
    BYTE(ByteBuffer.class), CHAR(CharBuffer.class), SHORT(ShortBuffer.class),
    INT(IntBuffer.class), LONG(LongBuffer.class), FLOAT(FloatBuffer.class), DOUBLE(DoubleBuffer.class);
    private Class<? extends AbstractBuffer> clazz;

    PoolMode(Class<? extends AbstractBuffer> clazz) {
        this.clazz = clazz;
    }

    public boolean isAssignableFrom(Class<? extends Buffer> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
}
