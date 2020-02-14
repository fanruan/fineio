package com.fineio.v3.buffer;

import com.fineio.io.file.FileBlock;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class BufferClosedException extends RuntimeException {
    public BufferClosedException(long address, FileBlock fileBlock) {
        super(String.format("file %s, address %x", fileBlock.getPath(), address));
    }
}