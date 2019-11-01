package com.fineio.v2.cache;


import com.fineio.io.Level;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.memory.manager.deallocator.DeAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.manager.MemoryManager;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.storage.Connector;
import com.fineio.v2.cache.creator.BufferCreator;
import com.fineio.v2.io.Buffer;
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
            public boolean cleanTimeout() {
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
            public boolean cleanOne() {
                boolean result = false;
                result |= BYTE_CREATOR.cleanBuffers(Level.CLEAN);
                result |= INT_CREATOR.cleanBuffers(Level.CLEAN);
                result |= LONG_CREATOR.cleanBuffers(Level.CLEAN);
                result |= DOUBLE_CREATOR.cleanBuffers(Level.CLEAN);
                result |= SHORT_CREATOR.cleanBuffers(Level.CLEAN);
                result |= CHAR_CREATOR.cleanBuffers(Level.CLEAN);
                result |= FLOAT_CREATOR.cleanBuffers(Level.CLEAN);
                return result;
            }

            @Override
            public void cleanReadable() {
                BYTE_CREATOR.cleanBuffers(Level.READ);
                INT_CREATOR.cleanBuffers(Level.READ);
                LONG_CREATOR.cleanBuffers(Level.READ);
                DOUBLE_CREATOR.cleanBuffers(Level.READ);
                SHORT_CREATOR.cleanBuffers(Level.READ);
                CHAR_CREATOR.cleanBuffers(Level.READ);
                FLOAT_CREATOR.cleanBuffers(Level.READ);
            }

            private boolean clean(BufferCreator creator) {

                try {
                    Buffer buffer = creator.poll();
                    if (null != buffer) {
                        MemoryObject obj = buffer.getFreeObject();
                        if (null != obj) {
                            buffer.unLoad();
                            deAllocator.deAllocate(obj);
                            return true;
                        }
                    }
                } catch (InterruptedException e) {
                    FineIOLoggers.getLogger().error(e);
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
            public ByteBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
                return BYTE_CREATOR.createBuffer(connector, fileBlock, maxOffset, sync);
            }

            @Override
            public ByteBuffer createBuffer(Connector connector, URI uri, boolean sync) {
                return BYTE_CREATOR.createBuffer(connector, uri, sync);
            }
        },
        INT {
            @Override
            public IntBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
                return INT_CREATOR.createBuffer(connector, fileBlock, maxOffset, sync);
            }

            @Override
            public IntBuffer createBuffer(Connector connector, URI uri, boolean sync) {
                return INT_CREATOR.createBuffer(connector, uri, sync);
            }
        }, LONG {
            @Override
            public LongBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
                return LONG_CREATOR.createBuffer(connector, fileBlock, maxOffset, sync);
            }

            @Override
            public LongBuffer createBuffer(Connector connector, URI uri, boolean sync) {
                return LONG_CREATOR.createBuffer(connector, uri, sync);
            }
        }, DOUBLE {
            @Override
            public DoubleBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
                return DOUBLE_CREATOR.createBuffer(connector, fileBlock, maxOffset, sync);
            }

            @Override
            public DoubleBuffer createBuffer(Connector connector, URI uri, boolean sync) {
                return DOUBLE_CREATOR.createBuffer(connector, uri, sync);
            }
        },
        SHORT {
            @Override
            public ShortBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
                return SHORT_CREATOR.createBuffer(connector, fileBlock, maxOffset, sync);
            }

            @Override
            public ShortBuffer createBuffer(Connector connector, URI uri, boolean sync) {
                return SHORT_CREATOR.createBuffer(connector, uri, sync);
            }
        },
        CHAR {
            @Override
            public CharBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
                return CHAR_CREATOR.createBuffer(connector, fileBlock, maxOffset, sync);
            }

            @Override
            public CharBuffer createBuffer(Connector connector, URI uri, boolean sync) {
                return CHAR_CREATOR.createBuffer(connector, uri, sync);
            }
        },
        FLOAT {
            @Override
            public FloatBuffer createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync) {
                return FLOAT_CREATOR.createBuffer(connector, fileBlock, maxOffset, sync);
            }

            @Override
            public FloatBuffer createBuffer(Connector connector, URI uri, boolean sync) {
                return FLOAT_CREATOR.createBuffer(connector, uri, sync);
            }
        };

        public abstract <B extends Buffer> B createBuffer(Connector connector, FileBlock fileBlock, int maxOffset, boolean sync);

        public <B extends Buffer> B createBuffer(Connector connector, FileBlock fileBlock, int maxOffset) {
            return createBuffer(connector, fileBlock, maxOffset, false);
        }

        public abstract <B extends Buffer> B createBuffer(Connector connector, URI uri, boolean sync);

        public <B extends Buffer> B createBuffer(Connector connector, URI uri) {
            return createBuffer(connector, uri, false);
        }
    }
}
