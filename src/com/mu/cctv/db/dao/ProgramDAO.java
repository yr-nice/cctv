package com.mu.cctv.db.dao;

import com.mu.cctv.Main;
import com.mu.cctv.db.DBMgr;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.ProgramInfo;
import com.mu.cctv.program.ProgramStatus;
import com.mu.cctv.program.ProgramUtil;
import com.mu.cctv.program.impl.SpaceProgramInfo;
import hello.mu.util.MuLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Peng Mu
 */
public class ProgramDAO
{
    public static void main(String[] argu)
    {
        Main.init();
        ProgramInfo p = new SpaceProgramInfo();
        p.setProgramName("test4");
        p.setProgramId("prog4");
        
        update(p);/**/

        //create("elem3", "prog2", "http://www.google.com", "test2", 1, "VodProgramInfo");
        //ProgramInfo p = getByProgramId("prog3");
        MuLog.log(p.getProgramName());
        MuLog.log(p.getClass().getName());

        /*p.setTotalPage(3);
        update(p);*/
    }
	public static void create(ProgramInfo p)
    {
        create(p.getElementId(), p.getProgramId(), p.getProgramURL(), p.getProgramName(), p.getTotalPage(), ProgramUtil.getType(p), p.getStatus().toString());
    }
	public static void create(String elementId, String programId, String url, String name, int totalPage, String type, String status)
    {
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("insert into app.program " +
                " (element_id, program_id, url, name, total_page, type, status) " +
                " values (?, ?, ?, ?, ?, ?, ?) ");
            p.setObject(1, elementId);
            p.setObject(2, programId);
            p.setObject(3, url);
            p.setObject(4, name);
            p.setInt(5, totalPage);
            p.setObject(6, type);
            p.setObject(7, status);
            p.execute();
        }catch(Exception e ){MuLog.log(e);}
    }

	public static ProgramInfo getByProgramId(String programId)
    {
        return getByProgramId(programId, true);
    }
	public static ProgramInfo getByProgramId(String programId, boolean loadEpisode)
    {
        ProgramInfo re = null;
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("select * from app.program  where program_id=? ");
            p.setObject(1, programId);
            ResultSet rs = p.executeQuery();
            re = RsToObject(rs, loadEpisode);
        }catch(Exception e ){MuLog.log(e);}
        return re;
    }
	public static ArrayList<ProgramInfo> getWithFilter(String status, String programId, String elementId, String url, String name, String type)
    {
        return getWithFilter(status, programId, elementId, url, name, type, true);
    }

	public static ArrayList<ProgramInfo> getWithFilter(String status, String programId, String elementId, String url, String name, String type, boolean loadEpisode )
    {
        ArrayList<ProgramInfo> re = new ArrayList<ProgramInfo>();
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("select * from app.program  where 1=1 " +
                (status!=null ? String.format(" and status='%s' ", status) : "") +
                (programId!=null ? String.format(" and program_id='%s' ", programId) : "") +
                (elementId!=null ? String.format(" and element_id='%s' ", elementId) : "") +
                (url!=null ? String.format(" and url='%s' ",url) : "") +
                (name!=null ? String.format(" and name='%s' ",name) : "") +
                (type!=null ? String.format(" and type='%s' ",type) : "")
                );
            ResultSet rs = p.executeQuery();
            re = RsToObjectList(rs, loadEpisode);
        }catch(Exception e ){MuLog.log(e);}
        return re;
    }

	private static ProgramInfo RsToObject(ResultSet rs, boolean loadEpisode)
    {
        ProgramInfo re = null;
        try{
        if(rs.next())
        {
            String url = rs.getString("url");
            String name = rs.getString("name");
            //re = ProgramUtil.getProgram(url, name);
            re = ProgramUtil.typeToProgram(rs.getString("type"));
            re.setProgramURL(url);
            re.setProgramName(name);
            re.setElementId(rs.getString("element_id"));
            re.setProgramId(rs.getString("program_id"));
            re.setTotalPage(rs.getInt("total_page"));
            re.setStatus(ProgramStatus.valueOf(rs.getString("status")));
            if(loadEpisode)
                re.setEpisodes(EpisodeDAO.getWithFilter(null, null, re.getProgramId(), null, null));
            else
                re.setEpisodes(new ArrayList<Episode>());

        }
        }catch(Exception e ){MuLog.log(e);}
        return re;
    }
	private static ArrayList<ProgramInfo> RsToObjectList(ResultSet rs, boolean loadEpisode)
    {
        ArrayList<ProgramInfo> re = new ArrayList<ProgramInfo>();
		while (true)
        {
            ProgramInfo p = RsToObject(rs, loadEpisode);
            if(p==null)
                break;
            re.add(p);
        }
        return re;
    }

	public static void update(ProgramInfo pi)
    {
        try{
            Connection conn = DBMgr.getConnection();
            PreparedStatement p = conn.prepareStatement("update app.program " +
                " set element_id=?, program_id=?, url=?, name=?, total_page=?, status=? " +
                " where url=? ");
            p.setObject(1, pi.getElementId());
            p.setObject(2, pi.getProgramId());
            p.setObject(3, pi.getProgramURL());
            p.setObject(4, pi.getProgramName());
            p.setInt(5, pi.getTotalPage());
            p.setObject(6, pi.getStatus().toString());
            p.setObject(7, pi.getProgramURL());
            int i = p.executeUpdate();
            if(i==0)
                create(pi);
            else
            {
                for(Episode e : pi.getEpisodes())
                    EpisodeDAO.update(e);
            }

        }catch(Exception e ){MuLog.log(e);}
    }

	public static void delete(String programId)
    {
        try{
        DBMgr.deleteRecord("app.program", "program_id", programId);
        }catch(Exception e ){MuLog.log(e);}
    }
}
