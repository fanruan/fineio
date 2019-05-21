package com.fineio.v3.file;

import java.util.Objects;

/**
 * @author yee
 */
public class FileKey implements Block {
    private final String dir;

    private final String name;

    public FileKey(String dir, String name) {
        this.dir = dir;
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return dir + "/" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileKey fileKey = (FileKey) o;
        return Objects.equals(dir, fileKey.dir) &&
                Objects.equals(name, fileKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dir, name);
    }
}