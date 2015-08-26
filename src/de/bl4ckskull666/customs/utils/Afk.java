/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.customs.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author PapaHarni
 */
public final class Afk {
    private final String _name;
    private final String _message;
    private final long _since;
    
    public Afk(String name, String msg) {
        _name = name;
        _message = msg;
        _since = System.currentTimeMillis();
    }
    
    public String getName() {
        return _name;
    }
    
    public String getMessage() {
        return _message;
    }
    
    public long getSince() {
        return _since;
    }
    
    public String getSinceFormat(String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(_since);
        SimpleDateFormat form = new SimpleDateFormat(format);
        return form.format(cal.getTime());
    }
}
