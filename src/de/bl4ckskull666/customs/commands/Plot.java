/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.customs.utils.Utils;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Plot implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can be only run by a Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(Customs.isBlockedWorldbyCommand("plot", p, p.getWorld().getName()))
            return true;
        
        String rBegins = "";
        if(a.length == 0) {
            if(Customs.getPlugin().getConfig().isString("default-plot." + p.getWorld().getName().toLowerCase())) {
                rBegins = Customs.getPlugin().getConfig().getString("default-plot." + p.getWorld().getName().toLowerCase());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.plot.use-world-default", "Use World Default Plot begin."));
            }
        } else
            rBegins = a[0];
        
        if(rBegins.isEmpty() && !p.hasPermission("customs.use.plot.empty")) {
            //No rights to use it yet
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.plot.permission.empty", "You don't have permission to use this command without plot name begin."));
            return true;
        } else if(!rBegins.isEmpty() && !p.hasPermission("customs.use.plot")) {
            //No rights to use this command.
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.plot.permission.use", "You don't have permission to use this command."));
            return true;
        }
        
        ArrayList<String> aRegions = Customs.getRegionsStartsWith(p.getWorld().getName().toLowerCase(), rBegins);
        if(aRegions.size() < 1) {
            //No Free Region found
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.plot.noFreeRegion", "Can't find any free Regions on this World."));
            return true;
        }
        
        ProtectedRegion pr = getPR(p, aRegions);
        Location tpLoc = BukkitUtil.toLocation(pr.getFlag(DefaultFlag.TELE_LOC));
        if(tpLoc == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.plot.noRegionWithTeleport", "We are sorry, but we can't find any free Region with teleport point yet. Please try again."));
            return true;
        }
        
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.plot.successful", "We have teleport you to a free region, so we hope it's free ;-)"));
        Utils.teleportPlayer(p, tpLoc);
        return true;
    }
    
    private ProtectedRegion getPR(Player p, ArrayList<String> regions) {
        String reg = regions.get(Rnd.get(0, (regions.size()-1)));
        ProtectedRegion pr = Customs.getPlugin().getWG().getRegionManager(p.getWorld()).getRegion(reg);
        regions.remove(pr.getId());
        if(pr.getOwners().size() > 0 || pr.getMembers().size() > 0)
            return getPR(p, regions);
        
        Object o = pr.getFlag(DefaultFlag.TELE_LOC);
        if(o == null)
            return getPR(p, regions);
        
        Location loc = BukkitUtil.toLocation(pr.getFlag(DefaultFlag.TELE_LOC));
        if(loc == null)
            return getPR(p, regions);
        return pr;
    }
    
/*    private Location getTpLocation(Player p, String w, ArrayList<String> regions, int runs) {
        if(runs > 10)
            return null;
        
        runs = runs+1;
        
        if(regions.size() < 1)
            return null;

        ProtectedRegion pr = Customs.getPlugin().getWG().getRegionManager(p.getWorld()).getRegion(regions.get(Rnd.get(0, (regions.size()-1))));
        regions.remove(pr.getId());
        
        if(pr.getOwners().size() > 0 || pr.getMembers().size() > 0)
            return getTpLocation(p, w, regions, runs);
        
        Object o = pr.getFlag(DefaultFlag.TELE_LOC);
        if(o == null)
            return getTpLocation(p, w, regions, runs);
        
        String[] temp1 = o.toString().replace(" ", "").split(":");
        if(temp1.length != 5)
            return getTpLocation(p, w, regions, runs);
        
        if(!temp1[0].equalsIgnoreCase("World"))
            return getTpLocation(p, w, regions, runs);
        
        if(!temp1[1].endsWith("Coordinates"))
            return getTpLocation(p, w, regions, runs);
        
        String world = (temp1[1].split(","))[0];
        String[] temp2 = temp1[2].replace("(", "").replace(")", "").split(",");
        
        if(temp2.length != 4)
            return getTpLocation(p, w, regions, runs);
        
        if(!Rnd.isDouble(temp2[0]) || !Rnd.isDouble(temp2[1]) || !Rnd.isDouble(temp2[2]) || !temp2[3].equalsIgnoreCase("yaw"))
            return getTpLocation(p, w, regions, runs);
        
        double posX = Double.parseDouble(temp2[0]);
        double posY = Double.parseDouble(temp2[1]);
        double posZ = Double.parseDouble(temp2[2]);
        
        String temp3[] = temp1[3].split(",");
        if(temp3.length != 2)
            return getTpLocation(p, w, regions, runs);
        
        if(!Rnd.isFloat(temp3[0]) || !temp3[1].equalsIgnoreCase("Pitch") || !Rnd.isFloat(temp1[4]))
            return getTpLocation(p, w, regions, runs);
        
        Float yaw = Float.parseFloat(temp3[0]);
        Float pitch = Float.parseFloat(temp1[4]);
        
        Location l = new Location(Bukkit.getWorld(world), posX, posY, posZ, yaw, pitch);
        return l;
    }*/
}
