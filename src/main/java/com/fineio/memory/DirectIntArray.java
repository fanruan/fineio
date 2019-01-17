//package com.fineio.memory;
//
///**
// * Created by daniel on 2017/5/18.
// */
//public class DirectIntArray {
//    private long address;
//    private long maxLen;
//    private  volatile boolean isClear =false;
//
//    public DirectIntArray(DirectIntArray old, long newLen) {
//        if(old.isClear){
//            throw new RuntimeException("ERROR CLEARED");
//        }
//        old.isClear = true;
//        address = MemoryUtils.reallocate(old.address, newLen << 2);
//        maxLen = newLen;
//    }
//
//    public long size(){
//        return maxLen;
//    }
//
//    public DirectIntArray(long len) {
//        setValue(len);
//        MemoryUtils.fill0(address, len << 2);
//    }
//
//    private void setValue(long len) {
//        if(len < 0){
//            throw new ArrayIndexOutOfBoundsException((int)len);
//        }
//        this.maxLen = len;
//        address = MemoryUtils.allocate(len << 2);
//    }
//
//    public DirectIntArray(long len, int defaultValue) {
//        setValue(len);
//        for(int i = 0; i < len; i++){
//            MemoryUtils.put(address, i, defaultValue);
//        }
//    }
//
//    public void put(long index, int value) {
//        MemoryUtils.put(address, checkIndex(index), value);
//    }
//
//    private long checkIndex(long index) {
//        if(isClear){
//            throw new RuntimeException("ERROR CLEARED");
//        }
//        if(index < 0 || index >= maxLen) {
//            throw new ArrayIndexOutOfBoundsException((int)index);
//        }
//        return index;
//    }
//
//    public int get(int index) {
//        return MemoryUtils.getInt(address, checkIndex(index));
//    }
//
//    public void finalize () throws  Throwable{
//        release();
//        super.finalize();
//    }
//
//    public void  release() {
//        if(!isClear && address != 0) {
//            synchronized (this) {
//                if(!isClear && address != 0) {
//                    MemoryUtils.free(address);
//                    isClear = true;
//                }
//            }
//        }
//    }
//}
