package com.fineio.io.mem;

import java.net.URI;
import java.util.Observable;

/**
 * @author yee
 * @date 2017/12/04
 */
public class MemBean extends Observable {
    private long address;
    private int maxSize;
    private boolean load;
    private boolean close;
    private final URI uri;

    public MemBean(URI uri) {
        this.uri = uri;
    }



    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemBean bean = (MemBean) o;

        return uri.equals(bean.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    public void reset() {
        address = 0;
        load = false;
        maxSize = 0;
        close = true;
        setChanged();
        notifyObservers();
    }
}
