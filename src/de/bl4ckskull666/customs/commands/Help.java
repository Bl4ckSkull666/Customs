/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.LoadAndSave;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.mu1ti1ingu41.UUIDLanguages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Help implements CommandExecutor {
    private static FileConfiguration _fc;
    private static long _lastLoad;
    
    public Help() {
        loadHelp();
    }
    
    public static void loadHelp() {
        _fc = LoadAndSave.loadHelp();
        _lastLoad = System.currentTimeMillis();
    }
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can be only run by a player.");
            return true;
        }
        
        Player p = (Player)s;
        
        if(_lastLoad <= (System.currentTimeMillis()-(1000*60*60))) {
            _fc = LoadAndSave.loadHelp();
            _lastLoad = System.currentTimeMillis();
        }
        
        if(_fc == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.help.error", "Error on load help. Please Inform the Team."));
            return true;
        }
        
        String language = UUIDLanguages.getPlayerLanguage(p.getUniqueId());
        if(!_fc.isConfigurationSection(language))
            language = "default";
        
        
        String section = "index";
        int page = 1;
        if(a.length > 0) {
            for(String str: a) {
                if(Rnd.isNumeric(str) && Integer.parseInt(str) >= 1) {
                    page = Integer.parseInt(str);
                    continue;
                }
                if(_fc.isConfigurationSection(language + "." + str.toLowerCase()))
                    section = str.toLowerCase();
            }
        }
        
        if(!_fc.isList(language + "." + section + "." + page)) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.help.notexist", "The wished Page can't be found."));
            return true;
        }
        
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.help.header", "&e=============== &6Help Menu &e==============="));
        for(String msg: _fc.getStringList(language + "." + section + "." + page))
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        
        int max = 1;
        for(String k: _fc.getConfigurationSection(language + "." + section).getKeys(false)) {
            if(Rnd.isNumeric(k))
                max = Integer.parseInt(k);
        }
        
        if(max > 1)
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.help.page", "Page %cur% of %max% pages.", new String[] {"%cur%", "%max%"}, new String[] {String.valueOf(page), String.valueOf(max)}));
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.help.footer", "&e=============== &6Help Menu &e==============="));
        return true;
    }
}
