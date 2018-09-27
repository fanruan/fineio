package com.fineio.cache.creator;


import com.fineio.cache.SyncStatus;
import com.fineio.io.Buffer;
import com.fineio.io.ByteBuffer;
import com.fineio.io.CharBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.FloatBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.Level;
import com.fineio.io.LongBuffer;
import com.fineio.io.ShortBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.manager.deallocator.DeAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.storage.Connector;
import com.fineio.thread.FineIOExecutors;
import com.fineio.v1.cache.CacheLinkedMap;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    protected final ReferenceQueue<B> bufferReferenceQueue;
    private CacheLinkedMap<B> bufferMap;
    private Map<URI, B> keyMap = new ConcurrentHashMap<URI, B>();

    private BufferCreator() {
        bufferReferenceQueue = new ReferenceQueue<B>();
        bufferMap = new CacheLinkedMap<B>(bufferReferenceQueue);
        activeService.scheduleWithFixedDelay(createActiveTask(), TIMEOUT / 2, TIMEOUT / 2, TimeUnit.MILLISECONDS);
        timeoutService.scheduleWithFixedDelay(createTimeoutTask(), TIMEOUT, TIMEOUT, TimeUnit.MILLISECONDS);
        listener = new Buffer.Listener() {
            @Override
            public void remove(Buffer buffer) {
                MemoryObject object = buffer.getFreeObject();
                if (null != object) {
                    B b = keyMap.remove(buffer.getUri());
                    bufferMap.remove(b, true);
                    DE_ALLOCATOR.deAllocate(object);
                    buffer.unLoad();
                    Reference<? extends B> ref = null;
                    while (null != (ref = bufferReferenceQueue.poll())) {
                        synchronized (ref) {
                            ref.clear();
                            ref = null;
                        }
                    }
                }
            }

            @Override
            public void update(Buffer buffer) {
                bufferMap.put((B) buffer);
            }
        };
    }

    public final boolean cleanableBuffers() {
        boolean result = false;
        Iterator<B> iterator = bufferMap.iterator();
        while (iterator.hasNext()) {
            B buffer = iterator.next();
            if (buffer.getLevel() == Level.CLEAN) {
                B b = keyMap.remove(buffer.getUri());
                bufferMap.remove(b, true);
                DE_ALLOCATOR.deAllocate(buffer.getFreeObject());
                buffer.unLoad();
                result = true;
            }
        }
        return result;
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
                Iterator<B> iterator = bufferMap.iterator();
                while (iterator.hasNext()) {
                    iterator.next().resetAccess();
                }
            }

            private void update() {
                Iterator<B> iterator = bufferMap.iterator();
                while (iterator.hasNext()) {
                    B buffer = iterator.next();
                    if (buffer.resentAccess()) {
                        bufferMap.update(buffer);
                    }
                }
            }
        };
    }

    private final Runnable createTimeoutTask() {
        return new Runnable() {
            @Override
            public void run() {
                Iterator<B> iterator = bufferMap.iterator();
                while (iterator.hasNext()) {
                    B buffer = iterator.next();
                    if (bufferMap.getIdle(buffer) >= TIMEOUT) {
                        switch (buffer.getLevel()) {
                            case WRITE:
                                bufferMap.update(buffer);
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
                        Reference<? extends B> ref = null;
                        while (null != (ref = bufferReferenceQueue.poll())) {
                            synchronized (ref) {
                                ref.clear();
                                ref = null;
                            }
                        }
                    }
                }
            }

            private void clearBuffer(Buffer buffer) {
                MemoryObject object = buffer.getFreeObject();
                if (null != object) {
                    B b = keyMap.remove(buffer.getUri());
                    bufferMap.remove(b, true);
                    DE_ALLOCATOR.deAllocate(object);
                    buffer.unLoad();
                }
            }
        };
    }

    public B createBuffer(Connector connector, FileBlock block, int maxOffset) {
        synchronized (this) {
            B buffer = keyMap.get(block.getBlockURI());
            if (null == buffer) {
                buffer = create(connector, block, maxOffset);
                keyMap.put(block.getBlockURI(), buffer);
            } else {
                bufferMap.update(buffer);
            }
            return buffer;
        }
//        return create(connector, block, maxOffset);
    }

    protected abstract B create(Connector connector, FileBlock block, int maxOffset);

    public B createBuffer(Connector connector, URI uri) {
        synchronized (this) {
            B buffer = keyMap.get(uri);
            if (null == buffer) {
                buffer = create(connector, uri);
                keyMap.put(uri, buffer);
            }
            bufferMap.put(buffer);
            return buffer;
        }
    }

    protected abstract B create(Connector connector, URI uri);

    public B poll() {
        B buffer = bufferMap.peek();
        if (null == buffer) {
            return null;
        }
        switch (buffer.getLevel()) {
            case READ:
                if (buffer.getSyncStatus() == SyncStatus.UNSUPPORTED) {
                    keyMap.remove(buffer.getUri());
                    return bufferMap.poll();
                } else {
                    bufferMap.update(buffer);
                }
                return null;
            case CLEAN:
                keyMap.remove(buffer.getUri());
                return bufferMap.poll();
            default:
                bufferMap.update(buffer);
                return null;

        }
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
