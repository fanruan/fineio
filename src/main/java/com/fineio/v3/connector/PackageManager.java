package com.fineio.v3.connector;

import java.io.IOException;

/**
 *
 */
public interface PackageManager {

    /**
     * @param resourcePath
     */
    void packageDir(String targetPath, String resourcePath) throws IOException;

    /**
     * @param unPackDir
     * @param resourceName
     */
    void unPackageDir(String unPackDir, String resourceName) throws IOException;

    PackageConnector getPackageConnector();
}