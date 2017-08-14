
package com.mu.cctv.cfg;

import com.mu.cctv.global.Global;
import hello.mu.util.MuLog;
import hello.mu.util.xml.XMLDOMConfigParser;
import java.io.File;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Hello
 */
public class CfgMgr extends XMLDOMConfigParser
{
    static public String getDownloadDestFolder()
    {
        //return "C:\\temp\\tasks.list";
        return Global.baseDir+File.separator+"download";
    }

    public static ArrayList<String> getList(String listId)
    {
        ArrayList<String> re = new ArrayList<String>();
        try{
        NodeList list = findNodes(doc, String.format("/config/*/list[@id='%s']/item/@value", listId));
        for(int i=0; i<list.getLength(); i++)
        {
            Node n = list.item(i);
            String item = n.getNodeValue();
            re.add(item);
        }
        }catch(Exception e){MuLog.log(e);}

        return re;
    }

    static public String getParam(String paramId)
    {
        String re = "";
        Node node = null;
        try{
        node = findSingleNode(doc, String.format("/config/param[@id='%s']/@value", paramId));
        re = node.getTextContent();
        }catch(Exception e){MuLog.log(e);}

        return re;
    }

    static public int getParamInt(String paramId)
    {
        return Integer.parseInt(getParam(paramId));
    }





}
