package com.fineio.v3.file;

import com.fineio.io.file.FileBlock;

/**
 * @author anchore
 * @date 2019/5/21
 */
public class FileClosedException extends RuntimeException {
    public FileClosedException(FileBlock FileBlock) {
        super(FileBlock.getPath());
    }
}