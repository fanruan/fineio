package com.fineio.logger;

import com.fineio.FineIO;

/**
 * @author yee
 * @date 2018/7/12
 */
public interface FineIOLogger {
    void info(String msg);
    void error(String msg);
    void error(String msg, Throwable throwable);
    void error(Throwable throwable);
    void debug(String msg);

    FineIOLogger DEFAULT = new FineIOLogger() {
        @Override
        public void info(String msg) {
            System.out.println("[INFO] " + msg);
        }

        @Override
        public void error(String msg) {
            error(msg, null);
        }

        @Override
        public void error(String msg, Throwable throwable) {
            System.err.print("[ERROR] ");
            if (null == msg) {
                System.err.println();
            } else {
                System.err.println(msg);
            }
            if (null != throwable) {
                throwable.printStackTrace();
            }
        }

        @Override
        public void error(Throwable throwable) {
            error(null, throwable);
        }

        @Override
        public void debug(String msg) {
            if (FineIO.DEBUG) {
                System.out.println("[DEBUG] " + msg);
            }
        }
    };
}
