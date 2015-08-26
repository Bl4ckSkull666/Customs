/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Back implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can be only used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        if(!p.hasPermission("customs.use.back")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.back.permission.use", "You don't have permission to use this command."));
            return true;
        }
        
        if(pd.getLastPos() == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.back.noPosition", "Can't find your last Position to go Back."));
            return true;
        }
        
        if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
            return true;
                    
        if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
            return true;
                
        if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
            return true;
        
        if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
            pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
        p.teleport(pd.getLastPos());
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.back.successful", "You have been teleported back to the last saved position."));
        return true;
    }
    
}
