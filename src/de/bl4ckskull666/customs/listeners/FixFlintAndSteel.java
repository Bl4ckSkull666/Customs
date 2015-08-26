/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Rnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author PapaHarni
 */
public class FixFlintAndSteel implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        Location loc = e.getClickedBlock().getLocation();
        loc.setY(loc.getBlockY()+1);
        if(loc.getBlock().getType() != Material.AIR)
            return;
        
        if(!e.hasItem())
            return;
        
        if(e.getItem().getType() != Material.FLINT_AND_STEEL)
            return;
        
        if(!Customs.getPlugin().getWG().canBuild(e.getPlayer(), loc))
            return;
        
        int newDura = Rnd.get(e.getItem().getDurability(), (e.getItem().getDurability()+1));
        if(newDura >= e.getItem().getType().getMaxDurability())
            e.getPlayer().getInventory().remove(e.getItem());
        else
            e.getItem().setDurability((short)newDura);
        e.getPlayer().updateInventory();
        loc.getBlock().setType(Material.FIRE);
    }
}
