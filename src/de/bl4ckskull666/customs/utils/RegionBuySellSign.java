/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Tasks.updateRegionSigns;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public final class RegionBuySellSign {
    private final String _name;
    private final String _world;
    private String _owner_uuid = "";
    private String _owner_last_name = "";
    private boolean _isOwned = false;
    private long _buyed = 0; 
    private int _price;
    private final ArrayList<Location> _buySigns = new ArrayList<>();
    private final HashMap<Location, Boolean> _tpToRegion = new HashMap<>();
    private String _selledBy = "server";
    
    public RegionBuySellSign(String world, String region, int price) {
        _world = world.toLowerCase();
        _name = region.toLowerCase();
        _price = price;
    }
    
    public String getRegion() {
        return _name;
    }
    
    public String getWorld() {
        return _world;
    }
    
    public int getPrice() {
        return _price;
    }
    
    public void setPrice(int price) {
        _price = price;
    }
    
    public void setOwnerUUID(String uuid) {
        _owner_uuid = uuid;
    }
    
    public String getOwnerUUID() {
        return _owner_uuid;
    }
    
    public void setOwnerLastName(String name) {
        _owner_last_name = name;
    }
    
    public String getOwnerLastName() {
        return _owner_last_name;
    }
    
    public void setOwned(boolean bol) {
        _isOwned = bol;
    }
    
    public boolean isOwned() {
        return _isOwned;
    }
    
    public void setBuyed(long btime) {
        _buyed = btime;
    }
    
    public long getBuyed() {
        return _buyed;
    }
    
    public void setBuyBy(Player p) {
        _owner_uuid = p.getUniqueId().toString();
        _owner_last_name = p.getName();
        _isOwned = true;
        _buyed = System.currentTimeMillis();
    }
    
    public void setSelled() {
        _owner_uuid = "";
        _owner_last_name = "";
        _isOwned = false;
        _buyed = 0;
    }
    
    public void setSeller(String uuid) {
        _selledBy = uuid;
    }
    
    public String getSeller() {
        return _selledBy;
    }
    
    public boolean isSignRegisted(Location loc) {
        for(Location l: _buySigns) {
            if(!l.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                continue;
            
            if(l.getBlockX() != loc.getBlockX() || l.getBlockY() != loc.getBlockY() || l.getBlockZ() != loc.getBlockZ())
                continue;
            return true;
        }
        return false;
    }
    
    public ArrayList<Location> getSigns() {
        return _buySigns;
    }
    
    public void addBuySign(Location loc) {
        _buySigns.add(loc);
    }
    
    public void delBuySign(Location loc) {
        _buySigns.remove(loc);
    }
    
    public void delAllSigns() {
        for(Location loc: _buySigns) {
            if(!loc.getChunk().isLoaded())
                loc.getChunk().load();
            loc.getBlock().breakNaturally();
            if(loc.getChunk().isLoaded())
                loc.getChunk().unload();
        }
        _buySigns.clear();
    }
    
    public int buySignCount() {
        return _buySigns.size();
    }
    
    public void updateSigns() {
        Bukkit.getScheduler().runTask(Customs.getPlugin(), new updateRegionSigns(this));
    }

    public void setTpToRegion(Location loc, boolean tp) {
        _tpToRegion.put(loc, tp);
    }
    
    public boolean getTpToRegion(Location loc) {
        if(_tpToRegion.containsKey(loc))
            return _tpToRegion.get(loc);
        return false;
    }
    
    private static final HashMap<String, HashMap<String, RegionBuySellSign>> _regions = new HashMap<>();
    public static HashMap<String, HashMap<String, RegionBuySellSign>> getRegions() {
        return _regions;
    }
    
    public static RegionBuySellSign getRegionSign(String world, String region) {
        if(!_regions.containsKey(world.toLowerCase()))
            return null;
        
        if(_regions.get(world.toLowerCase()).containsKey(region))
            return _regions.get(world.toLowerCase()).get(region);
        
        return null;
    }
    
    public static RegionBuySellSign getRegionSign(String world, String region, int price) {
        if(!_regions.containsKey(world.toLowerCase()))
            _regions.put(world.toLowerCase(), new HashMap<String, RegionBuySellSign>());
        if(_regions.get(world.toLowerCase()).containsKey(region))
            return _regions.get(world.toLowerCase()).get(region);

        RegionBuySellSign rbss = new RegionBuySellSign(world.toLowerCase(), region, price);
        _regions.get(world.toLowerCase()).put(region, rbss);
        return rbss;
    }
    
    public static RegionBuySellSign getRegionSign(Location loc) {
        for(World w: Bukkit.getWorlds()) {
            if(!_regions.containsKey(w.getName().toLowerCase()))
                continue;
            
            for(Map.Entry<String, RegionBuySellSign> e: _regions.get(w.getName().toLowerCase()).entrySet()) {
                if(e.getValue().isSignRegisted(loc))
                    return e.getValue();
            }
        }
        return null;
    }
    
    public static void removeRegion(String world, String region) {
        if(_regions.containsKey(world.toLowerCase()))
            _regions.get(world.toLowerCase()).remove(region);
    }
}
