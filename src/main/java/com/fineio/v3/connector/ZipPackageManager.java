package com.fineio.v3.connector;

import com.fineio.accessor.Block;
import com.fineio.storage.Connector;
import com.fineio.v3.utils.ZipUtils;

import java.io.IOException;

/**
 * @author yee
 * @date 2019-05-21
 */
public class ZipPackageManager implements PackageManager {

    private Connector connector;
    private PackageConnector packageConnector;

    public ZipPackageManager(Connector connector, PackageConnector packageConnector) {
        this.connector = connector;
        this.packageConnector = packageConnector;
    }

    @Override
    public void packageDir(String dir) throws IOException {
        Block block = connector.list(dir);
        ZipUtils.toZip(block, connector, packageConnector);
    }

    @Override
    public void unPackageDir(String unPackDir, String resourceName) throws IOException {
        ZipUtils.unZip(unPackDir, connector, packageConnector.read(resourceName));
    }

    @Override
    public PackageConnector getPackageConnector() {
        return packageConnector;
    }

}
