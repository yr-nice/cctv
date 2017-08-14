/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.net.downloader;

import com.mu.cctv.db.dao.DownloadTaskDAO;
import com.mu.cctv.web.CCTVDownloadTask;
import com.mu.util.Log;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author Hello
 */
public class DownloadTaskMgr
{
    private ConcurrentHashMap<String, DownloadTask> taskList = new ConcurrentHashMap<String, DownloadTask>();
    private BlockingDeque queue;
    private DownloadThrPoolMgr poolMgr;
    //private con

    public static void main(String[] argu) throws Exception
    {
        LinkedBlockingDeque<DownloadTask> queue = new LinkedBlockingDeque<DownloadTask>();
        DownloadThrPoolMgr mgr = new DownloadThrPoolMgr("test", queue, 1);
        mgr.startAllThr();
        DownloadTaskMgr tmgr = new DownloadTaskMgr(queue, mgr);
        for(int i=1; i<=15; i++)
        {
            DownloadTask tk = new DownloadTask();
            tk.setId("task"+i);
            tk.setUrl(String.format("http://203.208.206.19/v.cctv.com/flash/200911/qgds/2009/11/14/qgds_h264818000nero_aac32_20091114_1258131579791-%d.mp4", i));
            tk.setDest(String.format("c:\\temp\\gezi%d.mp4", i));
            tmgr.AddTask(tk);
        }/*
        DownloadTask k = new DownloadTask();
        k.setUrl(String.format("http://203.208.206.19/v.cctv.com/flash/200911/qgds/2009/11/14/qgds_h264818000nero_aac32_20091114_1258131579791-%d.mp4", 13));
        k.setDest(String.format("c:\\temp\\gezi%d.mp4", 13));
        queue.add(k);*/

        for(int i=0; i<10000; i++)
        {
            tmgr.getCurStatus();
            Thread.sleep(1000);
        }

        Thread.sleep(15*1000);
        mgr.stopAllThr();
    }

    public DownloadTaskMgr(BlockingDeque queue, DownloadThrPoolMgr poolMgr)
    {
        this.queue = queue;
        this.poolMgr = poolMgr;
        TaskSpeedChecker speedChecker = new TaskSpeedChecker(taskList);
        speedChecker.setDaemon(true);
        speedChecker.start();
    }

    public void AddTask(DownloadTask t)
    {
        if(t.getStatusCode() == TaskStatus.Removed || t.getStatusCode() == TaskStatus.Waiting_for_Start || t.getStatusCode() == TaskStatus.Start_Downloading)
            return;
        if(t.getStatusCode() != TaskStatus.Finished )
            t.setStatusCode(TaskStatus.Waiting_for_Start);

        taskList.put(t.getUrl(), t);
        queue.add(t);
    }

    public void removeTask(DownloadTask t)
    {
        if(t.getStatusCode() != TaskStatus.Finished && t.getStatusCode() != TaskStatus.Removed )
        {
            t.setStatusCode(TaskStatus.Suspended);
            DownloadTaskDAO.update((CCTVDownloadTask)t);
        }
        taskList.remove(t.getUrl());
        if(queue.contains(t))
           queue.remove(t);
    }

    public void stopAll()
    {
        //this.poolMgr.stopAllThr();
        queue.clear();
        for(DownloadTask t: taskList.values())
        {
            //t.setAverSpeed("0");
            //t.setCurSpeed("0");
            if(t.getStatusCode() != TaskStatus.Finished && t.getStatusCode() != TaskStatus.Removed )
                t.setStatusCode(TaskStatus.Suspended);
            DownloadTaskDAO.update((CCTVDownloadTask)t);
        }
        taskList.clear();

    }

    public String getCurStatus()
    {

        String re = "";
        int downloading = 0;
        int finished = 0;
        int pending = 0;

        for(DownloadTask t: taskList.values())
        {
            if(t.getStatusCode() == TaskStatus.Waiting_for_Start) pending++;
            if(t.getStatusCode() == TaskStatus.Finished) finished++;
            if(t.getStatusCode() == TaskStatus.Start_Downloading)
            {
                Log.log(String.format("%s, size=%d, downloaded=%d, percentage=%s, curSpeed=%s, averSpeed=%s",
                    t.getName(), t.getSize(), t.getDownloaded(), t.getPercentage(), t.getCurSpeed(), t.getAverSpeed()));
                downloading++;
            }
        }
        if(downloading>0)
            Log.log(String.format("Total: %d, downloading: %d, pending: %d, finished: %d", taskList.size(), downloading, pending, finished));
        return re;
    }


    synchronized public void update(DownloadTask task)
    {
        try{
        //TaskGenerator.updateTaskStatus(task, CfgMgr.getTaskListPath());
        }catch(Exception e){Log.log(e);}

    }


}
