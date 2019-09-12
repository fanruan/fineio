package com.fineio.v21.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2019/9/12
 */
public class FileConnector extends AbstractConnector {
    @Override
    public InputStream read(FileBlock file) throws IOException {
        return new FileInputStream(new File(file.getBlockURI().getPath()));
    }

    @Override
    public void write(FileBlock file, InputStream inputStream) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(new File(file.getBlockURI().getPath()));
        IOUtils.copy(inputStream, fileOutputStream);
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(fileOutputStream);
    }

    @Override
    public boolean delete(FileBlock block) {
        return false;
    }

    @Override
    public boolean exists(FileBlock block) {
        return true;
    }

    @Override
    public boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException {
        return false;
    }
}
