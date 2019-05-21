package com.fineio.v3.file;

/**
 * @author anchore
 * @date 2019/5/21
 */
public class FileClosedException extends RuntimeException {
    public FileClosedException(FileKey fileKey) {
        super(fileKey.getPath());
    }
}