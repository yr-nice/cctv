package com.mu.util.net.downloader;

/**
 *
 * @author Peng Mu
 */
public class PrintStatus extends Thread
{
    private DownloadTaskMgr tmgr;
    public PrintStatus(DownloadTaskMgr tmgr)
    {
        this.tmgr=tmgr;
    }
    
    public void run()
    {
        while(true)
        {
            //tmgr.getCurStatus();
            try{sleep(1000);}catch(Exception e){}
        }
    }

}
