/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class GameMode implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        if(a.length > 0) {
            String type;
            Player pl;
            if(a.length <= 1) {
                if(!(s instanceof Player)) {
                    s.sendMessage("This command can only run by a player.");
                    return true;   
                }
                
                if(!s.hasPermission("customs.use.gamemode")) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.own", "You don't have permission to change gamemode."));
                    return true;
                }
                pl = (Player)s;
                type = a[0];
            } else {
                if(!s.hasPermission("customs.use.gamemode.other")) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.other", "You don't have permission to change gamemode."));
                    return true;
                }
                
                if(Bukkit.getPlayer(a[0]) != null) {
                    pl = Bukkit.getPlayer(a[0]);
                    type = a[1];
                } else if(Bukkit.getPlayer(a[1]) != null) {
                    pl = Bukkit.getPlayer(a[1]);
                    type = a[0];
                } else {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.noPlayerFound", "Can't find the given Player."));
                    return true;
                }
            }
            
            switch(type.toLowerCase()) {
                case "0":
                    if(!s.hasPermission("customs.use.gamemode.survival")) {
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.noModeRights", "You don't have permission to use the survival gamemode.", new String[] {"%gamemode%"}, new String[] {"survival"}));
                        return true;
                    }
                    pl.setGameMode(org.bukkit.GameMode.SURVIVAL);
                    if(!s.getName().equalsIgnoreCase(pl.getName())) {
                        pl.sendMessage(Language.getMessage(Customs.getPlugin(), pl.getUniqueId(), "command.gamemode.switch.by", "Your GameMode has been switched to survival mode by " + s.getName() + ".", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"survival", s.getName(), pl.getName()}));
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.of", "The GameMode of " + pl.getName() + " has been switched to survival mode.", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"survival", s.getName(), pl.getName()}));
                    } else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.own", "Your GameMode has been switched to survival mode.", new String[] {"%gamemode%"}, new String[] {"survival"}));
                    break;
                case "survival":
                    if(!s.hasPermission("customs.use.gamemode.survival")) {
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.noModeRights", "You don't have permission to use the survival gamemode.", new String[] {"%gamemode%"}, new String[] {"survival"}));
                        return true;
                    }
                    pl.setGameMode(org.bukkit.GameMode.SURVIVAL);
                    if(!s.getName().equalsIgnoreCase(pl.getName())) {
                        pl.sendMessage(Language.getMessage(Customs.getPlugin(), pl.getUniqueId(), "command.gamemode.switch.by", "Your GameMode has been switched to survival mode by " + s.getName() + ".", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"survival", s.getName(), pl.getName()}));
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.of", "The GameMode of " + pl.getName() + " has been switched to survival mode.", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"survival", s.getName(), pl.getName()}));
                    } else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.own", "Your GameMode has been switched to survival mode.", new String[] {"%gamemode%"}, new String[] {"survival"}));
                    break;
                case "1":
                    if(!s.hasPermission("customs.use.gamemode.creative")) {
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.noModeRights", "You don't have permission to use the survival gamemode.", new String[] {"%gamemode%"}, new String[] {"creative"}));
                        return true;
                    }
                    pl.setGameMode(org.bukkit.GameMode.CREATIVE);
                    if(!s.getName().equalsIgnoreCase(pl.getName())) {
                        pl.sendMessage(Language.getMessage(Customs.getPlugin(), pl.getUniqueId(), "command.gamemode.switch.by", "Your GameMode has been switched to creative mode by " + s.getName() + ".", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"creative", s.getName(), pl.getName()}));
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.of", "The GameMode of " + pl.getName() + " has been switched to creative mode.", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"creative", s.getName(), pl.getName()}));
                    } else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.own", "Your GameMode has been switched to creative mode.", new String[] {"%gamemode%"}, new String[] {"creative"}));
                    break;
                case "creative":
                    if(!s.hasPermission("customs.use.gamemode.creative")) {
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.noModeRights", "You don't have permission to use the survival gamemode.", new String[] {"%gamemode%"}, new String[] {"creative"}));
                        return true;
                    }
                    pl.setGameMode(org.bukkit.GameMode.CREATIVE);
                    if(!s.getName().equalsIgnoreCase(pl.getName())) {
                        pl.sendMessage(Language.getMessage(Customs.getPlugin(), pl.getUniqueId(), "command.gamemode.switch.by", "Your GameMode has been switched to creative mode by " + s.getName() + ".", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"creative", s.getName(), pl.getName()}));
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.of", "The GameMode of " + pl.getName() + " has been switched to creative mode.", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"creative", s.getName(), pl.getName()}));
                    } else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.own", "Your GameMode has been switched to creative mode.", new String[] {"%gamemode%"}, new String[] {"creative"}));
                    break;
                case "2":
                    if(!s.hasPermission("customs.use.gamemode.adventure")) {
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.noModeRights", "You don't have permission to use the survival gamemode.", new String[] {"%gamemode%"}, new String[] {"adventure"}));
                        return true;
                    }
                    pl.setGameMode(org.bukkit.GameMode.ADVENTURE);
                    if(!s.getName().equalsIgnoreCase(pl.getName())) {
                        pl.sendMessage(Language.getMessage(Customs.getPlugin(), pl.getUniqueId(), "command.gamemode.switch.by", "Your GameMode has been switched to adventure mode by " + s.getName() + ".", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"adventure", s.getName(), pl.getName()}));
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.of", "The GameMode of " + pl.getName() + " has been switched to adventure mode.", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"adventure", s.getName(), pl.getName()}));
                    } else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.own", "Your GameMode has been switched to adventure mode.", new String[] {"%gamemode%"}, new String[] {"adventure"}));
                    break;
                case "adventure":
                    if(!s.hasPermission("customs.use.gamemode.adventure")) {
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.permission.noModeRights", "You don't have permission to use the survival gamemode.", new String[] {"%gamemode%"}, new String[] {"adventure"}));
                        return true;
                    }
                    pl.setGameMode(org.bukkit.GameMode.ADVENTURE);
                    if(!s.getName().equalsIgnoreCase(pl.getName())) {
                        pl.sendMessage(Language.getMessage(Customs.getPlugin(), pl.getUniqueId(), "command.gamemode.switch.by", "Your GameMode has been switched to adventure mode by " + s.getName() + ".", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"adventure", s.getName(), pl.getName()}));
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.of", "The GameMode of " + pl.getName() + " has been switched to adventure mode.", new String[] {"%gamemode%", "%by%", "%of%"}, new String[] {"adventure", s.getName(), pl.getName()}));
                    } else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.gamemode.switch.own", "Your GameMode has been switched to adventure mode.", new String[] {"%gamemode%"}, new String[] {"adventure"}));
                    break;
                default:
                    s.sendMessage("Can't find the wished Gamemode.");
                    break;
            }
        } else {
            s.sendMessage("Please give in the wished Gamemode.");
        }
        return true;
    }
    
}
