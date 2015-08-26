/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 *
 * @author PapaHarni
 */
public class NPCProtection implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player))
            return;
        
        Player p = (Player)e.getDamager();
        if(p.isOp() || p.hasPermission("customs.amcteam"))
            return;
        
        String world = e.getEntity().getLocation().getWorld().getName().toLowerCase();
        if(!Customs.getPlugin().getConfig().isList("protect-npc-on-world." + world))
            return;
        
        if(!Customs.getPlugin().getConfig().getStringList("protect-npc-on-world." + world).contains(e.getEntityType().name().toLowerCase()))
            return;
        
        PlayerData pd = PlayerData.getPlayerData(p);
        e.getDamager().sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.npcprotection.attack", "You can't attack type of %npc% in this World.", new String[] {"%npc%"}, new String[] {e.getEntityType().name().toLowerCase().replace("_", " ")}));
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        Location loc = p.getLocation();
        String world = p.getWorld().getName().toLowerCase();
        
        if(p.isOp() || p.hasPermission("customs.amcteam"))
            return;
        
        if(!Customs.getPlugin().getConfig().isConfigurationSection("protect-world-by-npc." + world))
            return;
        
        if(!isProectedNPCinNear(loc))
            return;
        
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.npcprotection.break", "You can't break blocks here."));
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        Location loc = p.getLocation();
        String world = p.getWorld().getName().toLowerCase();
        
        if(p.isOp() || p.hasPermission("customs.amcteam"))
            return;
        
        if(!Customs.getPlugin().getConfig().isConfigurationSection("protect-world-by-npc." + world))
            return;
        
        if(!isProectedNPCinNear(loc))
            return;
        
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.npcprotection.place", "You can't place blocks here."));
        e.setCancelled(true);
    }
    
    private boolean isProectedNPCinNear(Location loc) {
        String world = loc.getWorld().getName().toLowerCase();
        for(String key: Customs.getPlugin().getConfig().getConfigurationSection("protect-world-by-npc." + world).getKeys(false)) {
            int r = Customs.getPlugin().getConfig().getInt("protect-world-by-npc." + world + "." + key);
            for(LivingEntity ent: loc.getWorld().getLivingEntities()) {
                if(!ent.getType().name().equalsIgnoreCase(key))
                    continue;
                
                if(loc.distance(ent.getLocation()) <= r)
                    return true;
            }
        }
        return false;
    }
}
