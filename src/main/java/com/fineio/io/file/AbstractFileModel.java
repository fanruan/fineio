package com.fineio.io.file;

import com.fineio.storage.Connector;

import java.net.URI;

public abstract class AbstractFileModel<T> {
    protected abstract <F extends T> F createBuffer(final Connector p0, final FileBlock p1, final int p2);

    public abstract <F extends T> F createBuffer(final Connector p0, final URI p1);

    protected abstract byte offset();
}
