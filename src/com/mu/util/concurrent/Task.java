/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mu.util.concurrent;

import java.util.Observable;

/**
 *
 * @author Peng mu
 */
public class Task extends Observable
{
    protected String id;
    protected String status;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void notifyObservers()
    {
        setChanged();
        super.notifyObservers();
    }

}
