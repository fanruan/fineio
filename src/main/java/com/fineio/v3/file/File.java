package com.fineio.v3.file;

import com.fineio.v3.buffer.Buffer;
import com.fineio.v3.connector.Connector;

import java.net.URI;

/**
 *
 */
public class File {

    /**
     *
     */
    protected URI uri;
    /**
     *
     */
    protected Connector connector;
    /**
     *
     */
    protected Buffer[] buffers;

    /**
     * Default constructor
     */
    public File() {
    }

    /**
     *
     */
    public void close() {
        // TODO implement here
    }

    /**
     * @param uri
     * @param offset
     */
    protected Buffer createBuffer(URI uri, int offset) {
        return null;
    }

    /**
     *
     */
    public void delete() {
        // TODO implement here
    }

}