package com.vava33.cellsymm;

import java.util.ArrayList;

import org.apache.commons.math3.util.FastMath;

public class HKLrefl implements Comparable<HKLrefl>{
    int h;
    int k;
    int l;
    double dsp=-1;
    double ycalc;
    int mult = 2;
    Cell cel; //per si de cas al futur ho volem fer servir
    
    public HKLrefl(int hi, int ki, int li, double dspi, Cell cel) {
        this.h=hi;
        this.k=ki;
        this.l=li;
        this.cel=cel;
        this.dsp=dspi;
        this.ycalc = 0;
        this.mult= 2;
    }
    
    public HKLrefl(int hi, int ki, int li, double wave, double tth_deg, Cell cel) {
        this(hi,ki,li,-1,cel);
        this.dsp = (wave/(2*FastMath.sin(FastMath.toRadians(tth_deg/2))));
    }
    
    public HKLrefl(int hi, int ki, int li, int multiplicity, double dspi, Cell cel) {
        this(hi,ki,li,dspi,cel);
        mult = multiplicity;
    }
    
    public HKLrefl(int hi, int ki, int li, int multiplicity, double dspi ,double yc, Cell cel) {
        this(hi,ki,li,multiplicity, dspi,cel);
        ycalc = yc;
    }
    
    public double gett2(float wave, boolean degrees) {
        double th = FastMath.asin(wave/(2*dsp));
        if (degrees) {
            return FastMath.toDegrees(2*th);
        }else {
            return 2*th;            
        }
    }
    
    public String toString_HKL_tth_mult_Fc2(float wave) {
        return String.format(" %4d %4d %4d %8.4f %3d %12.4f",h,k,l,gett2(wave,true),mult,ycalc );
    }
    
    public String toString_HKL_D_Fc2() {
        return String.format(" %4d %4d %4d %8.4f %12.4f",h,k,l,dsp,ycalc );
    }

    public String toString(){
        return String.format("%d %d %d", h,k,l);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(HKLrefl o) {
        if (dsp < o.dsp)return 1;
        if (dsp > o.dsp)return -1;
        return 0;
    }

    public int getMult() {
        return mult;
    }

    public void setMult(int mult) {
        this.mult = mult;
    }
    
    public void calcY(ArrayList<Atom> cellContent, boolean estimBiso) {
        double A = 0;
        double B = 0;
        for (Atom at:cellContent) {
            double[] AB = at.calcStructFactorContributionToHKL(this, estimBiso);
            A = A + AB[0];
            B = B + AB[1];
        }
        this.ycalc = (A*A + B*B);
    }
    
}
