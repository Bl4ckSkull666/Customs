/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class ClearInventory implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used only by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        if(a.length > 0) {
            if(!p.hasPermission("customs.use.clearinventory.other")) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.clearinventory.permission.other", "You don't have permission to clear inventory of other players."));
                return true;
            }
            
            Player sp = Bukkit.getPlayer(a[0]);
            if(sp == null) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.clearinventory.noPlayerFound", "Can't find the given Player %name%", new String[] {"%name%"}, new String[] {a[0]}));
                return true;
            }
            PlayerData sppd = PlayerData.getPlayerData(sp);
            if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase() + "_other"))
                return true;
                    
            if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase() + "_other", p, sp.getWorld().getName()))
                return true;
                
            if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase() + "_other", p))
                return true;
            
            sp.getInventory().clear();
            if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase() + "_other"))
                pd.setTimeStamp(c.getName().toLowerCase() + "_other", System.currentTimeMillis());
            sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.clearinventory.clearedby", "Your Inventory is cleared by %by%.", new String[] {"%by%", "%name%"}, new String[] {p.getName(), sp.getName()}));
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.clearinventory.clearedof", "Inventory of %name% has been cleared.", new String[] {"%by%", "%name%"}, new String[] {p.getName(), sp.getName()}));
        } else {
            if(!p.hasPermission("customs.use.clearinventory")) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.clearinventory.permission.own", "You don't have permission to clear your inventory."));
                return true;
            }
            
            if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                return true;
                    
            if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                return true;
                
            if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                return true;
            
            p.getInventory().clear();
            if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.clearinventory.clearedown", "Inventory has been cleared."));
        }
        return true;
    }
}
