/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author PapaHarni
 */
public class PlayerTeleport implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if(e.isCancelled())
            return;
        
        if(e.getFrom() == null || e.getTo() == null)
            return;

        if(e.getFrom().getWorld().getName().equalsIgnoreCase(e.getTo().getWorld().getName()))
            return;

        if(!Customs.getPlugin().getConfig().isList("ignore-teleport"))
            return;

        if(Customs.getPlugin().getConfig().getStringList("ignore-teleport").isEmpty())
            return;
        
        boolean isWorld = false;
        for(String w: Customs.getPlugin().getConfig().getStringList("ignore-teleport")) {
            if(w.equalsIgnoreCase(e.getFrom().getWorld().getName()))
                isWorld = true;
        }
        
        if(!isWorld)
            return;
        
        PlayerData pd = PlayerData.getPlayerData(e.getPlayer());
        if(pd.getAge() <= 0 || pd.getGender().equalsIgnoreCase("none")) {
            Language.sendMessage(Customs.getPlugin(), e.getPlayer(), "function.ignore-teleport", "&cYou must add Birthday and Gender before you can teleport.");
            e.setCancelled(true);
            Customs.getPlugin().getLogger().log(Level.INFO, "Cancel Teleport of Player {0}", e.getPlayer().getName());
        }
    }
}
