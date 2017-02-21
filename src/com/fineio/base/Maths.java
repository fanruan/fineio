package com.fineio.base;

/**
 * Created by daniel on 2017/2/21.
 */
public class Maths {

    public static int log2(int v) {
        if(v <= 1){
            return 0;
        } else {
            return 1 + log2(v >> 1);
        }
    }
}
