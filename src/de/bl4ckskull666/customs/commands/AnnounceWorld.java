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
 * @author Pappi
 */
public class AnnounceWorld implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(a.length < 1) {
            s.sendMessage(new String[] {"Missing Message!","Vermisse Nachricht!"});
            return true;
        }
        
        Player p = null;
        String w;
        
        int b;
        if(Bukkit.getWorld(a[0]) != null) {
            w = a[0];
            b = 1;
        } else {
            b = 0;
            if(!(s instanceof Player)) {
                s.sendMessage(new String[] {"This Command can't run from the console!","Dieser Befehl kann nicht von der Konsole benutzt werden!"});
                return true;
            } else {
                p = (Player)s;
                w = p.getLocation().getWorld().getName();
            }
        }
        
        if(!s.hasPermission("customcommands.announceWorld.*") && !s.hasPermission("customcommands.announceWorld." + w)) {
            s.sendMessage("Du hast keine Rechte den Befehl in dieser Welt auszuführen.");
            return true;
        }
        String str = "";
        if(a.length >= b) {
            str = a[b];
            if(a.length > b) {
                for(int i = (b+1); i < a.length; i++)
                    str += " " + a[i];
            }
        } else {
            s.sendMessage(new String[] {"Missing Message!","Vermisse Nachricht!"});
            return true;
        }
        
        for(Player pl: Bukkit.getWorld(w).getPlayers()) {
            pl.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
        }
        return true;
    }  
}
