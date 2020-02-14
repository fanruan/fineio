package com.fineio;

import com.fineio.logger.FineIOLogger;
import com.fineio.logger.FineIOLoggers;
import com.fineio.v3.file.impl.BufferCache;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class FineIO {

    /**
     * 设置Logger
     *
     * @param logger
     */
    public static void setLogger(FineIOLogger logger) {
        FineIOLoggers.setLogger(logger);
    }

    public static void start() {
        BufferCache.get().start();
    }

    public static void stop() {
        BufferCache.get().stop();
    }
}
