package com.fineio.v3.connector;

import com.fineio.accessor.Block;
import com.fineio.v3.utils.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2019-05-24
 */
public class ZipPackageManagerDemo {
    public static void main(String[] args) throws IOException {
        FileConnector connector = new FileConnector();
        ZipPackageManager zipPackageManager = new ZipPackageManager(connector, new PackageConnector() {
            @Override
            public void write(String path, InputStream is) throws IOException {
                try (FileOutputStream fileOutputStream = new FileOutputStream(path + getSuffix()); InputStream input = is) {
                    IOUtils.copyBinaryTo(input, fileOutputStream);
                }
            }

            @Override
            public InputStream read(String path) throws IOException {
                return new FileInputStream(path + getSuffix());
            }

            @Override
            public Block list(String dir) {
                return connector.list(dir);
            }

            @Override
            public String getSuffix() {
                return ".zip";
            }

            @Override
            public boolean delete(String dir) {
                return false;
            }

            @Override
            public long size(String dir) {
                return 0;
            }

            @Override
            public boolean exist(String dir) {
                return false;
            }
        });
        zipPackageManager.packageDir("/Users/yee/test1", "/Users/yee/Downloads/2019");
        zipPackageManager.unPackageDir("/Users/yee/Downloads/oss1", "/Users/yee/Downloads/oss");
    }
}