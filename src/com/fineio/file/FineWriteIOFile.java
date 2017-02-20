package com.fineio.file;

import com.fineio.io.read.ReadBuffer;
import com.fineio.io.write.DoubleWriteBuffer;
import com.fineio.io.write.WriteBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/10.
 */
public final class FineWriteIOFile<T extends WriteBuffer> extends  FineIOFile<T> {

    FineWriteIOFile(Connector connector, URI uri, Class<T> clazz){
        super(connector, uri, clazz);
        this.block_size_offset = (byte) (connector.getBlockOffset() - getOffset());
    }



    /**
     * 创建File方法
     * @param connector 连接器
     * @param uri 子路径
     * @param clazz 子类型
     * @param <E> 继承ReadBuffer的子类型
     * @return
     */
    public static final <E extends WriteBuffer> FineWriteIOFile<E> createFineIO(Connector connector, URI uri, Class<E> clazz){
        return  new FineWriteIOFile<E>(connector, uri, clazz);
    }

//    public static void put(FineWriteIOFile<DoubleWriteBuffer> file, double d) {
//        file.getBuffer().
//    }


}
