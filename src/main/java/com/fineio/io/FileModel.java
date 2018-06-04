package com.fineio.io;

import com.fineio.io.edit.EditBuffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.read.ReadOnlyBuffer;
import com.fineio.io.write.WriteOnlyBuffer;
import com.fineio.storage.Connector;

import java.net.URI;

/**
 * @author yee
 * @date 2018/6/1
 */
public enum FileModel {
    BYTE(ByteBuffer.MODE), CHAR(CharBuffer.MODE),
    SHORT(ShortBuffer.MODE), INT(IntBuffer.MODE),
    LONG(LongBuffer.MODE), FLOAT(FloatBuffer.MODE),
    DOUBLE(DoubleBuffer.MODE);
    private BufferModel<? extends AbstractBuffer> bufferModel;

    FileModel(BufferModel<? extends AbstractBuffer> bufferModel) {
        this.bufferModel = bufferModel;
    }

    public ReadOnlyBuffer createBufferForRead(Connector connector, FileBlock block, int max_offset) {
        return bufferModel.createBuffer(connector, block, max_offset).readOnlyBuffer();
    }

    public ReadOnlyBuffer createBufferForRead(Connector connector, URI uri) {
        return bufferModel.createBuffer(connector, uri).readOnlyBuffer();
    }

    public EditBuffer createBufferForEdit(Connector connector, FileBlock block, int max_offset) {
        return bufferModel.createBuffer(connector, block, max_offset).editBuffer();
    }

    public EditBuffer createBufferForEdit(Connector connector, URI uri) {
        return bufferModel.createBuffer(connector, uri).editBuffer();
    }

    public WriteOnlyBuffer createBufferForWrite(Connector connector, FileBlock block, int max_offset) {
        return bufferModel.createBuffer(connector, block, max_offset).writeOnlyBuffer();
    }

    public WriteOnlyBuffer createBufferForWrite(Connector connector, URI uri) {
        return bufferModel.createBuffer(connector, uri).writeOnlyBuffer();
    }

    public byte offset() {
        return bufferModel.offset();
    }
}
