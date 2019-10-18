package com.fineio.accessor;

import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IFile;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.impl.v2.IOAccessorImpl;
import com.fineio.java.JavaVersion;
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
        int version = 2;
        if (JavaVersion.isOverJava8()) {
            version = 3;
        }
        try {
            accessor = (IOAccessor) Class.forName("com.fineio.accessor.impl.v" + version + ".IOAccessorImpl").newInstance();
        } catch (Exception e) {
            accessor = new IOAccessorImpl();
        }
    }

    @Override
    public <F extends IFile> F createFile(Connector connector, URI uri, Model<F> model) {
        return accessor.createFile(connector, uri, model);
    }

    @Override
    public <F extends IAppendFile<? extends ByteBuf>> void put(F file, byte value) {
        accessor.put(file, value);
    }

    @Override
    public <F extends IAppendFile<? extends LongBuf>> void put(F file, long value) {
        accessor.put(file, value);
    }

    @Override
    public <F extends IAppendFile<? extends IntBuf>> void put(F file, int value) {
        accessor.put(file, value);
    }

    @Override
    public <F extends IAppendFile<? extends DoubleBuf>> void put(F file, double value) {
        accessor.put(file, value);
    }

    @Override
    public <F extends IAppendFile<? extends ByteBuf>> void put(F file, int pos, byte value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IAppendFile<? extends LongBuf>> void put(F file, int pos, long value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IAppendFile<? extends IntBuf>> void put(F file, int pos, int value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IAppendFile<? extends DoubleBuf>> void put(F file, int pos, double value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends ByteBuf>> void put(F file, int pos, byte value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends LongBuf>> void put(F file, int pos, long value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends IntBuf>> void put(F file, int pos, int value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends DoubleBuf>> void put(F file, int pos, double value) {
        accessor.put(file, pos, value);
    }

    @Override
    public <F extends IReadFile<? extends ByteBuf>> byte getByte(F file, int pos) {
        return accessor.getByte(file, pos);
    }

    @Override
    public <F extends IReadFile<? extends LongBuf>> long getLong(F file, int pos) {
        return accessor.getLong(file, pos);
    }

    @Override
    public <F extends IReadFile<? extends IntBuf>> int getInt(F file, int pos) {
        return accessor.getInt(file, pos);
    }

    @Override
    public <F extends IReadFile<? extends DoubleBuf>> double getDouble(F file, int pos) {
        return accessor.getDouble(file, pos);
    }
}
