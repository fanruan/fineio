package com.fineio.file.writer;

import com.fineio.file.FileBlock;
import com.fineio.storage.Connector;

/**
 * Created by daniel on 2017/2/23.
 */
public class SyncKey {

    private Connector connector;

    private FileBlock block;

    public SyncKey(Connector connector, FileBlock block) {
        this.connector = connector;
        this.block = block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncKey syncKey = (SyncKey) o;

        if(connector != syncKey.connector){
            return false;
        }
        return block != null ? block.equals(syncKey.block) : syncKey.block == null;

    }

    @Override
    public int hashCode() {
        int result = connector != null ? connector.hashCode() : 0;
        result = 31 * result + (block != null ? block.hashCode() : 0);
        return result;
    }
}
