package com.fineio.v3.connector;

import com.fineio.v3.file.Block;
import com.fineio.v3.file.FileKey;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public interface Connector {

    /**
     * @param is
     * @param file
     */
    void write(InputStream is, FileKey file) throws IOException;

    void write(byte[] data, FileKey file) throws IOException;

    /**
     * @param file
     */
    InputStream read(FileKey file) throws IOException;

    /**
     * @param file
     */
    boolean delete(Block file);

    /**
     * @param file
     */
    boolean exists(Block file);

    /**
     *
     */
    int getBlockOffset();

    Block list(String file);
}