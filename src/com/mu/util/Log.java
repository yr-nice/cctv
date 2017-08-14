/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util;

import java.util.Collection;

/**
 *
 * @author Hello
 */
public class Log
{
    static public void log(Object o)
    {
        System.out.println(o);
    }
    static public void error(Object o)
    {
        log(o.toString());
    }
    static public void logCollection(Collection c)
    {
        if(c == null)
            log(c);
        else
        {
            log(String.format("Type: %s, Size: %d", c.getClass().getCanonicalName(), c.size()));
            for(Object o:c)
                log(o);
        }
    }


}
