package com.mu.cctv.db.dao;

import com.mu.cctv.Main;
import com.mu.cctv.db.DBMgr;
import com.mu.cctv.web.CCTVDownloadTask;
import com.mu.util.UniqueTimeGenerator;
import com.mu.util.net.downloader.TaskStatus;
import hello.mu.util.MuLog;
import hello.mu.util.time.DateUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Peng Mu
 */
public class DownloadTaskDAO
{
    public static void main(String[] argu)
    {
        Main.init();
        /*CCTVDownloadTask t = new CCTVDownloadTask();
        t.setUrl("http://www.mu.com/test.txt");
        t.setDest("c:/test.txt");
        t.setEpisodeId("vide123456");
        update(t);
        CCTVDownloadTask t = getByUrl("http://www.mu.com/test.txt");
        t.setCreateTime(new Date());
        MuLog.log(t.getEpisodeId());
        update(t);*/
        ArrayList<CCTVDownloadTask> tasks = DownloadTaskDAO.getWithFilter(null, null, TaskStatus.Waiting_for_Start.toString(), null);

    }
	public static void create(CCTVDownloadTask p)
    {
        create(p.getUrl(), p.getDest(), p.getSize(), p.getDownloaded(), p.getCreateTime(), p.getFinishTime(), p.getStatusCode().toString(), p.isResumable(), p.getEpisodeId());
    }

	public static void create(String url, String dest, long totalSize, long downloadedSize, Date createTime, Date finishTime, String status, boolean resumable, String episodeId)
    {
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("insert into app.download_task " +
                " (url, dest, total_size, downloaded_size, create_time, finish_time, last_update, status, resumable, episode_id, index ) " +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
            p.setObject(1, url);
            p.setObject(2, dest);
            p.setLong(3, totalSize);
            p.setLong(4, downloadedSize);
            if(createTime != null)
                p.setObject(5, DateUtil.dateToTS(createTime));
            else
                p.setNull(5, Types.TIMESTAMP);
            if(finishTime != null)
                p.setObject(6, DateUtil.dateToTS(finishTime));
            else
                p.setNull(6, Types.TIMESTAMP);

            p.setObject(7, new Timestamp(System.currentTimeMillis()));
            p.setObject(8, status);
            p.setBoolean(9, resumable);
            p.setObject(10, episodeId);
            p.setLong(11, UniqueTimeGenerator.currentTimeMillis());
            p.execute();
        }catch(Exception e ){MuLog.log(e);}
    }

	public static CCTVDownloadTask getByUrl(String url)
    {
        CCTVDownloadTask re = null;
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("select * from app.download_task where url=? ");
            p.setObject(1, url);
            ResultSet rs = p.executeQuery();
            re = RsToObject(rs);
        }catch(Exception e ){MuLog.log(e);}
        return re;
    }


	public static ArrayList<CCTVDownloadTask> getWithFilter(String url, String dest, String status, String episodeId)
    {
        ArrayList<CCTVDownloadTask> re = new ArrayList<CCTVDownloadTask>();
        try{
            Connection conn = DBMgr.getConnection();
            String sql = "select * from app.download_task where 1=1 " +
                (url!=null ? String.format(" and url='%s' ", url) : "") +
                (dest!=null ? String.format(" and dest='%s' ", dest) : "") +
                (episodeId!=null ? String.format(" and episode_id='%s' ", episodeId) : "") +
                (status!=null ? String.format(" and status='%s' ", status) : "") +
                 " order by index ";
        MuLog.log(sql);
            
            PreparedStatement p = conn.prepareStatement(sql);
            ResultSet rs = p.executeQuery();
            re = RsToObjectList(rs);
        }catch(Exception e ){MuLog.log(e);}
        return re;
    }

	public static CCTVDownloadTask RsToObject(ResultSet rs)
    {
        CCTVDownloadTask re = null;
        try{
        if(rs.next())
        {
            re = new CCTVDownloadTask();
            re.setUrl(rs.getString("url"));
            re.setDest(rs.getString("dest"));
            re.setSize(rs.getLong("total_size"));
            re.setDownloaded(rs.getLong("downloaded_size"));
            re.setCreateTime(rs.getDate("create_time"));
            re.setFinishTime(rs.getDate("finish_time"));
            //re.setLastStartTime(rs.getDate("last_update"));
            re.setStatusCode(TaskStatus.valueOf(rs.getString("status")));
            re.setResumable(rs.getBoolean("resumable"));
            re.setEpisodeId(rs.getString("episode_id"));
            re.setIndex(rs.getLong("index"));

        }
        }catch(Exception e ){MuLog.log(e);}
        return re;

    }

	public static void update(CCTVDownloadTask pi)
    {
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("update app.download_task " +
                " set url=?, dest=?, total_size=?, downloaded_size=?, create_time=?, finish_time=?, last_update=?, status=?, resumable=?, episode_id=?, index=?  " +
                " where url=? ");
            p.setObject(1, pi.getUrl());
            p.setObject(2, pi.getDest());
            p.setLong(3,   pi.getSize());
            p.setLong(4,   pi.getDownloaded());

            if(pi.getCreateTime() != null)
                p.setObject(5, DateUtil.dateToTS(pi.getCreateTime()));
            else
                p.setNull(5, Types.TIMESTAMP);
            if(pi.getFinishTime() != null)
                p.setObject(6, DateUtil.dateToTS(pi.getFinishTime()));
            else
                p.setNull(6, Types.TIMESTAMP);

            p.setObject(7, new Timestamp(System.currentTimeMillis()));
            p.setObject(8, pi.getStatusCode().toString());
            p.setBoolean(9, pi.isResumable());
            p.setObject(10,pi.getEpisodeId());
            p.setLong(11, pi.getIndex());
            p.setObject(12,pi.getUrl());
            int i = p.executeUpdate();
            if(i==0)
                create(pi);
        }catch(Exception e ){MuLog.log(e);}
    }

	public static void delete(String url)
    {
        try{
        DBMgr.deleteRecord("app.download_task", "url", url);
        }catch(Exception e ){MuLog.log(e);}
    }
	public static void deleteByEpi(String episodeId)
    {
        try{
        DBMgr.deleteRecord("app.download_task", "episode_id", episodeId);
        }catch(Exception e ){MuLog.log(e);}
    }

	public static ArrayList<CCTVDownloadTask> RsToObjectList(ResultSet rs)
    {
        ArrayList<CCTVDownloadTask> re = new ArrayList<CCTVDownloadTask>();
		while (true)
        {
            CCTVDownloadTask p = RsToObject(rs);
            if(p==null)
                break;
            re.add(p);
        }
        return re;
    }

}
