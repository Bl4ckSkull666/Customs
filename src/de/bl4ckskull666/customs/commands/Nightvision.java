/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author PapaHarni
 */
public class Nightvision implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
            
        if(a.length == 1) {
            if(Bukkit.getPlayer(a[0]) != null) {
                setStatus(Bukkit.getPlayer(a[0]), s, "auto");
            } else if(a[0].equalsIgnoreCase("on") || a[0].equalsIgnoreCase("off")) {
                if(!(s instanceof Player)) {
                    //Error , need Player
                    s.sendMessage("This command can be run only by a player.");
                    return true;
                }
                setStatus((Player)s, s, a[0]);
            } else {
                //Error, understand syntax
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.nightvision.wrongSyntax.1", "Please use /%cmd% <player/on/off>", new String[] {"%cmd%"}, new String[] {c.getName()}));
            }
        } else if(a.length == 2) {
            if(Bukkit.getPlayer(a[0]) != null &&(a[1].equalsIgnoreCase("on") || a[1].equalsIgnoreCase("off"))) {
                setStatus(Bukkit.getPlayer(a[0]), s, a[1]);
            } else if(Bukkit.getPlayer(a[1]) != null &&(a[0].equalsIgnoreCase("on") || a[0].equalsIgnoreCase("off"))) {
                setStatus(Bukkit.getPlayer(a[1]), s, a[0]);
            } else {
                //Error, understand syntax
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.nightvision.wrongSyntax.2", "Please use /%cmd% <player> <on/off>", new String[] {"%cmd%"}, new String[] {c.getName()}));
            }
        } else {
            if(!(s instanceof Player)) {
                //Error , need Player
                s.sendMessage("This command can be run only by a player.");
                return true;
            }
            setStatus((Player)s, s, "auto");
        }
        return true;
    }
        
    private void setStatus(Player p, CommandSender s, String typ) {
        String isOther = s.getName().equalsIgnoreCase(p.getName())?"":".other";
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        if(s instanceof Player) {
            Player sp = (Player)s;
            if(!sp.hasPermission("customs.use.nightvision" + isOther)) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.nightvision.noPerm", "You don't have permission to use this command."));
                return;
            }
            
            if(!Customs.canCmdUseByPlayer(sp, "nightvision"))
                return;

            if(Customs.isBlockedWorldbyCommand("nightvision", sp, sp.getWorld().getName()))
                return;

            if(!Customs.hasPaidForUseCommand("nightvision", sp))
                return;
        }
        
        if(typ.equalsIgnoreCase("on") || typ.equalsIgnoreCase("auto") && !p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
            if(s.getName().equalsIgnoreCase(p.getName()))
                s.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.nightvision.successful.active.own", "You can now see better in the night."));
            else {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.nightvision.successful.active.of", "%of% can see now better in the night.", new String[] {"%of%", "%by%"}, new String[] {p.getName(), s.getName()}));
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.nightvision.active.successful.by", "You can now see good in the night by %by%.", new String[] {"%of%", "%by%"}, new String[] {p.getName(), s.getName()}));
            }
        } else {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            if(s.getName().equalsIgnoreCase(p.getName()))
                s.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.nightvision.successful.inactive.own", "You can't now more see good in the night."));
            else {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.nightvision.successful.inactive.of", "%of% can't now more good see in the night.", new String[] {"%of%", "%by%"}, new String[] {p.getName(), s.getName()}));
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.nightvision.successful.inactive.by", "You can't more good see in the night by %by%.", new String[] {"%of%", "%by%"}, new String[] {p.getName(), s.getName()}));
            }
        }
    }
    
}
