/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Tp implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can be only run by a Player.");
            return false;
        }
        Player p = (Player)s;
        
        switch(c.getName().toLowerCase()) {
            case "tphere":
                if(a.length > 1) {
                    if(!p.hasPermission("customs.use.tphere.more")) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tphere.noPermissionMore", "You don't have permission to Teleport a player to your position."));
                        return true;
                    }
                } else if(a.length == 1) {
                    if(!p.hasPermission("customs.use.tphere")) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tphere.noPermission", "You don't have permission to Teleport a player to your position."));
                        return true;
                    }
                } else {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tphere.missingPlayer", "Please give us a Player name."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, "tphere"))
                    return true;

                if(Customs.isBlockedWorldbyCommand("tphere", p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand("tphere", p))
                    return true;
                
                for(String strP: a) {
                    if(Bukkit.getPlayer(strP) == null) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tphere.playerNotExist", "Can't find the given player."));
                    } else {
                        Player gp = Bukkit.getPlayer(strP);
                        gp.sendMessage(Language.getMessage(Customs.getPlugin(), gp.getUniqueId(), "command.tphere.goTo", "You are teleported to %name%", new String[] {"%name%"}, new String[] {p.getName()}));
                        Utils.teleportPlayer(gp, p.getLocation());
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tphere.successful", "The Player %name% has been teleported to you.", new String[] {"%name%"}, new String[] {gp.getName()}));
                    }
                }
                break;
            case "tp":
                if(!p.hasPermission("customs.use.tp")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tp.noPermission", "You don't have permission to Teleport a player to your position."));
                    return true;
                }
                
                if(Bukkit.getPlayer(a[0]) == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tp.playerNotExist", "Can't find the given player %name%.", new String[] {"%name%"}, new String[] {a[0]}));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, "tp"))
                    return true;

                if(Customs.isBlockedWorldbyCommand("tp", p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand("tp", p))
                    return true;
                
                Player gp = Bukkit.getPlayer(a[0]);
                if(!p.hasPermission("customs.use.tp.spy"))
                    gp.sendMessage(Language.getMessage(Customs.getPlugin(), gp.getUniqueId(), "command.tp.toYou", "%name% has teleports to you.", new String[] {"%name%"}, new String[] {p.getName()}));
                Utils.teleportPlayer(p, gp.getLocation());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tp.successfulTo", "You have been successful teleported to %name%", new String[] {"%name%"}, new String[] {gp.getName()}));
                
                if(a.length > 1) {
                    if(!p.hasPermission("customs.use.tp.more")) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tp.noPermissionMore", "You don't have permission to teleport other players to a other player too"));
                        break;
                    }
                    for(int i = 1; i < a.length; i++) {
                        if(Bukkit.getPlayer(a[i]) == null) {
                            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tp.playerNotExist", "Can't find the given player %name%.", new String[] {"%name%"}, new String[] {a[i]}));
                        } else {
                            Player ggp = Bukkit.getPlayer(a[i]);
                            if(ggp.isInsideVehicle()) {
                                Entity v = ggp.getVehicle();
                                ggp.leaveVehicle();
                                v.remove();
                            }
                            Utils.teleportPlayer(ggp, gp.getLocation());
                            ggp.sendMessage(Language.getMessage(Customs.getPlugin(), ggp.getUniqueId(), "command.tp.goTo", "You are teleported to %name%", new String[] {"%name%","%by%"}, new String[] {gp.getName(),p.getName()}));
                            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tp.successfulGoTo", "%name% are teleported to %to%", new String[] {"%name%","%to%"}, new String[] {ggp.getName(),gp.getName()}));
                        }
                    }
                }
                break;
            default:
                p.sendMessage("Unknown Command.");
        }
        return true;
    }
}
