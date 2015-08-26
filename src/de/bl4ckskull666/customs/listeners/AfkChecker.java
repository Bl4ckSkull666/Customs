package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Afk;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author PapaHarni
 */
public class AfkChecker implements Listener {
    private final Customs _p;
    private final static HashMap<String, Long> _lastActive = new HashMap<>();
    private final static HashMap<String, Afk> _afk = new HashMap<>();
    
    public AfkChecker(Customs pl) {
        _p = pl;
    }
    
    private void checkAway(String p) {
        if(_afk.containsKey(p)) {
            _afk.remove(p);
            _lastActive.put(p, System.currentTimeMillis());
        }
        
    }
    
    private void informPlayers(String pName, boolean isAFK, boolean isAuto) {
        for(Player p: Bukkit.getOnlinePlayers()) {
            if(p.getName().equalsIgnoreCase(pName))
                continue;
            
            PlayerData pd = PlayerData.getPlayerData(p);
            if(isAFK) {
                if(isAuto)
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.afk.autoAfk", pName + " is now Away from Keyboard.", new String[] {"%name%"}, new String[] {pName}));
                else
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.afk.setAfk", pName + " has change the Status to Away from Keyboard.", new String[] {"%name%"}, new String[] {pName}));
            } else {
                if(isAuto)
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.afk.autoBack", pName + " is no more longer Away from Keyboard.", new String[] {"%name%"}, new String[] {pName}));
                else
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.afk.setBack", pName + " has change the Status to active.", new String[] {"%name%"}, new String[] {pName}));
            }
        }
    }
    
    public static class checkAway implements Runnable {
        private String _name;
        public checkAway(String name) {
            _name = name;
        }
        
        @Override
        public void run() {
            if(_afk.containsKey(_name)) {
                _afk.remove(_name);
                _lastActive.put(_name, System.currentTimeMillis());
            }
        }
    }
    
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        String pName = e.getPlayer().getName();
        if(_afk.containsKey(pName) && _p.getConfig().getBoolean("afk.cancel-on-away.bed-enter", false)) {
            e.setCancelled(true);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new checkAway(pName));
    }
    
    public void onPlayerBedLeave(PlayerBedLeaveEvent e) {
        String pName = e.getPlayer().getName();
        if(_afk.containsKey(pName) && _p.getConfig().getBoolean("afk.cancel-on-away.bed-leave", false)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new checkAway(pName));
    }
    
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        String pName = e.getPlayer().getName();
        if(_afk.containsKey(pName) && _p.getConfig().getBoolean("afk.cancel-on-away.changed-world", false)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new checkAway(pName));
    }
    
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String pName = e.getPlayer().getName();
        if(_afk.containsKey(pName) && _p.getConfig().getBoolean("afk.cancel-on-away.changed-world", false)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new checkAway(pName));
    }
    
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerEditBook(PlayerEditBookEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerExpChangeEvent(PlayerExpChangeEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerFish(PlayerFishEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerItemBreak(PlayerItemBreakEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerItemConsume(PlayerItemConsumeEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerJoin(PlayerJoinEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerKick(PlayerKickEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerLevelChange(PlayerLevelChangeEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerLogin(PlayerLoginEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerMove(PlayerMoveEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerPortal(PlayerPortalEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerQuit(PlayerQuitEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerShearEntity(PlayerShearEntityEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerToggleSprint(PlayerToggleSprintEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent e) {
        checkAway(e.getPlayer().getName());
    }
    
    public void onPlayerVelcity(PlayerVelocityEvent e) {
        checkAway(e.getPlayer().getName());
    }
}
