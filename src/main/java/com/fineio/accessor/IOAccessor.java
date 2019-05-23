package com.fineio.accessor;

import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IFile;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.store.IConnector;

import java.net.URI;

/**
 * @author yee
 * @date 2019-05-22
 */
public interface IOAccessor {
    <F extends IFile> F createFile(IConnector connector, URI uri, Model<F> model);

    <F extends IAppendFile<? extends ByteBuf>> void put(F file, byte value);

    <F extends IAppendFile<? extends LongBuf>> void put(F file, long value);

    <F extends IAppendFile<? extends IntBuf>> void put(F file, int value);

    <F extends IAppendFile<? extends DoubleBuf>> void put(F file, double value);

    <F extends IWriteFile<? extends ByteBuf>> void put(F file, int pos, byte value);

    <F extends IWriteFile<? extends LongBuf>> void put(F file, int pos, long value);

    <F extends IWriteFile<? extends IntBuf>> void put(F file, int pos, int value);

    <F extends IWriteFile<? extends DoubleBuf>> void put(F file, int pos, double value);

    <F extends IReadFile<? extends ByteBuf>> byte getByte(F file, int pos);

    <F extends IReadFile<? extends LongBuf>> long getLong(F file, int pos);

    <F extends IReadFile<? extends IntBuf>> int getInt(F file, int pos);

    <F extends IReadFile<? extends DoubleBuf>> double getDouble(F file, int pos);
}
