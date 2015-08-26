/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

/**
 *
 * @author PapaHarni
 */
public final class GameShop {
    private final int _id;
    private final String _give;
    private final String _type;
    private final int _amount;
    
    
    public GameShop(int id, String give, int amount, String type) {
        _id = id;
        _give = give;
        _type = type;
        _amount = amount;
    }
    
    public int getId() {
        return _id;
    }
    
    public String getGive() {
        return _give;
    }
    
    public String getType() {
        return _type;
    }
    
    public int getAmount() {
        return _amount;
    }
}
