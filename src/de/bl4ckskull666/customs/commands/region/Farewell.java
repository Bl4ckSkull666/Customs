/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands.region;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
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
 */
public class Farewell implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use." + c.getName())) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.noPerm","You don't have permission to use this command."));
            return true;
        }
        
        ProtectedRegion pr = RegionUtils.getOwnRegion(p);
        if(pr == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.needRegion","Please standing in your own Region to add/remove entity to deny-spawn list"));
            return true;
        }
        
        String msg = "";
        for(String str: a)
            msg += (msg.isEmpty()?"":" ") + str;
        
        if(msg.startsWith("+")) {
            msg = msg.substring(1, msg.length());
            if(pr.getFlag(DefaultFlag.FAREWELL_MESSAGE) != null && !pr.getFlag(DefaultFlag.FAREWELL_MESSAGE).isEmpty())
                msg = pr.getFlag(DefaultFlag.FAREWELL_MESSAGE) + msg;
        }
        
        if(msg.isEmpty()) {
            pr.setFlag(DefaultFlag.FAREWELL_MESSAGE, null);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.farewell.cleared","Unset Farewell message."));
        } else {
            pr.setFlag(DefaultFlag.FAREWELL_MESSAGE, msg);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.farewell.added","Farewell Message successful set."));
        }
        return true;
    }
}
