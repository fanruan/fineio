package com.fineio.v3.connector;

import com.fineio.v3.file.DirectoryBlock;

/**
 *
 */
public interface PackageConnector extends Connector {

    /**
     * @param dir
     */
    DirectoryBlock readDir(String dir);

    /**
     * @param dir
     */
    void writeDir(String dir);

    /**
     * @param dir
     */
    boolean delete(String dir);

    /**
     * @param dir
     */
    boolean exists(String dir);

}