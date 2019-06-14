package com.fineio.v3.buffer;

import com.fineio.io.file.FileBlock;

/**
 * @author anchore
 * @date 2019/5/15
 */
public class BufferAllocateFailedException extends RuntimeException {
    public BufferAllocateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BufferAllocateFailedException ofAllocate(int requestSize, Throwable cause, FileBlock fileBlock) {
        return new BufferAllocateFailedException(
                String.format("allocate buffer failed, request %d byte(s), file %s", requestSize, fileBlock.getPath()), cause);
    }

    public static BufferAllocateFailedException ofReallocate(long address, int requestSize, int oldSize, Throwable cause, FileBlock fileBlock) {
        return new BufferAllocateFailedException(
                String.format("reallocate buffer@%x failed, request %d byte(s), previous %d byte(s), file %s", address, requestSize, oldSize, fileBlock.getPath()), cause);
    }
}