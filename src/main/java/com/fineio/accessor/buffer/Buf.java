package com.fineio.accessor.buffer;

import java.io.Closeable;

/**
 * @author yee
 * @date 2019-05-22
 */
public interface Buf extends Closeable {
    @Override
    void close();
}
