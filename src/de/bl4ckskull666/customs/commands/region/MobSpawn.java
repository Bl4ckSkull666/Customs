/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands.region;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RegionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class MobSpawn implements CommandExecutor {
    
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use." + c.getName())) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.noPerm","YOu don't have permission to use this command."));
            return true;
        }
        
        ProtectedRegion pr = RegionUtils.getOwnRegion(p);
        if(pr == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.needRegion","Please standing in your own Region to add/delete a member"));
            return true;
        }
        
        if(pr.getFlag(DefaultFlag.MOB_SPAWNING) != null && pr.getFlag(DefaultFlag.MOB_SPAWNING) == State.ALLOW) {
            pr.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobspawn.denied","Animal/Monster spawn successful denied."));
        } else {
            if(pr.getFlag(DefaultFlag.DENY_SPAWN) == null && !p.isOp()) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobspawn.noDenySpawn","You must set first /mobdeny before you can allow spawn animals/monsters on your plot."));
            } else {
                pr.setFlag(DefaultFlag.MOB_SPAWNING, State.ALLOW);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.mobspawn.allowed","Now it can be spawn Animals/Monsters on your plot."));
            }
        }
        return true;
    }
    
}
