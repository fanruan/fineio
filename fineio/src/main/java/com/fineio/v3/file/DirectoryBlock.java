package com.fineio.v3.file;

import com.fineio.accessor.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 */
public class DirectoryBlock implements Block {

    /**
     *
     */
    private String dirPath;
    /**
     *
     */
    private List<Block> files;

    /**
     * Default constructor
     */
    public DirectoryBlock(String dirPath, List<Block> blocks) {
        this.dirPath = dirPath;
        this.files = blocks;
    }

    /**
     *
     */
    public List<Block> getFiles() {
        return this.files;
    }

    /**
     *
     */
    @Override
    public String getPath() {
        return dirPath;
    }

    @Override
    public String getName() {
        return new File(dirPath).getName();
    }

    @Override
    public String toString() {
        return "DirectoryBlock{" +
                "dirPath='" + dirPath + '\'' +
                '}';
    }

    @Override
    public Block clone() {
        List<Block> target = new ArrayList<>();
        for (Block file : files) {
            target.add(file.clone());
        }
        return new DirectoryBlock(dirPath, target);
    }
}