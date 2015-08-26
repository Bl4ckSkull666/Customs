package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.RegionUtils;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Pappi
 */
public class BreakSpawner implements Listener {
    private final int _needHits = 10;
    private final long _maxHitTimeOut = 2000;
    private final HashMap<String, Integer> _hits = new HashMap<>();
    private final HashMap<String, Long> _lastHitTime = new HashMap<>();
    private final HashMap<String, Location> _lastHitLoc = new HashMap<>();
    
/*    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null)
            return;
        
        Customs.getPlugin().getLogger().log(Level.INFO, "Use PlayerInteract");
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockDamage(BlockDamageEvent e) {
        Customs.getPlugin().getLogger().log(Level.INFO, "Use BlockDamage");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        Customs.getPlugin().getLogger().log(Level.INFO, "Use BlockBreak");
        if(e.getBlock() == null || e.getPlayer().getItemInHand() == null)
            return;
        
        if(!e.getPlayer().getItemInHand().getType().equals(Material.DIAMOND_PICKAXE))
            return;
        
        if(!(e.getBlock().getState() instanceof CreatureSpawner))
            return;

        if(!Customs.getPlugin().getWG().canBuild(e.getPlayer(), e.getBlock()))
            return;

        if(!RegionUtils.isInOwnRegion(e.getPlayer().getName(), e.getBlock().getLocation()) && Customs.getPlugin().getConfig().getBoolean("break-spawner-in-own-region-only", true))
            return;
                    
        if(!_hits.containsKey(e.getPlayer().getName())) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            _lastHitLoc.put(e.getPlayer().getName(), e.getBlock().getLocation());
            return;
        }
        
        if(_lastHitLoc.get(e.getPlayer().getName()).distance(e.getBlock().getLocation()) >= 1.0) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            _lastHitLoc.put(e.getPlayer().getName(), e.getBlock().getLocation());
            return;
        }
        
        if((System.currentTimeMillis()-_lastHitTime.get(e.getPlayer().getName())) > _maxHitTimeOut) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            return;
        }
        
        if(_hits.get(e.getPlayer().getName()) < _needHits) {
            _hits.put(e.getPlayer().getName(), _hits.get(e.getPlayer().getName())+1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            return;
        }
        
        _hits.remove(e.getPlayer().getName());
        _lastHitTime.remove(e.getPlayer().getName());
        _lastHitLoc.remove(e.getPlayer().getName());
        
        CreatureSpawner csp = (CreatureSpawner)e.getBlock().getState();
        if(Customs.getPlugin().getConfig().isInt("mobtypes." + csp.getSpawnedType().name().toLowerCase())) {
            ItemStack item = new ItemStack(Material.MONSTER_EGG, 10, (short)Customs.getPlugin().getConfig().getInt("mobtypes." + csp.getSpawnedType().name().toLowerCase()));
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), item);
        }
        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.MOB_SPAWNER, 1));
        e.getBlock().breakNaturally();
    }*/
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null || !e.hasItem())
            return;
        
        if(!e.getItem().getType().equals(Material.DIAMOND_PICKAXE))
            return;
        
        if(!(e.getClickedBlock().getState() instanceof CreatureSpawner))
            return;

        if(!Customs.getPlugin().getWG().canBuild(e.getPlayer(), e.getClickedBlock())) {
            return;
        }
    
        if(!RegionUtils.isInOwnRegion(e.getPlayer().getName(), e.getClickedBlock().getLocation()) && Customs.getPlugin().getConfig().getBoolean("break-spawner-in-own-region-only", true)) {
            return;
        }
                    
        if(!_hits.containsKey(e.getPlayer().getName())) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            _lastHitLoc.put(e.getPlayer().getName(), e.getClickedBlock().getLocation());
            e.setCancelled(true);
            return;
        }
        
        if(_lastHitLoc.get(e.getPlayer().getName()).distance(e.getClickedBlock().getLocation()) >= 1.0) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            _lastHitLoc.put(e.getPlayer().getName(), e.getClickedBlock().getLocation());
            e.setCancelled(true);
            return;
        }
        
        if((System.currentTimeMillis()-_lastHitTime.get(e.getPlayer().getName())) > _maxHitTimeOut) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            e.setCancelled(true);
            return;
        }
        
        if(_hits.get(e.getPlayer().getName()) < _needHits) {
            _hits.put(e.getPlayer().getName(), _hits.get(e.getPlayer().getName())+1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            e.setCancelled(true);
            return;
        }
        
        _hits.remove(e.getPlayer().getName());
        _lastHitTime.remove(e.getPlayer().getName());
        _lastHitLoc.remove(e.getPlayer().getName());
        
        CreatureSpawner csp = (CreatureSpawner)e.getClickedBlock().getState();
        if(Customs.getPlugin().getConfig().isInt("mobtypes." + csp.getSpawnedType().name().toLowerCase())) {
            ItemStack item = new ItemStack(Material.MONSTER_EGG, 10, (short)Customs.getPlugin().getConfig().getInt("mobtypes." + csp.getSpawnedType().name().toLowerCase()));
            e.getClickedBlock().getWorld().dropItem(e.getClickedBlock().getLocation(), item);
        }
        e.getClickedBlock().getWorld().dropItem(e.getClickedBlock().getLocation(), new ItemStack(Material.MOB_SPAWNER, 1));
        e.getClickedBlock().breakNaturally();
    }
}