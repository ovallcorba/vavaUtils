package com.vava33.cellsymm;

public class HKLrefl implements Comparable<HKLrefl>{
    int h;
    int k;
    int l;
    double t2;
    double ycalc;
    int mult = 2;
    
    public HKLrefl(int hi, int ki, int li,double t2i) {
        h=hi;
        k=ki;
        l=li;
        t2=t2i;
        ycalc = 0;
    }
    
    public HKLrefl(int hi, int ki, int li,double t2i,double yc) {
        h=hi;
        k=ki;
        l=li;
        t2=t2i;
        ycalc = yc;
    }
    
    public HKLrefl(int hi, int ki, int li,double t2i, int multi) {
        h=hi;
        k=ki;
        l=li;
        t2=t2i;
        mult = multi;
    }
    
    public String toString() {
        System.out.println(String.format(" %4d %4d %4d %8.4f %3d %12.4f",h,k,l,t2,mult,ycalc));
        return String.format(" %4d %4d %4d %8.4f %3d %12.4f",h,k,l,t2,mult,ycalc );
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(HKLrefl o) {
        if (t2 > o.t2)return 1;
        if (t2 < o.t2)return -1;
        return 0;
    }

    public int getMult() {
        return mult;
    }

    public void setMult(int mult) {
        this.mult = mult;
    }
}
