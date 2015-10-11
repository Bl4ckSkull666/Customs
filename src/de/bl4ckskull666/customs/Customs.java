/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.bl4ckskull666.customs;

import de.bl4ckskull666.customs.listeners.region.BuyAndSellRegions;
import de.bl4ckskull666.customs.commands.region.Member;
import de.bl4ckskull666.customs.commands.region.RegionSign;
import ch.dragon252525.frameprotect.FrameProtect;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import de.bl4ckskull666.customs.commands.*;
import de.bl4ckskull666.customs.commands.dummy.Dummy;
import de.bl4ckskull666.customs.commands.region.Access;
import de.bl4ckskull666.customs.commands.region.Farewell;
import de.bl4ckskull666.customs.commands.region.Greeting;
import de.bl4ckskull666.customs.commands.region.MobDeny;
import de.bl4ckskull666.customs.commands.region.MobSpawn;
import de.bl4ckskull666.customs.commands.region.MyHome;
import de.bl4ckskull666.customs.listeners.*;
import de.bl4ckskull666.customs.utils.*;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.Tasks.autoSaver;
import de.bl4ckskull666.customs.utils.Tasks.checkEntityStatus;
import de.bl4ckskull666.customs.utils.Tasks.checkOpenPlayerData;
import de.bl4ckskull666.customs.utils.Tasks.checkTeleportRequests;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.TheDgtl.Tombstone.Tombstone;
import net.citizensnpcs.Citizens;
import net.milkbowl.vault.economy.Economy;
import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Pappi
 */
public class Customs extends JavaPlugin {
    
    private boolean _isLWC = false;
    private boolean _isWG = false;
    private boolean _isNPC = false;
    private boolean _isCB = false;
    private boolean _isFP = false;
    private boolean _isTS = false;
    private boolean _isGroupManager = false;
    
    private Location _spawnPoint;
    private Location _firstSpawnPoint;
    private final ArrayList<BukkitTask> _tasks = new ArrayList<>();
    
    @Override
    public void onEnable() {
        _p = this;
        
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
            getConfig().options().copyDefaults(true);
        }
        saveConfig();
        setupEconomy();
        
        Plugin p = Bukkit.getPluginManager().getPlugin("LWC");
        if(p != null && (p instanceof LWCPlugin)) {
            _isLWC = true;
            getLogger().log(Level.INFO, "LWC found! All functions with LWC can be used.");
        }
        
        p = getServer().getPluginManager().getPlugin("WorldGuard");
        if (p != null && (p instanceof WorldGuardPlugin)) {
            _isWG = true;
            getLogger().log(Level.INFO, "WorldGuard found! All functions with WorldGuard can be used.");
            if(!getConfig().getBoolean("deactivate.function.breakspawner", false))
                getServer().getPluginManager().registerEvents(new BreakSpawner(), this);
            
            if(!getConfig().getBoolean("deactivate.function.breakcommandblock", false))
                getServer().getPluginManager().registerEvents(new BreakCommandblock(), this);
            
            if(!getConfig().getBoolean("deactivate.function.fixflintandsteel", false))
                getServer().getPluginManager().registerEvents(new FixFlintAndSteel(), this);
            
            if(!getConfig().getBoolean("deactivate.function.buyandsellsign", false)) {
                getServer().getPluginManager().registerEvents(new BuyAndSellRegions(), this);
                LoadAndSave.loadRegions(this);
                LoadAndSave.loadBuySellSigns(this);
            }
            
            if(!getConfig().getBoolean("deactivate.function.armorstand", false))
                getServer().getPluginManager().registerEvents(new ArmorStandEvent(), this);
            
            if(!getConfig().getBoolean("deactivate.command.plot", false))
                getCommand("plot").setExecutor(new Plot());
            
            if(!getConfig().getBoolean("deactivate.command.deathgs", false))
                getCommand("deathgs").setExecutor(new DeathGs());
            
            if(!getConfig().getBoolean("deactivate.command.regionsign", false))
                getCommand("regionsign").setExecutor(new RegionSign());
            
            if(!getConfig().getBoolean("deactivate.command.member", false))
                getCommand("member").setExecutor(new Member());

            if(!getConfig().getBoolean("deactivate.command.mobdeny", false))
                getCommand("mobdeny").setExecutor(new MobDeny());
            
            if(!getConfig().getBoolean("deactivate.command.mobspawn", false))
                getCommand("mobspawn").setExecutor(new MobSpawn());
            
            if(!getConfig().getBoolean("deactivate.command.greeting", false))
                getCommand("greeting").setExecutor(new Greeting());
            
            if(!getConfig().getBoolean("deactivate.command.farewell", false))
                getCommand("farewell").setExecutor(new Farewell());
            
            if(!getConfig().getBoolean("deactivate.command.access", false))
                getCommand("access").setExecutor(new Access());
            
            if(!getConfig().getBoolean("deactivate.command.myhome", false))
                getCommand("myhome").setExecutor(new MyHome());
        }
        
        p = getServer().getPluginManager().getPlugin("GroupManager");
        if (p != null && (p instanceof GroupManager)) {
            _isGroupManager = true;
            if(!getConfig().getBoolean("deactivate.function.chat", false))
                getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        }
        
        p = getServer().getPluginManager().getPlugin("Citizens");
        if (p != null && (p instanceof Citizens)) {
            _isNPC = true;
        }
        
        p = getServer().getPluginManager().getPlugin("Craftbook");
        if (p != null && (p instanceof CraftBookPlugin)) {
            _isCB = true;
        }
        
        p = getServer().getPluginManager().getPlugin("FrameProtect");
        if (p != null && (p instanceof FrameProtect)) {
            _isFP = true;
        }
        
        p = getServer().getPluginManager().getPlugin("Tombstone");
        if (p != null && (p instanceof Tombstone)) {
            _isTS = true;
            if(!getConfig().getBoolean("deactivate.function.tombstoneopen", false))
                getServer().getPluginManager().registerEvents(new TombStoneOpen(), this);
        }
        
        if(getServer().getVersion().toLowerCase().contains("spigot")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getLogger().log(Level.INFO, "Plugin Message Channel Outgoing registred.");
        }
        
        if(!getConfig().getBoolean("deactivate.function.join-left-message", false))
            getServer().getPluginManager().registerEvents(new JoinLeftMessage(), this);
        
        if(!getConfig().getBoolean("deactivate.function.join", false))
            getServer().getPluginManager().registerEvents(new onJoin(), this);
        
        if(!getConfig().getBoolean("deactivate.function.achievments", false))
            getServer().getPluginManager().registerEvents(new Achievments(), this);
        
        if(!getConfig().getBoolean("deactivate.function.disposal", false))
            getServer().getPluginManager().registerEvents(new Disposal(), this);
        
        if(!getConfig().getBoolean("deactivate.function.checkkill", false))
            getServer().getPluginManager().registerEvents(new CheckKill(), this);
        
        if(!getConfig().getBoolean("deactivate.command.expdrop", false)) {
            getServer().getPluginManager().registerEvents(new ExpDrop(), this);
            getCommand("expdrop").setExecutor(new Expdrop());
        }
        
        if(!getConfig().getBoolean("deactivate.function.repairsign", false))
            getServer().getPluginManager().registerEvents(new RepairSign(), this);
        
        if(!getConfig().getBoolean("deactivate.command.viewinventory", false)) {
            getServer().getPluginManager().registerEvents(new ViewInventory(), this);
            getCommand("viewinventory").setExecutor(new ViewInventory());
        }
        if(!getConfig().getBoolean("deactivate.function.rentfly", false)) {
            getServer().getPluginManager().registerEvents(new RentFly(), this);
            LoadAndSave.loadRentFlyTimes(this);
        }
        
        if(!getConfig().getBoolean("deactivate.function.gamestore", false)) {
            getServer().getPluginManager().registerEvents(new ShopChest(), this);
            File f = new File(ShopChest.checkPath(), "gamestore.yml");
            _gamestore = YamlConfiguration.loadConfiguration(f);
        }
        
        if(!getConfig().getBoolean("deactivate.function.ignore-teleport", false))
            getServer().getPluginManager().registerEvents(new PlayerTeleport(), this);
        
        if(!getConfig().getBoolean("deactivate.function.npcprotection", false))
            getServer().getPluginManager().registerEvents(new NPCProtection(), this);
        
        if(!getConfig().getBoolean("deactivate.function.tabcomplete", false)) {
            getServer().getPluginManager().registerEvents(new PlayerChatTabComplete(), this);
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PlayerChatTabComplete());
        }
        
        if(!getConfig().getBoolean("deactivate.command,worldtp", false))
            getCommand("worldtp").setExecutor(new WorldTp());
        
        if(!getConfig().getBoolean("deactivate.command.aworld", false))
            getCommand("aworld").setExecutor(new AnnounceWorld());
        
        if(!getConfig().getBoolean("deactivate.command.clearinventory", false))
            getCommand("clearinventory").setExecutor(new ClearInventory());
        
        if(!getConfig().getBoolean("deactivate.command.fly", false))
            getCommand("fly").setExecutor(new Fly());
        
        if(!getConfig().getBoolean("deactivate.command.gamemode", false))
            getCommand("gamemode").setExecutor(new GameMode());
        
        if(!getConfig().getBoolean("deactivate.command.speed", false))
            getCommand("speed").setExecutor(new Speed());
        
        if(!getConfig().getBoolean("deactivate.command.birthday", false))
            getCommand("birthday").setExecutor(new Birthday());
        
        if(!getConfig().getBoolean("deactivate.command.gender", false))
            getCommand("gender").setExecutor(new Gender());
        
        if(!getConfig().getBoolean("deactivate.command.kit", false)) {
            getCommand("kit").setExecutor(new Kits());
            LoadAndSave.loadKits(this);
        }
        
        if(!getConfig().getBoolean("deactivate.command.invisible", false))
            getCommand("invisible").setExecutor(new Invisible());
        
        if(!getConfig().getBoolean("deactivate.command.nightvision", false))
            getCommand("nightvision").setExecutor(new Nightvision());
        
        if(!getConfig().getBoolean("deactivate.command.creload", false))
            getCommand("creload").setExecutor(new Creload());
        
        if(!getConfig().getBoolean("deactivate.command.broadcast", false))
            getCommand("broadcast").setExecutor(new Broadcast());
        
        if(!getConfig().getBoolean("deactivate.command.give", false)) {
            getCommand("give").setExecutor(new Give());
            LoadAndSave.loadBooks(this);
        }
        
        if(!getConfig().getBoolean("deactivate.command.kill", false))
            getCommand("kill").setExecutor(new Kill());
        
        if(!getConfig().getBoolean("deactivate.command.remove", false))
            getCommand("remove").setExecutor(new Remove());

        if(!getConfig().getBoolean("deactivate.command.infoitem", false))
            getCommand("infoitem").setExecutor(new ItemInfo());
        
        if(!getConfig().getBoolean("deactivate.command.spawnmob", false))
            getCommand("spawnmob").setExecutor(new SpawnMob());
        
        if(!getConfig().getBoolean("deactivate.command.userinfo", false))
            getCommand("userinfo").setExecutor(new UserInfo());
        
        if(!getConfig().getBoolean("deactivate.command.achievments", false))
            getCommand("achievements").setExecutor(new SetAchievement());
        
        if(!getConfig().getBoolean("deactivate.command.help", false))
            getCommand("help").setExecutor(new Help());
        
        if(!getConfig().getBoolean("deactivate.command.tppos", false))
            getCommand("tppos").setExecutor(new Tppos());
        
        if(!getConfig().getBoolean("deactivate.command.ride", false)) {
            getCommand("ride").setExecutor(new Ride());
            getServer().getPluginManager().registerEvents(new Ride(), this);
        }
        
        //Teleport Commands
        if(!getConfig().getBoolean("deactivate.command.spawn", false)) {
            getCommand("spawn").setExecutor(new Spawn());
            getCommand("setspawn").setExecutor(new Spawn());
            getCommand("delspawn").setExecutor(new Spawn());
            if(getConfig().isConfigurationSection("spawnpoint")) {
                if(Bukkit.getWorld(getConfig().getString("spawnpoint.world")) != null) {
                    _spawnPoint = new Location(Bukkit.getWorld(getConfig().getString("spawnpoint.world")),
                        getConfig().getDouble("spawnpoint.x"),
                        getConfig().getDouble("spawnpoint.y"),
                        getConfig().getDouble("spawnpoint.z"),
                        (float)getConfig().getDouble("spawnpoint.yaw"),
                        (float)getConfig().getDouble("spawnpoint.pitch"));
                }
            }

            if(getConfig().isConfigurationSection("firstspawnpoint")) {
                if(Bukkit.getWorld(getConfig().getString("firstspawnpoint.world")) != null) {
                    _firstSpawnPoint = new Location(Bukkit.getWorld(getConfig().getString("firstspawnpoint.world")),
                        getConfig().getDouble("firstspawnpoint.x"),
                        getConfig().getDouble("firstspawnpoint.y"),
                        getConfig().getDouble("firstspawnpoint.z"),
                        (float)getConfig().getDouble("firstspawnpoint.yaw"),
                        (float)getConfig().getDouble("firstspawnpoint.pitch"));
                }
            }
        }
        
        if(!getConfig().getBoolean("deactivate.command.home", false)) {
            getCommand("home").setExecutor(new Home());
            getCommand("sethome").setExecutor(new Home());
            getCommand("delhome").setExecutor(new Home());
        }
        
        if(!getConfig().getBoolean("deactivate.command.warp", false)) {
            getCommand("warp").setExecutor(new Warp());
            getCommand("setwarp").setExecutor(new Warp());
            getCommand("delwarp").setExecutor(new Warp());
            LoadAndSave.loadWarps(this);
        }
        
        if(!getConfig().getBoolean("deactivate.command.back", false))
            getCommand("back").setExecutor(new Back());
        
        if(!getConfig().getBoolean("deactivate.command.tp", false))
            getCommand("tp").setExecutor(new Tp());
        
        if(!getConfig().getBoolean("deactivate.command.tphere", false))
            getCommand("tphere").setExecutor(new Tp());
        
        if(!getConfig().getBoolean("deactivate.command.tpa", false))
            getCommand("tpa").setExecutor(new Tpa());
        
        if(!getConfig().getBoolean("deactivate.command.tpahere", false))
            getCommand("tpahere").setExecutor(new Tpa());
        
        if(!getConfig().getBoolean("deactivate.command.tpacancel", false))
            getCommand("tpacancel").setExecutor(new Tpa());
        
        if(!getConfig().getBoolean("deactivate.command.tpadeny", false))
            getCommand("tpadeny").setExecutor(new Tpa());
        
        if(!getConfig().getBoolean("deactivate.command.tpaaccept", false))
            getCommand("tpaaccept").setExecutor(new Tpa());
        
        
        LoadAndSave.loadBlockedWorldsByCommand(this);;
        _p = this;
        
        //Database Checks
        _db = new Database();
        getConfig().set("database.server.useable", _db.checkServerDBConnection());
        getConfig().set("database.bungee.useable", _db.checkBungeeDBConnection());
        if(!getConfig().getBoolean("database.server.useable") || !getConfig().getBoolean("database.bungee.useable"))
            getLogger().log(Level.INFO, "Happend error on using Database. Please check it to save and load datas there.");
 
        
        //Start Tasks
        BukkitTask ctr = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new checkTeleportRequests(), 100, 100);
        _tasks.add(ctr);
        BukkitTask ces = Bukkit.getServer().getScheduler().runTaskTimer(this, new checkEntityStatus(), (30*20), (30*20));
        _tasks.add(ces);
        BukkitTask as = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new autoSaver(), (1800*20), (1800*20));
        _tasks.add(as);
        BukkitTask cpd = Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, new checkOpenPlayerData(), (600*20), (600*20));
        _tasks.add(cpd);
        
        //Dummy Commands
        if(!getConfig().getBoolean("deactivate.dummy.register", false))
            getCommand("register").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.password", false))
            getCommand("password").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.ip", false))
            getCommand("ip").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.glist", false))
            getCommand("glist").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.vote", false))
            getCommand("vote").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.ban", false))
            getCommand("ban").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.tempban", false))
            getCommand("tempban").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.mute", false))
            getCommand("mute").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.tempmute", false))
            getCommand("tempmute").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.ipban", false))
            getCommand("ipban").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.iptempban", false))
            getCommand("iptempban").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.ipmute", false))
            getCommand("ipmute").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.iptempmute", false))
            getCommand("iptempmute").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.unban", false))
            getCommand("unban").setExecutor(new Dummy());
        if(!getConfig().getBoolean("deactivate.dummy.unmute", false))
            getCommand("unmute").setExecutor(new Dummy());
        
        Mu1ti1ingu41.loadExternalDefaultLanguage(this, "languages");
    }
    
    @Override
    public void onDisable() {
        for(BukkitTask task: _tasks)
            task.cancel();
        
        for(Player p: Bukkit.getOnlinePlayers()) {
            PlayerData pd = PlayerData.getPlayerData(p);
            pd.setLogOutPos(p.getLocation());
        }
        
        if(!getConfig().getBoolean("deactivate.function.rentfly", false)) {
            RFly.saveAll();
            LoadAndSave.saveRentFlyTimes(this);
        }
        
        PlayerData.saveAllPlayerDatas();
        if(!getConfig().getBoolean("deactivate.command.warp", false))
            LoadAndSave.saveWarps(this);
        if(!getConfig().getBoolean("deactivate.function.buyandsellsign", false))
            LoadAndSave.saveBuySellSigns(this);
        
        if(!getConfig().getBoolean("deactivate.function.gamestore", false)) {
            try {
                File gs = new File(ShopChest.checkPath(), "gamestore.yml");
                _gamestore.save(gs);
            } catch(Exception ex) {}
        }
    }
    
    private boolean setupEconomy() {
        try {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                _eco = economyProvider.getProvider();
            }
        } catch(NoClassDefFoundError ex) {}
        return (_eco != null);
    }
    
    public boolean isGroupManager() {
        return _isGroupManager;
    }
    
    public GroupManager getGroupManager() {
        if(_isGroupManager)
            return (GroupManager)Bukkit.getPluginManager().getPlugin("GroupManager");
        return null;
    }
    
    public boolean isLWC() {
        return _isLWC;
    }
    
    public LWCPlugin getLWC() {
        if(_isLWC)
            return (LWCPlugin)Bukkit.getPluginManager().getPlugin("LWC");
        return null;
    }
    
    public boolean isWG() {
        return _isWG;
    }
    
    public WorldGuardPlugin getWG() {
        if(_isWG)
            return (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
        return null;
    }
    
    public boolean isCitizens() {
        return _isNPC;
    }
    
    public Citizens getNPC() {
        if(_isNPC)
            return (Citizens)getServer().getPluginManager().getPlugin("Citizens");
        return null;
    }
    
    public boolean isFrameProtect() {
        return _isFP;
    }
    
    public FrameProtect getFrameProtect() {
        if(_isFP)
            return (FrameProtect)getServer().getPluginManager().getPlugin("FrameProtect");
        return null;
    }
    
    public Location getSpawnPoint() {
        return _spawnPoint;
    }
    
    public void setSpawnPoint(Location loc) {
        _spawnPoint = loc;
        if(loc != null) {
            getConfig().set("spawnpoint.world", loc.getWorld().getName());
            getConfig().set("spawnpoint.x", loc.getX());
            getConfig().set("spawnpoint.y", loc.getY());
            getConfig().set("spawnpoint.z", loc.getZ());
            getConfig().set("spawnpoint.yaw", loc.getYaw());
            getConfig().set("spawnpoint.pitch", loc.getPitch());
        } else {
            getConfig().set("spawnpoint", null);
        }
        saveConfig();
    }
    
    public Location getFirstSpawnPoint() {
        return _firstSpawnPoint;
    }
    
    public void setFirstSpawnPoint(Location loc) {
        _firstSpawnPoint = loc;
        if(loc != null) {
            getConfig().set("firstspawnpoint.world", loc.getWorld().getName());
            getConfig().set("firstspawnpoint.x", loc.getX());
            getConfig().set("firstspawnpoint.y", loc.getY());
            getConfig().set("firstspawnpoint.z", loc.getZ());
            getConfig().set("firstspawnpoint.yaw", loc.getYaw());
            getConfig().set("firstspawnpoint.pitch", loc.getPitch());
        } else {
            getConfig().set("firstspawnpoint", null);
        }
        saveConfig();
    }
    
    //Statics
    private static Customs _p = null;
    public static Customs getPlugin() {
        return _p;
    }
    
    private static Economy _eco = null;
    public static Economy getEco() {
        return _eco;
    }
    
    //LET US PAY FOR COMMAND IF IS SET
    public static boolean hasPaidForUseCommand(String cmd, Player p, int cost) {
        if(_eco == null)
            return true;
        
        if(cost <= 0)
            return true;
        
        PlayerData pd = PlayerData.getPlayerData(p);
        if((int)_eco.getBalance(p) < cost) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "cmdCost.notEnough", "You need %need% %currency% to use this command.", new String[] {"%need%", "%have%", "%currency%", "%cmd%"}, new String[] {String.valueOf(cost), String.valueOf((int)_eco.getBalance(p)),_eco.currencyNamePlural(), cmd}));
            return false;
        }
        
        _eco.withdrawPlayer(p, cost);
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "cmdCost.paid", "You were %need% %currency% for the use of %cmd% deducted.", new String[] {"%need%", "%have%", "%currency%", "%cmd%"}, new String[] {String.valueOf(cost), String.valueOf((int)_eco.getBalance(p)),_eco.currencyNamePlural(), cmd}));
        return true;
    }
    
    public static boolean hasPaidForUseCommand(String cmd, Player p) {
        if(_eco == null)
            return true;
        
        if(!Customs.getPlugin().getConfig().isInt("cmdCost." + cmd))
            return true;
        
        return hasPaidForUseCommand(cmd, p, _p.getConfig().getInt("cmdCost." + cmd));
    }
    
    public static boolean canPaidIt(Player p, int sum) {
        return canPaidIt(p, sum, false);
    }
    
    public static boolean canPaidIt(Player p, int sum, boolean payIt) {
        if(_eco == null)
            return true;
        
        if(sum <= 0)
            return true;
        
        if(!payIt)
            return ((int)_eco.getBalance(p) >= sum);
        
        if((int)_eco.getBalance(p) < sum)
            return false;
        
        _eco.withdrawPlayer(p, sum);
        return true;
    }
    
    public static boolean givePlayerMoney(Player p, int sum) {
        if(_eco == null)
            return false;
        
        _eco.depositPlayer(p, sum);
        return true;
    }
    
    public static String currencyNamePlural() {
        if(_eco == null)
            return "";
        return _eco.currencyNamePlural();
    }
    
    //AVAILABLE LANGUAGES
    private final static HashMap<String, FileConfiguration> _languages = new HashMap<>();
    public static void setLanguage(String name, FileConfiguration fc) {
        if(_p.getConfig().isString("defaultLanguage")) {
            if(_p.getConfig().getString("defaultLanguage").equalsIgnoreCase(name)) {
                _languages.put("default", fc);
                return;
            }
        }
        _languages.put(name.toLowerCase(), fc);
    }
    
    public static FileConfiguration getLanguage(String name) {
        name = name.toLowerCase();
        if(_p.getConfig().isString("customLanguages." + name))
            name = _p.getConfig().getString("customLanguages." + name);
        
        if(_languages.containsKey(name))
            return _languages.get(name);
        
        if(_languages.containsKey("default"))
            return _languages.get("default");
        return null;
    }
    
    public static void clearLanguages() {
        _languages.clear();
    }
    
    
    //BLOCK COMMMANDS IN GIVEN WORLD NAMES
    private final static HashMap<String, ArrayList<String>> _blockedWorldsByCommand = new HashMap<>();
    public static void setBlockedWorldByCommand(String cmd, String world) {
        if(_blockedWorldsByCommand.containsKey(cmd)) {
            _blockedWorldsByCommand.get(cmd.toLowerCase()).add(world.toLowerCase());
        } else {
            ArrayList<String> arr = new ArrayList<>();
            arr.add(world.toLowerCase());
            _blockedWorldsByCommand.put(cmd.toLowerCase(), arr);
        }
    }
    
    public static boolean isBlockedWorldbyCommand(String cmd, Player p, String world) {
        if(!_blockedWorldsByCommand.containsKey(cmd.toLowerCase()))
            return false;
        if(!_blockedWorldsByCommand.get(cmd.toLowerCase()).contains(world.toLowerCase()))
            return false;
        PlayerData pd = PlayerData.getPlayerData(p);
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.blockedworld.use", "You can't use the Command " + cmd + " "));
        return true;
    }
    
    public static boolean isBlockedWorldbyCommand(String cmd, String world) {
        if(!_blockedWorldsByCommand.containsKey(cmd.toLowerCase()))
            return false;
        if(!_blockedWorldsByCommand.get(cmd.toLowerCase()).contains(world.toLowerCase()))
            return false;
        return true;
    }

    //ALL ABOUT COOLDOWN TIME OF COMMANDS
    public static boolean canCmdUseByPlayer(Player p, String cmd, long cd, String pdSt) {
        if(p.hasPermission("customs.use." + cmd.toLowerCase() + ".bypass"))
            return true;
        
        PlayerData pd = PlayerData.getPlayerData(p);
        if(!pd.isTimeStamp(pdSt))
            return true;
        
        long diff = getRestCoolDownTime(cd, pd.getTimeStamp(pdSt));
        if(diff < 1000)
            return true;
        
        String tStr = calculateTimeToString(diff, p);
        if(tStr.isEmpty())
            return true;
        
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.cmdCoolDown.wait", "You can use this command in " + tStr + " again.", new String[] {"%time%", "%cmd%"}, new String[] {tStr, cmd}));
        return false;
    }
    
    public static boolean canCmdUseByPlayer(Player p, String cmd) {
        if(!_p.getConfig().isInt("cmdCoolDown." + cmd))
            return true;
        
        return canCmdUseByPlayer(p, cmd, (long)(_p.getConfig().getInt("cmdCoolDown." + cmd)*1000), cmd);
    }

    public static long getRestCoolDownTime(long cd, long lastUse) {
        if((cd+lastUse) > System.currentTimeMillis())
            return ((cd+lastUse)-System.currentTimeMillis());
        
        return 0;
    }

    public static String calculateTimeToString(long msec, Player p) {
        PlayerData pd = PlayerData.getPlayerData(p);
        long sec = msec/1000;

        if(sec <= 0)
            return "";

        String d = "";
        int day = (int)Math.ceil((double)(sec/(60*60*24)));
        if(day >= 1) {
            sec = sec-(day*(60*60*24));
            if(day == 1)
                d += day + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.day", "Day");
            else
                d += day + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.days", "Days");
        }
        
        int hour = (int)Math.ceil((double)(sec/(60*60)));
        if(hour >= 1) {
            sec = sec-(hour*(60*60));
            if(!d.isEmpty())
                d += (sec > 0)?Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.sepa", ", "):Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.and", " and ");
            if(hour == 1)
                d += hour + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.hour", "Hour");
            else
                d += hour + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.hours", "Hours");
        }
        
        int min = (int)Math.ceil((double)(sec/60));
        if(min >= 1) {
            sec = sec-(min*60);
            if(!d.isEmpty())
                d += (sec > 0)?Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.sepa", ", "):Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.and", " and ");
            if(min == 1)
                d += min + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.minute", "Minute");
            else
                d += min + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.minutes", "Minutes");
        }
        
        if(sec > 0) {
            if(!d.isEmpty())
                d += Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.and", " and ");
            if(sec == 1)
                d += sec + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.second", "Second");
            else
                d += sec + " " + Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.time.seconds", "Seconds");
        }
        return d;
    }
    
    private final static HashMap<String, Location> _warps = new HashMap<>();
    public static boolean isWarp(String name) {
        return _warps.containsKey(name);
    }
    
    public static void setWarp(String name, Location loc) {
        _warps.put(name, loc);
    }
    
    public static void removeWarp(String name) {
        _warps.remove(name);
    }
    
    public static Location getWarp(String name) {
        if(!isWarp(name))
            return null;
        return _warps.get(name);
    }
    
    public static HashMap<String, Location> getWarps() {
        return _warps;
    }
    
    //All about Kits
    private final static HashMap<String, KitTool> _kits = new HashMap<>();
    public static boolean isKit(String name) {
        return _kits.containsKey(name);
    }
    
    public static KitTool getKit(String name) {
        if(isKit(name))
            return _kits.get(name);
        return null;
    }
    
    public static void addKit(String name, KitTool kit) {
        _kits.put(name, kit);
    }
    
    public static void removeKit(String name) {
        if(isKit(name))
            _kits.remove(name);
    }
    
    public static HashMap<String, KitTool> getKits() {
        return _kits;
    }
    
    public static void setKit(String name, KitTool kt) {
        _kits.put(name, kt);
    }
    
    //Regions Owned ,by World
    public final static HashMap<String, HashMap<String, Boolean>> _regions = new HashMap<>();
    public static void setRegion(String w, String r, boolean owned) {
        if(!_regions.containsKey(w.toLowerCase()))
            _regions.put(w.toLowerCase(), new HashMap<String, Boolean>());
        
        _regions.get(w.toLowerCase()).put(r, owned);
    }
    
    public static boolean isRegionListed(String w, String r) {
        if(!_regions.containsKey(w.toLowerCase()))
            return false;
        return _regions.get(w.toLowerCase()).containsKey(r);
    }
    
    public static boolean isRegionOwned(String w, String r) {
        if(!isRegionListed(w.toLowerCase(), r))
            return true;
        return _regions.get(w.toLowerCase()).get(r);
    }
    
    public static ArrayList<String> getRegionsStartsWith(String w, String sw) {
        if(!_regions.containsKey(w.toLowerCase()))
            return new ArrayList<>();
        
        ArrayList<String> regio = new ArrayList<>();
        for(Map.Entry<String, Boolean> e: _regions.get(w.toLowerCase()).entrySet()) {
            if(e.getKey().startsWith(sw) && !e.getValue()) {
                regio.add(e.getKey());
            }
        }
        return regio;
    }
    
    //BookData
    private final static HashMap<String, BookData> _bookDatas = new HashMap<>();
    public static HashMap<String, BookData> getBooks() {
        return _bookDatas;
    }
    
    public static void setBook(String name, BookData bm) {
        _bookDatas.put(name, bm);
    }
    
    public static BookData getBook(String name) {
        return _bookDatas.containsKey(name)?_bookDatas.get(name):null;
    }

    //Color and Format
    public static String setColors(Player p, String perm, String msg) {
        boolean hasPerm = p.hasPermission(perm);
        for(ChatColor cc: ChatColor.values()) {
            if(cc.isColor()) {
                String strcc = hasPerm?"" + cc:"";
                msg = msg.replace("&" + cc.getChar(), strcc);
            }
        }
        return msg;
    }
    
    public static String setFormat(Player p, String perm, String msg) {
        boolean hasPerm = p.hasPermission(perm);
        for(ChatColor cc: ChatColor.values()) {
            if(cc.isFormat()) {
                String strcc = hasPerm?"" + cc:"";
                msg = msg.replace("&" + cc.getChar(), strcc);
            }
        }
        return msg;
    }
    
    //KillStats
    private final static HashMap<String, KillStats> _killStats = new HashMap<>();
    public static KillStats getKillStatsByPlayer(String pName) {
        return _killStats.containsKey(pName)?_killStats.get(pName):null;
    }
    
    public static void setKillStats(String pName, KillStats ks) {
        _killStats.put(pName, ks);
    }
    
    public static ArrayList<Location> _furnaces = new ArrayList<>();
    public static ArrayList<Location> getFurnaces() {
        return _furnaces;
    }
    
    public static boolean isFurnace(Location loc) {
        for(Location l: _furnaces) {
            if(!l.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                continue;
            
            if(l.getBlockX() == loc.getBlockX() && l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ())
                return true;
        }
        return false;
    }
    
    public static void addFurnace(Location loc) {
        _furnaces.add(loc);
    }
    
    public static void delFurnace(Location loc) {
        _furnaces.remove(loc);
    }
    
    private static Database _db;
    public static Database getMySQL() {
        return _db;
    }
    
    private static YamlConfiguration _gamestore;
    public static YamlConfiguration getGameStore() {
        return _gamestore;
    }
    
    private static final ArrayList<UUID> _hidePlayers = new ArrayList<>();
    public static boolean isHidePlayer(Player p) {
        return _hidePlayers.contains(p.getUniqueId());
    }
    public static void setHidePlayer(Player p) {
        _hidePlayers.add(p.getUniqueId());
        for(Player pl: Bukkit.getOnlinePlayers()) {
            if(!pl.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString()))
                pl.hidePlayer(p);
        }
    }
    
    public static void removeHidePlayer(Player p) {
        _hidePlayers.remove(p.getUniqueId());
        for(Player pl: Bukkit.getOnlinePlayers()) {
            if(!pl.getUniqueId().toString().equalsIgnoreCase(p.getUniqueId().toString()))
                pl.showPlayer(p);
        }
    }
    
    public static void setHidenPlayers(Player p) {
        for(UUID uuid: _hidePlayers) {
            Player pl = Bukkit.getPlayer(uuid);
            if(pl != null)
                p.hidePlayer(pl);
        }
    }
    
    public static void sendPluginMessage(Player p, String type, Object vari) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MyBungee");
        out.writeUTF(type);
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF(String.valueOf(vari));
        p.sendPluginMessage(_p, "BungeeCord", out.toByteArray());
    }
    
    private final static ArrayList<String> _errors = new ArrayList<>();
    public static ArrayList<String> getErrors() {
        return _errors;
    }
}