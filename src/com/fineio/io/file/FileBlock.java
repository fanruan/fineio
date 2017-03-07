package com.fineio.io.file;


import java.io.File;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class FileBlock {

    private URI uri;

    private String fileName;

    FileBlock(URI uri, String fileName){
        this.uri = uri;
        this.fileName = fileName;
    }

    public String toString() {
        return (uri == null ? "":uri.toString()) + File.separator + (fileName == null ? "": fileName);
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
}
