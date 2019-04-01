package com.fineio.v3.memory;

/**
 * @author yee
 * @date 2019-01-22
 */
public enum Offset {
    //
    BYTE(0), CHAR(1), SHORT(1), INT(2), FLOAT(2), LONG(3), DOUBLE(3);

    public int offset;
    private int step;

    Offset(int offset) {
        this.offset = offset;
        this.step = 1 << offset;
    }

    public int getStep() {
        return step;
    }}
