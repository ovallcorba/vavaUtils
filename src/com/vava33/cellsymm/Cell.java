package com.vava33.cellsymm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import com.vava33.cellsymm.CellSymm_global.CrystalCentering;
import com.vava33.cellsymm.CellSymm_global.CrystalFamily;
import com.vava33.jutils.AtomProperties;
import com.vava33.jutils.Cif_file;
import com.vava33.jutils.VavaLogger;
import com.vava33.jutils.CrystOps.CrystalSystem;

public class Cell {

    private static final String className = "Cell";
    private static VavaLogger log = CellSymm_global.getVavaLogger(className);    
    
    private double a,b,c,al,be,ga; //radians
    private double aStar,bStar,cStar,alStar,beStar,gaStar;//constants reciproques
    private SpaceGroup sg;
    private ArrayList<Atom> atoms;
    private ArrayList<HKLrefl> refl_allowed_asy; //reflexions permeses de la unitat asimetrica
    private ArrayList<HKLrefl> refl_extinct_asy; //reflexions extingides de la unitat asimetrica
    RealMatrix G,Gstar;//metricMatrix
    private CrystalFamily cf;
    private CrystalCentering cc;
    
    
    public Cell(double a, double b, double c, double alfa, double beta, double gamma, boolean inDegrees, CrystalFamily cf, CrystalCentering cc) {
        this.a=a;
        this.b=b;
        this.c=c;
        if(inDegrees) {
            this.al= FastMath.toRadians(alfa);
            this.be= FastMath.toRadians(beta);
            this.ga= FastMath.toRadians(gamma);
        }else {
            this.al= alfa;
            this.be= beta;
            this.ga= gamma;
            
        }
        this.cf = cf;
        this.cc = cc;
        setMaxSymmSG();
        initCalcs();
    }
    
    public Cell(double a, double b, double c, double alfa, double beta, double gamma, boolean inDegrees, SpaceGroup sg) {
        this(a,b,c,alfa,beta,gamma,inDegrees,sg.getCrystalFamily(),sg.getCrystalCentering());
        this.sg=sg;
    }
    
    public Cell(double a, double b, double c, double alfa, double beta, double gamma, boolean inDegrees, CrystalFamily cf) {
        this(a,b,c,alfa,beta,gamma,inDegrees,cf,CrystalCentering.P);
    }
    
    public Cell(double a, double b, double c, double alfa, double beta, double gamma, boolean inDegrees) {
        this(a,b,c,alfa,beta,gamma,inDegrees,CrystalFamily.NONE);
    }
    
    public Cell(String cifFile,boolean confirmReadedCifDataDialog) {
        this.getAllfromCIF(cifFile,confirmReadedCifDataDialog);
        log.writeFloats("config", a,b,c,al,be,ga);
        initCalcs();
    }
    
    public Cell(Cif_file cf) {
        this.getAllfromCIF(cf);
        log.writeFloats("config", a,b,c,al,be,ga);
        initCalcs();
    }
    
    private void getAllfromCIF(Cif_file cf) {
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
        this.sg = CellSymm_global.getSpaceGroupByNum(cf.getSgNum());
    }
    
    private void getAllfromCIF(String cif_file_path,boolean confirmReadedCifDataDialog) {
        Cif_file cf = new Cif_file(cif_file_path,confirmReadedCifDataDialog);
        getAllfromCIF(cf);
    }
    
    private void initCalcs() {
        this.calcMetricMatrix();
        this.calcGstarMatrix();
        this.calcReciprocalParameters(); //NO CALDRIA
    }
    
    private void setMaxSymmSG() {
        switch (this.cf) {
        case CUBIC:this.sg=CellSymm_global.getSpaceGroupByNum(221);break;
        case HEXA:this.sg=CellSymm_global.getSpaceGroupByNum(191);break;
        case MONO:this.sg=CellSymm_global.getSpaceGroupByNum(10);break;
        case ORTO:this.sg=CellSymm_global.getSpaceGroupByNum(47);break;
        case TETRA:this.sg=CellSymm_global.getSpaceGroupByNum(123);break;
        case TRIC:this.sg=CellSymm_global.getSpaceGroupByNum(2);break;
        //TODO:ferho per sistema? cal afegir trigonal o rhombo??
        default:this.sg=CellSymm_global.getSpaceGroupByNum(2);break;
        }
    }
    
    public void calcMetricMatrix() {
        G = MatrixUtils.createRealMatrix(3, 3); //rows, columns
        G.setEntry(0, 0, a*a);
        G.setEntry(1, 1, b*b);
        G.setEntry(2, 2, c*c);
        G.setEntry(0, 1, a*b*FastMath.cos(ga));
        G.setEntry(0, 2, a*c*FastMath.cos(be));
        G.setEntry(1, 0, G.getEntry(0, 1));
        G.setEntry(2, 0, G.getEntry(0, 2));
        G.setEntry(2, 1, b*c*FastMath.cos(al));
        G.setEntry(1, 2, G.getEntry(2, 1));
//        return G;
    }
    
    public void calcGstarMatrix() {
        Gstar=null;
        try {
            Gstar = MatrixUtils.inverse(G);  
        }catch(Exception e) {
            System.out.println(e.getMessage()+" Error inverting metric matrix");
            e.printStackTrace();
        }
//        return Gstar;
    }
    
    public void calcReciprocalParameters() {
        aStar = FastMath.sqrt(Gstar.getEntry(0, 0));
        bStar = FastMath.sqrt(Gstar.getEntry(1, 1));
        cStar = FastMath.sqrt(Gstar.getEntry(2, 2));
        gaStar = FastMath.acos(Gstar.getEntry(0, 1)/(aStar*bStar)); 
        beStar = FastMath.acos(Gstar.getEntry(0, 2)/(aStar*cStar));
        alStar = FastMath.acos(Gstar.getEntry(2, 1)/(cStar*bStar));
        log.writeNameNumPairs("config", true, "aStar,bStar,cStar, alStar,beStar,gaStar", aStar,bStar,cStar, alStar,beStar,gaStar);
    }

    private int[] limithkl_GeneralEQ(double Qmax) {
        int[] maxHKL = new int[3];

        maxHKL[0]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(0, 0)));
        maxHKL[1]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(1, 1)));
        maxHKL[2]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(2, 2)));
        
        return maxHKL;
    }
    
    //takes into consideration the limits according to the crystal family (max/min)
    private int[] limithkl_MaxMin_GeneralEQ(double Qmax) {
        int[] maxminHKL = new int[6];

        maxminHKL[0]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(0, 0)));
        maxminHKL[1]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(1, 1)));
        maxminHKL[2]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(2, 2)));
        maxminHKL[3]=0;
        maxminHKL[4]=0;
        maxminHKL[5]=0;
        
        switch(this.cf) {
        case CUBIC: // m-3m: ilaue=12
          //nothing
            break;
        case TETRA: // 4/mmm: ilaue=5
            //nothing
            break;
        case HEXA:  // 6/mmm (ilaue=10)
          //nothing
            break;
        case ORTO:  // mmm: ilaue=3
            //nothing
            break;
        case MONO:  // 2/m: ilaue=2
            maxminHKL[3]=-maxminHKL[0];
            break;
        case TRIC:  // -1: ilaue=1
            maxminHKL[3]=-maxminHKL[0];
            maxminHKL[4]=-maxminHKL[1];
            break;
        default:
            break;
        }
        
        return maxminHKL;
    }
    
    //aquest es pelat, bo per la indexacio
    public  ArrayList<HKLrefl> generateHKLsAsymetricUnitCrystalFamily(double Qmax){
        return this.generateHKLsAsymetricUnitCrystalFamily(Qmax,false,false,false,false);
    }
    
    //AQUEST ES COMPLERT
    //genera la llista d'hkls de la UNITAT ASIMÈTRICA per la família cristal·lina de la cel·la (opcinos de consdierar centratges i/o grup espacial)
    public  ArrayList<HKLrefl> generateHKLsAsymetricUnitCrystalFamily(double Qmax, boolean exCentering, boolean exSymmetry, boolean sortbytth, boolean calcMultiplicity) {
        int[] maxminHKL = this.limithkl_MaxMin_GeneralEQ(Qmax);
        int ihmax = maxminHKL[0];
        int ikmax = maxminHKL[1];
        int ilmax = maxminHKL[2];
        int ihmin = maxminHKL[3];
        int ikmin = maxminHKL[4];
        int ilmin = maxminHKL[5];
        
        log.configf("Max/Min HKL for cell %.4f %.4f %.4f %.3f %.3f %.3f are [%d %d %d]/[%d %d %d]",this.a,this.b,this.c,getAlfaDeg(),getBetaDeg(),getGammaDeg(),ihmax,ikmax,ilmax,ihmin,ikmin,ilmin);
        log.config("crystal family: "+this.cf);
        
        refl_allowed_asy = new ArrayList<HKLrefl>();
        
        for (int l=ilmin;l<=ilmax;l++) {
            for (int k=ikmin;k<=ikmax;k++) {
                for (int h=ihmin;h<=ihmax;h++) {
                    if ((h==0)&&(k==0)&&(l==0))continue;
                    
                    switch(this.cf) {
                    case CUBIC: // m-3m: ilaue=12
                        if ((h<k)&&(k<l))continue;
                        if ((h*h+k*k+l*l)>maxminHKL[0]*maxminHKL[0]) continue; //TODO: revisar
                        break;
                    case TETRA: // 4/mmm: ilaue=5
                        if (h<k)continue;
                        break;
                    case HEXA:  // 6/mmm (ilaue=10)
                        if (h<k)continue;
                        break;
                    case ORTO:  // mmm: ilaue=3
                        //s'accepten totes??
                        break;
                    case MONO:  // 2/m: ilaue=2
                        if ((l==0)&&(h<0))continue; //hauria de ser h>=0?
                        break;
                    case TRIC:  // -1: ilaue=1
                        if ((l==0)&&(k<0))continue;
                        if ((l==0)&&(k==0)&&(h<0))continue;
                        break;
                    default:
                        break;
                    }
                    
                    if (exCentering) {
                        //considerar extincions degudes a centratge
                        if (!isReflectionAllowedByCentering(h,k,l))continue;
                    }
                    
                    if (exSymmetry) {
                        //considerar extincions degudes a traslacions del grup espacial
                        if (!isReflectionAllowedBySymmetry(h,k,l))continue;
                    }
                    
                    // si arribem aquí es que pertany a la unitat asimetrica amb les condicions acordades (simetria, traslacions)
                    refl_allowed_asy.add(new HKLrefl(h,k,l,this.calcDspHKL(h, k, l),this)); //TODO COMPARAR LA VELOCIAT AMB I SENSE CALCULAR DSPACING (i activar ordenament en consequencia
                }
            }
        }
        
        if (sortbytth) {
            Collections.sort(refl_allowed_asy);  
        }
        
        if (calcMultiplicity) {
            Iterator<HKLrefl> itrHKL = refl_allowed_asy.iterator();
            while (itrHKL.hasNext()){
                HKLrefl hkl = itrHKL.next();
                sg.calcMultiplicityReflection(hkl);
            }
        }
        log.debug("num. refls. "+refl_allowed_asy.size());
        return refl_allowed_asy;
    }
    
    public double calcDspHKL(int h, int k, int l) {
        double a2 = a*a;
        double b2 = b*b;
        double c2 = c*c;
        double sbe = FastMath.sin(be);
        double cbe = FastMath.cos(be);
        double sal = FastMath.sin(al);
        double cal = FastMath.cos(al);
        double sga = FastMath.sin(ga);
        double cga = FastMath.cos(ga);
        
        double hsqcalc = b2*c2*sal*sal*h*h + a2*c2*sbe*sbe*k*k + a2*b2*sga*sga*l*l + 2*a*b*c2*(cal*cbe-cga)*h*k + 2*a2*b*c*(cbe*cga-cal)*k*l + 2*a*b2*c*(cga*cal-cbe)*h*l;
        double v2 = (a*b*c*FastMath.sqrt(1-cal*cal-cbe*cbe-cga*cga+2*cal*cbe*cga));
        v2 = v2*v2;
        hsqcalc = hsqcalc/v2;
        double dcalc = FastMath.sqrt(1/hsqcalc);
        return dcalc;
    }
    
    public String getListAsString_HKLMerged_dsp_Fc2() {
        Iterator<HKLrefl> itrO = refl_allowed_asy.iterator();
        StringBuilder sb = new StringBuilder();
        while (itrO.hasNext()) {
            sb.append(itrO.next().toString_HKL_D_Fc2());
            sb.append(System.lineSeparator());
        }
        return sb.toString();

    }
    
    public String getListAsString_HKLMerged_tth_mult_Fc2(float wave) {
        Iterator<HKLrefl> itrO = refl_allowed_asy.iterator();
        StringBuilder sb = new StringBuilder();
        while (itrO.hasNext()) {
            sb.append(itrO.next().toString_HKL_tth_mult_Fc2(wave));
            sb.append(System.lineSeparator());
        }
        return sb.toString();

    }
    
    public String getListAsString_HKLextinct_dsp_Fc2() {
        Iterator<HKLrefl> itrO = refl_extinct_asy.iterator();
        StringBuilder sb = new StringBuilder();
        while (itrO.hasNext()) {
            sb.append(itrO.next().toString_HKL_D_Fc2());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    
    // Extincions degudes al reticle (TODO: moure a SG?... el deixo aqui per si volgues canviar centratge i no grup...
    private boolean isReflectionAllowedByCentering(int h, int k, int l) {
        boolean hpar = h%2==0;
        boolean kpar = k%2==0;
        boolean lpar = l%2==0;
        boolean klpar = (k+l)%2==0;
        boolean hlpar = (h+l)%2==0;
        boolean hkpar = (h+k)%2==0;
        boolean hklpar = (h+k+l)%2==0;
        boolean r1 = (-h+k+l)%3==0;
        boolean r2 = (h-k+l)%3==0;
        switch (this.cc) {
        case A:if (klpar) return true;
        case B:if (hlpar) return true;
        case C:if (hkpar) return true;
        case F:
            if (hpar&&kpar&&lpar)return true;
            if (!hpar&&!kpar&&!lpar)return true;
            return false;
        case I:if (hklpar)return true;
        case P:return true;
        case R:
            if (r1)return true;
            if (r2)return true;
            return false;
        default:return true;
        }
    }
    
    //   Extincions degudes a les translacions
    private boolean isReflectionAllowedBySymmetry(int h, int k, int l) {
        return sg.isReflectionAllowedBySymmetry(h, k, l);
    }

   
    public void calcInten(boolean estimBiso) {
        try {
            if(atoms.size()<=0) {
                log.info("no atoms found");
                return;
            }
            if(refl_allowed_asy.size()<=0) {
                log.info("no reflections found");
                return;
            }
        }catch(Exception e) {
            e.printStackTrace();
            log.info("atoms or reflections missing");
            return;
        }
        
        //primer mirarem els atoms independents despres d'aplicar totes les operacions de simetria
        ArrayList<Atom> indAt = new ArrayList<Atom>();
    
        
        for(Atom at:atoms) {
            indAt.addAll(at.getEquivalents(this.sg,true,true,true));
        }
        
        log.debug("total indepenent atoms = "+indAt.size());
        
        for(HKLrefl hkl:refl_allowed_asy) {
            hkl.calcY(indAt, estimBiso);
        }
    }
    
    public void normIntensities(float maxI) {
        //primer busquem factor
        Iterator<HKLrefl> itrO = refl_allowed_asy.iterator();
        double maxIref = 0;
        while (itrO.hasNext()) {
            HKLrefl h = itrO.next();
            if(h.ycalc>maxIref)maxIref = h.ycalc;
        }
        double factor = maxI/maxIref;
        //now we apply factor
        itrO = refl_allowed_asy.iterator();
        while (itrO.hasNext()) {
            HKLrefl h = itrO.next();
            h.ycalc=h.ycalc*factor;
        }
    }
    
    
    //TODO: AQUESTA PART DE CREACIO DEL PATTERN ENCARA S'HA D'ARREGLAR, REVISAR I IMPLEMENTAR COM I ON TOCA
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
            Iterator<HKLrefl> itrhkl = refl_allowed_asy.iterator();
            while (itrhkl.hasNext()) {
                HKLrefl hkl = itrhkl.next();
                double wu = 0.25*(hkl.h*hkl.h*Gstar.getEntry(0, 0)+hkl.k*hkl.k*Gstar.getEntry(1, 1)+hkl.l*hkl.l*Gstar.getEntry(2, 2)+2*(hkl.h*hkl.k*Gstar.getEntry(0, 1)+hkl.h*hkl.l*Gstar.getEntry(0, 2)+hkl.k*hkl.l*Gstar.getEntry(1, 2)));
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
    
    private double getAlfaDeg() {
        return FastMath.toDegrees(this.al);
    }
    private double getBetaDeg() {
        return FastMath.toDegrees(this.be);
    }
    private double getGammaDeg() {
        return FastMath.toDegrees(this.ga);
    }
    
}
