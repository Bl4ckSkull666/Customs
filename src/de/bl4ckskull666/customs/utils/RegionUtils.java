/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bl4ckskull666.customs.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Tasks.clearRegionAsync;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public final class RegionUtils {
    public static boolean isRegionExist(World world, String region) {
        try {
            RegionManager rm = Customs.getPlugin().getWG().getRegionManager(world);
            ProtectedRegion pr = rm.getRegion(region);
            return pr != null;
        } catch(Exception ex) {}
        return false;
    }
    
    public static boolean isInOwnRegion(String pn, Location loc) {
        ProtectedRegion pr = null;
        Player p = Bukkit.getPlayer(pn);
        LocalPlayer bp = new BukkitPlayer(Customs.getPlugin().getWG(), p);
        for (Map.Entry<String, ProtectedRegion> e : Customs.getPlugin().getWG().getRegionManager(loc.getWorld()).getRegions().entrySet()) {
            if(e.getValue().contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                if(e.getValue().isOwner(bp)) {
                    if(pr == null || e.getValue().getPriority() > pr.getPriority() || pr.getId().contains(e.getKey())) {
                        pr = e.getValue();
                    }
                }
            }
        }
        
        return pr != null;
    }
    
    public static void clearFlagsAndMore(ProtectedRegion pr) {
        if(pr == null)
            return;
        List<String> ignoreList = Customs.getPlugin().getConfig().getStringList("region-clear-ignore-flags");
        for(Flag<?> df: DefaultFlag.getFlags()) {
            if(ignoreList.contains(df.getName().toLowerCase()))
                continue;
            if(df == DefaultFlag.TELE_LOC)
                continue;
            if(df == DefaultFlag.PISTONS)
                continue;
                
            try {
                if(pr.getFlag(df) != null)
                    pr.setFlag(df, null);
            } catch(Exception ex) {}
        }
            
        if(pr.hasMembersOrOwners()) {
            pr.getMembers().removeAll();
            pr.getOwners().removeAll();
        }
    }
    
    public static void clearRegion(String region, World w) {
        Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new clearRegionAsync(w.getName(), region));
        /*RegionManager rm = Customs.getPlugin().getWG().getRegionManager(w);
        ProtectedRegion pr = rm.getRegion(region);
        
        if(pr != null) {
            int x1 = pr.getMinimumPoint().getBlockX();
            int x2 = pr.getMaximumPoint().getBlockX();
            int y1 = pr.getMinimumPoint().getBlockY();
            int y2 = pr.getMaximumPoint().getBlockY();
            int z1 = pr.getMinimumPoint().getBlockZ();
            int z2 = pr.getMaximumPoint().getBlockZ();
            
            //Remove LWC Protetions
            if(Customs.getPlugin().isLWC()) {
                List<Protection> lis = Customs.getPlugin().getLWC().getLWC().getPhysicalDatabase().loadProtections(w.getName(), x1, x2, y1, y2, z1, z2);
                for(Protection prot: lis)
                    prot.remove();
            }

            //Remove FrameProtect
            if(Customs.getPlugin().isFrameProtect()) {
                List<ch.dragon252525.frameprotect.protection.Protection> lis = Customs.getPlugin().getFrameProtect().getDataManager().loadProtections();
                for(ch.dragon252525.frameprotect.protection.Protection prot: lis) {
                    if(prot.getData().getX() < x1 || prot.getData().getX() > x2)
                        continue;
                    if(prot.getData().getY() < y1 || prot.getData().getY() > y2)
                        continue;
                    if(prot.getData().getZ() < z1 || prot.getData().getZ() > z2)
                        continue;
                    Customs.getPlugin().getFrameProtect().getDataManager().removeProtection(prot.getUuid());
                }
            }
            
            //Remove Entitiy'S
            for(Entity ent: w.getEntities()) {
                if(ent.getType() == EntityType.PLAYER)
                    continue;
                if(ent.getLocation().getBlockX() < x1 || ent.getLocation().getBlockX() > x2)
                    continue;
                if(ent.getLocation().getBlockY() < y1 || ent.getLocation().getBlockY() > y2)
                    continue;
                if(ent.getLocation().getBlockZ() < z1 || ent.getLocation().getBlockZ() > z2)
                    continue;
                ent.remove();
            }
        }
        
        try {
            CuboidCopy copy = CopyManager.getInstance().load(w, "Server", region);
            if(copy != null)
                copy.paste();
        } catch (IOException | CuboidCopyException ex) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Can't clear region " + region + " on world " + w.getName(), ex);
            Utils.InformTeam("Can't clear region " + region + " on world " + w.getName());
        }*/
    }
    
    public static void teleportToRegion(Player p, String region, World w) {
        PlayerData pd = PlayerData.getPlayerData(p);
        RegionManager rm = Customs.getPlugin().getWG().getRegionManager(w);
        ProtectedRegion pr = rm.getRegion(region);
        if(pr == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.noRegionFound", "Can't find region " + region + " on world " + w.getName(), new String[] {"%region%","%world%"}, new String[] {region, w.getName()}));
            return;
        }
        
        Location tpLoc = null;
        try {
            tpLoc = BukkitUtil.toLocation(pr.getFlag(DefaultFlag.TELE_LOC));
        } catch(Exception ex) {}

        if(tpLoc == null) {
            int x = pr.getMinimumPoint().getBlockX();
            x += (pr.getMaximumPoint().getBlockX()-x)/2;
            
            int z = pr.getMinimumPoint().getBlockZ();
            z += (pr.getMaximumPoint().getBlockZ()-z)/2;
            
            for(int y = 1; y < 255; y++) {
                Location loc = new Location(w, x, y, z);
                if(loc.getBlock().getType() != Material.AIR)
                    continue;
                
                loc.setY(loc.getBlockY()+1);
                if(loc.getBlock().getType() != Material.AIR)
                    continue;
                
                loc.setY(loc.getBlockY()+1);
                if(loc.getBlock().getType() != Material.AIR)
                    continue;
                
                tpLoc = loc;
                y = 256;
            }
        }
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.goToRegion", "You are successful teleported to the region " + region + ".", new String[] {"%region%","%world%"}, new String[] {region, w.getName()}));
        Utils.teleportPlayer(p, tpLoc);
    }
    
    public static boolean setOwner(String world, String region, UUID uuid) {
        try {
            RegionManager rm = Customs.getPlugin().getWG().getRegionManager(Bukkit.getWorld(world));
            ProtectedRegion pr = rm.getRegion(region);
            if(pr == null) return false;
            
            LocalPlayer lp = null;
            if(Bukkit.getPlayer(uuid) != null) 
                lp = Customs.getPlugin().getWG().wrapPlayer(Bukkit.getPlayer(uuid));
            else if(Bukkit.getOfflinePlayer(uuid) != null)
                lp = Customs.getPlugin().getWG().wrapOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
            
            if(lp == null) return false;
            pr.getOwners().addPlayer(lp);
            return true;
        } catch(Exception ex) {}
        return false;
    }
    
    public static boolean delOwner(String world, String region, UUID uuid) {
        try {
            RegionManager rm = Customs.getPlugin().getWG().getRegionManager(Bukkit.getWorld(world));
            ProtectedRegion pr = rm.getRegion(region);
            if(pr == null) return false;
            
            LocalPlayer lp = null;
            if(Bukkit.getPlayer(uuid) != null) 
                lp = Customs.getPlugin().getWG().wrapPlayer(Bukkit.getPlayer(uuid));
            else if(Bukkit.getOfflinePlayer(uuid) != null)
                lp = Customs.getPlugin().getWG().wrapOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
            
            if(lp == null)
                return false;
            pr.getOwners().removePlayer(lp);
            return true;
        } catch(Exception ex) {}
        return false;
    }
    
    public static int getPlayerPlotCount(String uuid, String world) {
        int h = 0;
        if(!RegionBuySellSign.getRegions().containsKey(world))
            return h;
        
        for(Map.Entry<String, RegionBuySellSign> e: RegionBuySellSign.getRegions().get(world).entrySet()) {
            if(e.getValue().isOwned()) {
                if(e.getValue().getOwnerUUID().equalsIgnoreCase(uuid))
                    h++;
            }
        }
        return h;
    }
    
    public static ProtectedRegion getRegion(World world, String region) {
        try {
            RegionManager rm = Customs.getPlugin().getWG().getRegionManager(world);
            ProtectedRegion pr = rm.getRegion(region);
            return pr;
        } catch(Exception ex) {}
        return null;
    }
    
    public static ProtectedRegion getOwnRegion(Player p) {
        ProtectedRegion pr = null;
        for(Map.Entry<String, ProtectedRegion> e: Customs.getPlugin().getWG().getRegionManager(p.getWorld()).getRegions().entrySet()) {
            if(!e.getValue().hasMembersOrOwners())
                continue;
            
            if(!isInRegion(e.getValue().getMinimumPoint(), e.getValue().getMaximumPoint(), p.getLocation()))
                continue;
            
            if(!(e.getValue().getOwners().getUniqueIds().contains(p.getUniqueId()) || e.getValue().getOwners().getPlayers().contains(p.getName())))
                continue;
            
            if(pr != null) {
                if(pr.getPriority() > e.getValue().getPriority())
                    continue;
                
                if(pr.getParent() == e.getValue())
                    continue;
            }
            pr = e.getValue();
        }
        return pr;
    }
    
    public static boolean isInRegion(BlockVector min, BlockVector max, Location loc) {
        if(!(loc.getBlockX() >= min.getBlockX() && loc.getBlockX() <= max.getBlockX()))
            return false;
        
        if(!(loc.getBlockY() >= min.getBlockY() && loc.getBlockY() <= max.getBlockY()))
            return false;
        
        return (loc.getBlockZ() >= min.getBlockZ() && loc.getBlockZ() <= max.getBlockZ());
    }
}
