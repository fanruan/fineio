package com.fineio.v21.file.read;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fineio.storage.Connector;

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

    public FileConnector() {
    }

    public static Connector newInstance(String path) {
        return new FileConnector();
    }

    private File toFile(FileBlock block, boolean mkdirs) {
        File dir = new File("D:/fineWork/fineIO 3.0/fineio/fineio/testInt1" + block.getParentUri().getPath());
        if (mkdirs) {
            dir.mkdirs();
        }
        return new File(dir, block.getFileName());
    }

    @Override
    public InputStream read(FileBlock block) throws IOException {
        File f = toFile(block, false);
        return new FileInputStream(f);
    }

    @Override
    public void write(FileBlock block, InputStream is) throws IOException {
        File f = toFile(block, true);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            byte[] bytes = new byte[1024];
            for (int len; (len = is.read(bytes)) != -1; ) {
                fos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fos.close();
            is.close();
        }
    }

    @Override
    public boolean delete(FileBlock block) {
        File f = toFile(block, false);
        return f.delete();
    }

    @Override
    public boolean exists(FileBlock block) {
        File f = toFile(block, false);
        return f.exists() && f.length() > 0;
    }


    @Override
    public boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException {
        if (!exists(srcBlock) || exists(destBlock)) {
            return false;
        }
        write(destBlock, read(srcBlock));
        return true;
    }
}