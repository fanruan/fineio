package com.fineio.directio;

import com.fineio.io.Buffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.FileModel;
import com.fineio.io.file.writer.JobFinishedManager;
import com.fineio.memory.manager.deallocator.DeAllocator;
import com.fineio.memory.manager.deallocator.impl.BaseDeAllocator;
import com.fineio.memory.manager.obj.MemoryObject;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/10/2
 */
public final class DirectReadIOFile<B extends Buffer> extends DirectIOFile<B> {
    private static final DeAllocator DE_ALLOCATOR = BaseDeAllocator.Builder.READ.build();

    DirectReadIOFile(Connector connector, URI uri, FileModel model) {
        super(connector, uri, model);
    }

    public static final <E extends Buffer> DirectReadIOFile<E> createFineIO(Connector connector, URI uri, FileModel model) {
        return new DirectReadIOFile<E>(connector, uri, model);
    }

    @Override
    protected Buffer initBuffer() {
        if (buffer == null) {
            synchronized (this) {
                buffer = model.createBuffer(connector, uri).asRead();
            }
        }
        return buffer;
    }

    @Override
    protected void closeChild() {
        if (buffer != null) {
            MemoryObject object = buffer.getFreeObject();
            if (null != object) {
                DE_ALLOCATOR.deAllocate(object);
                buffer.unLoad();
            }
            buffer = null;
        }
    }

    public void delete() {
        JobFinishedManager.getInstance().finish(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if (!released) {
                        closeChild();
                        released = true;
                    }
                    connector.delete(new FileBlock(uri));
                }
            }
        });
    }
}
