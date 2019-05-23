package com.fineio.accessor;

import com.fineio.accessor.file.IFile;
import com.fineio.accessor.store.IConnector;

import java.net.URI;

/**
 * @author yee
 * @date 2019-05-22
 */
public interface FileCreator<F extends IFile> {
    F create(IConnector connector, URI uri);
}
