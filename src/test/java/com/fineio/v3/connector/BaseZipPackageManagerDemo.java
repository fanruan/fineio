package com.fineio.v3.connector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author yee
 * @date 2019-05-24
 */
public class BaseZipPackageManagerDemo {
    public static void main(String[] args) throws IOException {
        BaseZipPackageManager baseZipPackageManager = new BaseZipPackageManager(new FileConnector()) {
            @Override
            protected OutputStream output(String dir) throws FileNotFoundException {
                return new FileOutputStream(dir + ".cube");
            }

            @Override
            protected InputStream input(String dir) throws FileNotFoundException {
                return new FileInputStream(dir + ".cube");
            }
        };
        baseZipPackageManager.packageDir("/Users/yee/Downloads/oss");
        baseZipPackageManager.unPackageDir("/Users/yee/Downloads/oss1", "/Users/yee/Downloads/oss");
    }
}