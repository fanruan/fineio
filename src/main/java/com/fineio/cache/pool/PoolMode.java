package com.fineio.cache.pool;

import com.fineio.io.AbstractBuffer;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;

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
