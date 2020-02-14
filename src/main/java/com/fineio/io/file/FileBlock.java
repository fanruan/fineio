package com.fineio.io.file;


import com.fineio.accessor.Block;

import java.net.URI;

/**
 * @author daniel
 * @date 2017/2/9
 */
public class FileBlock implements Block {
    private final static String EMPTY = "";

    private String dir;

    private String fileName;

    /**
     * 空就代表文件夹把
     *
     * @param dir
     * @param fileName
     */
    public FileBlock(String dir, String fileName) {
        this.dir = dir;
        this.fileName = fileName;
    }

    /**
     * 空就代表文件夹把
     *
     * @param dir
     */
    public FileBlock(String dir) {
        this.dir = dir;
        this.fileName = EMPTY;
    }

    @Override
    public String toString() {
        return (dir == null ? "" : dir) + "/" + (fileName == null ? "" : fileName);
    }

    /**
     * parent的URI
     *
     * @return
     */
    public String getDir() {
        return dir;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public Block clone() {
        return new FileBlock(dir, fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FileBlock fileBlock = (FileBlock) o;

        if (dir != null ? !dir.equals(fileBlock.dir) : fileBlock.dir != null) {
            return false;
        }
        return fileName != null ? fileName.equals(fileBlock.fileName) : fileBlock.fileName == null;

    }

    @Override
    public int hashCode() {
        int result = dir != null ? dir.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }

    @Override
    public String getPath() {
        return toString();
    }

    public URI getParentUri() {
        return URI.create(dir);
    }

    public URI getBlockURI() {
        return URI.create(getPath());
    }

    public String getFileName() {
        return fileName;
    }
}
