/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.Database;
import de.bl4ckskull666.customs.LoadAndSave;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.customs.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author PapaHarni
 */
public class Creload implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!s.hasPermission("customs.use.creload")) {
            s.sendMessage("You don't have permission to use this command.");
            return true;
        }
        
        if(a.length >= 1) {
            switch(a[0].toLowerCase()) {
                case "loadwarps":
                    LoadAndSave.loadWarps(Customs.getPlugin());
                    s.sendMessage("Warp-Points have been reloaded.");
                    break;
                case "savewarps":
                    LoadAndSave.saveWarps(Customs.getPlugin());
                    s.sendMessage("Warp-Points successful saved.");
                    break;
                case "loadkits":
                    LoadAndSave.loadKits(Customs.getPlugin());
                    s.sendMessage("Kits have been successful loaded.");
                    break;
                case "loadregions":
                    LoadAndSave.loadRegions(Customs.getPlugin());
                    s.sendMessage("The Regionstatus has been updated.");
                    break;
                case "config":
                    Customs.getPlugin().reloadConfig();
                    s.sendMessage("The Config has been reloaded.");
                    break;
                case "checkdb":
                    if(Customs.getMySQL().checkServerDBConnection()) {
                        s.sendMessage("Database connect works successful.");
                    } else {
                        s.sendMessage("It's happend an error withe the Database. Please check it.");
                    }
                    break;
                case "loadbooks":
                    LoadAndSave.loadBooks(Customs.getPlugin());
                    s.sendMessage("Books have been successful reloaded.");
                    break;
                case "pluginreload":
                    if(a.length <= 1) {
                        //Need PluginName
                        s.sendMessage("Need a Plugin Name to reload it.");
                        return true;
                    }
                    Plugin pl = Bukkit.getPluginManager().getPlugin(a[1]);
                    if(pl == null) {
                        //Can't find the plugin.
                        s.sendMessage("Can't find the given Plugin.");
                        return true;
                    }
                    
                    s.sendMessage("Beginning with restart the wished plugin.");
                    Bukkit.getPluginManager().disablePlugin(pl);
                    Bukkit.getPluginManager().enablePlugin(pl);
                    s.sendMessage("Hope the wished Plugin is reloaded successful.");
                    break;
                case "mem":
                    Runtime rt = Runtime.getRuntime();
                    s.sendMessage("Using Memory : " + Rnd.bytesToMegabytes((rt.totalMemory()-rt.freeMemory())));
                    s.sendMessage("Free Memory  : " + Rnd.bytesToMegabytes(rt.freeMemory()));
                    s.sendMessage("Total Memory : " + Rnd.bytesToMegabytes(rt.totalMemory()));
                    s.sendMessage("Max Memory   : " + Rnd.bytesToMegabytes(rt.maxMemory()));
                    break;
                case "tick":
                    s.sendMessage("Starting Tick test.");
                    Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new Tasks.checkMyTicks(s, 0, System.currentTimeMillis()), 1L);
                    break;
                case "help":
                    Help.loadHelp();
                    s.sendMessage("Help file has been reloaded. *hope*");
                    break;
                default:
                    s.sendMessage("Please use /creload {language/loadwarps/savewarps/loadkits/savekits/loadregions/loadbooks/config/checkdb}");
                    break;
            }
        } else
            s.sendMessage("Please use /creload {language/loadwarps/savewarps/loadkits/savekits/loadregions}");
        return true;
    }
}
