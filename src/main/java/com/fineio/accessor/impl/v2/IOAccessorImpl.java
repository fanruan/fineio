package com.fineio.accessor.impl.v2;

import com.fineio.accessor.FileMode;
import com.fineio.accessor.IOAccessor;
import com.fineio.accessor.Model;
import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IFile;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.io.ByteBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.file.AppendIOFile;
import com.fineio.io.file.FileModel;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.io.file.WriteIOFile;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2019-05-22
 */
public class IOAccessorImpl implements IOAccessor {

    @Override
    public <F extends IFile> F createFile(Connector connector, URI uri, Model<F> model) {
        return (F) create(connector, uri, model.getFileMode(), FileModel.valueOf(model.getDataType().name()));

    }

    @Override
    public <F extends IAppendFile<? extends ByteBuf>> void put(F file, byte value) {
        IOFile.put((IOFile<ByteBuffer>) file, value);
    }

    @Override
    public <F extends IAppendFile<? extends LongBuf>> void put(F file, long value) {
        IOFile.put((IOFile<LongBuffer>) file, value);
    }

    @Override
    public <F extends IAppendFile<? extends IntBuf>> void put(F file, int value) {
        IOFile.put((IOFile<IntBuffer>) file, value);
    }

    @Override
    public <F extends IAppendFile<? extends DoubleBuf>> void put(F file, double value) {
        IOFile.put((IOFile<DoubleBuffer>) file, value);
    }

    @Override
    public <F extends IWriteFile<? extends ByteBuf>> void put(F file, int pos, byte value) {
        IOFile.put((IOFile<ByteBuffer>) file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends LongBuf>> void put(F file, int pos, long value) {
        IOFile.put((IOFile<LongBuffer>) file, pos, value);

    }

    @Override
    public <F extends IWriteFile<? extends IntBuf>> void put(F file, int pos, int value) {
        IOFile.put((IOFile<IntBuffer>) file, pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends DoubleBuf>> void put(F file, int pos, double value) {
        IOFile.put((IOFile<DoubleBuffer>) file, pos, value);
    }

    @Override
    public <F extends IReadFile<? extends ByteBuf>> byte getByte(F file, int pos) {
        return IOFile.getByte((IOFile<ByteBuffer>) file, pos);
    }

    @Override
    public <F extends IReadFile<? extends LongBuf>> long getLong(F file, int pos) {
        return IOFile.getLong((IOFile<LongBuffer>) file, pos);
    }

    @Override
    public <F extends IReadFile<? extends IntBuf>> int getInt(F file, int pos) {
        return IOFile.getInt((IOFile<IntBuffer>) file, pos);
    }

    @Override
    public <F extends IReadFile<? extends DoubleBuf>> double getDouble(F file, int pos) {
        return IOFile.getDouble((IOFile<DoubleBuffer>) file, pos);
    }

    private IFile create(Connector connector, URI uri, FileMode mode, FileModel fileModel) {
        switch (mode) {
            case WRITE:
                return WriteIOFile.createFineIO(connector, uri, fileModel, true);
            case READ:
                return ReadIOFile.createFineIO(connector, uri, fileModel);
            case APPEND:
                return AppendIOFile.createFineIO(connector, uri, fileModel, true);
            default:
                return null;
        }
    }
}
