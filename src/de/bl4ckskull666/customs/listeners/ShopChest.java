/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.customs.utils.GameShop;
import de.bl4ckskull666.customs.utils.Items;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Rnd;
import de.bl4ckskull666.customs.utils.Utils;
import de.bl4ckskull666.customs.utils.invserialization.InventorySerialization;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import org.anjocaido.groupmanager.data.Group;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author PapaHarni
 */
public class ShopChest implements Listener {
    
    private String[] _names = {"ShoppingCart", "GameStore", "CashPoint", "Kasse", "Einkaufswagen"}; 
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player p = (Player)e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        
        Location l = getLocation(e.getInventory().getHolder());
        if(l == null)
            return;
        
        if(!isSignOnChest(l))
            return;
        
        Inventory inv = Bukkit.createInventory(null, 54, "§2" + _names[Rnd.get(0, _names.length-1)]);
        getInventory(p, inv);
        
        if(inv.getSize() == 0) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.gamestore.emptychest", "You have no Items in your GameStore Chest."));
            e.setCancelled(true);
            return;
        }
        
        p.openInventory(inv);
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent e) {
        if(!isName(ChatColor.stripColor(e.getInventory().getName())) && 
                !isName(ChatColor.stripColor(e.getInventory().getTitle())))
            return;
        
        if(e.getInventory().getSize() > 0)
            Customs.getGameStore().set(e.getPlayer().getUniqueId().toString(), InventorySerialization.serializeInventoryAsString(e.getInventory()));
        else
            Customs.getGameStore().set(e.getPlayer().getUniqueId().toString(), null);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(!e.hasItem())
            return;
        
        ItemStack item = e.getItem();
        if(item.getType() != Material.WATCH)
            return;
        
        if(!item.hasItemMeta())
            return;
        
        if(!item.getItemMeta().hasDisplayName() || !item.getItemMeta().hasLore())
            return;
        
        if(!ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Permission Clock") && !ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("Group Clock"))
            return;
        
        if(item.getItemMeta().getLore().size() < 4)
            return;
        
        PlayerData pd = PlayerData.getPlayerData(p);
        String[] name = ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ");
        switch(name[0].toLowerCase()) {
            case "group":
                String group = ChatColor.stripColor(item.getItemMeta().getLore().get(2));
                String group_time = ChatColor.stripColor(item.getItemMeta().getLore().get(3));
                Group g = null;
                Customs.getPlugin().getLogger().log(Level.INFO, "{0} use group clock.", p.getName());
                for(Map.Entry<String, Group> me: Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getGroups().entrySet()) {
                    if(group.equalsIgnoreCase(me.getKey()))
                        g = me.getValue();
                }
                
                if(g != null) {
                    Calendar cal = pd.getSpecialGroupCal();
                    Customs.getPlugin().getLogger().log(Level.INFO, "Player {0} has rent Group {1}", new Object[]{p.getName(), g.getName()});
                    if(!group_time.equalsIgnoreCase("unlimit")) {
                        if(cal == null)
                            cal = Calendar.getInstance();
                        Utils.addTimeToCalendar(group_time.split(","), cal);
                    } else
                        cal = null;
                    pd.setSpecialGroupCal(g.getName(), cal);
                    if(item.getAmount() > 1)
                        item.setAmount(item.getAmount()-1);
                    else
                        p.getInventory().remove(item);
                    p.updateInventory();
                }
                break;
            case "permission":
                String perm = ChatColor.stripColor(item.getItemMeta().getLore().get(3));
                String perm_time = ChatColor.stripColor(item.getItemMeta().getLore().get(2));
                Customs.getPlugin().getLogger().log(Level.INFO, "Player {0} has rent permission  {1}", new Object[]{p.getName(), perm});
                Calendar cal = pd.getPermCalendar(perm);
                if(!perm_time.equalsIgnoreCase("unlimit")) {
                    if(cal == null)
                        cal = Calendar.getInstance();
                    Utils.addTimeToCalendar(perm_time.split(","), cal);
                } else
                    cal = null;
                pd.setPermCalendar(perm, cal);
                if(item.getAmount() > 1)
                    item.setAmount(item.getAmount()-1);
                else
                    p.getInventory().remove(item);
                p.updateInventory();
                break;
            default:
                Customs.getPlugin().getLogger().log(Level.INFO, "Can''t find {0}.", name[0].toLowerCase());
                break;
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(e.getRightClicked().getType() != EntityType.VILLAGER)
            return;
        
        if(!isSignOnChest(e.getRightClicked().getLocation()))
            return;
        
        e.getRightClicked().setCustomName("§2" + _names[Rnd.get(0, _names.length-1)]);
        if(!e.getRightClicked().isCustomNameVisible())
            e.getRightClicked().setCustomNameVisible(true);
        
        Inventory inv = Bukkit.createInventory(null, 54, "§2" + _names[Rnd.get(0, _names.length-1)]);
        getInventory(p, inv);
        
        if(inv.getSize() == 0) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.gamestore.emptyvillager", "You have no Items in your GameStore Shoppingcart. Sorry."));
            e.setCancelled(true);
            return;
        }
        
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.gamestore.shoppingcart", "Here are your stuff from the shopping cart."));
        p.openInventory(inv);
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent e) {
        if(e.getEntityType() != EntityType.VILLAGER)
            return;
        
        if(!isSignOnChest(e.getEntity().getLocation()))
            return;
        
        e.setDamage(0.0);
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        PlayerData pd = PlayerData.getPlayerData(p);
        if(!"[GameStore]".equalsIgnoreCase(ChatColor.stripColor(e.getLine(1))))
            return;
        
        if(!e.getPlayer().hasPermission("customs.gamestore.create")) {
            e.getBlock().breakNaturally(new ItemStack(Material.SIGN, 1));
            e.setCancelled(true);
            return;
        }
        
        e.setLine(1, "[§2GameStore§r]");
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.gamestore.create", "GameStore Sign successful created."));
    }
    
    public static File checkPath() {
        File f = new File(Customs.getPlugin().getConfig().getString("gamestore.inv-save-path", Customs.getPlugin().getDataFolder().getAbsolutePath()));
        if(!f.exists())
            f.mkdirs();
        return f;
    }
    
    private Location getLocation(InventoryHolder ih) {
        if(ih instanceof BlockState)
            return ((BlockState)ih).getLocation();
        if(ih instanceof Chest)
            return ((Chest)ih).getLocation();
        if(ih instanceof DoubleChest)
            return ((DoubleChest)ih).getLocation();
        return null;
    }
    
    private boolean isSignOnChest(Location l) {
        if(l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY(), l.getBlockZ()).getState() instanceof Sign) {
            Sign s = (Sign)l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY(), l.getBlockZ()).getState();
            if("[GameStore]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(1))))
                return true;
        }
        if(l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY(), l.getBlockZ()).getState() instanceof Sign) {
            Sign s = (Sign)l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY(), l.getBlockZ()).getState();
            if("[GameStore]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(1))))
                return true;
        }
        
        if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()-1).getState() instanceof Sign) {
            Sign s = (Sign)l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()-1).getState();
            if("[GameStore]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(1))))
                return true;
        }
        
        if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()+1).getState() instanceof Sign) {
            Sign s = (Sign)l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()+1).getState();
            if("[GameStore]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(1))))
                return true;
        }
        
        if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()-1, l.getBlockZ()).getState() instanceof Sign) {
            Sign s = (Sign)l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()-1, l.getBlockZ()).getState();
            if("[GameStore]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(1))))
                return true;
        }
        
        if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()-2, l.getBlockZ()).getState() instanceof Sign) {
            Sign s = (Sign)l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()-2, l.getBlockZ()).getState();
            if("[GameStore]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(1))))
                return true;
        }
        
        if(l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()+1, l.getBlockZ()).getState() instanceof Sign) {
            Sign s = (Sign)l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()+1, l.getBlockZ()).getState();
            if("[GameStore]".equalsIgnoreCase(ChatColor.stripColor(s.getLine(1))))
                return true;
        }
        return false;
    }
    
    private void getInventory(Player p, Inventory inv) {
        if(Customs.getGameStore().isString(p.getUniqueId().toString())) {
            InventorySerialization.setInventory(
                inv,
                Customs.getGameStore().getString(p.getUniqueId().toString())
            );
        }
        
        if(inv.getSize() <= inv.getMaxStackSize()) {
            ArrayList<GameShop> myItems = Customs.getMySQL().getGameStoreItems(p.getUniqueId().toString(), Customs.getPlugin().getConfig().getString("gamestore.server", "default"));
            PlayerData pd = PlayerData.getPlayerData(p);
            ArrayList<Integer> inchest = new ArrayList<>();
            for(GameShop gs: myItems) {
                String[] listing = gs.getGive().split("::");
                switch(gs.getType()) {
                    case "item":
                        if(inv.firstEmpty() == -1)
                            break;
                        for(int i_amount = 0; i_amount < gs.getAmount(); i_amount++) {
                            ItemStack item = Items.getItem(gs.getGive());
                            if(item == null) 
                                break;
                            
                            if(inv.firstEmpty() == -1) {
                                p.getLocation().getWorld().dropItem(p.getLocation(), item);
                                String name = item.hasItemMeta()?(item.getItemMeta().hasDisplayName()?item.getItemMeta().getDisplayName():Utils.upperFirst(item.getType().name().replace("_", " ").toLowerCase(), true)):Utils.upperFirst(item.getType().name().replace("_", " ").toLowerCase(), true);
                                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.gamestore.mustdrop", "Your Caddie is full. Must drop now %item% x %amount%.", new String[] {"%item%","%amount%"}, new String[] {name, String.valueOf(item.getAmount())}));
                            } else 
                                inv.addItem(item);
                        }
                        inchest.add(gs.getId());
                        break;
                    case "command":
                        if(listing.length < 3)
                            break;
                        
                        if(Bukkit.getServer().getPluginCommand(listing[1]) != null) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), listing[1] + " " + listing[2].replace("%player%", p.getName()));
                            inchest.add(gs.getId());
                        }
                        break;
                    case "group":
                        if(!Customs.getPlugin().isGroupManager())
                            break;
                        
                        if(listing.length < 4)
                            break;
                        
                        if(inv.firstEmpty() == -1)
                            break;
                        
                        ItemStack group_clock = Items.getItem("watch " + listing[3] + " name:&6Group_Clock lore:&2Right_click_for|&3" + listing[0].replace(" ", "_") + "|&3" + listing[1].replace(" ", "_") + "|&3" + listing[2].replace(" ", "_"));
                        if(group_clock == null) 
                            break;
                        
                        inv.addItem(group_clock);
                        inchest.add(gs.getId());
                        break;
                    case "permission":
                        if(!Customs.getPlugin().isGroupManager())
                            break;
                        
                        if(listing.length < 4)
                            break;
                        
                        if(inv.firstEmpty() == -1)
                            break;
                        
                        ItemStack perm_clock = Items.getItem("watch " + listing[3] + " name:&ePermission_Clock lore:&3Right_click_for|&2" + listing[0].replace(" ", "_") + "|&2" + listing[2].replace(" ", "_") + "|&2" + listing[1].replace(" ", "_"));
                        if(perm_clock == null) 
                            break;
                        
                        inv.addItem(perm_clock);
                        inchest.add(gs.getId());
                        break;
                    default:
                        Customs.getPlugin().getLogger().log(Level.INFO, "Cant find Type {0} with {1} on Open GameChest.", new Object[]{gs.getType(), gs.getGive()});
                        break;
                }
            }
            Customs.getMySQL().removeItems(inchest, p.getUniqueId().toString());
        }
    }
    
    private boolean isName(String name) {
        for(String str: _names) {
            if(str.contains(name))
                return true;
        }
        return false;
    }
    
    public static class removeSpecialGroup implements Runnable {
        private final String _p;
        
        public removeSpecialGroup(String p) {
            _p = p;
        }
        
        @Override
        public void run() {
            Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getUser(_p).setGroup(
                Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getDefaultGroup()
            );
            PlayerData pd = PlayerData.getPlayerData(_p);
            pd.setSpecialGroupCal("", null);
        }
    }
    
    public static class removePermissionFromPlayer implements Runnable {
        private final String _perm;
        private final String _p;
        public removePermissionFromPlayer(String p, String perm) {
            _p = p;
            _perm = perm;
        }
        
        @Override
        public void run() {
            if(Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getUser(_p).getPermissionList().contains(_perm)) {
                Customs.getPlugin().getGroupManager().getWorldsHolder().getDefaultWorld().getUser(_p).removePermission(_perm);
                PlayerData pd = PlayerData.getPlayerData(_p);
                pd.removePermission(_perm);
            }
        }
    }
}
