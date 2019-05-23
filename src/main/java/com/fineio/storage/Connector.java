package com.fineio.storage;

import com.fineio.accessor.store.IConnector;
import com.fineio.io.file.FileBlock;

import java.io.IOException;
import java.net.URI;

/**
 * Created by daniel on 2017/2/9.
 * 存储对接接口
 */
public interface Connector extends IConnector<FileBlock> {


    /**
     * 复制文件
     * @param srcBlock
     * @param destBlock
     * @return
     */
    boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException;

    /**
     * 删除空白父亲块
     * @param block
     * @return
     */
    URI deleteParent(FileBlock block);
}
