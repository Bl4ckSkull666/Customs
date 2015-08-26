/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

/**
 *
 * @author PapaHarni
 */
public class ItemInfo implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("");
        }
    
        Player p = (Player)s;
        if(p.getInventory().getItemInHand() == null) {
            p.sendMessage("No item in Hand.");
            return true;
        }
        
        ItemStack item = p.getInventory().getItemInHand();
        try {
            p.sendMessage("§eItem Material : §9" + item.getType().name().replace("_", " ").toLowerCase() + "(" + item.getType().name() + ")");
            p.sendMessage("§eItem Durability : §9" + item.getDurability() + "/" + item.getType().getMaxDurability());
            if(item.hasItemMeta()) {
                p.sendMessage("§9~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                if(item.getItemMeta().hasDisplayName())
                    p.sendMessage("§eDisplay Name : §9" + item.getItemMeta().getDisplayName());
                if(item.getItemMeta().hasLore()) {
                    int i = 1;
                    for(String lore:item.getItemMeta().getLore()) {
                        p.sendMessage("§eLore Line " + i + " : §9" + lore);
                        i++;
                    }
                }
                
                if(item.getItemMeta().hasEnchants()) {
                    for(Map.Entry<Enchantment, Integer> e: item.getItemMeta().getEnchants().entrySet()) {
                        p.sendMessage("§eEnchant : §9" + e.getKey().getName() + " §e- Lv : §9" + e.getValue());
                    }
                }
                if(item.getItemMeta() instanceof BannerMeta)
                    p.sendMessage("§eIs BannerMeta");
                if(item.getItemMeta() instanceof BookMeta)
                    p.sendMessage("§eIs BookMeta");
                if(item.getItemMeta() instanceof EnchantmentStorageMeta)
                    p.sendMessage("§eIs EnchantmentStorageMeta");
                if(item.getItemMeta() instanceof FireworkEffectMeta)
                    p.sendMessage("§eIs FireWorkEffectMeta");
                if(item.getItemMeta() instanceof FireworkMeta)
                    p.sendMessage("§eIs Firework");
                if(item.getItemMeta() instanceof LeatherArmorMeta)
                    p.sendMessage("§eIs LeatherArmorMeta");
                if(item.getItemMeta() instanceof MapMeta)
                    p.sendMessage("§eIs MapMeta");
                if(item.getItemMeta() instanceof PotionMeta)
                    p.sendMessage("§eIs PotionMeta");
                if(item.getItemMeta() instanceof Repairable)
                    p.sendMessage("§eIs Repairable Meta");
                if(item.getItemMeta() instanceof SkullMeta)
                    p.sendMessage("§eIs SkullMeta");
            }
        } catch(Exception e) {
            p.sendMessage("Error on Item Info.");
        }
        return true;
    }
    
}
