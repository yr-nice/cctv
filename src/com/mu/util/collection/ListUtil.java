/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.collection;

import java.util.ArrayList;

/**
 *
 * @author Peng mu
 */
public class ListUtil
{
    static public ArrayList getUniqueList(ArrayList list)
    {
        ArrayList re = new ArrayList();
        for(Object o : list)
            if(!re.contains(o))
                re.add(o);
        return re;
    }
}
