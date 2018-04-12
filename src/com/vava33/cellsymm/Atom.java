package com.vava33.cellsymm;

public class Atom {

    private String label;
    private String tipus;  //Simbol
    private float xcryst, ycryst, zcryst;
    private float ocupancy, adp;
    private int multiplicityPosition;
    
    public Atom(String AtSymbol, String label, float xcr, float ycr, float zcr, float occ, float displ) {
        this.label=label;
        this.tipus=AtSymbol;
        this.xcryst=xcr;
        this.ycryst=ycr;
        this.zcryst=zcr;
        this.ocupancy=occ;
        this.adp=displ;
        this.multiplicityPosition=1;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public float getXcryst() {
        return xcryst;
    }

    public void setXcryst(float xcryst) {
        this.xcryst = xcryst;
    }

    public float getYcryst() {
        return ycryst;
    }

    public void setYcryst(float ycryst) {
        this.ycryst = ycryst;
    }

    public float getZcryst() {
        return zcryst;
    }

    public void setZcryst(float zcryst) {
        this.zcryst = zcryst;
    }

    public float getOcupancy() {
        return ocupancy;
    }

    public void setOcupancy(float ocupancy) {
        this.ocupancy = ocupancy;
    }

    public float getAdp() {
        return adp;
    }

    public void setAdp(float adp) {
        this.adp = adp;
    }

    public int getMultiplicityPosition() {
        return multiplicityPosition;
    }

    public void setMultiplicityPosition(int multiplicityPosition) {
        this.multiplicityPosition = multiplicityPosition;
    }
}
