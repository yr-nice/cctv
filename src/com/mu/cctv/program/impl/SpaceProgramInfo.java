/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv.program.impl;

import com.mu.util.Log;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Peng mu
 */
public class SpaceProgramInfo extends BaseProgramInfo implements Serializable
{
    protected String queryUrl="http://space.dianshiju.cctv.com/act/platform/view/page/showElement.jsp?para_for_refresh=";
    //"http://space.dianshiju.cctv.com/act/block/showViewMsg.jsp?targetId=PAGE1197972550318289&type=page&para_for_refresh=1260847384624";

    protected String episodePattern = "<div class=\"text\">\\s*<a href=\"/video/(VIDE[0-9]+)\"" +                   // video id
                                         " +target=\"_blank\" +title *= *\"(.*?)\">" + "";            // title

    protected String pattern = "\"total_page\">([0-9]+)<" +                   // total page
                                         ".*?function *toPage_(ELEM[0-9]+)" +            // element id
                                         ".*composePodcastId *= *\"(PODC[0-9]+)\"";     // program id

    protected Class episodeClass = SpaceEpisode.class;

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

}
