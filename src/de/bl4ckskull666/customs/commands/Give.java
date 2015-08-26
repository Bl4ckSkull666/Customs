/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Items;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public class Give implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        Player p = null;
        String itemStr = "";
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        if(a.length >= 1) {
            for(String strA: a) {
                if(Bukkit.getPlayer(strA) != null) {
                    p = Bukkit.getPlayer(strA);
                } else if(strA.equalsIgnoreCase("me") && s instanceof Player) {
                    p = (Player)s;
                } else {
                    itemStr += itemStr.isEmpty()?strA:" " + strA;
                }
            }
        }
        
        if(p != null && !p.getName().equalsIgnoreCase(s.getName())) {
            if(!s.hasPermission("customs.use.give.other")) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.permission.other", "You dont have permission to give other players an item."));
                return true;
            }
        } else if(s instanceof Player && p == null || s instanceof Player && p.getName().equalsIgnoreCase(s.getName())) {
            p = (Player)s;
            if(!p.hasPermission("customs.use.give")) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.permission.own", "You dont have permission to use this command."));
                return true;
            }
        } else {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.unknownerror", "Unknown error. Please Report it to the Server Team. Thanks."));
            return true;
        }
        
        if(itemStr.isEmpty()) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.noItemString", "Please give in the Item string. Need min. Type and Amount."));
            return true;
        }
        
        ItemStack item = Items.getItem(itemStr);
        if(item == null) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.noItem", "Can't create item from your String. Please check your string."));
            return true;
        }
        
        int needPlace = 1;
        if(item.getAmount() > item.getMaxStackSize())
            needPlace = (int)Math.floor((item.getAmount()/item.getMaxStackSize()));
        
        if(!Items.hasEnoughSize(p, needPlace)) {
            if(p.getName().equalsIgnoreCase(s.getName()))
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.iNeedMorePlace", "You need more place in your Inventory."));
            else
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.pNeedMorePlace", "%name% needs more place in the Inventory.", new String[] {"%name%"}, new String[] {p.getName()}));
        }

        p.getInventory().addItem(item);
        if(s.getName().equalsIgnoreCase(p.getName()))
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.successful.own", "You have earn %amount%x%item%.", new String[] {"%amount%","%item%"}, new String[] {String.valueOf(item.getAmount()),item.getType().name().replace("_", " ")}));
        else {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.give.successful.to", "You have %to% given %amount%x%item%.", new String[] {"%to%","%amount%","%item%"}, new String[] {p.getName(),String.valueOf(item.getAmount()),item.getType().name().replace("_", " ")}));
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.give.successful.from", "%from% has given you %amount%x%item%", new String[] {"%from%","%amount%","%item%"}, new String[] {s.getName(),String.valueOf(item.getAmount()),item.getType().name().replace("_", " ")}));
        }
        return true;
    }
}
