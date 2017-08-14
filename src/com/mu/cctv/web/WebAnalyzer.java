/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv.web;

import com.mu.cctv.program.impl.VodEpisode;
import com.mu.cctv.program.impl.VodProgramInfo;
import com.mu.util.Log;
import com.mu.util.RegexUtil;
import com.mu.util.io.FileUtil;
import com.mu.util.net.WebUtil;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Peng mu
 */
public class WebAnalyzer
{
    static public void main(String[] argu) throws Exception
    {
        //String url = "http://space.dianshiju.cctv.com/podcast/shibingtuji";
        String url = "http://vod.cctv.com/podcast/sbtj";
        String result = WebUtil.getWebPage(url, "GB2312");
        Log.log(result);
        FileUtil.writeFile(result, "C:\\temp\\output-str.txt");/**/
        //WebUtil.downloadFile(url, "C:\\temp\\output-raw.txt");
        /*String result = FileUtil.readTxtFile("C:\\temp\\output-raw.txt");
        //Log.log(w);
        /*String pattern = "<div class=\"text\">\\s*<a href=\"/video/(VIDE[0-9]+)\"" +                   // video id
                                         " +target=\"_blank\" +title *= *\"(.*?)\">" + "";            // title*/

        String pattern = "\"total_page\">([0-9]+)<" +                   // total page
                                         ".*function *toPage_(ELEM[0-9]+)" +            // element id
                                         ".*composePodcastId *= *\"(PODC[0-9]+)\"";     // program id
        ArrayList<String> arr = RegexUtil.getAllUniqueMatch(result, pattern);
        Log.logCollection(arr);

       /* /*Log.log(result.length());
        Log.log(result);
        //FileUtil.writeFile(result, "C:\\temp\\output.txt");*/
        //String re = FileUtil.readTxtFile("C:\\temp\\output.txt");
        //Log.log(re);
        //Log.logCollection(analyze(url, "."));
        //getProgramInfo(url);
        //String re = FileUtil.readTxtFile("C:\\temp\\output-str.txt");
        /*String re = WebUtil.getWebPage(url);
        Log.log(re);
        Log.logCollection(RegexUtil.getAllUniqueMatch(re,"\"([^\"]*?mp4)\""));
        VodEpisode e = new VodEpisode();
        e.setId("VIDE1258131559000634");
        e.initVideoUrl();
        Log.logCollection(e.getHdUrl());
        Log.logCollection(e.getSdUrl());
		ObjectOutputStream oop = new ObjectOutputStream(new FileOutputStream("C:\\temp\\episode"));
		oop.writeObject(e);
		oop.close();*/
        /*
        //getEpisodeList(re);
        /*VodProgramInfo p = getProgramInfo(url);
        ArrayList<VodEpisode> eps = p.getEpisodes();;
        for(VodEpisode e : eps)
            Log.log(e.getId() + ":" + e.getTitle());
        //Log.logCollection(p.getEpisodes());*/
    }

    static public ArrayList<String> analyze(String url, String pattern)
    {
        ArrayList<String> re = new ArrayList<String>();
        try{
            String s = WebUtil.getWebPage(url);
            Log.log(s);
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(s);
            Log.log(m.matches());
            for(int i=0; i<=m.groupCount(); i++)
                re.add(m.group(i));
        }catch(Exception e)
        {
            Log.error(e);
        }

        return re;
    }
/*
    static public VodProgramInfo getProgramInfo(String url, String name)
    {
        //return null;
        VodProgramInfo re = new VodProgramInfo();
        try{
            String webpage = WebUtil.getWebPage(url, "GB2312");
            //Log.log(webpage);
            //FileUtil.writeFile(webpage, "C:\\temp\\output.txt");
            ArrayList<String> arr = RegexUtil.getAllUniqueMatch(webpage, VodProgramInfo.pattern);
            if(arr.size() != 3)
            {
                Log.error("Failed to retrieve Program Info!");
                Log.logCollection(arr);
                return re;
            }
            re.setProgramURL(url);
            re.setTotalPage(Integer.parseInt(arr.get(0)));
            re.setElementId(arr.get(1));
            re.setProgramId(arr.get(2));
            re.setProgramName(name);
            //Log.logCollection(arr);
            Log.log(String.format("Progam %s, ElementId=%s, ProgramId=%s, Total Page=%d", re.getProgramName(), re.getElementId(), re.getProgramId(), re.getTotalPage()));
            //Add episode in first page.
            re.setEpisodes(getEpisodeList(webpage));
            ArrayList<VodEpisode> eplist = re.getEpisodes();
            for(int p=2; p<=re.getTotalPage(); p++)
            {
                Log.log(String.format("Processing page %d ...", p));
                String w = getProgramPage(re, p);
                eplist.addAll(getEpisodeList(w));
                Log.log(String.format("Finished page %d ...", p));
            }
            TreeMap<String, VodEpisode> map = new TreeMap<String, VodEpisode>();
            for(VodEpisode e : eplist)
                map.put(e.getTitle(), e);
            re.setEpisodes(new ArrayList<VodEpisode>(map.values()));
            re.logEpisodes();


        }catch(Exception e)
        {
            Log.error(e);
        }

        return re;
    }

    static public String getProgramPage(VodProgramInfo p, int page) throws Exception
    {
        HashMap<String,String> prop = new HashMap<String,String>();
        prop.put("elementId", p.getElementId());
        prop.put("ownerPodcastId", p.getProgramId());
        prop.put("currpage", String.valueOf(page));
        return WebUtil.getWebPage(VodProgramInfo.getQueryPageUrl(), "GB2312", prop);
    }

    static public ArrayList<VodEpisode> getEpisodeList(String webpageContent)
    {
        ArrayList<VodEpisode> re = new ArrayList<VodEpisode>();
        ArrayList<String> arr = RegexUtil.getAllMatch(webpageContent, VodEpisode.pattern);
        //Log.logCollection(arr);
        for(int i=0; i<arr.size(); i++)
        {
            VodEpisode e = new VodEpisode();
            e.setId(arr.get(i));
            i++;
            e.setTitle(arr.get(i));
            re.add(e);
        }

        return re;
    }
*/
}
