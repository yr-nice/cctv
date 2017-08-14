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
public class VodProgramInfo extends BaseProgramInfo implements Serializable
{
    protected String queryUrl="http://vod.cctv.com/act/platform/view/page/showElement.jsp?para_for_refresh=";
    protected String episodePattern="<div class=\"text\"><a href=\"/video/(VIDE[0-9]+)\"" +                   // video id
                                         " +title *= *\"(.*?)\" *target" + "";            // title

    protected String pattern = "\"total_page\">([0-9]+)<" +                   // total page
                                         ".*function *toPage_(ELEM[0-9]+)" +            // element id
                                         ".*composePodcastId *= *\"(PODC[0-9]+)\"";     // program id

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


}
