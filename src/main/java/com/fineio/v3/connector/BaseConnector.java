package com.fineio.v3.connector;

import com.fineio.v3.file.FileKey;

import java.io.IOException;
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
    public void write(InputStream is, FileKey file) throws IOException {
        // TODO implement here
    }

    /**
     * @param file
     */
    @Override
    public InputStream read(FileKey file) throws IOException {
        // TODO implement here
        return null;
    }

    /**
     * @param file
     */
    @Override
    public boolean delete(FileKey file) {
        return false;
    }

    /**
     * @param file
     */
    @Override
    public boolean exists(FileKey file) {
        return false;
    }
}