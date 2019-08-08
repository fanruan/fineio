package com.fineio.transfer;

/**
 * @author yee
 * @date 2019-08-08
 */
public interface TransferProgressListener {
    void progress(int progress, String path);
}
