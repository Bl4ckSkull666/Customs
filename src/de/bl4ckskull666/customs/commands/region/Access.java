/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands.region;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RegionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 * /region flag Grundstücksname entry allow/deny
 */
public class Access implements CommandExecutor {
    
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use." + c.getName())) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.noPerm","YOu don't have permission to use this command."));
            return true;
        }
        
        ProtectedRegion pr = RegionUtils.getOwnRegion(p);
        if(pr == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.needRegion","Please standing in your own Region to add/delete a member"));
            return true;
        }
        
        String msg = "";
        for(String str: a)
            msg += (msg.isEmpty()?"":" ") + str;
        
        if(msg.startsWith("+")) {
            msg = msg.substring(1, msg.length());
            if(pr.getFlag(DefaultFlag.ENTRY_DENY_MESSAGE) != null && !pr.getFlag(DefaultFlag.ENTRY_DENY_MESSAGE).isEmpty())
                msg = pr.getFlag(DefaultFlag.ENTRY_DENY_MESSAGE) + msg;
        }
        
        if(msg.equalsIgnoreCase("clear")) {
            pr.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, null);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.access.cleared","Clear player access deny message."));
        } else if(!msg.isEmpty()) {
            pr.setFlag(DefaultFlag.ENTRY_DENY_MESSAGE, msg);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.access.added","Set successful player access deny message."));
        } else if(pr.getFlag(DefaultFlag.ENTRY) != null && pr.getFlag(DefaultFlag.ENTRY) == StateFlag.State.ALLOW) {
            pr.setFlag(DefaultFlag.ENTRY, StateFlag.State.DENY);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.access.denied","Deny access of Player to your plot."));
        } else {
            pr.setFlag(DefaultFlag.ENTRY, StateFlag.State.ALLOW);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.access.allowed","Allow access of players to your plot"));
        }
        return true;
    }   
}