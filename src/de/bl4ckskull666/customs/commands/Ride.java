/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Utils;
import de.bl4ckskull666.customs.utils.WaitingAction;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

/**
 *
 * @author PapaHarni
 */
public class Ride implements CommandExecutor,Listener {
    private final static ArrayList<Entity> _vehicles = new ArrayList<>();
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("Only Players can be use this command.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(!p.hasPermission("customs.use.riding")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.ride.noperm", "You have no permission to use this command."));
            return true;
        }
        
        if(WaitingAction.isWaiting(p.getName())) {
            WaitingAction wa = WaitingAction.getWaiting(p.getName());
            if(wa.getAction().equalsIgnoreCase("ride")) {
                WaitingAction.removeWaiting(p.getName());
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.ride.cancel", "You have cancel your action."));
            } else {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.ride.other", "You have waiting a other action."));
            }
            return true;
        }
        
        WaitingAction wa = new WaitingAction(p.getName(), "ride", "");
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.ride.press", "Right click now the Entity you want to ride."));
        return true;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        
        if(!WaitingAction.isWaiting(p.getName()))
            return;
        
        WaitingAction wa = WaitingAction.getWaiting(p.getName());
        if(!wa.getAction().equalsIgnoreCase("ride"))
            return;
        
        if(Customs.getPlugin().getConfig().getBoolean("ride.uselist", false)) {
            if(!Customs.getPlugin().getConfig().getStringList("riding.allowed").contains(e.getRightClicked().getType().name().toLowerCase())) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.ride.notlisted", "You can't ride this type of Entity.", new String[] {"%entity%"}, new String[] {Utils.toString((ArrayList<String>)Arrays.asList(Utils.upperFirst(e.getRightClicked().getType().name().toLowerCase().replace("_", " "), true)))}));
                return;
            }
        } else if(!p.hasPermission("customs.use.ride." + e.getRightClicked().getType().name().toLowerCase())) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.ride.noperms", "You have no permission to use this Entity Type for ride.", new String[] {"%entity%"}, new String[] {Utils.toString((ArrayList<String>)Arrays.asList(Utils.upperFirst(e.getRightClicked().getType().name().toLowerCase().replace("_", " "), true)))}));
            return;
        }
        
        e.getRightClicked().setPassenger(p);
        _vehicles.add(e.getRightClicked());
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.ride.successful", "Good Riding on %entity%.", new String[] {"%entity%"}, new String[] {Utils.upperFirst(e.getRightClicked().getType().name().toLowerCase().replace("_", " "), true)}));
        WaitingAction.removeWaiting(p.getName());
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getPlayer().getVehicle() == null)
            return;
        
        if(!_vehicles.contains(e.getPlayer().getVehicle()))
            return;
        
        Entity en = e.getPlayer().getVehicle();
        int x = e.getFrom().getBlockX()-e.getTo().getBlockX();
        int y = e.getFrom().getBlockY()-e.getTo().getBlockY();
        int z = e.getFrom().getBlockZ()-e.getTo().getBlockZ();
        
        en.setVelocity(new Vector(x, y, z));
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDismount(EntityDismountEvent e) {
        if(_vehicles.contains(e.getDismounted()))
            _vehicles.remove(e.getDismounted());
        
        if(_vehicles.contains(e.getEntity()))
            _vehicles.remove(e.getEntity());
    }
}
