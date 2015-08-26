/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.customs.utils.Utils;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Remove implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            puuid = ((Player)s).getUniqueId();
            
            if(!s.hasPermission("customs.use.remove")) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.noPermission", "You don't have permission to use this command."));
                return true;
            }
        }

        boolean isAll = false;
        String allIs = "";
        EntityType et = null;
        int radius = -1;
        Location loc = null;
        World w = null;
        if(a.length < 1) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.syntax", "Need min. a Item (EntityType) and max. Item(EntityType), Radius and World."));
            return true;
        }
        
        if(a[0].equalsIgnoreCase("list")) {
            String items = "";
            for(EntityType ent: EntityType.values()) {
                if(Utils.isItemEntity(ent) || Utils.isVehicle(ent))
                    items += items.isEmpty()?"§9" + ent.name().toLowerCase():"§e, §9" + ent.name().toLowerCase();
            }
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.list", "Here a small list of all available Items."));
            s.sendMessage(items);
            return true;
        }
        
        for(String str: a) {
            if(Utils.isEntityType(str)) {
                et = EntityType.valueOf(str.toUpperCase());
                if(!Utils.isItemEntity(et) && !Utils.isVehicle(et)) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.wrongType", "Wrong Type.."));
                    return true;
                }
            } else if(Bukkit.getWorld(str) != null)
                w = Bukkit.getWorld(str);
            else if(Rnd.isNumeric(str))
                radius = Integer.parseInt(str);
            else if(str.toLowerCase().equalsIgnoreCase("all") || str.toLowerCase().startsWith("item") || str.toLowerCase().startsWith("vehicle")) {
                isAll = true;
                allIs = str.toLowerCase();
            }
        }
        
        if(et == null && !isAll) {
            //Need EntityType or all
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.needEntity", "Can't find the given EntityType."));
            return true;
        }
        
        if(et != null && !isAll) {
            if(!Utils.isVehicle(et) && !Utils.isItemEntity(et)) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.wrongType", "The given Entity is not a Living Creature."));
                return true;
            }
        }
        
        if(w == null) {
            if(!(s instanceof Player)) {
                //Must be a Player or add World.
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.mustPlayer", "Can only run by player or add world."));
                return true;
            }
            
            w = ((Player)s).getLocation().getWorld();
            if(radius > -1)
                loc = ((Player)s).getLocation();
        }
        
        int rem = 0;
        for (LivingEntity ent : w.getLivingEntities()) {
            if(isAll || et != null && ent.getType().name().equalsIgnoreCase(et.name())) {
                if(Customs.getPlugin().isCitizens()) {
                    if(Customs.getPlugin().getNPC().getNPCRegistry().isNPC(ent))
                        continue;
                }
                
                if(allIs.startsWith("item") && !Utils.isItemEntity(ent.getType()) || 
                        allIs.startsWith("vehicle") && !Utils.isVehicle(ent.getType()) ||
                        !Utils.isItemEntity(ent.getType()) && !Utils.isVehicle(ent.getType()))
                    continue;

                if(radius == -1 || loc != null && (int)ent.getLocation().distance(loc) < radius) {
                    ent.remove();
                    rem++;
                }
            }
        }
        
        if(et == null)
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.successful.all", "%count% Items are removed.", new String[] {"%count%"}, new String[] {String.valueOf(rem)}));
        else
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.remove.successful.type", "%count% of %item% are found and removed.", new String[] {"%count%", "%item%"}, new String[] {String.valueOf(rem), et.name().replace("_", " ")}));
        return true;
    }
}