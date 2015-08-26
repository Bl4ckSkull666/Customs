/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import java.util.HashMap;

/**
 *
 * @author PapaHarni
 */
public final class WaitingAction {
    private final long _time;
    private final String _action;
    private final String _doing;
    
    public WaitingAction(String name, String action, String doing) {
        _action = action;
        _time = System.currentTimeMillis();
        _doing = doing;
        WaitingAction wa = this;
        _actions.put(name, wa);
    }
    
    public String getAction() {
        return _action;
    }
    
    public String getDoing() {
        return _doing;
    }
    
    public Long getTime() {
        return _time;
    }
    
    private static final HashMap<String, WaitingAction> _actions = new HashMap<>();
    public static boolean isWaiting(String name) {
        return _actions.containsKey(name);
    }
    
    public static WaitingAction getWaiting(String name) {
        if(!isWaiting(name))
            return null;
        return _actions.get(name);
    }
    
    public static void removeWaiting(String name) {
        _actions.remove(name);
    }
}
