package com.mu.cctv.program.Comparator;

import com.mu.cctv.program.Episode;
import java.util.Comparator;

/**
 *
 * @author Peng Mu
 */
public class CompareEpiId implements Comparator<Episode>
{
    public int compare(Episode e1, Episode e2)
    {
        return e1.getId().compareTo(e2.getId());
    }
}
