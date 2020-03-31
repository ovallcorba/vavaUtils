package com.vava33.cellsymm;

import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.VavaLogger;

public class HKLrefl implements Comparable<HKLrefl>{

    private static final String className = "HKLrefl";
    private static VavaLogger log = CellSymm_global.getVavaLogger(className);  
    
    int h;
    int k;
    int l;
    double dsp;
    double ycalc;
    int mult;
        
    public HKLrefl(int hi, int ki, int li, double dspi, double ycal, int multiplicity) {
        this.h=hi;
        this.k=ki;
        this.l=li;
        this.dsp=dspi;
        this.ycalc = ycal; 
        this.mult= multiplicity;
    }
    public HKLrefl(int hi, int ki, int li, double wave, double tth_deg, double ycal, int multiplicity) {
        this(hi,ki,li,(wave/(2*FastMath.sin(FastMath.toRadians(tth_deg/2)))),ycal,multiplicity);
    }
    
    public HKLrefl(int hi, int ki, int li, double dspi) {
        this(hi,ki,li,dspi,0,2);
    }

    public HKLrefl(int hi, int ki, int li, double wave, double tth_deg) {
        this(hi,ki,li,wave,tth_deg,0,2);
    }
    
    public HKLrefl(int hi, int ki, int li, int multiplicity, double dspi) {
        this(hi,ki,li,dspi,0,multiplicity);
    }
    
    public double calct2(double wave1, boolean degrees) {
        double th = FastMath.asin(wave1/(2*dsp));
        if (degrees) {
            return FastMath.toDegrees(2*th);
        }else {
            return 2*th;            
        }
    }
    
    public String toString_HKL_tth_mult_Fc2(float wave) {
        return String.format(" %4d %4d %4d %8.4f %3d %12.4f",h,k,l,calct2(wave,true),mult,ycalc );
    }
    
    public String toString_HKL_D_Fc2() {
        return String.format(" %4d %4d %4d %8.4f %12.4f",h,k,l,dsp,ycalc );
    }

    public String toString(){
        return String.format("%d %d %d", h,k,l);
    }
    
    @Override
    public int compareTo(HKLrefl o) {
        return Double.compare(o.dsp,dsp);
    }

    public int getMult() {
        return mult;
    }

    public double getDsp() {
        return dsp;
    }
    
    public double getYcalc() {
        return ycalc;
    }
    
    public void setYcalc(double ycal) {
        this.ycalc=ycal;
    }
    
    public void setMult(int mult) {
        this.mult = mult;
    }
    
    //powder intensity takes into account Mult, LP, etc... TODO implement
    public void calcY(List<Atom> cellContent, boolean estimBiso, boolean powderIntensity) {
        double A = 0;
        double B = 0;
        for (Atom at:cellContent) {
            double[] AB = at.calcStructFactorContributionToHKL(this, estimBiso);
            A = A + AB[0];
            B = B + AB[1];
        }
        if (powderIntensity) {
            this.ycalc = (A*A + B*B)*this.mult;
        }else {
            this.ycalc = (A*A + B*B);            
        }

        log.configf("%d %d %d %.5f %.5f %.5f", h,k,l,A,B,this.ycalc);
        
    }
    
    public RealMatrix getAsColumnMatrixDim3() {
        return MatrixUtils.createColumnRealMatrix(new double[] {h,k,l});
    }
    
    //to another reflection, considering all *-1
    public boolean isEquivalent(int h0, int k0, int l0) {
        if (h==h0&&k==k0&&l==l0) return true;
        if (-h==h0&&-k==k0&&-l==l0) return true;
        return false;
    }
    //to another reflection, considering all *-1
    public boolean isEquivalent(HKLrefl hkl2) {
        if (h==hkl2.h&&k==hkl2.k&&l==hkl2.l) return true;
        if (-h==hkl2.h&&-k==hkl2.k&&-l==hkl2.l) return true;
        return false;
    }
    
    public double calcQvalue() {
        return 1/(dsp*dsp);
    }
    
    public int[] getHKLindices() {
        return new int[] {h,k,l};
    }
    
    public int getH() {
        return h;
    }
    public int getK() {
        return k;
    }
    public int getL() {
        return l;
    }
    
    
    /**
     * PROFILE (WiP)
     */
    
    double prf;
    public double width;
    public double aint;
    double ori=1; //march preferred orientation
    
    public void calcORI(double coef, int oh, int ok, int ol) {
        if (coef==1) {
            this.ori=1;
            return; //deixem ori=1 as default
        }
        //TODO
    }
}

