package com.vava33.cellsymm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import com.vava33.cellsymm.CellSymm_global.CrystalCentering;
import com.vava33.cellsymm.CellSymm_global.CrystalFamily;
import com.vava33.cellsymm.CellSymm_global.CrystalLaueGroup;
import com.vava33.cellsymm.CellSymm_global.CrystalSystem;
import com.vava33.jutils.VavaLogger;


public class SpaceGroup {

    private static final String className = "SpaceGroup";
    private static VavaLogger log = CellSymm_global.getVavaLogger(className);    
    
    private int SGnum;
    private List<String> SGnames;
    private List<RealMatrix> symMat; //matrius grup espacial augmentades, es a dir 4x4 amb traslació inclosa
    private boolean centro;
    private CrystalFamily crystalFamily;
    private CrystalCentering crystalCentering; //xarxa
    @SuppressWarnings("unused")
    private CrystalSystem crystalSystem;
    @SuppressWarnings("unused")
    private CrystalLaueGroup crystalLaue;
    @SuppressWarnings("unused")
    private int[] setting;
    @SuppressWarnings("unused")
    private int cellChoice;
    
    
    //Names as string separated by semi colons ";"
    //matrices as string separated by semicolons ;
    
    public SpaceGroup(int SGnum, String names, String matrices, boolean centro, int cellChoice, int[] setting) {
        this.SGnames = new ArrayList<String>(Arrays.asList(names.trim().split(";")));
        this.SGnum = SGnum;
        this.centro = centro;
        String[] matrix = matrices.trim().split(";");
        symMat = new ArrayList<RealMatrix>();
        for (int i=0;i<matrix.length;i++) {
            symMat.add(createSymmMat(matrix[i]));
        }
        this.setCrystalFamily();
        this.setCentering();
        this.cellChoice=cellChoice;
        this.setting=setting;
    }
    
    public SpaceGroup(int SGnum, String names, String matrices, boolean centro) {
        this(SGnum,names,matrices,centro,1,new int[] {1}); //default choice/settings
    }
    
    public SpaceGroup(int SGnum, String names, String matrices, boolean centro, char centering,int cellChoice, int[] setting) {
        this(SGnum,names,matrices,centro,cellChoice,setting);
        this.setCentering(centering);
    }
    
    public SpaceGroup(int SGnum, String names, String matrices, boolean centro, char centering) {
        this(SGnum,names,matrices,centro);
        this.setCentering(centering);        
    }
    
    private void setCrystalFamily() {
        if (this.SGnum<3) this.crystalFamily=CrystalFamily.TRIC;
        if (this.SGnum>2 && this.SGnum<16) this.crystalFamily=CrystalFamily.MONO;
        if (this.SGnum>15 && this.SGnum<75) this.crystalFamily=CrystalFamily.ORTO;
        if (this.SGnum>74 && this.SGnum<143) this.crystalFamily=CrystalFamily.TETRA;
        if (this.SGnum>142 && this.SGnum<195) this.crystalFamily=CrystalFamily.HEXA;
        if (this.SGnum>194) this.crystalFamily=CrystalFamily.CUBIC;
    }
    
    public CrystalFamily getCrystalFamily() {
        return this.crystalFamily;
    }
    
    public void setSettings(int...is) {
        this.setting=is;
    }
    public void setCellChoice(int cellchoice) {
        this.cellChoice=cellchoice;
    }
    
    @SuppressWarnings("unused")
    private void setCrystalSystem() {
        if (this.SGnum<3) this.crystalSystem=CrystalSystem.TRIC;
        if (this.SGnum>2 && this.SGnum<16) this.crystalSystem=CrystalSystem.MONO;
        if (this.SGnum>15 && this.SGnum<75) this.crystalSystem=CrystalSystem.ORTO;
        if (this.SGnum>74 && this.SGnum<143) this.crystalSystem=CrystalSystem.TETRA;
        if (this.SGnum>142 && this.SGnum<168) this.crystalSystem=CrystalSystem.TRIGO;
        if (this.SGnum>167 && this.SGnum<195) this.crystalSystem=CrystalSystem.HEXA;
        if (this.SGnum>194) this.crystalSystem=CrystalSystem.CUBIC;
    }
    
    //takes from sgname
    private void setCentering() {
        char c = this.SGnames.get(0).trim().substring(0, 1).toLowerCase().toCharArray()[0];
        this.setCentering(c);
    }
    
    public CrystalCentering getCrystalCentering() {
        return this.crystalCentering;
    }
    
    private void setCentering(char c) {
        switch (c) {
        case 'p': this.crystalCentering=CrystalCentering.P;break;
        case 'a': this.crystalCentering=CrystalCentering.A;break;
        case 'b': this.crystalCentering=CrystalCentering.B;break;
        case 'c': this.crystalCentering=CrystalCentering.C;break;
        case 'f': this.crystalCentering=CrystalCentering.F;break;
        case 'r': this.crystalCentering=CrystalCentering.R;break;
        case 'i': this.crystalCentering=CrystalCentering.I;break;
        case 'P': this.crystalCentering=CrystalCentering.P;break;
        case 'A': this.crystalCentering=CrystalCentering.A;break;
        case 'B': this.crystalCentering=CrystalCentering.B;break;
        case 'C': this.crystalCentering=CrystalCentering.C;break;
        case 'F': this.crystalCentering=CrystalCentering.F;break;
        case 'R': this.crystalCentering=CrystalCentering.R;break;
        case 'I': this.crystalCentering=CrystalCentering.I;break;
        default:this.crystalCentering=CrystalCentering.P;break;
        }
    }
    
    @SuppressWarnings("unused")
    private void setCrystalLaue() {
        if (this.SGnum<3) this.crystalLaue=CrystalLaueGroup.L1;
        if (this.SGnum>2 && this.SGnum<16) this.crystalLaue=CrystalLaueGroup.L2;
        if (this.SGnum>15 && this.SGnum<75) this.crystalLaue=CrystalLaueGroup.L3;
        if (this.SGnum>74 && this.SGnum<89) this.crystalLaue=CrystalLaueGroup.L4;
        if (this.SGnum>88 && this.SGnum<143) this.crystalLaue=CrystalLaueGroup.L5;
        if (this.SGnum>142 && this.SGnum<149) this.crystalLaue=CrystalLaueGroup.L6;
        if (this.SGnum>148 && this.SGnum<168) this.crystalLaue=CrystalLaueGroup.L7;
        if (this.SGnum>167 && this.SGnum<177) this.crystalLaue=CrystalLaueGroup.L8;
        if (this.SGnum>176 && this.SGnum<195) this.crystalLaue=CrystalLaueGroup.L9;
        if (this.SGnum>194 && this.SGnum<207) this.crystalLaue=CrystalLaueGroup.L10;
        if (this.SGnum>206) this.crystalLaue=CrystalLaueGroup.L11;
    }
    
    public int getMaximumMultiplicity() {
        int nmat = this.symMat.size();
        int ntranslat = (int)(this.crystalCentering.getTranslations().length/3.);
        int cen = 1;
        if (this.centro)cen=2;
        return nmat*ntranslat*cen;
    }
    
    public boolean isThisSG(String sgname) {
        Iterator<String> itrS = this.SGnames.iterator();
        while (itrS.hasNext()) {
            if (itrS.next().trim().equalsIgnoreCase(sgname.trim())) return true;
        }
        return false;
    }
    
    public boolean isThisSGnoCentering(String sgname) {
        Iterator<String> itrS = this.SGnames.iterator();
        while (itrS.hasNext()) {
            String name = itrS.next();
            if (name.trim().substring(1).equalsIgnoreCase(sgname.trim().substring(1))) return true;
        }
        return false;
    }
    
    public String getName() {
        return SGnames.get(0);
    }
    
    //converteix matrius text x,y,z ... a array floats
    // --> originalment eren 12 numeros per matriu així:1  0  0   0  1  0   0  0  1  0.00000  0.00000  0.00000 
    // on 
    // index a float[]: 0  1  2   3  4  5   6  7  8   9 10 11
    //                 11 12 13  21 22 23  31 32 33  t1 t2 t3
    //
    //PERO ARA els posaré així per generar matrius 4x4: xx xy xz tx yx yy yz ty zx zy zz tz
    // on 
    // index a float[]: 0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
    //                 xx xy xz tx yx yy yz ty zx zy zz tz  0  0  0  1
    public RealMatrix createSymmMat(String matrixXYZtxt) {
        String[] xyz = matrixXYZtxt.trim().split(",");
        //matrix a zero
        double[] matrix = new double[16]; //4x4
        for (int k=0;k<matrix.length;k++) {
            matrix[k]=0;
        }
        //les x
        char[] ch = xyz[0].trim().toCharArray();
        boolean neg = false;
        boolean denominador = false;
        float trans = 0;
        for (int j=0; j<ch.length;j++) {
            switch (ch[j]) {
                case '-':
                    neg = true;
                    break;
                case '+':
                    neg = false;
                    break;
                case 'X':
                    matrix[0]= 1;
                    if (neg) {
                        matrix[0] = -1;
                        neg = false;
                    }
                    break;
                case 'Y':
                    matrix[1]= 1;
                    if (neg) {
                        matrix[1] = -1;
                        neg = false;
                    }
                    break;
                case 'Z':
                    matrix[2]= 1;
                    if (neg) {
                        matrix[2] = -1;
                        neg = false;
                    }
                    break;
                case '/':
                    denominador = true;
                    break;
                default:
                    if (!denominador) {
                        trans = trans + Integer.parseInt(String.valueOf(ch[j]));    
                    }else {
                        trans = trans / Integer.parseInt(String.valueOf(ch[j]));
                        denominador = false;
                    }
                    if (neg) trans = -1*trans;
                    break;
            }
            matrix[3] = trans;
        }
        //les y
        ch = xyz[1].trim().toCharArray();
        neg = false;
        denominador = false;
        trans = 0;
        for (int j=0; j<ch.length;j++) {
            switch (ch[j]) {
                case '-':
                    neg = true;
                    break;
                case '+':
                    neg = false;
                    break;
                case 'X':
                    matrix[4]= 1;
                    if (neg) {
                        matrix[4] = -1;
                        neg = false;
                    }
                    break;
                case 'Y':
                    matrix[5]= 1;
                    if (neg) {
                        matrix[5] = -1;
                        neg = false;
                    }
                    break;
                case 'Z':
                    matrix[6]= 1;
                    if (neg) {
                        matrix[6] = -1;
                        neg = false;
                    }
                    break;
                case '/':
                    denominador = true;
                    break;
                default:
                    if (!denominador) {
                        trans = trans + Integer.parseInt(String.valueOf(ch[j]));    
                    }else {
                        trans = trans / Integer.parseInt(String.valueOf(ch[j]));
                        denominador = false;
                    }
                    if (neg) trans = -1*trans;
                    break;
            }
            matrix[7] = trans;
        }
        
        //les z
        ch = xyz[2].trim().toCharArray();
        neg = false;
        denominador = false;
        trans = 0;
        for (int j=0; j<ch.length;j++) {
            switch (ch[j]) {
                case '-':
                    neg = true;
                    break;
                case '+':
                    neg = false;
                    break;
                case 'X':
                    matrix[8]= 1;
                    if (neg) {
                        matrix[8] = -1;
                        neg = false;
                    }
                    break;
                case 'Y':
                    matrix[9]= 1;
                    if (neg) {
                        matrix[9] = -1;
                        neg = false;
                    }
                    break;
                case 'Z':
                    matrix[10]= 1;
                    if (neg) {
                        matrix[10] = -1;
                        neg = false;
                    }
                    break;
                case '/':
                    denominador = true;
                    break;
                default:
                    if (!denominador) {
                        trans = trans + Integer.parseInt(String.valueOf(ch[j]));    
                    }else {
                        trans = trans / Integer.parseInt(String.valueOf(ch[j]));
                        denominador = false;
                    }
                    if (neg) {
                        trans = -1*trans;
                        neg=false;
                    }
                    break;
            }
            matrix[11] = trans;
        }

        
        log.fine(Arrays.toString(matrix));
        RealMatrix rm = MatrixUtils.createRealMatrix(4, 4);
        rm.setRow(0, Arrays.copyOfRange(matrix, 0, 4)); //rang inclusive-exclusive, es a dir, de n a (n-1)
        rm.setRow(1, Arrays.copyOfRange(matrix, 4, 8));
        rm.setRow(2, Arrays.copyOfRange(matrix, 8, 12));
        rm.setRow(3, Arrays.copyOfRange(matrix, 12, 16));
        log.fine(rm.toString());
        return rm;
    }
    
    public List<RealMatrix> symmat(String[] matrius){
        List<RealMatrix> matrix_arraylist = new ArrayList<RealMatrix>(); 
        for (int i=0;i<matrius.length;i++) {
            matrix_arraylist.add(createSymmMat(matrius[i]));
        }
        return matrix_arraylist;
    }
    
    public boolean isReflectionAllowedBySymmetry(int h, int k, int l) {
        
        RealMatrix hkl1 = MatrixUtils.createRowRealMatrix(new double[] {h,k,l,1});
        int iter = 1;
        if (this.centro) iter=2;
        int sign = -1;
        for (int it=0;it<iter;it++) {
            sign = sign*-1;
            Iterator<RealMatrix> itrM = this.symMat.iterator();
            while (itrM.hasNext()) {
                RealMatrix rm = itrM.next();
                RealMatrix res = hkl1.multiply(rm);
                int ihr = (int) res.getEntry(0, 0);
                int ikr = (int) res.getEntry(0, 1);
                int ilr = (int) res.getEntry(0, 2);
                double tr = res.getEntry(0, 3);
                int ips = (int) ((360*sign*tr)%360);
                if ((h==(sign*ihr))&&(k==(sign*ikr))&&(l==(sign*ilr))&&(ips!=0))return false;
            }
        }
        return true;
    }
    
    //apply symmetry matrices and return related atom list
    //CENTROSYMMETRY IS INCLUDED IF SG IS CENTROSYMMETRIC
    public List<Atom> getSymmetryRelatedPos(Atom at0,boolean putInUnitCell){
        List<Atom> ats = new ArrayList<Atom>();
        
        List<RealMatrix>  allMatrix = this.getAllMatricesIncludingCentro();
        
        for (RealMatrix sym: allMatrix) {
            RealMatrix newCoords = sym.multiply(at0.getCoordsAsExtended4rowMatrix());
            
            double x = newCoords.getEntry(0, 0);
            double y = newCoords.getEntry(1, 0);
            double z = newCoords.getEntry(2, 0);
            
            if (putInUnitCell) {
                x = x + 20.0;
                x = x%((int)FastMath.floor(x));
                y = y + 20.0;
                y = y%((int)FastMath.floor(y));
                z = z + 20.0;
                z = z%((int)FastMath.floor(z));
            }
            ats.add(new Atom(at0,x,y,z));
        }
        return ats;
    }
    
    public List<Atom> getLattTransRelatedPos(Atom at0,boolean putInUnitCell){
        List<Atom> ats = new ArrayList<Atom>();
        RealMatrix AtCoords = at0.getCoordsAs3rowMatrix();
        for (RealMatrix trans:this.crystalCentering.getTranslationsAsColumnMatrices()) {
            double[] coords = trans.add(AtCoords).getColumn(0);
            if (putInUnitCell) {
                coords[0] = coords[0] + 20.0;
                coords[0] = coords[0]%((int)FastMath.floor(coords[0]));
                coords[1] = coords[1] + 20.0;
                coords[1] = coords[1]%((int)FastMath.floor(coords[1]));
                coords[2] = coords[2] + 20.0;
                coords[2] = coords[2]%((int)FastMath.floor(coords[2]));
            }
            ats.add(new Atom(at0,coords[0],coords[1],coords[2]));
        }
        return ats;
    }
    
    private List<RealMatrix> getAllMatricesIncludingCentro() {
        if (!this.centro) return this.symMat;
        
        //duplicar el nombre de matrius canviant el signe nomes a la part no traslacional (la no extesa).
        List<RealMatrix> allMatrix = new ArrayList<RealMatrix>();
        allMatrix.addAll(this.symMat);
        for (RealMatrix m:this.symMat) {
            RealMatrix neg = m.copy();
            neg.multiplyEntry(0, 0, -1);
            neg.multiplyEntry(0, 1, -1);
            neg.multiplyEntry(0, 2, -1);
            neg.multiplyEntry(1, 0, -1);
            neg.multiplyEntry(1, 1, -1);
            neg.multiplyEntry(1, 2, -1);
            neg.multiplyEntry(2, 0, -1);
            neg.multiplyEntry(2, 1, -1);
            neg.multiplyEntry(2, 2, -1);
            allMatrix.add(neg);            
        }
        return allMatrix;
    }
    
    public int calcMultiplicityReflection(HKLrefl hkl) {
        return this.getEquivalentReflections(hkl).size()*2;
    }
    
    //just in case we want to print all the equivalents we return an arraylist, podriem posar-lo també dins HKLrefl pero de moment no ho faig
    public List<HKLrefl> getEquivalentReflections(HKLrefl hkl) {
        List<HKLrefl> eqRefl = new ArrayList<HKLrefl>();
        for(RealMatrix mat:this.symMat) {
            double[] newhkl = mat.getSubMatrix(0, 2, 0, 2).multiply(hkl.getAsColumnMatrixDim3()).getColumn(0); 
            boolean hasEquivalent = false;
            for (HKLrefl ref:eqRefl) {
                if (ref.isEquivalent((int)newhkl[0], (int)newhkl[1], (int)newhkl[2]))hasEquivalent=true;
            }
            if (hasEquivalent) continue;
            //si arribem aqui l'afegim
            eqRefl.add(new HKLrefl((int)newhkl[0], (int)newhkl[1], (int)newhkl[2],hkl.dsp));
        }
        return eqRefl;
    }
    
    public int getsgNum() {
        return this.SGnum;
    }
    
    @Override
    public String toString() {
        return this.SGnames.get(0);
    }
    
}
