/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.cctv.global;

import java.io.File;
import java.util.Properties;

/**
 *
 * @author Hello
 */
public class Global
{
    static public Properties prop = new Properties();
    static public String baseDir = "";
    static public String getDownloadFolder()
    {
        return baseDir + File.separator + "download";
    }
}
