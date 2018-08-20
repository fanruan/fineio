package com.fineio.exception;

import java.net.URI;

/**
 * Created by daniel on 2017/2/24.
 */
public class FileCloseException extends RuntimeException {
    public FileCloseException(URI uri) {
        super("Buffer '" + uri + "' had been close");
    }
}
