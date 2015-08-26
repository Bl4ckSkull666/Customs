/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Items;
import de.bl4ckskull666.customs.utils.KillStats;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Tasks.checkMobSpawnFlag;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Pappi
 */
public class onJoin implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        pd.setTimeStamp("login", System.currentTimeMillis());
        
        if(pd.getLogOutPos() != null && !Customs.getPlugin().getConfig().getBoolean("useSpawnAsJoin", false))
            p.teleport(pd.getLogOutPos());
        else if(pd.getLogOutPos() != null && Customs.getPlugin().getConfig().getBoolean("useSpawnAsJoin", false)) {
            if((pd.getAge() <= 0 || pd.getGender().equalsIgnoreCase("none") || pd.getGender().isEmpty()) && Customs.getPlugin().getFirstSpawnPoint() != null) {
                p.teleport(Customs.getPlugin().getFirstSpawnPoint());
            } else if(Customs.getPlugin().getSpawnPoint() != null)
                p.teleport(Customs.getPlugin().getSpawnPoint());
            else if(Customs.getPlugin().getFirstSpawnPoint() != null)
                p.teleport(Customs.getPlugin().getFirstSpawnPoint());
            else
                p.teleport(pd.getLogOutPos());
        } else {
            for(String str: Language.getMessages(Customs.getPlugin(), p.getUniqueId(), "welocme")) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', str.replace("%name%", e.getPlayer().getName())));
            }
            
            if(Customs.getPlugin().getConfig().isList("welcome-kit." + pd.getLanguage().toLowerCase())) {
                for(String str_item: Customs.getPlugin().getConfig().getStringList("welcome-kit." + pd.getLanguage().toLowerCase())) {
                    ItemStack item = Items.getItem(str_item);
                    if(item != null)
                        p.getInventory().addItem(item);
                }
            } else if(Customs.getPlugin().getConfig().isList("welcome-kit.default")) {
                for(String str_item: Customs.getPlugin().getConfig().getStringList("welcome-kit.default")) {
                    ItemStack item = Items.getItem(str_item);
                    if(item != null)
                        p.getInventory().addItem(item);
                }
            }
            p.updateInventory();
            
            if(Customs.getPlugin().getFirstSpawnPoint() != null)
                p.teleport(Customs.getPlugin().getFirstSpawnPoint());
            else if(Customs.getPlugin().getSpawnPoint() != null)
                p.teleport(Customs.getPlugin().getSpawnPoint());
        }
        
        if(!Customs.getPlugin().getConfig().getBoolean("deactivate.function.checkkill", false)) {
            KillStats.createKillStats(p);
            Customs.getMySQL().loadPlayerKills(p.getName());
        }
        
        for(Achievement achiv: Achievement.values()) {
            if(pd.isAchievment(achiv.name().toLowerCase()) && !p.hasAchievement(achiv))
                p.awardAchievement(achiv);
            else if(!pd.isAchievment(achiv.name().toLowerCase()) && p.hasAchievement(achiv))
                p.removeAchievement(achiv);
        }
        
        Customs.setHidenPlayers(p);
        
        if(p.hasPermission("customs.team")) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(Customs.getPlugin(), new checkMobSpawnFlag(p));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        PlayerData pd = PlayerData.getPlayerData(e.getPlayer());
        pd.setLogOutPos(e.getPlayer().getLocation());
        pd.setTimeStamp("logout", System.currentTimeMillis());
        pd.savePlayerData();
        PlayerData.removePlayerData(e.getPlayer().getUniqueId().toString());
        if(!Customs.getPlugin().getConfig().getBoolean("deactivate.function.checkkill", false))
            Customs.getMySQL().savePlayerKills(e.getPlayer().getName());
        Customs.removeHidePlayer(e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKickEvent(PlayerKickEvent e) {
        PlayerData pd = PlayerData.getPlayerData(e.getPlayer());
        pd.setLogOutPos(e.getPlayer().getLocation());
        pd.setTimeStamp("logout", System.currentTimeMillis());
        pd.savePlayerData();
        PlayerData.removePlayerData(e.getPlayer().getUniqueId().toString());
        if(!Customs.getPlugin().getConfig().getBoolean("deactivate.function.checkkill", false))
            Customs.getMySQL().savePlayerKills(e.getPlayer().getName());
        Customs.removeHidePlayer(e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        PlayerData pd = PlayerData.getPlayerData(e.getPlayer());
        pd.setHome("bed", e.getPlayer().getLocation());
        e.getPlayer().sendMessage(Language.getMessage(Customs.getPlugin(), e.getPlayer().getUniqueId(), "function.bed.save", "New Bed Home-Point saved."));
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        PlayerData pd = PlayerData.getPlayerData(e.getPlayer());
        if(!Customs.isBlockedWorldbyCommand("back", e.getFrom().getWorld().getName().toLowerCase())) {
            if(pd.getLastPos() == null || 
                    (!pd.getLastPos().getWorld().getName().equals(e.getFrom().getWorld().getName()) || pd.getLastPos().distance(e.getFrom()) > 1.0) &&
                    (!e.getFrom().getWorld().getName().equals(e.getTo().getWorld().getName()) || e.getTo().distance(e.getFrom()) > 1.0))
                pd.setLastPos(e.getFrom());
        }
        
        if(Customs.getPlugin().getConfig().isList("forbidden-fly-worlds") && !e.getPlayer().hasPermission("customs.team")) {
            if(Customs.getPlugin().getConfig().getStringList("forbidden-fly-worlds").contains(e.getTo().getWorld().getName().toLowerCase())) {
                e.getPlayer().setFlying(false);
                e.getPlayer().setAllowFlight(false);
                e.getPlayer().sendMessage(Language.getMessage(Customs.getPlugin(), e.getPlayer().getUniqueId(), "function.fly.denied", "You have entered a non-flying world."));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;
        
        Player p = (Player)e.getEntity();
        PlayerData pd = PlayerData.getPlayerData(p);
        pd.setLastPos(p.getLocation());
    }
}