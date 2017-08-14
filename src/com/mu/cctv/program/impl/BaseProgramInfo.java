/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv.program.impl;

import com.mu.cctv.cfg.CfgMgr;
import com.mu.cctv.db.dao.EpisodeDAO;
import com.mu.cctv.db.dao.ProgramDAO;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.EpisodeStatus;
import com.mu.cctv.program.ProgramInfo;
import com.mu.cctv.program.ProgramStatus;
import com.mu.cctv.program.ProgramUtil;
import com.mu.util.Log;
import com.mu.util.RegexUtil;
import com.mu.util.net.WebUtil;
import hello.mu.util.MuLog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author Peng mu
 */
public class BaseProgramInfo implements Serializable, ProgramInfo
{
    protected String elementId = "";
    protected String programId = "";
    protected String programURL = "";
    protected String programName = "";
    protected ProgramStatus status = ProgramStatus.Init;
    private int totalPage = 0;
    protected ArrayList<Episode> episodes = new ArrayList<Episode>();

    protected String pattern = "";
    protected String queryUrl = "";
    protected String episodePattern = "";
    protected Class episodeClass;


    public String toString()
    {
        StringBuilder re = new StringBuilder();
        re.append("Name: " + getProgramName() + "\n");
        re.append("ID: " + getProgramId() + "\n");
        re.append("Element ID: " + getElementId() + "\n");
        re.append("URL: " + getProgramURL() + "\n");
        re.append("Total Page: " + getTotalPage() + "\n");
        re.append("Episode No: " + getEpisodes().size() + "\n");
        re.append("Status: " + getStatus() + "\n");
        re.append("Query URL: " + getQueryPageUrl() + "\n");
        re.append("Type: " + ProgramUtil.getType(this) + "\n");
        return re.toString();
    }




    public ProgramStatus getStatus()
    {
        return status;
    }

    public void setStatus(ProgramStatus status)
    {
        this.status = status;
    }

    public Class getEpisodeClass()
    {
        return episodeClass;
    }

    public void setEpisodeClass(Class episodeClass)
    {
        this.episodeClass = episodeClass;
    }

    public String getEpisodePattern()
    {
        return episodePattern;
    }

    public void setEpisodePattern(String episodePattern)
    {
        this.episodePattern = episodePattern;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }


    public String getProgramName()
    {
        return programName;
    }

    public void setProgramName(String programName)
    {
        this.programName = programName;
    }

    public String getProgramURL()
    {
        return programURL;
    }

    public void setProgramURL(String programURL)
    {
        this.programURL = programURL;
    }


    public String getElementId()
    {
        return elementId;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public ArrayList<Episode> getEpisodes()
    {
        return episodes;
    }

    public void setEpisodes(ArrayList<Episode> episodes)
    {
        this.episodes = episodes;
    }

    public String getProgramId()
    {
        return programId;
    }

    public void setProgramId(String programId)
    {
        this.programId = programId;
    }

    public int getTotalPage()
    {
        return totalPage;
    }

    public void setTotalPage(int totalPage)
    {
        this.totalPage = totalPage;
    }

    public String getQueryPageUrl()
    {
        return queryUrl + System.currentTimeMillis();
    }

    public void logEpisodes()
    {
        Log.log(episodes.size() + " Episodes found.");
        for(Episode e : episodes)
            Log.log(String.format("Episode %s, Id=%s", e.getTitle(), e.getId()));

    }

    public void initInfo()
    {
        try{
            if(status.ordinal() < ProgramStatus.Basic_Info_Retrieved.ordinal())
            {
                ProgramDAO.update(this);
                String webpage = WebUtil.getWebPage(programURL, "GB2312");
                //Log.log(webpage);
                //FileUtil.writeFile(webpage, "C:\\temp\\output.txt");
                ArrayList<String> arr = RegexUtil.getAllUniqueMatch(webpage, getPattern());
                if(arr.size() != 3)
                {
                    Log.error("Failed to retrieve Program Info!");
                    Log.logCollection(arr);
                    return;
                }
                setTotalPage(Integer.parseInt(arr.get(0)));
                setElementId(arr.get(1));
                setProgramId(arr.get(2));
                //Log.logCollection(arr);
                MuLog.log(String.format("Progam %s, ElementId=%s, ProgramId=%s, Total Page=%d", getProgramName(), getElementId(), getProgramId(), getTotalPage()));
                //Add episode in first page.
                setEpisodes(getEpisodeList(webpage));

                setStatus(ProgramStatus.Basic_Info_Retrieved);
                ProgramDAO.update(this);
            }

            if(status.ordinal() < ProgramStatus.Episode_Id_Retrieved.ordinal())
            {
                ArrayList<Episode> eplist = getEpisodes();
                for(int p=2; p<=getTotalPage(); p++)
                {
                    Log.log(String.format("Processing page %d ...", p));
                    String w = getProgramPage(p);
                    eplist.addAll(getEpisodeList(w));
                    Log.log(String.format("Finished page %d ...", p));
                }
                TreeMap<String, Episode> map = new TreeMap<String, Episode>();
                for(Episode e : eplist)
                    map.put(e.getTitle(), e);
                setEpisodes(new ArrayList<Episode>(map.values()));
                logEpisodes();

                setStatus(ProgramStatus.Episode_Id_Retrieved);
                ProgramDAO.update(this);
            }


        }catch(Exception e)
        {
            Log.error(e);
        }

        return;
    }

    private String getProgramPage(int page) throws Exception
    {
        HashMap<String,String> prop = new HashMap<String,String>();
        prop.put("elementId", getElementId());
        prop.put("ownerPodcastId", getProgramId());
        prop.put("currpage", String.valueOf(page));
        return WebUtil.getWebPage(this.getQueryPageUrl(), "GB2312", prop);
    }

    private ArrayList<Episode> getEpisodeList(String webpageContent)
    {
        ArrayList<Episode> re = new ArrayList<Episode>();
        ArrayList<String> arr = RegexUtil.getAllMatch(webpageContent, getEpisodePattern());
        //Log.logCollection(arr);
        for(int i=0; i<arr.size(); i++)
        {
            try{
            String id = arr.get(i);
            i++;
            Episode e = EpisodeDAO.getByEpisodeId(id);
            if(e == null)
            {
                e = (Episode)getEpisodeClass().getConstructor().newInstance();
                e.setId(id);
                e.setTitle(arr.get(i));
                e.setProgramId(programId);
                e.setStatus(EpisodeStatus.Basic_Info_Retrieved);
            }
            re.add(e);
            }catch(Exception e){MuLog.log(e);}
        }

        return re;
    }

    public void retrieveEpiUrl()
    {
        if(status.ordinal() < ProgramStatus.Episode_Url_Retieved.ordinal())
        {
            ArrayList<Episode> eps = getEpisodes();
            int failed=0;
            for(Episode e : eps)
            {
                if(!e.isUrlRetrieved()) failed++;
            }
            Log.log(String.format("Total Episodes:%d, url processed:%d, unprocessed:%d", eps.size(), eps.size()-failed, failed));
            failed=0;
            int count = 0;
            int retry = 0;
            while(retry < CfgMgr.getParamInt("episode_url_retry"))
            {
                for(Episode e : eps)
                {
                    if(!e.isUrlRetrieved())
                    {
                        e.initVideoUrl();
                        count++;
                    }
                    //FileUtil.saveObject2File(e, outputPath+File.separator+e.getTitle()+".episode");
                    if(!e.isUrlRetrieved()) failed++;
                }
                if(failed == 0) break;
                retry++;
            }
            Log.log(String.format("Total Episodes:%d, url processed:%d, unprocessed:%d", eps.size(), eps.size()-failed, failed));
            if(failed == 0)
            {
                status = ProgramStatus.Episode_Url_Retieved;
                ProgramDAO.update(this);
            }
        }

    }

}
