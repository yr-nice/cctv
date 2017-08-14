/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv;

import com.mu.cctv.cfg.CfgMgr;
import com.mu.cctv.program.impl.VodEpisode;
import com.mu.util.Log;
import com.mu.util.UniqueTimeGenerator;
import com.mu.util.net.downloader.DownloadTask;
import com.mu.util.net.downloader.DownloadTaskMgr;
import com.mu.util.net.downloader.DownloadThrPoolMgr;
import com.mu.util.net.downloader.TaskStatus;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Hello
 */
public class TaskRetriever
{
    static public void main(String[] argu) throws Exception
    {
        //String outputPath = "C:\\temp";
        //String programName = "lengjian";
        //ProgramInfo p = (ProgramInfo)FileUtil.getObjectFromFile(outputPath+File.separator+programName+".full");
        //ArrayList<DownloadTask> list = loadTaskList(CfgMgr.getTaskListPath());
        /*TreeMap<String, DownloadTask> map = new TreeMap<String, DownloadTask>();
        for(DownloadTask t : list)
        {
            map.put(t.getId(), t);
        }
        DownloadTask tmp=null;
        for(DownloadTask t : map.values())
        {
            t.setEnqueueTime(new Date(UniqueTimeGenerator.currentTimeMillis()));
            TaskGenerator.updateTaskStatus(t, CfgMgr.getTaskListPath(), false);
            tmp = t;
        }
        TaskGenerator.updateTaskStatus(tmp, CfgMgr.getTaskListPath());*/
        /*for(DownloadTask t : map.values())
            Log.log(t.getId() + ":"+ t.getEnqueueTime());
*/
        
        /*LinkedBlockingQueue<DownloadTask> queue = new LinkedBlockingQueue<DownloadTask>();
        for(DownloadTask t : list)
            Log.log(String.format("id:%s, enqueueTime:%s", t.getId(), t.getEnqueueTime().getTime()));
        DownloadThrPoolMgr mgr = new DownloadThrPoolMgr("Pool", queue, 1);
        DownloadTaskMgr tmgr = new DownloadTaskMgr(queue, mgr);
        mgr.setTaskMgr(tmgr);
        mgr.startAll();
        for(DownloadTask t : list)
            tmgr.AddTask(t);*/
/**/
        /*ProgramInfo p = TaskGenerator.objFileToProgram(outputPath, programName, false);

        ArrayList<VodEpisode> eps = p.getEpisodes();
        ArrayList<DownloadTask> list = new ArrayList<DownloadTask>();
        for(int i=eps.size()-1; i>=0; i--)
            list.addAll(EpisodeToTask(eps.get(i), outputPath, true));
        
        LinkedBlockingQueue<DownloadTask> queue = new LinkedBlockingQueue<DownloadTask>();
        DownloadThrPoolMgr mgr = new DownloadThrPoolMgr("Pool", queue, 2);
        mgr.startAll();
        
        DownloadTaskMgr tmgr = new DownloadTaskMgr(queue, mgr);
        for(DownloadTask t : list)
            tmgr.AddTask(t);
        */
       /* for(int i=0; i<10000; i++)
        {
            tmgr.getCurStatus();
            Thread.sleep(1000);
        }/**/

    }
    
    static public ArrayList<DownloadTask> EpisodeToTask(VodEpisode e, String path, boolean hd)
    {
        ArrayList<DownloadTask> re = new ArrayList<DownloadTask>();
        ArrayList<String> list;
        if(hd && e.getHdUrl().size()>0)
            list = e.getHdUrl();
        else
            list = e.getSdUrl();
        int i=1;
        for(String url : list)
        {
            DownloadTask t = new DownloadTask();
            t.setUrl(url);
            String id = e.getTitle()+"-"+ i;
            t.setId(id);
            t.setDest(path + File.separator + id+".mp4");
            i++;
            re.add(t);
        }
        return re;        
    }

    static public ArrayList<DownloadTask> loadTaskList(String path)
    {
        ArrayList<DownloadTask> re = new ArrayList<DownloadTask>();
        Properties prop = new Properties();
        TreeMap<Date, DownloadTask> map = new TreeMap<Date, DownloadTask>();

        File f = new File(path);
        try{
        if(f.exists())
        {
            prop.load(new FileInputStream(path));

            for(String url : prop.stringPropertyNames())
            {
                DownloadTask t = new DownloadTask();
                t.setUrl(url);
                String[] arr = prop.getProperty(url).split("\\|");
                t.setId(arr[0]);
                t.setStatusCode(TaskStatus.valueOf(arr[1]));
                t.setDest(arr[2]);
                if(arr.length>3)
                    t.setEnqueueTime(new Date(Long.parseLong(arr[3])));
                else
                    t.setEnqueueTime(new Date(UniqueTimeGenerator.currentTimeMillis()));
                //re.add(t);
                map.put(t.getEnqueueTime(), t);
            }

            //Iterator<String, DownloadTask> it = map.
            /*for(DownloadTask d : map.values())
                Log.log(d.getId());/**/

            re.addAll(map.values());
        }
        }catch(Exception e){Log.log(e);}
        return re;
    }


}
