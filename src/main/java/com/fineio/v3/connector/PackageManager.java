package com.fineio.v3.connector;

import java.io.IOException;

/**
 *
 */
public interface PackageManager {

    /**
     * @param dir
     */
    void packageDir(String dir) throws IOException;

    /**
     *
     * @param unPackDir
     * @param resourceName
     */
    void unPackageDir(String unPackDir, String resourceName) throws IOException;

    PackageConnector getPackageConnector();
}