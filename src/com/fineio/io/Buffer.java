package com.fineio.io;

import java.io.InputStream;

/**
 * Created by daniel on 2017/2/20.
 */
public interface Buffer {

    InputStream getInputStream();

    boolean full();
}
