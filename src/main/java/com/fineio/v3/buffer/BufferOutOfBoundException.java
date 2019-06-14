package com.fineio.v3.buffer;

import com.fineio.io.file.FileBlock;

/**
 * @author anchore
 * @date 2019/4/12
 */
public class BufferOutOfBoundException extends RuntimeException {
    public BufferOutOfBoundException(int pos, int cap, FileBlock fileBlock) {
        super(String.format("file %s, pos %d, cap %d", fileBlock, pos, cap));
    }
}