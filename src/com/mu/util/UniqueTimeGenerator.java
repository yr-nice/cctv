/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This utility is used when multiple threads need a guarantee that calls to
 * System.currentTimeMillis() do not return the same long value. We considered
 * using nanoTime() but even though it has a million times more resolution,
 * there was no guarantee that each call would return unique values. On top of
 * that, all relative time calculations would need to be modified to convert
 * nanoseconds to milliseconds.
 * 
 * @author bernardng
 */
public class UniqueTimeGenerator {
    private static long lastTime = 0;
    private static Lock timeLOCK = new ReentrantLock();
    
    public static long currentTimeMillis() {
        long now = System.currentTimeMillis();
        
        timeLOCK.lock();
        if (!(now > lastTime)) {
            now = lastTime + 1;
        } else {
        }
        lastTime = now;
        timeLOCK.unlock();
        
        return now;
    }
    
    private static final int NUM_ITERATIONS = 100;
    
    public static void main(String args[]) throws InterruptedException {
        long lastReturnedTime = 0;
        
        System.out.println("Using System.curentTimeMillis():");
        for (int i = 0 ; i <= NUM_ITERATIONS; i++) {
            long now = System.currentTimeMillis();
            if (now == lastReturnedTime) {
                System.err.println("Duplicate time " + now + ".");
            }
            lastReturnedTime = now;
        }
        
        Thread.sleep(1000);
        
        System.out.println("Using UniqueTimeGenerator.curentTimeMillis():");
        for (int i = 0 ; i <= NUM_ITERATIONS; i++) {
            long now = UniqueTimeGenerator.currentTimeMillis();
            if (now == lastReturnedTime) {
                System.err.println("BUG! DUPLICATE time " + now + ".");
            }
            lastReturnedTime = now;
        }
        System.out.println("Should have no duplicates for UniqueTimeGenerator.curentTimeMillis().");
    }
}
