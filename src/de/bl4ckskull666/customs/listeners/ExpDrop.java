/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.customs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public class ExpDrop implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent e) {
        if(e.getEntity() == null)
            return;
        
        if(e.getEntity().getKiller() == null)
            return;
        
        Player p = e.getEntity().getKiller();
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!pd.activExpDrop())
            return;

        if(e.getEntity().isDead() && Utils.isMonster(e.getEntityType())) {
            int exp = e.getDroppedExp();
            e.setDroppedExp(0);
            ItemStack item = null;
            for(int i = exp; i > 0;) {
                i = i-Rnd.get(Math.min(i, 3), Math.min(i, 11));
                if(item == null)
                    item = new ItemStack(Material.EXP_BOTTLE,1);
                else
                    item.setAmount(item.getAmount()+1);
                    
                if(item.getAmount() == 64) {
                    e.getDrops().add(item);
                    item = null;
                }
            }
            if(item != null)
                e.getDrops().add(item);
        }
    }
}
