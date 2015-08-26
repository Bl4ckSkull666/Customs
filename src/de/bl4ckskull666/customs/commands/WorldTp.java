/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.Rnd;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Pappi
 */
public class WorldTp implements CommandExecutor {
    
    private final HashMap<String, BukkitTask> _tasks;

    public WorldTp() {
        _tasks = new HashMap<>();
    }
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) { 
            s.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return true;
        }
        
        Player p = (Player)s;
        World w = p.getWorld();
        
        if(a.length >= 1) {
            w = Bukkit.getWorld(a[0]);
            if(w == null) {
                p.sendMessage("Die von dir angegebene Welt existiert leider nicht.");
                return true;
            }
        }
        
        if(!p.hasPermission("customcommands.worldtp.*") && !p.hasPermission("customcommands.worldtp." + w.getName().toLowerCase())) {
            p.sendMessage("Du hast keine Rechte diesen Befehl zu verwenden für diese Welt.");
            return true;
        }
        
        BukkitTask bTask = Bukkit.getScheduler().runTask(Customs.getPlugin(), new SearchTeleportPoint(p, w, 1));
        _tasks.put(p.getName(), bTask);
        return true;
    }
    
    public class SearchTeleportPoint implements Runnable {
        private final Player _p;
        private final World _w;
        private final int _r;
        
        public SearchTeleportPoint(Player p, World w, int r) {
            _p = p;
            _w = w;
            _r = r;
        }
        
        @Override
        public void run() {
            if(_tasks.containsKey(_p.getName())) {
                if(_tasks.get(_p.getName()) != null)
                    _tasks.get(_p.getName()).cancel();
                _tasks.remove(_p.getName());
            }
            
            int x = Rnd.get(0, 40000)-20000;
            int z = Rnd.get(0, 40000)-20000;
            Chunk ch = _w.getChunkAt(x, z);
            if(!ch.isLoaded())
                ch.load();
            for(int y = 255; y > 0; y--) {
                Entity et = null;
                Block b = _w.getBlockAt(x, y, z);
                Block bt = _w.getBlockAt(x, y+1, z);
                Block btt = _w.getBlockAt(x, y+2, z);
                if(b == null || bt == null || btt == null)
                    continue;

                if(!b.getType().equals(Material.AIR) && !b.getType().equals(Material.LAVA) && bt.getType().equals(Material.AIR) && btt.getType().equals(Material.AIR)) {
                    if(b.getType().equals(Material.STATIONARY_WATER)) {
                        if(Rnd.get(0, 1) == 0) {
                            Customs.getPlugin().getLogger().log(Level.INFO, "Setze Boot ins Wasser.");
                            et = _w.spawnEntity(b.getLocation(), EntityType.BOAT);
                        } else {
                            Customs.getPlugin().getLogger().log(Level.INFO, "Setze Seerose.");
                            bt.setType(Material.WATER_LILY);
                            bt.getState().setType(Material.WATER_LILY);
                            bt.getState().update(true);
                        }
                    }
                    if(!bt.getLocation().getChunk().isLoaded())
                        bt.getLocation().getChunk().load();
                    BukkitTask bTask = Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new TeleportPlayer(_p, bt.getLocation(), et), 100);
                    _tasks.put(_p.getName(), bTask);
                    _p.sendMessage("§2Du wirst in 5 Sekunden Teleportiert. §9- §aYou will be teleporting in 5 seconds.");
                    return;
                }
            }
            
            if(_r == 5) {
                _p.sendMessage("Konnte kein passenden Platz ermitteln. Bitte versuch es erneut.");
                return;
            }
            
            //Search new
            BukkitTask bTask = Bukkit.getScheduler().runTask(Customs.getPlugin(), new SearchTeleportPoint(_p, _w, (_r+1)));
            _tasks.put(_p.getName(), bTask);
        }
    }
    
    public class TeleportPlayer implements Runnable {
        private final Player _p;
        private final Location _l;
        private final Entity _et;
        
        public TeleportPlayer(Player p, Location l, Entity et) {
            _p = p;
            _l = l;
            _et = et;
        }
        
        @Override
        public void run() {
            if(_tasks.containsKey(_p.getName())) {
                if(_tasks.get(_p.getName()) != null)
                    _tasks.get(_p.getName()).cancel();
                _tasks.remove(_p.getName());
            }
            _p.teleport(_l, PlayerTeleportEvent.TeleportCause.COMMAND);
            _p.sendMessage("§2Viel Spass in der Wildnis. §9- §aHave Fun in the wilderness.");
            if(_et != null) {
                BukkitTask bTask = Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new EnterPlayerInBoat(_p, _et), 40);
                _tasks.put(_p.getName(), bTask);
            }
        }
    }
    
    public class EnterPlayerInBoat implements Runnable {
        private final Player _p;
        private final Entity _et;
        
        public EnterPlayerInBoat(Player p, Entity et) {
            _p = p;
            _et = et;
        }
        
        @Override
        public void run() {
            if(_tasks.containsKey(_p.getName())) {
                if(_tasks.get(_p.getName()) != null)
                    _tasks.get(_p.getName()).cancel();
                _tasks.remove(_p.getName());
            }
            if(_p instanceof Entity)
                _et.setPassenger((Entity)_p);
        }
    }
}
