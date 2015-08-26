/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands.region;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RegionUtils;
import de.bl4ckskull666.customs.utils.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class MobDeny implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use." + c.getName())) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.noPerm","You don't have permission to use this command."));
            return true;
        }
        
        ProtectedRegion pr = RegionUtils.getOwnRegion(p);
        if(pr == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.needRegion","Please standing in your own Region to add/remove entity to deny-spawn list"));
            return true;
        }
        
        if(a.length < 1) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobdeny.needEntityTypes","Please give us min. one entity."));
            String list = "§9Clear§e, §9Animal§e, §9Monster";
            for(EntityType et: EntityType.values()) {
                if(!Utils.isAnimal(et) && !Utils.isMonster(et) && !Utils.isOtherCreature(et))
                    continue;
                list += "§e, §9" + Utils.upperFirst(et.name().toLowerCase());
            }
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobdeny.available","Here is a list of all available Entity's :"));
            p.sendMessage(list);
            return true;
        }
        
        Set<EntityType> sf = new HashSet<>();
        ArrayList<EntityType> given = new ArrayList<>();
        for(String str: a) {
            if(str.toLowerCase().startsWith("animal")) {
                for(EntityType et: EntityType.values())
                    if(Utils.isAnimal(et)) given.add(et);
                continue;
            } else if(str.toLowerCase().startsWith("monster")) {
                for(EntityType et: EntityType.values())
                    if(Utils.isMonster(et)) given.add(et);
                continue;
            } else if(str.equalsIgnoreCase("clear")) {
                pr.setFlag(DefaultFlag.DENY_SPAWN, null);
                pr.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobdeny.cleared","Cleared deny-spawn flag."));
                return true;
            } else if(!Utils.isEntityType(str)) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobdeny.ignored","Wrong Entity %entity%. Ignore it.", new String[] {"%entity%"}, new String[] {str}));
                continue;
            }
            given.add(EntityType.valueOf(str.toUpperCase()));
        }
        
        if(pr.getFlag(DefaultFlag.DENY_SPAWN) != null) {
            for(EntityType et: pr.getFlag(DefaultFlag.DENY_SPAWN)) {
                if(!given.contains(et)) {
                    //Hold it in list
                    sf.add(et);
                } else {
                    //Remove from list
                    given.remove(et);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobdeny.removed","Entity %entity% removed from deny-spawn list.", new String[] {"%entity%"}, new String[] {et.name().toLowerCase().replace("_", " ")}));
                }
            }
        }
        for(EntityType et: given) {
            //Add to list
            sf.add(et);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobdeny.added","Entity %entity% added to deny-spawn list.", new String[] {"%entity%"}, new String[] {et.name().toLowerCase().replace("_", " ")}));
        }
        
        if(sf.isEmpty()) {
            pr.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
            pr.setFlag(DefaultFlag.DENY_SPAWN, null);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobdeny.cleared","Cleared deny-spawn flag"));
        } else 
            pr.setFlag(DefaultFlag.DENY_SPAWN, sf);
        return true;
    }
}
