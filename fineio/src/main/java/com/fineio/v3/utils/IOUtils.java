package com.fineio.v3.utils;

import com.fineio.logger.FineIOLoggers;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author yee
 * @date 2019-05-21
 */
public class IOUtils {
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                FineIOLoggers.getLogger().error(e);
            }
        }
    }

    public static void copyBinaryTo(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] data = new byte[10240];
        int len;
        while ((len = inputStream.read(data)) > 0) {
            outputStream.write(data, 0, len);
        }

        outputStream.flush();
    }
}
