package com.fineio.io.pool;

import com.fineio.io.read.ReadBuffer;
import com.fineio.observer.CallBack;

/**
 * @author yee
 * @date 2018/4/16
 */
public  class BufferCallBack implements CallBack {

    private volatile ReadBuffer buffer;
    private volatile ReadBuffer newBuffer;

    public BufferCallBack(ReadBuffer buffer) {
        this.buffer = buffer;
    }

    public BufferCallBack() {
    }

    public void setBuffer(ReadBuffer buffer) {
        this.buffer = buffer;
    }

    public void setNewBuffer(ReadBuffer newBuffer) {
        this.newBuffer = newBuffer;
    }

    public void call() {
        if (null != this.buffer && !this.buffer.isClose()) {
            this.buffer.closeWithOutSync();
            this.buffer = null;
            if (null != newBuffer) {
                buffer = newBuffer;
            }
        }
    }
}