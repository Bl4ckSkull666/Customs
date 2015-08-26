/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Broadcast implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!s.hasPermission("customs.use.broadcast")) {
            s.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if(a.length < 1) {
            s.sendMessage("Need a message.");
            return true;
        }

        String msg = "";
        for(String str: a)
            msg += msg.isEmpty()?str:" " + str;

        for(Player p: Bukkit.getOnlinePlayers())
            p.sendMessage("§f[§2Broadcast§f] §a" + ChatColor.translateAlternateColorCodes('&', msg));
        return true;
    }
    
}
