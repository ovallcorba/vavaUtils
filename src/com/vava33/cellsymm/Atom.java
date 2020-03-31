package com.vava33.cellsymm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.AtomProperties;
import com.vava33.jutils.VavaLogger;

public class Atom {

    private static final float tolposAtoms = 0.01f; //tolerance in the crystallographic parameters to consider equivalent atoms
    
    private static final String className = "Atom";
    private static VavaLogger log = CellSymm_global.getVavaLogger(className);   
    
    private String label;
    private String tipus;  //Simbol
    private double xcryst, ycryst, zcryst;
    private double ocupancy, adp;
    private int multiplicityPosition;
    
    public Atom(String AtSymbol, String label, double xcr, double ycr, double zcr, double occ, double displ) {
        this.label=label;
        this.tipus=AtSymbol;
        this.xcryst=xcr;
        this.ycryst=ycr;
        this.zcryst=zcr;
        this.ocupancy=occ;
        this.adp=displ;
        this.multiplicityPosition=1;
    }
    
    // copy everything but coordinates
    public Atom(Atom origin, double xcr, double ycr, double zcr) {
        this.label=origin.label;
        this.tipus=origin.tipus;
        this.xcryst=xcr;
        this.ycryst=ycr;
        this.zcryst=zcr;
        this.ocupancy=origin.ocupancy;
        this.adp=origin.adp;
        this.multiplicityPosition=1;
    }
    
    public RealMatrix getCoordsAsExtended4rowMatrix() {
        return MatrixUtils.createColumnRealMatrix(new double[] {xcryst,ycryst,zcryst,1});
    }
    
    public RealMatrix getCoordsAs3rowMatrix() {
        return MatrixUtils.createColumnRealMatrix(new double[] {xcryst,ycryst,zcryst});
    }
    
    public boolean isInTheSamePosition(Atom at, double tolCrystCoord) {
        if ((FastMath.abs(at.xcryst-this.xcryst)<tolCrystCoord) &&
                (FastMath.abs(at.ycryst-this.ycryst)<tolCrystCoord) &&
                (FastMath.abs(at.zcryst-this.zcryst)<tolCrystCoord)) {
            //son iguals
            return true;
        }
        return false;
    }
    
    //es pot incloure simetria i/o traslacions reticulars
    public List<Atom> getEquivalents(SpaceGroup sg, boolean bySymmetry, boolean byLattTrans, boolean putInUnitCell){
        List<Atom> eqAts = new ArrayList<Atom>();
        List<Atom> symmGenerated = new ArrayList<Atom>();
        List<Atom> lattGenerated = new ArrayList<Atom>();
        this.multiplicityPosition = 0;
        //primer aplicarem el centratge
        if (byLattTrans) {
            lattGenerated.addAll(sg.getLattTransRelatedPos(this, putInUnitCell)); //afegim els de les traslacions reticulars també
        }else {
            lattGenerated.add(this);
        }
        for (Atom at1:lattGenerated) {
            //aleshores a cada atom generat pel centratge aplicarem les matrius de simetria
            if (bySymmetry) {
                symmGenerated.addAll(sg.getSymmetryRelatedPos(at1,putInUnitCell)); //ja inclou si es o no centro
            }else {
                symmGenerated.add(at1);
            }
            //i ara comprovem existents (volem un representant de cada únicament)
            for (Atom at2:symmGenerated) {
                boolean existing = false;
                //mirem si ja existeix
                for (Atom atex:eqAts) {
                    if (at2.isInTheSamePosition(atex,tolposAtoms)) {
                        existing=true;
                    }
                }
                if (!existing) {
                    eqAts.add(at2);
                    this.multiplicityPosition++;
                }
            }
        }
        //debug (to REMOVE)
        log.debug("LATT TRANS + SYMM RELATED:");
        int count = 0;
        for (Atom a:sg.getLattTransRelatedPos(this,putInUnitCell)) {
            for (Atom b:sg.getSymmetryRelatedPos(a, putInUnitCell)) {
                log.debug(b.toString());
                count++;
            }
        }
        log.debug("total related="+count);
        /////////--end debug block

        log.configf("Atom %s multiplicity= %d (SG max. %d)",this.label,this.multiplicityPosition,sg.getMaximumMultiplicity());
        for (Atom a:eqAts) {
            log.debug(a.toString());
        }
        
        return eqAts;
    }
    
    //returns double pair {A,B}
    public double[] calcStructFactorContributionToHKL(HKLrefl hkl, boolean estimBiso) {
        double[] AB = new double[] {0,0};
        double f0 = AtomProperties.calcfform_cromer(this.tipus, hkl.dsp);
        double cos = FastMath.cos((FastMath.PI*2)*(hkl.h*this.xcryst+hkl.k*this.ycryst+hkl.l*this.zcryst));
        double sin = FastMath.sin((FastMath.PI*2)*(hkl.h*this.xcryst+hkl.k*this.ycryst+hkl.l*this.zcryst));

        double fB = 1;
        if (estimBiso) {
            double meanDisplacementA = 0.05;
            try {
                meanDisplacementA = this.adp;    
            }catch(Exception e) {
                log.debug("no adp found");
            }
            double Biso = 8 * (FastMath.PI * FastMath.PI) * (meanDisplacementA * meanDisplacementA);
            double ST_L = 1/(2*hkl.dsp);
            fB = FastMath.exp(-1*Biso*ST_L*ST_L);
        }
        AB[0]= f0 * cos * fB;
        AB[1]= f0 * sin * fB;
        return AB;
    }
    
    public String toString() {
        return String.format("%s %.5f %.5f %.5f ", this.label, this.xcryst,this.ycryst,this.zcryst);
    }
    
    
    public String getLabel() {
        return label;
    }
}
