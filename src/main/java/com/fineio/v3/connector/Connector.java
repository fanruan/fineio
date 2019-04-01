package com.fineio.v3.connector;

import com.fineio.v3.file.FileBlock;

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
    void write(InputStream is, FileBlock file) throws IOException;

    void write(byte[] data, FileBlock file) throws IOException;

    /**
     * @param file
     */
    void read(FileBlock file) throws IOException;

    /**
     * @param file
     */
    boolean delete(FileBlock file);

    /**
     * @param file
     */
    boolean exists(FileBlock file);

    /**
     *
     */
    byte getBlockOffset();

}