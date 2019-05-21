package com.fineio.v3.connector;

import com.fineio.v3.file.Block;
import com.fineio.v3.utils.ZipUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author yee
 * @date 2019-05-21
 */
public abstract class BaseZipPackageManager implements PackageManager {

    private Connector connector;

    public BaseZipPackageManager(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void packageDir(String dir) throws IOException {
        Block block = connector.list(dir);
        ZipUtils.toZip(block, connector, output(dir));
    }

    @Override
    public void unPackageDir(String unPackDir, String resourceName) throws IOException {
        ZipUtils.unZip(unPackDir, connector, input(resourceName));
    }

    /**
     * 获取输出流
     *
     * @param dir
     * @return
     */
    protected abstract OutputStream output(String dir);

    /**
     * 获取输入流
     *
     * @param dir
     * @return
     */
    protected abstract InputStream input(String dir);
}
