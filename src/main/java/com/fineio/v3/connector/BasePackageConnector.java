package com.fineio.v3.connector;

import com.fineio.v3.file.DirectoryBlock;

/**
 *
 */
public abstract class BasePackageConnector extends BaseConnector implements PackageConnector {

    /**
     *
     */
    protected PackageManager packageManager;
    /**
     *
     */
    protected String unPackageRootPath;

    /**
     * Default constructor
     *
     * @param blockOffset
     */
    public BasePackageConnector(int blockOffset) {
        super(blockOffset);
    }

    /**
     *
     */
    public void getUnPackageRootPath() {
        // TODO implement here
    }

    /**
     * @param dir
     */
    @Override
    public DirectoryBlock readDir(String dir) {
        // TODO implement here
        return null;
    }

    /**
     * @param dir
     */
    @Override
    public void writeDir(String dir) {
        // TODO implement here
    }

    /**
     * @param dir
     */
    @Override
    public boolean delete(String dir) {
        // TODO implement here
        return false;
    }

    /**
     * @param dir
     */
    @Override
    public boolean exists(String dir) {
        // TODO implement here
        return false;
    }

}