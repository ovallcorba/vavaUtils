package com.vava33.cellsymm;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public final class CellSymm_global {
    
//*******************************************************************************************************
// LOGGING
    
    public static VavaLogger log;
    private static boolean loggingConsole = true; //console
    private static boolean loggingFile = false; //file
    private static boolean loggingTA = true; //textArea -- NO ESCRIT AL FITXER DE CONFIGURACIO JA QUE VOLEM SEMPRE ACTIVAT
    private static String loglevel = "info"; //info, config, etc...
    

    public static void setLogLevel(String lev) {
        loglevel=lev;
    }
    
    public static void setLogging(boolean logConsole, boolean logfile, boolean logTA) {
        loggingConsole=logConsole;
        loggingFile=logfile;
        loggingTA=logTA;
    }
    
    public static void initLogger(String name){
        log = new VavaLogger(name,loggingConsole,loggingFile,loggingTA);
        log.setLogLevel(loglevel);
        
        if (isAnyLogging()) {
            log.enableLogger(true);
        }else {
            log.enableLogger(false);
        }

    }
    
    public static VavaLogger getVavaLogger(String name){
        VavaLogger l = new VavaLogger(name,loggingConsole,loggingFile,loggingTA);
        l.setLogLevel(loglevel);
        if (isAnyLogging()) {
            l.enableLogger(true);
        }else {
            l.enableLogger(false);
        }
        return l;
    }
    
    public static boolean isAnyLogging() {
        if (loggingConsole || loggingFile || loggingTA) return true;
        return false;
    }

    //returns true if logging is enabled and level is <= config
    public static boolean isDebug(){
        if (isAnyLogging()){
            if (loglevel.equalsIgnoreCase("config")||loglevel.equalsIgnoreCase("debug")||loglevel.equalsIgnoreCase("fine")||loglevel.equalsIgnoreCase("finest")){
                return true;
            }
        }
        return false;
    }
    
//*******************************************************************************************************
    
    
    private static final String sgpropsurl = "/com/vava33/cellsymm_new/res/sgproperties";
    private static ArrayList<SpaceGroup> spaceGroups;
    
    public static enum CrystalFamily {CUBIC,TETRA,HEXA,ORTO,MONO,TRIC,NONE}
    public static enum CrystalSystem {CUBIC,TETRA,HEXA,TRIGO,ORTO,MONO,TRIC,NONE}
    public static enum CrystalCentering {P,A,B,C,F,I,R;
        public double[] getTranslations() {
            switch (this) {
            case P:return new double[] {0,0,0};
            case A:return new double[] {0,0,0,0,0.5,0.5};
            case B:return new double[] {0,0,0,0.5f,0,0.5f};
            case C:return new double[] {0,0,0,0.5f,0.5f,0};
            case F:return new double[] {0,0,0,0,0.5f,0.5f,0.5f,0,0.5f,0.5f,0.5f,0};
            case I:return new double[] {0,0,0,0.5f,0.5f,0.5f};
            case R:return new double[] {0,0,0,0.6666667f,0.3333333f,0.3333333f,0.3333333f,0.6666667f,0.6666667f};
            default:return new double[] {0,0,0};
            }
        }
        public ArrayList<RealMatrix> getTranslationsAsColumnMatrices() {
            ArrayList<RealMatrix> lattMatrices = new ArrayList<RealMatrix>();
            lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0,0,0}));
            switch (this) {
            case A:
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0,0.5,0.5}));
                break;
            case B:
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0.5,0,0.5}));
                break;
            case C:
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0.5,0.5,0}));
                break;
            case F:
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0,0.5,0.5}));
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0.5,0,0.5}));
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0.5,0.5,0}));
                break;
            case I:
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {0.5,0.5,0.5}));
                break;
            case R:
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {2/3,1/3,1/3}));
                lattMatrices.add(MatrixUtils.createColumnRealMatrix(new double[] {1/3,2/3,2/3}));
                break;
            default:break;
            }
            return lattMatrices;
        }
    }
    public static enum CrystalLaueGroup {L1,L2,L3,L4,L5,L6,L7,L8,L9,L10,L11;
        public String getName() {
            switch (this) {
            case L1:return "-1";
            case L2:return "2/m";
            case L3:return "mmm"; 
            case L4:return "4/m";
            case L5:return "4/mmm";
            case L6:return "-3(H)"; //el dajust considera també "-31m(H)"; i aquest té uns limits hkl diferents...
            case L7:return "-3m1(H)";
            case L8:return "6/m";
            case L9:return "6/mmm";
            case L10:return "m-3";
            case L11:return "m-3m";
            default:return "";
            }
        }
    }

    
    /*
     *--------------------------------------------------------------------------------
     * SGNum=4
     * SGNames=P 1 21 1;P1211;P 21;P21
     * Latt=P
     * Centro=false
     * SYMM=X,Y,Z;-X,Y+1/2,-Z
     * --------------------------------------------------------------------------------
     * SGNum=5
     * SGNames=C 1 2 1;C121;C 2;C2
     * Latt=C
     * Centro=false
     * SYMM=X,Y,Z;-X,Y,-Z
     * --------------------------------------------------------------------------------
     */
    private static void initSpaceGroups() {
        File f;
        try {
            f = new File(CellSymm_global.class.getResource(sgpropsurl).toURI());
        } catch (URISyntaxException e1) {
            log.info("Error reading resource");
            return;
        }
        Scanner sc;
        try {
            sc = new Scanner(f);
            spaceGroups = new ArrayList<SpaceGroup>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("SGNum=")){
                    //new space group
                    int sgnum = Integer.parseInt(line.split("=")[1]);
                    line = sc.nextLine();
                    String sgnames = line.split("=")[1];
                    line = sc.nextLine();
                    char centering = line.split("=")[1].toCharArray()[0];
                    line = sc.nextLine();
                    boolean centro = Boolean.parseBoolean(line.split("=")[1]);
                    line = sc.nextLine();
                    String matrices = line.split("=")[1];
                    spaceGroups.add(new SpaceGroup(sgnum,sgnames,matrices,centro,centering));
                }
            }
        } catch (FileNotFoundException e) {
            log.info("SGproperties file not found");
        }

    }
    
    public static SpaceGroup getSpaceGroupByNum(int SGnum) {
        if (spaceGroups==null)initSpaceGroups();
        if (spaceGroups.size()<=0)initSpaceGroups();
        return spaceGroups.get(SGnum-1);
    }
    
    public static SpaceGroup getSpaceGroupByName(String sgname, boolean askifnotfound) {
        if (spaceGroups==null)initSpaceGroups();
        if (spaceGroups.size()<=0)initSpaceGroups();
        Iterator<SpaceGroup> itrsg = spaceGroups.iterator();
        while (itrsg.hasNext()){
            SpaceGroup sg = itrsg.next();
            if (sg.isThisSG(sgname)) return sg;
        }
        String sgnamefix = identifySGsymbol(sgname, true);
        while (itrsg.hasNext()){
            SpaceGroup sg = itrsg.next();
            if (sg.isThisSG(sgnamefix)) return sg;
        }
        //if not found, now ask for SGnumber
        if (askifnotfound) {
            String s = (String)JOptionPane.showInputDialog(
                    null,
                    "Could not find SG by symbol ("+sgname+") Please, provide the IT number: ",
                    "SG not found",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "1");
            if ((s != null) && (s.length() > 0)) {
                try{
                    int sgn=Integer.parseInt(s);
                    return getSpaceGroupByNum(sgn);
                }catch(Exception ex){
                    log.debug("error reading SG number");
                }
            }
        }
        return spaceGroups.get(0); //not found, tornem sg 1
    }
    
    
    //aquesta subrutina provara d'identificar un grup mal escrit, convertint al format adient
    //retorna una string que correspon al nom arreglat o bé una cadena buida ("") si no s'ha
    //pogut arreglar ni identificar.
    //si elimparentesis=true s'eliminen els parentesis, sinó no.
    public static String identifySGsymbol(String SGsymbol, boolean elimParentesis){
        //passem a sequencia de caracters
        char[] SGin = SGsymbol.toCharArray();
        char[] SGout = new char[20];
        String centratge = "PABCFIRpabcfir";
        if(centratge.indexOf(SGin[0])!=-1){
            //vol dir que el centratge és correcte
            SGout[0]=SGin[0];
        }else{
            log.debug("Error reading centering P, A, B, C, F, I, R");
            return "";
        }
        // el segon ha de ser un espai
        if(SGin[1]==' '){
            SGout[1]=SGin[1];
            for(int i = 2; i<SGin.length;i++){
                SGout[i]=SGin[i];
            }
        }else{ //hem d'afegir l'espai
            SGout[1]=' ';
            for(int i = 1; i<SGin.length;i++){
                SGout[i+1]=SGin[i];
            }
        }

        String fixedSGsymbol = new String(SGout);

        //Provem de treure parèntesis si n'hi ha de posats
        if(elimParentesis){fixedSGsymbol=fixedSGsymbol.replaceAll("\\(|\\)", "");}

        return fixedSGsymbol.trim();
    }
    
    

}
