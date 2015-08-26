/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import net.TheDgtl.Tombstone.Tombstone;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author PapaHarni
 */
public class TombStoneOpen implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent e) {
        Location l = getLocation(e.getInventory().getHolder());
        if(l == null)
            return;
        
        if(!Tombstone.getPlugin().isTombStone(l))
            return;
        
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player)e.getWhoClicked();
            PlayerData pd = PlayerData.getPlayerData(p);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.tombstone", "This chest is protected by TombStone. Is this your chest, please press the sign."));
        }
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent e) {
        Location l = getLocation(e.getInventory().getHolder());
        if(l == null)
            return;
        
        if(!Tombstone.getPlugin().isTombStone(l))
            return;
        
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player)e.getWhoClicked();
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.tombstone", "This chest is protected by TombStone. Is this your chest, please press the sign."));
        }
        e.setCancelled(true);
    }
    
    /*@EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent e) {
        Location l = getLocation(e.getInventory().getHolder());
        if(l == null)
            return;
        
        if(!Tombstone.getPlugin().isTombStone(l))
            return;
        
        if(!(e.getInventory().getHolder() instanceof DoubleChest) && !(e.getInventory().getHolder() instanceof Chest))
            return;
        
        e.setCancelled(true);
    }
    
    private boolean isInventoryEmpty(Inventory inv) {
        for(ItemStack item: inv.getContents()) {
            if(item != null)
                return false;
        }
        return true;
    }*/
    
    private Location getLocation(InventoryHolder ih) {
        if(ih instanceof BlockState)
            return ((BlockState)ih).getLocation();
        if(ih instanceof Chest)
            return ((Chest)ih).getLocation();
        if(ih instanceof DoubleChest)
            return ((DoubleChest)ih).getLocation();
        return null;
    }
}
