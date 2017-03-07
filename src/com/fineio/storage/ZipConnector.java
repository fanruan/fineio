package com.fineio.storage;

import com.fineio.io.file.FileBlock;
import com.fineio.third.zip4j.core.ZipFile;
import com.fineio.third.zip4j.exception.ZipException;

import java.io.InputStream;


/**
 * Created by daniel on 2017/2/9.
 */
public class ZipConnector extends AbstractConnector {

    private ZipFile file;

    public ZipConnector(String zipPath) {
        try {
            file = new ZipFile(zipPath);
        } catch (ZipException e) {
        }
    }

    @Override
    public InputStream read(FileBlock file) {
        return null;
    }

    @Override
    public void write(FileBlock file, InputStream inputStream) {

    }

    @Override
    public boolean delete(FileBlock block) {
        return false;
    }

    @Override
    public byte getBlockOffset() {
        return 26;
    }
}
