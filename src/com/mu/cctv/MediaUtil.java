package com.mu.cctv;

import com.mu.cctv.global.Global;
import hello.mu.util.MuLog;
import hello.mu.util.ProcessExecuter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 *
 * @author Peng Mu
 */
public class MediaUtil
{
    static public void main(String[] argu)
    {
        Main.init();
        joinMp4MediaFolder("C:\\Project\\Code\\SelfCreatedTool\\cctv\\");
    }

    static public void joinMp4MediaFolder(String folder)
    {
        //folder = "F:\\CCTV\\shi_bing_tu_ji\\9\\";
        String mp4box = Global.baseDir + "\\mp4tools\\mp4box ";
        String batPath = folder+File.separator+"combin.bat";
        File bat = new File(batPath);
        if(bat.exists())
            bat.delete();

        File fo = new File(folder);
        for(File f : fo.listFiles())
        {
            String path = f.getAbsolutePath();
            if(path.endsWith("-1.mp4"))
            {
                String prePath = path.substring(0, path.length()-6);
                File out = new File(prePath + ".mp4");
                MuLog.log("Joined File:"+ out.getAbsolutePath());
                if(!out.exists())
                {
                    ArrayList<String> input = new ArrayList<String>();
                    for(int j=1; j<1000; j++)
                    {
                        File ff = new File(prePath+"-"+j+".mp4");
                        if(!ff.exists()) break;
                        input.add(ff.getAbsolutePath());
                    }
                    try{
                    appendMp4FilesJoinBatch(input, mp4box, batPath, out.getPath());

                    }catch(Exception e){MuLog.log(e);}
                }
                else
                {
                    long curSize = out.length();
                    long accumulate = 0;

                    ArrayList<String> input = new ArrayList<String>();
                    for(int j=1; j<1000; j++)
                    {
                        File ff = new File(prePath+"-"+j+".mp4");
                        if(!ff.exists()) break;
                        accumulate += ff.length();
                        MuLog.log(String.format("File:%s, cursize=%d, acc=%d", ff.getAbsolutePath(), curSize, accumulate));
                        if(accumulate - curSize > 1000*1000)
                            input.add(ff.getAbsolutePath());
                    }
                    try{
                    appendMp4FilesJoinBatch(input, mp4box, batPath, out.getPath());

                    }catch(Exception e){MuLog.log(e);}

                }
            }
        }
       try{
        if(bat.exists())
        {
            MuLog.log("Start to combine media files...");
            int i = ProcessExecuter.exec(bat.getAbsolutePath(), true);
            MuLog.log("Finished combining media files. Return code="+i);
            //MuLog.log("Finished combining media files. Return code="+i);
        }
       }catch(Exception e){MuLog.log(e);}
    }

    static public void joinMp4MediaFile(String path)
    {
        File out = new File(path);
        String folder = out.getParent();
        String mp4box = Global.baseDir + "\\mp4tools\\mp4box ";
        String batPath = folder+File.separator+"combin.bat";
        File bat = new File(batPath);
        if(bat.exists())
            bat.delete();

        MuLog.log("Joined File:"+ out.getAbsolutePath());
        long curSize = out.length();
        long accumulate = 0;

        ArrayList<String> input = new ArrayList<String>();
        String prePath = path.substring(0, path.length()-4);
        for(int j=1; j<1000; j++)
        {
            File ff = new File(prePath+"-"+j+".mp4");
            if(!ff.exists()) break;
            accumulate += ff.length();
            if(accumulate - curSize > 1000*1000)
                input.add(ff.getAbsolutePath());
        }
        try{
        appendMp4FilesJoinBatch(input, mp4box, batPath, out.getPath());
        }catch(Exception e){MuLog.log(e);}
        try{
            if(bat.exists())
            {
                MuLog.log("Start to combine media files...");
                int i = ProcessExecuter.exec(bat.getAbsolutePath(), true);
                MuLog.log("Finished combining media files. Return code="+i);
            //MuLog.log("Finished combining media files. Return code="+i);
            }
        }catch(Exception e){MuLog.log(e);}

    }

    static public void appendMp4FilesJoinBatch(ArrayList<String> input, String mp4boxPath, String batchOutput,  String mp4Output) throws Exception
    {
        ArrayList<String> argu = new ArrayList<String>();
        argu.add(mp4boxPath);
        FileOutputStream fo = new FileOutputStream(batchOutput, true);
        StringBuilder sp = new StringBuilder(mp4boxPath + " ");
        for(String s : input)
        {
            argu.add("-cat \""+s+"\"");
            sp.append("-cat ");
            sp.append("\""+s+"\"");
            sp.append(" ");
        }
        argu.add(mp4Output);
        sp.append("\""+mp4Output+"\"");
        sp.append("\r\n");
        fo.write(sp.toString().getBytes("GB2312"));
        fo.close();
        //ProcessBuilder pc = new ProcessBuilder(argu);
        //Process p =pc.start();
        /*OutputStream o = p.getOutputStream();
        while(o.)
        System.out.print(p.getOutputStream();*/
    }

}
