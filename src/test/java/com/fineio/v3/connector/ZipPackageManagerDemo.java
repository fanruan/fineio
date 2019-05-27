package com.fineio.v3.connector;

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
        ZipPackageManager zipPackageManager = new ZipPackageManager(new FileConnector(), new PackageConnector() {
            @Override
            public void write(String path, InputStream is) throws IOException {
                try (FileOutputStream fileOutputStream = new FileOutputStream(path + ".zip"); InputStream input = is) {
                    IOUtils.copyBinaryTo(input, fileOutputStream);
                }
            }

            @Override
            public InputStream read(String path) throws IOException {
                return new FileInputStream(path + ".zip");
            }
        });
        zipPackageManager.packageDir("/Users/yee/Downloads/oss");
        zipPackageManager.unPackageDir("/Users/yee/Downloads/oss1", "/Users/yee/Downloads/oss");
    }
}