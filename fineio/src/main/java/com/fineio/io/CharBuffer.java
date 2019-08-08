package com.fineio.io;

import com.fineio.accessor.buffer.CharBuf;
import com.fineio.io.file.FileBlock;
import com.fineio.memory.MemoryConstants;
import com.fineio.memory.MemoryUtils;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/9/19
 */
@Deprecated
public class CharBuffer extends BaseBuffer<CharBuffer.CharReadBuffer, CharBuffer.CharWriteBuffer> implements CharBuf {
    public CharBuffer(Connector connector, URI uri, boolean syncWrite, Listener listener) {
        super(connector, uri, syncWrite, listener);
    }

    public CharBuffer(Connector connector, FileBlock block, int maxOffset, boolean syncWrite, Listener listener) {
        super(connector, block, maxOffset, syncWrite, listener);
    }

    @Override
    protected int getOffset() {
        return MemoryConstants.OFFSET_CHAR;
    }

    @Override
    public CharWriteBuffer asWrite() {
        return new CharBufferW();
    }

    @Override
    public CharReadBuffer asRead() {
        return new CharBufferR();
    }

    public interface CharReadBuffer extends BufferR {
        char get(int pos);
    }

    public interface CharWriteBuffer extends BufferW {
        void put(char value);

        void put(int pos, char value);
    }

    @Deprecated
    private class CharBufferR extends ReadBuffer implements CharReadBuffer {
        @Override
        public char get(int pos) {
            checkRead(pos);
            return MemoryUtils.getChar(readAddress, pos);
        }
    }

    @Deprecated
    private class CharBufferW extends WriteBuffer implements CharWriteBuffer {
        @Override
        public void put(char value) {
            put(++writeCurrentPosition, value);
        }

        @Override
        public void put(int pos, char value) {
            ensureCapacity(pos);
            MemoryUtils.put(address, pos, value);
        }
    }
}

