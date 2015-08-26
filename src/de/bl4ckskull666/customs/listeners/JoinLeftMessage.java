/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Pappi
 */
public class JoinLeftMessage implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void JoinEvent(PlayerJoinEvent e) {
        if(Customs.getPlugin().getConfig().isString("message.join")) {
            if(Customs.getPlugin().getConfig().getString("message.join").equalsIgnoreCase("null") || Customs.getPlugin().getConfig().getString("message.join").equalsIgnoreCase("")) {
                e.setJoinMessage(null);
            } else {
                String msg = Customs.getPlugin().getConfig().getString("message.join").replace("%name%", e.getPlayer().getName());
                e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void QuitEvent(PlayerQuitEvent e) {
        if(Customs.getPlugin().getConfig().isString("message.quit")) {
            if(Customs.getPlugin().getConfig().getString("message.quit").equalsIgnoreCase("null") || Customs.getPlugin().getConfig().getString("message.quit").equalsIgnoreCase("")) {
                e.setQuitMessage(null);
            } else {
                String msg = Customs.getPlugin().getConfig().getString("on.quit").replace("%name%", e.getPlayer().getName());
                e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void KickEvent(PlayerKickEvent e) {
        if(Customs.getPlugin().getConfig().isString("message.kick")) {
            if(Customs.getPlugin().getConfig().getString("message.kick").equalsIgnoreCase("null") || Customs.getPlugin().getConfig().getString("message.kick").equalsIgnoreCase("")) {
                e.setLeaveMessage(null);
            } else {
                String msg = Customs.getPlugin().getConfig().getString("message.kick").replace("%name%", e.getPlayer().getName());
                e.setLeaveMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }
}
