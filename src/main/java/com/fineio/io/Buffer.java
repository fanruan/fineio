package com.fineio.io;

import com.fineio.cache.LEVEL;

public interface Buffer {
    boolean full();

    void write();

    void force();

    void closeWithOutSync();

    void clear();

    LEVEL getLevel();
    
    boolean recentAccess();

    void resetAccess();

    int getAllocateSize();
    
    int getByteSize();

    int getLength();
}
