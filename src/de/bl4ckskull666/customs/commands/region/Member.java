/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands.region;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RegionUtils;
import de.bl4ckskull666.customs.utils.Utils;
import de.bl4ckskull666.uuiddatabase.UUIDDatabase;
import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Member implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can be only used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use.member")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.noPerm","YOu don't have permission to use this command."));
            return true;
        }
        
        ProtectedRegion pr = RegionUtils.getOwnRegion(p);
        if(pr == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.needRegion","Please standing in your own Region to add/delete a member"));
            return true;
        }
        
        if(a.length < 1) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.members.needPlayer","Please give us min. one playername."));
            return true;
        }
        
        ArrayList<String> added = new ArrayList<>();
        ArrayList<String> removed = new ArrayList<>();
        ArrayList<String> error = new ArrayList<>();
        
        for(String player: a) {
            LocalPlayer lp = null;
            if(Bukkit.getPlayer(player) != null) {
                lp = Customs.getPlugin().getWG().wrapPlayer(Bukkit.getPlayer(player));
            } else {
                String pUUID = UUIDDatabase.getUUIDByName(player);
                try {
                    if(pUUID.length() == 32)
                        pUUID = pUUID.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
                    UUID uuid = UUID.fromString(pUUID);
                    if(Bukkit.getOfflinePlayer(uuid) != null)
                        lp = Customs.getPlugin().getWG().wrapOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
                } catch(Exception ex) {}
            }
            
            if(lp != null) {
                if(pr.isMember(lp)) {
                    removed.add(lp.getName());
                    pr.getMembers().removePlayer(lp);
                } else {
                    added.add(lp.getName());
                    pr.getMembers().addPlayer(lp);
                }
            } else {
                error.add(player);
            }
        }
        
        if(removed.size() > 0)
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.members.deleted","Removed successful %names% §rfrom your Region.", new String[] {"%names%"}, new String[] {Utils.toString(removed)}));
        if(added.size() > 0)
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.members.added","Added successful %names% §rto your Region.", new String[] {"%names%"}, new String[] {Utils.toString(added)}));
        if(error.size() > 0) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.members.unknown.1","Can't add/remove the following given members :"));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Utils.toString(error)));
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.members.unknown.2","You can inform a Team member to help on this error."));
        }
        return true;
    }
}
