package com.fineio.accessor;

import com.fineio.accessor.file.IFile;

/**
 * 创建File的Model
 * @author yee
 * @date 2019-05-22
 */
public interface Model<F extends IFile> {
    /**
     * FileMode
     *
     * @return
     * @see FileMode
     */
    FileMode getFileMode();

    /**
     * readModel
     * @return
     */
    Model<F> asRead();

    /**
     * writeModel
     * @return
     */
    Model<F> asWrite();

    /**
     * appendModel
     * @return
     */
    Model<F> asAppend();

    /**
     * 数据类型
     * @return
     */
    DataType getDataType();
}
