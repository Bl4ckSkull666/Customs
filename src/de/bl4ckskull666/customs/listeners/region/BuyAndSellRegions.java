/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners.region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RegionBuySellSign;
import de.bl4ckskull666.customs.utils.RegionUtils;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.uuiddatabase.UUIDDatabase;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public class BuyAndSellRegions implements Listener {
    private final HashMap<String, Long> _lastClick = new HashMap<>();
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent e) {
        if(!"For Sale".equalsIgnoreCase(ChatColor.stripColor(e.getLine(0))))
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("signs.regionbuysellsign.create")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.noPerm", "You don't have permission to create the sign."));
            e.getBlock().breakNaturally();
            return;
        }
        World w = e.getBlock().getWorld();
        
        if(!e.getLine(1).isEmpty()) {
            if(Bukkit.getWorld(e.getLine(1)) != null)
                w = Bukkit.getWorld(e.getLine(1));
            else {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.world-not-exist", "The given World in line 2 don't exist."));
                e.getBlock().breakNaturally();
                return;
            }
        }
        
        if(e.getLine(2).isEmpty()) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.missing.region", "Please add in Line 3 the Region Name."));
            e.getBlock().breakNaturally();
            return;
        }
        
        if(e.getLine(3).isEmpty()) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.missing.price", "Please add in Line 4 the Price for the Region."));
            e.getBlock().breakNaturally();
            return;
        }
        
        if(!RegionUtils.isRegionExist(w, e.getLine(2))) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.noregion", "Please add in Line 4 the Price for the Region."));
            e.getBlock().breakNaturally();
            return;
        }
        
        String[] l4 = e.getLine(3).split(":");
        boolean tp = false;
        if(l4.length == 2) {
            if(l4[1].equalsIgnoreCase("t"))
                tp = true;
        }
        
        if(!Rnd.isNumeric(l4[0])) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.not-a-price", "Please given in line 4 the Price for the Region as number."));
            e.getBlock().breakNaturally();
            return;
        }
        
        ProtectedRegion pr = RegionUtils.getRegion(w, e.getLine(2));
        
        RegionBuySellSign rbss = RegionBuySellSign.getRegionSign(w.getName(), e.getLine(2), Integer.parseInt(l4[0]));
        if(pr.hasMembersOrOwners()) {
            if(pr.getOwners().getUniqueIds().size() > 0) {
                UUID[] temp = pr.getOwners().getUniqueIds().toArray(new UUID[pr.getOwners().getUniqueIds().size()]);
                rbss.setOwnerUUID(temp[0].toString());
                rbss.setOwnerLastName(UUIDDatabase.getNameByUUID(temp[0].toString()));
                rbss.setOwned(true);
            } else if(pr.getOwners().getPlayers().size() > 0) {
                String[] temp = pr.getOwners().getPlayers().toArray(new String[pr.getOwners().getPlayers().size()]);
                rbss.setOwnerUUID(UUIDDatabase.getUUIDByName(temp[0]));
                rbss.setOwnerLastName(temp[0]);
                rbss.setOwned(true);
            }
        }
        rbss.addBuySign(e.getBlock().getLocation());
        rbss.setTpToRegion(e.getBlock().getLocation(), tp);
        rbss.updateSigns();
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.successful", "Region sign have been successful added."));
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        //Right click with Hand --> Buy
        //Right click with Stock in Sneak --> Sell
        //Left click in Sneak --> Break --> Special Admin Permission needed
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;
        
        if(!(e.getClickedBlock().getState() instanceof Sign)) 
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        
        Sign s = (Sign)e.getClickedBlock().getState();
        if(!"[For Sale]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0))) && !"[R-Sold]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0))))
            return;
        
        RegionBuySellSign rbss = RegionBuySellSign.getRegionSign(e.getClickedBlock().getLocation());
        if(rbss == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.unregisted", "This Sign is not a registed Buy Sign. Please tell it the Team."));
            return;
        }
        
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(!p.isSneaking() && (!e.hasItem() || !e.getItem().getType().equals(Material.STICK))) {
                //Buy Sign is it Registed.
                if(_lastClick.containsKey(p.getName()) && !p.hasPermission("region.sign.buy.bypass")) {
                    if((System.currentTimeMillis()-_lastClick.get(p.getName())) < 60000L) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.toFastClick", "Please wait a moment before you click again.."));
                        return;
                    }
                }
                _lastClick.put(p.getName(), System.currentTimeMillis());
                
                String grp = "default";
                if(Customs.getPlugin().isGroupManager())
                    grp = Customs.getPlugin().getGroupManager().getWorldsHolder().getWorldData(p).getUser(p.getName()).getGroupName().toLowerCase();
                int maxPlots = (Customs.getPlugin().getConfig().getInt("plots." + rbss.getWorld() + "." + grp, 3)+ pd.getPlotLimit(rbss.getWorld()));
                if(RegionUtils.getPlayerPlotCount(p.getUniqueId().toString(), rbss.getWorld()) >= maxPlots) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.maximum", "You have reached your limit on plots in this world."));
                    return;
                }
                
                if(rbss.isOwned()) {
                    if(rbss.getOwnerUUID().equalsIgnoreCase(p.getUniqueId().toString()))
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.isMyOwned", "This Region is your Region. You can sell your region when you have a stick in your hand, sneaking and press with your right hand on the sign."));
                    else
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.isOwned", "This Region is already sold."));
                    return;
                }
                
                if(!RegionUtils.setOwner(rbss.getWorld(), rbss.getRegion(), p.getUniqueId())) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.internalerror", "It's happend an error. Please try again."));
                    return;
                }
                
                if(!Customs.canPaidIt(p, rbss.getPrice(), true)) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.needMoney", "You don't have enough Money."));
                    return;
                }
                
                rbss.setBuyBy(p);
                rbss.updateSigns();
                if(rbss.getTpToRegion(e.getClickedBlock().getLocation()))
                    RegionUtils.teleportToRegion(p, rbss.getRegion(), Bukkit.getWorld(rbss.getWorld()));
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.successful", "You have successful buy the region %region%.", new String[] {"%region%"}, new String[] {rbss.getRegion()}));
                Customs.setRegion(rbss.getWorld(), rbss.getRegion(), true);
            } else {
                if(_lastClick.containsKey(p.getName()) && !p.hasPermission("region.sign.sell.bypass")) {
                    if((System.currentTimeMillis()-_lastClick.get(p.getName())) < 60000L) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.toFastClick", "Please wait a moment before you click again."));
                        return;
                    }
                }
                _lastClick.put(p.getName(), System.currentTimeMillis());
                if(!p.getUniqueId().toString().equals(rbss.getOwnerUUID()) && !p.hasPermission("customs.use.region.sign.sell.bypass")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.sell.notTheOwner", "You are not the Owner of this Region."));
                    return;
                }
                
                if(!p.isSneaking()) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.sell.mustSneak", "You don't have enough Money."));
                    return;
                }
                
                if(!e.hasItem()) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.sell.needStick", "You need a Stick in your Hand to sell this region."));
                    return;
                }
                
                if(!e.getItem().getType().equals(Material.STICK)) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.sell.needStick", "You need a Stick in your Hand to sell this region."));
                    return;
                }
                
                if(p.getUniqueId().toString().equalsIgnoreCase(rbss.getOwnerUUID())) {
                    int moneyBack = ((rbss.getPrice()/100)*Customs.getPlugin().getConfig().getInt("region.moneyback", 50));
                    Customs.givePlayerMoney(p, moneyBack);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.sell.successful", "You have successful selled Region %region% for %money% %currency%.", new String[] {"%region%","%money%","%currency%"}, new String[] {rbss.getRegion(), String.valueOf(moneyBack), Customs.currencyNamePlural()}));
                } else
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.sell.successfulByTeam", "You have successful reset Region %region%.", new String[] {"%region%"}, new String[] {rbss.getRegion()}));
                
                RegionUtils.clearRegion(rbss.getRegion(), Bukkit.getWorld(rbss.getWorld()));
                rbss.setSelled();
                rbss.updateSigns();
                Customs.setRegion(rbss.getWorld(), rbss.getRegion(), false);
            }
        }
        
        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            //Left click in Sneak --> Break --> Special Admin Permission needed
            if(!p.hasPermission("customs.use.region.sign.break") && !p.hasPermission("customs.team")) {
                if(rbss.getOwnerUUID().equalsIgnoreCase(p.getUniqueId().toString()))
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.sign.buy.isMyOwned", "This Region is your Region. You can sell your region when you have a stick in your hand, sneaking and press with your right hand on the sign."));
                return;
            }
            
            if(!p.isSneaking())
                return;
            
            if(e.hasItem() && e.getItem().getType().equals(Material.STICK))
                rbss.delAllSigns();
            else
                rbss.delBuySign(e.getClickedBlock().getLocation());
            
            if(rbss.buySignCount() == 0) {
                RegionBuySellSign.removeRegion(rbss.getWorld(), rbss.getRegion());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.remove.all", "Region has been removed from Market."));
            } else
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.remove.sign", "Region Sign has successful destroyed and removed."));
            
            e.getClickedBlock().breakNaturally(new ItemStack(e.getClickedBlock().getType(), 1));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        if(!(e.getBlock().getState() instanceof Sign))
            return;
        Sign s = (Sign)e.getBlock().getState();
        if(!"[For Sale]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0))) && !"[R-Sold]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0))))
            return;
        
        if(RegionBuySellSign.getRegionSign(e.getBlock().getLocation()) == null)
            return;
        
        e.setCancelled(true);
    }
}
