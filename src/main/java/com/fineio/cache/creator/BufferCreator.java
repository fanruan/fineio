package com.fineio.cache.creator;


import com.fineio.cache.SyncStatus;
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
import com.fineio.memory.manager.obj.impl.AllocateObject;
import com.fineio.storage.Connector;
import com.fineio.thread.FineIOExecutors;
import com.fineio.v1.cache.CacheKeyLinkedMap;

import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/9/19
 */
public abstract class BufferCreator<B extends Buffer> {
    private static final long TIMEOUT = 10 * 60 * 1000L;
    private static final DeAllocator DE_ALLOCATOR = BaseDeAllocator.Builder.READ.build();
    protected final ScheduledExecutorService activeService = FineIOExecutors.newScheduledExecutorService(1, "active-thread");
    protected final ScheduledExecutorService timeoutService = FineIOExecutors.newScheduledExecutorService(1, "timeout-thread");
    protected Buffer.Listener listener;
    private CacheKeyLinkedMap<URI, B> bufferMap;

    private BufferCreator() {
        bufferMap = new CacheKeyLinkedMap<URI, B>();
        activeService.scheduleWithFixedDelay(createActiveTask(), TIMEOUT / 2, TIMEOUT / 2, TimeUnit.MILLISECONDS);
        timeoutService.scheduleWithFixedDelay(createTimeoutTask(), TIMEOUT, TIMEOUT, TimeUnit.MILLISECONDS);
        listener = new Buffer.Listener() {
            @Override
            public void remove(Buffer buffer) {
                bufferMap.remove(buffer.getUri(), true);
                DE_ALLOCATOR.deAllocate(new AllocateObject(buffer.getAddress(), buffer.getAllocateSize()));
            }
        };
    }

    private final Runnable createActiveTask() {
        return new Runnable() {
            @Override
            public void run() {
                resetAccess();
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                }
                update();
            }

            private void resetAccess() {
                Iterator<URI> iterator = bufferMap.iterator();
                while (iterator.hasNext()) {
                    bufferMap.get(iterator.next(), false).resentAccess();
                }
            }

            private void update() {
                Iterator<URI> iterator = bufferMap.iterator();
                while (iterator.hasNext()) {
                    URI key = iterator.next();
                    if (bufferMap.get(key, false).resentAccess()) {
                        bufferMap.update(key);
                    }
                }
            }
        };
    }

    private final Runnable createTimeoutTask() {
        return new Runnable() {
            @Override
            public void run() {
                Iterator<URI> iterator = bufferMap.iterator();
                while (iterator.hasNext()) {
                    URI key = iterator.next();
                    if (bufferMap.getIdle(key) >= TIMEOUT) {
                        B buffer = bufferMap.get(key, false);
                        switch (buffer.getLevel()) {
                            case WRITE:
                                bufferMap.update(key);
                                break;
                            case CLEAN:
                                clearBuffer(buffer);
                                break;
                            case READ:
                                if (buffer.getSyncStatus() != SyncStatus.SYNC) {
                                    clearBuffer(buffer);
                                }
                            default:
                        }
                    }
                }
            }

            private void clearBuffer(Buffer buffer) {
                bufferMap.remove(buffer.getUri(), true);
                DE_ALLOCATOR.deAllocate(new AllocateObject(buffer.getAddress(), buffer.getAllocateSize()));
            }
        };
    }

    public B createBuffer(Connector connector, FileBlock block, int maxOffset) {
        B buffer = bufferMap.get(block.getBlockURI(), true);
        if (null == buffer) {
            buffer = create(connector, block, maxOffset);
            bufferMap.put(block.getBlockURI(), buffer);
        }
        return buffer;
//        return create(connector, block, maxOffset);
    }

    protected abstract B create(Connector connector, FileBlock block, int maxOffset);

    public B createBuffer(Connector connector, URI uri) {
        B buffer = bufferMap.get(uri, true);
        if (null == buffer) {
            buffer = create(connector, uri);
            bufferMap.put(uri, buffer);
        }
        return buffer;
    }

    protected abstract B create(Connector connector, URI uri);

    public B poll() {
        int size = bufferMap.size();
        for (int i = 0; i < size; i++) {
            B buffer = bufferMap.poll();
            if (null == buffer) {
                return null;
            }
            switch (buffer.getLevel()) {
                case READ:
                    if (buffer.getSyncStatus() == SyncStatus.UNSUPPORTED) {
                        return buffer;
                    } else {
                        bufferMap.put(buffer.getUri(), buffer);
                    }
                    break;
                case CLEAN:
                    return buffer;
                case WRITE:
                    bufferMap.put(buffer.getUri(), buffer);
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    public enum Builder {
        /**
         * ByteBufferCreator
         */
        BYTE {
            @Override
            public BufferCreator<ByteBuffer> build() {
                return new BufferCreator<ByteBuffer>() {
                    @Override
                    protected ByteBuffer create(Connector connector, FileBlock block, int maxOffset) {
                        return new ByteBuffer(connector, block, maxOffset, listener);
                    }

                    @Override
                    protected ByteBuffer create(Connector connector, URI uri) {
                        return null;
                    }
                };
            }
        },
        /**
         * ByteBufferCreator
         */
        INT {
            @Override
            public BufferCreator<IntBuffer> build() {
                return new BufferCreator<IntBuffer>() {
                    @Override
                    protected IntBuffer create(Connector connector, FileBlock block, int maxOffset) {
                        return new IntBuffer(connector, block, maxOffset, listener);
                    }

                    @Override
                    protected IntBuffer create(Connector connector, URI uri) {
                        return null;
                    }
                };
            }
        },
        LONG {
            @Override
            public BufferCreator<LongBuffer> build() {
                return new BufferCreator<LongBuffer>() {
                    @Override
                    protected LongBuffer create(Connector connector, FileBlock block, int maxOffset) {
                        return new LongBuffer(connector, block, maxOffset, listener);
                    }

                    @Override
                    protected LongBuffer create(Connector connector, URI uri) {
                        return null;
                    }
                };
            }
        },
        FLOAT {
            @Override
            public BufferCreator<FloatBuffer> build() {
                return new BufferCreator<FloatBuffer>() {
                    @Override
                    protected FloatBuffer create(Connector connector, FileBlock block, int maxOffset) {
                        return new FloatBuffer(connector, block, maxOffset, listener);
                    }

                    @Override
                    protected FloatBuffer create(Connector connector, URI uri) {
                        return null;
                    }
                };
            }
        },
        CHAR {
            @Override
            public BufferCreator<CharBuffer> build() {
                return new BufferCreator<CharBuffer>() {
                    @Override
                    protected CharBuffer create(Connector connector, FileBlock block, int maxOffset) {
                        return new CharBuffer(connector, block, maxOffset, listener);
                    }

                    @Override
                    protected CharBuffer create(Connector connector, URI uri) {
                        return null;
                    }
                };
            }
        },
        SHORT {
            @Override
            public BufferCreator<ShortBuffer> build() {
                return new BufferCreator<ShortBuffer>() {
                    @Override
                    protected ShortBuffer create(Connector connector, FileBlock block, int maxOffset) {
                        return new ShortBuffer(connector, block, maxOffset, listener);
                    }

                    @Override
                    protected ShortBuffer create(Connector connector, URI uri) {
                        return null;
                    }
                };
            }
        },
        DOUBLE {
            @Override
            public BufferCreator<DoubleBuffer> build() {
                return new BufferCreator<DoubleBuffer>() {
                    @Override
                    protected DoubleBuffer create(Connector connector, FileBlock block, int maxOffset) {
                        return new DoubleBuffer(connector, block, maxOffset, listener);
                    }

                    @Override
                    protected DoubleBuffer create(Connector connector, URI uri) {
                        return null;
                    }
                };
            }
        };

        /**
         * 创建器
         *
         * @param <B>
         * @return
         */
        public abstract <B extends Buffer> BufferCreator<B> build();
    }
}
