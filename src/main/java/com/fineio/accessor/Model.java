package com.fineio.accessor;

import com.fineio.accessor.file.IFile;
import com.fineio.v3.type.DataType;
import com.fineio.v3.type.FileMode;

/**
 * @author yee
 * @date 2019-05-22
 */
public interface Model<F extends IFile> {
    FileMode getFileMode();

    Model<F> asRead();

    Model<F> asWrite();

    Model<F> asAppend();

    DataType getDataType();
}
