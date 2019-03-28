/**
 * @author ovallcorba
 *
 */
package com.vava33.jutils;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Options {

    private Map<String,String> options;
    
    public Options() {    
        options = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
    }

    public String getValue(String key) {
        return options.get(key);
    }
    
    public void readOptions(File confFile) {
        Scanner scParFile = null;
      try {
          scParFile = new Scanner(confFile);
          while (scParFile.hasNextLine()){
              String line = scParFile.nextLine();
              if (line.trim().startsWith("#"))continue;
              int iigual=line.indexOf("=")+1;
              if (iigual<=0)continue;
              String key = line.substring(0, iigual-1).trim();
              String value = line.substring(iigual, line.trim().length()).trim();
              options.put(key, value);
          }
      }catch(Exception e){
          e.printStackTrace();
      }finally {
          if (scParFile!=null)scParFile.close();          
      }
    }
    
    public void put(String key, String value) {
        options.put(key, value);
    }
    
    public void updateOptions() {
        //TODO 
    }
    
    public void writeOptions(File confFile) {
        //TODO
    }
 
    public boolean getValAsBoolean(String key, boolean def) {
        if(this.options.get(key)!=null) {
            return Boolean.parseBoolean(this.options.get(key));
        }else {
            return def;
        }
    }
    
    public int getValAsInteger(String key, int def) {
        try {
            def = Integer.parseInt(this.options.get(key));
        }catch (Exception ex) {
            //do nothing since it will return default value
        }
        return def;
    }
    
    public double getValAsDouble(String key, double def) {
        try {
            def = Double.parseDouble(this.options.get(key));
        }catch (Exception ex) {
            //do nothing since it will return default value
        }
        return def;
    }
    
    public float getValAsFloat(String key, float def) {
        try {
            def = Float.parseFloat(this.options.get(key));
        }catch (Exception ex) {
            //do nothing since it will return default value
        }
        return def;
    }
    
    public String getValAsString(String key, String def) {
        if(this.options.get(key)!=null) {
            return this.options.get(key);
        }else {
            return def;
        }
    }
    
    public Color getValAsColor(String key, Color def) {
        if(this.options.get(key)!=null) {
            Color c = FileUtils.parseColorName(this.options.get(key));
            if (c!=null)return c;
        }
        return def;
    }
    
    public Iterator<String> getIterator() {
        return options.keySet().iterator();
    }
}
