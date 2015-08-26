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

public class Kill implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player) {
            puuid = ((Player)s).getUniqueId();
            if(!s.hasPermission("customs.use.kill")) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.noPermission", "You don't have permission to use this command."));
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
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.syntax", "Need min. a EntityType and max. EntityType, Radius and World."));
            return true;
        }
        
        if(a[0].equalsIgnoreCase("list")) {
            String creatures = "";
            for(EntityType ent: EntityType.values()) {
                if(Utils.isMonster(ent) || Utils.isAnimal(ent) || Utils.isOtherCreature(ent) && ent != EntityType.PLAYER)
                    creatures += creatures.isEmpty()?"§9" + ent.name().toLowerCase():"§e, §9" + ent.name().toLowerCase();
            }
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.list", "Here a small list of all available Creatures."));
            s.sendMessage(creatures);
            return true;
        }
        
        for(String str: a) {
            if(Utils.isEntityType(str)) {
                et = EntityType.valueOf(str.toUpperCase());
                if(!Utils.isMonster(et) && !Utils.isAnimal(et) && Utils.isOtherCreature(et) || et == EntityType.PLAYER) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.noPlayer", "This command is not to kill Players."));
                    return true;
                }
            } else if(Bukkit.getWorld(str) != null)
                w = Bukkit.getWorld(str);
            else if(Rnd.isNumeric(str))
                radius = Integer.parseInt(str);
            else if(str.toLowerCase().equalsIgnoreCase("all") || str.toLowerCase().startsWith("monster") || str.toLowerCase().startsWith("animal")) {
                isAll = true;
                allIs = str.toLowerCase();
            }
        }
        
        if(et == null && !isAll) {
            //Need EntityType or all
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.needEntity", "Can't find the given EntityType."));
            return true;
        }
        
        if(et != null && !isAll) {
            if(!Utils.isMonster(et) && !Utils.isAnimal(et) && !Utils.isOtherCreature(et)) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.wrongType", "The given Entity is not a Living Creature."));
                return true;
            }
        }
        
        if(w == null) {
            if(!(s instanceof Player)) {
                //Must be a Player or add World.
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.mustPlayer", "Can only run by player or add world."));
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
                
                if(allIs.startsWith("monster") && !Utils.isMonster(ent.getType()) || 
                        allIs.startsWith("animal") && !Utils.isAnimal(ent.getType()) ||
                        !Utils.isMonster(ent.getType()) && !Utils.isAnimal(ent.getType()) && !Utils.isOtherCreature(ent.getType())||
                        ent.getType() == EntityType.PLAYER)
                    continue;

                if(radius == -1 || loc != null && (int)ent.getLocation().distance(loc) < radius) {
                    ent.remove();
                    rem++;
                }
            }
        }
         
        if(et == null)
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.successful.all", "%count% creatures on %world% are removed.", new String[] {"%count%", "%world%"}, new String[] {String.valueOf(rem), w.getName()}));
        else
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.kill.successful.type", "%count% of %creature% are found and removed on %world%.", new String[] {"%count%", "%world%", "%creature%"}, new String[] {String.valueOf(rem), w.getName(), et.name().replace("_", " ")}));
        return true;
    }
}