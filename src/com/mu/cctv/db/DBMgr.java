package com.mu.cctv.db;

import com.mu.cctv.cfg.CfgMgr;
import com.mu.cctv.global.Global;
import com.mu.util.io.FileUtil;
import hello.mu.util.MuLog;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author Peng Mu
 */
public class DBMgr
{
    private static Connection conn;

    static public void main(String[] argu)
    {
        init();
    }

    static public void init()
    {
        String dbFolder = Global.baseDir + File.separator + "db" + File.separator + "derby";
        if(!(new File(dbFolder)).exists())
            createDB();
    }

    static private void createDB()
    {
        String sqlFolder = Global.baseDir + File.separator + "sql";

        ArrayList<String> arr = CfgMgr.getList("create_table_sql");
        try{
        conn = getConnection();
        for(String s : arr)
        {
            String sql = FileUtil.readTxtFile(sqlFolder+ File.separator +s);
            sql = sql.trim().replace(";", "");
            PreparedStatement p = conn.prepareStatement(sql);
            p.execute();
            p.close();
        }

        }catch(Exception e){MuLog.log(e);}
    }

    static public Connection getConnection()
    {
        try{

        if(conn != null && conn.isValid(1000))
            return conn;
        else
        {
            String dbFolder = Global.baseDir + File.separator + "db" + File.separator + "derby";
            String driver = "org.apache.derby.jdbc.EmbeddedDriver";


            Class.forName(driver);
            System.out.println(driver + " loaded. ");
            String connectionURL = "jdbc:derby:" + dbFolder + ";create=true";
            conn = DriverManager.getConnection(connectionURL);
            System.out.println("Connected to database " + dbFolder);

        }
        }catch(Exception e){MuLog.log(e);}
        return conn;
    }

    public static boolean deleteRecord(String tableName, String keyColumnName, Object value) throws Exception
    {
        boolean re = false;
        try{
            if(conn == null)
                conn = getConnection();

            PreparedStatement ps = conn.prepareStatement(String.format("delete from %s where %s=?", tableName, keyColumnName));
            ps.setObject(1, value);
            MuLog.log(String.format("deleteRecord, sql=%s", ps.toString()));
            ps.executeUpdate();
            re = true;
        }
        finally{
        }
        return re;
    }

}
