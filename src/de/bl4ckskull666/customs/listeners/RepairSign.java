/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Rnd;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public class RepairSign implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        if(!(e.getClickedBlock().getState() instanceof Sign))
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        Sign s = (Sign)e.getClickedBlock().getState();
        if(!"[repair]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0))))
            return;
        
        String line2 = ChatColor.stripColor(s.getLine(1));
        String line4 = ChatColor.stripColor(s.getLine(3));

        if(!line4.isEmpty() && Customs.getEco() != null)
            line4 = line4.replace(" " + Customs.getEco().currencyNamePlural(), "");
        
        if(line2.isEmpty() || (!line2.equalsIgnoreCase("hand") && !line2.equalsIgnoreCase("inventory")) || 
                ((line4.isEmpty() || !Rnd.isNumeric(line4)) && Customs.getEco() != null)) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.use.breakedSign", "This repair sign is broken."));
            return;
        }
        
        if(!p.hasPermission("customs.use.repair." + line2.toLowerCase())) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.use.noPermission", "You don't have permission to use this Sign."));
            return;
        }
        
        if(Customs.getEco() != null && !Customs.canPaidIt(p, Integer.parseInt(line4), false)) {
           p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.use.noEnoughtMoney", "You don't have enough money to use this."));
           return;
        }
        
        boolean isRepaired = false;
        switch(s.getLine(1).toLowerCase()) {
            case "hand":
                if(p.getItemInHand() == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.use.noItemInHand", "You need an Item in your Hand."));
                    return;
                }
                
                isRepaired = RepairItems(p, pd, new ItemStack[] {p.getItemInHand()}, true);
                break;
            case "inventory":
                ArrayList<ItemStack> items = new ArrayList<>();
                for(int i = 0; i < p.getInventory().getSize(); i++) {
                    try {
                        if(p.getInventory().getItem(i) != null)
                            items.add(p.getInventory().getItem(i));
                    } catch(Exception ex) {}
                }
                for(ItemStack it: p.getInventory().getArmorContents()) {
                    if(it != null)
                        items.add(it);
                }
                
                isRepaired = RepairItems(p, pd, items.toArray(new ItemStack[items.size()]), false);
                break;
            default:
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.use.signBroken", "The Sign is broken."));
                break;
        }
        
        if(isRepaired && Customs.getEco() != null) {
            Customs.canPaidIt(p, Integer.parseInt(line4), true);
        }
        p.updateInventory();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent e) {
        if(!"[repair]".equalsIgnoreCase(ChatColor.stripColor(e.getLine(0))))
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(e.getLine(1).isEmpty() || (!e.getLine(1).equalsIgnoreCase("hand") && !e.getLine(1).equalsIgnoreCase("inventory"))) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.create.needType", "Need Type Hand or Inventory in line 2."));
            e.setCancelled(true);
            e.getBlock().breakNaturally(new ItemStack(e.getBlock().getType(), 1));
            return;
        }
        
        if(!p.hasPermission("customs.create.repair." + e.getLine(1))) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.create.noPermission", "You don't have permission to create this type of repair sign."));
            e.setCancelled(true);
            e.getBlock().breakNaturally(new ItemStack(e.getBlock().getType(), 1));
            return;
        }
        
        if((e.getLine(3).isEmpty() || !Rnd.isNumeric(e.getLine(3))) && Customs.getEco() != null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.create.needPrice", "Need a Price in Line 4."));
            e.setCancelled(true);
            e.getBlock().breakNaturally(new ItemStack(e.getBlock().getType(), 1));
            return;
        }
        
        e.setLine(0, "[" + ChatColor.DARK_GREEN + "Repair" + ChatColor.RESET + "]");
        e.setLine(1, String.valueOf(e.getLine(1).charAt(0)).toUpperCase() + e.getLine(1).substring(1));
        e.setLine(2, ChatColor.ITALIC + "Cost");
        e.setLine(3, ChatColor.ITALIC + e.getLine(3) + (Customs.getEco() != null?" " + Customs.getEco().currencyNamePlural():""));
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.create.successful", "Repair sign successful created."));
    }
    
    private boolean RepairItems(Player p, PlayerData pd, ItemStack[] items, boolean isSingle) {
        for(ItemStack item: items) {
            if(item.getType().getMaxDurability() == 0) {
                if(isSingle) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.notRepairable", "The wished Item can't repair."));
                    return false;
                }
                continue;
            }
            
            if(item.getDurability() == 0) {
                if(isSingle) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.notNeedRepair", "The wished Item can't repair."));
                    return false;
                }
                continue;
            }
            
            item.setDurability((short)0);
            String name = item.hasItemMeta()?item.getItemMeta().hasDisplayName()?item.getItemMeta().getDisplayName():item.getType().name().replace("_", " "):item.getType().name().replace("_", " ");
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.repair.repairSuccessful", name + " was repaired successful.", new String[] {"%name%"}, new String[] {name}));
        }
        return true;
    }
}
