package com.fineio.cache;


import com.fineio.cache.creator.BufferCreator;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.manager.deallocator.DeAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/19
 */
public final class CacheManager {

    private static final BufferCreator<ByteBuffer> BYTE_CREATOR = BufferCreator.Builder.BYTE.build();
    private static final BufferCreator<IntBuffer> INT_CREATOR = BufferCreator.Builder.INT.build();
    private static final BufferCreator<LongBuffer> LONG_CREATOR = BufferCreator.Builder.LONG.build();
    private static final BufferCreator<DoubleBuffer> DOUBLE_CREATOR = BufferCreator.Builder.DOUBLE.build();
    private static final BufferCreator<ShortBuffer> SHORT_CREATOR = BufferCreator.Builder.SHORT.build();
    private static final BufferCreator<CharBuffer> CHAR_CREATOR = BufferCreator.Builder.CHAR.build();
    private static final BufferCreator<FloatBuffer> FLOAT_CREATOR = BufferCreator.Builder.FLOAT.build();

    static {
        MemoryManager.INSTANCE.registerCleaner(createCleaner());
    }

    private static final MemoryManager.Cleaner createCleaner() {
        return new MemoryManager.Cleaner() {
            private DeAllocator deAllocator = BaseDeAllocator.Builder.READ.build();

            @Override
            public boolean clean() {
                boolean result = false;
                result |= clean(BYTE_CREATOR);
                result |= clean(INT_CREATOR);
                result |= clean(LONG_CREATOR);
                result |= clean(DOUBLE_CREATOR);
                result |= clean(SHORT_CREATOR);
                result |= clean(CHAR_CREATOR);
                result |= clean(FLOAT_CREATOR);
                return result;
            }

            @Override
            public boolean cleanAllCleanable() {
                boolean result = false;
                result |= BYTE_CREATOR.cleanableBuffers();
                result |= INT_CREATOR.cleanableBuffers();
                result |= LONG_CREATOR.cleanableBuffers();
                result |= DOUBLE_CREATOR.cleanableBuffers();
                result |= SHORT_CREATOR.cleanableBuffers();
                result |= CHAR_CREATOR.cleanableBuffers();
                result |= FLOAT_CREATOR.cleanableBuffers();
                return result;
            }

            @Override
            public void triggerWrite() {
            }

            private boolean clean(BufferCreator creator) {
                Buffer buffer = creator.poll();
                if (null != buffer) {
                    MemoryObject obj = buffer.getFreeObject();
                    deAllocator.deAllocate(obj);
                    buffer.unLoad();
                    return true;
                }
                return false;
            }
        };
    }

    public enum DataType {
        /**
         *
         */
        BYTE {
            @Override
            public ByteBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
                return BYTE_CREATOR.createBuffer(connector, fileBlock, maxOffset);
            }

            @Override
            public ByteBuffer createBuffer(Connector connector, URI uri) {
                return BYTE_CREATOR.createBuffer(connector, uri);
            }
        },
        INT {
            @Override
            public IntBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
                return INT_CREATOR.createBuffer(connector, fileBlock, maxOffset);
            }

            @Override
            public IntBuffer createBuffer(Connector connector, URI uri) {
                return INT_CREATOR.createBuffer(connector, uri);
            }
        }, LONG {
            @Override
            public LongBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
                return LONG_CREATOR.createBuffer(connector, fileBlock, maxOffset);
            }

            @Override
            public LongBuffer createBuffer(Connector connector, URI uri) {
                return LONG_CREATOR.createBuffer(connector, uri);
            }
        }, DOUBLE {
            @Override
            public DoubleBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
                return DOUBLE_CREATOR.createBuffer(connector, fileBlock, maxOffset);
            }

            @Override
            public DoubleBuffer createBuffer(Connector connector, URI uri) {
                return DOUBLE_CREATOR.createBuffer(connector, uri);
            }
        },
        SHORT {
            @Override
            public ShortBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
                return SHORT_CREATOR.createBuffer(connector, fileBlock, maxOffset);
            }

            @Override
            public ShortBuffer createBuffer(Connector connector, URI uri) {
                return SHORT_CREATOR.createBuffer(connector, uri);
            }
        },
        CHAR {
            @Override
            public CharBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
                return CHAR_CREATOR.createBuffer(connector, fileBlock, maxOffset);
            }

            @Override
            public CharBuffer createBuffer(Connector connector, URI uri) {
                return CHAR_CREATOR.createBuffer(connector, uri);
            }
        },
        FLOAT {
            @Override
            public FloatBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
                return FLOAT_CREATOR.createBuffer(connector, fileBlock, maxOffset);
            }

            @Override
            public FloatBuffer createBuffer(Connector connector, URI uri) {
                return FLOAT_CREATOR.createBuffer(connector, uri);
            }
        };

        public abstract <B extends Buffer> B createBuffer(Connector connector, FileBlock fileBlock, int maxOffset);

        public abstract <B extends Buffer> B createBuffer(Connector connector, URI uri);
    }
}
