/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.net.downloader;

import com.mu.util.Log;
import com.mu.util.concurrent.Task;
import com.mu.util.concurrent.Thr;
import com.mu.util.concurrent.ThreadPoolMgr;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author Hello
 */
public class DownloadThrPoolMgr extends ThreadPoolMgr
{
    public static void main(String[] argu) throws Exception
    {
        LinkedBlockingDeque<DownloadTask> queue = new LinkedBlockingDeque<DownloadTask>();
        DownloadThrPoolMgr mgr = new DownloadThrPoolMgr("test", queue, 1);
        mgr.startAllThr();
        /*Thread.sleep(5*1000);
        mgr.resizePool(20);
        Thread.sleep(5*1000);
        mgr.resizePool(3);*/
        DownloadTask tk = new DownloadTask();
        tk.setUrl("http://download.textpad.com/download/v53/txpeng531.exe");
        tk.setDest("c:\\temp\\downloaded_file.exe");
        queue.add(tk);
        for(int i=0; i<10000; i++)
        {
            Log.log(String.format("%d, status=%s, size=%d, downloaded=%d, percentage=%s",
                i, tk.getStatus(), tk.getSize(), tk.getDownloaded(), tk.getPercentage()));
            Thread.sleep(1000);
        }

        Thread.sleep(15*1000);
        mgr.stopAllThr();
    }

    private DownloadTaskMgr taskMgr;

    public void setTaskMgr(DownloadTaskMgr taskMgr)
    {
        this.taskMgr = taskMgr;
    }


    public DownloadThrPoolMgr(String poolName, BlockingDeque queue, int thrNo)
    {
        super(poolName, queue, thrNo, DownloaderThr.class);
    }

    public void update(Thr t, Task task)
    {
        if(taskMgr != null)
            taskMgr.update((DownloadTask)task);
    }

}
