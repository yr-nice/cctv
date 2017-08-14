package com.mu.cctv.program.Comparator;

import com.mu.cctv.program.Episode;
import java.util.Comparator;

/**
 *
 * @author Peng Mu
 */
public class CompareEpiIndex implements Comparator<Episode>
{
    public int compare(Episode e1, Episode e2)
    {
        return Long.valueOf(e1.getIndex()).compareTo(Long.valueOf(e2.getIndex()));
    }
}
