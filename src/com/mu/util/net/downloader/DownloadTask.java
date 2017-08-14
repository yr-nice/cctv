/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.net.downloader;

import com.mu.util.concurrent.Task;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Hello
 */
public class DownloadTask extends Task
{
    private String url = "";
    private String dest = "";
    private String name = "";
    private boolean resumable = true;

    private DownloadTaskMgr job;

    private String curSpeed = "";
    private String averSpeed = "";
    private Date enqueueTime;
    private Date createTime = new Date();
    private Date resumeTime;
    private Date finishTime;
    private long size = 0;
    private long downloaded = 0;
    private long lastScanSize = 0;
    private long lastStartSize = 0;
    private Date lastScanDate = new Date();
    private TaskStatus statusCode = TaskStatus.Suspended;
    private long index = 0;
    private HashMap<String, String> httpHeader = new HashMap<String, String> ();

    public long getIndex()
    {
        return index;
    }

    public void setIndex(long index)
    {
        this.index = index;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        notifyObservers();
    }


    public Date getEnqueueTime()
    {
        return enqueueTime;
    }

    public void setEnqueueTime(Date enqueueTime)
    {
        this.enqueueTime = enqueueTime;
    }

    public long getLastStartSize()
    {
        return lastStartSize;
    }

    public void setLastStartSize(long lastStartSize)
    {
        this.lastStartSize = lastStartSize;
    }



    public Date getLastScanDate()
    {
        return lastScanDate;
    }

    public void setLastScanDate(Date lastScanDate)
    {
        this.lastScanDate = lastScanDate;
    }

    public long getLastScanSize()
    {
        return lastScanSize;
    }

    public void setLastScanSize(long lastScanSize)
    {
        this.lastScanSize = lastScanSize;
    }

    public Date getResumeTime()
    {
        return resumeTime;
    }

    public void setResumeTime(Date resumeTime)
    {
        this.resumeTime = resumeTime;
    }

    public TaskStatus getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(TaskStatus statusCode)
    {
        this.statusCode = statusCode;
        notifyObservers();
    }




    public String getAverSpeed()
    {
        return averSpeed;
    }

    public void setAverSpeed(String averSpeed)
    {
        this.averSpeed = averSpeed;
    }

    public String getCurSpeed()
    {
        return curSpeed;
    }

    public void setCurSpeed(String curSpeed)
    {
        this.curSpeed = curSpeed;
        notifyObservers();
    }

    public String getDest()
    {
        return dest;
    }

    public void setDest(String dest)
    {
        this.dest = dest;
        notifyObservers();
    }

    public Date getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(Date finishTime)
    {
        this.finishTime = finishTime;
        notifyObservers();
    }

    public long getDownloaded()
    {
        return downloaded;
    }

    public void setDownloaded(long downloaded)
    {
        this.downloaded = downloaded;
    }

    public void addDownloaded(long downloaded)
    {
        this.downloaded += downloaded;
    }

    public DownloadTaskMgr getJob()
    {
        return job;
    }

    public void setJob(DownloadTaskMgr job)
    {
        this.job = job;
    }

    public boolean isResumable()
    {
        return resumable;
    }

    public void setResumable(boolean resumable)
    {
        this.resumable = resumable;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
        notifyObservers();
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date startTime)
    {
        this.createTime = startTime;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
        notifyObservers();
    }

    public String getPercentage()
    {
        if(size != 0)
            return "%" + downloaded*100/size;
        else
            return "Unknown";
    }


    public Date getLastStartTime()
    {
        return resumeTime != null ? resumeTime : createTime;
    }

    public HashMap<String, String> getHttpHeader()
    {
        return httpHeader;
    }

    public void setHttpHeader(HashMap<String, String> httpHeader)
    {
        this.httpHeader = httpHeader;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder("Task:\n");
        try{
        for(Field f : this.getClass().getDeclaredFields())
            sb.append(f.getName() + ":" + f.get(this).toString());
        }catch(Exception e){}
        return sb.toString();
    }


}
