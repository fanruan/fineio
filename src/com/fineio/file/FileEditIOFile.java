package com.fineio.file;

import com.fineio.io.edit.Edit;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/20.
 */
public class FileEditIOFile<T extends Edit> extends FineAbstractReadFile<T> {

    private FileEditIOFile(Connector connector, URI uri, Class<T> clazz) {
        super(connector, uri, clazz);
    }
}
