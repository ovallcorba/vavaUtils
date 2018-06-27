package com.vava33.jutils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

/**
 * @author ovallcorba
 *
 */
public class VavaLogger {

    /** THE LOGGER */
	public Logger LOG;
	private String levelString;
	private boolean logConsole, logFile, logTA;
	
	/** THE HANDLERS **/
	private static ConsoleHandler consoleH;
	private static FileHandler fileH;
	private static TextAreaHandler txtAreaH;
	
	public static String loggingFilePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log.txt";
	private static LogJTextArea logTextArea;

	private static List<VavaLogger> loggers = new ArrayList<VavaLogger>();
    
	public VavaLogger(String name, boolean logConsole, boolean logFile, boolean loggingTA){
		
	    LOG = Logger.getLogger(name);
	    
	    if (LOG.getHandlers().length>0)return; //vol dir que ja existeix el log
	    
	    this.logConsole=logConsole;
	    this.logFile=logFile;
	    this.logTA=loggingTA;
	    
	    loggers.add(this);
	    
	    //afegim handlers i propietats si s'escau
        LOG.setUseParentHandlers(false);
        if (logConsole)addConsoleHandler();
        if (logFile) addFileHandler();
        if (loggingTA) addTextAreaHandler();
	}
    
    
	//crea un logger amb el nom donat amb un handler per la consola, si usermode=true nomes mostra [level] msg
	public VavaLogger(String name){//, boolean debugmode){
		  this(name,true,false,false);
	}
	
	//default logger
	public VavaLogger(){
	    this("VAVALOGGER");
	}
	
	private void addConsoleHandler(){
		if (consoleH==null) {
			consoleH= new ConsoleHandler();
		}
		LOG.addHandler(consoleH);
		       	
	}
	
	private void addFileHandler(){
		if (fileH==null) {
			try {
				 fileH = new FileHandler(new File(loggingFilePath).getAbsolutePath(),true);
				} catch (Exception e) {
					System.out.println("Error initializing File Logging");
					return;
				}
		}
		LOG.addHandler(fileH);
	}
	
	private void addTextAreaHandler(){
		if (logTextArea!=null) {
			if (txtAreaH==null) {
			    txtAreaH = new TextAreaHandler(logTextArea);
			}
		    LOG.addHandler(txtAreaH);			
		}
	}
	
	public static void setTArea(LogJTextArea ta) {
		logTextArea = ta;
		txtAreaH = new TextAreaHandler(logTextArea);
		updateLOGS();
	}
	
	private static void updateLOGS() {
		Iterator<VavaLogger> itrL = loggers.iterator();
		while (itrL.hasNext()) {
			VavaLogger l = itrL.next();
			if(!l.logTA)continue;
			boolean hasTextArea = false;
	        for (Handler handler : l.LOG.getHandlers()) {
	            if (handler instanceof TextAreaHandler){
	          	    hasTextArea = true;
	          	    handler = txtAreaH;
	          	    break;
	            }
	          }
	        if (!hasTextArea)l.LOG.addHandler(txtAreaH);
	        l.setLogLevel(l.getLogLevelString());
		}
	}
	
	/**
	 * @return the loggingFilePath
	 */
	public static String getLoggingFilePath() {
		return loggingFilePath;
	}


	/**
	 * @param loggingFilePath the loggingFilePath to set
	 */
	public static void setLoggingFilePath(String loggingFilePath) {
		VavaLogger.loggingFilePath = loggingFilePath;
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
	
	public String getLogLevelString() {
	    return levelString;
	}

	public boolean isEnabled() {
	    if (LOG.getLevel()==Level.ALL) return true;
	    return false;
	}
    
	public void setLogLevel(String level){
        if (level.equalsIgnoreCase("config"))setCONFIG();
        if (level.equalsIgnoreCase("debug"))setCONFIG();
        if (level.equalsIgnoreCase("info"))setINFO();
        if (level.equalsIgnoreCase("warning"))setWARNING();
        if (level.equalsIgnoreCase("severe"))setSEVERE();
        if (level.equalsIgnoreCase("fine"))setFINE();
        levelString = level;
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
		StringBuilder sb = new StringBuilder();		
		if (this.logConsole) sb.append("Console logging ENABLED - level:"+levelString.toUpperCase()+"\n");
		if (this.logFile) sb.append("File logging ENABLED ("+loggingFilePath+") - level:"+levelString.toUpperCase()+"\n");
		if (this.logTA) sb.append("TextArea logging ENABLED - level:"+levelString.toUpperCase()+"\n");
		return sb.toString().trim();
 	}
	
// ************* WRITTING MESSAGES
	
	public void infoID(String s, String id_who_send_message){
		LOG.info("["+id_who_send_message+"] "+s);
	}
	public void info(String s){
		LOG.info(s);
	}
	public void infof(String format, Object... args) {
        LOG.info(String.format(format, args));
    }
	public void warning(String s){
		LOG.warning(s);
	}
	public void warningf(String format, Object... args){
        LOG.warning(String.format(format, args));
	}
	public void config(String s){
		LOG.config(s);
	}
	public void configf(String format, Object... args) {
	    LOG.config(String.format(format, args));
	}
	public void debug(String s){
	    LOG.config(s);
	}
	public void debugf(String format, Object... args) {
	    LOG.config(String.format(format, args));
	}
	   
    public void severe(String s){
        LOG.severe(s);
    }
	public void fine(String s){
	    LOG.fine(s);
	}
	public void finef(String format, Object... args) {
        LOG.fine(String.format(format, args));
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
            msg.append(nameslist[i]+"="+FileUtils.dfX_5.format(numbers[i]));
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
            msg.append(FileUtils.dfX_5.format(numbers[i]));
            msg.append(" ");
        }
        printmsg(level,msg.toString().trim());
    }
    
    public void writeNameNums(String level, boolean oneline, String names, int... numbers){
      StringBuilder msg = new StringBuilder();
      if (!oneline){
          printmsg(level,names.trim());
      }else{
          msg.append(names);
          msg.append(" = ");
      }
      for (int i=0; i<numbers.length; i++){
          msg.append(numbers[i]);
          msg.append(" ");
      }
      printmsg(level,msg.toString().trim());
  }
    
	//prints a list of floats (no names)
	public void writeFloats(String level, double... numbers){
	    StringBuilder msg = new StringBuilder();
	    for (int i=0; i<numbers.length; i++){
	        msg.append(FileUtils.dfX_5.format(numbers[i]));
	        msg.append(" ");
	    }
	    printmsg(level,msg.toString().trim());
	}

	
	
	
//******************* FORMATTER CLASSES 
	
	public static class FormatterDEBUG extends Formatter {
		 
	    @Override
	    public String format(LogRecord record) {
	    	String dt = FileUtils.fHora.format(new Date(record.getMillis()));
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
            String dt = FileUtils.fHora.format(new Date(record.getMillis()));
            return dt+" ["
                    +record.getLevel()+"] "
                    +record.getMessage()+"\n";
        }
     
    }
    
    
//HANDLER CLASSES
    public static class TextAreaHandler extends java.util.logging.Handler {

        private LogJTextArea tA;

        public TextAreaHandler(LogJTextArea txtAOut){
            super();
            this.tA=txtAOut;
        }
        
        @Override
        public void publish(final LogRecord record) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                	//no log of CONFIG or FINE
                	Level l = record.getLevel();
                	if (l==Level.CONFIG)return;
                	if (l==Level.FINE)return;
                	if(l==Level.INFO) {
                		tA.stat(record.getMessage());
                	}
                	if((l==Level.WARNING)||(l==Level.SEVERE)) {
                		tA.stat("["+record.getLevel()+"] "+record.getMessage());
                	}
                	
                }

            });
        }

        public LogJTextArea getTextArea() {
            return this.tA;
        }

        /* (non-Javadoc)
         * @see java.util.logging.Handler#close()
         */
        @Override
        public void close() throws SecurityException {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see java.util.logging.Handler#flush()
         */
        @Override
        public void flush() {
            // TODO Auto-generated method stub
            
        }
    }
	
}