/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import org.bukkit.Location;

/**
 *
 * @author PapaHarni
 */
public class TeleportRequest {
    private final String _from;
    private final String _to;
    private final Location _toLocation;
    private final long _time;
    private final boolean _toFrom;
    
    public TeleportRequest(String from, String to, Location loc, boolean toFrom) {
        _from = from;
        _to = to;
        _toLocation = loc;
        _toFrom = toFrom;
        _time = System.currentTimeMillis();
    }
    
    public String getFrom() {
        return _from;
    }
    
    public String getTo() {
        return _to;
    }
    
    public Location getLocation() {
        return _toLocation;
    }
    
    public long getRequestSendTime() {
        return _time;
    }
    
    public boolean getToFrom() {
        return _toFrom;
    }
}
