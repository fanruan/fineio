package com.fineio.io;


import com.fineio.cache.SyncStatus;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/19
 */
public interface Buffer {
    Level getLevel();

    SyncStatus getSyncStatus();

    long getAddress();

    long getAllocateSize();

    URI getUri();

    boolean isDirect();

    boolean isClose();

    void flip();

    void close();

    boolean resentAccess();

    void resetAccess();

    void unLoad();

    interface Listener {
        void remove(Buffer buffer);

        void update(Buffer buffer);
    }
}
