package com.mu.cctv.web;

import com.mu.util.concurrent.ThreadPoolMgr;
import com.mu.util.net.downloader.DownloaderThr;
import java.util.concurrent.BlockingDeque;

/**
 *
 * @author Peng Mu
 */
public class DownloaderPoolMgr<T extends DownloaderThr> extends ThreadPoolMgr
{
    public DownloaderPoolMgr(String poolName, BlockingDeque queue, int thrNo, Class threadClass)
    {
        super(poolName, queue, thrNo, threadClass);
    }

    public void stopCurTasks()
    {
        for(Object thr : pool.values())
            ((T)thr).setAbortTask(true);
    }

}
