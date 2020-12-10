package com.vava33.cellsymm;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;


public final class PDDatabase {

    //Full DB
    private static int nCompounds = 0;  //number of compounds in the DB
    private static String localDB = System.getProperty("user.dir") + FileUtils.fileSeparator + "default.db";  // local DB default file
    private static String currentDB;
    private static List<PDCompound> DBcompList = new ArrayList<PDCompound>();  
    private static List<PDSearchResult> DBsearchresults = new ArrayList<PDSearchResult>();
    private static boolean DBmodified = false;
    
    private static final String className = "PDdatabase";
    private static VavaLogger log = CellSymm_global.getVavaLogger(className);
    
    public static void resetDB(){
        DBcompList.clear();
        nCompounds = 0;
    }
    
    public static void addCompoundDB(PDCompound c){
        DBcompList.add(c);
        nCompounds = nCompounds + 1;
    }
    
    public static int getnCompounds() {
        return nCompounds;
    }

    public static void setnCompounds(int nCompounds) {
        PDDatabase.nCompounds = nCompounds;
    }

    public static List<PDCompound> getDBCompList() {
        return DBcompList;
    }
    
    public static void setDBCompList(List<PDCompound> compList) {
        PDDatabase.DBcompList = compList;
    }
    
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
    
    public static int countLines(ZipFile zfile, String entry) throws IOException {
        InputStream is = zfile.getInputStream(zfile.getEntry(entry));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
    
    //it closes the inputstream
    public static int countLines(InputStream is) throws IOException {
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
    
    public static String getDefaultDBpath(){
        File f = new File(localDB);
        return f.getAbsolutePath();
    }
    
    public static List<PDSearchResult> getDBSearchresults() {
        return DBsearchresults;
    }
    
    public static int getFirstEmptyNum(){
        //TODO:IMPLEMENTAR-HO, momentaneament fa aixo:
        return PDDatabase.getDBCompList().size()+1;
    }

    public static boolean isDBmodified() {
        return DBmodified;
    }

    public static String getLocalDB() {
        return localDB;
    }

    public static void setLocalDB(String localDB) {
        PDDatabase.localDB = localDB;
    }
    
    public static void setDBmodified(boolean dBmodified) {
        DBmodified = dBmodified;
    }
    
    public static String getCurrentDB() {
        return currentDB;
    }

    public static void setCurrentDB(String currentDB) {
        PDDatabase.currentDB = currentDB;
    }
    
    //Aixo llegira el fitxer per omplir la base de dades o la quicklist
    public static class openDBfileWorker extends SwingWorker<Integer,Integer> {

        private File dbfile;
        private boolean stop;
        
        public openDBfileWorker(File datafile) {
            this.dbfile = datafile;
            this.stop = false;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            //number of lines
            int totalLines = 0;
            totalLines = countLines(dbfile.toString());                

            int lines = 0;
            try {
                Scanner scDBfile;
                scDBfile = new Scanner(dbfile);
                
                while (scDBfile.hasNextLine()){
                    
                    if (stop) break;
                    
                    String line = scDBfile.nextLine();
                    
                    if ((lines % 500) == 0){
                        float percent = ((float)lines/(float)totalLines)*100.f;
                        setProgress((int) percent);
                    }
                    
                    lines = lines + 1;
                    
                    if ((line.startsWith("#COMP"))||(line.startsWith("#S "))) {  //#S per compatibilitat amb altres DBs
                        //new compound
                        
                        PDCompound comp;
                        if (line.startsWith("#S ")){
                          String[] cname = line.split("\\s+");
                          StringBuilder sb = new StringBuilder();
                          for (int i=2;i<cname.length;i++){
                              sb.append(cname[i]);
                              sb.append(" ");
                          }
                          
                          comp = new PDCompound(sb.toString().trim());
                        }else{
                          comp = new PDCompound(line.split(":")[1].trim());
                        }
                        
                        boolean cfinished = false;
                        while (!cfinished){
                            String line2 = scDBfile.nextLine();
                            lines = lines + 1;
                            
                            try {
                                if (line2.startsWith("#CELL_PARAMETERS:")){
                                    String[] cellPars = line2.split("\\s+");
                                    comp.getCella().setCellParameters(Double.parseDouble(cellPars[1]),Double.parseDouble(cellPars[2]),Double.parseDouble(cellPars[3]),Double.parseDouble(cellPars[4]),Double.parseDouble(cellPars[5]),Double.parseDouble(cellPars[6]),true);
                                }
                                if (line2.startsWith("#NAME")){ //ja inclou NAMEALT:
                                    if (line2.contains(":")){
                                        comp.addCompoundName((line2.split(":"))[1].trim());
                                    }
                                }
                                
                                if (line2.startsWith("#SPACE_GROUP:")){
                                	if (comp.getCella().getSg().getsgNum()==2) { //si es P-1 pot ser que sigui encara el default, per tant mirem nom
                                		comp.getCella().setSg(CellSymm_global.getSpaceGroupByName((line2.split(":"))[1].trim(),false));	
                                	}
                                }
                                
                                if (line2.startsWith("#SG_NUM:")){
                                	int sgnum=2;
                                	try{
                                		sgnum=Integer.parseInt((line2.split(":"))[1].trim());
                                	}catch(NumberFormatException ex) {
                                		log.info("Error reading SG number for "+comp.getCompName());
                                	}
                                    comp.getCella().setSg(CellSymm_global.getSpaceGroupByNum(sgnum));
                                }
                                
                                if (line2.startsWith("#FORMULA:")){
                                    comp.setFormula((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#REF")){
                                    if (line2.contains(":")){
                                        comp.setReference((line2.split(":"))[1].trim());
                                    }
                                }
                                
                                if (line2.startsWith("#COMMENT:")){
                                    comp.addComent((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#QL")){
                                    comp.setQuicklist(true);
                                }
                                
                                if (line2.startsWith("#LIST:")){
                                    boolean dsplistfinished = false;

                                    while (!dsplistfinished){
                                        if (!scDBfile.hasNextLine()){
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        String line3 = scDBfile.nextLine();
                                        lines = lines + 1;
                                        if (line3.trim().isEmpty()){
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        String[] dspline = line3.trim().split("\\s+");
                                        int h = Integer.parseInt(dspline[0]);
                                        int k = Integer.parseInt(dspline[1]);
                                        int l = Integer.parseInt(dspline[2]);
                                        float dsp = Float.parseFloat(dspline[3]);
                                        float inten = 1.0f;
                                        try{
                                            inten = Float.parseFloat(dspline[4]);    
                                        }catch(Exception exinten){
                                            log.warning(String.format("No intensity found for reflection %d %d %d",h,k,l));
                                        }
                                        comp.addPeak(h, k, l, dsp, inten);
                                    }
                                }
                                
                                //COMPATIBILITAT AMB ALTRES FORMATS
                                if (line2.startsWith("#UXRD_REFERENCE ")){
                                    comp.setReference(line2.substring(16).trim());
                                    comp.getComment().add("https://rruff.info/");
                                }
                                
                                if (line2.startsWith("#UXRD_INFO CELL PARAMETERS:")){
                                    String[] cellPars = line2.split("\\s+");
                                    comp.getCella().setCellParameters(Double.parseDouble(cellPars[3]),Double.parseDouble(cellPars[4]),Double.parseDouble(cellPars[5]),Double.parseDouble(cellPars[6]),Double.parseDouble(cellPars[7]),Double.parseDouble(cellPars[8]),true);
                                }
                               
                                if (line2.startsWith("#UXRD_INFO SPACE GROUP: ")){
                                    comp.getCella().setSg(CellSymm_global.getSpaceGroupByName((line2.split(":"))[1].trim(),false));
                                }
                                
                                if (line2.startsWith("#UXRD_ELEMENTS")){
                                    comp.setFormula(line2.substring(15).trim());
                                }
                                
                                if (line2.startsWith("#L ")){
                                    boolean dsplistfinished = false;

                                    while (!dsplistfinished){
                                        if (!scDBfile.hasNextLine()){
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        String line3 = scDBfile.nextLine();
                                        lines = lines + 1;
                                        if (line3.trim().isEmpty()){
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        String[] dspline = line3.trim().split("\\s+");
                                        int h = Integer.parseInt(dspline[2]);
                                        int k = Integer.parseInt(dspline[3]);
                                        int l = Integer.parseInt(dspline[4]);
                                        float dsp = Float.parseFloat(dspline[0]);
                                        float inten = Float.parseFloat(dspline[1]);
                                        comp.addPeak(h, k, l, dsp, inten);
                                    }
                                }
                                
                                
                            } catch (Exception e) {
                                log.warning("Error reading compound: "+comp.getCompName());
                            }                        
                            
                        }
                        addCompoundDB(comp);
                    }
                }
                scDBfile.close();
            }catch(Exception e){
                log.warning("Error reading DB file (at line: "+lines+")");
                this.cancel(true);
                return 1;
            }
            setProgress(100);
            setCurrentDB(this.dbfile.toString());
            return 0;
        }
        
        public File getDbfile() {
            return dbfile;
        }
        
        public String getReadedFile(){
            return this.dbfile.toString();
        }
        
    }
    
    //Aixo llegira el fitxer per omplir la base de dades o la quicklist
    public static class saveDBfileWorker extends SwingWorker<Integer,Integer> {

        private File dbfile;
        private boolean stop;
        
        public saveDBfileWorker(File datafile) {
            this.dbfile = FileUtils.canviExtensio(datafile,"db");
            this.stop = false;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            try{
                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(dbfile)));
                Iterator<PDCompound> itC = null;
                int ncomp = 0;
                int icomp = 0;
                
                ncomp = getDBCompList().size();
                itC = DBcompList.iterator();

                log.writeNameNumPairs("config", true, "ncomp", ncomp);

                String dt = FileUtils.getStringTimeStamp("[yyyy-MM-dd 'at' HH:mm]");
                
                output.println("# ====================================================================");
                output.println("#         dXDplot compound database "+dt);
                output.println("# ====================================================================");
                output.println();
                
                while (itC.hasNext()){

                    if (stop) break;

                    if ((icomp % 100) == 0){
                        float percent = ((float)icomp/(float)ncomp)*100.f;
                        setProgress((int) percent);
                    }
                    
                    icomp = icomp + 1;
                    
                    PDCompound c = itC.next();
                    output.println(String.format("#COMP: %s",c.getCompName()));
                    
                    String altnames = c.getAltNames();
                    if (!altnames.isEmpty())output.println(String.format("#NAMEALT: %s",altnames));
                    
                    if (!c.getFormula().isEmpty()){
                        output.println(String.format("#FORMULA: %s",c.getFormula()));
                    }
                    if (!c.getCellParameters().isEmpty()){
                        output.println(String.format("#CELL_PARAMETERS: %s",c.getCellParameters()));
                    }
                    if (!c.getCella().getSg().getName().isEmpty()){
                        output.println(String.format("#SPACE_GROUP: %s",c.getCella().getSg().getName()));
                        output.println(String.format("#SG_NUM: %d", c.getCella().getSg().getsgNum()));
                    }
                    if (!c.getReference().isEmpty()){
                        output.println(String.format("#REF: %s",c.getReference()));    
                    }
                    if (!c.getComment().isEmpty()){
                        output.println(String.format("#COMMENT: %s",c.getComment()));    
                    }
                    if (c.isQuicklist()){
                        output.println("#QL");    
                    }
                    output.println("#LIST: H  K  L  dsp  Int");
                    
                    int refs = c.getPeaks().size();
                    for (int i=0;i<refs;i++){
                        int h = c.getPeaks().get(i).getH();
                        int k = c.getPeaks().get(i).getK();
                        int l = c.getPeaks().get(i).getL();
                        double dsp = c.getPeaks().get(i).getDsp();
                        double inten = c.getPeaks().get(i).getYcalc();
                        output.println(String.format("%3d %3d %3d %9.5f %7.2f",h,k,l,dsp,inten));                    
                    }
                    output.println(); //linia en blanc entre compostos

                    log.config("itC end loop cycle");

                }
                output.close();
                
            }catch(Exception e){
                this.cancel(true);
                log.info("Error writting compound DB: "+dbfile.toString());
                return 1;
            }
            setProgress(100);
            setCurrentDB(this.dbfile.toString());
            return 0;
        }
        
        public File getDbfile() {
            return dbfile;
        }
        
        public String getDbFileString(){
            return this.dbfile.toString();
        }
        
    }
    
    
    /*
     * Farem que la intensitat integrada dels pics seleccionats es normalitzi amb el valor màxim dels
     * N primers pics de cada compost per poder-se comparar bé. (N sera igual al nombre de dsp entrats, que 
     * no te perquè ser els N primers però es una bona aproximació).
     */
    
    public static class searchDBWorker extends SwingWorker<Integer,Integer> {

        private List<Double> dspList;
        private List<Double> intList;
        private boolean stop;
        
        public searchDBWorker(List<Double> dspacings, List<Double> intensities) {
            dspList = dspacings;
            intList = intensities;
            DBsearchresults.clear();
            this.stop = false;
        }
        
        public void mySetProgress(int prog){
            setProgress(prog);
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            
        	double maxIslist = Collections.max(intList);
            PDSearchResult.setMinDSPin(Collections.min(dspList));
            PDSearchResult.setnDSPin(dspList.size());
            
            Iterator<PDCompound> itrComp = DBcompList.iterator();
            int compIndex = 0;
            while (itrComp.hasNext()){
                if (stop) break;
                PDCompound c = itrComp.next();
                Iterator<Double> itrDSP = this.dspList.iterator();
                float diffPositions = 0;
                float diffIntensities = 0;
                int npk = 0;
                
                //mirem la intensitat màxima dels n primers pics de COMP per normalitzar!
                float maxI_factorPerNormalitzar = (float) c.getMaxInten(dspList.size());
                if (maxI_factorPerNormalitzar <= 0){maxI_factorPerNormalitzar=1.0f;}
                
                while (itrDSP.hasNext()){
                    double dsp = itrDSP.next();  //pic entrat a buscar
                    int index = c.closestPeak(dsp);
                    float diffpk = (float) FastMath.abs(dsp-c.getPeaks().get(index).getDsp());
                    diffPositions = diffPositions + (diffpk*2.5f); 
                    double intensity = this.intList.get(npk);
                    //normalitzem la intensitat utilitzant el maxim dels N primers pics.
                    intensity = (intensity/maxIslist) * maxI_factorPerNormalitzar;
                    if (c.getPeaks().get(index).getYcalc()>=0){ //no tenim en compte les -1 (NaN)
                        diffIntensities = (float) (diffIntensities + FastMath.abs(intensity-c.getPeaks().get(index).getYcalc()));    
                    }
                    npk = npk +1;
                }
                DBsearchresults.add(new PDSearchResult(c,diffPositions,diffIntensities));
                compIndex = compIndex + 1;
                
                if ((compIndex % nCompounds/100) == 0){
                    float percent = ((float)compIndex/(float)nCompounds)*100.f;
                    setProgress((int) percent);
                }
            }
            setProgress(100);
            return 0;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }
    }
}