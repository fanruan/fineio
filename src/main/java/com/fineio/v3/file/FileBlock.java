package com.fineio.v3.file;

import com.fineio.v3.type.DataType;

import java.net.URI;

/**
 * @author yee
 */
public class FileBlock implements Block {
    /**
     *
     */
    private URI uri;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private DataType type;

    public FileBlock(URI uri, String name, DataType type) {
        this.uri = uri;
        this.name = name;
        this.type = type;
    }

    public FileBlock(URI uri, String name) {
        this.uri = uri;
        this.name = name;
    }

    /**
     *
     */
    public String getName() {
        return name;
    }

    /**
     *
     */
    public DataType getType() {
        return type;
    }

    /**
     *
     */
    @Override
    public URI getPath() {
        return uri.resolve(name);
    }

}