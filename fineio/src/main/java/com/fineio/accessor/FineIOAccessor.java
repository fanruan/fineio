package com.fineio.accessor;

import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IFile;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.impl.v3.IOAccessorImpl;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2019-05-23
 */
public enum FineIOAccessor implements IOAccessor {
    //
    INSTANCE;

    private IOAccessor accessor;

    FineIOAccessor() {
        accessor = new IOAccessorImpl();
    }

    @Override
    public <F extends IFile> F createFile(Connector connector, URI uri, Model<F> model) {
        return accessor.createFile(connector, uri, model);
    }

    @Override
    public <F extends IWriteFile<? extends ByteBuf>> void put(F file, long pos, byte value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends LongBuf>> void put(F file, long pos, long value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends IntBuf>> void put(F file, long pos, int value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends DoubleBuf>> void put(F file, long pos, double value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IReadFile<? extends ByteBuf>> byte getByte(F file, long pos) {
        return accessor.getByte(file, pos);
    }

    @Override
    public <F extends IReadFile<? extends LongBuf>> long getLong(F file, long pos) {
        return accessor.getLong(file, pos);
    }

    @Override
    public <F extends IReadFile<? extends IntBuf>> int getInt(F file, long pos) {
        return accessor.getInt(file, pos);
    }

    @Override
    public <F extends IReadFile<? extends DoubleBuf>> double getDouble(F file, long pos) {
        return accessor.getDouble(file, pos);
    }
}
