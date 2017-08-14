package com.mu.cctv.program;

/**
 *
 * @author Peng Mu
 */
public enum ProgramStatus
{
    Init,
    Basic_Info_Retrieved,
    Episode_Id_Retrieved,
    Episode_Url_Retieved,
    Added_To_Tasklist,
    Waiting,
    Downloading,
    Suspended,
    Removed,
    Finished
}
