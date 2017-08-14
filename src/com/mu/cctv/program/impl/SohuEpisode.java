package com.mu.cctv.program.impl;

import com.mu.cctv.db.dao.EpisodeDAO;
import com.mu.cctv.program.EpisodeStatus;
import com.mu.util.Log;
import com.mu.util.RegexUtil;
import com.mu.util.net.WebUtil;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Peng mu
 */
public class SohuEpisode extends BaseEpisode implements Serializable
{

    //space site
    /*protected String pattern = "<div class=\"text\">\\s*<a href=\"/video/(VIDE[0-9]+)\"" +                   // video id
                                         " +target=\"_blank\" +title *= *\"(.*?)\">" + "";            // title
    */
    protected String queryUrl = "http://hot.vrs.sohu.com/vrs_flash.action?vid=";
    public String getQueryUrl()
    {
        return queryUrl;
    }

    static public void main(String[] argu)
    {
        SohuEpisode e = new SohuEpisode();
        e.setId("159651");
        e.initVideoUrl();
        Log.logCollection(e.getHdUrl());
        Log.logCollection(e.getSdUrl());

    }

    public void initVideoUrl()
    {
        if(status.ordinal() < EpisodeStatus.Url_Retrieved.ordinal())
        {
            Log.log("Start to retrieve downloading url for episode " + this.title);
            try{
            String re = WebUtil.getWebPage(getQueryUrl() + this.getId(), "GB2312");
            Log.log(re);
            ArrayList<String> l = RegexUtil.getAllUniqueMatch(re, "\"(http[^\"\\s]+?mp4|flv)\"");
            Log.log(l);
            hdUrl.clear();
            for(String s : l)
                hdUrl.add(s);

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


}
