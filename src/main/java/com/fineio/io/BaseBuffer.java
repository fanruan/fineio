package com.fineio.io;

import com.fineio.cache.LEVEL;
import com.fineio.exception.StreamCloseException;
import com.fineio.io.base.DirectInputStream;
import com.fineio.io.base.StreamCloseChecker;

import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/30
 */
public abstract class BaseBuffer implements Buffer {
    protected AbstractBuffer buffer;


    public BaseBuffer(AbstractBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public long getAddress() {
        return buffer.getAddress();
    }

    @Override
    public int getMaxSize() {
        return buffer.getMaxSize();
    }

    @Override
    public int getAllocateSize() {
        return buffer.getAllocateSize();
    }

    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }

    @Override
    public int getOffset() {
        return buffer.getOffset();
    }


    @Override
    public int getStatus() {
        return buffer.getStatus();
    }

    protected abstract void loadContent();

    protected abstract void check(int position);


    @Override
    public int getByteSize() {
        return getLength() << getOffset();
    }

    @Override
    public int getLength() {
        loadContent();
        return buffer.getMaxSize();
    }

    protected InputStream getInputStream() {
        loadContent();
        if (getAddress() == 0) {
            throw new StreamCloseException();
        }
        DirectInputStream inputStream = new DirectInputStream(getAddress(), getByteSize(), new StreamCloseChecker(getStatus()) {
            public boolean check() {
                return BaseBuffer.this.getStatus() == getStatus();
            }
        });

        return inputStream;
    }

    @Override
    public void resetAccess() {
        buffer.resetAccess();
    }

    @Override
    public boolean recentAccess() {
        return buffer.recentAccess();
    }

    @Override
    public void closeWithOutSync() {
        buffer.closeWithOutSync();
    }

    @Override
    public LEVEL getLevel() {
        return buffer.getLevel();
    }

    @Override
    public URI getUri() {
        return buffer.getUri();
    }
}
