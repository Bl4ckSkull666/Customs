/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.customs.Customs;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 *
 * @author PapaHarni
 */
public class PlayerChatTabComplete implements Listener,PluginMessageListener {
    private static String[] _allPlayers;
    private static long _lastUpdate = 0;
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatTabCompleteRequest(PlayerChatTabCompleteEvent e) {
        if((System.currentTimeMillis()-_lastUpdate)/1000 > 6000) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF("ALL");
            e.getPlayer().sendPluginMessage(Customs.getPlugin(), "BungeeCord", out.toByteArray());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatTabCompleteAnswer(PlayerChatTabCompleteEvent e) {
        if(_allPlayers.length <= 0)
            return;
        
        String[] temp = _allPlayers.clone();
        e.getTabCompletions().clear();
        Customs.getPlugin().getLogger().log(Level.INFO, "LastToken : {0}", e.getLastToken());
        Customs.getPlugin().getLogger().log(Level.INFO, "Message : {0}", e.getChatMessage());
        Customs.getPlugin().getLogger().log(Level.INFO, "Current TabComplete :");
        for(String t: e.getTabCompletions())
            Customs.getPlugin().getLogger().log(Level.INFO, t);
        Customs.getPlugin().getLogger().log(Level.INFO, "~~~~~~~~~~ End ~~~~~~~~~~");
        
        if(e.getLastToken().isEmpty())
            e.getTabCompletions().addAll((Collection<String>)Arrays.asList(temp));
        else {
            for(String str: temp) {
                if(str.startsWith(e.getLastToken()))
                    e.getTabCompletions().add(str);
            }
        }
    }
    
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!channel.equals("BungeeCord"))
            return;
        
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        
        String subchannel = in.readUTF();
        if(subchannel.equalsIgnoreCase("PlayerList")) {
            String server = in.readUTF(); // The name of the server you got the player list of, as given in args.
            _allPlayers = in.readUTF().split(", ");
            _lastUpdate = System.currentTimeMillis();
        }
    }
}
