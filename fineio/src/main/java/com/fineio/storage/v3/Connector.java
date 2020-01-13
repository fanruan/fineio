package com.fineio.storage.v3;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;

import java.io.IOException;

/**
 * @author yee
 * @date 2019-08-05
 */
public interface Connector extends com.fineio.storage.Connector {
    /**
     * @param block
     * @return
     */
    boolean delete(Block block);

    /**
     * @param block
     * @return
     */
    boolean exists(Block block);

    /**
     * @param dir
     * @return
     * @since 3.0
     */
    Block list(String dir) throws IOException;

    @Override
    boolean exists(FileBlock block);

    @Override
    boolean delete(FileBlock block);


    long size(Block block);
}
