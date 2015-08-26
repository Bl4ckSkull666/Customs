/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RightUtils;
import de.bl4ckskull666.customs.utils.Rnd;
import java.text.DecimalFormat;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author PapaHarni
 */
public class Pds implements Listener {
    private final HashMap<String, HashMap<String, Long>> _lastClick;

    public Pds() {
        _lastClick = new HashMap<>();
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        
        if(!(e.getClickedBlock().getState() instanceof Sign))
            return;
        
        Sign s = (Sign)e.getClickedBlock().getState();
        if(!"[special]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(0).toLowerCase())))
            return;
        
        String line3 = ChatColor.stripColor(s.getLine(2).toLowerCase());
        if(!"home".equalsIgnoreCase(line3) && (!"plot".equalsIgnoreCase(line3) || Bukkit.getWorld(ChatColor.stripColor(s.getLine(1))) == null) &&
                !"speed".equalsIgnoreCase(line3) && !"nightvision".equalsIgnoreCase(line3) &&
                !"invisible".equalsIgnoreCase(line3) && !"stackable".equalsIgnoreCase(line3) &&
                !"fly".equalsIgnoreCase(line3) && !"unlimit_coal".equalsIgnoreCase(line3))
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        int multiplicator = 1;
        
        if("home".equalsIgnoreCase(line3))
            multiplicator = pd.getHomeLimit()+1;
        if("plot".equalsIgnoreCase(line3))
            multiplicator = pd.getPlotLimit(ChatColor.stripColor(s.getLine(1)))+1;
        
        
        String price = ChatColor.stripColor(s.getLine(3).replace(".", ""));
        int pos = price.indexOf(" ");
        if(pos > -1)
            price = price.substring(0, pos);
        price = price.replace(" ", "");
            
        if(!Rnd.isNumeric(price))
            return;

        if(_lastClick.containsKey(p.getUniqueId().toString())) {
            if(!_lastClick.get(p.getUniqueId().toString()).containsKey(line3) || 
                   (System.currentTimeMillis()-_lastClick.get(p.getUniqueId().toString()).get(line3)) > 30000) {
                //Please click again to confirm
                _lastClick.get(p.getUniqueId().toString()).put(line3, System.currentTimeMillis());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.special.confirm." + line3, "The next %type% cost you %amount% %currency%. Click again to accept it.", new String[] {"%type%","%amount%","%currency%"}, new String[] {line3, price, Customs.getEco().currencyNamePlural()}));
                return;
            }
        } else {
            _lastClick.put(p.getUniqueId().toString(), new HashMap<String, Long>());
            _lastClick.get(p.getUniqueId().toString()).put(line3, System.currentTimeMillis());
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.special.confirm." + line3, "The next %type% cost you %amount% %currency%. Click again to accept it.", new String[] {"%type%","%amount%","%currency%"}, new String[] {line3, price, Customs.getEco().currencyNamePlural()}));
            return;
        }
        
        switch(line3) {
            case "home":
                if(pd.getHomeLimit() >= Customs.getPlugin().getConfig().getInt("max.home", 2)) {
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, (multiplicator*Integer.parseInt(price)), true)) {
                    
                    return;
                }
                
                pd.addHomeLimit();
                p.sendMessage("");
                break;
            case "plot":
                if(pd.getPlotLimit(ChatColor.stripColor(s.getLine(1))) >= Customs.getPlugin().getConfig().getInt("max.plot", 2)) {
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, (multiplicator*Integer.parseInt(price)), true)) {
                    
                    return;
                }
                
                pd.addPlotLimit(ChatColor.stripColor(s.getLine(1)));
                p.sendMessage("");
                break;
            case "speed":
                if(!Rnd.isNumeric(s.getLine(1))) {
                    //Error on Sign
                    
                    return;
                }
                
                int step = Integer.parseInt(s.getLine(1));
                if(step > Customs.getPlugin().getConfig().getInt("max.speed", 3)) {
                    //Fly level is higher than allowed on the server.
                    
                    return;
                }
                
                if(p.hasPermission("customs.use.speed." + ChatColor.stripColor(s.getLine(1)))) {
                    //Already have this Speed Level.
                    
                    return;
                }
                
                if(step > 1 && !p.hasPermission("customs.use.speed." + (step-1))) {
                    //Need the lower Speed level before.
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, (multiplicator*Integer.parseInt(price)), true)) {
                    
                    return;
                }
                
                RightUtils.addPermission(p.getName(), p.getLocation().getWorld().getName(), "customs.use.speed." + ChatColor.stripColor(s.getLine(1)));
                
                break;
            case "nightvision":
                if(p.hasPermission("customs.use.nightvision")) {
                    //Already have this Permision.
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, Integer.parseInt(price), true)) {
                    
                    return;
                }
                
                RightUtils.addPermission(p.getName(), p.getLocation().getWorld().getName(), "customs.use.nightvision");
                
                break;
            case "invisible":
                if(p.hasPermission("customs.use.invisible")) {
                    //Already have this Permision.
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, Integer.parseInt(price), true)) {
                    
                    return;
                }
                
                RightUtils.addPermission(p.getName(), p.getLocation().getWorld().getName(), "customs.use.invisible");
                
                break;
            case "stackable":
                if(p.hasPermission("worldguard.stack.illegitimate")) {
                    //Already have this Permission.
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, Integer.parseInt(price), true)) {
                    
                    return;
                }
                
                RightUtils.addPermission(p.getName(), p.getLocation().getWorld().getName(), "worldguard.stack.illegitimate");
                
                break;
            case "fly":
                if(p.hasPermission("customs.use.fly")) {
                    //Already have this Permission.
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, Integer.parseInt(price), true)) {
                    
                    return;
                }
                
                RightUtils.addPermission(p.getName(), p.getLocation().getWorld().getName(), "customs.use.fly");
                
                break;
            case "unlimit_coal":
                if(p.hasPermission("customs.use.unlimit_coal")) {
                    //Already have this Permision.
                    
                    return;
                }
                
                if(!Customs.canPaidIt(p, Integer.parseInt(price), true)) {
                    
                    return;
                }
                
                RightUtils.addPermission(p.getName(), p.getLocation().getWorld().getName(), "customs.use.unlimit_coal");
                
                break;
            default:
                
                break;
        }
        _lastClick.get(p.getUniqueId().toString()).remove(line3);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent e) {
        if(!"[special]".equalsIgnoreCase(ChatColor.stripColor(e.getLine(0).toLowerCase())))
            return;
        
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("signs.regionbuysellsign.create")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.noPerm", "You don't have permission to create the sign."));
            e.getBlock().breakNaturally();
            return;
        }
        
        String line3 = ChatColor.stripColor(e.getLine(2).toLowerCase());
        if(!"home".equalsIgnoreCase(line3) && !"plot".equalsIgnoreCase(line3) &&
                !"speed".equalsIgnoreCase(line3) && !"nightvision".equalsIgnoreCase(line3) &&
                !"invisible".equalsIgnoreCase(line3) && !"stackable".equalsIgnoreCase(line3) &&
                !"fly".equalsIgnoreCase(line3) && !"unlimit_coal".equalsIgnoreCase(line3)) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.special.line3", "Please set Line 3 as Home/Plot/Speed/NightVision/Invisible/Stackable/Fly/Unlimit_Coal."));
            e.getBlock().breakNaturally();
            return;
        }
        
        if("plot".equalsIgnoreCase(line3) && (e.getLine(1).isEmpty() || Bukkit.getWorld(e.getLine(1)) == null)) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.special.line1", "You use Plot in Line 3, so you must add a available World in Line 2."));
            e.getBlock().breakNaturally();
            return;
        }
        
        if(!e.getLine(3).isEmpty() && Customs.getEco() != null) {
            if(!Rnd.isNumeric(e.getLine(3))) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "region.setsign.special.line4", "Please add in Line 4 a price. Default Price."));
                e.getBlock().breakNaturally();
                return;
            }
        }
        
        e.setLine(0, "[" + ChatColor.LIGHT_PURPLE + "Special" + ChatColor.RESET + "]");
        if(!e.getLine(1).isEmpty())
            e.setLine(1, ChatColor.ITALIC + e.getLine(1));
        e.setLine(2, ChatColor.ITALIC + e.getLine(2));
        if(!e.getLine(3).isEmpty() && Customs.getEco() != null) {
            DecimalFormat nf = new DecimalFormat();
            e.setLine(3, ChatColor.ITALIC + nf.format(Long.parseLong(e.getLine(3))) + " " + Customs.getEco().currencyNamePlural());
        }
    }
}
