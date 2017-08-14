/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv.program.impl;

import java.io.Serializable;

/**
 *
 * @author Peng mu
 */
public class VodEpisode extends BaseEpisode implements Serializable
{
    //vod site
   /* protected String pattern = "<div class=\"text\"><a href=\"/video/(VIDE[0-9]+)\"" +                   // video id
                                         " +title *= *\"(.*?)\" *target" + "";            // title
    */
    protected String queryUrl = "http://vod.cctv.com/playcfg/flv_info_new.jsp?&videoId=";
    public String getQueryUrl()
    {
        return queryUrl;
    }

}
