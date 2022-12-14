/**
 * FileUtils
 * 
 */
package com.vava33.jutils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.math3.util.FastMath;


/**
 * Class with static methods regarding file handling and String handling. 
 * Some of them require the use of a custom textArea to output messages.
 * 
 * @author ovallcorba
 * 
 */
public final class FileUtils {

    /** The decimal format #0.0 */
    public static DecimalFormat dfX_0 = new DecimalFormat("#0");
    
    /** The decimal format #0.0 */
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
    
    /** The currentlocale. */
    public static Locale currentlocale = Locale.ROOT;
    
    /** The lowercases. */
    private static char[] lowercases = { '\000', '\001', '\002', '\003',
            '\004', '\005', '\006', '\007', '\010', '\011', '\012', '\013',
            '\014', '\015', '\016', '\017', '\020', '\021', '\022', '\023',
            '\024', '\025', '\026', '\027', '\030', '\031', '\032', '\033',
            '\034', '\035', '\036', '\037', '\040', '\041', '\042', '\043',
            '\044', '\045', '\046', '\047', '\050', '\051', '\052', '\053',
            '\054', '\055', '\056', '\057', '\060', '\061', '\062', '\063',
            '\064', '\065', '\066', '\067', '\070', '\071', '\072', '\073',
            '\074', '\075', '\076', '\077', '\100', '\141', '\142', '\143',
            '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153',
            '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163',
            '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\133',
            '\134', '\135', '\136', '\137', '\140', '\141', '\142', '\143',
            '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153',
            '\154', '\155', '\156', '\157', '\160', '\161', '\162', '\163',
            '\164', '\165', '\166', '\167', '\170', '\171', '\172', '\173',
            '\174', '\175', '\176', '\177' };

    /** The Decimal Format Symbols for current Locale. */
    private static DecimalFormatSymbols mySymbols = new DecimalFormatSymbols(
            FileUtils.defaultLocale);
    
    /** The Operating System. */
    private static String os = "win";

    /** The separator character. */
    public static final String fileSeparator = System.getProperty("file.separator");
    public static final String lineSeparator = System.getProperty("line.separator");
    public static final String userDir = System.getProperty("user.dir");
    
    /** time stamps **/
    public static SimpleDateFormat fHora = new SimpleDateFormat("[HH:mm]");
    public static final SimpleDateFormat fDiaHora = new SimpleDateFormat("[yyyy-MM-dd 'at' HH:mm]");
    
    private static String[] charsets = {"","UTF-8","ISO8859-1","Windows-1251","Shift JIS","Windows-1252"};

    
    // M???tode que afegeix un car???cter enmig d'un CharArray
    /**
     * Adds to char array.
     *
     * @param original the original
     * @param index the index
     * @param newChar the new char
     * @return the char[]
     */
    public static char[] addToCharArray(char[] original, int index, char newChar) {
        char[] resultat = new char[original.length + 1];
        for (int i = 0; i < resultat.length; i++) {
            if (i == index) {
                resultat[index] = newChar;
            }
            if (i < index) {
                resultat[i] = original[i];
            }
            if (i > index) {
                resultat[i] = original[i - 1];
            }
        }
        return resultat;
    }

    /**
     * Unsigned Byte[1] to Integer
     *
     * @param b the byte array
     * @return the int
     */
    public static int B1toInt_unsigned(byte b) {
        int result = (0xFF & b);
        return result;
    }

    /**
     * Unsigned Byte[2] to integer (little endian convention)
     *
     * @param b the byte array
     * @return the int
     */
    public static int B2toInt_LE_unsigned(byte[] b) {
        int result = (((0xFF & b[1]) << 8) | (0xFF & b[0]));
        return result;
    }

    /**
     * Byte[4] to float.
     *
     * @param b the byte array
     * @return the float
     */
    public static float B4toFloat(byte[] b) {
        int asInt = (b[0] & 0xFF) | ((b[1] & 0xFF) << 8)
                | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);
        return Float.intBitsToFloat(asInt);
    }

    /**
     * Byte[4] to int (LITTLE ENDIAN).
     *
     * @param b the byte array
     * @return the int
     */
    public static int B4toInt_LE_signed(byte[] b) {
        return (b[0] & 0xFF) | ((b[1] & 0xFF) << 8)
                | ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);
    }
    
    //general 
    public static int ByteArrayToInt_signed(byte[] b,boolean littleEndian) {
    	
    	int x = 0;
    	long xl = 0;

    	switch (b.length){
    		case 2:
    	    	if (littleEndian) {
    	    		x = java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort();	
    	    	}else {
    	    		x = java.nio.ByteBuffer.wrap(b).getShort();	
    	    	}    			
    			break;
    		case 4:
    	    	if (littleEndian) {
    	    		x = java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();	
    	    	}else {
    	    		x = java.nio.ByteBuffer.wrap(b).getInt();	
    	    	}
    			break;
    		case 8:
    	    	if (littleEndian) {
    	    		xl = java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();	
    	    	}else {
    	    		xl = java.nio.ByteBuffer.wrap(b).getLong();	
    	    	}
    	    	x = (int) xl;
    			break;
    	}
    	
    	return x;
    }
    
    public static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
        
    /**
     * Canvi extensio. Si no en t?? l'afegeix.
     *
     * @param f the file
     * @param newExt the new ext
     * @return the file with new ext
     */
    public static File canviExtensio(File f, String newExt) {
        String path = f.toString(); // cami complert al fitxer
        String fname = f.getName(); // nom del fitxer (amb extensio si en t??)
        path = path.substring(0, path.length() - fname.length()); // directori
                                                                  // del fitxer

        int i = fname.lastIndexOf('.');
        if (i > 0) { // si t?? extensio
            int midaExt = fname.length() - i;
            fname = fname.substring(0, fname.length() - midaExt);
            if (newExt.equals("")) {// volem treure l'extensio, la nova es sense
                                    // ext
                f = new File(path + fname);
            } else {// afegim l'extensio normal
                f = new File(path + fname + "." + newExt);
            }
        } else { // no t?? extensio
            if (newExt.equals("")) {// volem treure l'extensio, la nova es sense
                                    // ext
                f = new File(path + fname);
            } else {// afegim l'extensio normal
                f = new File(path + fname + "." + newExt);
            }
        }
        return f;
    }

    /**
     * Canvi nom fitxer. Deixa l'extensi?? que tenia.
     *
     * @param f the file
     * @param nouNom the nou nom
     * @return the file amb nou nom
     */
    public static File canviNomFitxer(File f, String nouNom) {
        String path = f.toString(); // cami complert al fitxer
        String fname = f.getName(); // nom del fitxer (amb extensio si en te)
        path = path.substring(0, path.length() - fname.length()); // directori
                                                                  // del fitxer

        int i = fname.lastIndexOf('.');
        if (i > 0) { // si te extensio
            String ext = fname.substring(i + 1);
            f = new File(path + nouNom + "." + ext);
        } else { // no t?? extensio
            f = new File(path + nouNom);
        }
        return f;
    }

    /**
     * Confirm dialog.
     *
     * @param msg the msg
     * @param title the title
     * @return true, if successful
     */
    public static boolean confirmDialog(String msg, String title) {
        int n = JOptionPane.showConfirmDialog(null, msg, title,
                JOptionPane.YES_NO_OPTION);
        if ((n == JOptionPane.NO_OPTION) || (n == JOptionPane.CLOSED_OPTION)) {
            return false;
        }
        return true;
    }


    /**
     * Copy file. Metode que copia un fitxer, torna 0 si correcte o -1 si no 
     * ha anat be. Si borrar es true borra el fitxer origen. Guarda un backup
     * (.BAK) del fitxer dest?? si existeix i es sobreescriu (ho pregunta)
     *
     * @param srFile the source file
     * @param dtFile the destination file
     * @param borrar if the source file should be deleted after copying
     * @param outputWin the LogJTextArea to show output messages
     * @return 0 if correct
     */
    public static int copyFile(File srFile, File dtFile, boolean borrar,
            LogJTextArea outputWin) {
        int ret = -1;
        try {
            if (srFile.equals(dtFile)) {
                return 1;
            }

            if (dtFile.exists()) {
                // preguntem si sobreescriure
                int n = JOptionPane.showConfirmDialog(null, "Overwrite "
                        + dtFile.getName() + "?", "File exists",
                        JOptionPane.YES_NO_OPTION);
                if ((n == JOptionPane.NO_OPTION)
                        || (n == JOptionPane.CLOSED_OPTION)) {
                    return -1;
                }
                // fem backup de l'anterior i sobreescribim (el backup
                // sobreescriu anteriors backups)
                File bakFile = new File(FileUtils.getFNameNoExt(dtFile)
                        + ".BAK");
                if (bakFile.exists()) {
                    bakFile.delete();
                }
                FileUtils.copyFile(dtFile, bakFile, false, outputWin);
                dtFile.delete();
            }

            InputStream in = new FileInputStream(srFile);

            OutputStream out = new FileOutputStream(dtFile);

            byte[] buf = new byte[512];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            if (dtFile.exists()&&outputWin!=null) {
                String c1 = "\u250C"; // corner sup-esquerra
                String c2 = "\u2514"; // corner inf-esquerra
                // String c3="\u2500"; // guio llarg
                outputWin.afegirText(true, true, c1 + " " + srFile.toString());
                outputWin.afegirText(true, true,
                            c2 + "> copied to: " + dtFile.toString());
            }
            ret = 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        if (ret == 0) {
            if (borrar && dtFile.exists()) {
                srFile.delete();
            }
        }
        return ret;
    }
    
    public static int copyFile(File srFile, File dtFile, boolean borrar) {
    	return copyFile(srFile, dtFile, borrar,null);
    }

    /**
     * Detect Operating System, only WIN i LIN. Si no es cap
     *  dels dos es tractara com a WINDOWS per defecte
     * 
     * Gets the os.
     *
     * @return the os
     */
    public static String getOS() {
        //mirem versio java
        String javav = System.getProperty("java.version");
        // mirem sistema operatiu:
        String ops = System.getProperty("os.name").toLowerCase();
        if (ops.indexOf("win") >= 0) {
            System.out.println(String.format("Running on Windows [java %s]",javav));
            FileUtils.setOS("win");
        } else if (os.indexOf("mac") >= 0) {
            System.out.println(String.format("Running on Mac [java %s]",javav));
        	FileUtils.setOS("mac");
        } else if ((ops.indexOf("nix") >= 0) || (ops.indexOf("nux") >= 0)
                || (ops.indexOf("aix") > 0)) {
            System.out.println(String.format("Running on Linux [java %s]",javav));
            FileUtils.setOS("lin");
        } else {
            System.out.println("Your OS is not supported!!");
        }
        return FileUtils.os;
    }

    /**
     * true if senar.
     *
     * @param iNumero the i numero
     * @return true, if successful
     */
    public static boolean esSenar(int iNumero) {
        if ((iNumero % 2) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fchooser. Obra un File Chooser al directori especificat amb els filtres
     * especificats i retorna el fitxer seleccionat o null. TOTES LES OPCIONS
     *
     * @param startDir the start dir
     * @param filter the FileNameExtensionFilter array
     * @param defaultFilterIndex
     * @param multipleSelection allow multiple selection?
     * @param save is it a save dialog?
     * @param askowrite ask if overwrite?
     * @param forcedExtension null to not force any, string of the extension otherwise
     * @param title null for default
     * @return the opened file
     */
    private static File fchooser(Component parent, File startDir, FileNameExtensionFilter[] filter, int defaultFilterIndex, boolean save, boolean askowrite, String forcedExtension, String title) {
        // Creem un filechooser per seleccionar el fitxer obert
        JFileChooser fileChooser = new JFileChooser();
        if(startDir==null){
            startDir=new File(System.getProperty("user.dir"));
        }
        fileChooser.setCurrentDirectory(startDir); // directori inicial
        if (filter != null) {
            for (int i = 0; i < filter.length; i++) {
                fileChooser.addChoosableFileFilter(filter[i]);
            }
            fileChooser.setFileFilter(filter[defaultFilterIndex]);
        }
        if (title!=null) {
            fileChooser.setDialogTitle(title);
        }
        int selection;
        if(save){
            selection = fileChooser.showSaveDialog(parent);
        }else{
            selection = fileChooser.showOpenDialog(parent);   
        }
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            //aleshores si es SAVE cal potser for??ar extensi?? i mirar si existeix
            if (save) {
            	if ((filter != null)&&(FileUtils.getExtension(f).isEmpty())) {
            		FileNameExtensionFilter selfilt = (FileNameExtensionFilter)fileChooser.getFileFilter();
            		String extension = selfilt.getExtensions()[0];
            		f = FileUtils.canviExtensio(f, extension);
            	}
                if (forcedExtension!=null) {
                    f = FileUtils.canviExtensio(f, forcedExtension);
                }
                if (askowrite) {
                    if(f.exists()) {
                        int actionDialog = JOptionPane.showConfirmDialog(parent,"Replace "+f.getName()+"?");
                        if (actionDialog == JOptionPane.NO_OPTION)return null;
                    }
                }
            }
            return f;
        } else {
            return null;
        }
    }

    /**
     * fchooserMultiple. Obra un File Chooser al directori especificat amb els filtres
     * especificats per llegir multiples fitxers
     *
     * @param startDir the start dir
     * @param filter the FileNameExtensionFilter array
     * @param defaultFilterIndex
     * @param title null for default
     * @return the opened file
     */
    public static File[] fchooserMultiple(Component parent, File startDir, FileNameExtensionFilter[] filter, int defaultFilterIndex, String title) {
        // Creem un filechooser per seleccionar el fitxer obert
        JFileChooser fileChooser = new JFileChooser();
        if(startDir==null){
            startDir=new File(System.getProperty("user.dir"));
        }
        fileChooser.setCurrentDirectory(startDir); // directori inicial: el del
        if (filter != null) {
            for (int i = 0; i < filter.length; i++) {
                fileChooser.addChoosableFileFilter(filter[i]);
            }
            fileChooser.setFileFilter(filter[defaultFilterIndex]);
        }
        if (title!=null) {
            fileChooser.setDialogTitle(title);
        }
        int selection;
        fileChooser.setMultiSelectionEnabled(true);
        selection = fileChooser.showOpenDialog(parent);
        
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFiles();
        } else {
            return null;
        }
    }
    
    /**
     * Fchooser. Obra un File Chooser al directori especificat amb els filtres
     * especificats i retorna el fitxer seleccionat o null. Igual que anterior
     * per??? aquest no demana si sobreescriure el fitxer (interpreta que es far???
     * posteriorment, ???til per si es vol fer append).
     *
     * @param startDir the start dir
     * @param filter the FileNameExtensionFilter array
     * @return the opened file
     */
    public static File fchooserSaveNoAsk(Component parent, File startDir, FileNameExtensionFilter[] filter, String forceExt) {
        return fchooser(parent, startDir, filter, 0, true, false, forceExt,null);
    }
    
    public static File fchooserSaveAsk(Component parent, File startDir, FileNameExtensionFilter[] filter, String forceExt) {
        return fchooser(parent, startDir, filter, 0, true, true, forceExt,null);
    }
    
    public static File fchooserSaveAsk(Component parent, File startDir, FileNameExtensionFilter[] filter, String forceExt, String title) {
        return fchooser(parent, startDir, filter, 0, true, true, forceExt,title);
    }
    
    public static File fchooserOpen(Component parent, File startDir, FileNameExtensionFilter[] filter, int defaultFilterIndex) {
        return fchooser(parent, startDir, filter, defaultFilterIndex, false, false, null,null);
    }
    
    public static File fchooserOpenDir(Component parent, File startDir, String title) {
        // Creem un filechooser per seleccionar el fitxer obert
        JFileChooser fileChooser = new JFileChooser();
        if(startDir==null){
            startDir=new File(System.getProperty("user.dir"));
        }
        fileChooser.setCurrentDirectory(startDir);

        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int selection;
        selection = fileChooser.showOpenDialog(parent);   
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            return f;
        } else {
            return null;
        }
    }
    
    public static boolean YesNoDialog(Component parent, String question){
        int actionDialog = JOptionPane.showConfirmDialog(parent,
                question,"Select an option",JOptionPane.YES_NO_OPTION);
        if (actionDialog == JOptionPane.YES_OPTION)return true;
        return false;
    }
    
    public static boolean YesNoDialog(Component parent, String question, String title){
        int actionDialog = JOptionPane.showConfirmDialog(parent,
                question,title,JOptionPane.YES_NO_OPTION);
        if (actionDialog == JOptionPane.YES_OPTION)return true;
        return false;
    }
    
    public static int YesNoCancelDialog(Component parent, String question){
        int actionDialog = JOptionPane.showConfirmDialog(parent,
                question);
        if (actionDialog == JOptionPane.YES_OPTION)return 1;
        if (actionDialog == JOptionPane.NO_OPTION)return 0;
        return -1;
    }
    
    
    public static void InfoDialog(Component parent, String message, String title){
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static Double DialogAskForDouble(Component parent, String label, String title, String defaultValue) {
    	String s = (String)JOptionPane.showInputDialog(
    			parent,
    			label,
    			title,
    			JOptionPane.PLAIN_MESSAGE,
    			null,
    			null,
    			defaultValue);
    	Double dval = null;;
    	if ((s != null) && (s.length() > 0)) {
    		try{
    			dval = Double.parseDouble(s);
    		}catch(Exception ex){
    			return null;
    		}
    	}
    	return dval;
    }
    
    public static String DialogAskForString(Component parent, String label, String title, String defaultValue) {
        String s = (String)JOptionPane.showInputDialog(
                parent,
                label,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultValue);
        if ((s != null) && (s.length() > 0)) {
            return s;
        }
        return null;
    }
    
    public static double DialogAskForPositiveDouble(Component parent, String label, String title, String defaultValue) {
    	String s = (String)JOptionPane.showInputDialog(
    			parent,
    			label,
    			title,
    			JOptionPane.PLAIN_MESSAGE,
    			null,
    			null,
    			defaultValue);
    	double dval = -1;
    	if ((s != null) && (s.length() > 0)) {
    		try{
    			dval = Double.parseDouble(s);
    		}catch(Exception ex){
    			return -1;
    		}
    	}
    	return dval;
    }
    
    /**
     * Gets the extension of a file.
     *
     * @param f the file
     * @return the extension
     */
    public static String getExtension(File f) {
        String ext = "";
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Gets the extension of a file
     *
     * @param s the string path of the file.
     * @return the extension
     */
    public static String getExtension(String s) {
        String ext = "";
        int i = s.lastIndexOf('.');

        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Gets the f name no ext.
     *
     * @param fn the fn
     * @return the f name no ext
     */
    public static String getFNameNoExt(File fn) {
        String ext = FileUtils.getExtension(fn);
        if (ext.length() > 0) {
            return fn.getPath().substring(0,
                    fn.getPath().length() - ext.length() - 1);
        } else {
            return fn.getPath();
        }
    }

    /**
     * Gets the f name no ext.
     *
     * @param fn the fn
     * @return the f name no ext
     */
    public static String getFNameNoExt(String fn) {
        String ext = FileUtils.getExtension(fn);
        if (ext.length() > 0) {
            return fn.substring(0, fn.length() - ext.length() - 1);
        } else {
            return fn;
        }
    }

    /**
     * Moure fitxer.
     *
     * @param fitxerOrigen the fitxer origen
     * @param dirDesti the dir desti
     * @param nouNom the nou nom
     * @return true, if successful
     */
    public static boolean moureFitxer(File fitxerOrigen, String dirDesti,
            String nouNom) {
        // Destination directory
        File dir = new File(dirDesti);

        // Move file to new directory
        boolean success = fitxerOrigen.renameTo(new File(dir, nouNom));
        if (!success) {
            return false;
        }
        return true;
    }

    /**
     * Random number.
     *
     * @param min the min
     * @param max the max
     * @return the int
     */
    public static int randomNumber(int min, int max) {
        return min + (new Random()).nextInt(max - min);
    }

    // treu els parentesis d'una String
    /**
     * Removes the brk.
     *
     * @param s the s
     * @return the string
     */
    public static String removeBrk(String s) {
        return s.replaceAll("\\(.*?\\)", "");
    }

    // M???tode que elimina un car???cter d'enmig d'un CharArray
    /**
     * Removes the from char array.
     *
     * @param original the original
     * @param index the index
     * @return the char[]
     */
    public static char[] removeFromCharArray(char[] original, int index) {
        char[] resultat = new char[original.length - 1];
        for (int i = 0; i < resultat.length; i++) {
            if (i < index) {
                resultat[i] = original[i];
            }
            if (i >= index) {
                resultat[i] = original[i + 1];
            }
        }
        return resultat;
    }

    /**
     * Sets the locale. -- ROOT by default
     */
    public static void setLocale(Locale loc) {
        // To TEST:
        // System.out.println(mySymbols.getDecimalSeparator());
        // mySymbols.setDecimalSeparator('.');
        // System.out.println(mySymbols.getDecimalSeparator());
        // System.out.println(mySymbols.getGroupingSeparator());
        // System.out.println(dfX_3.format(12345678.009921));
        // dfX_3.setDecimalFormatSymbols(mySymbols);
        // System.out.println(dfX_3.format(12345678.009921));
        
    	if (loc==null)loc=Locale.ROOT;
    	
        Locale.setDefault(loc);

        FileUtils.mySymbols = new DecimalFormatSymbols(loc);
        FileUtils.mySymbols.setDecimalSeparator('.');
        FileUtils.dfX_1.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_2.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_3.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_4.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_5.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_6.setDecimalFormatSymbols(FileUtils.mySymbols);
        FileUtils.dfX_1.setGroupingUsed(false);
        FileUtils.dfX_2.setGroupingUsed(false);
        FileUtils.dfX_3.setGroupingUsed(false);
        FileUtils.dfX_4.setGroupingUsed(false);
        FileUtils.dfX_5.setGroupingUsed(false);
        FileUtils.dfX_6.setGroupingUsed(false);

        System.out.println(Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry());
        
    }

    /**
     * Sets the os.
     *
     * @param op the new os
     */
    private static void setOS(String op) {
        FileUtils.os = op;
    }

    /**
     * Starts with ignore case.
     *
     * @param s the s
     * @param w the w
     * @return true, if successful
     */
    public static boolean startsWithIgnoreCase(String s, String w) {
        if (w == null) {
            return true;
        }

        if ((s == null) || (s.length() < w.length())) {
            return false;
        }

        for (int i = 0; i < w.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = w.charAt(i);
            if (c1 != c2) {
                if (c1 <= 127) {
                    c1 = FileUtils.lowercases[c1];
                }
                if (c2 <= 127) {
                    c2 = FileUtils.lowercases[c2];
                }
                if (c1 != c2) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * To bytes2.format little endian
     *
     * @param i the i
     * @return the byte[]
     */
    public static byte[] toBytes2(int i) {
        byte[] result = new byte[2];
        result[1] = (byte) (i >> 8);
        result[0] = (byte) (i >> 0);
        return result;
    }

    /**
     * X float string to array.
     * converteix un string amb numeros xxx.xxxxx separats per un o mes espais
     * en un array que cont?? aquests numeros (tamb?? agafar?? enters de fins a
     * 8 xifres)
     *
     * @param linia the linia
     * @return the string[]
     */
    public static String[] xFloatStringToArray(String linia) {
        Scanner scanner = new Scanner(linia);
        Pattern p = Pattern.compile("\\d{1,3}.\\d{1,5}|\\d{1,8}");
        boolean fiLlista = false;
        String[] array = new String[20];
        int i = 0;

        while ((!fiLlista) && (i < 20)) {
            array[i] = scanner.findWithinHorizon(p, 0);
            if (array[i] != null) {
                i++;
            } else {
                fiLlista = true;
            }
        }
        scanner.close();
        return array;
    }

    /**
     * X float string to array2.
     * converteix un string amb numeros xxx.xxxxx separats per un o mes espais
     * en un array que cont?? aquests numeros INCLOU EXPONENCIALS
     * p = Pattern.compile("\\(P\\)|\\(C\\)|\\(I\\)|\\(F\\)|\\(R\\)"); | = OR
     * @param linia the linia
     * @return the string[]
     */
    public static String[] xFloatStringToArray2(String linia) {
        Scanner scanner = new Scanner(linia);
        Pattern p = Pattern
                .compile("\\d{1,3}.\\d{2,5}[Ee]\\p{Punct}\\d{2}|\\d{1,3}.\\d{1,5}");
        boolean fiLlista = false;
        String[] array = new String[20];
        int i = 0;

        while ((!fiLlista) && (i < 20)) {
            array[i] = scanner.findWithinHorizon(p, 0);
            if (array[i] != null) {
                i++;
            } else {
                fiLlista = true;
            }
        }
        scanner.close();
        return array;
    }
    
    public static double[] xFloatStringArrayToDoubleArray(String[] sdoubles) {
    	double[] doubles = new double[sdoubles.length];
    	int i=0;
    	try {
        	for(String sd: sdoubles) {
        		doubles[i]=Double.parseDouble(sd);
        	}
    	}catch(NumberFormatException ex) {
    		throw(ex);
    	}
    	return doubles;
    }
    
    /**
     * Green implementation of regionMatches.
     *
     * @param cs the {@code CharSequence} to be processed
     * @param ignoreCase whether or not to be case insensitive
     * @param thisStart the index to start on the {@code cs} CharSequence
     * @param substring the {@code CharSequence} to be looked for
     * @param start the index to start on the {@code substring} CharSequence
     * @param length character length of the region
     * @return whether the region matched
     */
    public static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
            final CharSequence substring, final int start, final int length)    {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            int index1 = thisStart;
            int index2 = start;
            int tmpLen = length;

            while (tmpLen-- > 0) {
                char c1 = cs.charAt(index1++);
                char c2 = substring.charAt(index2++);

                if (c1 == c2) {
                    continue;
                }

                if (!ignoreCase) {
                    return false;
                }

                // The same check as in String.regionMatches():
                if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                        && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                    return false;
                }
            }

            return true;
        }
    }
    
    /**
     * Checks if CharSequence contains a search CharSequence irrespective of case,
     */
    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration,
            String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static void openURL(String url) {

    	try {
    		if(Desktop.isDesktopSupported()){
    			Desktop desktop = Desktop.getDesktop();
    			desktop.browse(new URI(url));
    		}else {
    			String cOS = getOS();
    			if (cOS.equalsIgnoreCase("lin")) {
    				Runtime runtime = Runtime.getRuntime();
    				runtime.exec("xdg-open " + url);
    			}
    			if (cOS.equalsIgnoreCase("win")) {
    				Runtime rt = Runtime.getRuntime();
    				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
    			}
    			if (cOS.equalsIgnoreCase("mac")) {
    				Runtime rt = Runtime.getRuntime();
    				rt.exec("open " + url);
    			}
    		}
    	}catch(Exception e) {
    		System.out.println("Error opening url");
    	}
    }

    public static String getCharLine(char c, int lineWidth){
    	StringBuilder sb = new StringBuilder(lineWidth);
    	for(int i=0; i<lineWidth;i++){
    		sb.append(c);	
    	}
    	return sb.toString();
    }
    
    public static String getCenteredString(String toCenter, int lineWidth) {
        if (toCenter.length()>=lineWidth)return toCenter;
        int len = toCenter.length();
        int spacesToAdd = (int) ((lineWidth-len)/2.f);
        return getCharLine(' ',spacesToAdd).concat(toCenter);
    }
    
    public static Color parseColorName(String name){
        name = name.toLowerCase();
        if (FileUtils.containsIgnoreCase(name, "darker1")) {
            return parseColorName(name.replace("darker1", ""),1,0);
        }
        if (FileUtils.containsIgnoreCase(name, "darker2")) {
            return parseColorName(name.replace("darker2", ""),2,0);
        }
        if (FileUtils.containsIgnoreCase(name, "darker3")) {
            return parseColorName(name.replace("darker3", ""),3,0);
        }

        if (FileUtils.containsIgnoreCase(name, "brighter1")) {
            return parseColorName(name.replace("brighter1", ""),0,1);
        }
        if (FileUtils.containsIgnoreCase(name, "brighter2")) {
            return parseColorName(name.replace("brighter2", ""),0,2);
        }
        if (FileUtils.containsIgnoreCase(name, "brighter3")) {
            return parseColorName(name.replace("brighter3", ""),0,3);
        }

        return parseColorName(name,0,0);
    }
    
    private static Color parseColorName(String name,int nDarkers,int nBrighters){
        Color c = null;
        if (name.trim().equalsIgnoreCase("black")) c=Color.black;
        if (name.trim().equalsIgnoreCase("blue")) c= Color.blue;
        if (name.trim().equalsIgnoreCase("red")) c= Color.red;
        if (name.trim().equalsIgnoreCase("green")) c= Color.green;
        if (name.trim().equalsIgnoreCase("cyan")) c= Color.cyan;
        if (name.trim().equalsIgnoreCase("yellow")) c= Color.yellow;
        if (name.trim().equalsIgnoreCase("magenta")) c= Color.magenta;
        if (name.trim().equalsIgnoreCase("orange")) c= Color.orange;
        if (name.trim().equalsIgnoreCase("pink")) c= Color.pink;
        if (name.trim().equalsIgnoreCase("white")) c= Color.white;
        if (name.trim().equalsIgnoreCase("gray")) c= Color.gray;
        if (name.trim().equalsIgnoreCase("violet")) c= new Color(-6736897);
        if (c==null) {
            try {
                c= Color.decode(name);
            }catch(Exception ex) {
                //do nothing
            }
        }
        if (c!=null) {
            for (int i=0;i<nDarkers;i++) {
                c=c.darker();
            }
            for (int i=0;i<nBrighters;i++) {
                c=c.brighter();
            }
        }
        if (c!=null)return c;
        return Color.black;
    }
    
    
    public static String getColorName(Color c){
        if (c==Color.black) return "black";
        if (c==Color.white) return "white";
        if (c==Color.green) return "green";
        if (c==Color.red) return "red";
        if (c==Color.cyan) return "cyan";
        if (c==Color.yellow) return "yellow";
        if (c==Color.magenta) return "magenta";
        if (c==Color.orange) return "orange";
        if (c==Color.pink) return "pink";
        if (c==Color.blue) return "blue";
        if (c.getRGB()==-6736897) return "violet";

        if (c==Color.green.darker()) return "greenDarker1";
        if (c==Color.red.darker()) return "redDarker1";
        if (c==Color.cyan.darker()) return "cyanDarker1";
        if (c==Color.yellow.darker()) return "yellowDarker1";
        if (c==Color.magenta.darker()) return "magentaDarker1";
        if (c==Color.orange.darker()) return "orangeDarker1";
        if (c==Color.pink.darker()) return "pinkDarker1";
        if (c==Color.blue.darker()) return "blueDarker1";
        
        if (c==Color.green.darker().darker()) return "greenDarker2";
        if (c==Color.red.darker().darker()) return "redDarker2";
        if (c==Color.cyan.darker().darker()) return "cyanDarker2";
        if (c==Color.yellow.darker().darker()) return "yellowDarker2";
        if (c==Color.magenta.darker().darker()) return "magentaDarker2";
        if (c==Color.orange.darker().darker()) return "orangeDarker2";
        if (c==Color.pink.darker().darker()) return "pinkDarker2";
        if (c==Color.blue.darker().darker()) return "blueDarker2";
        
        if (c==Color.green.darker().darker().darker()) return "greenDarker3";
        if (c==Color.red.darker().darker().darker()) return "redDarker3";
        if (c==Color.cyan.darker().darker().darker()) return "cyanDarker3";
        if (c==Color.yellow.darker().darker().darker()) return "yellowDarker3";
        if (c==Color.magenta.darker().darker().darker()) return "magentaDarker3";
        if (c==Color.orange.darker().darker().darker()) return "orangeDarker3";
        if (c==Color.pink.darker().darker().darker()) return "pinkDarker3";
        if (c==Color.blue.darker().darker().darker()) return "blueDarker3";
        
        return Integer.toString(c.getRGB());
    }
    
    public static Color getColor(int rgb) {
    	return new Color(rgb);
    }
    public static Color getComplementary(Color c) {
    	float[] hsbvals = new float[3];
    	hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbvals);
    	float newhue = hsbvals[0] + 0.5f;
    	if (newhue>1) {
    		newhue = 1-(newhue%1);	
    	}
    	return new Color(Color.HSBtoRGB(newhue, hsbvals[1], hsbvals[2]));
    }
    
    
    public static String getEncodingToUse(File f) {
      //FIRST CHECK ENCODING
        Scanner sf=null;
        charsets[0] = Charset.defaultCharset().name();
        int charsetToUse = 0;
        try {
        	for (int i=0;i<charsets.length;i++) {
        		sf = new Scanner(f,charsets[i]);
        		if (sf.hasNextLine()) {
        			charsetToUse=i;
        			break;
        		}
        	}
        } catch (Exception e1) {
        	e1.printStackTrace();
        }finally {
        	if (sf!=null)sf.close();
        }

        return charsets[charsetToUse];
    }
   
    public static String getStringTimeStamp(String simpleDateFormatStr){
        SimpleDateFormat fHora = new SimpleDateFormat(simpleDateFormatStr);
        return fHora.format(new Date());
    }
    
    //INCLUSIVE, per aixo el +1
    public static float[] arange(float ini, float fin, float step) {
        int size = FastMath.round((fin-ini)/step)+1;
        float[] ret = new float[size];
        for (int i=0; i<ret.length; i++) {
            ret[i] = ini + i*step; 
        }
        if (ret.length==0) ret=new float[] {ini};
        return ret;
    }
    //INCLUSIVE
    public static int[] range(int ini, int fin, int step) {
        int size = (fin-ini)/step+1;
        int[] ret = new int[size];
        for (int i=0; i<ret.length; i++) {
            ret[i] = ini + i*step; 
        }
        if (ret.length==0) ret=new int[] {ini};
        return ret;
    }

    public static float[] arange(double aMin, double aMax, double aStep) {
        return arange((float)aMin, (float)aMax, (float)aStep);
    }
}
