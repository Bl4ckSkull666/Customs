/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RFly;
import de.bl4ckskull666.customs.utils.Rnd;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public class RentFly implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        if(!(e.getClickedBlock().getState() instanceof Sign))
            return;
        
        Sign s = (Sign)e.getClickedBlock().getState();
        if(!"[RentFly]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0))))
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        if(!e.getPlayer().hasPermission("customs.use.rentfly")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.noPerm", "You don't have permission to use this sign."));
            return;
        }
        
        if(s.getLine(1).equalsIgnoreCase("info")) {
            if(!RFly.isRFly(p.getUniqueId().toString())) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.noTime", "You don't have open fly time yet."));
                return;
            }
            
            RFly rf = RFly.getRFly(p.getUniqueId().toString());
            long time = rf.getTime()-((System.currentTimeMillis()-rf.getBeginTime())/1000);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.leftTime", "You have %time% minutes fly time.", new String[] {"%time%"}, new String[] {String.valueOf((int)Math.ceil(time/60))}));
            return;
        }
        
        String lineTime = ChatColor.stripColor(s.getLine(2).replace(" ", ""));
        if(!lineTime.endsWith("min."))
            return;
        
        String linePrice = ChatColor.stripColor(s.getLine(3).replace(" ", ""));
        if(Customs.getEco() != null) {
            if(!linePrice.endsWith(Customs.getEco().currencyNamePlural()))
                return;
        }
        
        lineTime = lineTime.substring(0, lineTime.length()-4);
        linePrice = linePrice.replace(Customs.getEco().currencyNamePlural(), "");
        
        if(!Rnd.isNumeric(linePrice) || !Rnd.isNumeric(lineTime))
            return;
        
        int price = Integer.parseInt(linePrice);
        if(!Customs.canPaidIt(p, price, true)) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.needMoney", "You havn't enough money to buy rent fly time."));
            return;
        }
        
        long t = Integer.parseInt(lineTime)*60;
        boolean isAdd = RFly.isRFly(p.getUniqueId().toString());
        RFly rf = RFly.setRFly(p.getUniqueId().toString(), t);
        if(isAdd) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.add", "You have buy %time% minutes more fly time. You can now fly %total% minutes.", new String[] {"%time%","%total%"}, new String[] {lineTime, String.valueOf((int)Math.ceil(rf.getTime()/60))}));
        } else {
            p.setAllowFlight(true);
            p.setFlying(true);  
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.set", "You have buy %time% minutes of fly time. Activate and deactivate fly now with /fly. Good Luck.", new String[] {"%time%"}, new String[] {lineTime}));
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent e) {
        if(!"RentFly".equalsIgnoreCase(e.getLine(0)))
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        if(!e.getPlayer().hasPermission("customs.create.rentfly")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.noPermCreate", "You don't have permission to create a rent fly sign."));
            e.getBlock().breakNaturally(new ItemStack(Material.SIGN,1));
            e.setCancelled(true);
            return;
        }
        
        if(e.getLine(1).equalsIgnoreCase("info")) {
            e.setLine(0, "[" + ChatColor.DARK_GREEN + "RentFly" + ChatColor.RESET + "]");
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.infosign", "The Info Sign was successful created."));
            return;
        }
        
        if(e.getLine(2).isEmpty() || !Rnd.isNumeric(e.getLine(2))) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.needTime", "Need a time in Minutes on Line 3."));
            e.getBlock().breakNaturally(new ItemStack(Material.SIGN,1));
            e.setCancelled(true);
            return;
        }
        
        if(e.getLine(3).isEmpty() || !Rnd.isNumeric(e.getLine(3))) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.needPrice", "Need a price on Line 4."));
            e.getBlock().breakNaturally(new ItemStack(Material.SIGN,1));
            e.setCancelled(true);
            return;
        }
        
        e.setLine(0, "[" + ChatColor.DARK_GREEN + "RentFly" + ChatColor.RESET + "]");
        e.setLine(2, e.getLine(2) + " min.");
        if(Customs.getEco() != null)
            e.setLine(3, e.getLine(3) + " " + Customs.getEco().currencyNamePlural());
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.createdsign", "The RentFly Sign was successful created."));
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(RFly.getOfflineRentTime(e.getPlayer().getUniqueId().toString()) == 0L)
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        RFly rf = RFly.setRFly(e.getPlayer().getUniqueId().toString(), RFly.getOfflineRentTime(e.getPlayer().getUniqueId().toString()));
        RFly.removeOfflineRentTime(e.getPlayer().getUniqueId().toString());
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.over", "You have a rest time of %time% minutes to fly.", new String[] {"%time%"}, new String[] {String.valueOf((int)Math.ceil(rf.getTime()/60)
        
        )}));
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(!RFly.isRFly(e.getPlayer().getUniqueId().toString()))
            return;
        
        Player p = e.getPlayer();
        RFly rf = RFly.getRFly(p.getUniqueId().toString());
        
        long restTime = rf.getTime()-((System.currentTimeMillis()-rf.getBeginTime())/1000);
        RFly.setOfflineRentTime(p.getUniqueId().toString(), restTime);
        if(rf.getTask() != null)
            rf.getTask().cancel();
        RFly.removeRFly(p.getUniqueId().toString());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent e) {
        if(!RFly.isRFly(e.getPlayer().getUniqueId().toString()))
            return;
        
        Player p = e.getPlayer();
        RFly rf = RFly.getRFly(p.getUniqueId().toString());
        
        long restTime = rf.getTime()-((System.currentTimeMillis()-rf.getBeginTime())/1000);
        RFly.setOfflineRentTime(p.getUniqueId().toString(), restTime);
        if(rf.getTask() != null)
            rf.getTask().cancel();
        RFly.removeRFly(p.getUniqueId().toString());
    }
}
