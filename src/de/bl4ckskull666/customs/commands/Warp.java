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
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Warp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }

        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        switch(c.getName().toLowerCase()) {
            case "warp":
                if(a.length < 1) {
                    String warps = "";
                    for(Map.Entry<String, Location> e : Customs.getWarps().entrySet()) {
                        if(p.hasPermission("customs.use.warp." + e.getKey()))
                            warps += (warps.isEmpty()?"§e":"§9, §e") + e.getKey();
                    }
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.warp.available", "You can select the following warp-points at this time :"));
                    p.sendMessage(warps);
                } else {
                    if(!Customs.isWarp(a[0].toLowerCase())) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.warp.notExist", "The wished Warp-Point not exist."));
                        return true;
                    }
                    
                    if(!p.hasPermission("customs.use.warp." + a[0].toLowerCase())) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.warp.permission.use", "You don't have permission to go to the wished Warp-Point."));
                        return true;
                    }
                    
                    if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                        return true;

                    if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                        return true;

                    if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                        return true;
                    
                    Utils.teleportPlayer(p, Customs.getWarp(a[0].toLowerCase()));
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.warp.successful", "You have been teleported to the wished Warp-Point."));
                    if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                        pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                }
                return true;
            case "setwarp":
                if(a.length < 1) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.setwarp.needName", "You have forgot to add a Warp-Point name."));
                    return true;
                }
                
                if(Customs.isWarp(a[0].toLowerCase())) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.setwarp.alreadyExist", "The wished Warp-Point already exist."));
                    return true;
                }
                
                if(!p.hasPermission("customs.use.setwarp")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.setwarp.permission.use", "You don't have permission to set a new Warp-Point."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;

                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;
                
                Customs.setWarp(a[0].toLowerCase(), p.getLocation());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.setwarp.successful", "Warp-Point successful set."));
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                break;
            case "delwarp":
                if(a.length < 1) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delwarp.needName", "You have forgot to given a Warp-Point name."));
                    return true;
                }
                
                if(!Customs.isWarp(a[0].toLowerCase())) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delwarp.notExist", "The Warp-Point you want to remove not exist."));
                    return true;
                }
                
                if(!p.hasPermission("customs.use.delwarp")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delwarp.permission.use", "You don't have permission to remove the Warp-Point."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;

                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;
                
                Customs.removeWarp(a[0].toLowerCase());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delwarp.successful", "Warp-Point successful removed."));
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                break;
            default:
                p.sendMessage("Internal Error - Warp");
                break;
        }
        return true;
    }
}
