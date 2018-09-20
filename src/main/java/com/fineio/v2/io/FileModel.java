package com.fineio.v2.io;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v2.io.edit.EditBuffer;
import com.fineio.v2.io.read.ReadOnlyBuffer;
import com.fineio.v2.io.write.WriteOnlyBuffer;

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

    @Deprecated
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
