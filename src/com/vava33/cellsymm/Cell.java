package com.vava33.cellsymm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

import com.vava33.cellsymm.CellSymm_global.CrystalCentering;
import com.vava33.cellsymm.CellSymm_global.CrystalFamily;
import com.vava33.jutils.Cif_file;
import com.vava33.jutils.VavaLogger;

public class Cell {

    private static final String className = "Cell";
    private static VavaLogger log = CellSymm_global.getVavaLogger(className);    
    
    private double a,b,c,al,be,ga; //radians
    public double sda, sdb, sdc, sdal, sdbe, sdga; //errors
    private double vol;
    private double aStar,bStar,cStar,alStar,beStar,gaStar;//constants reciproques
    private SpaceGroup sg;
    private List<Atom> atoms;
    private List<HKLrefl> refl_allowed_asy; //reflexions permeses de la unitat asimetrica
    private List<HKLrefl> refl_extinct_asy; //reflexions extingides de la unitat asimetrica
    public RealMatrix G,Gstar;//metricMatrix
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
        sda=0;sdb=0;sdc=0;sdal=0;sdbe=0;sdga=0;
        this.cf = cf;
        this.cc = cc;
        this.calcVolumeMetricEq();
        setMaxSymmSG();
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
    }
    
    //copy all, al principi nomes tenia els parametres de cella
    public Cell(Cell c) {
        this(c.a,c.b,c.c,c.al,c.be,c.ga,false,c.getSg());
        this.setErrors(c.sda, c.sdb, c.sdc, c.sdal, c.sdbe, c.sdga);
    }
    
    public void setErrors(double errA, double errB, double errC, double errAlRad, double errBeRad, double errGaRad) {
        this.sda=errA;
        this.sdb=errB;
        this.sdc=errC;
        this.sdal=errAlRad;
        this.sdbe=errBeRad;
        this.sdga=errGaRad;
    }
    
    
    public Cell(Cif_file cf) {
        this.getAllfromCIF(cf);
        log.writeFloats("config", a,b,c,al,be,ga);
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
        if (cf.getSgNum()<=0) {
            this.sg = CellSymm_global.getSpaceGroupByName(cf.getSgString(), true);
        }else {
            this.sg = CellSymm_global.getSpaceGroupByNum(cf.getSgNum());
        }
        //crystalcentering and crystalfamily
        this.cc=this.sg.getCrystalCentering();
        this.cf=this.sg.getCrystalFamily();
    }
    
    private void getAllfromCIF(String cif_file_path,boolean confirmReadedCifDataDialog) {
        Cif_file cf = new Cif_file(cif_file_path,confirmReadedCifDataDialog);
        getAllfromCIF(cf);
    }

    
    private void setMaxSymmSG() {
        switch (this.cf) {
        case CUBIC:this.sg=CellSymm_global.getSpaceGroupByNum(221);break;
        case HEXA:this.sg=CellSymm_global.getSpaceGroupByNum(191);break;
        case MONO:this.sg=CellSymm_global.getSpaceGroupByNum(10);break;
        case ORTO:this.sg=CellSymm_global.getSpaceGroupByNum(47);break;
        case TETRA:this.sg=CellSymm_global.getSpaceGroupByNum(123);break;
        case TRIC:this.sg=CellSymm_global.getSpaceGroupByNum(2);break;
        default:this.sg=CellSymm_global.getSpaceGroupByNum(2);break;
        }
    }
    
    private void calcMetricMatrix() {
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
    }
    
    private void calcGstarMatrix() {
        Gstar=null;
        if (G==null)calcMetricMatrix();
        try {
            Gstar = MatrixUtils.inverse(G);  
        }catch(Exception e) {
            System.out.println(e.getMessage()+" Error inverting metric matrix");
            e.printStackTrace();
        }
//        return Gstar;
    }
    
    @SuppressWarnings("unused")
    private void calcReciprocalParameters() {
        if (Gstar==null)calcGstarMatrix();
        aStar = FastMath.sqrt(Gstar.getEntry(0, 0));
        bStar = FastMath.sqrt(Gstar.getEntry(1, 1));
        cStar = FastMath.sqrt(Gstar.getEntry(2, 2));
        gaStar = FastMath.acos(Gstar.getEntry(0, 1)/(aStar*bStar)); 
        beStar = FastMath.acos(Gstar.getEntry(0, 2)/(aStar*cStar));
        alStar = FastMath.acos(Gstar.getEntry(2, 1)/(cStar*bStar));
        log.writeNameNumPairs("fine", true, "aStar,bStar,cStar, alStar,beStar,gaStar", aStar,bStar,cStar, alStar,beStar,gaStar);
    }
    
    
    //takes into consideration the limits according to the crystal family (max/min)
    private int[] limithkl_MaxMin_GeneralEQ(double Qmax) {
        if (Gstar==null) {
            this.calcGstarMatrix();
        }
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
        
        log.configf("Max/Min HKL for cell %.4f %.4f %.4f %.3f %.3f %.3f with Qmax=%.5f are [%d %d %d]/[%d %d %d]",this.a,this.b,this.c,getAlfaDeg(),getBetaDeg(),getGammaDeg(),Qmax,maxminHKL[0],maxminHKL[1],maxminHKL[2],maxminHKL[3],maxminHKL[4],maxminHKL[5]);
        log.config("crystal family: "+this.cf);
        
        return maxminHKL;
    }
    
    @SuppressWarnings("unused")
    private int[] limithkl_MaxMin_ByCrystFamily(double Qmax) {
        int[] maxminHKL = new int[6];
        maxminHKL[3]=0;
        maxminHKL[4]=0;
        maxminHKL[5]=0;
        
        switch(this.cf) {
        case CUBIC: // m-3m: ilaue=12
            maxminHKL[0]=(int) (int) FastMath.sqrt((Qmax)*(a*a));
            maxminHKL[1]=maxminHKL[0];
            maxminHKL[2]=maxminHKL[0];
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

        log.configf("Max/Min HKL for cell %.4f %.4f %.4f %.3f %.3f %.3f with Qmax=%.5f are [%d %d %d]/[%d %d %d]",this.a,this.b,this.c,getAlfaDeg(),getBetaDeg(),getGammaDeg(),Qmax,maxminHKL[0],maxminHKL[1],maxminHKL[2],maxminHKL[3],maxminHKL[4],maxminHKL[5]);
        log.config("crystal family: "+this.cf);
        
        return maxminHKL;
    }
    
    //aquest es pelat, bo per la indexacio
    public  List<HKLrefl> generateHKLsAsymetricUnitCrystalFamily(double Qmax){
        return this.generateHKLsAsymetricUnitCrystalFamily(Qmax,false,false,false,false,false);
    }
    
    public List<HKLrefl> generateHKLsAsymetricUnitCrystalFamily(double Qmax, boolean exCentering, boolean exSymmetry, boolean sortbytth, boolean calcMultiplicity,boolean strictQmax) {
        return this.generateHKL(Qmax, exCentering, exSymmetry, sortbytth, calcMultiplicity,strictQmax, true);
    }

    public List<HKLrefl> generateHKLsCrystalFamily(double Qmax, boolean exCentering, boolean exSymmetry, boolean sortbytth, boolean calcMultiplicity,boolean strictQmax) {
        return this.generateHKL(Qmax, exCentering, exSymmetry, sortbytth, calcMultiplicity,strictQmax, false);
    }
    
    //AQUEST ES COMPLERT
    //genera la llista d'hkls de la UNITAT ASIMÈTRICA per la família cristal·lina de la cel·la (opcinos de consdierar centratges i/o grup espacial)
    //strict Qmax to generate reflections but not good when working with ranges
    private  List<HKLrefl> generateHKL(double Qmax, boolean exCentering, boolean exSymmetry, boolean sortbytth, boolean calcMultiplicity,boolean strictQmax, boolean asymUnitOnly) {
        int[] maxminHKL = this.limithkl_MaxMin_GeneralEQ(Qmax);
        
        //added for monochromator glitches calculation
        if (!asymUnitOnly) {
            maxminHKL[0]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(0, 0)));
            maxminHKL[1]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(1, 1)));
            maxminHKL[2]=(int) FastMath.round(FastMath.sqrt(Qmax/Gstar.getEntry(2, 2)));
            maxminHKL[3]=-maxminHKL[0];
            maxminHKL[4]=-maxminHKL[1];
            maxminHKL[5]=-maxminHKL[2];
        }
        
        int ihmax = maxminHKL[0];
        int ikmax = maxminHKL[1];
        int ilmax = maxminHKL[2];
        int ihmin = maxminHKL[3];
        int ikmin = maxminHKL[4];
        int ilmin = maxminHKL[5];
      
        refl_allowed_asy = new ArrayList<HKLrefl>();
        refl_extinct_asy = new ArrayList<HKLrefl>();
        
        for (int l=ilmin;l<=ilmax;l++) {
            for (int k=ikmin;k<=ikmax;k++) {
                for (int h=ihmin;h<=ihmax;h++) {
                    if ((h==0)&&(k==0)&&(l==0))continue;
                    
                    if (asymUnitOnly) {
                        switch(this.cf) {
                        case CUBIC: // m-3m: ilaue=12
                            if ((h<k)||(k<l))continue;

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
                    }
                    
                    HKLrefl hkl = new HKLrefl(h,k,l,this.calcDspHKL(h, k, l));
                    if (strictQmax) {
                        if (hkl.calcQvalue()>Qmax) {
                            continue;
                        }
                    }
                    
                    if (exCentering) {
                        //considerar extincions degudes a centratge
                        if (!isReflectionAllowedByCentering(h,k,l)) {
                            refl_extinct_asy.add(hkl);
                            continue;
                        }
                    }
                    
                    if (exSymmetry) {
                        //considerar extincions degudes a traslacions del grup espacial
                        if (!isReflectionAllowedBySymmetry(h,k,l)) {
                            refl_extinct_asy.add(hkl);
                            continue;
                        }
                    }
                    
                    // si arribem aquí es que pertany a la unitat asimetrica amb les condicions acordades (simetria, traslacions)
                    refl_allowed_asy.add(hkl); //TODO COMPARAR LA VELOCIAT AMB I SENSE CALCULAR DSPACING (i activar ordenament en consequencia
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
                hkl.setMult(sg.calcMultiplicityReflection(hkl));
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
    
    private double calcVolumeGeneralEq() {
        double cal = FastMath.cos(al);
        double cbe = FastMath.cos(be);
        double cga = FastMath.cos(ga);
        return (a*b*c*FastMath.sqrt(1-cal*cal-cbe*cbe-cga*cga+2*cal*cbe*cga));
    }
    
    public void calcVolumeMetricEq() {
        switch (this.cf) {
        case CUBIC:
            this.vol=a*a*a;
            break;
        case TETRA:
            this.vol=a*a*c;
            break;
        case HEXA:
            this.vol=0.86602540378*a*a*c; //  0.866..= sqrt(3)/2  == 1.73205080757/2
            break;
        case ORTO:
            this.vol=a*b*c;
            break;
        case MONO:
            this.vol=a*b*c*FastMath.sin(be);
            break;
        default: //none, triclinic
            this.vol=this.calcVolumeGeneralEq();
        }
    }
    
    
    public String getListAsString_HKLMerged_dsp_Fc2() {
        Iterator<HKLrefl> itrO = refl_allowed_asy.iterator();
        StringBuilder sb = new StringBuilder();
        while (itrO.hasNext()) {
            sb.append(itrO.next().toString_HKL_D_Fc2());
            sb.append(CellSymm_global.lineSeparator);
        }
        return sb.toString();

    }
    
    public String getListAsString_HKLMerged_tth_mult_Fc2(float wave) {
        Iterator<HKLrefl> itrO = refl_allowed_asy.iterator();
        StringBuilder sb = new StringBuilder();
        while (itrO.hasNext()) {
            sb.append(itrO.next().toString_HKL_tth_mult_Fc2(wave));
            sb.append(CellSymm_global.lineSeparator);
        }
        return sb.toString();

    }
    
    public String getListAsString_HKLextinct_dsp_Fc2() {
        Iterator<HKLrefl> itrO = refl_extinct_asy.iterator();
        StringBuilder sb = new StringBuilder();
        while (itrO.hasNext()) {
            sb.append(itrO.next().toString_HKL_D_Fc2());
            sb.append(CellSymm_global.lineSeparator);
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

    public void setAtoms(List<Atom> at) {
        this.atoms=at;
    }
   
    public void calcInten(boolean estimBiso) {
        calcInten(estimBiso, false);
    }
    
    public void calcInten(boolean estimBiso, boolean powderIntensities) {
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
        List<Atom> indAt = new ArrayList<Atom>();
        
        for(Atom at:atoms) {
            indAt.addAll(at.getEquivalents(this.sg,true,true,true));
        }
        
        log.debug("total indepenent atoms = "+indAt.size());
        
        for(HKLrefl hkl:refl_allowed_asy) {
            hkl.calcY(indAt, estimBiso,powderIntensities);
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
    
    public SpaceGroup getSg() {
        return sg;
    }

    public void setCellParameters(double a, double b, double c, double alfa, double beta, double gamma, boolean inDegrees) {
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
    }
    
    public void setSg(SpaceGroup sg) {
        this.sg = sg;
        this.setCrystalFamily(sg.getCrystalFamily());
        this.cc = sg.getCrystalCentering();
    }

    public CrystalFamily getCrystalFamily() {
        return cf;
    }
    public CrystalCentering getCrystalCentering() {
        return cc;
    }

    public void setCrystalFamily(CrystalFamily cfam) {
        this.cf = cfam;
    }
    
    public void setCrystalCentering(CrystalCentering ccen) {
        this.cc = ccen;
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
    
    public double[] getCellParameters(boolean inDeg) {
        if (inDeg) {
            return new double[] {a,b,c,getAlfaDeg(),getBetaDeg(),getGammaDeg()};
        }else {
            return new double[] {a,b,c,al,be,ga};
        }
    }
    
    public double getVol() {
        return vol;
    }

    public String toStringCellParamOnly() {
        return String.format("%8.4f %8.4f %8.4f %6.2f %6.2f %6.2f", a,b,c,getAlfaDeg(),getBetaDeg(),getGammaDeg());
    }
    
    public Cell getIncrementedCell(double incPar, double incAngRad) {
        return new Cell(this.a+incPar,this.b+incPar,this.c+incPar,this.al+incAngRad,this.be+incAngRad,this.ga+incAngRad,false,this.cf);
    }
    
    public String toStringCellParamWithErr() {
        return String.format("%8.4f %8.4f %8.4f %6.2f %6.2f %6.2f\n%8.4f %8.4f %8.4f %6.2f %6.2f %6.2f", a,b,c,getAlfaDeg(),getBetaDeg(),getGammaDeg(),sda,sdb,sdc,FastMath.toDegrees(sdal),FastMath.toDegrees(sdbe),FastMath.toDegrees(sdga));
    }
    
    
    //minimize parameters according to Qobs (but it can be implemented for different things as well if we plan to do a PM)
    //ens ha de retornar una altra cella, amb els paràmetres afinats
    public Cell refineCellByQobs(final double[] Qobs, final double Qmax, double iniIncPar, double iniIncAng) { //0 for DEFAULT values
        /* Que necessito:
         * - Llista de Qobs entrats
         * - Calcular llista de Qcal fins a Qmax + Qerr. (podem fer 3*Qerr per si de cas...)
         */
        double[] pars = null;
        double[] inc = null;
        double defParInc = 0.1;
        if (iniIncPar!=0)defParInc=iniIncPar;
        double defAngInc = 0.1;
        if (iniIncAng!=0)defAngInc=iniIncAng;
        switch (this.cf) {
        case CUBIC:
            pars = new double[] {this.a};
            inc = new double[] {defParInc};
            break;
        case TETRA:
            pars = new double[] {this.a,this.c};
            inc = new double[] {defParInc,defParInc};
            break;
        case HEXA:
            pars = new double[] {this.a,this.c};
            inc = new double[] {defParInc,defParInc};
            break;
        case ORTO:
            pars = new double[] {this.a,this.b,this.c};
            inc = new double[] {defParInc,defParInc,defParInc};
            break;
        case MONO:
            pars = new double[] {this.a,this.b,this.c,this.be};
            inc = new double[] {defParInc,defParInc,defParInc,defAngInc};
            break;
        case TRIC:
            pars = new double[] {this.a,this.b,this.c,this.al,this.be,this.ga};
            inc = new double[] {defParInc,defParInc,defParInc,defAngInc,defAngInc,defAngInc};
            break;
        default:
            break;
        }
        
        if(pars==null) {
            log.info("error refining cell parameters");
            return null;
        }
        
        MultivariateFunction function = new MultivariateFunction() {
            @Override
            public double value(double[] pars) {
                double res = calcResidual(Qmax, Qobs, pars);
                return res;
            }
        };
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-5, 1e-10);
        PointValuePair optimum = optimizer.optimize(
                new MaxEval(1000), 
                new ObjectiveFunction(function), 
                GoalType.MINIMIZE, 
                new InitialGuess(pars), 
                new NelderMeadSimplex(inc));//,1,2,0.5,0.5

        log.debug("opt sol="+Arrays.toString(optimum.getPoint()) + " : " + optimum.getSecond());
        
        //RESULT
        Cell c = null;
        switch (this.cf) {
        case CUBIC:
            c = new Cell(optimum.getPoint()[0],optimum.getPoint()[0],optimum.getPoint()[0],90.0,90.0,90.0,true,this.cf);
            break;
        case TETRA:
            c = new Cell(optimum.getPoint()[0],optimum.getPoint()[0],optimum.getPoint()[1],90.0,90.0,90.0,true,this.cf);
            break;
        case HEXA:
            c = new Cell(optimum.getPoint()[0],optimum.getPoint()[0],optimum.getPoint()[1],90.0,90.0,120.0,true,this.cf);
            break;
        case ORTO:
            c = new Cell(optimum.getPoint()[0],optimum.getPoint()[1],optimum.getPoint()[2],90.0,90.0,90.0,true,this.cf);
            break;
        case MONO:
            c = new Cell(optimum.getPoint()[0],optimum.getPoint()[1],optimum.getPoint()[2],90.0,FastMath.toDegrees(optimum.getPoint()[3]),90.0,true,this.cf);
            break;
        case TRIC:
            c = new Cell(optimum.getPoint()[0],optimum.getPoint()[1],optimum.getPoint()[2],optimum.getPoint()[3],optimum.getPoint()[4],optimum.getPoint()[5],false,this.cf);
            break;
        default:
            c = new Cell(optimum.getPoint()[0],optimum.getPoint()[1],optimum.getPoint()[2],optimum.getPoint()[3],optimum.getPoint()[4],optimum.getPoint()[5],false,this.cf);
            break;
        }
        
        return c;
        
    }
    
    private double calcResidual(double Qmax, double[] obsValues, double[] pars) {
        Cell c = null;
        switch (this.cf) {
        case CUBIC:
            c = new Cell(pars[0],pars[0],pars[0],90.0,90.0,90.0,true,this.cf);
            break;
        case TETRA:
            c = new Cell(pars[0],pars[0],pars[1],90.0,90.0,90.0,true,this.cf);
            break;
        case HEXA:
            c = new Cell(pars[0],pars[0],pars[1],90.0,90.0,120.0,true,this.cf);
            break;
        case ORTO:
            c = new Cell(pars[0],pars[1],pars[2],90.0,90.0,90.0,true,this.cf);
            break;
        case MONO:
            c = new Cell(pars[0],pars[1],pars[2],90.0,FastMath.toDegrees(pars[3]),90.0,true,this.cf);
            break;
        case TRIC:
            c = new Cell(pars[0],pars[1],pars[2],pars[3],pars[4],pars[5],false,this.cf);
            break;
        default:
            c = new Cell(pars[0],pars[1],pars[2],pars[3],pars[4],pars[5],false,this.cf);
            break;
        }
        
        List<HKLrefl> refls = c.generateHKLsAsymetricUnitCrystalFamily(Qmax);
        double[] calcValues = new double[refls.size()];
        int i=0;
        for (HKLrefl hkl:refls) {
            calcValues[i]=hkl.calcQvalue();
             i++;
        }

        //TODO: aqui no es te en compte els espuris (pero això s'hauria d'implementar a indexing, enviar la llista de qobs que ja no tingui espuris)
        double res = 0;
        for (i=0; i<obsValues.length; i++) {
            double mindiff = Double.MAX_VALUE;
            for (int j=0; j<calcValues.length; j++) {
                double diff = FastMath.abs(obsValues[i]-calcValues[j]);
                if (diff<mindiff)mindiff = diff;
            }
            res = res + mindiff;
        }
        log.debug("residual="+res);
        return res;
    }

    public void updateCell(double a, double b, double c, double alfa, double beta, double gamma, boolean inDegrees) {
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
        this.calcVolumeMetricEq();
        this.calcMetricMatrix();
        this.calcGstarMatrix();
    }
    
    public void initHKLProf(double marchCoef, int marchH, int marchK, int marchL) {
        for (HKLrefl hkl:this.refl_allowed_asy) {
            hkl.calcORI(marchCoef, marchH, marchK, marchL);
            hkl.aint=0.0;
        }
    }
    
    public HKLrefl getReflection(int i) {
        return refl_allowed_asy.get(i);
    }
    
    public int getNAllowedRefl() {
        return refl_allowed_asy.size();
    }
   
    
    @Override
    public boolean equals(Object obj) {
        Cell c2 = (Cell)obj;
        float tolPar = 0.00001f;
        float tolAng = 0.001f;
        if (FastMath.abs(this.a-c2.a)>tolPar)return false;
        if (FastMath.abs(this.b-c2.b)>tolPar)return false;
        if (FastMath.abs(this.c-c2.c)>tolPar)return false;
        if (FastMath.abs(this.al-c2.al)>tolAng)return false;
        if (FastMath.abs(this.be-c2.be)>tolAng)return false;
        if (FastMath.abs(this.ga-c2.ga)>tolAng)return false;
        //family, spacegroup,...
        if (this.cf!=c2.cf)return false;
        if (this.cc!=c2.cc)return false;
        if (!this.sg.isThisSGnoCentering(c2.sg.getName()))return false;
        return true;
    }
    
}
