/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.net.downloader;

import com.mu.cctv.db.dao.DownloadTaskDAO;
import com.mu.cctv.web.CCTVDownloadTask;
import com.mu.util.concurrent.Thr;
import com.mu.util.concurrent.ThreadPoolMgr;
import hello.mu.util.MuLog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author peng mu
 */
public class DownloaderThr extends Thr
{
    private boolean abortTask = false;
    public static void main(String[] argu) throws Exception
    {
        LinkedBlockingDeque<DownloadTask> queue = new LinkedBlockingDeque<DownloadTask>();
        //DownloadThrPoolMgr mgr = new DownloadThrPoolMgr("test", queue, 1);
        //mgr.startAllThr();
        DownloaderThr t = new DownloaderThr("test1", queue, null);
        t.start();
        DownloadTask tk = new DownloadTask();
        //tk.setUrl("http://203.208.206.19/v.cctv.com/flash/media/qgds/2009/05/qgds_null_20090522_295.mp4");
        tk.setUrl("http://download.textpad.com/download/v53/txpeng531.exe");
        tk.setDest("c:\\temp\\downloaded_file.exe");
        queue.add(tk);
        for(int i=0; i<10000; i++)
        {
            MuLog.log(String.format("%d, status=%s, size=%d, downloaded=%d, percentage=%s",
                i, tk.getStatus(), tk.getSize(), tk.getDownloaded(), tk.getPercentage()));
            Thread.sleep(1000);
        }
     }

    public boolean isAbortTask()
    {
        return abortTask;
    }

    public void setAbortTask(boolean abortTask)
    {
        this.abortTask = abortTask;
    }


    public DownloaderThr(String name, BlockingDeque queue, ThreadPoolMgr mgr)
    {
        super(name, queue, mgr);
        MuLog.log("Downloader Thread "+ this.getName() + " was created!");
    }

    public void run()
    {
        MuLog.log("Downloader Thread "+ this.getName() + " started!");
        bStop = false;
        //try{
        while(!bStop)
        {
            InputStream is = null;
            FileOutputStream fo = null;
            DownloadTask t = null;
            try{
            //do something
            MuLog.log("Thread "+ this.getName() + " is waiting for new task.");
            t = (DownloadTask)queue.take();
            currentTask = t;
            if(t.getStatusCode() == TaskStatus.Finished)
                continue;
            MuLog.log("Thread "+ this.getName() + " took a task.");
            t.setStatusCode(TaskStatus.Connecting_to_Server);
            is = prepareTask(t);
            /*if(is == null)
            {
                mgr.update(this, t);
                continue;
            }*/

            fo = new FileOutputStream(t.getDest(), t.isResumable());
            byte[] buff = new byte[10240];

            int retryCount = 0;
            while(!bStop && t.getStatusCode() == TaskStatus.Start_Downloading && retryCount<5)
            {
                int i = is.read(buff);
                if(i == -1)
                {
                    if(t.getDownloaded() == t.getSize() || retryCount>=5)
                        break;
                    else
                    {
                        MuLog.log("Retry "+(retryCount+1)+"...");
                        fo.close();
                        is.close();
                        Thread.sleep(3*1000);
                        is = prepareTask(t);
                        fo = new FileOutputStream(t.getDest(), t.isResumable());
                        retryCount++;
                        continue;
                    }

                }
                fo.write(buff, 0, i);
                t.addDownloaded(i);
                if(t.getDownloaded()>t.getSize())
                {
                    MuLog.log("Downloaded > TotalSize, Redownload "+(retryCount+1)+"...");
                    fo.close();
                    is.close();
                    Thread.sleep(3*1000);
                    is = prepareTask(t);
                    fo = new FileOutputStream(t.getDest(), t.isResumable());
                    retryCount++;
                    continue;
                }
                if(t.getDownloaded()==t.getSize())
                    break;
                //String spd = getSpeedStr(dur, i);
                //t.setCurSpeed(spd);
            }

            fo.close();
            is.close();

            if(t.getStatusCode() == TaskStatus.Start_Downloading)
            {

                if(bStop)
                {
                    t.setStatusCode(TaskStatus.Waiting_for_Start);
                    queue.addFirst(t);
                }
                else if(t.getDownloaded() != t.getSize())
                    t.setStatusCode(TaskStatus.Failed);
                else
                    t.setStatusCode(TaskStatus.Finished);
            }
            t.setCurSpeed("0");
            DownloadTaskDAO.update((CCTVDownloadTask)t);

            mgr.update(this, t);

            }catch(InterruptedException e)
            {
                try{
                if(fo != null)
                    fo.close();
                if(is != null)
                    is.close();
                if(t != null)
                {
                    t.setStatusCode(TaskStatus.Suspended);
                    t.setCurSpeed("0 Kb/s");
                }
                }catch(Exception x){MuLog.error(x);}

            }
            catch(Exception e){
                t.setStatusCode(TaskStatus.Failed);
                DownloadTaskDAO.update((CCTVDownloadTask)t);
                MuLog.error(e);}
        }
    }

    private String getSpeedStr(long dur, long i)
    {

        return i*1000/dur/1024 + " Kb/S";
    }

    private File prepareFile(String dest)
    {
        File f = new File(dest);
        File parentFolder = f.getParentFile();
        if(!parentFolder.exists())
            parentFolder.mkdirs();
        return f;

    }

    private InputStream prepareTask(DownloadTask t) throws Exception
    {
        URL u = new URL(t.getUrl());
        HttpURLConnection hu = (HttpURLConnection) u.openConnection();
        initTaskHeader(t);
        for(String k : t.getHttpHeader().keySet())
            hu.setRequestProperty(k, t.getHttpHeader().get(k));

        t.setStatusCode(TaskStatus.Initing_Task);

        File f = prepareFile(t.getDest());
        if(f.length()>t.getSize())
        {
            MuLog.info("Remove Corrupted File.");
            f.delete();
        }
        hu.setRequestProperty("RANGE","bytes="+f.length()+"-");
        t.setSize(hu.getContentLength());
        t.setResumable(hu.getHeaderFields().containsKey("Content-Range"));
        t.setStatusCode(TaskStatus.Start_Downloading);
        t.setResumeTime(new Date());
        t.setDownloaded(f.length());
        t.setLastStartSize(f.length());

        MuLog.info(t);

        /*
        if(f.exists())
        {
            long size = f.length();
            t.setDownloaded(size);
            if(size == t.getSize())
            {
                t.setStatusCode(TaskStatus.Finished);
                t.setCurSpeed("0");
                DownloadTaskDAO.update((CCTVDownloadTask)t);
                return null;
            }
            else if(size > t.getSize() && t.getSize()>0)
            {
                t.setStatusCode(TaskStatus.File_Corrupt);
                DownloadTaskDAO.update((CCTVDownloadTask)t);
                //return null;
            }
            else
            {
                MuLog.log("Resume downloanding from "+size);
                hu = (HttpURLConnection)u.openConnection();
                for(String k : t.getHttpHeader().keySet())
                    hu.setRequestProperty(k, t.getHttpHeader().get(k));

                t.setStatusCode(TaskStatus.Start_Downloading);
                t.setResumeTime(new Date());
                t.setDownloaded(size);
                t.setLastStartSize(size);
            }
        }
        else
        {
            MuLog.log("New downloanding ...");
            t.setStatusCode(TaskStatus.Start_Downloading);
            t.setDownloaded(0);
            t.setCreateTime(new Date());
            t.setResumeTime(new Date());

        }*/

        DownloadTaskDAO.update((CCTVDownloadTask)t);
        return hu.getInputStream();
    }

    private void initTaskHeader(DownloadTask t)
    {
        if(t.getUrl().indexOf(".itc.cn")!=-1)
            t.getHttpHeader().put("Referer", "http://tv.sohu.com/upload/swf/20091230/Player.swf");

    }
}
