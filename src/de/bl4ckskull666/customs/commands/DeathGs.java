/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.RegionBuySellSign;
import de.bl4ckskull666.customs.utils.RegionUtils;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.customs.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class DeathGs implements CommandExecutor {
    private long _lastUpdate = 0;
    private String _lastWorld = "";
    private final ArrayList<String> _deathPlot = new ArrayList<>();
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can only be run by a player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        if(!p.hasPermission("customs.use.deathgs") && !p.hasPermission("customs.team")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.deathgs.noPermission", "You don't have permission to use this command."));
            return true;
        }

        World w = p.getWorld();
        int page = 1;
        boolean goToPlot = false;
        boolean clearPlot = false;
        for(String str: a) {
            if(str.equalsIgnoreCase("goto")) {
                goToPlot = true;
            } else if(str.equalsIgnoreCase("clear")) {
                clearPlot = true;
            } else if(Bukkit.getWorld(str) != null) {
                w = Bukkit.getWorld(str);
            } else if(Rnd.isNumeric(str)) {
                page = Integer.parseInt(str);
            } else if(str.equalsIgnoreCase("all")) {
                w = null;
            }
        }
        
        if(goToPlot && !clearPlot && _deathPlot.size() > 0 && page <= _deathPlot.size()) {
            goToThePlot(p, pd, page);
        } else if(!goToPlot && clearPlot && _deathPlot.size() > 0 && page <= _deathPlot.size()) {
            clearThePlot(p, pd, page);
        } else {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.deathgs.inProgress", "Death Plots will be loading. Please wait a moment."));
            Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new checkPlots(p, w, page));
        }
        return true;
    }
    
    private void goToThePlot(Player p, PlayerData pd, int plotId) {
        String[] plotInfo = _deathPlot.get((plotId-1)).split("::");
        RegionUtils.teleportToRegion(p, plotInfo[1], Bukkit.getWorld(plotInfo[2]));
    }
    
    private void clearThePlot(Player p, PlayerData pd, int plotId) {
        String[] plotInfo = _deathPlot.get((plotId-1)).split("::");
        RegionUtils.clearRegion(plotInfo[1], Bukkit.getWorld(plotInfo[2]));
        Customs.setRegion(plotInfo[2], plotInfo[1], false);
        RegionBuySellSign rbss = RegionBuySellSign.getRegionSign(plotInfo[2], plotInfo[1]);
        if(rbss != null)
            rbss.setSelled();
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.deathgs.plotCleared", "The wished plot is cleared now."));
    }
    
    public class checkPlots implements Runnable {
        private final Player _p;
        private final World _w;
        private final int _page;
        
        public checkPlots(Player pl, World w, int pa) {
            _p = pl;
            _w = w;
            _page = pa;
        }
        
        @Override
        public void run() {
            PlayerData pd = PlayerData.getPlayerData(_p);
            if(_lastUpdate == 0 || (System.currentTimeMillis()-_lastUpdate) > 300000L || _lastWorld.isEmpty() || _w != null && !_lastWorld.equalsIgnoreCase(_w.getName())) {
                if(_w == null)
                    _lastWorld = "all";
                else
                    _lastWorld = _w.getName();
                _deathPlot.clear();
                
                Map<String, Long> users = new HashMap<>();
                List<World> useWorlds = new ArrayList<>();
                if(_w == null) {
                    useWorlds = Bukkit.getWorlds();
                } else
                    useWorlds.add(_w);
                
                for(World w: useWorlds) {
                    RegionManager rm = Customs.getPlugin().getWG().getRegionManager(w);
                    ArrayList<String> temp = new ArrayList<>();
                    for (Map.Entry<String, ProtectedRegion> e : rm.getRegions().entrySet()) {
                        ProtectedRegion pr = e.getValue();
                        if(!pr.hasMembersOrOwners())
                            continue;

                        DefaultDomain dd = pr.getOwners();
                        for(String player: dd.getPlayers()) {
                            String pUUID = player;
                            if(temp.equals(e.getKey()))
                                continue;
                            
                            if(player.length() < 32) {
                                UUID uuid = Utils.getUUIDByOfflinePlayer(player);
                                if(uuid != null)
                                    pUUID = uuid.toString();
                            }
                            
                            long lastLogOut = 0;
                            if(users.containsKey(player))
                                lastLogOut = users.get(player);
                            else {
                                PlayerData upd = PlayerData.getPlayerData(pUUID);
                                lastLogOut = upd.getTimeStamp("logout");
                                users.put(player, lastLogOut);
                            }

                            if(lastLogOut == 0) {
                                continue;
                            }

                            if((System.currentTimeMillis()-lastLogOut) >= 2592000000L) {
                                _deathPlot.add(player + "::" + e.getKey() + "::" + w.getName());
                                temp.add(e.getKey());
                            }
                        }
                        
                        for(UUID uuid: dd.getUniqueIds()) {
                            if(temp.equals(e.getKey()))
                                continue;
                            
                            long lastLogOut = 0;
                            if(users.containsKey(uuid.toString()))
                                lastLogOut = users.get(uuid.toString());
                            else {
                                PlayerData upd = PlayerData.getPlayerData(uuid.toString());
                                lastLogOut = upd.getTimeStamp("logout");
                                users.put(uuid.toString(), lastLogOut);
                            }
                            
                            String player = uuid.toString();
                            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                            if(op != null)
                                player = op.getName();

                            if(lastLogOut == 0) {
                                continue;
                            }

                            if((System.currentTimeMillis()-lastLogOut) >= 2592000000L) {
                                _deathPlot.add(player + "::" + e.getKey() + "::" + w.getName());
                                temp.add(e.getKey());
                            }
                        }
                    }
                }
            } else
                _p.sendMessage(Language.getMessage(Customs.getPlugin(), _p.getUniqueId(), "command.deathgs.useCache", "§6§l~~~ §r§cUsing cache of death plots §6§l~~~"));
            
            _lastUpdate = System.currentTimeMillis();
            if(_deathPlot.isEmpty()) {
                _p.sendMessage(Language.getMessage(Customs.getPlugin(), _p.getUniqueId(), "command.deathgs.noDeathPlots", "§6§l~~ §r§cNo death plots found. §6§l~~"));
                return;
            }

            _p.sendMessage(Language.getMessage(Customs.getPlugin(), _p.getUniqueId(), "command.deathgs.header", "§6§l~~~~~ §r§c%count% §9death plots found §6§l~~~~~", new String[] {"%count%"}, new String[] {String.valueOf(_deathPlot.size())}));
            int end = Math.min(_deathPlot.size(), (_page*10));
            
            for(int i = ((_page*10)-10);i < end; i++) {
                String[] gsinfo = _deathPlot.get(i).split("::");
                _p.sendMessage(Language.getMessage(Customs.getPlugin(), _p.getUniqueId(), "command.deathgs.plot", "§c%id%. §a%name% §e: §9%plot% - §eOwner : §9%world%", new String[] {"%id%","%name%","%plot%","%world%"}, new String[] {String.valueOf((i+1)),gsinfo[0],gsinfo[1],gsinfo[2]}));
            }
            
            if(Math.floor(_deathPlot.size()/10) > 1)
                _p.sendMessage(Language.getMessage(Customs.getPlugin(), _p.getUniqueId(), "command.deathgs.sites", "§9Page §e%page% §9of §c%total%", new String[] {"%page%","%total%"}, new String[] {String.valueOf(_page), String.valueOf(Math.round(Math.floor(_deathPlot.size()/10)))}));
        }
    }
}