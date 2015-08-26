/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Items;
import de.bl4ckskull666.customs.utils.KitTool;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import java.util.Map;
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
public class Kits implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        if(a.length == 1) {
            if(!(s instanceof Player)) {
                s.sendMessage("This command can be only run by Player.");
                return true;
            }
            
            Player p = (Player)s;
            PlayerData pd = PlayerData.getPlayerData(p);
            
            if(!Customs.isKit(a[0].toLowerCase())) {
                //No Kit exist
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.kit.notExist", "Can't find the given Kit Name."));
                return true;
            }
            
            if(!p.hasPermission("customs.use.kit." + a[0].toLowerCase())) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.kit.permission.missing", "You don't have permission to get the given Kit."));
                return true;
            }
            
            KitTool kt = Customs.getKit(a[0].toLowerCase());
            if(!Items.hasEnoughSize(p, kt.getItemsAsString().size())) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.kit.inventoryfull", "Your Inventory is too full."));
                return true;
            }
            
            if(!Customs.canCmdUseByPlayer(p, c.getName(), kt.getDelay(), c.getName() + "_" + a[0].toLowerCase()))
                return true;
            
            if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                return true;

            if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p, kt.getCost()))
                return true;
            
            p.getInventory().addItem(kt.getItems());
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.kit.successful.own", "You have got the kit successful."));
            if(kt.getDelay() > 0)
                pd.setTimeStamp("kit_" + kt.getName().toLowerCase(), System.currentTimeMillis());
            return true;
        } else if(a.length == 2) {
            Player p;
            KitTool kt;
            String lang = "default";
            if(s instanceof Player) {
                PlayerData pd = PlayerData.getPlayerData((Player)s);
                lang = pd.getLanguage();
                
                if(!s.hasPermission("customs.use.kit.other")) {
                    //No Permission to give other a kit
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.permission.other", "You don't have permission to give other player a kit."));
                    return true;
                }
            }
            if(Bukkit.getPlayer(a[0]) != null) {
                if(!Customs.isKit(a[1].toLowerCase())) {
                    //No Kit found
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.notExist",  "Can't find the given Kit Name."));
                    return true;
                }
                
                p = Bukkit.getPlayer(a[0]);
                kt = Customs.getKit(a[1].toLowerCase());
            } else if(Bukkit.getPlayer(a[1]) != null) {
                if(!Customs.isKit(a[0].toLowerCase())) {
                    //No Kit found
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.notExist",  "Can't find the given Kit Name."));
                    return true;
                }
                
                p = Bukkit.getPlayer(a[1]);
                kt = Customs.getKit(a[0].toLowerCase());
            } else {
                //No Player Foind
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.permission.noPlayerFound",  "Can't find the Player who you given."));
                return true;
            }
            
            if(s instanceof Player) {
                if(!s.hasPermission("customs.use.kit." + a[0].toLowerCase())) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.permission.missing", "You don't have permission to get the given Kit."));
                    return true;
                }
            }
            
            if(!Items.hasEnoughSize(p, kt.getItemsAsString().size())) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.inventoryfullother", "The inventory of %name% is too full to give the Kit.", new String[] {"%name%"}, new String[] {p.getName()}));
                return true;
            }
            
            if(s instanceof Player) {
                if(!Customs.canCmdUseByPlayer((Player)s, c.getName(), kt.getDelay(), c.getName() + "_" + a[0].toLowerCase()))
                    return true;
            
                if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), (Player)s, ((Player)s).getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), (Player)s, kt.getCost()))
                    return true;
            }
            p.getInventory().addItem(kt.getItems());
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.kit.successful.by", "You have got the Kit %kit% by %by%.", new String[] {"%by%", "%of%", "%kit%"}, new String[] {s.getName(), p.getName(), kt.getName()}));
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.succeasful.of", "You have given the Kit %kit% to %of% successfully.", new String[] {"%by%", "%of%", "%kit%"}, new String[] {s.getName(), p.getName(), kt.getName()}));
            if(kt.getDelay() > 0 && (s instanceof Player)) {
                PlayerData pd = PlayerData.getPlayerData((Player)s);
                pd.setTimeStamp("kit_" + kt.getName().toLowerCase(), System.currentTimeMillis());
            }
        } else {
            String kits = "";
            for(Map.Entry<String, KitTool> e : Customs.getKits().entrySet()) {
                String striked = "";
                if(s instanceof Player) {
                    Player p = (Player)s;
                    if(!p.hasPermission("customs.use.kit." + e.getKey()))
                        continue;
                    
                    if(!Customs.canCmdUseByPlayer((Player)s, c.getName(), e.getValue().getDelay(), c.getName() + "_" + e.getValue().getName()) || !Customs.canPaidIt(p, e.getValue().getCost()))
                        striked = "§m";
                }
                kits += (kits.isEmpty()?"§e" + striked:"§9, §e" + striked) + e.getKey() + ((e.getValue().getCost() > 0 && Customs.getEco() != null)?"§f(§c" + e.getValue().getCost() + " " + Customs.getEco().currencyNamePlural() + "§f)":"");
            }
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kit.available", "You can select the following kits at this time :"));
            s.sendMessage(kits);
        }
        return true;
    }
}
