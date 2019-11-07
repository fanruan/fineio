package com.fineio.v2.io.file;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.storage.Connector;
import com.fineio.v2.cache.CacheManager;
import com.fineio.v2.io.BaseBuffer;
import com.fineio.v2.io.ByteBuffer;
import com.fineio.v2.io.CharBuffer;
import com.fineio.v2.io.DoubleBuffer;
import com.fineio.v2.io.FloatBuffer;
import com.fineio.v2.io.IntBuffer;
import com.fineio.v2.io.LongBuffer;
import com.fineio.v2.io.ShortBuffer;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public enum FileModel {
    /**
     *
     */
    INT {
        @Override
        public <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync) {
            return CacheManager.DataType.INT.createBuffer(connector, uri, sync);
        }

        @Override
        public byte offset() {
            return MemoryConstants.OFFSET_INT;
        }

        @Override
        public IntBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
            return CacheManager.DataType.INT.createBuffer(connector, fileBlock, maxOffset, sync);
        }
    },
    BYTE {
        @Override
        public <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync) {
            return CacheManager.DataType.BYTE.createBuffer(connector, uri, sync);
        }

        @Override
        public byte offset() {
            return MemoryConstants.OFFSET_BYTE;
        }

        @Override
        public ByteBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
            return CacheManager.DataType.BYTE.createBuffer(connector, fileBlock, maxOffset, sync);
        }
    },
    LONG {
        @Override
        public <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync) {
            return CacheManager.DataType.LONG.createBuffer(connector, uri, sync);
        }

        @Override
        public byte offset() {
            return MemoryConstants.OFFSET_LONG;
        }

        @Override
        public LongBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
            return CacheManager.DataType.LONG.createBuffer(connector, fileBlock, maxOffset, sync);
        }
    },
    SHORT {
        @Override
        public <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync) {
            return CacheManager.DataType.SHORT.createBuffer(connector, uri, sync);
        }

        @Override
        public byte offset() {
            return MemoryConstants.OFFSET_SHORT;
        }

        @Override
        public ShortBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
            return CacheManager.DataType.SHORT.createBuffer(connector, fileBlock, maxOffset, sync);
        }
    },
    FLOAT {
        @Override
        public <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync) {
            return CacheManager.DataType.FLOAT.createBuffer(connector, uri, sync);
        }

        @Override
        public byte offset() {
            return MemoryConstants.OFFSET_FLOAT;
        }

        @Override
        public FloatBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
            return CacheManager.DataType.FLOAT.createBuffer(connector, fileBlock, maxOffset, sync);
        }
    },
    DOUBLE {
        @Override
        public <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync) {
            return CacheManager.DataType.DOUBLE.createBuffer(connector, uri, sync);
        }

        @Override
        public byte offset() {
            return MemoryConstants.OFFSET_DOUBLE;
        }

        @Override
        public DoubleBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
            return CacheManager.DataType.DOUBLE.createBuffer(connector, fileBlock, maxOffset, sync);
        }
    },
    CHAR {
        @Override
        public <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync) {
            return CacheManager.DataType.CHAR.createBuffer(connector, uri, sync);
        }

        @Override
        public byte offset() {
            return MemoryConstants.OFFSET_CHAR;
        }

        @Override
        public CharBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
            return CacheManager.DataType.CHAR.createBuffer(connector, fileBlock, maxOffset, sync);
        }
    };

    public abstract <B extends BaseBuffer> B createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync);

    public abstract <B extends BaseBuffer> B createBuffer(Connector connector, URI uri, boolean sync);

    public abstract byte offset();
}
