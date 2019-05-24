package com.fineio.v3.file.impl.read;


import com.fineio.accessor.file.IReadFile;
import com.fineio.io.file.FileBlock;
import com.fineio.logger.FineIOLoggers;
import com.fineio.storage.Connector;
import com.fineio.v3.buffer.DirectBuffer;
import com.fineio.v3.file.impl.File;
import com.fineio.v3.memory.MemoryManager;
import com.fineio.v3.memory.MemoryUtils;
import com.fineio.v3.memory.Offset;
import com.fineio.v3.type.FileMode;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * @author anchore
 * @date 2019/4/16
 */
abstract class ReadFile<B extends DirectBuffer> extends File<B> implements IReadFile<B> {
    ReadFile(FileBlock fileBlock, Offset offset, Connector connector) {
        super(fileBlock, offset, connector);
    }

    @Override
    protected B getBuffer(int nthBuf) {
        return buffers.computeIfAbsent(nthBuf, this::loadBuffer);
    }

    private B loadBuffer(int nthBuf) {
        // TODO: 2019/4/16 anchore 先拿cache，拿不到就生成buffer，put进cache
        FileBlock nthFileBlock = new FileBlock(fileBlock.getPath(), String.valueOf(nthBuf));
        Long address = null;
        int avail = 0;
        try (InputStream input = new BufferedInputStream(connector.read(nthFileBlock))) {
            avail = input.available();
            address = MemoryManager.INSTANCE.allocate(avail, FileMode.READ);
            long ptr = address;
            byte[] bytes = new byte[1024];
            for (int read; (read = input.read(bytes)) != -1; ptr += read) {
                MemoryUtils.copyMemory(bytes, ptr, read);
            }
            return newDirectBuf(address, (int) ((ptr - address) >> offset.getOffset()), nthFileBlock);
        } catch (Throwable e) {
            if (address != null) {
                MemoryManager.INSTANCE.release(address, avail, FileMode.READ);
            }
            FineIOLoggers.getLogger().error(e);
            return null;
        }
    }

    abstract B newDirectBuf(long address, int size, FileBlock fileBlock);

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            // TODO: 2019/4/15 anchore read file直接归还buffer给cache，由cache管理buffer生命周期
            buffers.clear();
        }
    }
}