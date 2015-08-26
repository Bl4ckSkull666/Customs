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
import java.util.logging.Level;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Home implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        switch(c.getName().toLowerCase()) {
            case "home":
                if(!p.hasPermission("customs.use.home")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.home.permission.use", "You don't have permission to use the /home command."));
                    return true;
                }
                
                if(a.length < 1) {
                    if(pd.getHomeCount() > 1) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.home.whatHome", "What of your home do you wish to go?"));
                        String[] h = pd.getHomes(true);
                        int i = 0;
                        String hList = "";
                        for(String home: h) {
                            hList += ((i == 0)?"§e":"§9, §e") + home;
                            i++;
                        }
                        p.sendMessage(hList);
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.home.selectOne", "To go to the wished home use /home (homename)"));
                        return true;
                    } else if(pd.getHomeCount() == 1) {
                        if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                            return true;

                        if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                            return true;

                        if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                            return true;

                        if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                            pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                        String[] h = pd.getHomes(false);
                        Utils.teleportPlayer(p, pd.getHome(h[0]));
                        return true;
                    } else {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.home.noHomes", "You must set first a home with /sethome (homename)"));
                        return true;
                    }
                } else {
                    if(!pd.isHome(a[0])) {
                        if(a[0].equalsIgnoreCase("list")) {
                            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.home.whatHome", "What of your home do you wish to go?"));
                            String[] h = pd.getHomes(true);
                            int i = 0;
                            String hList = "";
                            for(String home: h) {
                                hList += ((i == 0)?"§e":"§9, §e") + home;
                                i++;
                            }
                            p.sendMessage(hList);
                            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.home.selectOne", "To go to the wished home use /home (homename)"));
                        } else
                            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.home.homeNotExist", "The wished Home-Point don't exist."));
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
                    Utils.teleportPlayer(p, pd.getHome(a[0]));
                    return true;
                }
            case "sethome":
                if(a.length < 1) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.sethome.needSyntax", "Please add a Home name."));
                    return true;
                }
                
                if(!p.hasPermission("customs.use.sethome")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.sethome.permission.use", "You don't have permission to use the /sethome command."));
                    return true;
                }
                
                int mhc = hasHomePermissions(p)+pd.getHomeLimit();
                if(pd.getHomeCount() >= mhc) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.sethome.permission.max", "You don't have permission to add more Home-Points."));
                    return true;
                }
                
                if(pd.isHome(a[0])) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.sethome.alreadyExist", "The wished Home-Point already exist."));
                    return true;
                }
                
                if(a[0].equalsIgnoreCase("list")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.sethome.cantUse", "The wished Home-Point Name can't be used."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;
                    
                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;
                
                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;
                
                pd.setHome(a[0], p.getLocation());
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.sethome.successful", "Home-Point successful saved."));
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.sethome.haveNow", "You have now %have% of %max% Home-Points.", new String[] {"%have%", "%max%"}, new String[] {String.valueOf(pd.getHomeCount()), String.valueOf(mhc)}));
                break;
            case "delhome":
                if(a.length < 1) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delhome.needSyntax", "Please add a Home name who you want delete."));
                    return true;
                }
                
                if(!p.hasPermission("customs.use.delhome")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delhome.permission.use", "You don't have permission to use the /delhome command."));
                    return true;
                }
                
                if(!pd.isHome(a[0])) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delhome.notExist", "The given Home-Point don't exist."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;
                    
                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;
                
                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;
                
                pd.delHome(a[0]);
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delhome.successful", "Home-Point successful removed."));
                mhc = hasHomePermissions(p);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.delhome.haveNow", "You have now %have% of %max% Home-Points.", new String[] {"%have%", "%max%"}, new String[] {String.valueOf(pd.getHomeCount()), String.valueOf(mhc)}));
                break;
            default:
                p.sendMessage("Command not understand.");
                break;
        }
        return true;
    }
    
    private int hasHomePermissions(Player p) {
        String grp = "default";
        if(Customs.getPlugin().isGroupManager())
            grp = Customs.getPlugin().getGroupManager().getWorldsHolder().getWorldData(p).getUser(p.getName()).getGroupName().toLowerCase();
        return Customs.getPlugin().getConfig().getInt("homes." + grp, 3);
    }
}
