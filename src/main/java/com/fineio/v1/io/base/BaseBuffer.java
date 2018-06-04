package com.fineio.v1.io.base;


import com.fineio.v1.io.file.IOFile;

/**
 * Created by daniel on 2017/2/20.
 * 这里不需要继承buffer 目的是限制传参
 * @see IOFile 限制IOFile的模板类型必须是Buffer的实际类 而不是abstract类
 */
public interface BaseBuffer {


    void clear();



}
