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
public interface ProgramInfo
{
    public String getProgramName();
    public void setProgramName(String programName);
    public String getProgramURL();
    public void setProgramURL(String programURL);
    public String getElementId();
    public void setElementId(String elementId);
    public ArrayList<Episode> getEpisodes();
    public void setEpisodes(ArrayList<Episode> episodes);
    public String getProgramId();
    public void setProgramId(String programId);
    public int getTotalPage();
    public void setTotalPage(int totalPage);
    public String getQueryPageUrl();
    public ProgramStatus getStatus();
    public void setStatus(ProgramStatus status);
    public void initInfo();
    public void retrieveEpiUrl();


}
