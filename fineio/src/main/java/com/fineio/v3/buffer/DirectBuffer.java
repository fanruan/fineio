package com.fineio.v3.buffer;

import com.fineio.accessor.buffer.Buf;
import com.fineio.io.file.FileBlock;

import java.io.Closeable;

/**
 * @author anchore
 * @date 2019/4/16
 */
public interface DirectBuffer extends Closeable, Buf {
    FileBlock getFileBlock();

    long getAddress();

    int getSizeInBytes();

    int getCapInBytes();

    @Override
    void close();

    void letGcHelpRelease();
}