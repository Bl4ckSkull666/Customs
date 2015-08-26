/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Utils;
import java.util.UUID;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class SetAchievement implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        String type = "";
        Player p = null;
        Achievement achi = null;
        
        for(String str: a) {
            if(str.equalsIgnoreCase("give") || str.equalsIgnoreCase("take")) {
                type = str.toLowerCase();
            } else if(Bukkit.getPlayer(str) != null) {
                p = Bukkit.getPlayer(str);
            } else if(Utils.isAchievement(str)) {
                achi = Achievement.valueOf(str);
            } else if(str.equalsIgnoreCase("me") && (s instanceof Player)) {
                p = (Player)s;
            }
        }
        
        if(type.isEmpty()) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.missingType", "Missing type <give/take>"));
            return true;
        }
        
        if(achi == null) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.missingAchievement", "Missing the Achievement."));
            String achievements = "";
            for(Achievement achieve: Achievement.values())
                achievements += achievements.isEmpty()?"§9" + achieve.name():"§e, §9" + achieve.name();
            s.sendMessage(achievements);
            return true;
        }
        
        if(p == null) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.missingPlayer", "Missing player <online player/me>"));
            return true;
        }
        
        PlayerData gpd = PlayerData.getPlayerData(p);
        String p_achievment = Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.achievment.achievment." + achi.name().toLowerCase(), achi.name().toLowerCase().replace("_", " "));
        String s_achievment = Language.getMessage(Customs.getPlugin(), puuid, "function.achievment.achievment." + achi.name().toLowerCase(), achi.name().toLowerCase().replace("_", " "));
        switch(type) {
            case "give":
                if(achi.hasParent() && p.hasAchievement(achi.getParent())) {
                    Utils.setAchievement(p, achi);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.setachievement.meGiven", "You get the achievement %achievement%.", new String[] {"%achievement%"}, new String[] {p_achievment}));
                    if(!p.getName().equalsIgnoreCase(s.getName()))
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.otherGiven", "You have given the achievement %achievement% to %name%.", new String[] {"%achievement%","%name%"}, new String[] {s_achievment, p.getName()}));
                } else {
                    if(!p.getName().equalsIgnoreCase(s.getName()))
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.otherNotGiven", "%name% not meet the requirements for the achievement %achievement%.", new String[] {"%achievement%","%name%"}, new String[] {s_achievment, p.getName()}));
                    else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.selfNotGiven", "You not meet the requirements for the achievement %achievement%.", new String[] {"%achievement%"}, new String[] {s_achievment}));
                }
                break;
            case "take":
                if(p.hasAchievement(achi) && !hasChildren(p, achi)) {
                    p.removeAchievement(achi);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.setachievement.meTaked","%name% has removed your achievement %achievement%.", new String[] {"%achievement%","%name%"}, new String[] {p_achievment, s.getName()}));
                    if(!p.getName().equalsIgnoreCase(s.getName()))
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.otherTake", "Removed achievement %achievement% from %name%.", new String[] {"%achievement%","%name%"}, new String[] {s_achievment, p.getName()}));
                } else {
                    if(!p.getName().equalsIgnoreCase(s.getName()))
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.otherNotTake", "%name% has not the achievement %achievement%.", new String[] {"%achievement%"}, new String[] {s_achievment}));
                    else
                        s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.setachievement.selfNotTake", "You havn't the achievement %achievement%.", new String[] {"%achievement%"}, new String[] {s_achievment}));
                }
                break;
            default:
                break;
        }
        return true;
    }
    
    private boolean hasChildren(Player p, Achievement a) {
        boolean isChild = false;
        for(Achievement ac: Achievement.values()) {
            if(ac.hasParent() && ac.getParent() == a && p.hasAchievement(ac))
                isChild = true;
        }
        return isChild;
    }
}