/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Pappi
 */
public class ArmorStandEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        ArmorStand as = e.getRightClicked();
        Player p = e.getPlayer();
        
        if(Customs.getPlugin().isWG()) {
            if(!Customs.getPlugin().getWG().canBuild(p, as.getLocation())) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.armorstand.no-modify-rights", "You can't use the Armor Stand here, you don't have Build permission on this place."));
                e.setCancelled(true);
            }    
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player))
            return;
        
        if(!e.getEntity().getType().equals(EntityType.ARMOR_STAND))
            return;
        
        Player p = (Player)e.getDamager();
        if(Customs.getPlugin().isWG()) {
            if(!Customs.getPlugin().getWG().canBuild(p, e.getEntity().getLocation())) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.armorstand.no-destroy-rights", "You can't destroy Armor Stand here, you don't have Build permission on this place."));
                e.setDamage(0.0);
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        
        if(!e.hasItem())
            return;
        
        if(!e.getItem().getType().equals(Material.ARMOR_STAND))
            return;
        
        Player p = e.getPlayer();
        if(Customs.getPlugin().isWG()) {
            if(Customs.getPlugin().getWG().canBuild(p, e.getClickedBlock().getLocation())) {
                Location entLoc = e.getClickedBlock().getLocation().clone();
                entLoc.setY(entLoc.getY()+1);
                
                entLoc.setYaw((p.getLocation().getYaw() > 180)?(p.getLocation().getYaw()-180):(p.getLocation().getYaw()+180));
                Entity ent = e.getClickedBlock().getLocation().getWorld().spawnEntity(entLoc, EntityType.ARMOR_STAND);
                e.setCancelled(true);
            }
        }
    }
}
