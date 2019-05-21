package com.fineio.v3.buffer;

/**
 * @author anchore
 * @date 2019/5/15
 */
public class BufferAllocateFailedException extends RuntimeException {
    public BufferAllocateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public static BufferAllocateFailedException ofAllocate(int requestSize, Throwable cause) {
        return new BufferAllocateFailedException(String.format("allocate buffer failed, request %d byte(s)", requestSize), cause);
    }

    public static BufferAllocateFailedException ofReallocate(long address, int requestSize, int oldSize, Throwable cause) {
        return new BufferAllocateFailedException(String.format("reallocate buffer@%x failed, request %d byte(s), previous %d byte(s)", address, requestSize, oldSize), cause);
    }
}