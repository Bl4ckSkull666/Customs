/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Tasks.RemoveFly;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author PapaHarni
 */
public final class RFly {
    private final String _name;
    private long _time; //Seconds
    private BukkitTask _bt;
    private final long _start; //MilliSeconds
    
    public RFly(String name, long time) {
        _name = name;
        _time = time;
        _start = System.currentTimeMillis();
    }
    
    public void addTime(long add) {
        _time += add;
    }
    
    public long getTime() {
        return _time;
    }
    
    public void setTime(long t) {
        _time = t;
    }
    
    public long getBeginTime() {
        return _start;
    }
    
    public void setTask(BukkitTask task) {
        _bt = task;
    }
    
    public BukkitTask getTask() {
        return _bt;
    }
    
    //statics
    public static HashMap<String, RFly> _online = new HashMap<>();
    public static HashMap<String, Long> _offline = new HashMap<>();
    
    public static HashMap<String, Long> getOfflineRentTimes() {
        return _offline;
    }
    
    public static long getOfflineRentTime(String name) {
        return _offline.containsKey(name)?_offline.get(name):0L;
    }
    
    public static void removeOfflineRentTime(String name) {
        _offline.remove(name);
    }
    
    public static void setOfflineRentTime(String name, long time) {
        _offline.put(name, time);
    }
    
    public static boolean isRFly(String name) {
        return _online.containsKey(name);
    }
    
    public static RFly getRFly(String name) {
        return _online.get(name);
    }
    
    public static RFly setRFly(String name, long t) {
        long runTime;
        RFly rf;
        if(_online.containsKey(name)) {
            rf = _online.get(name);
            if(rf.getTask() != null) 
                rf.getTask().cancel();
            
            runTime = rf.getTime()-((System.currentTimeMillis()-rf.getBeginTime())/1000)+t;
            rf.setTime(runTime);
        } else {
            rf = new RFly(name, t);
            runTime = t;
            _online.put(name, rf);
        }
        
        BukkitTask bt = Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new RemoveFly(name, rf), (runTime*20));
        rf.setTask(bt);
        return rf;
    }
    
    public static void removeRFly(String name) {
        _online.remove(name);
    }
    
    public static void saveAll() {
        for(Map.Entry<String, RFly> e: _online.entrySet()) {
            long restTime = e.getValue().getTime()-((System.currentTimeMillis()-e.getValue().getBeginTime())/1000);
            _offline.put(e.getKey(), restTime);
            if(e.getValue().getTask() != null)
                e.getValue().getTask().cancel();
        }
        _online.clear();
    }
    
    public static void loadAll() {
        for(Player p: Bukkit.getOnlinePlayers()) {
            if(!_offline.containsKey(p.getUniqueId().toString()))
                continue;
            
            RFly rf = RFly.setRFly(p.getUniqueId().toString(), _offline.get(p.getUniqueId().toString()));
            _offline.remove(p.getUniqueId().toString());
            PlayerData pd = PlayerData.getPlayerData(p);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.over", "You have a rest time of %time% minutes to fly.", new String[] {"%time%"}, new String[] {String.valueOf((int)Math.ceil(rf.getTime()/60))}));
        }
    }
}