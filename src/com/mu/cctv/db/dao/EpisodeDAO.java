package com.mu.cctv.db.dao;

import com.mu.cctv.Main;
import com.mu.cctv.db.DBMgr;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.EpisodeStatus;
import com.mu.cctv.program.ProgramUtil;
import com.mu.util.UniqueTimeGenerator;
import hello.mu.util.MuLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Peng Mu
 */
public class EpisodeDAO
{
    static private final String urlSeparator = " ";
    public static void main(String[] argu)
    {
        Main.init();
        Episode p = getByEpisodeId("VIDE1222818983000695");
        MuLog.log(p.getHdUrl().get(0));
        /*
        String a="ad cd de";
        String[] arr = a.split("\\s+");
        MuLog.log(arr.length+"");
        MuLog.log(">"+arr[2]+"<");*/


    }
	public static void create(Episode p)
    {
        create(p.getId(), p.getProgramId(), p.getTitle(), p.getHdUrl(), p.getSdUrl(), ProgramUtil.getType(p), p.getStatus().toString());
    }

	public static void create(String episodeId, String programId, String title, ArrayList<String> hdURL, ArrayList<String> sdURL, String type, String status)
    {
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("insert into app.episode " +
                " (episode_id, program_id, title, hd_url, sd_url, type, status, index) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?) ");
            p.setObject(1, episodeId);
            p.setObject(2, programId);
            p.setObject(3, title);
            p.setObject(4, urlArrToStr(hdURL));
            p.setObject(5, urlArrToStr(sdURL));
            p.setObject(6, type);
            p.setObject(7, status);
            p.setLong(8, UniqueTimeGenerator.currentTimeMillis());
            p.execute();
        }catch(Exception e ){MuLog.log(e);}
    }

    private static String urlArrToStr(ArrayList<String> arr)
    {
        MuLog.log("Length: "+arr.size());
        StringBuffer re=new StringBuffer("");
        if(arr != null)
        {
            for(String s : arr)
            {
                re.append(s);
                re.append(" ");
            }
        }
        MuLog.log(re.toString());
        return re.toString().trim();
    }
    private static ArrayList<String> strToUrlArr(String str)
    {
        ArrayList<String> re=new ArrayList<String>();
        String[] arr = str.split(urlSeparator);
        if(arr.length>1 || !arr[0].isEmpty())
            re.addAll(Arrays.asList(arr));
        return re;
    }

	public static Episode getByEpisodeId(String episodeId)
    {
        Episode re = null;
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("select * from app.episode  where episode_id=? ");
            p.setObject(1, episodeId);
            ResultSet rs = p.executeQuery();
            re = RsToObject(rs);
        }catch(Exception e ){MuLog.log(e);}
        return re;
    }


	public static ArrayList<Episode> getWithFilter(String status, String episodeId, String programId, String title, String type)
    {
        ArrayList<Episode> re = new ArrayList<Episode>();
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("select * from app.episode where 1=1 " +
                (status!=null ? String.format(" and status='%s' ", status) : "") +
                (episodeId!=null ? String.format(" and episode_id='%s' ", episodeId) : "") +
                (programId!=null ? String.format(" and program_id='%s' ", programId) : "") +
                (title!=null ? String.format(" and title='%s' ", title) : "") +
                (type!=null ? String.format(" and type='%s' ",type) : "") +
                " order by index "
                );
            ResultSet rs = p.executeQuery();
            re = RsToObjectList(rs);
        }catch(Exception e ){MuLog.log(e);}
        return re;
    }

	public static Episode RsToObject(ResultSet rs)
    {
        Episode re = null;
        try{
        if(rs.next())
        {
            re = ProgramUtil.typeToEpisode(rs.getString("type"));
            re.setId(rs.getString("episode_id"));
            re.setProgramId(rs.getString("program_id"));
            re.setTitle(rs.getString("title"));
            re.setHdUrl(strToUrlArr(rs.getString("hd_url")));
            re.setSdUrl(strToUrlArr(rs.getString("sd_url")));
            re.setStatus(EpisodeStatus.valueOf(rs.getString("status")));
            re.setIndex(rs.getLong("index"));

        }
        }catch(Exception e ){MuLog.log(e);}
        return re;

    }

	public static void update(Episode pi)
    {
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("update app.episode " +
                " set episode_id=?, program_id=?, title=?, hd_url=?, sd_url=?, status=?, index=? " +
                " where episode_id=? ");
            p.setObject(1, pi.getId());
            p.setObject(2, pi.getProgramId());
            p.setObject(3, pi.getTitle());
            p.setObject(4, urlArrToStr(pi.getHdUrl()));
            p.setObject(5, urlArrToStr(pi.getSdUrl()));
            p.setObject(6, pi.getStatus().toString());
            p.setLong(7, pi.getIndex());
            p.setObject(8, pi.getId());
            int i = p.executeUpdate();
            if(i==0)
                create(pi);
        }catch(Exception e ){MuLog.log(e);}
    }

	public static void delete(String episodeId)
    {
        try{
        DBMgr.deleteRecord("app.episode", "episode_id", episodeId);
        }catch(Exception e ){MuLog.log(e);}
    }

	public static ArrayList<Episode> RsToObjectList(ResultSet rs)
    {
        ArrayList<Episode> re = new ArrayList<Episode>();
		while (true)
        {
            Episode p = RsToObject(rs);
            if(p==null)
                break;
            re.add(p);
        }
        return re;
    }

}
