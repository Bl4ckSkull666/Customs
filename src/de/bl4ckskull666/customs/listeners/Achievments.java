/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

/**
 *
 * @author PapaHarni
 */
public class Achievments implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE)
            e.setCancelled(true);
        
        Utils.setAchievement(e.getPlayer(), e.getAchievement());
    }
}
