/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv.program;

import java.util.ArrayList;

/**
 *
 * @author Peng mu
 */
public interface Episode
{

    public String getId();
    public void setId(String Id);
    public String getTitle();
    public void setTitle(String title);
    public String getQueryUrl();
    public void setQueryUrl(String queryUrl);
    public ArrayList<String> getHdUrl();
    public void setHdUrl(ArrayList<String> hdUrl);
    public ArrayList<String> getSdUrl();
    public void setSdUrl(ArrayList<String> sdUrl);
    public boolean isUrlRetrieved();
    public void setProgramId(String programId);
    public String getProgramId();
    public EpisodeStatus getStatus();
    public void setStatus(EpisodeStatus status);
    public void setIndex(long i);
    public long getIndex();
    public void initVideoUrl();

}
