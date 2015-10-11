/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import de.bl4ckskull666.customs.Customs;
import static de.bl4ckskull666.customs.utils.Rnd.isNumeric;
import de.bl4ckskull666.customs.utils.Tasks.TeleportPlayer;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public final class Utils {
    public static String upperFirst(String str) {
        return upperFirst(str, false);
    }
    
    public static String upperFirst(String str, boolean allFirst) {
        if(str.isEmpty())
            return "";
        
        if(!allFirst)
            return str.substring(0, 1).toUpperCase() + (str.length() > 1?str.substring(1):"");
        
        String msg = "";
        for(String temp: str.split(" "))
            msg += msg.isEmpty()?"":" " + temp.substring(0, 1).toUpperCase() + (temp.length() > 1?temp.substring(1):"");
        return msg;
    }
    
    public static Location getCursorLocation(Player p) {
        Set<Material> sm = new HashSet<>();
        sm.addAll(Arrays.asList(Material.values()));
        for(Block b: p.getLineOfSight(sm, 32)) {
            if(b != null && !b.getType().equals(Material.AIR)) {
                if(b.getLocation().getChunk().getBlock(b.getX(), b.getY()+1, b.getZ()).getType().equals(Material.AIR)) {
                    return b.getLocation().getChunk().getBlock(b.getX(), b.getY()+1, b.getZ()).getLocation();
                }
            }
        }
        return p.getLocation();
    }
    
    public static boolean isBoolean(String str) {
        try {
            boolean bol = Boolean.parseBoolean(str);
            return true;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }
    
    public static boolean isEntityType(String str) {
        try {
            EntityType ent = EntityType.valueOf(str.toUpperCase());
            return ent != null;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }
    
    public static boolean isAchievement(String str) {
        try {
            Achievement achi = Achievement.valueOf(str);
            return achi != null;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }
    
    public static void teleportPlayer(Player p, Location loc) {
        long tpDelay = p.isOp()?0L:getMaxPermission(p, "customs.teleport.delay");
        tpDelay = Math.max(tpDelay, 2);
        
        if(!loc.getChunk().isLoaded())
            loc.getChunk().load();
        
        Entity vehicle = null;
        ArrayList<Entity> leashed = new ArrayList<>();
        if(p.getItemInHand() != null) {
            if(p.getItemInHand().getType() == Material.LEASH) {
                for(Entity e: p.getWorld().getEntities()) {
                    Entity en = checkAndTpEntity(p, loc, e);
                    if(en != null)
                        leashed.add(en);
                }
            }
        }
        
        boolean isInVehicle = false;
        if(p.isInsideVehicle()) {
            Entity e = p.getVehicle();
            p.leaveVehicle();
            e.teleport(loc);
            vehicle = e;
            isInVehicle = true;
        }
        
        if(isInVehicle || !leashed.isEmpty())
            Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new TeleportPlayer(p, vehicle, leashed, loc), (tpDelay*20));
        else
            Bukkit.getScheduler().runTask(Customs.getPlugin(), new TeleportPlayer(p, null, null, loc));
    }
    
    public static void setLeashed(Player p, Entity e) {
        if(e instanceof Animals) {
            Animals en = (Animals)e;
            en.setLeashHolder(p);
        }
        
        if(e instanceof Monster) {
            Monster en = (Monster)e;
            en.setLeashHolder(p);
        }
    }
    
    public static Entity checkAndTpEntity(Player p, Location loc, Entity e) {
        if(e instanceof Animals) {
            Animals en = (Animals)e;
            if(en.isLeashed() && en.getLeashHolder() instanceof Player) {
                Player holder = (Player)en.getLeashHolder();
                if(holder.getName().equalsIgnoreCase(p.getName())) {
                    en.teleport(loc);
                    return e;
                }
            }
        }
        
        if(e instanceof Monster) {
            Monster en = (Monster)e;
            if(en.isLeashed() && en.getLeashHolder() instanceof Player) {
                Player holder = (Player)en.getLeashHolder();
                if(holder.getName().equalsIgnoreCase(p.getName())) {
                    en.teleport(loc);
                    return e;
                }
            }
        }
        return null;
    }
    
    public static boolean isMonster(EntityType et) {
        if(et == EntityType.BLAZE)
            return true;
        if(et == EntityType.CAVE_SPIDER)
            return true;
        if(et == EntityType.CREEPER)
            return true;
        if(et == EntityType.ENDERMAN)
            return true;
        if(et == EntityType.ENDERMITE)
            return true;
        if(et == EntityType.ENDER_DRAGON)
            return true;
        if(et == EntityType.GHAST)
            return true;
        if(et == EntityType.GIANT)
            return true;
        if(et == EntityType.GUARDIAN)
            return true;
        if(et == EntityType.LEASH_HITCH)
            return true;
        if(et == EntityType.MAGMA_CUBE)
            return true;
        if(et == EntityType.PIG_ZOMBIE)
            return true;
        if(et == EntityType.SILVERFISH)
            return true;
        if(et == EntityType.SKELETON)
            return true;
        if(et == EntityType.SLIME)
            return true;
        if(et == EntityType.SQUID)
            return true;
        if(et == EntityType.WITCH)
            return true;
        if(et == EntityType.WITHER)
            return true;
        return (et == EntityType.ZOMBIE);
    }
    
    public static boolean isAnimal(EntityType et) {
        if(et == EntityType.BAT)
            return true;
        if(et == EntityType.PIG)
            return true;
        if(et == EntityType.SHEEP)
            return true;
        if(et == EntityType.COW)
            return true;
        if(et == EntityType.CHICKEN)
            return true;
        if(et == EntityType.SQUID)
            return true;
        if(et == EntityType.MUSHROOM_COW)
            return true;
        if(et == EntityType.RABBIT)
            return true;
        if(et == EntityType.WOLF)
            return true;
        if(et == EntityType.HORSE)
            return true;
        return (et == EntityType.OCELOT);
    }
    
    public static boolean isOtherCreature(EntityType et) {
        if(et == EntityType.VILLAGER)
            return true;
        if(et == EntityType.IRON_GOLEM)
            return true;
        return (et == EntityType.SNOWMAN);
    }
    
    public static boolean isVehicle(EntityType et) {
        if(et == EntityType.BOAT)
            return true;
        if(et == EntityType.MINECART)
            return true;
        if(et == EntityType.MINECART_CHEST)
            return true;
        if(et == EntityType.MINECART_COMMAND)
            return true;
        if(et == EntityType.MINECART_FURNACE)
            return true;
        if(et == EntityType.MINECART_HOPPER)
            return true;
        if(et == EntityType.MINECART_MOB_SPAWNER)
            return true;
        return (et == EntityType.MINECART_TNT);
    }
    
    public static boolean isItemEntity(EntityType et) {
        if(et == EntityType.ARMOR_STAND)
            return true;
        if(et == EntityType.ARROW)
            return true;
        if(et == EntityType.COMPLEX_PART)
            return true;
        if(et == EntityType.DROPPED_ITEM)
            return true;
        if(et == EntityType.EGG)
            return true;
        if(et == EntityType.ENDER_CRYSTAL)
            return true;
        if(et == EntityType.ENDER_PEARL)
            return true;
        if(et == EntityType.ENDER_SIGNAL)
            return true;
        if(et == EntityType.EXPERIENCE_ORB)
            return true;
        if(et == EntityType.FALLING_BLOCK)
            return true;
        if(et == EntityType.FIREWORK)
            return true;
        if(et == EntityType.FIREBALL)
            return true;
        if(et == EntityType.ITEM_FRAME)
            return true;
        if(et == EntityType.LIGHTNING)
            return true;
        if(et == EntityType.PAINTING)
            return true;
        if(et == EntityType.PRIMED_TNT)
            return true;
        if(et == EntityType.SMALL_FIREBALL)
            return true;
        if(et == EntityType.SNOWBALL)
            return true;
        if(et == EntityType.SPLASH_POTION)
            return true;
        if(et == EntityType.THROWN_EXP_BOTTLE)
            return true;
        return (et == EntityType.WITHER_SKULL);
    }
    
    public static void InformTeam(String message) {
        for(Player p: Bukkit.getOnlinePlayers()) {
            if(p.hasPermission("customs.team"))
                p.sendMessage("§f[§4Team-Info§f]§e" + message);
        }
    }
    
    public static int getMaxPermission(Player p, String perm) {
        int a = 0;
        for(int i = 0;i <= 100; i++) {
            if(p.hasPermission(perm + "." + i))
                a = i;
        }
        return a;
    }
    
    public static void remAchievement(Player p, Achievement a) {
        PlayerData pd = PlayerData.getPlayerData(p);
        if(!pd.isAchievment(a.name().toLowerCase()))
            return;
        
        pd.delAchievment(a.name().toLowerCase());
    }
    
    public static void setAchievement(Player p, Achievement a) {
        PlayerData pd = PlayerData.getPlayerData(p);
        if(pd.isAchievment(a.name().toLowerCase()))
            return;
        
        p.awardAchievement(a);
        pd.setAchievment(a.name().toLowerCase());
        String str_achivment = Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.achievment.achievment." + a.name().toLowerCase(), a.name().toLowerCase().replace("_", " "));
        if(Customs.getPlugin().getConfig().isInt("achievment.economy." + a.name().toLowerCase())) {
            int pay = Customs.getPlugin().getConfig().getInt("achievment.economy." + a.name().toLowerCase());
            if(pay > 0) {
                Customs.givePlayerMoney(p, pay);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.achievment.economy", "You have become " + String.valueOf(pay) + " for " + str_achivment, new String[] {"%amount%", "%currency%", "%achievment%"}, new String[] {String.valueOf(pay), Customs.currencyNamePlural(), str_achivment}));
            }
        }

        if(Customs.getPlugin().getConfig().isList("achievment.items." + a.name().toLowerCase())) {
            for(String strItem: Customs.getPlugin().getConfig().getStringList("achievment.items." + a.name().toLowerCase())) {
                ItemStack item = Items.getItem(strItem);
                if(item != null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.achievment.item", "You have become " + item.getAmount() + " x " + item.getType().name() + " for " + str_achivment, new String[] {"%item%", "%amount%", "%achievment%"}, new String[] {item.getType().name(), String.valueOf(item.getAmount()), str_achivment}));
                    if(p.getInventory().firstEmpty() == -1) {
                        pd.setHoldingItems(strItem);
                        continue;
                    }
                    p.getInventory().addItem(item);
                }
            }
        }
        
        for(Player ap: Bukkit.getOnlinePlayers()) {
            String ap_achievment = Language.getMessage(Customs.getPlugin(), ap.getUniqueId(), "function.achievment.achievment." + a.name().toLowerCase(), a.name().toLowerCase().replace("_", " "));
            ap.sendMessage(Language.getMessage(Customs.getPlugin(), ap.getUniqueId(), "function.achievment.announce", p.getName() + " has reached the achievment " + ap_achievment, new String[] {"%name%", "%achievment%"}, new String[] {p.getName(), ap_achievment}));
        }
        
        if(Customs.getPlugin().getConfig().isString("achievement.automatic." + a.name().toLowerCase()))
        
        if(a == Achievement.GET_BLAZE_ROD) {
            setAchievement(p, Achievement.END_PORTAL);
        }
    }
    
    private static String shortString(String str, String[] remove) {
        for(String search: remove)
            str = str.replace(search, "");
        return str.replace(" ", "");
    }
    
    public static void addTimeToCalendar(String[] stra, Calendar cal) {
        for(String str: stra) {
            str = str.toLowerCase();
            if(str.endsWith("years") || str.endsWith("year")) {
                String number = shortString(str, new String[] {"years", "year", "y"});
                if(isNumeric(number))
                    cal.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(number));
            } else if(str.endsWith("month") || str.endsWith("moths") || str.equalsIgnoreCase("mon")) {
                String number = shortString(str, new String[] {"months", "month", "mon"});
                if(isNumeric(number))
                    cal.add(Calendar.MONTH, Integer.parseInt(number));
            } else if(str.endsWith("week") || str.endsWith("weeks")) {
                String number = shortString(str, new String[] {"weeks", "week", "w"});
                if(isNumeric(number))
                    cal.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(number));
            } else if(str.endsWith("day") || str.endsWith("days")) {
                String number = shortString(str, new String[] {"days", "day", "d"});
                if(isNumeric(number))
                    cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(number));
            } else if(str.endsWith("hour") || str.endsWith("hours")) {
                String number = shortString(str, new String[] {"hours", "hour", "h"});
                if(isNumeric(number))
                    cal.add(Calendar.HOUR, Integer.parseInt(number));
            } else if(str.endsWith("min") || str.endsWith("mins") || str.endsWith("minute") || str.endsWith("minutes")) {
                String number = shortString(str, new String[] {"minutes", "minute", "mins", "min", "m"});
                if(isNumeric(number))
                    cal.add(Calendar.MINUTE, Integer.parseInt(number));
            } else if(str.endsWith("sec") || str.endsWith("secs") || str.endsWith("second") || str.endsWith("seconds")) {
                String number = shortString(str, new String[] {"seconds", "second", "secs", "sec", "s"});
                if(isNumeric(number))
                    cal.add(Calendar.SECOND, Integer.parseInt(number));
            }
        }
    }

    public static String getDateByCalendar(Calendar cal) {
        String temp = (cal.get(Calendar.DAY_OF_MONTH) <= 9?"0":"") + cal.get(Calendar.DAY_OF_MONTH);
        temp += "." + (cal.get(Calendar.MONTH) <= 8?"0":"") + String.valueOf(cal.get(Calendar.MONTH)+1);
        temp += "." + cal.get(Calendar.YEAR);
        return temp;
    }
    
    public static String getTimeByCalendar(Calendar cal) {
        return (cal.get(Calendar.HOUR_OF_DAY) <= 9?"0":"") + cal.get(Calendar.HOUR_OF_DAY)
                + ":" + (cal.get(Calendar.MINUTE) <= 9?"0":"") + cal.get(Calendar.MINUTE)
                + ":" + (cal.get(Calendar.SECOND) <= 9?"0":"") + cal.get(Calendar.SECOND);
    }
    
    public static String toString(ArrayList<String> l) {
        String str = "";
        for(String s: l)
            str += (str.isEmpty()?"§e":"§9, §e") + s;
        return str;
    }
    
    public static UUID getUUIDByOfflinePlayer(String name) {
       for(OfflinePlayer op: Bukkit.getOfflinePlayers()) {
           if(op != null) {
               if(op.getName().equalsIgnoreCase(name))
                   return op.getUniqueId();
           }
       }
       return null;
    }
    
    public static String getNameByOfflinePlayer(String uuid) {
       for(OfflinePlayer op: Bukkit.getOfflinePlayers()) {
           if(op != null) {
               if(op.getUniqueId().toString().equalsIgnoreCase(uuid))
                   return op.getName();
           }
       }
       return "";
    }
}
