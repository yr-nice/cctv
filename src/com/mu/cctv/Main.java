package com.mu.cctv;

import com.mu.cctv.cfg.CfgMgr;
import com.mu.cctv.db.DBMgr;
import com.mu.cctv.db.dao.DownloadTaskDAO;
import com.mu.cctv.db.dao.EpisodeDAO;
import com.mu.cctv.global.Global;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.ProgramInfo;
import com.mu.cctv.web.CCTVDownloadTask;
import com.mu.util.net.downloader.DownloadTask;
import com.mu.util.net.downloader.DownloadTaskMgr;
import com.mu.util.net.downloader.DownloadThrPoolMgr;
import com.mu.util.net.downloader.PrintStatus;
import com.mu.util.net.downloader.TaskStatus;
import hello.mu.util.MuLog;
import hello.mu.util.ProcessExecuter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author Peng Mu
 */
public class Main
{
    static private DownloadThrPoolMgr mgr;
    static private DownloadTaskMgr tmgr;
    static private PrintStatus ps;

    static public void main(String[] argu) throws Exception
    {
        //argu = {"-add-program", "shi_bing", "http://space.dianshiju.cctv.com/podcast/shibingtuji"};
        init();
        //TaskGenerator.addProgram("shi_bing", "http://vod.cctv.com/podcast/sbtj");
        //TaskGenerator.addEpisode("VIDE1222818983000695", "shi", "PODC1207896540749901", TypeEnum.SpaceEpisode.toString());
        //TaskGenerator.episodeToTask("VIDE1222818983000695", "D:\\cctv\\shi_bing_tu_ji", true);
        startDownload();

        Thread.sleep(5*1000);
        stopDownload();
        
        /*
        if(argu.length<1)
        {
            MuLog.log("Incomplete arguments! Pls put in <Action> <Params...>");
        }
        else if(argu[0].compareToIgnoreCase("-add-program")==0)
        {
            TaskGenerator.addProgram(argu[1], argu[2]);
        }
        /*else if(argu[0].compareToIgnoreCase("-add-episode")==0)
        {
            TaskGenerator.addEpisode();
        }
        else if(argu[0].compareToIgnoreCase("-program-status")==0)
        {
            TaskGenerator.showProgramStatus();
        }
        else if(argu[0].compareToIgnoreCase("-episode-status")==0)
        {
            TaskGenerator.showEpisodeStatus();
        }*/
    }

    static public void init()
    {
        initBaseDir();
        initLog();
        initCfg();
        initDB();

    }

    static private void initBaseDir()
    {
        Global.baseDir = System.getProperty("user.dir");
    }
    static private void initLog()
    {
        String cfg =  Global.baseDir + "\\config\\" + "mulog.property";
        MuLog.init(cfg, "mu");
        MuLog.log("Logger is ready!");
    }

    static private void initCfg()
    {
        CfgMgr.reload(Global.baseDir+File.separator+"config"+File.separator+"config.xml");
    }

    static private void initDB()
    {
        DBMgr.init();
    }

    static public void startDownload()
    {
        ArrayList<CCTVDownloadTask> tasks = DownloadTaskDAO.getWithFilter(null, null, TaskStatus.Waiting_for_Start.toString(), null);
        startDownload(tasks);
    }

    static public void startDownload(ArrayList<CCTVDownloadTask> tasks)
    {
        LinkedBlockingDeque<DownloadTask> queue = new LinkedBlockingDeque<DownloadTask>();
        for(CCTVDownloadTask t : tasks)
            MuLog.log(String.format("id:%s, dest:%s", t.getName(), t.getDest()));
        if(mgr==null)
        {
            mgr = new DownloadThrPoolMgr("Pool", queue, 1);
            mgr.setTaskMgr(tmgr);
            mgr.startAllThr();
        }
        if(tmgr == null)
            tmgr = new DownloadTaskMgr(queue, mgr);

        for(CCTVDownloadTask t : tasks)
            tmgr.AddTask(t);

        if(ps == null)
        {
            ps = new PrintStatus(tmgr);
            ps.setDaemon(true);
            ps.start();
        }

    }

    static public void stopDownload()
    {
        if(tmgr != null)
            tmgr.stopAll();
    }

    static public void stopDownload(ArrayList<CCTVDownloadTask> arr)
    {
        if(tmgr != null)
        {
            CCTVDownloadTask running = null;
            for(CCTVDownloadTask t : arr)
            {
                if(t.getStatusCode() != TaskStatus.Start_Downloading)
                    tmgr.removeTask(t);
                else
                    running = t;
            }
            if(running != null)
                tmgr.removeTask(running);
        }
    }

    static public void addDownloader()
    {
        if(mgr != null)
        {
            int i = mgr.getPoolSize();
            mgr.resizePool(i+1);
        }
    }
    static public void reduceDownloader()
    {
        if(mgr != null)
        {
            int i = mgr.getPoolSize();
            if(i>0)
                mgr.resizePool(i-1);
        }
    }

    static public void combineEpisode(Episode e)
    {
        try{
        ArrayList<CCTVDownloadTask> list = DownloadTaskDAO.getWithFilter(null, null, null, e.getId());
        if(list.size()==0)
        {
            MuLog.info("Episode is not Finished, Skip First");
            return;
        }

        File progFolder = (new File(list.get(0).getDest())).getParentFile();
        File outMp4 = new File(progFolder.getAbsolutePath()+File.separator+e.getTitle()+".mp4");
        File batFile = new File(progFolder, e.getTitle()+".bat");
        if(!outMp4.exists())
        {
            FileOutputStream bat = new FileOutputStream(batFile);

            StringBuilder sp = new StringBuilder(Global.baseDir + "\\mp4tools\\mp4box ");
            for(CCTVDownloadTask t : list)
            {
                if( t.getStatusCode()!=TaskStatus.Finished)
                {
                    MuLog.info("Episode is not Finished, Skip First");
                    return;
                }

                sp.append("-cat \""+t.getDest()+"\" ");
            }
            sp.append("\""+outMp4.getAbsolutePath()+"\" ");
            bat.write(sp.toString().getBytes("GB2312"));
            bat.close();

            MuLog.log("Start to combine media files...");
            int i = ProcessExecuter.exec(batFile.getAbsolutePath(), true);
            MuLog.log("Finished combining media files. Return code="+i);
        }
        if(outMp4.exists())
        {
            File processed = new File(progFolder, "processed");
            if(!processed.exists()) processed.mkdirs();

            batFile.renameTo(new File(processed, batFile.getName()));

            for(CCTVDownloadTask t : list)
            {
                File f = new File(t.getDest());
                f.renameTo(new File(processed, f.getName()));
            }
        }
        }catch(Exception x)
        {
            MuLog.error(x);
        }
    }
    static public void combineProgram(ProgramInfo p)
    {
        ArrayList<Episode> list = EpisodeDAO.getWithFilter(null, null, p.getProgramId(), null, null);
        for(Episode e : list)
            combineEpisode(e);
    }

    static public void redownload(ArrayList<CCTVDownloadTask> tasks)
    {
        stopDownload(tasks);
        for(CCTVDownloadTask t : tasks)
        {
            File f = new File(t.getDest());
            if(f.exists())
                f.delete();
            t.setStatusCode(TaskStatus.Suspended);
        }
        startDownload(tasks);
    }

}   
