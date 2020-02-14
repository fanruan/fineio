package com.fineio.logger;

/**
 * @author yee
 * @date 2018/7/12
 */
public class FineIOLoggers {
    private static FineIOLogger logger = FineIOLogger.DEFAULT;

    public static FineIOLogger getLogger() {
        return logger;
    }

    public static void setLogger(FineIOLogger logger) {
        FineIOLoggers.logger = logger;
    }
}
