package com.fineio.v2.io.file.writer.task;

import java.net.URI;

/**
 * @author yee
 * @date 2018/7/13
 */
public class FinishOneTaskKey implements TaskKey, Comparable<FinishOneTaskKey> {

    private URI uri;

    public FinishOneTaskKey(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinishOneTaskKey that = (FinishOneTaskKey) o;

        return uri != null ? uri.equals(that.uri) : that.uri == null;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    @Override
    public KeyType getType() {
        return KeyType.FINISHED;
    }

    @Override
    public int compareTo(FinishOneTaskKey o) {
        return hashCode() - o.hashCode();
    }
}
