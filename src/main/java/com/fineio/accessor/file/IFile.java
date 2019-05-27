package com.fineio.accessor.file;

import com.fineio.accessor.buffer.Buf;

import java.io.Closeable;

/**
 * @author yee
 * @date 2019-05-22
 */
public interface IFile<B extends Buf> extends Closeable {
    @Override
    void close();

    boolean exists();
}
