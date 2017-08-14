/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.concurrent;

import com.mu.util.Log;
import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author peng mu
 */
public class ThreadPoolMgr <T extends Thr>
{
    protected BlockingDeque queue;
    protected ConcurrentHashMap<String, T> pool = new ConcurrentHashMap<String, T>();
    protected int thrNo = 0;
    protected String poolName;
    protected String className;
    protected Class threadClass;

    public static void main(String[] argu) throws Exception
    {
        LinkedBlockingDeque<Task> queue = new LinkedBlockingDeque<Task>();
        ThreadPoolMgr mgr = new ThreadPoolMgr("test", queue, 3, Thr.class);
        mgr.startAllThr();
        //mgr.resizePool(1);
        ArrayList<Task> arr = new ArrayList();
        for(int i=0; i<10000; i++)
        {
            Task t1 = new Task();
            t1.setId(System.currentTimeMillis() + " - " + i);
            arr.add(t1);
        }
        for(Task t:arr)
            queue.add(t);

        Thread.sleep(5*1000);

        for(int i=0; i<10000; i++)
        {
            Task t1 = new Task();
            t1.setId(System.currentTimeMillis() + " - " + i);
            arr.add(t1);
        }
        for(Task t:arr)
            queue.add(t);

        Thread.sleep(2*1000);
        mgr.resizePool(100);
        Thread.sleep(5*1000);
        mgr.resizePool(1);
        Thread.sleep(10*1000);

        mgr.stopAllThr();/**/

    }

    public ThreadPoolMgr(String poolName, BlockingDeque queue, int thrNo, Class threadClass)
    {
        this.poolName = poolName;
        this.queue = queue;
        this.thrNo = thrNo;
        this.threadClass = threadClass;
        preparePool();
    }
    public void startAllThr()
    {
        for(T thr : pool.values())
            if(thr.getState() == Thread.State.NEW)
                thr.start();
    }
    public void stopAllThr()
    {
        for(T thr : pool.values())
            thr.end();
    }

    public void startSingleThr(String thrName)
    {
        T thr = pool.get(thrName);
        if(!thr.isAlive())
            thr.start();
    }
    public void stopSingleThr(String thrName)
    {
        T thr = pool.get(thrName);
        thr.end();
    }
    public void preparePool()
    {
        //clearPool();
        try{
        removeStoppedThr();
        if(pool.size() <= thrNo)
        {
            int no = thrNo - pool.size();
            String timestamp = String.valueOf(System.currentTimeMillis());
            for(int i=0; i<no; i++)
            {
                String name = poolName + "_" + timestamp + "_" + i;
                //T thr = new Thr(name, queue, this);
                //T thr = T.c;
                T thr = (T)threadClass.getConstructor(String.class, BlockingDeque.class, ThreadPoolMgr.class).newInstance(name, queue, this);
                pool.put(name, thr);
            }
        }
        else
        {
            int no = pool.size() - thrNo;
            int i = 0;
            for(Thr thr : pool.values())
            {
                if(i >= no)
                    break;
                thr.end();
                i++;
            }
            removeStoppedThr();
        }
        }catch(Exception e){Log.error(e);}
    }

    public void resizePool(int i)
    {
        thrNo = i;
        preparePool();
        startAllThr();
    }
    public int getPoolSize()
    {
        return thrNo;
    }
    public void removeStoppedThr()
    {
        ArrayList<String> stoppedArr = new ArrayList<String>();
        for(Thr thr : pool.values())
            if(!thr.isAlive())
                stoppedArr.add(thr.getName());

        for(String n : stoppedArr)
           pool.remove(n);
    }

    public void clearPool()
    {
        stopAllThr();
        pool.clear();
    }

    public void update(Thr t, Task task)
    {
        Log.log(String.format("Updated by %s, task id = %s, task status = %s", t.getName(), task.getId(), task.getStatus()));
        try{Thread.sleep(1000);}catch(Exception e){}

    }



}
