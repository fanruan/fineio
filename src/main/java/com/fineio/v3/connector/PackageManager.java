package com.fineio.v3.connector;

/**
 *
 */
public interface PackageManager {

    /**
     * @param dir
     */
    void packageDir(String dir);

    /**
     * @param dir
     */
    void unPackageDir(String dir);

}