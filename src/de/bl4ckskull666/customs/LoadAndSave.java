/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.utils.BookData;
import de.bl4ckskull666.customs.utils.Items;
import de.bl4ckskull666.customs.utils.KitTool;
import de.bl4ckskull666.customs.utils.RFly;
import de.bl4ckskull666.customs.utils.RegionBuySellSign;
import de.bl4ckskull666.customs.utils.RegionUtils;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.uuiddatabase.UUIDDatabase;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author PapaHarni
 */
public final class LoadAndSave {
    public static void loadRentFlyTimes(Customs p) {
        try {
            if(!p.getDataFolder().exists())
                p.getDataFolder().mkdir();
            
            File f = new File(p.getDataFolder(), "rentfly.yml");
            if(!f.exists())
                f.createNewFile();
            
            FileConfiguration t = YamlConfiguration.loadConfiguration(f);
            if(!t.isConfigurationSection("times"))
                return;
            
            for(String k: t.getConfigurationSection("times").getKeys(false)) {
                RFly.setRFly(k, t.getLong("times." + k));
            }
        } catch (IOException ex) {
            p.getLogger().log(Level.INFO, "Can't load Player rent fly times.", ex);
        }
    }
    
    public static void saveRentFlyTimes(Customs p) {
        try {
            if(!p.getDataFolder().exists())
                p.getDataFolder().mkdir();
            
            File f = new File(p.getDataFolder(), "rentfly.yml");
            if(!f.exists())
                f.createNewFile();
            
            FileConfiguration t = YamlConfiguration.loadConfiguration(f);
            t.set("times", null);
            RFly.saveAll();
            for(Map.Entry<String, Long> e: RFly.getOfflineRentTimes().entrySet())
                t.set("times." + e.getKey(), e.getValue());
            
            t.save(f);
        } catch (IOException ex) {
            p.getLogger().log(Level.INFO, "Can't save Player Rent Fly Times.", ex);
        }
    }
    
    public static void loadWarps(Customs p) {
        if(!p.getDataFolder().exists())
            p.getDataFolder().mkdir();
        
        File lFold = new File(p.getDataFolder(), "warps");
        if(!lFold.exists())
            lFold.mkdir();
        
        Customs.getWarps().clear();
        for(File l: lFold.listFiles()) {
            String name = l.getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0) {
                name = name.substring(0, pos);
            }
            FileConfiguration warp = YamlConfiguration.loadConfiguration(l);
            if(!warp.isString("world") || !warp.isDouble("x") || !warp.isDouble("y") || !warp.isDouble("z") || !warp.isDouble("yaw") || !warp.isDouble("pitch"))
                continue;
            if(Bukkit.getWorld(warp.getString("world")) == null) {
                p.getLogger().log(Level.INFO, "Can''t find Warp Point World {0}", warp.getString("world"));
                continue;
            }
            
            Location loc = new Location(Bukkit.getWorld(warp.getString("world")), warp.getDouble("x"), warp.getDouble("y"), warp.getDouble("z"), (float)warp.getDouble("yaw"), (float)warp.getDouble("pitch"));
            Customs.setWarp(name, loc);
            p.getLogger().log(Level.INFO, "Warp {0} is successful loaded.", name);
        }
    }
    
    public static void saveWarps(Customs p) {
        if(!p.getDataFolder().exists())
            p.getDataFolder().mkdir();
        
        File lFold = new File(p.getDataFolder(), "warps");
        if(!lFold.exists())
            lFold.mkdir();
        
        for(File l: lFold.listFiles())
            l.delete();
        
        for(Map.Entry<String, Location> e : Customs.getWarps().entrySet()) {
            File f = new File(lFold, e.getKey() + ".yml");
            FileConfiguration warp = YamlConfiguration.loadConfiguration(f);
            warp.set("world", e.getValue().getWorld().getName());
            warp.set("x", e.getValue().getX());
            warp.set("y", e.getValue().getY());
            warp.set("z", e.getValue().getZ());
            warp.set("yaw", e.getValue().getYaw());
            warp.set("pitch", e.getValue().getPitch());
            warp.set("name", e.getKey());
            try {
                warp.save(f);
            } catch (IOException ex) {
                p.getLogger().log(Level.WARNING, "Can't save Warp Point " + e.getKey(), ex);
            }
        }
    }
    
    public static void loadKits(Customs p) {
        if(!p.getDataFolder().exists())
            p.getDataFolder().mkdir();
        
        File lFold = new File(p.getDataFolder(), "kits");
        if(!lFold.exists())
            lFold.mkdir();
        
        Customs.getKits().clear();
        for(File l: lFold.listFiles()) {
            String name = l.getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0) {
                name = name.substring(0, pos);
            }
            FileConfiguration kit = YamlConfiguration.loadConfiguration(l);
            if(kit.isList("items")) {
                List<String> temp = new ArrayList<>();
                for(String strItem: kit.getStringList("items")) {
                    if(!Items.isItem(strItem)) {
                        p.getLogger().log(Level.INFO, "Item \"{0}\" in kit {1} is not a available item.", new Object[]{strItem, name});
                        continue;
                    }
                    temp.add(strItem);
                }
                KitTool kt = new KitTool(name, kit.getLong("delay", 0), kit.getInt("cost", 0), temp);
                Customs.setKit(name, kt);
                p.getLogger().log(Level.INFO, "Kit {0} is successful loaded.", name);
            } else {
                p.getLogger().log(Level.INFO, "Can''t load Kit {0}, missing Items.", name);
            }
        }
    }
    
    public static void saveKits(Customs p) {
        if(!p.getDataFolder().exists())
            p.getDataFolder().mkdir();
        
        File lFold = new File(p.getDataFolder(), "kits");
        if(!lFold.exists())
            lFold.mkdir();
        
        for(File l: lFold.listFiles())
            l.delete();
        
        for(Map.Entry<String, KitTool> e : Customs.getKits().entrySet()) {
            File f = new File(lFold, e.getKey() + ".yml");
            try {
                if(!f.exists())
                    f.createNewFile();
            } catch (IOException ex) {
                p.getLogger().log(Level.WARNING, "Can't create file Kit " + e.getKey(), ex);
            }
            FileConfiguration kits = YamlConfiguration.loadConfiguration(f);
            kits.set("name", e.getValue().getName());
            kits.set("delay", e.getValue().getDelay());
            kits.set("cost", e.getValue().getCost());
            kits.set("items", e.getValue().getItemsAsString());
            try {
                kits.save(f);
            } catch (IOException ex) {
                p.getLogger().log(Level.WARNING, "Can't save Kit " + e.getKey(), ex);
            }
        }
    }
    
    public static void loadBuySellSigns(Customs p) {
        if(!p.getDataFolder().exists())
            p.getDataFolder().mkdir();
        
        File lFold = new File(p.getDataFolder(), "regions/buyAndSell");
        if(!lFold.exists())
            return;
        
        for(File w: lFold.listFiles()) {
            int count = 0;
            if(!w.isDirectory())
                continue;
            
            String world = w.getName();
            
            for(File f: w.listFiles()) {
                FileConfiguration r = YamlConfiguration.loadConfiguration(f);
                try {
                    if(!r.isString("name") || !r.isString("world") || !r.isInt("price") || Bukkit.getWorld(r.getString("world", "")) == null)
                        continue;

                    RegionBuySellSign rbss = RegionBuySellSign.getRegionSign(r.getString("world"), r.getString("name"), r.getInt("price"));
                    ProtectedRegion pr = RegionUtils.getRegion(Bukkit.getWorld(rbss.getWorld()), rbss.getRegion());
                    if(r.getBoolean("isOwner", false)) {
                        rbss.setOwned(true);
                        rbss.setOwnerUUID(r.getString("owner_uuid", "00000000-0000-0000-0000-000000000000"));
                        rbss.setOwnerLastName(r.getString("owner_last_name", "unknown"));
                        rbss.setBuyed(r.getLong("buyTime", 0));
                        Customs.setRegion(rbss.getWorld(), rbss.getRegion(), true);
                        if(rbss.getOwnerUUID().length() <= 16)
                            rbss.setOwnerUUID(UUIDDatabase.getUUIDByName(rbss.getOwnerUUID()));
                    }
                    
                    if(pr != null) {
                        if(pr.getOwners().size() == 0) {
                            rbss.setOwned(false);
                            rbss.setOwnerUUID("");
                            rbss.setOwnerLastName("");
                            rbss.setBuyed(0L);
                        } else if(!rbss.isOwned() && pr.getOwners().size() > 0) {
                            rbss.setOwnerUUID(pr.getOwners().getUniqueIds().toArray()[0].toString());
                            rbss.setOwnerLastName(UUIDDatabase.getNameByUUID(pr.getOwners().getUniqueIds().toArray()[0].toString()));
                        } else {
                            if(!pr.getOwners().getUniqueIds().contains(UUID.fromString(rbss.getOwnerUUID()))) {
                                rbss.setOwnerUUID(pr.getOwners().getUniqueIds().toArray()[0].toString());
                                rbss.setOwnerLastName(UUIDDatabase.getNameByUUID(rbss.getOwnerUUID()));
                            }
                        }
                    }

                    if(r.isConfigurationSection("signs")) {
                        for(String id: r.getConfigurationSection("signs").getKeys(false)) {
                            if(Bukkit.getWorld(r.getString("signs." + id + ".world")) == null)
                                continue;

                            Location loc = new Location(
                                    Bukkit.getWorld(r.getString("signs." + id + ".world")),
                                    r.getInt("signs." + id + ".x"),
                                    r.getInt("signs." + id + ".y"),
                                    r.getInt("signs." + id + ".z")
                            );

                            if(!rbss.isSignRegisted(loc)) {
                                rbss.addBuySign(loc);
                                rbss.setTpToRegion(loc, r.getBoolean("signs." + id + ".useTP", true));
                            }
                        }
                        rbss.updateSigns();
                    }
                    count++;
                } catch(Exception ex) {
                    p.getLogger().log(Level.INFO, "Error on load " + r.getString("name"), ex);
                }
            }
            p.getLogger().log(Level.INFO, "{0} region signs are loaded on {1}", new Object[]{count, world});
        }
    }
    
    public static void saveBuySellSigns(Customs p) {
        if(!p.getDataFolder().exists())
            p.getDataFolder().mkdir();
        
        File lFold = new File(p.getDataFolder(), "regions/buyAndSell");
        if(!lFold.exists())
            lFold.mkdirs();
        
        for(File l: lFold.listFiles()) {
            try {
                FileUtils.deleteDirectory(l);
            } catch (IOException ex) {
                p.getLogger().log(Level.WARNING, "Can't delete folder " + l.getName(), ex);
            }
        }
        
        for(Map.Entry<String, HashMap<String, RegionBuySellSign>> e1: RegionBuySellSign.getRegions().entrySet()) {
            File wFold = new File(lFold, e1.getKey());
            if(!wFold.exists())
                wFold.mkdir();
            
            for(Map.Entry<String, RegionBuySellSign> e2 : e1.getValue().entrySet()) {
                RegionBuySellSign rbss = e2.getValue();
                File f = new File(wFold, e2.getKey() + ".yml");
                FileConfiguration r = YamlConfiguration.loadConfiguration(f);
                r.set("name", rbss.getRegion());
                r.set("world", rbss.getWorld());
                r.set("price", rbss.getPrice());
                r.set("isOwner", rbss.isOwned());
                r.set("owner_uuid", rbss.getOwnerUUID());
                r.set("owner_last_name", rbss.getOwnerLastName());
                r.set("buyTime", rbss.getBuyed());
                int i = 1;
                for(Location loc: rbss.getSigns()) {
                    r.set("signs." + i + ".useTP", rbss.getTpToRegion(loc));
                    r.set("signs." + i + ".world", loc.getWorld().getName());
                    r.set("signs." + i + ".x", loc.getBlockX());
                    r.set("signs." + i + ".y", loc.getBlockY());
                    r.set("signs." + i + ".z", loc.getBlockZ());
                    i++;
                }
                
                try {
                    r.save(f);
                } catch (IOException ex) {
                    p.getLogger().log(Level.WARNING, "Can't save RegoinBuySellSign " + e2.getKey(), ex);
                }
            }
        }
    }
    
    public static void loadBooks(Customs p) {
        if(!p.getDataFolder().exists())
            p.getDataFolder().mkdir();
        
        File lFold = new File(p.getDataFolder(), "books");
        if(!lFold.exists())
            lFold.mkdir();
        
        Customs.getBooks().clear();
        for(File l: lFold.listFiles()) {
            String name = l.getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0) {
                name = name.substring(0, pos);
            }
            FileConfiguration book = YamlConfiguration.loadConfiguration(l);
            BookData bd = new BookData(book.getString("author", "No Autor found"), book.getString("title", name));
            if(book.isConfigurationSection("pages")) {
                for(String page: book.getConfigurationSection("pages").getKeys(false)) {
                    if(book.isList("pages." + page) && Rnd.isNumeric(page)) {
                        String myPage = "";
                        for(String line: book.getStringList("pages." + page))
                            myPage += line + "\n";
                        bd.addPage(Integer.parseInt(page), ChatColor.translateAlternateColorCodes('&', myPage));
                    } else if(book.isString("pages." + page) && Rnd.isNumeric(page)) {
                        bd.addPage(Integer.parseInt(page), ChatColor.translateAlternateColorCodes('&', book.getString("pages." + page)));
                    } else {
                        p.getLogger().log(Level.INFO, "Wrong Type on Book {0} Page Line {1}", new Object[]{name, page});
                    }
                }
            } else if(book.isList("pages")) {
                int pageNumber = 1;
                for(String page: book.getStringList("pages")) {
                    bd.addPage(pageNumber, ChatColor.translateAlternateColorCodes('&', page));
                    pageNumber++;
                }
            } else {
                p.getLogger().log(Level.INFO, "Wrong Type in Book {0} by Pages", name);
                continue;
            }
            
            if(bd.getPageCount() > 0) {
                Customs.setBook(name, bd);
                p.getLogger().log(Level.INFO, "Book {0} is successful loaded.", name);
            }
        }
    }
    
    public static void loadRegions(Customs p) {
        for(World w: Bukkit.getWorlds()) {
            for(Map.Entry<String, ProtectedRegion> e: p.getWG().getRegionManager(w).getRegions().entrySet()) {
                Customs.setRegion(w.getName(),e.getKey(), (e.getValue().getOwners().size() > 0));
            }
        }
    }

    public static void loadBlockedWorldsByCommand(Customs p) {
        if(p.getConfig().isConfigurationSection("blockCmdOnWorld")) {
            for(String cmd : p.getConfig().getConfigurationSection("blockCmdOnWorld").getKeys(false)) {
                if(p.getConfig().isList("blockCmdOnWorld." + cmd)) {
                    for(String w: p.getConfig().getStringList("blockCmdOnWorld." + cmd)) {
                        Customs.setBlockedWorldByCommand(cmd, w);
                    }
                } else if(p.getConfig().isString("blockCmdOnWorld." + cmd))
                    Customs.setBlockedWorldByCommand(cmd, p.getConfig().getString("blockCmdOnWorld." + cmd));
            } 
        }
    }
    
    public static FileConfiguration loadHelp() {
        if(!Customs.getPlugin().getDataFolder().exists())
            Customs.getPlugin().getDataFolder().mkdir();
            
        File f = new File(Customs.getPlugin().getDataFolder(), "help.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Customs.getPlugin().getLogger().log(Level.INFO, "Can't load Help file");
                return null;
            }
        }
        return YamlConfiguration.loadConfiguration(f);
        
    }
}
