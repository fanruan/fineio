package com.fineio.v3.buffer;

import com.fineio.io.file.FileBlock;

/**
 * @author anchore
 * @date 2019/6/11
 */
public class BufferAcquireFailedException extends RuntimeException {
    public BufferAcquireFailedException(FileBlock fileBlock) {
        super(fileBlock.getPath());
    }

    public BufferAcquireFailedException(FileBlock fileBlock, Throwable e) {
        super(fileBlock.getPath(), e);
    }
}