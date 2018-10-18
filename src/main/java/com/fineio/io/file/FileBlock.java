package com.fineio.io.file;

import java.net.URI;

public final class FileBlock {
    private static final String EMPTY = "";
    private URI uri;
    private String fileName;

    public FileBlock(final URI uri, final String fileName) {
        this.uri = uri;
        this.fileName = fileName;
    }

    public FileBlock(final URI uri) {
        this.uri = uri;
        this.fileName = "";
    }

    @Override
    public String toString() {
        return ((this.uri == null) ? "" : this.uri.toString()) + ((this.fileName == null) ? "" : this.fileName);
    }

    public URI getParentUri() {
        return this.uri;
    }

    public String getFileName() {
        return this.fileName;
    }

    public URI getBlockURI() {
        return this.uri.resolve(this.fileName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final FileBlock fileBlock = (FileBlock) o;
        if (this.uri != null) {
            if (this.uri.equals(fileBlock.uri)) {
                return (this.fileName != null) ? this.fileName.equals(fileBlock.fileName) : (fileBlock.fileName == null);
            }
        } else if (fileBlock.uri == null) {
            return (this.fileName != null) ? this.fileName.equals(fileBlock.fileName) : (fileBlock.fileName == null);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * ((this.uri != null) ? this.uri.hashCode() : 0) + ((this.fileName != null) ? this.fileName.hashCode() : 0);
    }
}
