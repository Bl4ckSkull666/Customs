/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Expdrop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        Player p;
        String type = "toggle";
        String lang = "default";
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            puuid = ((Player)s).getUniqueId();
        }
        
        if(a.length > 0) {
            if(a.length == 1) {
                if(Bukkit.getPlayer(a[0]) != null) {
                    p = Bukkit.getPlayer(a[0]);
                } else if(a[0].equalsIgnoreCase("on") || a[0].equalsIgnoreCase("off") || a[0].equalsIgnoreCase("toggle")) {
                    if(!(s instanceof Player)) {
                        //Must be a Player
                        s.sendMessage("This command can be only run by a player.");
                        return true;
                    }
                    type = a[0].toLowerCase();
                    p = (Player)s;
                } else {
                    //Wrong Syntax
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.syntax.1", "Please use /%cmd% <player/on/off>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                    return true;
                }
            } else if(a.length == 2) {
                if(Bukkit.getPlayer(a[0]) != null && (a[1].equalsIgnoreCase("on") || a[1].equalsIgnoreCase("off") || a[1].equalsIgnoreCase("toggle"))) {
                    p = Bukkit.getPlayer(a[0]);
                    type = a[1].toLowerCase();
                } else if(Bukkit.getPlayer(a[1]) != null && (a[0].equalsIgnoreCase("on") || a[0].equalsIgnoreCase("off") || a[0].equalsIgnoreCase("toggle"))) {
                    p = Bukkit.getPlayer(a[1]);
                    type = a[0].toLowerCase();
                } else {
                    //Wrong Syntax
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.syntax.2", "Please use /%cmd% <player> <on/off>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                    return true;
                }
            } else {
                //Too many parameters
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.toManyParameters", "Please use /%cmd% <player> <on/off> or /%cmd% <player/on/off> or without parameters.", new String[] {"%cmd%"}, new String[] {c.getName()}));
                return true;
            }
        } else {
            if(!(s instanceof Player)) {
                s.sendMessage("This command can be only run by a player.");
                return true;
            }
            p = (Player)s;
        }
        
        if(s instanceof Player) {
            Player ps = (Player)s;
            if(!ps.hasPermission("customs.use.expdrop" + (ps.getName().equalsIgnoreCase(p.getName())?"":".other"))) {
                ps.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.permission." + (ps.getName().equalsIgnoreCase(p.getName())?"own":"other"), l));
                return true;
            }
        }
        
        PlayerData ppd = PlayerData.getPlayerData(p);
        
        if(type.equalsIgnoreCase("toggle") && ppd.activExpDrop() || type.equalsIgnoreCase("off")) {
            //Deactivate Drop
            ppd.setExpDrop(false);
            if(s.getName().equalsIgnoreCase(p.getName())) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.deactive.own", "On Monster kill you will earn now experience bullets."));
            } else {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.deactive.of", "%name% will be no more own experience bottles from monster kills.", new String[] {"%name%"}, new String[] {p.getName()}));
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.expdrop.deactive.by", "%name% has set your experience drop to bullets now.", new String[] {"%name%"}, new String[] {s.getName()}));
            }
        } else {
            ppd.setExpDrop(true);
            if(s.getName().equalsIgnoreCase(p.getName())) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.active.own", "On Monster kill you will earn now experience bullets."));
            } else {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.expdrop.active.of", "%name% will be now own experience bottles by killing monsters.", new String[] {"%name%"}, new String[] {p.getName()}));
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.expdrop.active.by", "%name% has set your experience drop to bottles now.", new String[] {"%name%"}, new String[] {s.getName()}));
            }
        }
        return true;
    }
    
}
