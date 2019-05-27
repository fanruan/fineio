package com.fineio.v3.connector;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2019-05-27
 */
public interface PackageConnector {
    void write(String path, InputStream is) throws IOException;

    InputStream read(String path) throws IOException;
}
