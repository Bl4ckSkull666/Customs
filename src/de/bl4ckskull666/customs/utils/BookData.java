/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author PapaHarni
 */
public class BookData {
    private final HashMap<Integer, String> _pages = new HashMap<>();
    private final String _autor;
    private final String _title;
    private int _pageCount = 0;
    
    public BookData(String autor, String title) {
        _autor = autor;
        _title = title;
    }
    
    public void addPage(int p, String txt) {
        _pages.put(p, txt);
        if(p > _pageCount)
            _pageCount = p;
    }
    
    public String getAutor() {
        return _autor;
    }
    
    public String getTitle() {
        return _title;
    }
    
    public HashMap<Integer, String> getPages() {
        return _pages;
    }
    
    public String[] getArrayPages() {
        String[] temp = new String[_pageCount];
        for(Map.Entry<Integer, String> e: _pages.entrySet())
            temp[(e.getKey()-1)] = e.getValue();
        return temp;
    }
    
    public int getPageCount() {
        return _pageCount;
    }
}
