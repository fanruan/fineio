package com.fineio.file;

import com.fineio.base.Bits;
import com.fineio.exception.BlockNotFoundException;
import com.fineio.exception.BufferIndexOutOfBoundsException;
import com.fineio.io.read.*;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 */
public final class FineReadIOFile<T extends ReadBuffer> extends FineIOFile<T> {

    private FineReadIOFile(Connector connector, URI uri,  Class<T> clazz){
        super(connector, uri, clazz);
        readHeader(getOffset());

    }

    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends ReadBuffer> FineReadIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new FineReadIOFile<E>(connector, uri, clazz);
    }

    public final static long getLong(FineIOFile<LongReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static int getInt(FineIOFile<IntReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static int getChar(FineIOFile<CharReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static double getDouble(FineIOFile<DoubleReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static float getFloat(FineIOFile<FloatReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static byte getByte(FineIOFile<ByteReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }

    public final static short getShort(FineIOFile<ShortReadBuffer> file, long p) {
        return file.getBuffer(file.gi(p)).get(file.gp(p));
    }



    private void readHeader(byte offset) {
        InputStream is  = this.connector.read(new FileBlock(uri, FileConstants.HEAD));
        if(is == null){
            throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
        }
        try {
            byte[] header = new byte[9];
            is.read(header);
            int p = 0;
            blocks = Bits.getInt(header, p);
            buffers = (T[])new ReadBuffer[blocks];
            //先空个long的位置
            p += MemoryConstants.STEP_LONG;
            block_size_offset = (byte) (header[p] - offset);
            single_block_len = (1L << block_size_offset) - 1;
        } catch (IOException e) {
            throw new BlockNotFoundException("block:" + uri.toString() +" not found!");
        }
    }

    public String getPath(){
        return uri.getPath();
    }

}
