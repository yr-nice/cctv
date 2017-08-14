package com.mu.cctv.web;

import com.mu.util.net.downloader.DownloadTask;
import java.io.File;

/**
 *
 * @author Peng Mu
 */
public class CCTVDownloadTask extends DownloadTask
{
    private String episodeId="";

    public String toString()
    {
        StringBuilder re = new StringBuilder();
        re.append("Name: " + getName() + "\n");
        re.append("Episode ID: " + getEpisodeId() + "\n");
        re.append("Dest: " + getDest() + "\n");
        re.append("URL: " + getUrl() + "\n");
        re.append("Size: " + getSize() + "\n");
        re.append("Download Size: " + getDownloaded() + "\n");
        re.append("Resumable: " + isResumable() + "\n");
        re.append("Status : " + getStatusCode() + "\n");
        re.append("Create Time : " + getCreateTime() + "\n");
        re.append("Current Speed : " + getCurSpeed() + "\n");
        re.append("Average Speed : " + getAverSpeed() + "\n");
        return re.toString();
    }


    public String getEpisodeId()
    {
        return episodeId;
    }

    public void setEpisodeId(String episodeId)
    {
        this.episodeId = episodeId;
    }

    public String getName()
    {
        File f = new File(getDest());
        return f.getName();
    }
    
}
