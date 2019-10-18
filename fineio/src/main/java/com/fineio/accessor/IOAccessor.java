package com.fineio.accessor;

import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IFile;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * IO访问器
 *
 * @author yee
 * @date 2019-05-22
 */
public interface IOAccessor {
    /**
     * 创建File
     *
     * @param connector
     * @param uri
     * @param model
     * @param <F>
     * @return
     * @see com.fineio.v3.file.impl.File
     * @see com.fineio.io.file.IOFile
     */
    <F extends IFile> F createFile(Connector connector, URI uri, Model<F> model);

    /**
     * ByteAppendFile put
     *
     * @param file
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.ByteAppendFile
     * @see com.fineio.io.file.AppendIOFile
     */
    <F extends IAppendFile<? extends ByteBuf>> void put(F file, byte value);

    /**
     * LongAppendFile put
     *
     * @param file
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.LongAppendFile
     */
    <F extends IAppendFile<? extends LongBuf>> void put(F file, long value);

    /**
     * IntAppendFile put
     *
     * @param file
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.IntAppendFile
     */
    <F extends IAppendFile<? extends IntBuf>> void put(F file, int value);

    /**
     * DoubleAppendFile put
     *
     * @param file
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.DoubleAppendFile
     */
    <F extends IAppendFile<? extends DoubleBuf>> void put(F file, double value);

    /**
     * 兼容接口，v3走write file，但会初始化最后一个buffer，顺序写相当于append
     * v2继续走append file
     *
     * @param file  write file
     * @param pos   pos
     * @param value value
     * @param <F>   write file
     */
    <F extends IAppendFile<? extends ByteBuf>> void put(F file, int pos, byte value);

    /**
     * 兼容接口，v3走write file，但会初始化最后一个buffer，顺序写相当于append
     * v2继续走append file
     *
     * @param file  write file
     * @param pos   pos
     * @param value value
     * @param <F>   write file
     */
    <F extends IAppendFile<? extends LongBuf>> void put(F file, int pos, long value);

    /**
     * 兼容接口，v3走write file，但会初始化最后一个buffer，顺序写相当于append
     * v2继续走append file
     *
     * @param file  write file
     * @param pos   pos
     * @param value value
     * @param <F>   write file
     */
    <F extends IAppendFile<? extends IntBuf>> void put(F file, int pos, int value);

    /**
     * 兼容接口，v3走write file，但会初始化最后一个buffer，顺序写相当于append
     * v2继续走append file
     *
     * @param file  write file
     * @param pos   pos
     * @param value value
     * @param <F>   write file
     */
    <F extends IAppendFile<? extends DoubleBuf>> void put(F file, int pos, double value);

    /**
     * IntWriteFile put
     *
     * @param file
     * @param pos
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.write.ByteWriteFile
     * @see com.fineio.io.file.WriteIOFile
     */
    <F extends IWriteFile<? extends ByteBuf>> void put(F file, int pos, byte value);

    /**
     * LongWriteFile put
     *
     * @param file
     * @param pos
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.write.LongWriteFile
     */
    <F extends IWriteFile<? extends LongBuf>> void put(F file, int pos, long value);

    /**
     * IntWriteFile put
     *
     * @param file
     * @param pos
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.write.IntWriteFile
     */
    <F extends IWriteFile<? extends IntBuf>> void put(F file, int pos, int value);

    /**
     * DoubleWriteFile put
     *
     * @param file
     * @param pos
     * @param value
     * @param <F>
     * @see com.fineio.v3.file.impl.write.DoubleWriteFile
     */
    <F extends IWriteFile<? extends DoubleBuf>> void put(F file, int pos, double value);

    /**
     * ByteReadFile get
     *
     * @param file
     * @param pos
     * @param <F>
     * @return
     * @see com.fineio.v3.file.impl.read.ByteReadFile
     * @see com.fineio.io.file.ReadIOFile
     */
    <F extends IReadFile<? extends ByteBuf>> byte getByte(F file, int pos);

    /**
     * LongReadFile get
     *
     * @param file
     * @param pos
     * @param <F>
     * @return
     * @see com.fineio.v3.file.impl.read.LongReadFile
     */
    <F extends IReadFile<? extends LongBuf>> long getLong(F file, int pos);

    /**
     * IntReadFile get
     *
     * @param file
     * @param pos
     * @param <F>
     * @return
     * @see com.fineio.v3.file.impl.read.IntReadFile
     */
    <F extends IReadFile<? extends IntBuf>> int getInt(F file, int pos);

    /**
     * DoubleReadFile get
     *
     * @param file
     * @param pos
     * @param <F>
     * @return
     * @see com.fineio.v3.file.impl.read.DoubleReadFile
     */
    <F extends IReadFile<? extends DoubleBuf>> double getDouble(F file, int pos);
}
