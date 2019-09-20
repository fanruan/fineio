package com.fineio.v2.io.file.writer.task;


/**
 * @author yee
 * @date 2018/7/13
 */
public class DoneTaskKey implements TaskKey, Comparable<DoneTaskKey> {
    private long id;

    public DoneTaskKey() {
        id = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoneTaskKey that = (DoneTaskKey) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public KeyType getType() {
        return KeyType.DONE;
    }

    @Override
    public int compareTo(DoneTaskKey o) {
        return (int) (id - o.id);
    }
}
