package com.fineio.test.io.read;

import Sense4.LockUtils;
import junit.framework.TestCase;

import java.lang.management.RuntimeMXBean;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by daniel on 2017/2/28.
 */
public class SingleTest extends TestCase {


    private static class A {
        static A ins;

        private String v;

        A() {
            v = "China";
        }

        public String getV(){
            return v;
        }

        public static A getIns() {
            if(ins == null) {
                synchronized (A.class){
                    if(ins == null){
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
        int len = (int) (Math.random()*100000000);
        double[] arrays = new double[len];
        for(int i = 0; i< len; i++){
            arrays[i] = Math.random() * 100000000000d;
        }
        return arrays;
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
                       ai.addAndGet(1);
                       assertEquals(A.getIns().getV(), "China");
                       ai.addAndGet(-1);
                   }
               }
            };
        }


        for(int i = 0; i < len; i++){
            threads[i].start();
        }
        new Thread(){
            public void run() {
                while (atomicBoolean.get()){
                    System.out.println(ai.get());
                }
            }
        }.start();
        atomicBoolean.set(false);
        for(int i = 0; i < len; i++){
            threads[i].join();
        }
        A.clear();
    }
}
