package com.fineio.storage;

import com.fineio.file.FileBlock;
import com.fineio.third.zip4j.core.ZipFile;
import com.fineio.third.zip4j.exception.ZipException;


/**
 * Created by daniel on 2017/2/9.
 */
public class ZipConnector implements Connector {

    private ZipFile file;

    public ZipConnector(String zipPath) {
        try {
            file = new ZipFile(zipPath);
        } catch (ZipException e) {
        }
    }

    @Override
    public byte[] read(FileBlock file) {
        return new byte[0];
    }

    @Override
    public void write(FileBlock file, byte[] v) {

    }

    @Override
    public long getBlockSize() {
        return 1 << 26;
    }
}
