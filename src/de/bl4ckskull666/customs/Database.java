/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs;

import de.bl4ckskull666.customs.utils.GameShop;
import de.bl4ckskull666.customs.utils.KillStats;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

/**
 *
 * @author PapaHarni
 */
public final class Database {
    private final FileConfiguration _conf;
    
    public Database() {
        _conf = Customs.getPlugin().getConfig();
    }
    
    public boolean isMySQLDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        } catch(ClassNotFoundException t) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Konnte den MySQL Treiber nicht finden!", t);
            return false;
        }
    }
    
    public boolean checkServerDBConnection() {
        if(!isMySQLDriver())
            return false;
        Connection con = getConnect("server");
        if(con == null)
            return false;
        close(con);
        checkEntityDB();
        return true;
    }
    
    public boolean checkBungeeDBConnection() {
        if(!isMySQLDriver())
            return false;
        Connection con = getConnect("bungee");
        if(con == null)
            return false;
        close(con);
        checkGameStoreDB();
        return true;
    }
    
    public Connection getConnect(String type) {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + _conf.getString("database." + type + ".host", "root")
                + ":" + _conf.getString("database." + type + ".port", "3306")
                + "/" + _conf.getString("database." + type + ".db", "minecraft"),
                _conf.getString("database." + type + ".user", "root"),
                _conf.getString("database." + type + ".pass", "rootmc")
            );
        } catch(SQLException e) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Fehler beim Herstellen der Verbindung zum MySQL Type Server!", e);
        }
        return null;
    }
    
    public void close(Connection con) {
        if(con == null)
            return;
        
        try {
            con.close();
        } catch(SQLException e) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Fehler beim beenden einer MySQL Verbindung", e);
        }
    }
    
    public void checkGameStoreDB() {
        Connection con = getConnect("bungee");
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + _conf.getString("database.bungee.gamestore.prefix", "gamestore_") + _conf.getString("database.bungee.gamestore.shop", "shop") + "` ("
                    + "`id` int(11) NOT NULL AUTO_INCREMENT, "
                    + "`give_name` varchar(64) NOT NULL, "
                    + "`give_id` varchar(255) NOT NULL, "
                    + "`give_count` int(11) NOT NULL, "
                    + "`give_extras` varchar(255) NOT NULL, "
                    + "`give_picture` text NOT NULL,"
                    + "`take_type` enum('Coins','Vote-Points','Foins','PvP-Münzen','SP-Points') NOT NULL DEFAULT 'Coins', "
                    + "`take_count` int(11) NOT NULL, "
                    + "`servers` text NOT NULL, "
                    + "`shop_type` enum('command','item','permission','group') NOT NULL DEFAULT 'item', "
                    + "PRIMARY KEY (`id`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
            
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + _conf.getString("database.bungee.gamestore.prefix", "gamestore_") + _conf.getString("database.bungee.gamestore.caddie", "caddie") + "` ("
                    + "`id` int(11) NOT NULL AUTO_INCREMENT, "
                    + "`uuid` varchar(40) NOT NULL, "
                    + "`shopid` int(11) NOT NULL, "
                    + "`amount` int(11) NOT NULL, "
                    + "`server` enum('city','minigames','bedwars','pvp','hungergames','creative') NOT NULL DEFAULT 'city', "
                    + "PRIMARY KEY (`id`)"
                + ") ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Error on create Table " + _conf.getString("database.server.table", "playerkills") + ".", e); 
        }
        close(con);
    }
    
    public void checkEntityDB() {
        Connection con = getConnect("server");
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + _conf.getString("database.server.table", "playerkills") + "` (`uuid` varchar(64) NOT NULL, "
                + "`lastName` varchar(32) NOT NULL, "
                + "`total` bigint(13) NOT NULL DEFAULT '0', "
                + "PRIMARY KEY (`uuid`)) "
                + "ENGINE=MyISAM DEFAULT CHARSET=latin1"
            );
            statement.execute();
            statement.close();
        } catch(SQLException e) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Error on create Table " + _conf.getString("database.server.table", "playerkills") + ".", e); 
        }
        
        for(EntityType ent: EntityType.values()) {
            if(!ent.isAlive())
                continue;
        
            try {
                DatabaseMetaData md = con.getMetaData();
                ResultSet rs;
                
                rs = md.getColumns(null, null, _conf.getString("database.server.table", "playerkills"), ent.name().toLowerCase());
                if(!rs.next()) {
                    Customs.getPlugin().getLogger().log(Level.INFO, "Add Colum {0} to Table {1}", new Object[]{ent.name().toLowerCase(), _conf.getString("database.server.table", "playerkills")});
                    try {
                        PreparedStatement statement;
                        statement = con.prepareStatement("ALTER TABLE `" + _conf.getString("database.server.table", "playerkills") + "` ADD (" + ent.name().toLowerCase() + " bigint(13) NOT NULL DEFAULT 0)");
                        statement.execute();
                        statement.close();
                    } catch(SQLException e) {
                        Customs.getPlugin().getLogger().log(Level.WARNING, "Error on add Colum " + ent.name().toLowerCase() + " to Table " + _conf.getString("database.server.table", "playerkills") + ".", e); 
                    } 
                }
                rs.close();
            } catch(SQLException e) {
                Customs.getPlugin().getLogger().log(Level.WARNING, "Error on Check Colum " + ent.name().toLowerCase() + " in Table " + _conf.getString("database.server.table", "playerkills") + " exist.", e);
            }
        }
        close(con);
    }
    
    public void loadPlayerKills(String pName) {
        if(!Customs.getPlugin().getConfig().getBoolean("database.server.useable"))
            return;
        
        KillStats ks = Customs.getKillStatsByPlayer(pName);
        Connection con = getConnect("server");
        for(EntityType ent: EntityType.values()) {
            if(!ent.isAlive())
                continue;
            
            try {
                PreparedStatement statement;
                ResultSet rs;
                
                statement = con.prepareStatement("SELECT `" + ent.name().toLowerCase() + "` FROM `" + _conf.getString("database.server.table", "playerkills") + "` WHERE `uuid` = ? LIMIT 0,1");
                statement.setString(1, ks.getUUID());
                rs = statement.executeQuery();
                if(rs.next())
                    ks.setKills(ent, rs.getLong(ent.name().toLowerCase()));
                rs.close();
                statement.close();
            } catch(SQLException e) {
                Customs.getPlugin().getLogger().log(Level.WARNING, "Error on Check Colum " + ent.name().toLowerCase() + " in Table " + _conf.getString("database.server.table", "playerkills") + " exist.", e);
            }
        }
        close(con);
    }
    
    public void savePlayerKills(String pName) {
        if(!Customs.getPlugin().getConfig().getBoolean("database.server.useable"))
            return;
        
        KillStats ks = Customs.getKillStatsByPlayer(pName);
        Connection con = getConnect("server");
        for(EntityType ent: EntityType.values()) {
            if(!ent.isAlive())
                continue;
            
            try {
                PreparedStatement statement;
                statement = con.prepareStatement("INSERT INTO " + _conf.getString("database.server.table", "playerkills") + " (`uuid`,`lastName`,`total`,`" + ent.name().toLowerCase() + "`) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE total=?," + ent.name().toLowerCase() + "=?");
                statement.setString(1, ks.getUUID());
                statement.setString(2, pName);
                statement.setLong(3, ks.getAllKills());
                statement.setLong(4, ks.getKill(ent));
                statement.setLong(5, ks.getAllKills());
                statement.setLong(6, ks.getKill(ent));
                statement.execute();
            } catch(SQLException e) {
                Customs.getPlugin().getLogger().log(Level.WARNING, "Error on save KillStats of " + pName + " by Entity " + ent.getName().toLowerCase() + ".", e);
            }
        }
        close(con);
    }
    
    public ArrayList<GameShop> getGameStoreItems(String uuid, String server) {
        ArrayList<GameShop> items = new ArrayList<>();
        if(!Customs.getPlugin().getConfig().getBoolean("database.bungee.useable"))
            return items;
        
        Connection con = getConnect("bungee");
        try {
            PreparedStatement statement;
            ResultSet rs;
            statement = con.prepareStatement("SELECT s.give_name,s.give_id,s.give_count,s.give_extras,s.shop_type,b.id,b.amount FROM `" + _conf.getString("database.bungee.gamestore.prefix", "gamestore_") + _conf.getString("database.bungee.gamestore.caddie", "caddie") + "` AS b LEFT JOIN `" + _conf.getString("database.bungee.gamestore.prefix", "gamestore_") + _conf.getString("database.bungee.gamestore.shop", "shop") + "` AS s ON b.shopid = s.id WHERE b.uuid = ? AND b.server = ? LIMIT 0,52");
            statement.setString(1, uuid);
            statement.setString(2, server);
            rs = statement.executeQuery();
            while(rs.next()) {
                GameShop gs;
                switch(rs.getString("shop_type")) {
                    case "item":
                        gs = new GameShop(rs.getInt("id"), rs.getString("give_id") + " " + rs.getString("give_count") + " " + rs.getString("give_extras"), rs.getInt("amount"), rs.getString("shop_type"));
                        break;
                    default:
                        gs = new GameShop(rs.getInt("id"), rs.getString("give_name") + "::" + rs.getString("give_id") + "::" + rs.getString("give_extras") + "::" + rs.getString("give_count"), rs.getInt("amount"), rs.getString("shop_type"));
                        break;
                }
                items.add(gs);
            }
        } catch(SQLException e) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Error on get gameStore items for " + uuid + ".", e);
        }
        close(con);
        return items;
    }
    
    public void removeItems(ArrayList<Integer> items, String uuid) {
        if(!Customs.getPlugin().getConfig().getBoolean("database.bungee.useable"))
            return;
        
        Connection con = getConnect("bungee");
        try {
            PreparedStatement statement;
            statement = con.prepareStatement("DELETE FROM `" + _conf.getString("database.bungee.gamestore.prefix", "gamestore_") + _conf.getString("database.bungee.gamestore.caddie", "caddie") + "` WHERE `id` = ? AND `uuid` = ?");
            for(int id: items) {
                statement.setInt(1, id);
                statement.setString(2, uuid);
                statement.execute();
            }
        } catch(SQLException e) {
            Customs.getPlugin().getLogger().log(Level.WARNING, "Error on remove gameStore items for " + uuid + ".", e);
        }
        close(con);
    }
}