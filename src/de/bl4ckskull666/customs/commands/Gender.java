/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Gender implements CommandExecutor {
    private final ArrayList<String> _gender = new ArrayList<>();
    
    public Gender() {
        _gender.add("none");
        _gender.add("male");
        _gender.add("female");
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(a.length == 1) {
            if(!p.hasPermission("customs.use.gender")) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.gender.needPerm", "You don't have permission to set your gender."));
                return true;
            }
            
            String gender = Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.gender.genders." + a[0].toLowerCase(), a[0].toLowerCase());
            if(!_gender.contains(gender) && !gender.equalsIgnoreCase("none")) {
                //Gender not found
                String available = "";
                FileConfiguration f = Language.getMessageFile(Customs.getPlugin(), p.getUniqueId());
                if(f != null && f.isConfigurationSection("command.gender.genders")) {
                    for(String cmd : f.getConfigurationSection("command.gender.genders").getKeys(false))
                        available += (available.isEmpty()?"§e" + cmd:"§9, §e" + cmd);
                }
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.gender.available", "The following genders are available to use :"));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', available));
                return true;
            }
            
            if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                return true;

            if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                return true;

            if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                return true;
            
            pd.setGender(gender);
            pd.setVerify(false);
            Customs.sendPluginMessage(p, "gender", pd.getGender());
            Customs.sendPluginMessage(p, "verify", false);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.gender.successful", "You have set successful your gender."));
        } else
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.gender.wrongFormat", "Please add your gender after command."));
        return true;
    }
    
}
