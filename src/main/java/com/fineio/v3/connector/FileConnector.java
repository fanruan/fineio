package com.fineio.v3.connector;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.v3.file.DirectoryBlock;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2019/4/15
 */
public class FileConnector extends BaseConnector {
    public FileConnector(byte blockOffset) {
        super(blockOffset);
    }

    public FileConnector() {
    }

    @Override
    public void write(FileBlock file, InputStream is) throws IOException {
        File parent = new File(file.getPath()).getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(file.getPath()))) {
            for (int b; (b = is.read()) != -1; ) {
                output.write(b);
            }
        }
    }

    @Override
    public InputStream read(FileBlock file) throws FileNotFoundException {
        return new FileInputStream(file.getPath());
    }

    @Override
    public boolean delete(FileBlock file) {
        return delete((Block) file);
    }

    @Override
    public boolean exists(FileBlock file) {
        return exists((Block) file);
    }

    @Override
    public boolean delete(Block block) {
        return new File(block.getPath()).delete();
    }

    @Override
    public boolean exists(Block block) {
        return new File(block.getPath()).exists();
    }

    @Override
    public Block list(String file) {
        File f = new File(file);
        if (f.isDirectory()) {
            List<Block> blocks = new ArrayList<>();
            File[] list = f.listFiles((dir, name) -> !(".".equals(name) || "..".equals(name)));
            if (null != list) {
                for (File s : list) {
                    blocks.add(list(s.getAbsolutePath()));
                }
            }
            return new DirectoryBlock(file, blocks);
        } else {
            return new FileBlock(f.getParent(), f.getName());
        }
    }

    @Override
    public boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException {
        return false;
    }

    @Override
    public URI deleteParent(FileBlock block) {
        return null;
    }

    @Override
    public void write(FileBlock file, byte[] data) throws IOException {
        try (OutputStream output = new FileOutputStream(file.getPath())) {
            output.write(data);
        }
    }
}