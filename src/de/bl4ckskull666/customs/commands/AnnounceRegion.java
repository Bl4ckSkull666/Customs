/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bl4ckskull666.customs.commands;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Pappi
 */
public class AnnounceRegion implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        
        return true;
    }   


    private String getRegion(Location l) {
        Map<String, ProtectedRegion> m = Customs.getPlugin().getWG().getGlobalRegionManager().get(l.getWorld()).getRegions();
        if(m.size() < 1)
            return "";
        
        int priority = -1;
        for (Map.Entry<String, ProtectedRegion> e : m.entrySet()) {
            if(e.getValue().getPriority() < priority)
                continue;
            
            if(e.getValue().getPriority() > priority)
                priority = e.getValue().getPriority();
            
            BlockVector min = e.getValue().getMinimumPoint();
            BlockVector max = e.getValue().getMaximumPoint();
            if(l.getBlockX() < min.getBlockX() || l.getBlockX() > max.getBlockX())
                continue;
            if(l.getBlockY() < min.getBlockY() || l.getBlockY() > max.getBlockY())
                continue;
            if(l.getBlockZ() < min.getBlockZ() || l.getBlockZ() > max.getBlockZ())
                continue;
            
            return e.getKey();
        }
        return "";
    }
    
    private boolean isRegion(String w, String r) {
        if(Bukkit.getWorld(w) == null)
            return false;
        
        Map<String, ProtectedRegion> m = Customs.getPlugin().getWG().getGlobalRegionManager().get(Bukkit.getWorld(w)).getRegions();
        for (Map.Entry<String, ProtectedRegion> e : m.entrySet()) {
            if(e.getKey().equalsIgnoreCase(r))
                return true;
        }
        return false;
    }
}