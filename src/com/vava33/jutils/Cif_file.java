/**
 * 
 */
package com.vava33.jutils;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author ovallcorba
 *
 */
public class Cif_file {

    private static VavaLogger log = Cif_file.getVavaLogger(Cif_file.class.getName());
    
    public static VavaLogger getVavaLogger(String name){
        VavaLogger l = new VavaLogger(name);
        l.setLogLevel("CONFIG");
        l.enableLogger(true);
        return l;
    }
    
    private class Atom {
        private String label;
        private String tipus;  //Simbol
        private float xcryst, ycryst, zcryst;
        private float ocupancy, displ;
    }
    
    private String nom;
    private float a,b,c,al,be,ga;
    private String sgString;
    private int sgNum = 0;
    private ArrayList<Atom> atoms;
    
    public Cif_file(String ciffile, boolean ReviewReadedDataDialog) {
        boolean ok = readCIF(new File(ciffile),ReviewReadedDataDialog);
        if (!ok) log.info("error reading CIF file");
    }
    
    public Cif_file(File ciffile, boolean ReviewReadedDataDialog) {
        boolean ok = readCIF(ciffile,ReviewReadedDataDialog);
        if (!ok) log.info("error reading CIF file");
    }
    
    public int getNAtoms() {
        return atoms.size();
    }
    
    public float getAtomXcrys(int nAtom) {
        return atoms.get(nAtom).xcryst;
    }
    public float getAtomYcrys(int nAtom) {
        return atoms.get(nAtom).ycryst;
    }
    public float getAtomZcrys(int nAtom) {
        return atoms.get(nAtom).zcryst;
    }
    public String getAtomLabel(int nAtom) {
        return atoms.get(nAtom).label;
    }
    public String getAtomType(int nAtom) {
        return atoms.get(nAtom).tipus;
    }
    public float getAtomOcc(int nAtom) {
        return atoms.get(nAtom).ocupancy;
    }
    public float getAtomDisp(int nAtom) {
        return atoms.get(nAtom).displ;
    }
    
    //true if everything ok
    private boolean readCIF(File cf, boolean reviewReadInfoDialog){
//        int nAtoms=0;
        try{
            Scanner scanner = new Scanner(cf);
            String line;
            boolean atomsFinished = false;
            //la primera linia no blanca es el num d'atoms
            while(scanner.hasNextLine()){
                if (atomsFinished)break;
                line=scanner.nextLine().trim();
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "data_")){
                    this.setNom(line.trim().substring(5));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_cell_length_a")){
                    String[] linia= line.split("\\s+|\\t");
//                    a=Float.parseFloat(removeBrk(linia[1].trim()));
                    this.setA(Float.parseFloat(FileUtils.removeBrk(linia[1].trim())));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_cell_length_b")){
                    String[] linia= line.split("\\s+|\\t");
                    this.setB(Float.parseFloat(FileUtils.removeBrk(linia[1].trim())));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_cell_length_c")){
                    String[] linia= line.split("\\s+|\\t");
                    this.setC(Float.parseFloat(FileUtils.removeBrk(linia[1].trim())));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_cell_angle_alpha")){
                    String[] linia= line.split("\\s+|\\t");
                    this.setAl(Float.parseFloat(FileUtils.removeBrk(linia[1].trim())));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_cell_angle_beta")){
                    String[] linia= line.split("\\s+|\\t");
                    this.setBe(Float.parseFloat(FileUtils.removeBrk(linia[1].trim())));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_cell_angle_gamma")){
                    String[] linia= line.split("\\s+|\\t");
                    this.setGa(Float.parseFloat(FileUtils.removeBrk(linia[1].trim())));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_symmetry_int_tables_number")){
                    String[] linia= line.split("\\s+|\\t");
                    this.setSgNum(Integer.parseInt(linia[1].trim()));
                }
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_space_group_IT_number")){
                    String[] linia= line.split("\\s+|\\t");
                    this.setSgNum(Integer.parseInt(linia[1].trim()));
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_space_group_name_H-M_alt")){
                    String[] linia= line.split("\\s+|\\t");
                    StringBuilder sb = new StringBuilder();
                    for (int i=1; i<linia.length;i++) {
                        sb.append(linia[i].replaceAll("'", "").trim());
                        sb.append(" ");
                    }
                    this.setSgString(sb.toString().trim());
                }
                
                if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_")){
                    //mirar a quina posicio esta el atom_site_label,atom_site_type_symbol,_atom_site_fract_x,y,z
                    //i quan s'acabin els labels que comencen amb _atom... aleshores llegir les coord
                    int posLab=-1;//les posicions de cadascun
                    int posTyp=-1;
                    int posX=-1;
                    int posY=-1;
                    int posZ=-1;
                    int posOcu=-1;
                    int posADP=-1;
                    int nItems=0; //quants _atom_* hi ha (realment es n-1, ja que indicara la posicio)
                    //determinem les posicions
                    while(true){
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_label")){
                            posLab=nItems;
                        }
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_type_symbol")){
                            posTyp=nItems;
                        }
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_fract_x")){
                            posX=nItems;
                        }
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_fract_y")){
                            posY=nItems;
                        }
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_fract_z")){
                            posZ=nItems;
                        }
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_occupancy")){
                            posOcu=nItems;
                        }
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_site_U_iso_or_equiv")){
                            posADP=nItems;
                        }
                        if(scanner.hasNextLine()){
                            line=scanner.nextLine().trim();
                        }else{
                            scanner.close();
                            return false;
                        }
                        if(FileUtils.startsWithIgnoreCase(line.trim(), "_atom_")){
                            nItems=nItems+1;
                        }else{
                            //comencen els atoms!
                            break;
                        }
                    }
                    
                    
                    
                    //ja hem arribat als atoms i sabem les posicions (atencio:tenim la 1a linia llegida ja!)
                    int nAtoms=0;
                    this.setAtoms(new ArrayList<Atom>());
                    while(true){
                        Cif_file.Atom at = new Cif_file.Atom();
                        String[] linia= line.split("\\s+|\\t");
                        log.debug(line);
                        if(linia.length<=nItems){
                            //ja no es atom?
                            break;
                        }
                        if(posTyp>=0){
                            at.tipus=linia[posTyp].trim();    
                        }else{
                            log.debug("unable to identify atom type, default C");
                            at.tipus="C";
                        }
                        
                        if(posLab>=0){
                            at.label=linia[posLab].trim();
                        }else{
                            log.debug("unable to identify atom label, default num");
                            at.label=at.tipus.trim()+nAtoms;
                        }
                        //asumim que x,y,z han de ser-hi!
                        at.xcryst=Float.parseFloat(FileUtils.removeBrk(linia[posX]));
                        at.ycryst=Float.parseFloat(FileUtils.removeBrk(linia[posY]));
                        at.zcryst=Float.parseFloat(FileUtils.removeBrk(linia[posZ]));
                        
                        if (posOcu>=0) {
                            at.ocupancy=Float.parseFloat(FileUtils.removeBrk(linia[posOcu]));
                        }
                        if (posADP>=0) {
                            at.displ=Float.parseFloat(FileUtils.removeBrk(linia[posADP]));
                        }
                        
                        this.getAtoms().add(at);
                        nAtoms=nAtoms+1;
                        
                        //ara llegim el seguent si n'hi ha i es atom
                        if(scanner.hasNextLine()){
                            line=scanner.nextLine().trim();
                        }else{
                            break;
                        }
                        if(line.trim().startsWith("_")||line.trim().equalsIgnoreCase("")||line.trim().startsWith("#")||line.trim().startsWith("loop")){
                            //s'han acabat els atoms
                            atomsFinished=true;
                            break;
                        }
                    }
                }
            } 
            scanner.close();
            if(reviewReadInfoDialog)confirmReadInfo();
            return true;
            
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void confirmReadInfo() {
        //show a dialog with the important fields, to check and correct them if necessary
        ImportCIFdialog cifdiag = new ImportCIFdialog(this);
        cifdiag.setVisible(true);
        cifdiag.setAlwaysOnTop(true);
        
        if (!cifdiag.getOkclosed()) {
            return;
        }
        
        try {
            this.setA(cifdiag.getA());
            this.setB(cifdiag.getB());
            this.setC(cifdiag.getC());
            this.setAl(cifdiag.getAlfa());
            this.setBe(cifdiag.getBeta());
            this.setGa(cifdiag.getGamma());
        }catch(Exception ex) {
            FileUtils.InfoDialog(null, "error reading cif cell parameters", "read CIF error");
            return;
        }
        try {
            this.setNom(cifdiag.getName());
            this.setSgString(cifdiag.getSGsymbol());
            this.setSgNum(cifdiag.getSGnum());
        }catch(Exception ex) {
            FileUtils.InfoDialog(null, "error reading cif symmetry", "read CIF error");
            return;
        }
        //ATOMS
        try {
            int nrows = cifdiag.getAtomTableNRows();
            this.setAtoms(new ArrayList<Atom>());
            for (int i=0;i<nrows;i++) {
                Cif_file.Atom at = new Cif_file.Atom();
                at.tipus = cifdiag.getAtomTipus(i);
                at.label = cifdiag.getAtomLabel(i);
                at.xcryst = cifdiag.getAtomXcryst(i);
                at.ycryst = cifdiag.getAtomYcryst(i);
                at.zcryst = cifdiag.getAtomZcryst(i);
                at.ocupancy = cifdiag.getAtomOccup(i);
                at.displ = cifdiag.getAtomDisp(i);
                this.getAtoms().add(at);
            }
        }catch(Exception ex) {
            FileUtils.InfoDialog(null, "error reading cif atoms", "read CIF error");
            return;
        }
    }
    
    public String getCellParametersAsString() {
        return String.format("%.5f %.5f %.5f %.3f %.3f %.3f", a,b,c,al,be,ga);
    }
    
    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getAl() {
        return al;
    }

    public void setAl(float al) {
        this.al = al;
    }

    public float getBe() {
        return be;
    }

    public void setBe(float be) {
        this.be = be;
    }

    public float getGa() {
        return ga;
    }

    public void setGa(float ga) {
        this.ga = ga;
    }

    public String getSgString() {
        return sgString;
    }

    public void setSgString(String sgString) {
        this.sgString = sgString;
    }

    public int getSgNum() {
        return sgNum;
    }

    public void setSgNum(int sgNum) {
        this.sgNum = sgNum;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the atoms
     */
    public ArrayList<Atom> getAtoms() {
        return atoms;
    }

    /**
     * @param atoms the atoms to set
     */
    public void setAtoms(ArrayList<Atom> atoms) {
        this.atoms = atoms;
    }
    
}
