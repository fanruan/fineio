package com.fineio.io.file;


import com.fineio.accessor.Block;

import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class FileBlock implements Block {
    private final static String EMPTY = "";

    private URI uri;

    private String fileName;

    /**
     * 空就代表文件夹把
     * @param uri
     * @param fileName
     */
    public FileBlock(URI uri, String fileName){
        this.uri = uri;
        this.fileName = fileName;
    }

    /**
     * 空就代表文件夹把
     * @param uri
     */
    public FileBlock(URI uri){
        this.uri = uri;
        this.fileName = EMPTY;
    }

    public String toString() {
        return (uri == null ? "":uri.toString())+ (fileName == null ? "": fileName);
    }

    /**
     * parent的URI
     * @return
     */
    public URI getParentUri() {
        return  uri;
    }

    @Override
    public String getName() {
        return fileName;
    }

    /**
     * 实际URI
     * @return
     */
    public URI getBlockURI(){
        if (EMPTY.equals(fileName)) {
            return uri;
        }
        return uri.resolve(fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileBlock block = (FileBlock) o;

        if (uri != null ? !uri.equals(block.uri) : block.uri != null) return false;
        return fileName != null ? fileName.equals(block.fileName) : block.fileName == null;

    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }

    @Override
    public String getPath() {
        return getBlockURI().getPath();
    }

}
