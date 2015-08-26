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
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pappi
 */
public class Speed implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        //String type = "";
        //float speed = 0.0F;
        //Player p = null;
        
        if(a.length == 1) {
            if(!(s instanceof Player)) {
                s.sendMessage("This command can only run by a player.");
                return true;   
            }
            
            Player p = (Player)s;
            if(!Rnd.isFloat(a[0])) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.speed.notANumber", "Please given a Speed Level after command."));
                return true; 
            }
            
            float speed = getMoveSpeed(a[0]);
            String type = p.isFlying()?"fly":"walk";
            
            setSpeed(s, speed, p, type);
            return true;
        } else if(a.length == 2) {
            float speed;
            Player sp;
            String type;
            if(Bukkit.getPlayer(a[0]) != null && Rnd.isFloat(a[1])) {
                if(!s.hasPermission("customs.speed.other")) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.permission.other", "You don't have permission to set speed for other players.", new String[] {"%cmd%"}, new String[] {c.getName()}));
                    return true;
                }
                speed = getMoveSpeed(a[1]);
                sp = Bukkit.getPlayer(a[0]);
                type = sp.isFlying()?"fly":"walk";
            } else if(Bukkit.getPlayer(a[1]) != null && Rnd.isFloat(a[0])) {
                if(!s.hasPermission("customs.speed.other")) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.permission.other", "You don't have permission to set speed for other players.", new String[] {"%cmd%"}, new String[] {c.getName()}));
                    return true;
                }
                speed = getMoveSpeed(a[0]);
                sp = Bukkit.getPlayer(a[1]);
                type = sp.isFlying()?"fly":"walk";
            } else if(Rnd.isFloat(a[0]) && (a[1].equalsIgnoreCase("walk") || a[1].equalsIgnoreCase("fly") || a[1].equalsIgnoreCase("both"))) {
                if(!(s instanceof Player)) {
                    s.sendMessage("This command can only run by a player.");
                    return true;
                }
                sp = (Player)s;
                speed = getMoveSpeed(a[0]);
                type = a[1].toLowerCase();
            } else if(Rnd.isFloat(a[1]) && (a[0].equalsIgnoreCase("walk") || a[0].equalsIgnoreCase("fly") || a[0].equalsIgnoreCase("both"))) {
                if(!(s instanceof Player)) {
                    s.sendMessage("This command can only run by a player.");
                    return true;
                }
                sp = (Player)s;
                speed = getMoveSpeed(a[1]);
                type = a[0].toLowerCase();
            } else {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.2", "Wrong Command format. Please use /%cmd% <playername> <level> or /speed <type> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                return true;
            }
            
            setSpeed(s, speed, sp, type);
            return true;
        } else if(a.length == 3) {
            float speed;
            Player sp;
            String type;
            if(Bukkit.getPlayer(a[0]) != null) {
                sp = Bukkit.getPlayer(a[0]);
                if(Rnd.isFloat(a[1]) && (a[2].equalsIgnoreCase("walk") || a[2].equalsIgnoreCase("fly") || a[2].equalsIgnoreCase("both"))) {
                    speed = getMoveSpeed(a[1]);
                    type = a[2].toLowerCase();
                } else if(Rnd.isFloat(a[2]) && (a[1].equalsIgnoreCase("walk") || a[1].equalsIgnoreCase("fly") || a[1].equalsIgnoreCase("both"))) {
                    speed = getMoveSpeed(a[2]);
                    type = a[1].toLowerCase();
                } else {
                    //Error
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.3", "Wrong Command format. Please use /%cmd% <type> <playername> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                    return true;
                }
            } else if(Bukkit.getPlayer(a[1]) != null) {
                sp = Bukkit.getPlayer(a[1]);
                if(Rnd.isFloat(a[0]) && (a[2].equalsIgnoreCase("walk") || a[2].equalsIgnoreCase("fly") || a[2].equalsIgnoreCase("both"))) {
                    speed = getMoveSpeed(a[0]);
                    type = a[2].toLowerCase();
                } else if(Rnd.isFloat(a[2]) && (a[0].equalsIgnoreCase("walk") || a[0].equalsIgnoreCase("fly") || a[0].equalsIgnoreCase("both"))) {
                    speed = getMoveSpeed(a[2]);
                    type = a[0].toLowerCase();
                } else {
                    //Error
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.3", "Wrong Command format. Please use /%cmd% <type> <playername> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                    return true;
                }
            } else if(Bukkit.getPlayer(a[2]) != null) {
                sp = Bukkit.getPlayer(a[2]);
                if(Rnd.isFloat(a[0]) && (a[1].equalsIgnoreCase("walk") || a[1].equalsIgnoreCase("fly") || a[1].equalsIgnoreCase("both"))) {
                    speed = getMoveSpeed(a[0]);
                    type = a[1].toLowerCase();
                } else if(Rnd.isFloat(a[1]) && (a[0].equalsIgnoreCase("walk") || a[0].equalsIgnoreCase("fly") || a[0].equalsIgnoreCase("both"))) {
                    speed = getMoveSpeed(a[1]);
                    type = a[0].toLowerCase();
                } else {
                    //Error
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.3", "Wrong Command format. Please use /%cmd% <type> <playername> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                    return true;
                }
           } else {
               //Error
               s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.3", "Wrong Command format. Please use /" + c.getName() + " <type> <playername> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
               return true;
           }
           setSpeed(s, speed, sp, type);
        } else {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.list.top", "Wrong Command format. Please use one of the follow command types :", new String[] {"%cmd%"}, new String[] {c.getName()}));
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.list.1", "/%cmd% <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.list.2", "/%cmd% <type> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
            if(s.hasPermission("customs.use.speed.other")) {
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.list.3", "/%cmd% <playername> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.list.4", "/%cmd% <type> <playername> <level>", new String[] {"%cmd%"}, new String[] {c.getName()}));                
            }
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.syntax.list.end", "All Parameters can be set in individual order.", new String[] {"%cmd%"}, new String[] {c.getName()}));
        }
        return true;
    }
    
    private void setSpeed(CommandSender s, float speed, Player sp, String type) {
        UUID puuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(s instanceof Player)
            puuid = ((Player)s).getUniqueId();
        
        if(speed < 0.1F) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.toLow", "The given Speed Level is to low. Set it to Min Speed 1."));
            speed = 0.1F;
        }
            
        if(speed > 10.0F) {
            s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.toHigh", "The given Speed Level is to high. Set it to Max Speed 10."));
            speed = 10.0F;
        }
            
        if(s instanceof Player) {
            Player p = (Player)s;
            PlayerData pd = PlayerData.getPlayerData(p);
            if(maxSpeedPermission(p) < Math.ceil(speed)) {
                p.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.permission.level", "You dont have permission to use the wished speed level."));
                return;
            }
            
            if(!Customs.canCmdUseByPlayer(p, "speed"))
                return;

            if(Customs.isBlockedWorldbyCommand("speed", p, p.getWorld().getName()))
                return;

            if(!Customs.hasPaidForUseCommand("speed", p))
                return;
            
            if(Customs.getPlugin().getConfig().isInt("cmdCoolDown.speed"))
                pd.setTimeStamp("speed", System.currentTimeMillis());
        }
        
        
        float mSpeed = speed;
        switch(type.toLowerCase()) {
            case "fly":
                speed = getRealMoveSpeed(speed, true, s.hasPermission("customs.use.speed.bypass"));
                sp.setFlySpeed(speed);
                if(!s.getName().equalsIgnoreCase(sp.getName())) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.successful.fly.of", "The fly speed of %of% is now set to %speed%.", new String[] {"%by%", "%of%", "%speed%", "%type%"}, new String[] {s.getName(), sp.getName(), String.valueOf(String.format("%.1f", mSpeed)), "fly"}));
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.speed.successful.fly.by", "Your fly speed is set to %speed% by %by%.", new String[] {"%by%", "%of%", "%speed%", "%type%"}, new String[] {s.getName(), sp.getName(), String.valueOf(String.format("%.1f", mSpeed)), "fly"}));
                } else
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.speed.successful.fly.own", "Your fly speed is set to %speed%.", new String[] {"%name%","%speed%","%type%"}, new String[] {s.getName(), String.valueOf(String.format("%.1f", mSpeed)), "fly"}));
                break;
            case "walk":
                speed = getRealMoveSpeed(speed, false, s.hasPermission("customs.use.speed.bypass"));
                sp.setWalkSpeed(speed);
                if(!s.getName().equalsIgnoreCase(sp.getName())) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.successful.walk.of", "The walk speed of %of% is now set to %speed%.", new String[] {"%by%", "%of%", "%speed%", "%type%"}, new String[] {s.getName(), sp.getName(), String.valueOf(String.format("%.1f", mSpeed)), "walk"}));
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.speed.successful.walk.by", "Your walk speed is set to %speed% by %by%.", new String[] {"%by%", "%of%", "%speed%", "%type%"}, new String[] {s.getName(), sp.getName(), String.valueOf(String.format("%.1f", mSpeed)), "walk"}));
                } else
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.speed.successful.walk.own", "Your walk speed is set to %speed%.", new String[] {"%name%","%speed%", "%type%"}, new String[] {s.getName(), String.valueOf(String.format("%.1f", mSpeed)), "walk"}));
                break;
            case "both":
                float fly_speed = getRealMoveSpeed(speed, true, s.hasPermission("customs.use.speed.bypass"));
                sp.setFlySpeed(fly_speed);
                float walk_speed = getRealMoveSpeed(speed, false, s.hasPermission("customs.use.speed.bypass"));
                sp.setWalkSpeed(walk_speed);
                if(!s.getName().equalsIgnoreCase(sp.getName())) {
                    s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.successful.both.of", "The walk and run speed of %of% is now set to %speed%.", new String[] {"%by%", "%of%", "%speed%", "%type%"}, new String[] {s.getName(), sp.getName(), String.valueOf(String.format("%.1f", mSpeed)), "walk & fly"}));
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.speed.successful.both.by", "Your walk and run speed is set to %speed% by %by%.", new String[] {"%by%", "%of%", "%speed%", "%type%"}, new String[] {s.getName(), sp.getName(), String.valueOf(String.format("%.1f", mSpeed)), "walk & fly"}));
                } else
                    sp.sendMessage(Language.getMessage(Customs.getPlugin(), sp.getUniqueId(), "command.speed.successful.both.own", "Your walk and run speed is set to %speed%.", new String[] {"%name%","%speed%", "%type%"}, new String[] {s.getName(), String.valueOf(String.format("%.1f", mSpeed)), "walk & fly"}));
                break;
            default:
                s.sendMessage(Language.getMessage(Customs.getPlugin(), puuid, "command.speed.type", "Wrong Speed Type."));
                break;
        }
    }
    
    private int maxSpeedPermission(Player p) {
        int max = 1;
        for(int i = 1;i <= 10; i++) {
            if(p.hasPermission("customs.use.speed." + i))
                max = i;
        }
        return max;
    }
    
    private float getMoveSpeed(String moveSpeed) {
        float userSpeed = 1.0E-004F;
        try {
            userSpeed = Float.parseFloat(moveSpeed);
            if(userSpeed > 10.0F) 
                userSpeed = 10.0F;
            else if (userSpeed < 1.0E-004F)
                userSpeed = 1.0E-004F;
        } catch (NumberFormatException e) {
            //throw new NotEnoughArgumentsException();
        }
        return userSpeed;
    }
  
    private float getRealMoveSpeed(float userSpeed, boolean isFly, boolean isBypass) {
        float defaultSpeed = isFly?0.1F:0.2F;
        float maxSpeed = 1.0F;
        if(!isBypass)
            maxSpeed = (float)(isFly?getMaxFlySpeed():getMaxWalkSpeed());
        if(userSpeed < 1.0F)
            return defaultSpeed * userSpeed;
        float ratio = (userSpeed - 1.0F) / 9.0F * (maxSpeed - defaultSpeed);
        return ratio + defaultSpeed;
    }
    
    private double getMaxFlySpeed() {
        double maxSpeed = Customs.getPlugin().getConfig().getDouble("max-fly-speed", 0.8D);
        return maxSpeed > 1.0D ? 1.0D : Math.abs(maxSpeed);
    }
  
    public double getMaxWalkSpeed() {
        double maxSpeed = Customs.getPlugin().getConfig().getDouble("max-walk-speed", 0.8D);
        return maxSpeed > 1.0D ? 1.0D : Math.abs(maxSpeed);
    }
}