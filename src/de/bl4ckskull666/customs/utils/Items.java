/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import de.bl4ckskull666.customs.Customs;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author Pappi
 */
public final class Items {
    public static boolean isItem(String str) {
        return ((getItem(str) != null));
    }
    
    public static ItemStack getItem(String str) {
        String[] args = str.split(" ");
        if(args.length < 2) {
            Customs.getPlugin().getLogger().log(Level.INFO, "{0} hat zu wenig Argumente Mindestens : Itemname Itemmenge - sind gefordert.", str);
            return null;
        }
        
        if(!Rnd.isNumeric(args[1])) {
            Customs.getPlugin().getLogger().log(Level.INFO, "{0} muss eine Zahl sein", args[1]);
            return null;
        }
        
        String[] itemname = args[0].split("\\:");
        if(Material.matchMaterial(itemname[0]) == null) {
            Customs.getPlugin().getLogger().log(Level.INFO, "{0} ist kein gultiges Item.", itemname[0]);
            return null;
        }
        
        ItemStack i;
        if(itemname.length >= 2 && Rnd.isNumeric(itemname[1]))
            i = new ItemStack(Material.matchMaterial(itemname[0]), Integer.parseInt(args[1]), Short.parseShort(itemname[1]));
        else
            i = new ItemStack(Material.matchMaterial(itemname[0]), Integer.parseInt(args[1]));
        
        i.setItemMeta(Bukkit.getItemFactory().getItemMeta(i.getType()));
        
        if(args.length >= 3) {
            for(int a = 2; a < args.length; a++) {
                String[] sargs = args[a].split(":");
                if(sargs.length == 2) {
                    switch(sargs[0].toLowerCase()) {
                        case "lore":
                            String[] msg = sargs[1].split("\\|");
                            if(msg.length > 4)
                                break;
                            List<String> lore = new ArrayList<>();
                            for (String msg1 : msg) {
                                lore.add(ChatColor.translateAlternateColorCodes('&', msg1.replaceAll("_", " ")));
                            }
                            setLore(i, lore);
                            break;
                        case "name":
                            setItemName(i, sargs[1]);
                            break;
                        case "color":
                            setColor(i, sargs[1]);
                            break;
                        case "book":
                            setBook(i, sargs[1]);
                            break;
                        case "player":
                            setPlayerHead(i, sargs[1]);
                            break;
                        case "myname":
                            checkMyName(i, sargs[1]);
                            break;
                        default:
                            if((Enchantment.getByName(sargs[0].toUpperCase()) != null || getEnchant(sargs[0]) != null) && Rnd.isNumeric(sargs[1]))
                                setEnchantment(i, sargs[0], Integer.parseInt(sargs[1]));
                            else
                                Customs.getPlugin().getLogger().log(Level.INFO, "Ignore {0}:{1}", new Object[]{sargs[0], sargs[1]});
                            break;
                    }
                }
            }
        }
        return i;
    }
    
    private static void setItemName(ItemStack i, String name) {
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name.replaceAll("_", " ")));
        i.setItemMeta(im);
    }
    
    private static void setLore(ItemStack i, List<String> lore) {
        ItemMeta im = i.getItemMeta();
        im.setLore(lore);
        i.setItemMeta(im);
    }
    
    private static void setColor(ItemStack i, String color) {
        if(i.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta lam = (LeatherArmorMeta)i.getItemMeta();
            String[] rgb = color.split("\\,");
            if(rgb.length == 3) {
                if(Rnd.isNumeric(rgb[0]) && Rnd.isNumeric(rgb[1]) && Rnd.isNumeric(rgb[2])) {
                    if(Integer.parseInt(rgb[0]) >= 0 && Integer.parseInt(rgb[0]) <= 255 && 
                            Integer.parseInt(rgb[1]) >= 0 && Integer.parseInt(rgb[1]) <= 255 && 
                            Integer.parseInt(rgb[2]) >= 0 && Integer.parseInt(rgb[2]) <= 255) {
                        Color c = Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                        lam.setColor(c);
                    }
                }
            }
            i.setItemMeta(lam);
        }
    }
    
    private static void checkMyName(ItemStack i, String name) {
        if(i.getItemMeta().hasDisplayName())
            i.getItemMeta().setDisplayName(i.getItemMeta().getDisplayName().replace("%myname%", name));
        
        if(i.getItemMeta().hasLore()) {
            List<String> temp = new ArrayList<>();
            for(String str: i.getItemMeta().getLore()) {
                temp.add(str.replace("%myname%", name));
            }
            i.getItemMeta().setLore(temp);
        }
        
        if((i.getItemMeta() instanceof BookMeta)) {
            BookMeta bm = (BookMeta)i.getItemMeta();
            List<String> temp = new ArrayList<>();
            for(String page: bm.getPages())
                temp.add(page.replace("%myname%", name));
            bm.setPages(temp);
            i.setItemMeta(bm);
        }
    }
    
    //END_PORTAL
    //EXPLORE_ALL_BIOMES
    //THE_END
    //FULL_BEACON
    
    private static void setPlayerHead(ItemStack i, String name) {
        if(!(i.getItemMeta() instanceof SkullMeta))
            return;
        
        SkullMeta sm = (SkullMeta)i.getItemMeta();
        sm.setOwner(name);
        sm.setDisplayName(name);
        i.setItemMeta(sm);
    }
    
    private static void setBook(ItemStack i, String book) {
        if(Customs.getBook(book) == null)
            return;
        
        if(!(i.getItemMeta() instanceof BookMeta))
            return;
        
        BookData bd = Customs.getBook(book);
        BookMeta bm = (BookMeta)i.getItemMeta();
        bm.setPages(bd.getArrayPages());
        if(!bd.getAutor().isEmpty())
            bm.setAuthor(ChatColor.translateAlternateColorCodes('&', bd.getAutor()));
        if(!bd.getTitle().isEmpty())
            bm.setTitle(ChatColor.translateAlternateColorCodes('&', bd.getTitle()));
        
        i.setItemMeta(bm);
    }
    
    private static void setEnchantment(ItemStack i, String ench, int lvl) {
        Enchantment e = (getEnchant(ench) != null)?getEnchant(ench):Enchantment.getByName(ench.toUpperCase());
        if(i.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)i.getItemMeta();
            esm.addStoredEnchant(e, lvl, true);
            if(!esm.hasStoredEnchant(e))
                Customs.getPlugin().getLogger().log(Level.INFO, "Failed to add {0} to the EnchantmentStorageMeta.", ench);
            i.setItemMeta(esm);
        } else {
            ItemMeta im = i.getItemMeta();
            im.addEnchant(e, lvl, true);
            if(!im.hasEnchant(e))
                Customs.getPlugin().getLogger().log(Level.INFO, "Failed to add {0} to the ItemMeta.", ench);
            i.setItemMeta(im);
        }
    }
    
    public static Enchantment getEnchant(String name) {
        name = name.toLowerCase();
        if(name.toLowerCase().equalsIgnoreCase("fire_protection")) return Enchantment.PROTECTION_FIRE;
        if(name.toLowerCase().equalsIgnoreCase("blast_protection")) return Enchantment.PROTECTION_EXPLOSIONS;
        if(name.toLowerCase().equalsIgnoreCase("projectile_protection")) return Enchantment.PROTECTION_PROJECTILE;
        if(name.toLowerCase().equalsIgnoreCase("protection")) return Enchantment.PROTECTION_ENVIRONMENTAL;
        if(name.toLowerCase().equalsIgnoreCase("feather_falling")) return Enchantment.PROTECTION_FALL;
        if(name.toLowerCase().equalsIgnoreCase("respiration")) return Enchantment.OXYGEN;
        if(name.toLowerCase().equalsIgnoreCase("aqua_affinity")) return Enchantment.WATER_WORKER;
        if(name.toLowerCase().equalsIgnoreCase("sharpness")) return Enchantment.DAMAGE_ALL;
        if(name.toLowerCase().equalsIgnoreCase("smite")) return Enchantment.DAMAGE_UNDEAD;
        if(name.toLowerCase().equalsIgnoreCase("bane_of_arthropods")) return Enchantment.DAMAGE_ARTHROPODS;
        if(name.toLowerCase().equalsIgnoreCase("knockback")) return Enchantment.KNOCKBACK;
        if(name.toLowerCase().equalsIgnoreCase("fire_aspect")) return Enchantment.FIRE_ASPECT;
        if(name.toLowerCase().equalsIgnoreCase("looting")) return Enchantment.LOOT_BONUS_MOBS;
        if(name.toLowerCase().equalsIgnoreCase("power")) return Enchantment.ARROW_DAMAGE;
        if(name.toLowerCase().equalsIgnoreCase("punch")) return Enchantment.ARROW_KNOCKBACK;
        if(name.toLowerCase().equalsIgnoreCase("flame")) return Enchantment.ARROW_FIRE;
        if(name.toLowerCase().equalsIgnoreCase("infinity")) return Enchantment.ARROW_INFINITE;
        if(name.toLowerCase().equalsIgnoreCase("efficiency")) return Enchantment.DIG_SPEED;
        if(name.toLowerCase().equalsIgnoreCase("unbreaking")) return Enchantment.DURABILITY;
        if(name.toLowerCase().equalsIgnoreCase("silk_touch")) return Enchantment.SILK_TOUCH;
        if(name.toLowerCase().equalsIgnoreCase("fortune")) return Enchantment.LOOT_BONUS_BLOCKS;
        if(name.toLowerCase().equalsIgnoreCase("thorns")) return Enchantment.THORNS;
        return null;
    }
    
    public static boolean hasEnoughSize(Player p, int need) {
        int free = 0;
        for(ItemStack item: p.getInventory().getContents()) {
            if(item == null)
                free++;
        }
        return (free >= need);
    }
}
