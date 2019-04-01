package com.fineio.v3.connector;

import com.fineio.v3.file.FileBlock;

import java.io.InputStream;

/**
 * @author yee
 */
public abstract class BaseConnector implements Connector {

    /**
     * Default constructor
     */
    public BaseConnector() {
    }

    /**
     * @param is
     * @param file
     */
    @Override
    public void write(InputStream is, FileBlock file) {
        // TODO implement here
    }

    /**
     * @param file
     */
    @Override
    public void read(FileBlock file) {
        // TODO implement here
    }

    /**
     * @param file
     */
    @Override
    public boolean delete(FileBlock file) {
        return false;
    }

    /**
     * @param file
     */
    @Override
    public boolean exists(FileBlock file) {
        return false;
    }
}