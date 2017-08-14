/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.net.downloader;

/**
 *
 * @author Hello
 */
public enum TaskStatus
{
    Waiting_for_Start,
    Connecting_to_Server,
    Initing_Task,
    Start_Downloading,
    Suspended,
    Failed,
    File_Corrupt,
    Removed,
    Finished


}
