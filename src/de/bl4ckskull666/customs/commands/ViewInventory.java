/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Tasks.updateInventorys;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author PapaHarni
 */
public class ViewInventory implements CommandExecutor,Listener {
    private final HashMap<String, BukkitTask> _tasks = new HashMap<>();
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only run by a player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use.viewInventory")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.viewinventory.noPermission", "You dont have permission to show in the inventory of other players."));
            return true;
        }
        
        if(a.length < 1) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.viewinventory.noPlayerGiven", "Please give a player name where you want to see the inventory."));
            return true;
        }
        
        if(Bukkit.getPlayer(a[0]) == null) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.viewinventory.noPlayerFound", "Can't find the given Player online."));
            return true;
        }
        
        Player vp = Bukkit.getPlayer(a[0]);
        p.openInventory(vp.getInventory());
        //_tasks.put(p.getName(), t);
        return true;
    }
    
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!_tasks.containsKey(e.getPlayer().getName()))
            return;
        if(_tasks.get(e.getPlayer().getName()) != null)
            _tasks.get(e.getPlayer().getName()).cancel();
        
        _tasks.remove(e.getPlayer().getName());
    }
}