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
import de.bl4ckskull666.customs.utils.Utils;
import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

/**
 *
 * @author PapaHarni
 */
public class SpawnMob implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if(!(s instanceof Player)) {
            s.sendMessage("This command can only run by a player.");
            return true;
        }
        
        Player p = (Player)s;
        if(!p.hasPermission("customs.use.spawnmob")) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawnmob.noPermissions", "You don't have permission to use this command."));
            return true;
        }
        
        if(a.length < 1) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawnmob.needSyntax", "Please add min. one entity type. Avaiblable EntityTypes, Amount, randomSpawn (true/false), radius (after randomSpawn) mount:entitytype", a, a));
            return true;
        }
        
        if(a[0].equalsIgnoreCase("list")) {
            String creatures = "";
            for(EntityType ent: EntityType.values()) {
                if((Utils.isMonster(ent) || Utils.isAnimal(ent) || Utils.isOtherCreature(ent) || Utils.isVehicle(ent)) && ent != EntityType.PLAYER)
                    creatures += creatures.isEmpty()?"§9" + ent.name().toLowerCase():"§e, §9" + ent.name().toLowerCase();
            }
            s.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawnmob.list", "Here a small list of all available Creatures :"));
            s.sendMessage(creatures);
            return true;
        }
        
        ArrayList<EntityType> ent = new ArrayList<>();
        EntityType mount = null;
        int amount = 1;
        boolean spawnRandom = false;
        double radius = 1.0D;
        
        for(String str: a) {
            if(Utils.isEntityType(str)) {
                EntityType enti = EntityType.valueOf(str.toUpperCase());
                if(!Utils.isMonster(enti) && !Utils.isAnimal(enti) && !Utils.isOtherCreature(enti) && !Utils.isVehicle(enti) || enti == EntityType.PLAYER) {
                    p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawnmob.unknowType", "%creature% is not a available Type of Creature", new String[] {"%creature%"}, new String[] {str}));
                    return true;
                }
                ent.add(enti);
                p.sendMessage("Add Entity " + enti.name());
            } else if(spawnRandom && Rnd.isDouble(str)) {
                radius = Double.parseDouble(str);
                p.sendMessage("Set Radius to " + radius);
            } else if(Rnd.isNumeric(str)) {
                amount = Integer.parseInt(str);
                p.sendMessage("Set Amount to " + amount);
            } else if(str.startsWith("mount:")) {
                String strMount = str.substring(6);
                if(Utils.isEntityType(strMount)) {
                    mount = EntityType.valueOf(strMount.toUpperCase());
                    p.sendMessage("Add Mount to " + mount.name());
                }
            } else if(str.equalsIgnoreCase("false") || str.equalsIgnoreCase("true")) {
                spawnRandom = Boolean.parseBoolean(str);
                p.sendMessage("Set Random to " + spawnRandom);
            }
        }
        
        if(ent.isEmpty()) {
            p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawnmob.noTypes", "Can't find any EntityType to Spawn."));
            return true;
        }

        Location mainLoc = Utils.getCursorLocation(p);
        for(int i = 0; i < amount; i++) {
            Location loc = mainLoc.clone();
            if(spawnRandom) {
                loc.setX(Rnd.get((loc.getX()-radius), (loc.getX()+radius)));
                loc.setZ(Rnd.get((loc.getZ()-radius), (loc.getZ()+radius)));
            }
            EntityType etUse = ent.get(Rnd.get(ent.size()));
            Entity entSpawn = loc.getWorld().spawnEntity(loc, etUse);
            if(mount != null) {
                Entity entMount = loc.getWorld().spawnEntity(loc, mount);
                entMount.setPassenger(entSpawn);
            }
        }
        p.sendMessage(Language.getMessage(Customs.getPlugin(), p.getUniqueId(), "command.spawnmob.successful", "%amount% Creatures are spawned.", new String[] {"%amount%"}, new String[] {String.valueOf(amount)}));
        return true;
    }
    
    /*
    private void setEntitySpecial(Entity e, String[] a, Player p) {
        if(e instanceof Animals) {
            Animals en = (Animals)e;
            for(String str: a) {
                if(str.equalsIgnoreCase("baby"))
                    en.setBaby();
                else if(str.equalsIgnoreCase("adult"))
                    en.setAdult();
                else if(str.equalsIgnoreCase("lock"))
                    en.setAgeLock(true);
                else if(str.equalsIgnoreCase("unlock"))
                    en.setAgeLock(false);
                else if(str.equalsIgnoreCase("breed"))
                    en.setBreed(true);
                else if(str.equalsIgnoreCase("unbreed"))
                    en.setBreed(false);
                else if(str.equalsIgnoreCase("show"))
                    en.setCustomNameVisible(true);
                else if(str.equalsIgnoreCase("hide"))
                    en.setCustomNameVisible(false);
                else if(str.equalsIgnoreCase("leashed"))
                    en.setLeashHolder((Entity)p);
                else if(str.equalsIgnoreCase("pickup"))
                    en.setCanPickupItems(true);
                else if(str.equalsIgnoreCase("unpick"))
                    en.setCanPickupItems(false);
                else if(str.toLowerCase().startsWith("age")) {
                    str = str.substring(3);
                    if(Rnd.isNumeric(str))
                        en.setAge(Integer.parseInt(str));
                } else if(str.toLowerCase().startsWith("health")) {
                    str = str.substring(6);
                    if(Rnd.isDouble(str))
                        en.setHealth(Double.parseDouble(str));
                } else if(str.toLowerCase().startsWith("name")) {
                    str = str.substring(4);
                    en.setCustomName(str);
                } else if(str.toLowerCase().startsWith("fire")) {
                    str = str.substring(4);
                    if(Rnd.isNumeric(str))
                        en.setFireTicks(Integer.parseInt(str));
                } else if(str.toLowerCase().startsWith("maxhealth")) {
                    str = str.substring(9);
                    if(Rnd.isDouble(str))
                        en.setMaxHealth(Double.parseDouble(str));
                } else if(str.toLowerCase().startsWith("nodmg")) {
                    str = str.substring(8);
                    if(Rnd.isNumeric(str))
                        en.setNoDamageTicks(Integer.parseInt(str));
                } else if(str.toLowerCase().startsWith("life")) {
                    str = str.substring(4);
                    if(Rnd.isNumeric(str))
                        en.setTicksLived(Integer.parseInt(str));
                }
            }
            
            if(e instanceof Bat) {
                Bat b = (Bat)e;
                for(String str: a) {
                    if(str.equalsIgnoreCase("awake"))
                        b.setAwake(true);
                    else if(str.equalsIgnoreCase("sleep"))
                        b.setAwake(false);
                }
            }
            
            if(e instanceof FishHook) {
                FishHook an = (FishHook)e;
                an.setBounce(true);
                an.setBiteChance(d);
                
            }
            
            if(e instanceof Horse) {
                Horse h = (Horse)e;
                h.setCarryingChest(true);
                h.setColor(Horse.Color.BLACK);
                h.setDomestication(i);
                h.setJumpStrength(d);
                h.setMaxDomestication(i);
                h.setStyle(Horse.Style.BLACK_DOTS);
                h.setTamed(true);
                h.setOwner(p);
                h.setVariant(Horse.Variant.DONKEY);
            }
            
            if(e instanceof IronGolem) {
                IronGolem ig = (IronGolem)e;
                ig.setPlayerCreated(true);
            }
            
            if(e instanceof Ocelot) {
                Ocelot o = (Ocelot)e;
                o.setCatType(Ocelot.Type.RED_CAT);
                o.setOwner(p);
                o.setSitting(true);
                o.setTamed(true);
            }
            
            if(e instanceof Pig) {
                Pig pi = (Pig)e;
                pi.setSaddle(true);
            }
            
            if(e instanceof Rabbit) {
                Rabbit r = (Rabbit)e;
                r.setRabbitType(Rabbit.Type.GOLD);
            }
            
            if(e instanceof Sheep) {
                Sheep s = (Sheep)e;
                s.setSheared(true);
            }
            
            if(e instanceof Villager) {
                Villager s = (Villager)e;
                s.setProfession(Villager.Profession.BLACKSMITH);
            }
            
            if(e instanceof Wolf) {
                Wolf w = (Wolf)e;
                w.setAngry(true);
                w.setCollarColor(DyeColor.RED);
                w.setSitting(true);
                w.setTamed(true);
                w.setOwner(p);
            }
        }
        
        if(e instanceof Monster) {
            Monster en = (Monster)e;
            for(String str: a) {
                if(str.equalsIgnoreCase("show"))
                    en.setCustomNameVisible(true);
                else if(str.equalsIgnoreCase("hide"))
                    en.setCustomNameVisible(false);
                else if(str.equalsIgnoreCase("leashed"))
                    en.setLeashHolder((Entity)p);
                else if(str.equalsIgnoreCase("pickup"))
                    en.setCanPickupItems(true);
                else if(str.equalsIgnoreCase("unpick"))
                    en.setCanPickupItems(false);
                else if(str.toLowerCase().startsWith("health")) {
                    str = str.substring(6);
                    if(Rnd.isDouble(str))
                        en.setHealth(Double.parseDouble(str));
                } else if(str.toLowerCase().startsWith("name")) {
                    str = str.substring(4);
                    en.setCustomName(str);
                } else if(str.toLowerCase().startsWith("fire")) {
                    str = str.substring(4);
                    if(Rnd.isNumeric(str))
                        en.setFireTicks(Integer.parseInt(str));
                } else if(str.toLowerCase().startsWith("maxhealth")) {
                    str = str.substring(9);
                    if(Rnd.isDouble(str))
                        en.setMaxHealth(Double.parseDouble(str));
                } else if(str.toLowerCase().startsWith("nodamage")) {
                    str = str.substring(8);
                    if(Rnd.isNumeric(str))
                        en.setNoDamageTicks(Integer.parseInt(str));
                } else if(str.toLowerCase().startsWith("life")) {
                    str = str.substring(4);
                    if(Rnd.isNumeric(str))
                        en.setTicksLived(Integer.parseInt(str));
                }
            }
        }
        
        if(e instanceof Bat) {
            Bat en = (Bat)e;
            for(String str: a) {
                if(str.equalsIgnoreCase("awake"))
                    en.setAwake(true);
        }
    }
    */
}
