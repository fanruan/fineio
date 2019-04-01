package com.fineio.v3.file;

import java.net.URI;
import java.util.List;

/**
 *
 */
public class DirectoryBlock implements Block {

    /**
     *
     */
    private String dirPath;
    /**
     *
     */
    private List<Block> files;

    /**
     * Default constructor
     */
    public DirectoryBlock() {
    }

    /**
     *
     */
    public void getFiles() {
        // TODO implement here
    }

    /**
     *
     */
    @Override
    public URI getPath() {
        return URI.create(dirPath);
    }

}