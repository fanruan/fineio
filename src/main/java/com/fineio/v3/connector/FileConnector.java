package com.fineio.v3.connector;

import com.fineio.v3.file.FileKey;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author anchore
 * @date 2019/4/15
 */
public class FileConnector extends BaseConnector {
    @Override
    public void write(InputStream is, FileKey file) throws IOException {
        File f = new File(file.getDir());
        if (!f.exists()) {
            f.mkdirs();
        }
        try (InputStream input = is;
             OutputStream output = new BufferedOutputStream(new FileOutputStream(file.getPath()))) {
            for (int b; (b = input.read()) != -1; ) {
                output.write(b);
            }
        }
    }

    @Override
    public InputStream read(FileKey file) throws FileNotFoundException {
        return new FileInputStream(file.getPath());
    }

    @Override
    public boolean delete(FileKey file) {
        return new File(file.getPath()).delete();
    }

    @Override
    public boolean exists(FileKey file) {
        return new File(file.getPath()).exists();
    }

    @Override
    public void write(byte[] data, FileKey file) throws IOException {
        try (OutputStream output = new FileOutputStream(file.getPath())) {
            output.write(data);
        }
    }

    @Override
    public byte getBlockOffset() {
        return 22;
    }
}