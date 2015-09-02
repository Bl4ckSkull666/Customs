/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.listeners.ShopChest.removePermissionFromPlayer;
import de.bl4ckskull666.customs.listeners.ShopChest.removeSpecialGroup;
import de.bl4ckskull666.uuiddatabase.UUIDDatabase;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Pappi
 */
public final class PlayerData {
    private final String _name;
    private final UUID _uuid;
    private final HashMap<String, Location> _homes = new HashMap<>();
    private Location _lastPos = null;
    private Location _logOutPos = null;
    private final HashMap<String, Long> _timestamps = new HashMap<>();
    private String _language = "";
    private int _bDay = -1;
    private int _bMonth = -1;
    private int _bYear = -1;
    private int _myAge = -1;
    private String _gender = "";
    private final HashMap<String, Long> _achievments = new HashMap<>();
    private final ArrayList<String> _holdingItems = new ArrayList<>();
    private boolean _expDrop = false;
    private int _limit_home = 0;
    private final HashMap<String, Integer> _limit_plot = new HashMap<>();
    private boolean _isVerify = false;
    private String _special_group = "";
    private Calendar _special_group_end = null;
    private BukkitTask _special_group_task = null;
    private final HashMap<String, Calendar> _permission_times = new HashMap<>();
    private BukkitTask _permission_next_end = null;
    private long _lastUse = 0L;
    
    public PlayerData(String name) {
        File folder = new File(Customs.getPlugin().getConfig().getString("user-data-save-path", Customs.getPlugin().getName() + "/users"));
        if(!folder.exists())
            folder.mkdirs();
        
        if(name.length() <= 32) {
            _name = name;
            UUID uuid = Utils.getUUIDByOfflinePlayer(_name);
            if(uuid == null)
                _uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
            else
                _uuid = uuid;
        } else {
            name = name.replace("-", "");
            name = name.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
            _uuid = UUID.fromString(name);
            _name = Utils.getNameByOfflinePlayer(name);
        }
        File uFile = new File(folder, _uuid.toString() + ".yml");
        File uNFile = new File(folder, name + ".yml");
        File uNLFile = new File(folder, name.toLowerCase() + ".yml");
        if(uFile.exists()) {
            setPlayerDatas(uFile);
        } else if(uNFile.exists()) {
            setPlayerDatas(uNFile);
            if(!_uuid.toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000")) {
                savePlayerData();
                uNFile.delete();
            }
        } else if(uNLFile.exists()) {
            setPlayerDatas(uNLFile);
            if(!_uuid.toString().equalsIgnoreCase("00000000-0000-0000-0000-000000000000")) {
                savePlayerData();
                uNLFile.delete();
            }
        }
        PlayerData pd = this;
        _players.put(_uuid.toString(), pd);
    }
    
    public PlayerData(Player p) {
        File folder = new File(Customs.getPlugin().getConfig().getString("user-data-save-path", Customs.getPlugin().getName() + "/users"));
        if(!folder.exists())
            folder.mkdirs();
        
        _name = p.getName();
        _uuid = p.getUniqueId();
        
        File uFile = new File(folder, p.getUniqueId().toString() + ".yml");
        File uNFile = new File(folder, p.getName() + ".yml");
        File uNLFile = new File(folder, p.getName().toLowerCase() + ".yml");
        if(uFile.exists()) {
            setPlayerDatas(uFile);
        } else if(uNFile.exists()) {
            setPlayerDatas(uNFile);
            savePlayerData();
            uNFile.delete();
        } else if(uNLFile.exists()) {
            setPlayerDatas(uNLFile);
            savePlayerData();
            uNLFile.delete();
        }
        PlayerData pd = this;
        _players.put(p.getUniqueId().toString(), pd);
    }
    
    private void setPlayerDatas(File f) {
        FileConfiguration u = YamlConfiguration.loadConfiguration(f);
        String mainString = u.isConfigurationSection(Customs.getPlugin().getConfig().getString("server-name", "default"))?Customs.getPlugin().getConfig().getString("server-name", "default") + ".":"";
        if(u.isConfigurationSection(mainString + "homes")) {
            for(String name : u.getConfigurationSection(mainString + "homes").getKeys(false)) {
                if(!u.isString(mainString + "homes." + name + ".world") ||
                        !u.isDouble(mainString + "homes." + name + ".x") ||
                        !u.isDouble(mainString + "homes." + name + ".y") ||
                        !u.isDouble(mainString + "homes." + name + ".z") ||
                        !u.isDouble(mainString + "homes." + name + ".yaw") ||
                        !u.isDouble(mainString + "homes." + name + ".pitch")) {
                    continue;
                }
                
                if(Bukkit.getWorld(u.getString(mainString + "homes." + name + ".world")) == null)
                    continue;
                
                Location loc = new Location(Bukkit.getWorld(u.getString(mainString + "homes." + name + ".world")),
                        u.getDouble(mainString + "homes." + name + ".x"),
                        u.getDouble(mainString + "homes." + name + ".y"),
                        u.getDouble(mainString + "homes." + name + ".z"),
                        (float)u.getDouble(mainString + "homes." + name + ".yaw"),
                        (float)u.getDouble(mainString + "homes." + name + ".pitch"));
                
                _homes.put(name, loc);
            }
        }
        
        if(u.isConfigurationSection(mainString + "lastlocation")) {
            if(Bukkit.getWorld(u.getString(mainString + "lastlocation.world")) != null) {
                _lastPos = new Location(Bukkit.getWorld(u.getString(mainString + "lastlocation.world")),
                    u.getDouble(mainString + "lastlocation.x"),
                    u.getDouble(mainString + "lastlocation.y"),
                    u.getDouble(mainString + "lastlocation.z"),
                    (float)u.getDouble(mainString + "lastlocation.yaw"),
                    (float)u.getDouble(mainString + "lastlocation.pitch"));
            }
        }
        
        if(u.isConfigurationSection(mainString + "logoutlocation")) {
            if(Bukkit.getWorld(u.getString(mainString + "logoutlocation.world")) != null) {
                _logOutPos = new Location(Bukkit.getWorld(u.getString(mainString + "logoutlocation.world")),
                    u.getDouble(mainString + "logoutlocation.x"),
                    u.getDouble(mainString + "logoutlocation.y"),
                    u.getDouble(mainString + "logoutlocation.z"),
                    (float)u.getDouble(mainString + "logoutlocation.yaw"),
                    (float)u.getDouble(mainString + "logoutlocation.pitch"));
            }
        }
        
        if(u.isConfigurationSection(mainString + "timestamps")) {
            for(String name : u.getConfigurationSection(mainString + "timestamps").getKeys(false)) {
                if(u.isConfigurationSection(mainString + "timestamps." + name)) {
                    for(String na : u.getConfigurationSection(mainString + "timestamps." + name).getKeys(false)) {
                        if(u.isLong(mainString + "timestamps." + name + "." + na)) {
                            _timestamps.put(name + "_" + na, u.getLong(mainString + "timestamps." + name + "." + na));
                        }
                    }
                } else {
                    if(u.isLong(mainString + "timestamps." + name))
                        _timestamps.put(name, u.getLong(mainString + "timestamps." + name));
                }
            }
        }
        
        if(u.isList(mainString + "achievments")) {
            for(String str: u.getStringList(mainString + "achievments")) {
                _achievments.put(str, -1L);
            }
        } else if(u.isConfigurationSection(mainString + "achievments")) {
            for(String str: u.getConfigurationSection(mainString + "achievments").getKeys(false)) {
                _achievments.put(str, u.getLong(mainString + "achievments." + str, 0));
            }
        }
        
        if(u.isList(mainString + "holdingItems")) {
            for(String str: u.getStringList(mainString + "holdingItems")) {
                _holdingItems.add(str);
            }
        }
        
        _bDay = u.getInt("birth.day", -1);
        _bMonth = u.getInt("birth.month", -1);
        _bYear = u.getInt("birth.year", -1);
        setAge();
        _gender = u.getString("gender", "none");
        _limit_home = u.getInt(mainString + "limits.home", 0);
        
        if(u.isConfigurationSection(mainString + "limits.plot")) {
            for(String k: u.getConfigurationSection(mainString + "limits.plot").getKeys(false))
                _limit_plot.put(k, u.getInt(mainString + "limits.plot." + k));
        }
        for(World w: Bukkit.getWorlds()) {
            if(!_limit_plot.containsKey(w.getName()))
                _limit_plot.put(w.getName().toLowerCase(), 0);
        }
        
        _isVerify = u.getBoolean("verifycated", false);
        
        if(u.isConfigurationSection("special_group")) {
            _special_group = u.getString("special_group.group", "");
            String[] t1 = u.getString("special_group.end", "unlimit").split(" ");
            if(t1.length == 2) {
                String[] t2 = t1[0].split("\\.");
                String[] t3 = t1[1].split(":");
                if(t2.length == 3 && t3.length == 3) {
                    Calendar cal = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();
                    now.setTimeInMillis(System.currentTimeMillis());
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt((t2[0].length() == 2 && t2[0].startsWith("0")?t2[0].substring(1, 2):t2[0])));
                    cal.set(Calendar.MONTH, Integer.parseInt((t2[1].length() == 2 && t2[1].startsWith("0")?t2[1].substring(1, 2):t2[1]))-1);
                    cal.set(Calendar.YEAR, Integer.parseInt(t2[2]));
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((t3[0].length() == 2 && t3[0].startsWith("0")?t3[0].substring(1, 2):t3[0])));
                    cal.set(Calendar.MINUTE, Integer.parseInt((t3[1].length() == 2 && t3[1].startsWith("0")?t3[1].substring(1, 2):t3[1])));
                    cal.set(Calendar.SECOND, Integer.parseInt((t3[2].length() == 2 && t3[2].startsWith("0")?t3[2].substring(1, 2):t3[2])));
                    if(cal.before(now))
                        setSpecialGroupCal("", null);
                    else
                        setSpecialGroupCal(_special_group, cal);
                }
            } else {
                setSpecialGroupCal(_special_group, null);
            }
        } else
            setSpecialGroupCal("", null);
        
        if(u.isConfigurationSection("permissions")) {
            for(String k: u.getConfigurationSection("permissions").getKeys(false)) {
                String[] t1 = u.getString("permissions." + k).split(" ");
                if(t1.length == 2) {
                    String[] t2 = t1[0].split("\\.");
                    String[] t3 = t1[1].split(":");
                    if(t2.length == 3 && t3.length == 3) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt((t2[0].length() == 2 && t2[0].startsWith("0")?t2[0].substring(1, 2):t2[0])));
                        cal.set(Calendar.MONTH, Integer.parseInt((t2[1].length() == 2 && t2[1].startsWith("0")?t2[1].substring(1, 2):t2[1]))-1);
                        cal.set(Calendar.YEAR, Integer.parseInt(t2[2]));
                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt((t3[0].length() == 2 && t3[0].startsWith("0")?t3[0].substring(1, 2):t3[0])));
                        cal.set(Calendar.MINUTE, Integer.parseInt((t3[1].length() == 2 && t3[1].startsWith("0")?t3[1].substring(1, 2):t3[1])));
                        cal.set(Calendar.SECOND, Integer.parseInt((t3[2].length() == 2 && t3[2].startsWith("0")?t3[2].substring(1, 2):t3[2])));
                        setPermCalendar(k.replace("-", "."), cal);
                    }
                } else
                    setPermCalendar(k.replace("-", "."), null);
            }
        }
    }
    
    public final void savePlayerData() {
        File folder = new File(Customs.getPlugin().getConfig().getString("user-data-save-path", Customs.getPlugin().getName() + "/users"));
        if(!folder.exists())
            folder.mkdirs();
        
        File f = new File(folder, _uuid.toString() + ".yml");
        
        String main = Customs.getPlugin().getConfig().getString("server-name", "default") + ".";
        
        FileConfiguration u = YamlConfiguration.loadConfiguration(f);
        u.set("uuid", _uuid.toString());
        u.set("lastNickname", _name);
        u.set("birth", null);
        u.set("gender", null);
        
        //clear Old Datas
        u.set("homes", null);
        u.set("lastlocation", null);
        u.set("logoutlocation", null);
        u.set("timestamps", null);
        u.set("homes", null);
        
        //clear New Datas
        u.set(main + "homes", null);
        u.set(main + "lastlocation", null);
        u.set(main + "logoutlocation", null);
        u.set(main + "timestamps", null);
        u.set(main + "homes", null);
        
        if(_homes.size() > 0) {
            for(Map.Entry<String, Location> e : _homes.entrySet()) {
                u.set(main + "homes." + e.getKey() + ".world", e.getValue().getWorld().getName());
                u.set(main + "homes." + e.getKey() + ".x", e.getValue().getX());
                u.set(main + "homes." + e.getKey() + ".y", e.getValue().getY());
                u.set(main + "homes." + e.getKey() + ".z", e.getValue().getZ());
                u.set(main + "homes." + e.getKey() + ".yaw", e.getValue().getYaw());
                u.set(main + "homes." + e.getKey() + ".pitch", e.getValue().getPitch());
            }
        }
        
        if(_lastPos != null) {
            u.set(main + "lastlocation.world",_lastPos.getWorld().getName());
            u.set(main + "lastlocation.x", _lastPos.getX());
            u.set(main + "lastlocation.y", _lastPos.getY());
            u.set(main + "lastlocation.z", _lastPos.getZ());
            u.set(main + "lastlocation.yaw", _lastPos.getYaw());
            u.set(main + "lastlocation.pitch", _lastPos.getPitch());
        }
        
        if(_logOutPos != null) {
            u.set(main + "logoutlocation.world", _logOutPos.getWorld().getName());
            u.set(main + "logoutlocation.x", _logOutPos.getX());
            u.set(main + "logoutlocation.y", _logOutPos.getY());
            u.set(main + "logoutlocation.z", _logOutPos.getZ());
            u.set(main + "logoutlocation.yaw", _logOutPos.getYaw());
            u.set(main + "logoutlocation.pitch", _logOutPos.getPitch());
        }
        
        if(_timestamps.size() > 0) {
            for(Map.Entry<String, Long> e : _timestamps.entrySet()) {
                if(e.getKey().matches("_")) {
                    String tst = "";
                    for(String str: e.getKey().split("_"))
                        tst += "." + str;
                    u.set(main + "timestamps" + tst, e.getValue());
                } else
                    u.set(main + "timestamps." + e.getKey(), e.getValue());
            }
        }
        u.set("birth.day", _bDay);
        u.set("birth.month", _bMonth);
        u.set("birth.year", _bYear);
        u.set("gender", _gender);
        
        if(_achievments.size() > 0) {
            for(Map.Entry<String, Long> ent: _achievments.entrySet()) {
                u.set(main + "achievments." + ent.getKey(), ent.getValue());
            }
        }
        if(_holdingItems.size() > 0)
            u.set(main + "holdingItems", (List<String>)_holdingItems);
        u.set(main + "limits.home", _limit_home);
        for(Map.Entry<String, Integer> e: _limit_plot.entrySet()) {
            if(e.getValue() > 0)
                u.set(main + "limits.plot." + e.getKey(), e.getValue());
        }
        
        if(_isVerify)
            u.set("verifycated", true);
        
        if(!_special_group.isEmpty()) {
            u.set("special_group.group", _special_group);
            if(_special_group_end != null)
                u.set("special_group.end", Utils.getDateByCalendar(_special_group_end) + " " + Utils.getTimeByCalendar(_special_group_end));
            else
                u.set("special_group.end", null);
        } else
            u.set("special_group", null);
        
        if(_permission_times.size() > 0) {
            for(Map.Entry<String, Calendar> e: _permission_times.entrySet()) {
                if(e.getValue() == null)
                    u.set("permissions." + e.getKey().replace(".", "-"), "");
                else
                    u.set("permissions." + e.getKey().replace(".", "-"), Utils.getDateByCalendar(e.getValue()) + " " + Utils.getTimeByCalendar(e.getValue()));
            }
        } else
            u.set("permissions", null);
        
        try {
            u.save(f);
        } catch(IOException ex) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Konnte Userdatei von " + _name + " nicht speichern,", ex);
        }
    }
    
    public String getName() {
        return _name;
    }
    
    public String getUUID() {
        return _uuid.toString();
    }
    
    private void setAge() {
        if(_bYear > -1) {
            Calendar cal = new GregorianCalendar();
            _myAge = cal.get(Calendar.YEAR)-_bYear;
            if(_bMonth > -1 && (cal.get(Calendar.MONTH)+1) <= _bMonth) {
                _myAge--;
                if(_bDay > -1 && cal.get(Calendar.DAY_OF_MONTH) >= _bDay && (cal.get(Calendar.MONTH)+1) == _bMonth)
                    _myAge++;
            }
        }
    }
    
    public boolean isHome(String home) {
        return _homes.containsKey(home);
    }
    
    public int getHomeCount() {
        return (_homes.size()-(_homes.containsKey("bed")?1:0));
    }
    
    public Location getHome(String home) {
        if(!isHome(home))
            return null;
        return _homes.get(home);
    }
    
    public String[] getHomes(boolean withBed) {
        String[] home = new String[_homes.size()];
        int i = 0;
        for(Map.Entry<String, Location> e : _homes.entrySet()) {
            if(!withBed && !e.getKey().equalsIgnoreCase("bed") || withBed) {
                home[i] = e.getKey();
                i++;
            }
        }
        return home;
    }
    
    public void setHome(String home, Location loc) {
        _homes.put(home, loc);
    }
    
    public void delHome(String home) {
        _homes.remove(home);
    }
    
    public Location getLastPos() {
        return _lastPos;
    }
    
    public void setLastPos(Location loc) {
        _lastPos = loc;
    }
    
    public Location getLogOutPos() {
        return _logOutPos;
    }
    
    public void setLogOutPos(Location loc) {
        _logOutPos = loc;
    }
    
    public boolean isTimeStamp(String t) {
        return _timestamps.containsKey(t);
    }
    
    public long getTimeStamp(String t) {
        if(!isTimeStamp(t))
            return 0L;
        return _timestamps.get(t);
    }
    
    public void setTimeStamp(String key, long val) {
        _timestamps.put(key, val);
    }
    
    public String getLanguage() {
        return _language;
    }
    
    public void setLanguage(String lang) {
        _language = lang.toLowerCase();
    }
    
    public int getAge() {
        return _myAge;
    }
    
    public void setBirthday(int d, int m, int y) {
        if(d > 0 && d < 32)
            _bDay = d;
        if(m > 0 && m < 13)
            _bMonth = m;
        Calendar cal = new GregorianCalendar();
        if(y > 1920 && y < cal.get(Calendar.YEAR)-5)
            _bYear = y;
        
        setAge();
    }
    
    public String getBirthday() {
        return _bDay + "." + _bMonth + "." + _bYear;
    }
    
    public void setGender(String gender) {
        _gender = gender;
    }
    
    public String getGender() {
        return _gender;
    }
    
    public void setAchievment(String achievment) {
        _achievments.put(achievment, System.currentTimeMillis());
    }
    
    public void addAchievment(String achievment, long tstamp) {
        _achievments.put(achievment, tstamp);
    }
    
    public void delAchievment(String achievment) {
        _achievments.remove(achievment);
    }
    
    public boolean isAchievment(String achievment) {
        return _achievments.containsKey(achievment);
    }
    
    public HashMap<String, Long> getAchievments() {
        return _achievments;
    }
    
    public void setHoldingItems(String item) {
        _holdingItems.add(item);
    }
    
    public boolean isHoldingItems(String item) {
        return _holdingItems.contains(item);
    }
    
    public ArrayList<String> getHoldingItems() {
        return _holdingItems;
    }
    
    public boolean activExpDrop() {
        return _expDrop;
    }
    
    public void setExpDrop(boolean bol) {
        _expDrop = bol;
    }
    
    public void addHomeLimit() {
        _limit_home++;
    }
    
    public int getHomeLimit() {
        return _limit_home;
    }
    
    public void delHomeLimit() {
        _limit_home--;
    }
    
    public void addPlotLimit(String w) {
        _limit_plot.put(w, _limit_plot.get(w)+1);
    }
    
    public int getPlotLimit(String w) {
        return _limit_plot.containsKey(w)?_limit_plot.get(w):0;
    }
    
    public void delPlotLimit(String w) {
        _limit_plot.put(w, _limit_plot.get(w)-1);
    }
    
    public boolean getVerify() {
        return _isVerify;
    }
    
    public void setVerify(boolean is) {
        _isVerify = is;
    }
    
    public String getSpecialGroup() {
        return _special_group;
    }
    
    public Calendar getSpecialGroupCal() {
        return _special_group_end;
    }
    
    public void setSpecialGroupCal(String group, Calendar cal) {
        if(_special_group_task != null)
            _special_group_task.cancel();
        
        if(!group.isEmpty()) {
            Group g = Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getGroup(group);
            Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getUser(_name).setGroup(g);
            _special_group = group;
            _special_group_end = cal;
            if(_special_group_end != null)
                _special_group_task = Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new removeSpecialGroup(_name), ((_special_group_end.getTimeInMillis()-System.currentTimeMillis())/1000)*20);
        } else {
            Group g = Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getDefaultGroup();
            Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getUser(_name).setGroup(g);
            _special_group = "";
            _special_group_end = null;
        }
    }

    public void removePermission(String perm) {
        _permission_times.remove(perm);
        checkNextEndingPermission();
    }
    
    public Map<String, Calendar> getPerms() {
        return _permission_times;
    }
    
    public Calendar getPermCalendar(String perm) {
        return _permission_times.containsKey(perm)?_permission_times.get(perm):null;
    }
    
    public void setPermCalendar(String perm, Calendar cal) {
        if(!perm.isEmpty()) {
            _permission_times.remove(perm);
            _permission_times.put(perm, cal);

            OverloadedWorldHolder owh = Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld();
            owh.getUser(_name).addPermission(perm);
            owh.getUser(_name).sortPermissions();
        }
        checkNextEndingPermission();
    }
    
    private void checkNextEndingPermission() {
        if(_permission_next_end != null)
            _permission_next_end.cancel();
        
        Calendar temp = null;
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        String temp_perm = "";
        for(Map.Entry<String, Calendar> m: _permission_times.entrySet()) {
            if(m.getValue() == null)
                continue;
            
            if(m.getValue().before(now)) {
                _permission_next_end = Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new removePermissionFromPlayer(_name, m.getKey()), 10L);
                return;
            }
            
            if(temp == null || temp.after(m.getValue())) {
                temp = m.getValue();
                temp_perm = m.getKey();
            }
        }
        
        if(temp != null)
            _permission_next_end = Bukkit.getScheduler().runTaskLater(Customs.getPlugin(), new removePermissionFromPlayer(_name, temp_perm), ((temp.getTimeInMillis()-System.currentTimeMillis())/1000)*20);
    }
    
    public long getLastUse() {
        return _lastUse;
    }
    
    public void setLastUse() {
        _lastUse = System.currentTimeMillis();
    }
    
    //statics
    private static final HashMap<String, PlayerData> _players = new HashMap<>();
    public static PlayerData getPlayerData(Player p) {
        if(_players.containsKey(p.getUniqueId().toString())) {
            PlayerData pd = _players.get(p.getUniqueId().toString());
            pd.setLastUse();
            return pd;
        }
        return new PlayerData(p);
    }
    
    public static PlayerData getPlayerData(String p) {
        String uuid = UUIDDatabase.getUUIDByName(p);
        if(_players.containsKey(uuid)) {
            PlayerData pd = _players.get(uuid);
            pd.setLastUse();
            return pd;
        }
        return new PlayerData(p);
    }
    
    public static void saveAllPlayerDatas() {
        for(Map.Entry<String, PlayerData> e : _players.entrySet()) {
            e.getValue().savePlayerData();
        }
    }
    
    public static void removePlayerData(String uuid) {
        _players.remove(uuid);
    }
    
    public static void checkOpenPlayerDatas() {
        HashMap<String, PlayerData> temp = (HashMap<String, PlayerData>)_players.clone();
        for(Map.Entry<String, PlayerData> me: temp.entrySet()) {
            if((System.currentTimeMillis()-me.getValue().getLastUse()) > 600000) {
                me.getValue().savePlayerData();
                _players.remove(me.getKey());
            }
        }
    }
    
    public static HashMap<String, PlayerData> getOpenPlayerDatas() {
        return _players;
    }
    
    public static class afkChecker implements Runnable {
        @Override
        public void run() {
            
        }
    }
}
