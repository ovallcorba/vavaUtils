package com.vava33.cellsymm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.VavaLogger;


public class SpaceGroup {

    private static VavaLogger log = SpaceGroup.getVavaLogger(SpaceGroup.class.getName());
    
    public static VavaLogger getVavaLogger(String name){
        VavaLogger l = new VavaLogger(name);
        l.setLogLevel("CONFIG");
        l.enableLogger(true);
        return l;
    }
    
    private static int ilaue,ind;
    private int SGnum;
    private String SGname;
    private char xarxa;
    private int n_mat_trans;
    private float[] mat_transFloat3;
    private boolean centro;
    private String mat_SymAsy;
    private ArrayList<float[]> mat_SymAsyFloat12 = new ArrayList<float[]>();
    private int maxMultiSG;

    
    public SpaceGroup(int SGnum) {
        this.SGname=SGproperties.sgNames[SGnum-1];
        this.centro=(SGproperties.sgCentro[SGnum-1]!=0);
        this.mat_SymAsy=SGproperties.symmMat[SGnum-1];
        this.setMat_SymAsyFloat12(this.symmat(SGnum-1));
        this.xarxa = this.SGname.trim().substring(0, 1).toLowerCase().toCharArray()[0];
        this.mat_transFloat3 =this.cellsub(this.SGname.trim().substring(0, 1));
        this.SGnum=SGnum;
        int nmat = this.getMat_SymAsyFloat12().size();
        int ntranslat = (int) (this.getMat_transFloat3().length/3.);
        int cen = 1;
        if (this.centro)cen=2;
        this.setMaxMultiSG(nmat*ntranslat*cen);
    }
    
    // traslacions reticulars
    public float[] cellsub(String xarxa){
        char x = xarxa.toLowerCase().toCharArray()[0];
        float[] mat_trans = null;
        switch (x) {
            case 'p':
                mat_trans = SGproperties.xarxa_P;
                break;
            case 'a':
                mat_trans = SGproperties.xarxa_A;
                break;
            case 'b':
                mat_trans = SGproperties.xarxa_B;
                break;
            case 'c':
                mat_trans = SGproperties.xarxa_C;
                break;
            case 'f':
                mat_trans = SGproperties.xarxa_F;
                break;
            case 'i':
                mat_trans = SGproperties.xarxa_I;
                break;
            case 'r':
                mat_trans = SGproperties.xarxa_R;
                break;
            default:
                break;
        }
        return mat_trans;
    }
    
    //converteix matrius text x,y,z ... a array floats
    //12 numeros per matriu: 1  0  0   0  1  0   0  0  1  0.00000  0.00000  0.00000
    public ArrayList<float[]> symmat(int sgnum){
        String matriusTXT = SGproperties.symmMat[sgnum];
        String[] matrius = matriusTXT.split(";");
        ArrayList<float[]> matrix_arraylist = new ArrayList<float[]>(); 
        
        for (int i=0;i<matrius.length;i++) {
            
            //matrix a zero
            float[] matrix = new float[12];
            for (int k=0;k<matrix.length;k++) {
                matrix[k]=0.0f;
            }
            
            String[] xyz = matrius[i].split(",");
            //les x
            char[] ch = xyz[0].toCharArray();
            boolean neg = false;
            boolean denominador = false;
            float trans = 0.0f;
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
                matrix[9] = trans;
            }
            //les y
            ch = xyz[1].toCharArray();
            neg = false;
            denominador = false;
            trans = 0.0f;
            for (int j=0; j<ch.length;j++) {
                switch (ch[j]) {
                    case '-':
                        neg = true;
                        break;
                    case '+':
                        neg = false;
                        break;
                    case 'X':
                        matrix[3]= 1;
                        if (neg) {
                            matrix[3] = -1;
                            neg = false;
                        }
                        break;
                    case 'Y':
                        matrix[4]= 1;
                        if (neg) {
                            matrix[4] = -1;
                            neg = false;
                        }
                        break;
                    case 'Z':
                        matrix[5]= 1;
                        if (neg) {
                            matrix[5] = -1;
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
                matrix[10] = trans;
            }
            
            //les z
            ch = xyz[2].toCharArray();
            neg = false;
            denominador = false;
            trans = 0.0f;
            for (int j=0; j<ch.length;j++) {
                switch (ch[j]) {
                    case '-':
                        neg = true;
                        break;
                    case '+':
                        neg = false;
                        break;
                    case 'X':
                        matrix[6]= 1;
                        if (neg) {
                            matrix[6] = -1;
                            neg = false;
                        }
                        break;
                    case 'Y':
                        matrix[7]= 1;
                        if (neg) {
                            matrix[7] = -1;
                            neg = false;
                        }
                        break;
                    case 'Z':
                        matrix[8]= 1;
                        if (neg) {
                            matrix[8] = -1;
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

            
            log.debug(Arrays.toString(matrix));
            matrix_arraylist.add(matrix);
        }
        return matrix_arraylist;
    }
    
    
    public String toString() {
        System.out.println(SGnum);
        System.out.println(SGname);
        System.out.println(Arrays.toString(mat_transFloat3));
        System.out.println(mat_SymAsy);
        Iterator<float[]> itrF = getMat_SymAsyFloat12().iterator();
        while (itrF.hasNext()){
            System.out.println(Arrays.toString(itrF.next()));
        }
        System.out.println(Boolean.toString(centro));
        return SGname;
    }

    /**
     * @return the mat_SymAsyFloat12
     */
    public ArrayList<float[]> getMat_SymAsyFloat12() {
        return mat_SymAsyFloat12;
    }

    /**
     * @param mat_SymAsyFloat12 the mat_SymAsyFloat12 to set
     */
    public void setMat_SymAsyFloat12(ArrayList<float[]> mat_SymAsyFloat12) {
        this.mat_SymAsyFloat12 = mat_SymAsyFloat12;
    }

    public static int getIlaue() {
        return ilaue;
    }

    public static void setIlaue(int ilaue) {
        SpaceGroup.ilaue = ilaue;
    }

    public static int getInd() {
        return ind;
    }

    public static void setInd(int ind) {
        SpaceGroup.ind = ind;
    }

    public int getSGnum() {
        return SGnum;
    }

    public void setSGnum(int sGnum) {
        SGnum = sGnum;
    }

    public String getSGname() {
        return SGname;
    }

    public void setSGname(String sGname) {
        SGname = sGname;
    }

    public char getXarxa() {
        return xarxa;
    }

    public void setXarxa(char xarxa) {
        this.xarxa = xarxa;
    }

    public int getN_mat_trans() {
        return n_mat_trans;
    }

    public void setN_mat_trans(int n_mat_trans) {
        this.n_mat_trans = n_mat_trans;
    }

    public float[] getMat_transFloat3() {
        return mat_transFloat3;
    }

    public void setMat_transFloat3(float[] mat_transFloat3) {
        this.mat_transFloat3 = mat_transFloat3;
    }

    public String getMat_SymAsy() {
        return mat_SymAsy;
    }

    public void setMat_SymAsy(String mat_SymAsy) {
        this.mat_SymAsy = mat_SymAsy;
    }

    public boolean isCentro() {
        return centro;
    }

    public void setCentro(boolean centro) {
        this.centro = centro;
    }

    /**
     * @return the maxMultiSG
     */
    public int getMaxMultiSG() {
        return maxMultiSG;
    }

    /**
     * @param maxMultiSG the maxMultiSG to set
     */
    public void setMaxMultiSG(int maxMultiSG) {
        this.maxMultiSG = maxMultiSG;
    }
    
}
