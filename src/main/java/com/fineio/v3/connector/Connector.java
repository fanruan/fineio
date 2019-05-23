package com.fineio.v3.connector;

import com.fineio.accessor.Block;
import com.fineio.accessor.store.IConnector;
import com.fineio.v3.file.FileKey;

/**
 *
 */
public interface Connector extends IConnector<FileKey> {

    Block list(String file);
}