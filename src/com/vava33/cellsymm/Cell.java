package com.vava33.cellsymm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.AtomProperties;
import com.vava33.jutils.Cif_file;
import com.vava33.jutils.VavaLogger;

public class Cell {

    private static VavaLogger log = Cell.getVavaLogger(Cell.class.getName());
    
    public static VavaLogger getVavaLogger(String name){
        VavaLogger l = new VavaLogger(name);
        l.setLogLevel("FINE");
        l.enableLogger(true);
        return l;
    }
    
    private int ihmax,ikmax,ilmax,ihmin,ikmin,ilmin;
    private int ilaue,ind;
    private float a,b,c,al,be,ga; //radians
    private float cr11,cr22,cr33,cr12,cr13,cr23;
    private SpaceGroup sg;
    private ArrayList<Atom> atoms;
    private ArrayList<HKLrefl> refl_obs;
    private ArrayList<HKLrefl> refl_obs_merged;
    private ArrayList<HKLrefl> refl_ext;
    
    
    public Cell(float a, float b, float c, float alfa, float beta, float gamma, int sgNum) {
        this.a=a;
        this.b=b;
        this.c=c;
        this.al=(float) FastMath.toRadians(alfa);
        this.be=(float) FastMath.toRadians(beta);
        this.ga=(float) FastMath.toRadians(gamma);
        sg = new SpaceGroup(sgNum);
        this.recip();
    }
    
    public Cell(String cifFile) {
        this.getAllfromCIF(cifFile);
        log.writeFloats("config", a,b,c,al,be,ga);
        this.recip();
        
    }
    
    private void recip() {
        
        double d2 = 1-FastMath.cos(al)*FastMath.cos(al)-FastMath.cos(be)*FastMath.cos(be)-FastMath.cos(ga)*FastMath.cos(ga)+2*FastMath.cos(al)*FastMath.cos(be)*FastMath.cos(ga);
        cr11 = (float) ((FastMath.sin(al)*FastMath.sin(al))/(d2*a*a));
        cr22 = (float) ((FastMath.sin(be)*FastMath.sin(be))/(d2*b*b));
        cr33 = (float) ((FastMath.sin(ga)*FastMath.sin(ga))/(d2*c*c));
        cr12 = (float) ((FastMath.cos(al)*FastMath.cos(be)-FastMath.cos(ga))/(d2*a*b));
        cr13 = (float) ((FastMath.cos(al)*FastMath.cos(ga)-FastMath.cos(be))/(d2*a*c));
        cr23 = (float) ((FastMath.cos(be)*FastMath.cos(ga)-FastMath.cos(al))/(d2*b*c));
        log.writeFloats("config", cr11,cr22,cr33, cr12,cr13,cr23);
    }
    
    public void latgen(float waveA, float t2max){
        double wumax = (FastMath.sin(FastMath.PI*t2max/360.)/waveA)*(FastMath.sin(FastMath.PI*t2max/360.)/waveA);
        ihmax = (int) FastMath.round(2*a*FastMath.sqrt(wumax));
        ikmax = (int) FastMath.round(2*a*FastMath.sqrt(wumax));
        ilmax = (int) FastMath.round(2*a*FastMath.sqrt(wumax));
        
        log.writeFloats("config", ihmax,ikmax,ilmax, wumax);
        
        int nm = sg.getMat_SymAsyFloat12().size();
        
        if (nm>0) this.laue();
        //TODO aqui hi ha l'opcio de si nm=0, fer el maxim del grup TGRUP ...
        
        refl_obs = new ArrayList<HKLrefl>();
        refl_obs_merged = new ArrayList<HKLrefl>();
        refl_ext = new ArrayList<HKLrefl>();
        recip();
        
        for (int l=ilmin;l<=ilmax;l++) {
            for (int k=ikmin;k<=ikmax;k++) {
                for (int h=ihmin;h<=ihmax;h++) {
                    log.fine(String.format("====  %4d %4d %4d ====", h,k,l));
                    if ((h==0)&&(k==0)&&(l==0))continue;
                    double wu = 0.25*(h*h*cr11+k*k*cr22+l*l*cr33+2*(h*k*cr12+h*l*cr13+k*l*cr23));
                    if (wu>wumax)continue;
                    double t2 = 360*FastMath.asin(waveA*FastMath.sqrt(wu))/FastMath.PI;

                    //eliminacio extincions reticle
                    ind = 0;
                    this.exlatt(h,k,l);
                    if (ind == 0) {
                        log.fine("EXTINGIDA PEL RETICLE");
                        refl_ext.add(new HKLrefl(h,k,l,t2));
                        continue;
                    }
                    ind = 0;
                    //Eliminacio extincions translacionals
                    this.extras(h,k,l);
                    if (ind == 0) {
                        log.fine("EXTINGIDA PER TRANS");
                        refl_ext.add(new HKLrefl(h,k,l,t2));
                        continue;
                    }
                    refl_obs.add(new HKLrefl(h,k,l,t2));
                    ind = 0;
                    this.asiuni(h,k,l);
                    if (ind == 0)continue;
                    refl_obs_merged.add(new HKLrefl(h,k,l,t2));
                }
            }
        }
        //ordenem per 2theta
        Collections.sort(refl_obs);
        Collections.sort(refl_obs_merged);
        Collections.sort(refl_ext);
        
        //calcul multiplicitat
        Iterator<HKLrefl> itrHKL = refl_obs_merged.iterator();
        float[] hs = new float[3];
        float[][] ks = new float[32][3];
        int nmat = this.getSg().getMat_SymAsyFloat12().size();
        while (itrHKL.hasNext()){
            HKLrefl hkl = itrHKL.next();
            int mult = 0;
            for (int i=0; i<nmat; i++) {
                hs[0]= hkl.h*this.getSg().getMat_SymAsyFloat12().get(i)[0] + hkl.k*this.getSg().getMat_SymAsyFloat12().get(i)[3] + hkl.l*this.getSg().getMat_SymAsyFloat12().get(i)[6];
                hs[1]= hkl.h*this.getSg().getMat_SymAsyFloat12().get(i)[1] + hkl.k*this.getSg().getMat_SymAsyFloat12().get(i)[4] + hkl.l*this.getSg().getMat_SymAsyFloat12().get(i)[7];
                hs[2]= hkl.h*this.getSg().getMat_SymAsyFloat12().get(i)[2] + hkl.k*this.getSg().getMat_SymAsyFloat12().get(i)[5] + hkl.l*this.getSg().getMat_SymAsyFloat12().get(i)[8];
                
                if (mult!=0) {
                    boolean loop = false;
                    for (int j=0; j<mult; j++) {
                        if ((hs[0]==ks[j][0]) && (hs[1]==ks[j][1]) && (hs[2]==ks[j][2]))loop=true;
                        if ((hs[0]==-ks[j][0]) && (hs[1]==-ks[j][1]) && (hs[2]==-ks[j][2]))loop=true;
                    }
                    if(loop)continue;
                }
                
                ks[mult][0] = hs[0];
                ks[mult][1] = hs[1];
                ks[mult][2] = hs[2];
                mult = mult +1;
            }
            hkl.setMult(mult*2);
        }
    }
    
    public void printHKLobs() {
        Iterator<HKLrefl> itrO = refl_obs.iterator();
        int i = 0;
        while (itrO.hasNext()) {
            itrO.next().toString();
            i = i+1;
        }
        log.debug(i+" reflections");

    }

    public void printHKLobsMerged() {
        int i=0;
        Iterator<HKLrefl> itrO = refl_obs_merged.iterator();
        while (itrO.hasNext()) {
            itrO.next().toString();
            i = i+1;
        }
        log.debug(i+" reflections");

    }
    
    public void printHKLext() {
        int i=0;
        Iterator<HKLrefl> itrE = refl_ext.iterator();
        while (itrE.hasNext()) {
            itrE.next().toString();
            i = i+1;
        }
        log.debug(i+" reflections");
    }
    
    
    private void laue() {
        ilaue = 0;
        switch (sg.getMat_SymAsyFloat12().size()) {
            case 1: // -1: ilaue=1
                ilaue = 1;
                ihmin = -ihmax;
                ikmin = -ikmax;
                break;
            case 2: // 2/m: ilaue=2
                ilaue = 2;
                ihmin = -ihmax;
                break;
            case 4: //mmm: ilaue=3   +  4/m: ilaue=4
                int isum = 0;
                for (int i=0;i<4;i++) {
                    isum = isum + (int)FastMath.abs(sg.getMat_SymAsyFloat12().get(i)[1]);
                }
                if (isum==0) {
                    ilaue=3;
                }else {
                    ilaue=4;
                }
                break;
            case 8: //4/mmm: ilaue=5
                ilaue = 5;
                break;
            case 3: //-3(H): ilaue=6
                ilaue = 6;
                ilmin=-ilmax;
                break;
            case 6: //-31m(H): ilaue=7  -3m1(H):ilaue=8  6/m:ilaue=9
                for (int i=0;i<6;i++) {
                    int xx = (int)sg.getMat_SymAsyFloat12().get(i)[0];
                    int yy = (int)sg.getMat_SymAsyFloat12().get(i)[4];
                    int zz = (int)sg.getMat_SymAsyFloat12().get(i)[8];
                    int yx = (int)sg.getMat_SymAsyFloat12().get(i)[3];
                    int xy = (int)sg.getMat_SymAsyFloat12().get(i)[1];
                    
                    if ((xx==-1)&&(yy==1)&&(zz==1)&&(yx==-1)) {
                        ilaue = 7;
                        ilmin=-ilmax;
                    }
                    if ((xx==1)&&(yy==-1)&&(zz==-1)&&(yx==1)) {
                        ilaue = 7;
                        ilmin=-ilmax;
                    }
                    if ((xx==-1)&&(yy==1)&&(zz==1)&&(xy==1)) {
                        ilaue = 8;
                    }
                    if ((xx==1)&&(yy==-1)&&(zz==-1)&&(xy==-1)) {
                        ilaue = 8;
                    }
                }
                if (ilaue==0) ilaue = 9;
                break;
            case 12: //6/mmm: ilaue=10  m-3: ilaue=11
                isum = 0;
                for (int i=0;i<12;i++) {
                    isum = isum + (int)FastMath.abs(sg.getMat_SymAsyFloat12().get(i)[8]);
                }
                if (isum==12) {
                    ilaue=10;
                }else {
                    ilaue=11;
                }
                break;
            case 24: //m-3m: ilaue=12
                ilaue = 12;
                break;
        }
    }
    
    // limits unitat asimètrica espai recíproc
    private void asiuni(int h, int k, int l) { 
        switch(ilaue) {
            case 1:
                if ((l==0)&&(k<0))return;
                if ((l==0)&&(k==0)&&(h<0))return;
                ind = 1;
                break;
            case 2:
                if ((l==0)&&(h<0))return;
                ind = 1;
                break;
            case 3:
                ind = 1;
                break;
            case 4:
                if ((h==0)&&(k>0))return;
                ind = 1;
                break;
            case 5:
                if (h>=k)ind=1;
                break;
            case 6:
                if ((k==0)&&(l<0))return;
                if ((h==0)&&(l<=0))return;
                ind=1;
                break;
            case 7:
                if (h>=k)ind=1;
                if ((k==0)&&(l<0))ind=0;
                break;
            case 8:
                if ((l==0)&&(h<k))return;
                ind=1;
                break;
            case 9:
                if ((h==0)&&(k>0))return;
                ind=1;
                break;
            case 10:
                if (h>=k)ind=1;
                break;
            case 11:
                if ((k>=h)&&(l>=h))ind=1;
                if ((h==l)&&(h<k))ind=0;
                break;
            case 12:
                if ((l>=k)&&(k>=h))ind=1;
                break;
        }
    }
    
    // lattice extinctions
    private void exlatt(int h, int k, int l) {
        int i2 = (k+l)%2;
        int i3 = (h+l)%2;
        int i4 = (h+k)%2;
        int i6 = (h+k+l)%2;
        int i7 = (-h+k+l)%3;
        switch (sg.getXarxa()) {
            case 'p':
                ind = 1;
                break;
            case 'a':
                if (i2==0)ind=1;
                break;
            case 'b':
                if (i3==0)ind=1;
                break;
            case 'c':
                if (i4==0)ind=1;
                break;
            case 'f':
                if ((i2==0)&&(i3==0)&&(i4==0))ind=1;
                break;
            case 'i':
                if (i6==0)ind=1;
                break;
            case 'r':
                if (i7==0)ind=1;
                break;
        }
    }

    // traslation extinctions
    private void extras(int h, int k, int l) {
        int iter = 1;
        if (sg.isCentro()) iter = 2;
        int signe = -1;
        
        for (int i=0;i<iter;i++) {
            signe = signe*-1;
            for (int j=0; j<sg.getMat_SymAsyFloat12().size();j++) {
                float tr = h*sg.getMat_SymAsyFloat12().get(j)[9]+k*sg.getMat_SymAsyFloat12().get(j)[10]+l*sg.getMat_SymAsyFloat12().get(j)[11];
                float ihr = h*sg.getMat_SymAsyFloat12().get(j)[0]+k*sg.getMat_SymAsyFloat12().get(j)[3]+l*sg.getMat_SymAsyFloat12().get(j)[6];
                float ikr = h*sg.getMat_SymAsyFloat12().get(j)[1]+k*sg.getMat_SymAsyFloat12().get(j)[4]+l*sg.getMat_SymAsyFloat12().get(j)[7];
                float ilr = h*sg.getMat_SymAsyFloat12().get(j)[2]+k*sg.getMat_SymAsyFloat12().get(j)[5]+l*sg.getMat_SymAsyFloat12().get(j)[8];
                int ips = (int) ((360*signe*tr)%360);
                if ((h==(signe*ihr))&&(k==(signe*ikr))&&(l==(signe*ilr))&&(ips!=0))return;
            }
        }
        ind = 1;
    }

    public void getAllfromCIF(String cif_file_path) {
        Cif_file cf = new Cif_file(cif_file_path);
        this.atoms = new ArrayList<Atom>();
        for (int i=0; i<cf.getNAtoms();i++) {
            //put in the unit cell
            float x = cf.getAtomXcrys(i) + 20.0f;
            x = x%(int)FastMath.floor(x);
            float y = cf.getAtomYcrys(i) + 20.0f;
            y = y%(int)FastMath.floor(y);
            float z = cf.getAtomZcrys(i) + 20.0f;
            z = z%(int)FastMath.floor(z);
            float occ = cf.getAtomOcc(i);
            float dis = cf.getAtomDisp(i);
            String lab = cf.getAtomLabel(i);
            String tip = cf.getAtomType(i);
            atoms.add(new Atom(tip,lab,x,y,z,occ,dis));
        }
        this.a=cf.getA();
        this.b=cf.getB();
        this.c=cf.getC();
        this.al=(float) FastMath.toRadians(cf.getAl());
        this.be=(float) FastMath.toRadians(cf.getBe());
        this.ga=(float) FastMath.toRadians(cf.getGa());
        this.sg = new SpaceGroup(cf.getSgNum());
        
    }
   
    public void calcInten(float wave) {
        try {
            if(atoms.size()<=0) {
                log.info("no atoms found");
                return;
            }
            if(refl_obs.size()<=0) {
                log.info("no reflections found");
                return;
            }
        }catch(Exception e) {
            e.printStackTrace();
            log.info("atoms or reflections missing");
            return;
        }
        float tolposAtoms = 0.01f;
        
        //primer mirarem els atoms independents despres d'aplicar totes les operacions de simetria
        ArrayList<Atom> indAt = new ArrayList<Atom>();
        int nmat = this.getSg().getMat_SymAsyFloat12().size();
        int ntranslat = (int) (this.getSg().getMat_transFloat3().length/3.);
        Iterator<Atom> itrat = atoms.iterator();
        while(itrat.hasNext()) {
            int multAtom = 0;
            Atom at = itrat.next();
            for (int j=0;j<nmat;j++) {
                float xn = this.getSg().getMat_SymAsyFloat12().get(j)[0]*at.getXcryst() + this.getSg().getMat_SymAsyFloat12().get(j)[3]*at.getYcryst() + this.getSg().getMat_SymAsyFloat12().get(j)[6]*at.getZcryst();
                xn = xn + this.getSg().getMat_SymAsyFloat12().get(j)[9]; //9 es trasl x
                float yn = this.getSg().getMat_SymAsyFloat12().get(j)[1]*at.getXcryst() + this.getSg().getMat_SymAsyFloat12().get(j)[4]*at.getYcryst() + this.getSg().getMat_SymAsyFloat12().get(j)[7]*at.getZcryst();
                yn = yn + this.getSg().getMat_SymAsyFloat12().get(j)[10];
                float zn = this.getSg().getMat_SymAsyFloat12().get(j)[2]*at.getXcryst() + this.getSg().getMat_SymAsyFloat12().get(j)[5]*at.getYcryst() + this.getSg().getMat_SymAsyFloat12().get(j)[8]*at.getZcryst();
                zn = zn + this.getSg().getMat_SymAsyFloat12().get(j)[11];
                
                //posem a la cel·la unitat
                xn = xn + 20.0f;
                xn = xn%((int)FastMath.floor(xn));
                yn = yn + 20.0f;
                yn = yn%((int)FastMath.floor(yn));
                zn = zn + 20.0f;
                zn = zn%((int)FastMath.floor(zn));
                
                log.writeNameNumPairs("config", true, "xn,yn,zn", xn,yn,zn);
                Iterator<Atom> itrAtInd = indAt.iterator();
                boolean existing = false;
                while (itrAtInd.hasNext()){
                    Atom atInd = itrAtInd.next();
                    if ((FastMath.abs(atInd.getXcryst()-xn)<tolposAtoms) &&
                            (FastMath.abs(atInd.getYcryst()-yn)<tolposAtoms) &&
                            (FastMath.abs(atInd.getZcryst()-zn)<tolposAtoms)) {
                        //son iguals
                        log.debug("atom existing");
                        existing = true;
                    }
                }
                if (!existing) {
                    indAt.add(new Atom(at.getTipus(),at.getLabel(),xn,yn,zn,at.getOcupancy(),at.getAdp()));    
                    multAtom = multAtom +1;
                }
            }
            
            //ho repeteixo en el cas que sigui centrosimetric
            if (this.getSg().isCentro()) {
                for (int j=0;j<nmat;j++) {
                    float xn = this.getSg().getMat_SymAsyFloat12().get(j)[0]*at.getXcryst()*-1 + this.getSg().getMat_SymAsyFloat12().get(j)[3]*at.getYcryst()*-1 + this.getSg().getMat_SymAsyFloat12().get(j)[6]*at.getZcryst()*-1;
                    xn = xn + this.getSg().getMat_SymAsyFloat12().get(j)[9]; //9 es trasl x
                    float yn = this.getSg().getMat_SymAsyFloat12().get(j)[1]*at.getXcryst()*-1 + this.getSg().getMat_SymAsyFloat12().get(j)[4]*at.getYcryst()*-1 + this.getSg().getMat_SymAsyFloat12().get(j)[7]*at.getZcryst()*-1;
                    yn = yn + this.getSg().getMat_SymAsyFloat12().get(j)[10];
                    float zn = this.getSg().getMat_SymAsyFloat12().get(j)[2]*at.getXcryst()*-1 + this.getSg().getMat_SymAsyFloat12().get(j)[5]*at.getYcryst()*-1 + this.getSg().getMat_SymAsyFloat12().get(j)[8]*at.getZcryst()*-1;
                    zn = zn + this.getSg().getMat_SymAsyFloat12().get(j)[11];
                    
                    //posem a la cel·la unitat
                    xn = xn + 20.0f;
                    xn = xn%((int)FastMath.floor(xn));
                    yn = yn + 20.0f;
                    yn = yn%((int)FastMath.floor(yn));
                    zn = zn + 20.0f;
                    zn = zn%((int)FastMath.floor(zn));
                    
                    log.writeNameNumPairs("config", true, "xn,yn,zn", xn,yn,zn);
                    Iterator<Atom> itrAtInd = indAt.iterator();
                    boolean existing = false;
                    while (itrAtInd.hasNext()){
                        Atom atInd = itrAtInd.next();
                        if ((FastMath.abs(atInd.getXcryst()-xn)<tolposAtoms) &&
                                (FastMath.abs(atInd.getYcryst()-yn)<tolposAtoms) &&
                                (FastMath.abs(atInd.getZcryst()-zn)<tolposAtoms)) {
                            //son iguals
                            log.debug("atom existing");
                            existing = true;
                        }
                    }
                    if (!existing) {
                        indAt.add(new Atom(at.getTipus(),at.getLabel(),xn,yn,zn,at.getOcupancy(),at.getAdp()));    
                        multAtom = multAtom +1;
                    }
                }
            }
            
            log.debug(at.getLabel()+ " indep pos after symm= "+multAtom);
            for (int j=0;j<ntranslat;j++) {
                int index = j*3;
                float xn = this.getSg().getMat_transFloat3()[index]+at.getXcryst();
                float yn = this.getSg().getMat_transFloat3()[index+1]+at.getYcryst();
                float zn = this.getSg().getMat_transFloat3()[index+2]+at.getZcryst();
                //posem a la cel·la unitat
                xn = xn + 20.0f;
                xn = xn%((int)FastMath.floor(xn));
                yn = yn + 20.0f;
                yn = yn%((int)FastMath.floor(yn));
                zn = zn + 20.0f;
                zn = zn%((int)FastMath.floor(zn));
                log.writeNameNumPairs("config", true, "xn,yn,zn", xn,yn,zn);
                Iterator<Atom> itrAtInd = indAt.iterator();
                boolean existing = false;
                while (itrAtInd.hasNext()){
                    Atom atInd = itrAtInd.next();
                    if ((FastMath.abs(atInd.getXcryst()-xn)<tolposAtoms) &&
                            (FastMath.abs(atInd.getYcryst()-yn)<tolposAtoms) &&
                            (FastMath.abs(atInd.getZcryst()-zn)<tolposAtoms)) {
                        existing = true;
                    }
                }
                if (!existing) {
                    indAt.add(new Atom(at.getTipus(),at.getLabel(),xn,yn,zn,at.getOcupancy(),at.getAdp()));    
                    multAtom = multAtom +1;
                }

            }
            log.debug(at.getLabel()+ " indep pos after translat = "+multAtom);
            at.setMultiplicityPosition(multAtom);
            log.debug(at.getLabel()+" multiplicity = "+at.getMultiplicityPosition());
            log.debug("sg multiplicity general = "+this.getSg().getMaxMultiSG());
        }
        log.debug("total indepenent atoms = "+indAt.size());
        
        //ARA CALCULEM ELS FACTORS D'ESTRUCTURA
        Iterator<HKLrefl> itrhkl = refl_obs_merged.iterator();
        while (itrhkl.hasNext()) {
            HKLrefl hkl = itrhkl.next();
            double A = 0;
            double B = 0;
            Iterator<Atom> itrIndAt = indAt.iterator();
            while (itrIndAt.hasNext()){
                Atom at = itrIndAt.next();
                double f0 = AtomProperties.calcfform_cromer(at.getTipus(), wave, hkl.t2);
                double cos = FastMath.cos((FastMath.PI*2)*(hkl.h*at.getXcryst()+hkl.k*at.getYcryst()+hkl.l*at.getZcryst()));
                double sin = FastMath.sin((FastMath.PI*2)*(hkl.h*at.getXcryst()+hkl.k*at.getYcryst()+hkl.l*at.getZcryst()));
                //considerem iso 0.05
                float meanDisplacementA = 0.05f;
                try {
                    meanDisplacementA = at.getAdp();    
                }catch(Exception e) {
                    log.debug("no adp found");
                }
                
                float Biso = (float) (8 * (FastMath.PI * FastMath.PI) * (meanDisplacementA * meanDisplacementA));
                float ST_L = (float) (FastMath.sin(FastMath.toRadians(hkl.t2)/2)/wave);
                float fB = (float) FastMath.exp(-1*Biso*ST_L*ST_L);
                A = A + f0 * cos * fB;
                B = B + f0 * sin * fB;
            }
            hkl.ycalc = (A*A + B*B);
        }
    }
    
    private class PointPatt1D{
        double t2;
        double ycal;
        public PointPatt1D(double t2i, double ycali) {
            t2=t2i;
            ycal=ycali;
        }
    }
    public void createPatternPV(double t2i, double t2f, double step, double waveA, double fwhm) {
        double scale = 1;
        ArrayList<PointPatt1D> patt = new ArrayList<PointPatt1D>();
        double t2p = t2i;
        while (t2p < t2f) {
            double inten = 0;
            //contribucio de les reflexions al punt
            Iterator<HKLrefl> itrhkl = refl_obs_merged.iterator();
            while (itrhkl.hasNext()) {
                HKLrefl hkl = itrhkl.next();
                double wu = 0.25*(hkl.h*hkl.h*cr11+hkl.k*hkl.k*cr22+hkl.l*hkl.l*cr33+2*(hkl.h*hkl.k*cr12+hkl.h*hkl.l*cr13+hkl.k*hkl.l*cr23));
                double t2deg = 2*FastMath.toDegrees(FastMath.asin(waveA*FastMath.sqrt(wu)));
                double thrad = FastMath.toRadians(t2deg/2);
                double lorentz = 1/((4*FastMath.sin(thrad)*FastMath.sin(thrad)*FastMath.cos(thrad)));
                double c0=2.7726; // 4*ln(2) used in gauss part
                double g1 = FastMath.sqrt(c0)/FastMath.sqrt(FastMath.PI);
                double g2 = FastMath.exp(-c0*(t2p-t2deg)*(t2p-t2deg)/(fwhm*fwhm));
                inten = inten + (g1*g2*scale*hkl.mult*lorentz*hkl.ycalc)/fwhm;
            }
            patt.add(new PointPatt1D(t2p,inten));
            t2p = t2p + step;
        }
        
        //Escriure fitxer format xye
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/testPatt.dat")));
            // ESCRIBIM AL FITXER:
            String linia = String.format(Locale.ENGLISH, "# %s","test");
            output.println(linia);
            Iterator<PointPatt1D> itp = patt.iterator();
            while(itp.hasNext()){
                PointPatt1D p = itp.next();
                linia = String.format(Locale.ENGLISH, "  %.7E  %.7E  %.7E",p.t2,p.ycal,0.01);
                output.println(linia);
            }
            output.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            log.warning("error writting dat file");
        }
    }
    
    public SpaceGroup getSg() {
        return sg;
    }

    public void setSg(SpaceGroup sg) {
        this.sg = sg;
    }
    
    
    
}
