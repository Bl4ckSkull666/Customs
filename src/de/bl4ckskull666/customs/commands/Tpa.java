/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.commands;

import de.bl4ckskull666.customs.Customs;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.customs.utils.PlayerData;
import de.bl4ckskull666.customs.utils.TeleportRequest;
import de.bl4ckskull666.customs.utils.Utils;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author PapaHarni
 */
public class Tpa implements CommandExecutor {
    private final static ArrayList<TeleportRequest> _requests = new ArrayList<>();
    
    public static ArrayList<TeleportRequest> getRequests() {
        return _requests;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can be only run by a Player.");
            return false;
        }
        Player p = (Player)s;
        TeleportRequest tpr = null;
        Player requestP = null;
        switch(c.getName().toLowerCase()) {
            case "tpaaccept":
                if(!p.hasPermission("customs.use.tpaaccept")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpaaccept.noPermission", "You don't have permission to use this command."));
                    return true;
                }
                
                tpr = getRequestTo(p.getName());
                if(tpr == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpaaccept.noRequest", "You don't have an open request yet."));
                    return true;
                }
                
                if(Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20) != -1L && (System.currentTimeMillis()-tpr.getRequestSendTime()) > (Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20)*1000)) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpaaccept.timeover", "The request Time is over."));
                    removeRequestTo(p.getName());
                    return true;
                }
                
                requestP = Bukkit.getPlayer(tpr.getFrom());
                if(requestP == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpaaccept.requesterOffline", "The requester is now offline."));
                    removeRequestTo(p.getName());
                    return true;
                }
                
                if(tpr.getToFrom()) {
                    Utils.teleportPlayer(p, tpr.getLocation());
                } else {
                    Utils.teleportPlayer(requestP, tpr.getLocation());
                }
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpaaccept.successful", "You have been accept the teleport request."));
                requestP.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpaaccept.accept", "%name% has accept your request.", new String[] {"%name%"}, new String[] {p.getName()}));
                removeRequestTo(p.getName());
                break;
            case "tpadeny":
                if(!p.hasPermission("customs.use.tpadeny")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpadeny.noPermission", "You don't have permission to use this command."));
                    return true;
                }
                
                tpr = getRequestTo(p.getName());
                if(tpr == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpadeny.noRequest", "You don't have an open request yet."));
                    return true;
                }
                
                requestP = Bukkit.getPlayer(tpr.getFrom());
                if(requestP != null) {
                    requestP.sendMessage(Language.getMessage(Customs.getPlugin(), requestP.getUniqueId(), "command.tpadeny.denied", "%name% has denied your teleport request.", new String[] {"%name%"}, new String[] {p.getName()}));
                }
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpadeny.successful", "You have canceled the teleport request of %name%", new String[] {"%name%"}, new String[] {tpr.getFrom()}));
                removeRequestTo(p.getName());
                break;
            case "tpacancel":
                if(!p.hasPermission("customs.use.tpacancel")) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpacancel.noPermission", "You don't have permission to use this command."));
                    return true;
                }
                
                if(a.length > 0) {
                    if(a[0].equalsIgnoreCase("all")) {
                        while(getRequestFrom(p.getName()) != null) {
                            tpr = getRequestFrom(p.getName());
                            requestP = Bukkit.getPlayer(tpr.getTo());
                            if(requestP != null) {
                                PlayerData requestPpd = PlayerData.getPlayerData(requestP);
                                requestP.sendMessage(Language.getMessage(Customs.getPlugin(), requestP.getUniqueId(), "command.tpacancel.cancel", "%name% has cancel the teleport request to you.", new String[] {"%name%"}, new String[] {p.getName()}));
                            }
                            removeRequestTo(tpr.getTo());
                        }
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpacancel.successfulAll", "You have canceled the teleport request for %name%", new String[] {"%name%"}, new String[] {tpr.getTo()}));
                        return true;
                    }
                    
                    tpr = getRequestTo(a[0]);
                } else
                    tpr = getRequestFrom(p.getName());
                
                if(tpr == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpacancel.noRequest", "You don't have an open request yet."));
                    return true;
                }
                
                requestP = Bukkit.getPlayer(tpr.getTo());
                if(requestP != null) {
                    requestP.sendMessage(Language.getMessage(Customs.getPlugin(), requestP.getUniqueId(), "command.tpacancel.cancel", "%name% has cancel the teleport request for you.", new String[] {"%name%"}, new String[] {p.getName()}));
                }
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpacancel.successful", "You have canceled the teleport request for " + tpr.getTo(), new String[] {"%name%"}, new String[] {tpr.getTo()}));
                removeRequestTo(p.getName());
                break;
            case "tpahere":
                if(a.length > 1) {
                    if(!p.hasPermission("customs.use.tpahere.more")) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpahere.noPermissionMore", "You don't have permission to use this command."));
                        return true;
                    }
                } else if(a.length == 1) {
                    if(!p.hasPermission("customs.use.tpahere")) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpahere.noPermission", "You don't have permission to use this command."));
                        return true;
                    }
                } else {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpahere.noSyntax", "You must given min. one player."));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, "tpahere"))
                    return true;

                if(Customs.isBlockedWorldbyCommand("tpahere", p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand("tpahere", p))
                    return true;
                
                for(String strA: a) {
                    if(Bukkit.getPlayer(strA) == null) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpahere.playerNotExist", "The given player %name% is not online yet.", new String[] {"%name%"}, new String[] {strA}));
                        continue;
                    }
                    
                    if(getRequestTo(strA) != null) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpahere.playerHasOpenRequest", "The given player has already a open request."));
                        continue;
                    }
                
                    Player rp = Bukkit.getPlayer(strA);
                    TeleportRequest tpR = new TeleportRequest(p.getName(), rp.getName(), p.getLocation(), true);
                    _requests.add(tpR);
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpahere.requestSend", "Teleport request successful send to %name%. You can cancel it with /tpacancel %name% in the next %seconds% seconds.", new String[] {"%name%", "%seconds%"}, new String[] {rp.getName(), String.valueOf(Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20))}));
                    rp.sendMessage(Language.getMessage(Customs.getPlugin(), rp.getUniqueId(), "command.tpahere.requestBecome", "You have got a teleport request to go to %name%. You can accept it with /tpaaccept or deny with /tapdeny in the next %seconds%", new String[] {"%name%","%seconds%"}, new String[] {p.getName(), String.valueOf(Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20))}));
                }
                break;
            case "tpa":
                if(a.length > 1) {
                    if(!p.hasPermission("customs.use.tpa.more")) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.noPermissionMore", "You don't have permission to use this command."));
                        return true;
                    }
                } else if(a.length == 1) {
                    if(!p.hasPermission("customs.use.tpa")) {
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.noPermission", "You don't have permission to use this command."));
                        return true;
                    }
                } else {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.noSyntax", "You must give min. one player."));
                    return true;
                }
                
                if(Bukkit.getPlayer(a[0]) == null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.playerNotExistTo", "The given player is not online yet.", new String[] {"%name%"}, new String[] {a[0]}));
                    return true;
                }
                
                Player rp = Bukkit.getPlayer(a[0]);
                if(getRequestTo(rp.getName()) != null) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.playerHasOpenRequestTo", "The given player has already a open request.", new String[] {"%name%"}, new String[] {rp.getName()}));
                    return true;
                }
                
                if(!Customs.canCmdUseByPlayer(p, "tpa"))
                    return true;

                if(Customs.isBlockedWorldbyCommand("tpa", p, p.getWorld().getName()))
                    return true;

                if(!Customs.hasPaidForUseCommand("tpa", p))
                    return true;
                
                TeleportRequest tpR = new TeleportRequest(p.getName(), rp.getName(), rp.getLocation(), false);
                _requests.add(tpR);
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.requestSend", "Teleport request successful send to %name%. You can cancel it with /tpacancel %name% in the next %seconds% seconds.", new String[] {"%name%", "%seconds%"}, new String[] {rp.getName(), String.valueOf(Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20))}));
                rp.sendMessage(Language.getMessage(Customs.getPlugin(), rp.getUniqueId(), "command.tpa.requestBecome", "You have got a teleport request to you. You can accept it with /tpaaccept or deny with /tapdeny in the next %seconds%", new String[] {"%name%","%seconds%"}, new String[] {p.getName(), String.valueOf(Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20))}));
                    
                if(a.length > 1) {
                    for(int i = 1;i < a.length; i++) {
                        if(Bukkit.getPlayer(a[i]) == null) {
                            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.playerNotExist", "The given player is not online yet.", new String[] {"%name%"}, new String[] {a[i]}));
                            continue;
                        }
                        
                        Player mrp = Bukkit.getPlayer(a[i]);
                        if(getRequestTo(mrp.getName()) != null) {
                            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.playerHasOpenRequest", "The given player has already a open request.", new String[] {"%name%"}, new String[] {a[i]}));
                            continue;
                        }
                        tpR = new TeleportRequest(p.getName(), mrp.getName(), rp.getLocation(), false);
                        _requests.add(tpR);
                        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.tpa.requestSend", "Teleport request successful send to %name%. You can cancel it with /tpacancel %name% in the next %seconds% seconds.", new String[] {"%name%", "%seconds%"}, new String[] {rp.getName(), String.valueOf(Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20))}));
                        mrp.sendMessage(Language.getMessage(Customs.getPlugin(), mrp.getUniqueId(), "command.tpa.requestBecomeMore", "You have got a teleport request to go to %name% by %by%. You can accept it with /tpaaccept or deny with /tapdeny in the next %seconds%", new String[] {"%name%","%seconds%","%by%"}, new String[] {rp.getName(), String.valueOf(Customs.getPlugin().getConfig().getLong("tpa-wait-time", 20)), p.getName()}));
                    }
                }
                break;
            default:
                p.sendMessage("Unknown Command.");
        }
        return true;
    }
    
    private TeleportRequest getRequestTo(String p) {
        for(TeleportRequest tpr: _requests) {
            if(tpr.getTo().equalsIgnoreCase(p))
                return tpr;
        }
        return null;
    }
    
    private TeleportRequest getRequestFrom(String p) {
        for(TeleportRequest tpr: _requests) {
            if(tpr.getFrom().equalsIgnoreCase(p))
                return tpr;
        }
        return null;
    }
    
    private void removeRequestFrom(String p) {
        ArrayList<TeleportRequest> temp = new ArrayList<>();
        for(TeleportRequest tpr: _requests) {
            if(tpr.getFrom().equalsIgnoreCase(p))
                temp.add(tpr);
        }
        for(TeleportRequest tpr: temp)
            _requests.remove(tpr);
    }
    
    private void removeRequestTo(String p) {
        ArrayList<TeleportRequest> temp = new ArrayList<>();
        for(TeleportRequest tpr: _requests) {
            if(tpr.getTo().equalsIgnoreCase(p))
                temp.add(tpr);
        }
        for(TeleportRequest tpr: temp)
            _requests.remove(tpr);
    }
}