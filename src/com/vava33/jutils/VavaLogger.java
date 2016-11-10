package com.vava33.jutils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author ovallcorba
 *
 */
public class VavaLogger {

    /** The decimal formats #0.0 */
    public static DecimalFormat dfX_1 = new DecimalFormat("#0.0");
    
    /** The decimal format #0.00 */
    public static DecimalFormat dfX_2 = new DecimalFormat("#0.00");
    
    /** The decimal format #0.000 */
    public static DecimalFormat dfX_3 = new DecimalFormat("#0.000");
    
    /** The decimal format #0.0000 */
    public static DecimalFormat dfX_4 = new DecimalFormat("#0.0000");
    
    /** The decimal format #0.00000 */
    public static DecimalFormat dfX_5 = new DecimalFormat("#0.00000");
    
    /** The decimal format #0.00000 */
    public static DecimalFormat dfX_6 = new DecimalFormat("#0.000000");
    
    /** The time stamp format [HH:mm] */
    private static SimpleDateFormat fHora = new SimpleDateFormat("[HH:mm]");
    
    /** THE LOGGER */
	public Logger LOG;
	
	//crea un logger amb el nom donat amb un handler per la consola, si usermode=true nomes mostra [level] msg
	public VavaLogger(String name){
	    LOG = Logger.getLogger(name);
        LOG.setUseParentHandlers(false);
        if (LOG.getHandlers().length==0){
            Handler ch = new ConsoleHandler();
            LOG.addHandler(ch);            
        }
	}
	
	//default logger
	public VavaLogger(){
	    this("VAVALOGGER");
	}
	
// ********** LEVEL CONTROL
	
	public void enableLogger(boolean enable){
	    if (enable){
	        this.enableLogger();
	    }else{
	        this.disableLogger();
	    }
	}
	
	public void enableLogger(){
		LOG.setLevel(Level.ALL);
	}
	
	public void setLogLevel(String level){
        if (level.equalsIgnoreCase("config"))setCONFIG();
        if (level.equalsIgnoreCase("debug"))setCONFIG();
        if (level.equalsIgnoreCase("info"))setINFO();
        if (level.equalsIgnoreCase("warning"))setWARNING();
        if (level.equalsIgnoreCase("severe"))setSEVERE();
        if (level.equalsIgnoreCase("fine"))setFINE();
	}
	
    public void setINFO(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.INFO);
            handler.setFormatter(new FormatterUSER());
        }
    }
    
    public void setWARNING(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.WARNING);
            handler.setFormatter(new FormatterUSER());
        }
    }
    
    public void setCONFIG(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.CONFIG);
            handler.setFormatter(new FormatterDEBUG());
        }
    }
    
    public void setSEVERE(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.SEVERE);
            handler.setFormatter(new FormatterUSER());
        }
    }
    
    public void setFINE(){
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.FINE);
            handler.setFormatter(new FormatterDEBUG());
        }
    }
	
	public void disableLogger(){
	    LOG.config("** LOGGING DISABLED **");
		LOG.setLevel(Level.OFF);
        for (Handler handler : LOG.getHandlers()) {
            handler.setLevel(Level.OFF);
        }
	}
	
	public String logStatus(){
        String out = "";
        for (Handler handler : LOG.getHandlers()) {
            if (handler.getLevel()==Level.OFF)out = "Console logging DISABLED";
            if (handler.getLevel()==Level.INFO)out = "Console logging ENABLED - INFO";
            if (handler.getLevel()==Level.WARNING)out = "Console logging ENABLED - WARNING";
            if (handler.getLevel()==Level.CONFIG)out = "Console logging ENABLED - CONFIG";
            if (handler.getLevel()==Level.SEVERE)out = "Console logging ENABLED - SEVERE";
            if (handler.getLevel()==Level.FINE)out = "Console logging ENABLED - FINE";
        }
        return out;

	}
	
// ************* WRITTING MESSAGES
	
	public void info(String s){
		LOG.info(s);
	}
	public void warning(String s){
		LOG.warning(s);
	}
	public void config(String s){
		LOG.config(s);
	}
	public void debug(String s){
	    LOG.config(s);
	}
    public void severe(String s){
        LOG.severe(s);
    }
	public void fine(String s){
	    LOG.fine(s);
	}

	
	public void printmsg(String LEVEL, String msg){
	    if (LEVEL.equalsIgnoreCase("config"))LOG.config(msg);
	    if (LEVEL.equalsIgnoreCase("debug"))LOG.config(msg);
	    if (LEVEL.equalsIgnoreCase("info"))LOG.info(msg);
	    if (LEVEL.equalsIgnoreCase("warning"))LOG.warning(msg);
	    if (LEVEL.equalsIgnoreCase("severe"))LOG.severe(msg);
	    if (LEVEL.equalsIgnoreCase("fine"))LOG.fine(msg);
	}
	    
	
	//it will write a line containing name=value, name2=value2, ...
	// oneline means if it will be written in one line or one variable per line
	public void writeNameNumPairs(String level, boolean oneline, String namesCommaSeparated, double... numbers){
	    String[] nameslist = namesCommaSeparated.trim().split(",");
	    if (nameslist.length != numbers.length){
	        //print in two groups names = numbers
	        writeNameNums(level, oneline, namesCommaSeparated,numbers);
	        return;
	    }
	    //farem les parelles
	    StringBuilder msg = new StringBuilder();
        for (int i=0; i<numbers.length; i++){
            msg.append(nameslist[i]+"="+dfX_5.format(numbers[i]));
            if (oneline){
                msg.append(" ");
            }else{
                msg.append("\n");
            }
         }
        printmsg(level,msg.toString().trim());
	}
	
    //prints two lines, one with the names, one with the values
	//if oneline=false, names i one line, numbers at the other
    public void writeNameNums(String level, boolean oneline, String names, double... numbers){
        StringBuilder msg = new StringBuilder();
        if (!oneline){
            printmsg(level,names.trim());
        }else{
            msg.append(names);
            msg.append(" = ");
        }
        for (int i=0; i<numbers.length; i++){
            msg.append(dfX_5.format(numbers[i]));
            msg.append(" ");
        }
        printmsg(level,msg.toString().trim());
    }
    
	//prints a list of floats (no names)
	public void writeFloats(String level, double... numbers){
	    StringBuilder msg = new StringBuilder();
	    for (int i=0; i<numbers.length; i++){
	        msg.append(dfX_5.format(numbers[i]));
	        msg.append(" ");
	    }
	    printmsg(level,msg.toString().trim());
	}

	
//******************* FORMATTER CLASSES 
	
	public static class FormatterDEBUG extends Formatter {
		 
	    @Override
	    public String format(LogRecord record) {
	    	String dt = fHora.format(new Date(record.getMillis()));
	        return dt+" "
  	        		+record.getThreadID()+" "
//	        		+record.getClass()+":"
//	        		+record.getSourceClassName()+":"
//	                +record.getSourceMethodName()+" ["
  	        		+record.getLoggerName()+" ["
	                +record.getLevel()+"] "
	                +record.getMessage()+"\n";
	    }
	 
	}
	
    public static class FormatterUSER extends Formatter {
        
        @Override
        public String format(LogRecord record) {
            String dt = fHora.format(new Date(record.getMillis()));
            return dt+" ["
                    +record.getLevel()+"] "
                    +record.getMessage()+"\n";
        }
     
    }	
	
}