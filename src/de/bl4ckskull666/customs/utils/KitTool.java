/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Pappi
 */
public final class KitTool {
    private final String _name;
    private final long _delay;
    private final int _cost;
    private final List<String> _items;
    
    public KitTool(String name, long delay, int cost, List<String> items) {
        _name = name;
        _delay = delay;
        _cost = cost;
        _items = items;
    }
    
    public String getName() {
        return _name;
    }
    
    public long getDelay() {
        return _delay;
    }
    
    public int getCost() {
        return _cost;
    }
    
    public List<String> getItemsAsString() {
        return _items;
    }
    
    public ItemStack[] getItems() {
        ItemStack[] temp = new ItemStack[_items.size()];
        int i = 0;
        for(String str: _items) {
            temp[i] = Items.getItem(str);
            i++;
        }
        return temp;
    }
}
