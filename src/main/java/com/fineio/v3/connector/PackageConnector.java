package com.fineio.v3.connector;

import com.fineio.accessor.Block;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2019-05-27
 */
public interface PackageConnector {
    void write(String path, InputStream is) throws IOException;

    InputStream read(String path) throws IOException;

    /**
     * @param dir
     * @return
     * @since 3.0
     */
    Block list(String dir);

    String getSuffix();

    boolean delete(String dir);

    long size(String dir);

    boolean exist(String dir);
}
