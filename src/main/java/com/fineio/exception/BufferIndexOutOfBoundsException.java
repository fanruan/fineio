package com.fineio.exception;

import java.net.URI;

/**
 * @author daniel
 * @date 2017/2/13
 */
public class BufferIndexOutOfBoundsException extends IndexOutOfBoundsException {

    public BufferIndexOutOfBoundsException(long index) {
        super("Index out of range: " + index);
    }

    public BufferIndexOutOfBoundsException(URI uri, long index, long maxSize) {
        super("Index out of range: " + uri + " index: " + index + " maxSize: " + maxSize);
    }
}
