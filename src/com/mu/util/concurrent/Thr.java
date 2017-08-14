/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.concurrent;

import com.mu.util.Log;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Peng mu
 */
public class Thr extends Thread
{
    protected String status = "";
    protected boolean bStop = false;
    protected BlockingDeque queue;
    protected Task currentTask;
    protected ThreadPoolMgr mgr;

   /* static public Thr create()
    {
        //return new this.
        //class
        Class.forName("java.lang.String").getConstructor().
    }*/


    public Thr(String name, BlockingDeque queue, ThreadPoolMgr mgr)
    {
        this.setName(name);
        this.queue = queue;
        this.mgr = mgr;
    }

    public Task getCurrentTask()
    {
        return currentTask;
    }

    public String getStatus()
    {
        return status;
    }

    public void end()
    {
        bStop = true;
        interrupt();

    }

    public void run()
    {
        bStop = false;
        try{
        while(!bStop)
        {
            try{
            //do something
            Task t = (Task)queue.take();
            t.setStatus("Done");
            mgr.update(this, t);
            }catch(InterruptedException e){}
        }}catch(Exception e){Log.error(e);}
    }


}
