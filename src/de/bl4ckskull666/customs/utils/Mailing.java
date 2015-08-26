/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

/**
 *
 * @author Pappi
 */
public class Mailing {
    private final String _from;
    private final String _message;
    private final long _received;
    private long _reading = 0;
    
    public Mailing(String from, long receive, String msg) {
        _from = from;
        _message = msg;
        _received = receive;
    }
    
    public String getFrom() {
        return _from;
    }
    
    public String getMessage() {
        return _message;
    }
    
    public long getReceived() {
        return _received;
    }
    
    public void setReading(long read) {
        _reading = read;
    }
    
    public long getReading() {
        return _reading;
    }
}