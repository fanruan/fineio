package com.fineio.accessor.impl;

import com.fineio.accessor.Model;
import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IFile;
import com.fineio.v3.type.DataType;
import com.fineio.v3.type.FileMode;

/**
 * @author yee
 * @date 2019-05-22
 */
public abstract class BaseModel<F extends IFile> implements Model<F> {

    private FileMode mode;

    private BaseModel(FileMode mode) {
        this.mode = mode;
    }

    private BaseModel() {
        this(FileMode.READ);
    }

    public static Model<IFile<ByteBuf>> ofByte() {
        return new BaseModel<IFile<ByteBuf>>() {
            @Override
            public DataType getDataType() {
                return DataType.BYTE;
            }
        };
    }

    public static Model<IFile<IntBuf>> ofInt() {
        return new BaseModel<IFile<IntBuf>>() {
            @Override
            public DataType getDataType() {
                return DataType.INT;
            }
        };
    }

    public static Model<IFile<LongBuf>> ofLong() {
        return new BaseModel<IFile<LongBuf>>() {
            @Override
            public DataType getDataType() {
                return DataType.LONG;
            }
        };
    }

    public static Model<IFile<DoubleBuf>> ofDouble() {
        return new BaseModel<IFile<DoubleBuf>>() {
            @Override
            public DataType getDataType() {
                return DataType.DOUBLE;
            }
        };
    }

    @Override
    public FileMode getFileMode() {
        return mode;
    }

    @Override
    public Model<F> asRead() {
        this.mode = FileMode.READ;
        return this;
    }

    @Override
    public Model<F> asWrite() {
        this.mode = FileMode.WRITE;
        return this;
    }

    @Override
    public Model<F> asAppend() {
        this.mode = FileMode.APPEND;
        return this;
    }
}
