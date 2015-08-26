/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author PapaHarni
 */
public class Disposal implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onSignClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        
        if(!(e.getClickedBlock().getState() instanceof Sign))
            return;
        
        Sign s = (Sign)e.getClickedBlock().getState();
        if(s.getLine(0).isEmpty())
            return;
        
        if(!"[disposal]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0))))
            return;
        Inventory inv = Bukkit.createInventory(null, p.getInventory().getSize(), "Disposal");
        p.openInventory(inv);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onSignChange(SignChangeEvent e) {
        if(!"[disposal]".equalsIgnoreCase(ChatColor.stripColor(e.getLine(0))))
            return;
        e.getPlayer().sendMessage(Language.getMessage(Customs.getPlugin(), e.getPlayer().getUniqueId(), "function.dispsal.created", "You have successful created a Disposal"));
    }
    
}
