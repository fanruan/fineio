package com.fineio.io.pool;

import com.fineio.io.read.ReadBuffer;
import com.fineio.observer.CallBack;
import com.fineio.observer.FineIOObservable;

import java.net.URI;

/**
 * @author yee
 * @date 2018/4/16
 */
public class BufferObservable extends FineIOObservable {
    private volatile ReadBuffer buffer;
    private int block = -1;
    private ObservableState state = ObservableState.READY;
    private URI uri;

    private BufferObservable(URI uri, CallBack callBack) {
        super(callBack);
        this.uri = uri;
    }

    public static BufferObservable newInstance(URI uri) {
        BufferObservable observable = new BufferObservable(uri, new BufferCallBack());
        observable.setBlock(uri);
        return observable;
    }

    public void setBuffer(ReadBuffer buffer, boolean updateCallBack) {
        this.buffer = buffer;
        if (-1 == block) {
            setBlock(buffer.getUri());
        }
        if (updateCallBack) {
            ((BufferCallBack) callBack).setNewBuffer(buffer);
        } else {
            ((BufferCallBack) callBack).setBuffer(buffer);
        }
    }

    public boolean isBufferValid() {
        synchronized (this) {
            if (null != buffer && !buffer.isClose()) {
                return buffer.isLoad();
            }
            return false;
        }
    }

    private int handleBlock(URI uri) {
        String path = uri.getPath();
        path.replace("\\", "/");
        int index = path.lastIndexOf("/");
        String name = null;
        if (-1 == index) {
            name = path;
        } else {
            name = path.substring(index + 1);
        }
        try {
            return Integer.parseInt(name);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getBlock() {
        return block;
    }

    private void setBlock(URI uri) {
        this.block = handleBlock(uri);
    }

    public void bufferChanged() {
        state = ObservableState.CHANGE;
        notifyAllChildren();
    }

    public void bufferCleaned() {
        state = ObservableState.CLEAR;
        stopService();
    }

    public ObservableState getState() {
        return state;
    }

    public ReadBuffer getBuffer() {
        return buffer;
    }

    public URI getUri() {
        return uri;
    }
}
