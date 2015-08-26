/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Spawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }

        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        switch(c.getName().toLowerCase()) {
            case "setspawn":
                if(!p.hasPermission("customs.use.setspawn")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.permission.set", "You don't have permission to set a new Spawn-Point."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;

                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;
                
                if(a.length == 1) {
                    if(a[0].equalsIgnoreCase("first")) {
                        Customs.getPlugin().setFirstSpawnPoint(p.getLocation());
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.setfirst", "First Spawn-Point successful set to your Position."));
                    } else {
                        Customs.getPlugin().setSpawnPoint(p.getLocation());
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.set", "Spawn-Point successful set to your Position."));
                    }
                } else {
                    Customs.getPlugin().setSpawnPoint(p.getLocation());
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.set", "Spawn-Point successful set to your Position."));
                }
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                break;
            case "delspawn":
                if(!p.hasPermission("customs.use.delspawn")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.permission.rem", "You don't have permission to remove the Spawn-Point."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;

                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;
                
                if(a.length == 1) {
                    if(a[0].equalsIgnoreCase("first")) {
                        Customs.getPlugin().setFirstSpawnPoint(null);
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.remfirst", "First Spawn-Point successful removed."));
                    } else {
                        Customs.getPlugin().setSpawnPoint(null);
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.rem", "Spawn-Point successful removed."));
                    }
                } else {
                    Customs.getPlugin().setSpawnPoint(null);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.rem", "Spawn-Point successful removed."));
                }
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                break;
            case "spawn":
                if(!p.hasPermission("customs.use.spawn")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.permission.use", "You don't have permission to use this command."));
                    return true;
                }
                
                if(Customs.getPlugin().getSpawnPoint() == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.notSet", "No Spawn Point available at this time."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;

                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;

                if(a.length == 1) {
                    if(a[0].equalsIgnoreCase("first") && p.hasPermission("customs.use.spawn.first")) {
                        p.teleport(Customs.getPlugin().getFirstSpawnPoint());
                    } else {
                        Utils.teleportPlayer(p, Customs.getPlugin().getSpawnPoint());
                    }
                } else {
                    Utils.teleportPlayer(p, Customs.getPlugin().getSpawnPoint());
                }
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawn.use", "You're teleported to the Spawn-Point."));
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                break;
            default:
                p.sendMessage("Command not understand.");
        }
        return true;
    }
}
