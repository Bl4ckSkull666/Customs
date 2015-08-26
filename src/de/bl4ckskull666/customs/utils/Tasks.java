/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import com.griefcraft.model.Protection;
import com.sk89q.craftbook.mechanics.area.CopyManager;
import com.sk89q.craftbook.mechanics.area.CuboidCopy;
import com.sk89q.craftbook.mechanics.area.CuboidCopyException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.LoadAndSave;
import de.bl4ckskull666.customs.commands.Tpa;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author PapaHarni
 */
public final class Tasks {
    public static class autoSaver implements Runnable {
        @Override
        public void run() {
            LoadAndSave.saveBuySellSigns(Customs.getPlugin());
            LoadAndSave.saveWarps(Customs.getPlugin());
            LoadAndSave.loadBooks(Customs.getPlugin());
        }
    }
    
    public static class checkMobSpawnFlag implements Runnable {
        private final Player _p;
        
        public checkMobSpawnFlag(Player p) {
            _p = p;
        }
        
        @Override
        public void run() {
            for(World w: Bukkit.getWorlds()) {
                RegionManager rm = Customs.getPlugin().getWG().getRegionManager(w);
                if(rm == null)
                    continue;
                
                for(Map.Entry<String, ProtectedRegion> e: rm.getRegions().entrySet()) {
                    try {
                        if(e.getValue().getFlag(DefaultFlag.MOB_SPAWNING) == null)
                            continue;
                        if(e.getValue().getFlag(DefaultFlag.MOB_SPAWNING) == State.ALLOW && e.getValue().getFlag(DefaultFlag.DENY_SPAWN) == null)
                            _p.sendMessage("§cPlot §9" + e.getKey() + " §con World §9" + w.getName() + " §chas §9mob-spawning allow §cbut no §9deny-spawn§c.");
                    } catch(Exception ex) {}
                }
            }
        }
    }
    
    public static class checkEntityStatus implements Runnable {
        @Override
        public void run() {
            for(World w: Bukkit.getWorlds()) {
                if(!Customs.getPlugin().getConfig().isConfigurationSection("removeEntity." + w.getName().toLowerCase()))
                    continue;
                
                if(!Customs.getPlugin().getConfig().isList("removeEntity." + w.getName().toLowerCase() + ".type"))
                    continue;
                
                List<String> kill = Customs.getPlugin().getConfig().getStringList("removeEntity." + w.getName().toLowerCase() + ".type");
                int t = Customs.getPlugin().getConfig().getInt("removeEntity." + w.getName().toLowerCase() + ".time", 300)*20;
                for(LivingEntity e: w.getLivingEntities()) {
                    if(Customs.getPlugin().isCitizens()) {
                        if(Customs.getPlugin().getNPC().getNPCRegistry().isNPC(e))
                            continue;
                    }
                    
                    if(e.getCustomName() != null && Customs.getPlugin().getConfig().isList("removeEntity." + w.getName().toLowerCase() + ".save-names")) {
                        if(Customs.getPlugin().getConfig().getStringList("removeEntity." + w.getName().toLowerCase() + ".save-names").contains(e.getCustomName().toLowerCase()))
                            continue;
                    }
                    
                    if(kill.contains(e.getType().name().toLowerCase()) && e.getTicksLived() >= t)
                        e.remove();
                }
            }
        }
    }
    
    public static class checkTeleportRequests implements Runnable {
        @Override
        public void run() {
            ArrayList<TeleportRequest> temp = new ArrayList<>();
            for(TeleportRequest tpr: Tpa.getRequests()) {
                if((System.currentTimeMillis()-tpr.getRequestSendTime()) > (Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20)*1000L)) {
                    if(Bukkit.getPlayer(tpr.getFrom()) != null) {
                        Player p = Bukkit.getPlayer(tpr.getFrom());
                        PlayerData pd = PlayerData.getPlayerData(p);
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.tprequests.cancel.sender", "Your Teleport request to " + tpr.getTo() + " has time expired. Automatic cancel.", new String[] {"%name%"}, new String[] {tpr.getTo()}));
                    }
                    
                    if(Bukkit.getPlayer(tpr.getTo()) != null) {
                        Player p = Bukkit.getPlayer(tpr.getTo());
                        PlayerData pd = PlayerData.getPlayerData(p);
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.tprequests.cancel.receiver", "The Teleport request of " + tpr.getFrom() + " has time expired. Automatic cancel.", new String[] {"%name%"}, new String[] {tpr.getFrom()}));
                    }
                    temp.add(tpr);
                }
            }
            for(TeleportRequest tpr: temp)
                Tpa.getRequests().remove(tpr);
        }
    }
    
    public static class updateRegionSigns implements Runnable {
        private final RegionBuySellSign _rbss;
        public updateRegionSigns(RegionBuySellSign rbss) {
            _rbss = rbss;
        }
        
        @Override
        public void run() {
            DecimalFormat nf = new DecimalFormat();
            for(Location loc: _rbss.getSigns()) {
                Block b = loc.getBlock();

                if(!(b.getState() instanceof Sign)) {
                    Customs.getPlugin().getLogger().log(Level.WARNING, "Can't find Buy sign at {0} {1} {2} {3}", new Object[]{loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()});
                    continue;
                }

                Sign s = (Sign)b.getState();
                if(_rbss.isOwned()) {
                    s.setLine(0, "[" + ChatColor.DARK_RED + "R-Sold" + ChatColor.RESET + "]");
                    s.setLine(1, "");
                    s.setLine(2, ChatColor.ITALIC + "to");
                    s.setLine(3, ChatColor.ITALIC + _rbss.getOwnerLastName());
                } else {
                    s.setLine(0, "[" + ChatColor.DARK_GREEN + "For Sale" + ChatColor.RESET + "]");
                    s.setLine(1, "");
                    s.setLine(2, ChatColor.ITALIC + "Price :");
                    s.setLine(3, ChatColor.ITALIC + nf.format(_rbss.getPrice()) + " " + Customs.getEco().currencyNamePlural());
                }
                s.update();

                for(Entity ent: loc.getWorld().getEntities()) {
                    if(ent.getType().equals(EntityType.PLAYER) && ent.getLocation().distance(loc) <= 32.0)
                        ((Player)ent).sendSignChange(loc, s.getLines());
                }
            }
        }
    }
    
    public static class clearRegionAsync implements Runnable {
        private final String _world;
        private final String _region;
        public clearRegionAsync(String world, String region) {
            _world = world;
            _region = region;
        }
        
        @Override
        public void run() {
            RegionManager rm = Customs.getPlugin().getWG().getRegionManager(Bukkit.getWorld(_world));
            ProtectedRegion pr = rm.getRegion(_region);

            if(pr != null) {
                RegionUtils.clearFlagsAndMore(pr);
                
                int x1 = pr.getMinimumPoint().getBlockX();
                int x2 = pr.getMaximumPoint().getBlockX();
                int y1 = pr.getMinimumPoint().getBlockY();
                int y2 = pr.getMaximumPoint().getBlockY();
                int z1 = pr.getMinimumPoint().getBlockZ();
                int z2 = pr.getMaximumPoint().getBlockZ();

                //Remove LWC Protetions
                if(Customs.getPlugin().isLWC()) {
                    List<Protection> lis = Customs.getPlugin().getLWC().getLWC().getPhysicalDatabase().loadProtections(_world, x1, x2, y1, y2, z1, z2);
                    for(Protection prot: lis)
                        prot.remove();
                }

                //Remove FrameProtect
                if(Customs.getPlugin().isFrameProtect()) {
                    List<ch.dragon252525.frameprotect.protection.Protection> lis = Customs.getPlugin().getFrameProtect().getDataManager().loadProtections();
                    for(ch.dragon252525.frameprotect.protection.Protection prot: lis) {
                        if(!prot.getData().getWorld().getName().equalsIgnoreCase(_world))
                            continue;
                        if(prot.getData().getX() < x1 || prot.getData().getX() > x2)
                            continue;
                        if(prot.getData().getY() < y1 || prot.getData().getY() > y2)
                            continue;
                        if(prot.getData().getZ() < z1 || prot.getData().getZ() > z2)
                            continue;
                        Customs.getPlugin().getFrameProtect().getDataManager().removeProtection(prot.getUuid());
                    }
                }
            }
            Bukkit.getScheduler().runTask(Customs.getPlugin(), new clearRegionSync(_world, _region));
        }
    }
    
    public static class clearRegionAsyncEnd implements Runnable {
        private final String _world;
        private final String _region;
        public clearRegionAsyncEnd(String world, String region) {
            _world = world;
            _region = region;
        }
        
        @Override
        public void run() {
            RegionManager rm = Customs.getPlugin().getWG().getRegionManager(Bukkit.getWorld(_world));
            ProtectedRegion pr = rm.getRegion(_region);

            if(pr != null) {
                RegionUtils.clearFlagsAndMore(pr);
                
                int x1 = pr.getMinimumPoint().getBlockX();
                int x2 = pr.getMaximumPoint().getBlockX();
                int y1 = pr.getMinimumPoint().getBlockY();
                int y2 = pr.getMaximumPoint().getBlockY();
                int z1 = pr.getMinimumPoint().getBlockZ();
                int z2 = pr.getMaximumPoint().getBlockZ();

                for(Entity ent: Bukkit.getWorld(_world).getEntities()) {
                    if(ent.getType() == EntityType.PLAYER)
                        continue;
                    if(ent.getLocation().getBlockX() < x1 || ent.getLocation().getBlockX() > x2)
                        continue;
                    if(ent.getLocation().getBlockY() < y1 || ent.getLocation().getBlockY() > y2)
                        continue;
                    if(ent.getLocation().getBlockZ() < z1 || ent.getLocation().getBlockZ() > z2)
                        continue;
                    ent.remove();
                } 
            }
        }
    }
    
    public static class clearRegionSync implements Runnable {
        private final String _world;
        private final String _region;
        public clearRegionSync(String world, String region) {
            _world = world;
            _region = region;
        }
        
        @Override
        public void run() {
            try {
                CuboidCopy copy = CopyManager.getInstance().load(Bukkit.getWorld(_world), "Server", _region);
                if(copy != null)
                    copy.paste();
            } catch (IOException | CuboidCopyException ex) {
                Customs.getPlugin().getLogger().log(Level.WARNING, "Can't clear region " + _region + " on world " + _world, ex);
                Utils.InformTeam("Can't clear region " + _region + " on world " + _world);
            }
            Bukkit.getScheduler().runTaskAsynchronously(Customs.getPlugin(), new clearRegionAsyncEnd(_world, _region));
        }
    }
    
    public static class changePriceOfRegion implements Runnable {
        private final World _world;
        private final String _region;
        private final String _type;
        private final int _price;
        private final CommandSender _sender;
        private final UUID _lang;
        public changePriceOfRegion(CommandSender s, UUID l, World w, String r, String t, int p) {
            _sender = s;
            _lang = l;
            _world = w;
            _region = r;
            _type = t;
            _price = p;
        }
        
        @Override
        public void run() {
            switch(_type.toLowerCase()) {
                case "buy":
                    if(!RegionBuySellSign.getRegions().containsKey(_world.getName().toLowerCase())) {
                        _sender.sendMessage(Language.getMessage(Customs.getPlugin(), _lang, "command.regionsign.buy.noRegionsInWorld", "Can't find any regionsigns in the given world."));
                        return;
                    }
                    int count = 0;
                    for(Map.Entry<String, RegionBuySellSign> e: RegionBuySellSign.getRegions().get(_world.getName().toLowerCase()).entrySet()) {
                        if(e.getKey().startsWith(_region)) {
                            e.getValue().setPrice(_price);
                            e.getValue().updateSigns();
                            count++;
                        }
                    }
                    if(count == 0) {
                        _sender.sendMessage(Language.getMessage(Customs.getPlugin(), _lang, "command.regionsign.buy.noUpdates", "No Region Buy Signs found."));
                    } else
                        _sender.sendMessage(Language.getMessage(Customs.getPlugin(), _lang, "command.regionsign.buy.updatedPrice", count + " region buy signs are updated successful.", new String[] {"%count%"}, new String[] {String.valueOf(count)}));
                    break;
                case "rent":
                    _sender.sendMessage(Language.getMessage(Customs.getPlugin(), _lang, "command.regionsign.notImplemented", "Function is not implemented yet."));
                    break;
                case "let":
                    _sender.sendMessage(Language.getMessage(Customs.getPlugin(), _lang, "command.regionsign.notImplemented", "Function is not implemented yet."));
                    break;
                default:
                    _sender.sendMessage(Language.getMessage(Customs.getPlugin(), _lang, "command.regionsign.notImplemented", "Function is not implemented yet."));
                    break;
            }
        }
    }
    
    public static class updateInventorys implements Runnable {
        private final Player _o;
        private final Player _v;
        public updateInventorys(Player open, Player view) {
            _o = open;
            _v = view;
        }
        
        @Override
        public void run() {
            _o.updateInventory();
            _v.updateInventory();
        }
    }
    
    public static class RamChecker implements Runnable {
        @Override
        public void run() {
            Runtime rt = Runtime.getRuntime();
            long pcfree = ((100/rt.maxMemory())*rt.freeMemory());
            if(pcfree <= Customs.getPlugin().getConfig().getLong("memory.low-percent", 10L))
                Utils.InformTeam(ChatColor.DARK_RED + "" + ChatColor.BOLD + "WARNING LOW FREE RAM");

            if(pcfree <= Customs.getPlugin().getConfig().getLong("memory.restart-percent", 2L)) {
                Bukkit.savePlayers();
                for(Player p: Bukkit.getOnlinePlayers())
                    p.kickPlayer("Server Automatic Restart. We are back in two Minutes.");
                
                for(World w: Bukkit.getWorlds())
                    w.save();
                
                Bukkit.shutdown();
            }
        }
    }
    
    public static class checkMyTicks implements Runnable {
        private final CommandSender _s;
        private final int _r;
        private final long _sT;
        
        public checkMyTicks(CommandSender s, int round, long startTime) {
            _s = s;
            _r = round;
            _sT = startTime;
        }
        
        @Override
        public void run() {
            if(_r < 20) {
                Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new checkMyTicks(_s, (_r+1), _sT), 1L);
                return;
            }
            long total = System.currentTimeMillis()-_sT;
            float fT = (float)(total/1000);
            _s.sendMessage("20 Ticks in " + fT + " seconds");
        }
    }
    
    public static class TeleportPlayer implements Runnable {
        private final Player _p;
        private final Entity _e;
        private final ArrayList<Entity> _l;
        private final Location _loc;
        
        public TeleportPlayer(Player p, Entity e, ArrayList<Entity> l, Location loc) {
            _p = p;
            _e = e;
            _l = l;
            _loc = loc;
        }
        
        @Override
        public void run() {
            _p.teleport(_loc);
            if(_e != null || _l.size() > 0)
                Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new TeleportPlayerAfter(_p, _e, _l), 40);
        }
    }
    
    public static class TeleportPlayerAfter implements Runnable {
        private final Player _p;
        private final Entity _e;
        private final ArrayList<Entity> _l;
        
        public TeleportPlayerAfter(Player p, Entity e, ArrayList<Entity> l) {
            _p = p;
            _e = e;
            _l = l;
        }
        
        @Override
        public void run() {
            if(_e != null)
                _e.setPassenger(_p);
        
            if(_l.isEmpty()) {
                for(Entity leash: _l)
                    Utils.setLeashed(_p, leash);
            }
        }
    }
    
    public static class RemoveFly implements Runnable {
        private final String _name;
        private final RFly _rf;
        
        public RemoveFly(String name, RFly rf) {
            _name = name;
            _rf = rf;
        }
        
        @Override
        public void run() {
            if(_rf.getTask() != null) 
                _rf.getTask().cancel();
            
            RFly.removeRFly(_name);
            if(Bukkit.getPlayer(UUID.fromString(_name)) != null) {
                Player p = Bukkit.getPlayer(UUID.fromString(_name));
                PlayerData pd = PlayerData.getPlayerData(p);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.rentfly.timeend", "Your fly rent time is over."));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (30*20), 1));
                p.setAllowFlight(false);
                p.setFlying(false);
            }
        }
    }
    
    public static class updateFurnaces implements Runnable {
        @Override
        public void run() {
            for(Location l: Customs.getFurnaces()) {
                Block b = l.getBlock();
                if(!(b.getState() instanceof Furnace)) {
                    Customs.delFurnace(l);
                    continue;
                }
                
                if(!l.getChunk().isLoaded())
                    l.getChunk().load();
                
                Furnace f = (Furnace)b.getState();
                f.setBurnTime(Short.MAX_VALUE);
            }
        }
    }
    
    public static class checkOpenPlayerData implements Runnable {
        @Override
        public void run() {
            PlayerData.checkOpenPlayerDatas();
        }
    }
}
        
