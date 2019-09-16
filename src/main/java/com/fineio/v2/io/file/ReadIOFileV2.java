package com.fineio.v2.io.file;

import com.fineio.io.file.FileBlock;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.storage.Connector;
import com.fineio.v2.io.BaseBuffer;
import com.fineio.v2.io.Buffer;
import com.fineio.v2.io.file.writer.JobFinishedManager;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/20
 */
public final class ReadIOFileV2<B extends Buffer> extends BaseReadIOFileV2<B> {
    ReadIOFileV2(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
    }

    public static final <E extends BaseBuffer> ReadIOFileV2<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new ReadIOFileV2<E>(connector, uri, model);
    }

    @Override
    protected FileLevel getFileLevel() {
        return FileLevel.READ;
    }

    @Override
    protected Buffer initBuffer(int index) {
        synchronized (this) {
            if (null == buffers[index]) {
                BaseBuffer buffer = model.createBuffer(connector, createIndexBlock(index), block_size_offset, false);
                buffers[index] = buffer.asRead();
            }
            return buffers[index];
        }
    }

    /**
     * 删除操作
     *
     * @return
     */
    public void delete() {

        JobFinishedManager.getInstance().finish(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    boolean delete = connector.delete(createHeadBlock());
                    if (buffers != null) {
                        for (int i = 0; i < buffers.length; i++) {
                            //内存泄露
                            if (!released && buffers[i] != null) {
                                MemoryObject object = buffers[i].getFreeObject();
                                if (null != object) {
                                    buffers[i].unLoad();
                                    BaseDeAllocator.Builder.READ.build().deAllocate(object);
                                }
                                buffers[i] = null;
                            }
                            boolean v = connector.delete(createIndexBlock(i));
                            if (delete) {
                                delete = v;
                            }
                        }
                    }
                    connector.delete(new FileBlock(uri));
                    URI parentURI = uri;
                    while (null != (parentURI = connector.deleteParent(new FileBlock(parentURI)))) {
                    }
                    released = true;
                }
            }
        });

    }

    @Override
    public void close() {
        synchronized (this) {
            if (null != buffers) {
                for (int i = 0; i < buffers.length; i++) {
                    if (null != buffers[i]) {
                        buffers[i].clearAfterClose();
                        buffers[i] = null;
                    }
                }
            }
        }
    }
}