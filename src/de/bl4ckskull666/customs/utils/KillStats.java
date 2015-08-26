/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import de.bl4ckskull666.customs.Customs;
import java.util.HashMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class KillStats {
    private final HashMap<EntityType, Long> _kills = new HashMap<>();
    private final String _name;
    private final String _uuid;
    private long _allKills = 0;
    
    public static void createKillStats(Player p) {
        KillStats ks = new KillStats(p);
        Customs.setKillStats(p.getName(), ks);
    }
    
    public KillStats(Player p) {
        _name = p.getName();
        _uuid = p.getUniqueId().toString();
        for(EntityType et: EntityType.values()) {
            if(et.isAlive())
                _kills.put(et, 0L);
        }
    }
    
    public String getName() {
        return _name;
    }
    
    public String getUUID() {
        return _uuid;
    }
    
    public void setKills(EntityType ent, long count) {
        _kills.put(ent, count);
        _allKills = _allKills+count;
    }
    
    public void addKill(EntityType ent) {
        _kills.put(ent, _kills.get(ent)+1);
        _allKills++;
    }
    
    public long getKill(EntityType ent) {
        return _kills.get(ent);
    }
    
    public long getAllKills() {
        return _allKills;
    }
}
