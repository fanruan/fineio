package com.fineio.v3.utils;


import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.file.DirectoryBlock;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author yee
 * @date 2018/7/2
 */
public class ZipUtils {
    /**
     * 压缩成ZIP 方法1
     *
     * @param srcDir 压缩文件夹路径
     * @param out    压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(Block srcDir, Connector connector, OutputStream out)
            throws IOException {

        long start = System.currentTimeMillis();
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            compress(srcDir, connector, zos, srcDir.getName());
            long end = System.currentTimeMillis();
            FineIOLoggers.getLogger().info(String.format("Zip %s finished. Cost %d ms", srcDir, (end - start)));
        }
    }

    public static void unZip(String parent, Connector connector, InputStream inputStream) throws IOException {
        long start = System.currentTimeMillis();
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
                connector.write(new FileBlock(parent, entry.getName()), inputStream);
            }
            long end = System.currentTimeMillis();
            FineIOLoggers.getLogger().info(String.format("Unzip %s finished. Cost %d ms", parent, (end - start)));
        }
    }

    private static void compress(Block sourceFile, Connector connector, ZipOutputStream zos, String name) throws IOException {
        if (sourceFile instanceof FileBlock) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            try (InputStream in = connector.read((FileBlock) sourceFile)) {
                IOUtils.copyBinaryTo(in, zos);
                // Complete the entry
                zos.closeEntry();
            }
        } else {
            List<Block> files = ((DirectoryBlock) sourceFile).getFiles();
            if (files == null || files.isEmpty()) {
                // 空文件夹的处理
                zos.putNextEntry(new ZipEntry(name + "/"));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
            } else {
                for (Block file : files) {
                    // 判断是否需要保留原来的文件结构
                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    compress(file, connector, zos, name + "/" + file.getName());
                }
            }
        }
    }
}
