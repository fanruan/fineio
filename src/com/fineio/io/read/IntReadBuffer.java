package com.fineio.io.read;

import com.fineio.io.file.FileBlock;
import com.fineio.io.file.ReadModel;
import com.fineio.io.IntBuffer;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * Created by daniel on 2017/2/14.
 */
public  final class IntReadBuffer extends ReadBuffer implements IntBuffer {

    public static final ReadModel MODEL = new ReadModel<IntBuffer>() {

        @Override
        protected final IntBuffer createBuffer(Connector connector, FileBlock block, int max_offset) {
            return new IntReadBuffer(connector, block, max_offset);
        }

        @Override
        public final IntBuffer createBuffer(Connector connector, URI uri) {
            return new IntReadBuffer(connector, uri);
        }

        protected final byte offset() {
            return OFFSET;
        }
    };


    private IntReadBuffer(Connector connector, FileBlock block, int max_offset) {
        super(connector, block, max_offset);
    }

    private IntReadBuffer(Connector connector, URI uri) {
        super(connector, uri);
    }

    protected int getLengthOffset() {
        return OFFSET;
    }


    public final int get(int p) {
        checkIndex(p);
        return MemoryUtils.getInt(address, p);
    }
}
