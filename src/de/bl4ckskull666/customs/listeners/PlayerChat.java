/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;

/**
 *
 * @author Pappi
 */
public class PlayerChat implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if(e.getPlayer() == null)
            return;
        
        Player p = e.getPlayer();
        String pGroup = Customs.getPlugin().getGroupManager().getWorldsHolder().getWorldData(e.getPlayer()).getUser(e.getPlayer().getName()).getGroupName().toLowerCase();
        String pWorld = Customs.getPlugin().getConfig().isString("chat-world-prefix." + e.getPlayer().getWorld().getName().toLowerCase())?Customs.getPlugin().getConfig().getString("chat-world-prefix." + e.getPlayer().getWorld().getName().toLowerCase()):Customs.getPlugin().getConfig().getString("chat-world-prefix.default","");
        String pFormat = Customs.getPlugin().getConfig().isString("chat-format." + pGroup)?Customs.getPlugin().getConfig().getString("chat-format." + pGroup):Customs.getPlugin().getConfig().getString("chat-format.default","{name}:{message}");
        
        pFormat = pFormat.replace("{name}", e.getPlayer().getName());
        pFormat = pFormat.replace("{world}", pWorld);
        String msg = Customs.setColors(p, "customs.use.chat-color", e.getMessage());
        msg = Customs.setFormat(p, "customs.use.chat-format", msg);
        pFormat = pFormat.replace("{msg}", msg);
        
        PlayerData pd = PlayerData.getPlayerData(e.getPlayer());
        if(Customs.getPlugin().getConfig().getBoolean("allow-chat-only-with-gender-age", false) && (pd.getAge() <= 0 || pd.getGender() == "none")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "function.chat.notAllowed", "Please add your truthfully gender and birthday. /gender {female/male} and /birthday mm/dd/yyyy"));
            e.setCancelled(true);
        }
        
        if(pFormat.contains("{age}") || pFormat.contains("\\{age\\}")) {
            String verifyColor = Customs.getPlugin().getConfig().getString("chat-verify-color.is" + (pd.getVerify()?"":"not") + "verify", "&f");
            String age = verifyColor + "[";
            if(Customs.getPlugin().getConfig().getBoolean("chat-gender-color.use", false)) {
                if(pd.getGender().isEmpty())
                    pd.setGender("none");
                age += Customs.getPlugin().getConfig().getString("chat-gender-color." + pd.getGender(), "");
            }
            age += (pd.getAge() > -1)?String.valueOf(pd.getAge()):"N/A";
            age += verifyColor + "]";
            pFormat = pFormat.replace("{age}", age);
            if(pd.getAge() <= 0 || pd.getGender().equalsIgnoreCase("none") || pd.getGender().isEmpty())
                e.getPlayer().sendMessage(Language.getMessage(Customs.getPlugin(), e.getPlayer().getUniqueId(), "function.chat.no-support", "Please note, without specifying gender and birthdate, you will get no help from the server team. /birthday <day.month.year> and /gender <male/female>"));
        }
        
        pFormat = ChatColor.translateAlternateColorCodes('&', pFormat);
        pFormat = pFormat.replace("{msg}", msg);
        e.setFormat("%2$s");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        String time = ChatColor.WHITE + "[" + ChatColor.GOLD + (cal.get(Calendar.HOUR_OF_DAY) < 10?"0":"") + cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE) < 10?"0":"") + cal.get(Calendar.MINUTE) + ":" + (cal.get(Calendar.SECOND) < 10?"0":"") + cal.get(Calendar.SECOND) + ChatColor.WHITE + "]";
        e.setMessage(time + pFormat);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        int i = -1;
        for(String str: e.getLines()) {
            i++;
            if(str.isEmpty())
                continue;
            str = Customs.setColors(p, "customs.use.sign-color", str);
            str = Customs.setFormat(p, "customs.use.sign-format", str);
            e.setLine(i, str);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEditBook(PlayerEditBookEvent e) {
        Player p = e.getPlayer();
        BookMeta bmNew = e.getNewBookMeta();
        int i = 0;
        for(String page: bmNew.getPages()) {
            i++;
            if(page.isEmpty())
                continue;
            page = Customs.setColors(p, "customs.use.book-color", page);
            page = Customs.setFormat(p, "customs.use.book-format", page);
            bmNew.setPage(i, page);
        }
        e.setNewBookMeta(bmNew);
    }
}