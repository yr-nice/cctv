package com.mu.cctv.program.impl;

import com.mu.cctv.db.dao.EpisodeDAO;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.EpisodeStatus;
import com.mu.util.Log;
import com.mu.util.RegexUtil;
import com.mu.util.net.WebUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 *
 * @author Peng mu
 */
public class BaseEpisode extends Observable implements Serializable, Episode
{

    //space site
    protected String queryUrl = "";

    protected String Id = "";
    protected String title = "";
    protected String programId = "";
    protected EpisodeStatus status = EpisodeStatus.Init;
    protected ArrayList<String> hdUrl = new ArrayList<String> ();
    protected ArrayList<String> sdUrl = new ArrayList<String> ();
    protected long index = 0;

    public String toString()
    {
        StringBuilder re = new StringBuilder();
        re.append("Name: " + getTitle() + "\n");
        re.append("ID: " + getId() + "\n");
        re.append("Program ID: " + getProgramId() + "\n");
        re.append("Status: " + getStatus() + "\n");
        re.append("HD URL: \n");
        for(String s : getHdUrl())
            re.append("\t"+s+"\n");

        re.append("SD URL: \n");
        for(String s : getSdUrl())
            re.append("\t"+s+"\n");
        return re.toString();
    }

    static public void main(String[] argu)
    {
        BaseEpisode e = new BaseEpisode();
        e.setId("VIDE1219893972000408");
        e.initVideoUrl();
        Log.logCollection(e.getHdUrl());
        Log.logCollection(e.getSdUrl());

    }

    public void notifyObservers()
    {
        super.setChanged();
        super.notifyObservers();
    }
    public EpisodeStatus getStatus()
    {
        return status;
    }

    public void setStatus(EpisodeStatus status)
    {
        this.status = status;
        notifyObservers();
    }

    public String getProgramId()
    {
        return programId;
    }

    public void setProgramId(String programId)
    {
        this.programId = programId;
        notifyObservers();
    }

    public String getId()
    {
        return Id;
    }

    public void setId(String Id)
    {
        this.Id = Id;
        notifyObservers();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
        notifyObservers();
    }

    public long getIndex()
    {
        return index;
    }

    public void setIndex(long i)
    {
        index = i;
        notifyObservers();
    }

    public void initVideoUrl()
    {
        if(status.ordinal() < EpisodeStatus.Url_Retrieved.ordinal())
        {
            Log.log("Start to retrieve downloading url for episode " + this.title);
            try{
            String re = WebUtil.getWebPage(getQueryUrl() + this.getId());
            re = preprocess(re);
            String hd = getHDStr(re);
            //Log.log(hd);
            if(!hd.isEmpty())
            {
                ArrayList<String> l = RegexUtil.getAllUniqueMatch(hd, "\"([^\"]*?mp4)\"");
                if(l.size()==0)
                    l = RegexUtil.getAllUniqueMatch(hd, "\"([^\"]*?flv)\"");
                hdUrl.clear();
                for(String s : l)
                    hdUrl.add("http://203.208.206.19/v.cctv.com/flash/" + s);
            }
            String sd = getSDStr(re);
            //Log.log(sd);
            if(!sd.isEmpty())
            {
                ArrayList<String> l = RegexUtil.getAllUniqueMatch(sd, "\"([^\"]*?mp4)\"");
                if(l.size()==0)
                    l = RegexUtil.getAllUniqueMatch(sd, "\"([^\"]*?flv)\"");
                sdUrl.clear();
                for(String s : l)
                    sdUrl.add("http://203.208.206.19/v.cctv.com/flash/" + s);
            }
            Log.log(this.title + "'s HD url:");
            Log.logCollection(hdUrl);
            Log.log(this.title + "'s SD url:");
            Log.logCollection(sdUrl);

            Log.log("Finished retrieving url for episode " + this.title);

            }catch(Exception e) {Log.error(e);}

            if(isUrlRetrieved())
            {
                status = EpisodeStatus.Url_Retrieved;
                EpisodeDAO.update(this);
            }
        }
        notifyObservers();

    }

    public ArrayList<String> getHdUrl()
    {
        return hdUrl;
    }

    public void setHdUrl(ArrayList<String> hdUrl)
    {
        this.hdUrl = hdUrl;
    }

    public String getQueryUrl()
    {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl)
    {
        this.queryUrl = queryUrl;
    }

    public ArrayList<String> getSdUrl()
    {
        return sdUrl;
    }

    public void setSdUrl(ArrayList<String> sdUrl)
    {
        this.sdUrl = sdUrl;
    }

    private String preprocess(String queryResult)
    {
        queryResult = queryResult.replaceAll("\"embed\".*", "");
        return queryResult;
    }
    private String getHDStr(String str) throws Exception
    {
        ArrayList<String> arr = RegexUtil.getAllUniqueMatch(str, "\"chapters2\":\\[(.*?)\\]");
        if(arr.size()>0)
            return arr.get(0);
        else
            return "";
    }

    private String getSDStr(String str) throws Exception
    {
        ArrayList<String> arr = RegexUtil.getAllUniqueMatch(str, "\"chapters\":\\[(.*?)\\]");
        if(arr.size()>0)
            return arr.get(0);
        else
            return "";
    }

    public boolean isUrlRetrieved()
    {
        if(getHdUrl().size()==0 && getSdUrl().size()==0)
            return false;
        else
            return true;

    }
}
