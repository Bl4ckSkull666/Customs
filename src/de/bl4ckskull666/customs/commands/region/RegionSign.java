/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands.region;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.customs.utils.Tasks.changePriceOfRegion;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class RegionSign implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            Player p = (Player)s;
            puuid = ((Player)s).getUniqueId();;
            
            if(!p.hasPermission("customs.use.regionsign")) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.regionsign.noPermission", "You don't have permission to use this command."));
                return true;
            }
        }
        
        //need Sign Type (buy/rent/let) world:begins_with new_price
        if(a.length < 3) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.regionsign.needSyntax", "Please use /" + c.getName() + " <buy/rent/let> <{world:}region_begin> <price>", new String[] {"%comd%"}, new String[] {c.getName()}));
            return true;
        }
        
        World w = null;
        String region = "";
        int price = -1;
        String type = "";
        for(String str: a) {
            if(str.equalsIgnoreCase("buy") || str.equalsIgnoreCase("rent") || str.equalsIgnoreCase("let")) {
                type = str;
                continue;
            }
            if(Rnd.isNumeric(str)) {
                price = Integer.parseInt(str);
                continue;
            }
            String[] aStr = str.split(":");
            if(aStr.length == 2) {
                if(Bukkit.getWorld(aStr[0]) != null) {
                    w = Bukkit.getWorld(aStr[0]);
                    region = aStr[1];
                }
                if(Bukkit.getWorld(aStr[1]) != null) {
                    w = Bukkit.getWorld(aStr[1]);
                    region = aStr[0];
                }
            } else {
                if(!(s instanceof Player)) {
                    
                    return true;
                }
                w = ((Player)s).getWorld();
                region = str;
            }
        }
        
        if(region.isEmpty()) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.regionsign.needRegion", "Missing the begin of the Region."));
            return true;
        }
        
        if(price == -1) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.regionsign.needPrice", "Missing the new price"));
            return true;
        }
        
        if(type.isEmpty()) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.regionsign.needSignType", "What type you mean?"));
            return true;
        }
        
        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.regionsign.beginning", "Beginning with Process. Please wait a moment."));
        Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new changePriceOfRegion(s, puuid, w, region, type, price));
        return true;
    }
}