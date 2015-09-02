/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Utils;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class UserInfo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        String infos;
        PlayerData pd;
        if(s instanceof Player) {
            Player p = (Player)s;
            PlayerData pdo = PlayerData.getPlayerData(p);
            
            if(!p.hasPermission("customs.use.userinfo" + (a.length > 0?".other":""))) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.userinfo.noPermission","You don't have permission to use this command."));
                return true;
            }
            
            if(a.length > 0) {
                pd = PlayerData.getPlayerData(a[0]);
                infos = pd.getName();
            } else {
                pd = pdo;
                infos = Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.userinfo.me","me");
            }
        } else {
            if(a.length == 0) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.userinfo.needUser","Please given a Playername."));
                return true;
            }
            pd = PlayerData.getPlayerData(a[0]);
            infos = pd.getName();
        }
        
        if(a.length > 2) {
            switch(a[1].toLowerCase()) {
                case "addhome":
                    pd.addHomeLimit();
                    s.sendMessage("Added Home-Point limit to " + pd.getName());
                    return true;
                case "delhome":
                    if(pd.getHomeLimit() < 1) {
                        s.sendMessage("Player " + pd.getName() + " has no extra Home-Point limit.");
                        return true;
                    }
                    pd.delHomeLimit();
                    s.sendMessage("You have taken a Home-Point limit from " + pd.getName());
                    return true;
                case "addplot":
                    if(a.length < 3) {
                        s.sendMessage("Please give us a World.");
                        return true;
                    }
                    if(Bukkit.getWorld(a[2]) == null) {
                        s.sendMessage("The given World don't exist.");
                        return true;
                    }
                    pd.addPlotLimit(a[2].toLowerCase());
                    s.sendMessage("Added a new Plot Slot on World " + a[2] + " to " + pd.getName());
                    return true;
                case "delplot":
                    if(a.length < 3) {
                        s.sendMessage("Please give us a World.");
                        return true;
                    }
                    if(Bukkit.getWorld(a[2]) == null) {
                        s.sendMessage("The given World don't exist.");
                        return true;
                    }
                    if(pd.getPlotLimit(a[2].toLowerCase()) < 1) {
                        s.sendMessage("Player " + pd.getName() + " has no extra plot slots on " + a[2]);
                        return true;
                    }
                    pd.delPlotLimit(a[2].toLowerCase());
                    s.sendMessage("Removed a Plot slot for Player " + pd.getName() + " on World " + a[2]);
                    return true;
                case "verify":
                    if(pd.getVerify()) {
                        pd.setVerify(false);
                        s.sendMessage("The Birthday and Gender is now no more marked as verified.");
                        if(Bukkit.getPlayer(UUID.fromString(pd.getUUID())) != null)
                            Customs.sendPluginMessage(Bukkit.getPlayer(UUID.fromString(pd.getUUID())), "verify", false);
                    } else {
                        pd.setVerify(true);
                        s.sendMessage("The Birthday and Gender is now marked as verified.");
                        if(Bukkit.getPlayer(UUID.fromString(pd.getUUID())) != null)
                            Customs.sendPluginMessage(Bukkit.getPlayer(UUID.fromString(pd.getUUID())), "verify", true);
                    }
                    return true;
                case "setgroup":
                    if(a.length < 3) {
                        s.sendMessage("Please give us a World.");
                        return true;
                    }
                    if(!GroupManager.getGMEventHandler().getPlugin().getWorldsHolder().getDefaultWorld().getGroups().containsKey(a[2])) {
                        s.sendMessage("Unknown Group.");
                        return true;
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    cal.add(Calendar.YEAR, 100);
                    pd.setSpecialGroupCal(a[2], cal);
                    return true;
                default:
                    break;
            }
            
        }
        
        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.userinfo.infosAbout","Informations over %name%", new String[] {"%name%"}, new String[] {infos}));
        if(!pd.getName().isEmpty())
            s.sendMessage("§eName §9: §6" + pd.getName());
        if(!pd.getUUID().isEmpty())
            s.sendMessage("§eUUID §9: §6" + pd.getUUID());
        if(pd.getTimeStamp("login") > 0L) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(pd.getTimeStamp("login"));
            s.sendMessage("§eLast Login §9: §6" + cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH)+1) + "." + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE)<10?"0":"") + cal.get(Calendar.MINUTE) + " o'Clock");
            if(pd.getLastPos() != null)
                s.sendMessage("§ePosition §9: §6" + pd.getLastPos().getWorld().getName() + " - X:" + pd.getLastPos().getBlockX() + " Y:" + pd.getLastPos().getBlockY() + " Z:" + pd.getLastPos().getBlockZ());
        }
        if(pd.getTimeStamp("logout") > 0L) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(pd.getTimeStamp("logout"));
            s.sendMessage("§eLast LogOut §9: §6" + cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH)+1) + "." + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE)<10?"0":"") + cal.get(Calendar.MINUTE) + " o'Clock");
            s.sendMessage("§ePosition §9: §6" + pd.getLogOutPos().getWorld().getName() + " - X:" + pd.getLogOutPos().getBlockX() + " Y:" + pd.getLogOutPos().getBlockY() + " Z:" + pd.getLogOutPos().getBlockZ());
        }
        if(pd.getAge() > -1) {
            s.sendMessage("§eAge §9: §6" + pd.getAge() + " (" + pd.getBirthday() + ")");
        }
        if(pd.getHomeCount() > 0) {
            for(String home: pd.getHomes(true)) {
                Location loc = pd.getHome(home);
                s.sendMessage("§eHome " + home + " §9: §6" + loc.getWorld().getName() + " X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
            }
        }
        if(pd.activExpDrop())
            s.sendMessage("§eExpDrop is enable.");
        if(pd.getHomeLimit() > 0)
            s.sendMessage("§eBuyed Home Points §9: §6" + pd.getHomeLimit());
        for(World w: Bukkit.getWorlds()) {
            if(pd.getPlotLimit(w.getName()) > 0)
                s.sendMessage("§eMore Plots on " + w.getName() + " §9: §6" + pd.getPlotLimit(w.getName()));
        }
        
        if(!pd.getSpecialGroup().isEmpty()) {
            s.sendMessage("§eSpecial Group §9: §6" + pd.getSpecialGroup());
            if(pd.getSpecialGroupCal() != null)
                s.sendMessage("§eSpecial Group end §9: §6" + Utils.getDateByCalendar(pd.getSpecialGroupCal()) + " " + Utils.getTimeByCalendar(pd.getSpecialGroupCal()));
        }
        
        if(pd.getPerms().size() > 0) {
            for(Map.Entry<String, Calendar> me: pd.getPerms().entrySet()) {
                s.sendMessage("§eSpecial Permission §9: §6" + me.getKey());
                if(pd.getSpecialGroupCal() != null)
                    s.sendMessage("§eSpecial Permission end §9: §6" + Utils.getDateByCalendar(me.getValue()) + " " + Utils.getTimeByCalendar(me.getValue()));
            }
        }
        
        if(!pd.getAchievments().isEmpty()) {
            Calendar cal = Calendar.getInstance();
            s.sendMessage("§eAchievments §9:");
            for(Map.Entry<String, Long> me: pd.getAchievments().entrySet()) {
                cal.setTimeInMillis(me.getValue());
                s.sendMessage("§6" + me.getKey() + " §9- §6" + Utils.getDateByCalendar(cal) + " " + Utils.getTimeByCalendar(cal));
            }
        }
        return true;
    }
}
