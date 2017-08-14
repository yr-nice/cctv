package com.mu.cctv.program.impl;

import java.io.Serializable;

/**
 *
 * @author Peng mu
 */
public class SpaceEpisode extends BaseEpisode implements Serializable
{

    //space site
    /*protected String pattern = "<div class=\"text\">\\s*<a href=\"/video/(VIDE[0-9]+)\"" +                   // video id
                                         " +target=\"_blank\" +title *= *\"(.*?)\">" + "";            // title
    */
    protected String queryUrl = "http://space.dianshiju.cctv.com/playcfg/flv_info_new.jsp?&videoId=";
    public String getQueryUrl()
    {
        return queryUrl;
    }

}
