package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Pappi
 */
public class BreakCommandblock implements Listener {
    private final int _needHits = 10;
    private final long _maxHitTimeOut = 2000;
    private final HashMap<String, Integer> _hits = new HashMap<>();
    private final HashMap<String, Long> _lastHitTime = new HashMap<>();
    private final HashMap<String, Location> _lastHitLoc = new HashMap<>();
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null || !e.hasItem())
            return;
        
        if(!e.getItem().getType().equals(Material.DIAMOND_PICKAXE))
            return;
        
        if(!(e.getClickedBlock().getState() instanceof CommandBlock))
            return;
        
        if(!Customs.getPlugin().getWG().canBuild(e.getPlayer(), e.getClickedBlock()))
            return;
        
        if(!_hits.containsKey(e.getPlayer().getName())) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            _lastHitLoc.put(e.getPlayer().getName(), e.getClickedBlock().getLocation());
            return;
        }
        
        if(_lastHitLoc.get(e.getPlayer().getName()).distance(e.getClickedBlock().getLocation()) >= 1.0) {
            _hits.put(e.getPlayer().getName(), 1);
            _lastHitTime.put(e.getPlayer().getName(), System.currentTimeMillis());
            _lastHitLoc.put(e.getPlayer().getName(), e.getClickedBlock().getLocation());
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
        
        e.getClickedBlock().breakNaturally(new ItemStack(Material.COMMAND, 1));
    }
}
