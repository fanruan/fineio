package com.fineio.accessor.impl.v3;

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
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.file.impl.ByteAppendFile;
import com.fineio.v3.file.impl.DoubleAppendFile;
import com.fineio.v3.file.impl.IntAppendFile;
import com.fineio.v3.file.impl.LongAppendFile;
import com.fineio.v3.file.impl.read.ByteReadFile;
import com.fineio.v3.file.impl.read.DoubleReadFile;
import com.fineio.v3.file.impl.read.IntReadFile;
import com.fineio.v3.file.impl.read.LongReadFile;
import com.fineio.v3.file.impl.write.ByteWriteFile;
import com.fineio.v3.file.impl.write.DoubleWriteFile;
import com.fineio.v3.file.impl.write.IntWriteFile;
import com.fineio.v3.file.impl.write.LongWriteFile;

import java.net.URI;

/**
 * @author yee
 * @date 2019-05-22
 */
public class IOAccessorImpl implements IOAccessor {
    @Override
    public <F extends IFile> F createFile(Connector connector, URI uri, Model<F> model) {
        switch (model.getDataType()) {
            case INT:
                return (F) createInt(connector, uri, model.getFileMode());
            case BYTE:
                return (F) createByte(connector, uri, model.getFileMode());
            case LONG:
                return (F) createLong(connector, uri, model.getFileMode());
            case DOUBLE:
                return (F) createDouble(connector, uri, model.getFileMode());
            default:
        }
        return null;
    }

    @Override
    public <F extends IAppendFile<? extends ByteBuf>> void put(F file, byte value) {
        ((ByteAppendFile) file).putByte(value);
    }

    @Override
    public <F extends IAppendFile<? extends LongBuf>> void put(F file, long value) {
        ((LongAppendFile) file).putLong(value);
    }

    @Override
    public <F extends IAppendFile<? extends IntBuf>> void put(F file, int value) {
        ((IntAppendFile) file).putInt(value);
    }

    @Override
    public <F extends IAppendFile<? extends DoubleBuf>> void put(F file, double value) {
        ((DoubleAppendFile) file).putDouble(value);
    }

    @Override
    public <F extends IWriteFile<? extends ByteBuf>> void put(F file, int pos, byte value) {
        ((ByteWriteFile) file).putByte(pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends LongBuf>> void put(F file, int pos, long value) {
        ((LongWriteFile) file).putLong(pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends IntBuf>> void put(F file, int pos, int value) {
        ((IntWriteFile) file).putInt(pos, value);
    }

    @Override
    public <F extends IWriteFile<? extends DoubleBuf>> void put(F file, int pos, double value) {
        ((DoubleWriteFile) file).putDouble(pos, value);
    }

    @Override
    public <F extends IReadFile<? extends ByteBuf>> byte getByte(F file, int pos) {
        return ((ByteReadFile) file).getByte(pos);
    }

    @Override
    public <F extends IReadFile<? extends LongBuf>> long getLong(F file, int pos) {
        return ((LongReadFile) file).getLong(pos);
    }

    @Override
    public <F extends IReadFile<? extends IntBuf>> int getInt(F file, int pos) {
        return ((IntReadFile) file).getInt(pos);
    }

    @Override
    public <F extends IReadFile<? extends DoubleBuf>> double getDouble(F file, int pos) {
        return ((DoubleReadFile) file).getDouble(pos);
    }


    private IFile createByte(Connector connector, URI uri, FileMode mode) {
        FileBlock fileBlock = new FileBlock(uri.getPath(), "");
        switch (mode) {
            case APPEND:
                return new ByteAppendFile(new ByteWriteFile(fileBlock, connector, false));
            case READ:
                return new ByteReadFile(fileBlock, connector);
            case WRITE:
                return new ByteWriteFile(fileBlock, connector, false);
            default:
                return null;
        }
    }

    private IFile createInt(Connector connector, URI uri, FileMode mode) {
        FileBlock fileBlock = new FileBlock(uri.getPath(), "");
        switch (mode) {
            case APPEND:
                return new IntAppendFile(new IntWriteFile(fileBlock, connector, false));
            case READ:
                return new IntReadFile(fileBlock, connector);
            case WRITE:
                return new IntWriteFile(fileBlock, connector, false);
            default:
                return null;
        }
    }

    private IFile createLong(Connector connector, URI uri, FileMode mode) {
        FileBlock fileBlock = new FileBlock(uri.getPath(), "");
        switch (mode) {
            case APPEND:
                return new LongAppendFile(new LongWriteFile(fileBlock, connector, false));
            case READ:
                return new LongReadFile(fileBlock, connector);
            case WRITE:
                return new LongWriteFile(fileBlock, connector, false);
            default:
                return null;
        }
    }

    private IFile createDouble(Connector connector, URI uri, FileMode mode) {
        FileBlock fileBlock = new FileBlock(uri.getPath(), "");
        switch (mode) {
            case APPEND:
                return new DoubleAppendFile(new DoubleWriteFile(fileBlock, connector, false));
            case READ:
                return new DoubleReadFile(fileBlock, connector);
            case WRITE:
                return new DoubleWriteFile(fileBlock, connector, false);
            default:
                return null;
        }
    }
}