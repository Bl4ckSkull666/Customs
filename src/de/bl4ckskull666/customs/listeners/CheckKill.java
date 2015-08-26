/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.KillStats;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 *
 * @author PapaHarni
 */
public class CheckKill implements Listener {
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent e) {
        if(Customs.getPlugin().getConfig().getBoolean("deactivate.function.checkkill", false))
            return;
        
        if(e.getEntity().getKiller() == null)
            return;

        Player p = e.getEntity().getKiller();
        KillStats ks = Customs.getKillStatsByPlayer(p.getName());
        ks.addKill(e.getEntityType());
        
        int gp = Customs.getPlugin().getConfig().getInt("pay-for-kill." + e.getEntityType().name().toLowerCase(), 0);
        if(gp > 0 && Customs.getEco() != null) {
            Customs.givePlayerMoney(p, gp);
            
            String name = e.getEntity().getName();
            if(name.isEmpty())
                name = e.getEntityType().name().toLowerCase().replace("_", " ");
            
            PlayerData pd = PlayerData.getPlayerData(p);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(),
                    "function.checkkill.forKill",
                    "You have earn " + String.valueOf(gp) + " " + Customs.getEco().currencyNamePlural() + " for killing " + name,
                    new String[] {"%amount%", "%currency%", "%type%"},
                    new String[] {String.valueOf(gp), Customs.getEco().currencyNamePlural(), name}
            ));
        }
    }
    
}
