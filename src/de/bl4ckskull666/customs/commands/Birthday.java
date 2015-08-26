/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.Rnd;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Birthday implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This Command can only be used by Player.");
            return true;
        }
        
        Player p = (Player)s;
        PlayerData pd = PlayerData.getPlayerData(p);
        
        if(a.length == 1) {
            if(!p.hasPermission("customs.use.birthday")) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.birthday.needPerm", "You don't have permission to set your gender."));
                return true;
            }
            
            int[] i = getDate(a[0]);
            if(i.length < 3) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.birthday.wrongFormat", "Please use /%cmd% dd.mm.yyyy or /%cmd% mm/dd/yyyy.", new String[] {"%cmd%"}, new String[] {c.getName()}));
                return true;
            }
            
            if(!Customs.canCmdUseByPlayer(p, c.getName().toLowerCase()))
                return true;

            if(Customs.isBlockedWorldbyCommand(c.getName().toLowerCase(), p, p.getWorld().getName()))
                return true;

            if(!Customs.hasPaidForUseCommand(c.getName().toLowerCase(), p))
                return true;
            
            pd.setBirthday(i[0], i[1], i[2]);
            pd.setVerify(false);
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.birthday.successful", "Your Birthday is successful saved."));
        } else
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.birthday.wrongFormat", "Please use /%cmd% dd.mm.yyyy or /%cmd% mm/dd/yyyy.", new String[] {"%cmd%"}, new String[] {c.getName()}));
        return true;
    }
    
    private int[] getDate(String a) {
        Calendar cal = new GregorianCalendar();
        String[] d = a.split("\\.");
        if(d.length == 3) {
            if(Rnd.isNumeric(d[0]) && Rnd.isNumeric(d[1]) && Rnd.isNumeric(d[2])) {
                int day = Integer.parseInt(d[0]);
                int month = Integer.parseInt(d[1]);
                int year = Integer.parseInt(d[2]);
                if(day < 1 || day > 31 || month < 1 || month > 12 || year < 1920 || year > (cal.get(Calendar.YEAR)-5))
                    return new int[] {};
                return new int[] {day, month, year};
            }
        }

        d = a.split("/");
        if(d.length == 3) {
            if(Rnd.isNumeric(d[0]) && Rnd.isNumeric(d[1]) && Rnd.isNumeric(d[2])) {
                int day = Integer.parseInt(d[0]);
                int month = Integer.parseInt(d[1]);
                int year = Integer.parseInt(d[2]);
                if(day < 1 || day > 31 || month < 1 || month > 12 || year < 1920 || year > (cal.get(Calendar.YEAR)-5))
                    return new int[] {};
                return new int[] {day, month, year};
            }
        }
        
        d = a.split("-");
        if(d.length == 3) {
            if(Rnd.isNumeric(d[0]) && Rnd.isNumeric(d[1]) && Rnd.isNumeric(d[2])) {
                int day = Integer.parseInt(d[0]);
                int month = Integer.parseInt(d[1]);
                int year = Integer.parseInt(d[2]);
                if(day < 1 || day > 31 || month < 1 || month > 12 || year < 1920 || year > (cal.get(Calendar.YEAR)-5))
                    return new int[] {};
                return new int[] {day, month, year};
            }
        }
        return new int[0];
    }
}