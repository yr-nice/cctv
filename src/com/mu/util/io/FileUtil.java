/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Hello
 */
public class FileUtil
{


    public static void writeFile(String content, String path) throws Exception
    {
        BufferedWriter b = new BufferedWriter(new FileWriter(path));
        b.write(content);
        b.close();
        /*FileOutputStream fo = new FileOutputStream(path, true);
        fo.write(content.getBytes("UTF-8"));
        fo.close();*/



    }

    public static String readTxtFile(String path) throws Exception
    {
        BufferedReader b = new BufferedReader(new FileReader(path));
        StringBuffer buff = new StringBuffer();

        while(b.ready())
        {
            String tmp = b.readLine();
            if(tmp == null)
                break;
            buff.append(tmp);
            buff.append('\n');
        }
        return buff.toString();
    }

	public static void saveObject2File(Object o, String sPath)throws Exception
	{
		ObjectOutputStream oop = new ObjectOutputStream(new FileOutputStream(sPath));
		oop.writeObject(o);
		oop.close();
	}

	public static Object getObjectFromFile(String sPath)throws Exception
	{
		ObjectInputStream oip = new ObjectInputStream(new FileInputStream(sPath));
		Object re = oip.readObject();
		oip.close();
		return re;
	}



}
