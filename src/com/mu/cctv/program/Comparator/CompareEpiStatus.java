package com.mu.cctv.program.Comparator;

import com.mu.cctv.program.Episode;
import com.mu.cctv.program.EpisodeStatus;
import java.util.Comparator;

/**
 *
 * @author Peng Mu
 */
public class CompareEpiStatus implements Comparator<Episode>
{
    public int compare(Episode e1, Episode e2)
    {
        return e1.getStatus().compareTo(e2.getStatus());
    }
}
