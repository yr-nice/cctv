/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv;

import com.mu.cctv.db.dao.DownloadTaskDAO;
import com.mu.cctv.db.dao.EpisodeDAO;
import com.mu.cctv.db.dao.ProgramDAO;
import com.mu.cctv.global.Global;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.EpisodeStatus;
import com.mu.cctv.program.ProgramInfo;
import com.mu.cctv.program.ProgramStatus;
import com.mu.cctv.program.ProgramUtil;
import com.mu.cctv.program.impl.SohuEpisode;
import com.mu.cctv.web.CCTVDownloadTask;
import com.mu.util.Log;
import com.mu.util.net.downloader.DownloadTask;
import hello.mu.util.MuLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.String;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Hello
 */
public class TaskGenerator
{
    static public void main(String[] argu) throws Exception
    {
        String outputPath = "D:\\cctv";
        //String programUrl = "http://vod.cctv.com/podcast/sbtj";
        String programUrl = "http://tv.sohu.com/20090116/n261794870.shtml";
        String programName = "xin_jie_hun";
        if(argu.length>2)
        {
            outputPath = argu[0];
            programUrl = argu[1];
            programName = argu[2];
        }

        Main.init();


        //VodProgramInfo p = programToObjFile(outputPath, programUrl, programName);
        //VodProgramInfo p = objFileToProgram(outputPath, programName, true);
        //addProgramToTaskList(p, CfgMgr.getTaskListPath(), outputPath+File.separator+programName,  true);

        /*VodEpisode e = getEpisode("VIDE1245834331682088", "new_26");
        addEpisodeToTaskList(e, CfgMgr.getTaskListPath(), outputPath+File.separator+programName,  true);
        p.logEpisodes();
        ArrayList<VodEpisode> eps = p.getEpisodes();
        for(VodEpisode e : eps)
        {
            Log.log(e.getTitle() + "'s HD url:");
            Log.logCollection(e.getHdUrl());
            Log.log(e.getTitle() + "'s SD url:");
            Log.logCollection(e.getSdUrl());
        }*/
        ProgramInfo p = ProgramUtil.getProgram(programUrl, programName);
        p.initInfo();
        //retrieveAllEpisodes(p);
        p.retrieveEpiUrl();
    }

    static public ProgramInfo addProgram(String name, String url)
    {
        ArrayList<ProgramInfo> arr = ProgramDAO.getWithFilter(null, null, null, url, null, null);
        ProgramInfo p = null;
        if(arr.size()>0)
            p = arr.get(0);
        else
            p = ProgramUtil.getProgram(url, name);
        p.initInfo();
        p.retrieveEpiUrl();
        return p;
    }

    static public Episode addEpisode(String id, String title, String programId, String type)
    {
        Episode p = EpisodeDAO.getByEpisodeId(id);
        if(p == null)
        {
            p = ProgramUtil.typeToEpisode(type);
            p.setId(id);
            p.setTitle(title);
            p.setProgramId(programId);
            p.setStatus(EpisodeStatus.Basic_Info_Retrieved);
            EpisodeDAO.update(p);
        }
        p.initVideoUrl();
        EpisodeDAO.update(p);
        return p;
    }

    static public boolean episodeToTask(String id, String destFolder, boolean hd)
    {
        Episode p = EpisodeDAO.getByEpisodeId(id);
        return episodeToTask(p, destFolder, hd);
    }
    static public boolean episodeToTask(Episode p, String destFolder, boolean hd)
    {
        String id = p.getId();
        if(p == null)
        {
            MuLog.log("Episode does NOT exit. id:"+id);
            return false;
        }
        p.initVideoUrl();
        ArrayList<String> list;
        if(hd && p.getHdUrl().size() > 0)
            list = p.getHdUrl();
        else
            list = p.getSdUrl();
        String destFile = destFolder+File.separator+p.getTitle();

        boolean sohu = false;
        if(ProgramUtil.getType(p).equals(SohuEpisode.class.getSimpleName()))
            sohu = true;
        if(list.size()==1)
        {
            CCTVDownloadTask t = getCCTVDownloadTask(list.get(0), id, destFile+getFileType(list.get(0)));
            DownloadTaskDAO.update(t);
        }
        else
        {
            int count=0;
            for(String url : list)
            {
                count++;
                CCTVDownloadTask t = getCCTVDownloadTask(url, id, destFile+"-"+count+getFileType(url));
                DownloadTaskDAO.update(t);
            }
        }
        p.setStatus(EpisodeStatus.Added_To_Tasklist);
        EpisodeDAO.update(p);
        return true;
    }
    static public boolean programToTask(String id, String destFolder, boolean hd)
    {
        ProgramInfo p = ProgramDAO.getByProgramId(id);
        return programToTask(p, destFolder, hd);
    }

    static public boolean programToTask(ProgramInfo p, String destFolder, boolean hd)
    {
        ArrayList<Episode> list = p.getEpisodes();
        for(Episode e: list)
            if(e.getStatus() != EpisodeStatus.Removed)
                episodeToTask(e, destFolder, hd);
        p.setStatus(ProgramStatus.Added_To_Tasklist);
        return true;
    }


    static private CCTVDownloadTask getCCTVDownloadTask(String url, String episodeId, String dest)
    {
        CCTVDownloadTask t = DownloadTaskDAO.getByUrl(url);
        if(t == null)
            t = new CCTVDownloadTask();
        t.setUrl(url);
        t.setEpisodeId(episodeId);
        t.setDest(dest);
        return t;
    }






/*    static public VodProgramInfo programToObjFile(String outputPath, String url, String programName)
    {
        VodProgramInfo p = null;
        outputPath = prepareProgamFolder(outputPath, programName);

        try{
        p = WebAnalyzer.getProgramInfo(url, programName);
        FileUtil.saveObject2File(p, outputPath+File.separator+p.getProgramName()+".info");
        genEpisodeInfoFile(outputPath+File.separator+p.getProgramName()+".episodes.txt", p.getEpisodes());
        retrieveAllEpisodes(p);
        FileUtil.saveObject2File(p, outputPath+File.separator+p.getProgramName()+".full");
        }catch(Exception e)
        {
            Log.error(e);
        }
        return p;
    }

    static public VodProgramInfo objFileToProgram(String outputPath, String programName, boolean retrieveEpisodeUrl)
    {
        VodProgramInfo p = null;
        outputPath = prepareProgamFolder(outputPath, programName);

        try{
        File f = new File(outputPath+File.separator+programName+".full");
        if(!f.exists())
            f = new File(outputPath+File.separator+programName+".info");

        p = (VodProgramInfo)FileUtil.getObjectFromFile(f.getAbsolutePath());
        p.setEpisodes(filterSortEpi(outputPath+File.separator+programName+".episodes.txt", p.getEpisodes()));
        if(retrieveEpisodeUrl)
        {
            retrieveAllEpisodes(p);
            FileUtil.saveObject2File(p, outputPath+File.separator+p.getProgramName()+".full");
        }
        }catch(Exception e)
        {
            Log.error(e);
        }

        return p;

    }*/

    static public void retrieveAllEpisodes(ProgramInfo p) throws Exception
    {
        ArrayList<Episode> eps = p.getEpisodes();
        int failed=0;
        for(Episode e : eps)
        {
            if(!e.isUrlRetrieved()) failed++;
        }
        Log.log(String.format("Total Episodes:%d, url processed:%d, unprocessed:%d", eps.size(), eps.size()-failed, failed));
        failed=0;
        int count = 0;
        for(Episode e : eps)
        {
            if(!e.isUrlRetrieved())
            {
                e.initVideoUrl();
                count++;
                if(count%40==0)
                    Thread.sleep(10*1000);
            }
            //FileUtil.saveObject2File(e, outputPath+File.separator+e.getTitle()+".episode");
            if(!e.isUrlRetrieved()) failed++;
        }
        Log.log(String.format("Total Episodes:%d, url processed:%d, unprocessed:%d", eps.size(), eps.size()-failed, failed));

    }
/*
    static public String prepareProgamFolder(String outputPath, String programName)
    {
        outputPath += File.separator + programName;
        File folder = new File(outputPath);
        if(!folder.exists())
            folder.mkdirs();

        return outputPath;
    }

    static public void addProgramToTaskList(VodProgramInfo p, String propPath, String downloadFolder,  boolean hd) throws Exception
    {
        Properties prop = Global.prop;
        File f = new File(propPath);
        if(f.exists())
            prop.load(new FileInputStream(propPath));

        ArrayList<VodEpisode> eps = p.getEpisodes();
        int duplicated=0;
        int newtask = 0;
        for(VodEpisode e : eps)
        {
            if(e.isUrlRetrieved())
            {
                ArrayList<String> list = null;
                if(hd && e.getHdUrl().size() > 0)
                    list = e.getHdUrl();
                else
                    list = e.getSdUrl();

                int i=1;
                for(String url : list)
                {
                    //DownloadTask t = new DownloadTask();
                    if(prop.containsKey(url))
                    {
                        Log.log(String.format("url %s is already in task list", url));
                        duplicated++;
                        continue;
                    }

                    String id = e.getTitle()+"-"+ i;
                    String status = TaskStatus.Waiting_for_Start.toString();
                    String dest = downloadFolder+ File.separator + id + getFileType(url);
                    String value = String.format("%s|%s|%s|%s", id, status, dest, UniqueTimeGenerator.currentTimeMillis());
                    prop.put(url, value);
                    i++;
                    newtask++;
                }
            }
        }
        prop.store(new FileOutputStream(propPath), "");

        Log.log(String.format("Total Episodes:%d, new task add:%d, duplicated task:%d", eps.size(), newtask, duplicated));
    }

    static public VodEpisode getEpisode(String id, String title) throws Exception
    {
        VodEpisode re = new VodEpisode();
        re.setId(id);
        re.setTitle(title);
        for(int i=0; i<10; i++)
        {
            if(re.isUrlRetrieved())
                break;
            re.initVideoUrl();
        }
        return re;       
    }
    static public void addEpisodeToTaskList(VodEpisode e, String propPath, String downloadFolder,  boolean hd) throws Exception
    {
        Properties prop = Global.prop;
        File f = new File(propPath);
        if(f.exists())
            prop.load(new FileInputStream(propPath));

        if(e.isUrlRetrieved())
        {
            ArrayList<String> list = null;
            if(hd && e.getHdUrl().size() > 0)
                list = e.getHdUrl();
            else
                list = e.getSdUrl();

            int i=1;
            for(String url : list)
            {
                //DownloadTask t = new DownloadTask();
                if(prop.containsKey(url))
                {
                    Log.log(String.format("url %s is already in task list", url));
                    continue;
                }

                String id = e.getTitle()+"-"+ i;
                String status = TaskStatus.Waiting_for_Start.toString();
                String dest = downloadFolder+ File.separator + id + getFileType(url);
                String value = String.format("%s|%s|%s|%s", id, status, dest, UniqueTimeGenerator.currentTimeMillis());
                prop.put(url, value);
                i++;
            }
        }
        prop.store(new FileOutputStream(propPath), "");

    }
*/
    synchronized static public void updateTaskStatus(DownloadTask t, String propPath) throws Exception
    {
        updateTaskStatus(t, propPath, true);
    }

    synchronized static public void updateTaskStatus(DownloadTask t, String propPath, boolean saveToFile) throws Exception
    {
        Properties prop = Global.prop;
        File f = new File(propPath);
        if(f.exists())
            prop.load(new FileInputStream(propPath));
        String value = String.format("%s|%s|%s|%s", t.getId(), t.getStatusCode().toString(), t.getDest(), t.getEnqueueTime().getTime());
        prop.put(t.getUrl(), value);
        prop.store(new FileOutputStream(propPath), "");

        Log.log(String.format("Task %s status has been updated to %s", t.getId(), t.getStatusCode().toString()));
    }

    static public String getFileType(String url)
    {
        String re = "";
        try{

        url = url.trim();
        Pattern p = Pattern.compile("(\\..{1,3}$)");
        Matcher m = p.matcher(url);
        if(m.find())
            re = m.group(1);
        }catch(Exception e){Log.log(e);}
        return re;
    }
/*

    static public String genEpisodeInfoFile(String path, ArrayList<VodEpisode> episodes)
    {
        String re = "";

        try{
        for(VodEpisode e : episodes)
            re += String.format("Episode %s, Id=%s\n", e.getTitle(), e.getId());

        FileUtil.writeFile(re, path);
        }catch(Exception e){Log.log(e);}

        return re;
    }

    static public ArrayList<VodEpisode> filterSortEpi(String path, ArrayList<VodEpisode> episodes)
    {
        ArrayList<VodEpisode> re = new ArrayList<VodEpisode>();
        try{

        BufferedReader b = new BufferedReader(new FileReader(path));
        StringBuffer buff = new StringBuffer();

        while(b.ready())
        {
            String tmp = b.readLine();
            if(tmp == null)
                break;
            for(VodEpisode e : episodes)
                if(tmp.indexOf(e.getId())!=-1)
                    re.add(e);
        }
        }catch(Exception e){Log.log(e);}
        return re;
    }*/
}
