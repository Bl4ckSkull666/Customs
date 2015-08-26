/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands.region;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//k_s_x
//k_n_x
//k_x_wn_xx
//k_x_ws_xx
//k_x_on_xx
//k_x_os_xx

//m_w_xx = Mittleres West Weg Nr. xx
//m_o_xx = 
//m_x_sw_xx = Mittleres 3. SuedWest Weg Nr. xx
//m_x_so_xx
//m_x_nw_xx
//m_x_no_xx

//g_w_x
//g_o_x
//g_x_nw_xx
//g_x_no_xx
//g_x_sw_xx
//g_x_so_xx

//l_w_xx
//l_x_sw_xx
//l_x_so_xx
//l_x_nw_xx
//l_x_no_xx


/**
 *
 * @author PapaHarni
 */
public class MyHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can be only used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use.myhome")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.noPerm","YOu don't have permission to use this command."));
            return true;
        }
        
        WorldGuardPlugin wg = Customs.getPlugin().getWG();
        LocalPlayer lp = wg.wrapPlayer(p);
        for(World w: Bukkit.getWorlds()) {
            if(wg.getRegionManager(w).getRegionCountOfPlayer(lp) < 1)
                continue;
        
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.ownregionworld","You have %count% plots on %world% :", new String[] {"%count%","%world%"}, new String[] {String.valueOf(wg.getRegionManager(w).getRegionCountOfPlayer(lp)), w.getName()}));
            for(Map.Entry<String, ProtectedRegion> me: wg.getRegionManager(w).getRegions().entrySet()) {
                if(me.getValue().isOwner(lp))
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.ownerregion","§a%longname% ( %shortname% )", new String[] {"%longname%", "%shortname%"}, new String[] {formatRegionName(pd.getLanguage(), me.getValue().getId()), me.getValue().getId()}));
                if(me.getValue().isMember(lp) && !me.getValue().isOwner(lp))
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.region.memberregion","§e%longname% ( %shortname% )", new String[] {"%longname%", "%shortname%"}, new String[] {formatRegionName(pd.getLanguage(), me.getValue().getId()), me.getValue().getId()}));
            }
        }
        return true;
    }
    
    private String formatRegionName(String lang, String shortplotname) {
        if(shortplotname.indexOf("-") < 2)
            return shortplotname;
        
        String temp = "";
        String[] str = shortplotname.split("_");
        /*if(str.length == 3) {
            String plot = Language.getMessage(lang, "region.names." + str[0].toLowerCase(), str[0]) + " ";
            temp += plot + " ";
            for(char ch: str[1].toCharArray())
                temp += Language.getMessage(lang, "region.names." + String.valueOf(ch).toLowerCase(), String.valueOf(ch).toLowerCase());
            temp += " " + Language.getMessage(lang, "region.names." + plot.toLowerCase(), plot) + " ";
            temp += Language.getMessage(lang, "region.names.num", "") + " " + str[2];
        } else if(str.length == 4) {
            String plot = Language.getMessage(lang, "region.names." + str[0].toLowerCase(), str[0]) + " ";
            temp += plot + " " + str[1] + ". ";
            for(char ch: str[2].toCharArray())
                temp += Language.getMessage(lang, "region.names." + String.valueOf(ch).toLowerCase(), String.valueOf(ch).toLowerCase());
            temp += " " + Language.getMessage(lang, "region.names." + plot.toLowerCase(), plot) + " ";
            temp += Language.getMessage(lang, "region.names.num", "") + " " + str[3];
        } else
            temp = shortplotname;
        */
        return temp;
    }
}
