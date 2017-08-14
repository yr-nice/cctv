/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.net.downloader;

import com.mu.util.Log;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Hello
 */
public class TaskSpeedChecker extends Thread
{
    private ConcurrentHashMap<String, DownloadTask> taskList;

    TaskSpeedChecker(ConcurrentHashMap<String, DownloadTask> taskList)
    {
        this.taskList = taskList;
    }

    @Override
    public void run()
    {
        try{
        while(true)
        {
            for(DownloadTask task : taskList.values())
            {
                if(task.getStatusCode() == TaskStatus.Start_Downloading)
                {
                    long curSize = task.getDownloaded();
                    long lastSize = task.getLastScanSize();
                    Date lastScanDate = task.getLastScanDate();
                    Date startTime = task.getLastStartTime();
                    long lastDur = System.currentTimeMillis() - lastScanDate.getTime();
                    task.setCurSpeed(getSpeedStr(lastDur, curSize-lastSize));
                    long totalDur = System.currentTimeMillis() - startTime.getTime();
                    task.setAverSpeed(getSpeedStr(totalDur, curSize-task.getLastStartSize()));

                    task.setLastScanDate(new Date());
                    task.setLastScanSize(curSize);
                }
            }
            Thread.sleep(2000);
        }

        }catch(Exception e){Log.error(e);}
    }

    private String getSpeedStr(long dur, long i)
    {
        return i*1000/dur/1024 + " Kb/S";
    }


}
