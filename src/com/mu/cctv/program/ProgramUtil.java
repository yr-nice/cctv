package com.mu.cctv.program;

import com.mu.cctv.program.impl.SohuProgramInfo;
import com.mu.cctv.program.impl.SpaceProgramInfo;
import com.mu.cctv.program.impl.VodProgramInfo;
import hello.mu.util.MuLog;

/**
 *
 * @author Peng Mu
 */
public class ProgramUtil
{

    static private final String packagePath = "com.mu.cctv.program.impl.";
    static public ProgramInfo getProgram(String url, String programName)
    {
        ProgramInfo re = null;
        if(url.contains("space"))
            re = (ProgramInfo) new SpaceProgramInfo();
        else if(url.contains("sohu"))
            re = (ProgramInfo) new SohuProgramInfo();
        else
            re = (ProgramInfo) new VodProgramInfo();

        re.setProgramName(programName);
        re.setProgramURL(url);
        return re;

    }

    static public String getType(Object o)
    {
        String className = o.getClass().getSimpleName();
        return className;
    }

    static public ProgramInfo typeToProgram(String className)
    {
        ProgramInfo re=null;
        try{
        re = (ProgramInfo)Class.forName(packagePath + className).getConstructor().newInstance();
        }catch(Exception e){MuLog.log(e);}
        return re;
    }

    static public Episode typeToEpisode(String className)
    {
        Episode re=null;
        try{
        re = (Episode)Class.forName(packagePath + className).getConstructor().newInstance();
        }catch(Exception e){MuLog.log(e);}
        return re;
    }
}
