package com.fineio.test.io.read;

import junit.framework.TestCase;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by daniel on 2017/2/28.
 */
public class SingleTest extends TestCase {


    private static class A {
        private static Lock  lock = new ReentrantLock();
        static A ins;

        private static int len = 10;

        private String v;

        private String[] q;
        private String[] f;
        private String[] f1;
        private String[] f2;
        private String[] f3;
        private String[] f4;
        private String[] f5;


        A() {
            f = new String[len];
            for(int i = 0; i < f.length; i++){
                f[i] = UUID.randomUUID().toString();
            }
            f1 = new String[len];
            for(int i = 0; i < f1.length; i++){
                f1[i] = UUID.randomUUID().toString();
            }
            f2 = new String[len];
            for(int i = 0; i < f2.length; i++){
                f2[i] = UUID.randomUUID().toString();
            }
            f3 = new String[len];
            for(int i = 0; i < f3.length; i++){
                f3[i] = UUID.randomUUID().toString();
            }
            f4 = new String[len];
            for(int i = 0; i < f4.length; i++){
                f4[i] = UUID.randomUUID().toString();
            }

            f5 = new String[len];
            for(int i = 0; i < f5.length; i++){
                f5[i] = UUID.randomUUID().toString();
            }
            q = new String[len];
            for(int i = 0; i < q.length; i++){
                q[i] = UUID.randomUUID().toString();
            }

            v = "China";
        }

        public String getV(){
            return v;
        }

        public boolean nullCheck(){
            return f3[len/2] != null;
        }

        public static A getIns() {
            if(ins == null) {
                synchronized (A.class) {
                    if (ins == null) {
                        ins = new A();
                    }
                }
            }
            return ins;
        }

        public static void clear(){
            ins = null;
        }
    }

//
//    public void test2(){
//
//        while (true) {
//
//
//            Runtime.getRuntime().exec("java ")
//        }
//
//    }

    public void testLock() {
        double[] doubles = createRandomDouble();
        double d = 0;
        long t = System.currentTimeMillis();
        for(int i = 0; i < doubles.length; i++) {
            d+=doubles[i];
        }
        System.out.println(System.currentTimeMillis() - t);

        t = System.currentTimeMillis();
        for(int i = 0; i < doubles.length; i++) {
            d+=doubles[i];
        }
        System.out.println(System.currentTimeMillis() - t);


        t = System.currentTimeMillis();
        int count = 0;
        for(int i = 0; i < doubles.length; i++) {
            synchronized (doubles) {
                count++;
                d += doubles[i];
                count--;
            }
        }
        System.out.println(System.currentTimeMillis() - t);

        t = System.currentTimeMillis();
        AtomicInteger integer = new AtomicInteger();
        for(int i = 0; i < doubles.length; i++) {
                integer.addAndGet(1);
              d += doubles[i];
            integer.addAndGet(-1);
        }
        System.out.println(System.currentTimeMillis() - t);
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        for(int i = 0; i < doubles.length; i++) {
            readWriteLock.readLock().lock();
            d += doubles[i];
            readWriteLock.readLock().unlock();
        }
        System.out.println(System.currentTimeMillis() - t);
    }







    private double[] createRandomDouble(){
        int len = (int) (Math.random()*10000);
        double[] arrays = new double[len];
        for(int i = 0; i< len; i++){
            arrays[i] = Math.random() * 100000000000d;
        }
        return arrays;
    }

    public static void main(String[] args){
            try {
                new SingleTest().test1();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    public void test1() throws Exception {
        int len = 100;
        Thread[] threads = new Thread[len];
        final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        final AtomicInteger ai = new AtomicInteger(0);
        for(int i = 0; i < len; i++){
            threads[i] = new Thread(){
               public void run() {
                   while (atomicBoolean.get()) {
                   }
                   assertTrue(A.getIns().nullCheck());
               }
            };
        }


        for(int i = 0; i < len; i++){
            threads[i].start();
        }
//        new Thread(){
//            public void run() {
//                while (atomicBoolean.get()){
//                }
//                System.out.println(ai.get());
//            }
//        }.start();
        atomicBoolean.set(false);
        for(int i = 0; i < len; i++){
            threads[i].join();
        }
        A.clear();
    }
}
