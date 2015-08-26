/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RFly;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Fly implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        //Is not play that use /fly 
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        if(a.length < 1) {
            if(!(s instanceof Player)) {
                s.sendMessage("This command can only run by a player.");
                return true;   
            }
            
            Player p = (Player)s;
            PlayerData pd = PlayerData.getPlayerData(p);
            if(!p.hasPermission("customs.use.fly") && !RFly.isRFly(p.getUniqueId().toString())) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.fly.permission.own", "You dont have permission to fly."));
                return true;
            }
            
            if(Customs.getPlugin().getConfig().isList("forbidden-fly-worlds") && !p.hasPermission("customs.team")) {
                if(Customs.getPlugin().getConfig().getStringList("forbidden-fly-worlds").contains(p.getWorld().getName().toLowerCase())) {
                    p.setFlying(false);
                    p.setAllowFlight(false);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.fly.command", "You are on a World where is fly not allow."));
                }
            }
            
            if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                return true;
                    
            if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                return true;
                
            if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                return true;
                
            if(p.getAllowFlight()) {
                p.setAllowFlight(false);
                p.setFlying(false);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.fly.inactive.own", "Fly is now deactivated."));
            } else {
                p.setAllowFlight(true);
                p.setFlying(true);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.fly.active.own", "Fly is now activated."));
            }
            
            if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
            return true;
        } else if(a.length == 1) {
            if(Bukkit.getPlayer(a[0]) != null) {
                if(!s.hasPermission("customs.use.fly.other")) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.permission.other", "You dont have permission to change it for others."));
                    return true;
                }
                
                Player sp = Bukkit.getPlayer(a[0]);
                
                if(s instanceof Player) {
                    if(!Customs.canCmdUseByPlayer((Player)s, c.getName().toLowerCase() + "_other"))
                        return true;

                    if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase() + "_other", (Player)s, sp.getWorld().getName()))
                        return true;

                    if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase() + "_other", (Player)s))
                        return true;
                    
                    PlayerData pd = PlayerData.getPlayerData((Player)s);
                    if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase() + "_other"))
                        pd.setTimeStamp(c.getName().toLowerCase() + "_other", System.currentTimeMillis());
                }
                
                PlayerData spd = PlayerData.getPlayerData(sp);
                if(sp.getAllowFlight()) {
                    sp.setAllowFlight(false);
                    sp.setFlying(false);
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.fly.inactive.by", "Your fly mode has been deactivated by %by%", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.inactive.of", "The fly mode of Player %name% is now deactivaed.", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
                } else {
                    sp.setAllowFlight(true);
                    sp.setFlying(true);
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.fly.active.by", "Your fly mode has been deactivated by %by%", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.active.of", "The fly mode of Player %name% is now deactivaed.", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
                }
                return true;
            } else if(a[0].equalsIgnoreCase("on") || a[0].equalsIgnoreCase("off") ) {
                if(!(s instanceof Player)) {
                    s.sendMessage("This command can only run by a player.");
                    return true;   
                }

                Player p = (Player)s;
                PlayerData pd = PlayerData.getPlayerData(p);
                if(!p.hasPermission("customs.use.fly")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.fly.permission.own", "You dont have permission to fly."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                    return true;

                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                    return true;

                if(a[0].equalsIgnoreCase("off")) {
                    p.setAllowFlight(false);
                    p.setFlying(false);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.fly.inactive.own", "Fly is now deactivated."));
                } else {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.fly.active.own", "Fly is now activated."));
                }
                
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase()))
                    pd.setTimeStamp(c.getName().toLowerCase(), System.currentTimeMillis());
                return true;
            } else {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.syntax.1", "Wrong Command format. Please use /%cmd% <playername> or  /\"%cmd% <on/off>", new String[] {"%cmd%"}, new String[] {c.getName()}));
            }
            
        } else if(a.length == 2) {
            String status;
            Player sp;
            if(Bukkit.getPlayer(a[0]) != null && (a[1].equalsIgnoreCase("on") || a[1].equalsIgnoreCase("off"))) {
                status = a[1];
                sp = Bukkit.getPlayer(a[0]);
            } else if(Bukkit.getPlayer(a[1]) != null && (a[0].equalsIgnoreCase("on") || a[0].equalsIgnoreCase("off"))) {
                status = a[0];
                sp = Bukkit.getPlayer(a[1]);
            } else {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.syntax.2", "Wrong Command format. Please use /%cmd% <playername> <on/off>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                return true;
            }
            
            if(s instanceof Player) {
                if(!Customs.canCmdUseByPlayer((Player)s, c.getName().toLowerCase() + "_other"))
                    return true;

                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase() + "_other", (Player)s, sp.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase() + "_other", (Player)s))
                    return true;
                    
                PlayerData pd = PlayerData.getPlayerData((Player)s);
                if(Customs.getPlugin().getConfig().isInt("cmdCoolDown." + c.getName().toLowerCase() + "_other"))
                    pd.setTimeStamp(c.getName().toLowerCase() + "_other", System.currentTimeMillis());
            }
            
            PlayerData spd = PlayerData.getPlayerData(sp);
            if(status.equalsIgnoreCase("off")) {
                sp.setAllowFlight(false);
                sp.setFlying(false);
                sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.fly.inactive.by", "Your fly mode has been deactivated by %by%", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.inactive.of", "The fly mode of Player %name% is now deactivaed.", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
            } else {
                sp.setAllowFlight(true);
                sp.setFlying(true);
                sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.fly.active.by", "Your fly mode has been deactivated by %by%", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.active.of", "The fly mode of Player %name% is now deactivaed.", new String[] {"%by%", "%name%"}, new String[] {s.getName(), sp.getName()}));
            }
            return true;
        } else {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.fly.syntax.2", "Wrong Command format. Please use /%cmd% <playername> <on/off>", new String[] {"%cmd%"}, new String[] {c.getName()}));
        }
        
        return true;
    }
}
