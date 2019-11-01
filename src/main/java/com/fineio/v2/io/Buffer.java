package com.fineio.v2.io;


import com.fineio.io.Level;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.v2.cache.SyncStatus;

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

    void close();

    void clearAfterClose();

    boolean resentAccess();

    void resetAccess();

    void unLoad();

    int getLength();

    MemoryObject getFreeObject();

    <B extends Buffer> B asRead();

    <B extends Buffer> B asWrite();

    <B extends Buffer> B asAppend();

    interface Listener {
        void remove(Buffer buffer, BaseDeAllocator.Builder builder);

        void update(Buffer buffer);
    }
}

