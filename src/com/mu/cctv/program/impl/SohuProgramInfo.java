/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv.program.impl;

import com.mu.cctv.cfg.CfgMgr;
import com.mu.cctv.db.dao.ProgramDAO;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.ProgramStatus;
import com.mu.util.Log;
import com.mu.util.RegexUtil;
import com.mu.util.net.WebUtil;
import hello.mu.util.MuLog;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Peng mu
 */
public class SohuProgramInfo extends BaseProgramInfo implements Serializable
{
    protected String queryUrl="http://vod.cctv.com/act/platform/view/page/showElement.jsp?para_for_refresh=";
    protected String episodePattern="\"videoId\":([0-9]+).+?" +          // episode id
                                    "\"videoName\":\"(.+?)\"";            // title

    protected String pattern = "var vid *= *\"([0-9]+)\".*" +    //epi id
                                "var pid *= *\"([0-9]+)\"";      //program id

    protected Class episodeClass = VodEpisode.class;
    public String getQueryPageUrl()
    {
        return queryUrl + System.currentTimeMillis();
    }

    public String getEpisodePattern()
    {
        return episodePattern;
    }

    public Class getEpisodeClass()
    {
        return episodeClass;
    }
    public String getPattern()
    {
        return pattern;
    }


   public void initInfo()
    {
        try{
            if(status.ordinal() < ProgramStatus.Episode_Id_Retrieved.ordinal())
            {
                ProgramDAO.update(this);
                String webpage = WebUtil.getWebPage(programURL);
                //Log.log(webpage);
                //FileUtil.writeFile(webpage, "C:\\temp\\output.txt");
                ArrayList<String> arr = RegexUtil.getAllUniqueMatch(webpage, getPattern());
                if(arr.size() != 2)
                {
                    Log.error("Failed to retrieve Program Info!");
                    Log.logCollection(arr);
                    return;
                }
                String epiId = arr.get(0);
                setProgramId(arr.get(1));
                //Log.logCollection(arr);
                MuLog.log(String.format("Progam %s, programId=%s, epiId=%s", getProgramName(), getProgramId(), epiId));
                //Add episode in first page.
                String epiStr = WebUtil.getWebPage(String.format("http://hot.vrs.sohu.com/vrs_videolist.action?vid=%s&pid=%s", epiId, programId), "GB2312");
                Log.log(epiStr);
                arr = RegexUtil.getAllUniqueMatch(epiStr, episodePattern);
                Log.logCollection(arr);
                //ArrayList<SohuEpisode> seArr = new ArrayList<SohuEpisode>();
                for(int i=0; i<arr.size(); i++)
                {
                    SohuEpisode se = new SohuEpisode();
                    se.setId(arr.get(i));
                    i++;
                    se.setTitle(arr.get(i));
                    se.setProgramId(programId);
                    episodes.add(se);
                }
                //setStatus(ProgramStatus.Basic_Info_Retrieved);
                ProgramDAO.update(this);
            }
        }catch(Exception e)
        {
            Log.error(e);
        }

        return;
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
    static public void main(String[] argu)
    {
        CfgMgr.reload("C:\\Project\\Code\\SelfCreatedTool\\cctv\\config\\config.xml");
        SohuProgramInfo p = new SohuProgramInfo();
        p.setProgramURL("http://tv.sohu.com/20101002/n275400638.shtml");
        p.initInfo();
        p.retrieveEpiUrl();


    }

}
