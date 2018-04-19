package com.vava33.jutils;

import java.awt.Color;

import org.apache.commons.math3.util.FastMath;

public final class AtomProperties {
    
    private static String[] atSymbols= {
        "H","HE","LI","BE","B","C","N","O","F","NE",
        "NA","MG","AL","SI","P","S","CL","AR","K","CA",
        "SC","TI","V","CR","MN","FE","CO","NI","CU","ZN",
        "GA","GE","AS","SE","BR","KR","RB","SR","Y","ZR",
        "NB","MO","TC","RU","RH","PD","AG","CD","IN","SN",
        "SB","TE","I","XE","CS","BA","LA","CE","PR","ND",
        "PM","SM","EU","GD","TB","DY","HO","ER","TM","YB",
        "LU","HF","TA","W","RE","OS","IR","PT","AU","HG",
        "TL","PB","BI","PO","AT","RN","FR","RA","AC","TH",
        "PA","U","NP","PU","AM","CM","BK","CF","ES","FM",
        "MD","NO","LR","KU"};

    private static Color red_O = new Color(255,0,0); //oxigen (vermell)
    private static Color whi_H = new Color(255,255,255);  //Hydrogen (blanc)
    private static Color gre_Hal = new Color (0,255,0); //Clor, fluor (verd)
    private static Color mar_Br = new Color(166,41,41); //Brom (marro)
    private static Color lil_I = new Color(112,46,176); //iode (lila)
    private static Color blu_N = new Color(48,80,255); //nitrogen (blau)
    private static Color ora_P = new Color(255,128,0); //fosfor (taronja)
    private static Color cya_NG = new Color (0,0,255); //nobel gases (cian)
    private static Color gri_C = new Color (0.5f,0.5f,0.5f); //gris carboni (gris)
    private static Color gro_S = new Color(255,255,48); // sofre (groc)
    private static Color pea_B = new Color(255,181,181); //bor (salmó) i Alumini
    private static Color lil_alk = new Color(171,92,242); //metall alcalins (Li,Na,K,Rb,Cs) lila (tamb� per As,Sb,Bi)
    private static Color ver_alk = new Color(138,255,0); //alcaline earth metals (Be,Mg,Ca,Sr,Ba,Ra) verd
    private static Color gri_Ti = new Color(230,230,230); //titani gris
    private static Color tar_Fe = new Color(224,102,51); //ferro taronja
    private static Color gri_Met = new Color(191,194,199); //gris clar general pels metalls de transici�
    private static Color sal_Si = new Color(240,200,160); //salmo pel silici
    private static Color ros_All =new Color(240,144,160); //ALTRES rosa

    private static Color[] atColors= {
        whi_H,cya_NG,lil_alk,ver_alk,pea_B,gri_C,blu_N,red_O,gre_Hal,cya_NG,
        lil_alk,ver_alk,pea_B,sal_Si,ora_P,gro_S,gre_Hal,cya_NG,lil_alk,ver_alk,
        gri_Met,gri_Ti,gri_Met,gri_Met,gri_Met,tar_Fe,gri_Met,gri_Met,gri_Met,gri_Met,
        ros_All,gri_Met,lil_alk,ora_P,mar_Br,cya_NG,lil_alk,ver_alk,gri_Met,gri_Met,
        gri_Met,gri_Met,gri_Met,gri_Met,gri_Met,tar_Fe,tar_Fe,tar_Fe,ros_All,gri_Met,
        lil_alk,ora_P,lil_I,cya_NG,lil_alk,ver_alk,ros_All,ros_All,ros_All,ros_All,
        ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,
        ros_All,gri_Met,gri_Met,gri_Met,gri_Met,tar_Fe,tar_Fe,tar_Fe,tar_Fe,gri_Met,
        ros_All,gri_Met,lil_alk,ora_P,ros_All,cya_NG,lil_alk,ver_alk,ros_All,ros_All,
        ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,ros_All,
        ros_All,ros_All,ros_All,ros_All};

    private static int r_std=200; // radi standard pels desconeguts

    @SuppressWarnings("unused")
    private static int[] atRadisCalc={ //en picometres (calculats)
        53,31,167,112,87,67,56,48,42,38,190,145,118,111,98,88,79,71,243,194,
        184,176,171,166,161,156,152,149,145,142,136,125,114,103,94,88,265,219,212,206,
        198,190,183,178,173,169,165,161,156,145,133,123,115,108,298,253,r_std,r_std,247,206,
        205,238,231,233,225,228,r_std,226,222,222,217,208,200,193,188,185,180,177,174,171,
        156,154,143,135,r_std,120,r_std,r_std,r_std,r_std,r_std,r_std,r_std,r_std,r_std,r_std,r_std,r_std,r_std,r_std,
        r_std,r_std,r_std,r_std};

    private static int r_std2=140; //radi standard pels desconeguts

    private static int[] atRadisEmp={ //en picometres (empirics) --> seran els que utilitzarem per dibuixar
        25,25,145,105,85,70,65,60,50,40,180,150,125,110,100,100,100,71,220,180,
        160,140,135,140,140,140,135,135,135,135,130,125,115,115,115,105,235,200,180,155,
        145,145,135,130,135,140,160,155,155,145,145,140,140,130,260,215,195,185,185,185,
        185,185,185,180,175,175,175,175,175,175,175,155,145,135,135,130,135,135,135,150,
        190,180,160,190,r_std2,r_std2,r_std2,215,195,180,180,175,175,175,175,r_std2,r_std2,r_std2,r_std2,r_std2,
        r_std2,r_std2,r_std2,r_std2};

    public static int getAtomicNumber(String element){
        element.toUpperCase();
        for (int i=0;i<atSymbols.length;i++){
            if (atSymbols[i].equalsIgnoreCase(element.trim())){
                return i+1;
            }
        }
        //repetim eliminant possibles cations,anions,nums, etc...
        element = element.replace("+", "");
        element = element.replace("-", "");
        element = element.replaceAll("[0-9]", "");
        for (int i=0;i<atSymbols.length;i++){
            if (atSymbols[i].equalsIgnoreCase(element.trim())){
                return i+1;
            }
        }
        return 0;
    }

    public static boolean isElement(String s){
        for (int i=0;i<atSymbols.length;i++){
            if (atSymbols[i].equalsIgnoreCase(s.trim())){
                if(s.trim().equals("NO"))return false;
                return true;
            }
        }
        return false;
    }
    
    public static String getSymbol(int n){
        return atSymbols[n-1];
    }

    // retorna el radi de l'esfera per dibuixar segons l'atom llegit
    public static float calcAtomRadiDraw(String simbol){
        boolean trobat = false;
        simbol.toUpperCase();
        int index = 0;
        for (int i=0;i<atSymbols.length;i++){
            if (atSymbols[i].equalsIgnoreCase(simbol.trim())){
                index=i;
                trobat = true;
            }
        }
        if(trobat){ // s'ha trobat
            return atRadisEmp[index]/200.f;
        }else{ // no s'ha trobat, tornem un radi estandar
            return 0.25f;
        }
    }

    public static int getAtomRadiEmpPico(String simbol){
        boolean trobat = false;
        simbol.toUpperCase();
        int index = 0;
        for (int i=0;i<atSymbols.length;i++){
            if (atSymbols[i].equalsIgnoreCase(simbol.trim())){
                index=i;
                trobat = true;
            }
        }
        if(trobat){ // s'ha trobat
            return atRadisEmp[index];
        }else{ // no s'ha trobat, tornem un radi estandar:
            return r_std2;
        }
    }

    public static Color getAtomColor(String simbol){
        boolean trobat = false;
        simbol.toUpperCase();
        int index = 0;
        for (int i=0;i<atSymbols.length;i++){
            if (atSymbols[i].equalsIgnoreCase(simbol.trim())){
                index=i;
                trobat = true;
            }
        }
        if(trobat){ 
            return atColors[index];
        }else{ //no s'ha trobat, tornem el color Others
            return ros_All;
        }
    }

    private static Color blau = new Color(0,0,255);
    private static Color vermell = new Color(255,0,0);
    private static Color verd = new Color(0,255,0);
    private static Color groc = new Color(255,255,0);
    private static Color magenta = new Color(255,0,255);
    private static Color taronja = new Color(255,200,0);
    private static Color rosa = new Color(255,175,175);
    private static Color cyan = new Color(0,255,255);
    
    @SuppressWarnings("unused")
    private static Color[] frbCol= {
        blau,vermell,verd,groc,magenta,taronja,rosa,cyan};
    
    public static Color getFRBColor(int i){
        int modul = i % 8;
        switch (modul){
            case 0:return blau;
            case 1:return vermell;
            case 2:return verd;
            case 3:return groc;
            case 4:return magenta;
            case 5:return taronja;
            case 6:return rosa;
            case 7:return cyan;
        }
        return null;
    }
    
    // donat un string del tipus Na2, O3, S, ...
    // ha de tornar el num atomic del tipus d'atom (11, 8, 16, ...)
    public static int getTypeAtomicNumber(String s){
        char[] c = s.toCharArray();
        StringBuilder sym = new StringBuilder();
        for (int i=0;i<c.length;i++){
            if (Character.getType(c[i])!=Character.DECIMAL_DIGIT_NUMBER){
                sym.append(c[i]);
            }else{
                break;
            }
        }
        int atNum = getAtomicNumber(sym.toString());
        return atNum;
    }
    
    // donat un string del tipus Na2, O3, S, ...
    // ha de tornar la quantitat (2, 3, 1,...)
    public static int getAtomQuantity(String s){
        char[] c = s.toCharArray();
        StringBuilder q = new StringBuilder();
        for (int i=0;i<c.length;i++){
            if (Character.getType(c[i])!=Character.DECIMAL_DIGIT_NUMBER){
                continue;
            }else{
                q.append(c[i]);
            }
        }
        if(q.indexOf(" ")!=-1){ //hi ha espais, no es numero
            return -1;
        }
        if (q.length()==0 && s.length()!=0){
            //no és numero, sera que és 1 i no l'han posat
            return 1;
        }
        // cas >1:
        return Integer.parseInt(q.toString());
    }

    public static String getSpacedFormula(String formula){
        StringBuilder spacedFormula = new StringBuilder(32);
        int i=0;
        int caractersAfegits=0;
        while(i<formula.length()){
            char c = formula.charAt(i);
            if (c==' '){ //es espai
                //mirem l'�ltim caracter escrit a spacedFormula
                if (caractersAfegits>0){
                    char c1 = spacedFormula.charAt(caractersAfegits-1);
                    if (Character.getType(c1)!=Character.DECIMAL_DIGIT_NUMBER&&c1!=' '){
                        //si l'anterior no es numero i hem trobat un espai, cal afegir un 1 i un espai
                        spacedFormula.append("1 ");
                        caractersAfegits=caractersAfegits+2;
                    }else{ //l'anterior es un numero o espai, afegim espai en cas que sigui un num
                        if(c!=' '){
                            spacedFormula.append(" ");
                            caractersAfegits=caractersAfegits+1;
                        }
                    }
                    i=i+1;
                    continue; //proxim caracter;
                }else{
                    i=i+1;
                    continue; //proxim caracter;
                }
            }

            if (Character.getType(c)!=Character.DECIMAL_DIGIT_NUMBER){ //es lletra
                if(i<formula.length()-1){
                    char c1 = formula.charAt(i+1);
                    if(Character.getType(c1)!=Character.DECIMAL_DIGIT_NUMBER&&c1!=' '){//si el seguent tambe es lletra
                        if(AtomProperties.isElement(Character.toString(c)+Character.toString(c1))){//si es ELEMENT correcte
                            spacedFormula.append(Character.toString(c)).append(Character.toString(c1));
                            caractersAfegits=caractersAfegits+2;
                            i=i+2; //incrementem en 2 el punter
                            if(i>=formula.length()){//era l'ultim element, no hi ha numero
                                spacedFormula.append("1"); //voldra dir que l'ultim element nomes n'hi ha un
                                return spacedFormula.toString(); // retornem la cadena
                            }
                        }else{ //element de 2 caracters no es correcte, agafem el d'una lletra i contem com a X1
                            spacedFormula.append(Character.toString(c)).append("1 ");
                            caractersAfegits=caractersAfegits+3;
                            i=i+1;
                        }
                    }else{ //el seguent NO es lletra (pot ser numero o espai)
                        spacedFormula.append(Character.toString(c));
                        caractersAfegits=caractersAfegits+1;
                        i=i+1;
                    }
                }else{//ES L'ULTIM CARACTER DE LA FORMULA
                    spacedFormula.append(Character.toString(c));
                    spacedFormula.append("1"); //voldra dir que l'ultim element nomes n'hi ha un
                    return spacedFormula.toString(); // retornem la cadena
                }
                continue; //proxim caracter;
            }

            if (Character.getType(c)==Character.DECIMAL_DIGIT_NUMBER){ //es numero
                //el numero s'afegeix sempre. Aleshores si el seg�ent NO es numero tamb�
                //cal afegir un espai.
                spacedFormula.append(Character.toString(c));
                caractersAfegits=caractersAfegits+1;
                if(i<formula.length()-1){
                    char c1 = formula.charAt(i+1);
                    if(Character.getType(c1)!=Character.DECIMAL_DIGIT_NUMBER){
                        //no es numero, afegim espai
                        spacedFormula.append(" ");
                        caractersAfegits=caractersAfegits+1;
                    }
                }else{//ES L'ULTIM CARACTER DE LA FORMULA
                    return spacedFormula.toString();
                }
                i=i+1;
            }

        }
        return spacedFormula.toString();
    }
    
    private static float[] cromer_mann_a1= {0.489918f,0.873400f,1.128200f,1.591900f,2.054500f,2.260690f,12.212600f,3.048500f,3.539200f,3.955300f,4.762600f,5.420400f,6.420200f,5.662690f,6.434500f,6.905300f,11.460400f,7.484500f,8.218600f,8.626600f,9.189000f,9.759500f,10.297100f,10.640600f,11.281900f,11.769500f,12.284100f,12.837600f,13.338000f,14.074300f,15.235400f,16.081600f,16.672300f,17.000600f,17.178900f,17.355500f,17.178400f,17.566300f,17.776000f,17.876500f,17.614200f,3.702500f,19.130100f,19.267400f,19.295700f,19.331900f,19.280800f,19.221400f,19.162400f,19.188900f,19.641800f,19.964400f,20.147200f,20.293300f,20.389200f,20.336100f,20.578000f,21.167100f,22.044000f,22.684500f,23.340500f,24.004200f,24.627400f,25.070900f,25.897600f,26.507000f,26.904900f,27.656300f,28.181900f,28.664100f,28.947600f,29.144000f,29.202400f,29.081800f,28.762100f,28.189400f,27.304900f,27.005900f,16.881900f,20.680900f,27.544600f,31.061700f,33.368900f,34.672600f,35.316300f,35.563100f,35.929900f,35.763000f,35.659700f,35.564500f,35.884700f,36.022800f,36.187400f,35.510300f,36.670600f,36.648800f,36.788100f,36.918500f};
    private static float[] cromer_mann_a2= {0.262003f,0.630900f,0.750800f,1.127800f,1.332600f,1.561650f,3.132200f,2.286800f,2.641200f,3.112500f,3.173600f,2.173500f,1.900200f,3.071640f,4.179100f,5.203400f,7.196400f,6.772300f,7.439800f,7.387300f,7.367900f,7.355800f,7.351100f,7.353700f,7.357300f,7.357300f,7.340900f,7.292000f,7.167600f,7.031800f,6.700600f,6.374700f,6.070100f,5.819600f,5.235800f,6.728600f,9.643500f,9.818400f,10.294600f,10.948000f,12.014400f,17.235600f,11.094800f,12.918200f,14.350100f,15.501700f,16.688500f,17.644400f,18.559600f,19.100500f,19.045500f,19.013800f,18.994900f,19.029800f,19.106200f,19.297000f,19.599000f,19.769500f,19.669700f,19.684700f,19.609500f,19.425800f,19.088600f,19.079800f,18.218500f,17.638300f,17.294000f,16.428500f,15.885100f,15.434500f,15.220800f,15.172600f,15.229300f,15.430000f,15.718900f,16.155000f,16.729600f,17.763900f,18.591300f,19.041700f,19.158400f,13.063700f,12.951000f,15.473300f,19.021100f,21.281600f,23.054700f,22.906400f,23.103200f,23.421900f,23.294800f,23.412800f,23.596400f,22.578700f,24.099200f,24.409600f,24.773600f,25.199500f};
    private static float[] cromer_mann_a3= {0.196767f,0.311200f,0.617500f,0.539100f,1.097900f,1.050750f,2.012500f,1.546300f,1.517000f,1.454600f,1.267400f,1.226900f,1.593600f,2.624460f,1.780000f,1.437900f,6.255600f,0.653900f,1.051900f,1.589900f,1.640900f,1.699100f,2.070300f,3.324000f,3.019300f,3.522200f,4.003400f,4.443800f,5.615800f,5.165200f,4.359100f,3.706800f,3.431300f,3.973100f,5.637700f,5.549300f,5.139900f,5.422000f,5.726290f,5.417320f,4.041830f,12.887600f,4.649010f,4.863370f,4.734250f,5.295370f,4.804500f,4.461000f,4.294800f,4.458500f,5.037100f,6.144870f,7.513800f,8.976700f,10.662000f,10.888000f,11.372700f,11.851300f,12.385600f,12.774000f,13.123500f,13.439600f,13.760300f,13.851800f,14.316700f,14.559600f,14.558300f,14.977900f,15.154200f,15.308700f,15.100000f,14.758600f,14.513500f,14.432700f,14.556400f,14.930500f,15.611500f,15.713100f,25.558200f,21.657500f,15.538000f,18.442000f,16.587700f,13.113800f,9.498870f,8.003700f,12.143900f,12.473900f,12.597700f,12.747300f,14.189100f,14.949100f,15.640200f,12.776600f,17.341500f,17.399000f,17.891900f,18.331700f};
    private static float[] cromer_mann_a4= {0.049879f,0.178000f,0.465300f,0.702900f,0.706800f,0.839259f,1.166300f,0.867000f,1.024300f,1.125100f,1.112800f,2.307300f,1.964600f,1.393200f,1.490800f,1.586300f,1.645500f,1.644200f,0.865900f,1.021100f,1.468000f,1.902100f,2.057100f,1.492200f,2.244100f,2.304500f,2.348800f,2.380000f,1.673500f,2.410000f,2.962300f,3.683000f,4.277900f,4.354300f,3.985100f,3.537500f,1.529200f,2.669400f,3.265880f,3.657210f,3.533460f,3.742900f,2.712630f,1.567560f,1.289180f,0.605844f,1.046300f,1.602900f,2.039600f,2.466300f,2.682700f,2.523900f,2.273500f,1.990000f,1.495300f,2.695900f,3.287190f,3.330490f,2.824280f,2.851370f,2.875160f,2.896040f,2.922700f,3.545450f,2.953540f,2.965770f,3.638370f,2.982330f,2.987060f,2.989630f,3.716010f,4.300130f,4.764920f,5.119820f,5.441740f,5.675890f,5.833770f,5.783700f,5.860000f,5.967600f,5.525930f,5.969600f,6.469200f,7.025880f,7.425180f,7.443300f,2.112530f,3.210970f,4.086550f,4.807030f,4.172870f,4.188000f,4.185500f,4.921590f,3.493310f,4.216650f,4.232840f,4.243910f};
    private static float[] cromer_mann_b1= {20.659300f,9.103700f,3.954600f,43.642700f,23.218500f,22.690700f,0.005700f,13.277100f,10.282500f,8.404200f,3.285000f,2.827500f,3.038700f,2.665200f,1.906700f,1.467900f,0.010400f,0.907200f,12.794900f,10.442100f,9.021300f,7.850800f,6.865700f,6.103800f,5.340900f,4.761100f,4.279100f,3.878500f,3.582800f,3.265500f,3.066900f,2.850900f,2.634500f,2.409800f,2.172300f,1.938400f,1.788800f,1.556400f,1.402900f,1.276180f,1.188650f,0.277200f,0.864132f,0.808520f,0.751536f,0.698655f,0.644600f,0.594600f,0.547600f,5.830300f,5.303400f,4.817420f,4.347000f,3.928200f,3.569000f,3.216000f,2.948170f,2.812190f,2.773930f,2.662480f,2.562700f,2.472740f,2.387900f,2.253410f,2.242560f,2.180200f,2.070510f,2.073560f,2.028590f,1.988900f,1.901820f,1.832620f,1.773330f,1.720290f,1.671910f,1.629030f,1.592790f,1.512930f,0.461100f,0.545000f,0.655150f,0.690200f,0.704000f,0.700999f,0.685870f,0.663100f,0.646453f,0.616341f,0.589092f,0.563359f,0.547751f,0.529300f,0.511929f,0.498626f,0.483629f,0.465154f,0.451018f,0.437533f};
    private static float[] cromer_mann_b2= {7.740390f,3.356800f,1.052400f,1.862300f,1.021000f,0.656665f,9.893300f,5.701100f,4.294400f,3.426200f,8.842200f,79.261100f,0.742600f,38.663400f,27.157000f,22.215100f,1.166200f,14.840700f,0.774800f,0.659900f,0.572900f,0.500000f,0.438500f,0.392000f,0.343200f,0.307200f,0.278400f,0.256500f,0.247000f,0.233300f,0.241200f,0.251600f,0.264700f,0.272600f,16.579600f,16.562300f,17.315100f,14.098800f,12.800600f,11.916000f,11.766000f,1.095800f,8.144870f,8.434670f,8.217580f,7.989290f,7.472600f,6.908900f,6.377600f,0.503100f,0.460700f,0.420885f,0.381400f,0.344000f,0.310700f,0.275600f,0.244475f,0.226836f,0.222087f,0.210628f,0.202088f,0.196451f,0.194200f,0.181951f,0.196143f,0.202172f,0.197940f,0.223545f,0.238849f,0.257119f,9.985190f,9.599900f,9.370460f,9.225900f,9.092270f,8.979480f,8.865530f,8.811740f,8.621600f,8.448400f,8.707510f,2.357600f,2.923800f,3.550780f,3.974580f,4.069100f,4.176190f,3.871350f,3.651550f,3.462040f,3.415190f,3.325300f,3.253960f,2.966270f,3.206470f,3.089970f,3.046190f,3.007750f};
    private static float[] cromer_mann_b3= {49.551900f,22.927600f,85.390500f,103.483000f,60.349800f,9.756180f,28.997500f,0.323900f,0.261500f,0.230600f,0.313600f,0.380800f,31.547200f,0.916946f,0.526000f,0.253600f,18.519400f,43.898300f,213.187000f,85.748400f,136.108000f,35.633800f,26.893800f,20.262600f,17.867400f,15.353500f,13.535900f,12.176300f,11.396600f,10.316300f,10.780500f,11.446800f,12.947900f,15.237200f,0.260900f,0.226100f,0.274800f,0.166400f,0.125599f,0.117622f,0.204785f,11.004000f,21.570700f,24.799700f,25.874900f,25.205200f,24.660500f,24.700800f,25.849900f,26.890900f,27.907400f,28.528400f,27.766000f,26.465900f,24.387900f,20.207300f,18.772600f,17.608300f,16.766900f,15.885000f,15.100900f,14.399600f,13.754600f,12.933100f,12.664800f,12.189900f,11.440700f,11.360400f,10.997500f,10.664700f,0.261033f,0.275116f,0.295977f,0.321703f,0.350500f,0.382661f,0.417916f,0.424593f,1.482600f,1.572900f,1.963470f,8.618000f,8.793700f,9.556420f,11.382400f,14.042200f,23.105200f,19.988700f,18.599000f,17.830900f,16.923500f,16.092700f,15.362200f,11.948400f,14.313600f,13.434600f,12.894600f,12.404400f};
    private static float[] cromer_mann_b4= {2.201590f,0.982100f,168.261000f,0.542000f,0.140300f,55.594900f,0.582600f,32.908900f,26.147600f,21.718400f,129.424000f,7.193700f,85.088600f,93.545800f,68.164500f,56.172000f,47.778400f,33.392900f,41.684100f,178.437000f,51.353100f,116.105000f,102.478000f,98.739900f,83.754300f,76.880500f,71.169200f,66.342100f,64.812600f,58.709700f,61.413500f,54.762500f,47.797200f,43.816300f,41.432800f,39.397200f,164.934000f,132.376000f,104.354000f,87.662700f,69.795700f,61.658400f,86.847200f,94.292800f,98.606200f,76.898600f,99.815600f,87.482500f,92.802900f,83.957100f,75.282500f,70.840300f,66.877600f,64.265800f,213.904000f,167.202000f,133.124000f,127.113000f,143.644000f,137.903000f,132.721000f,128.007000f,123.174000f,101.398000f,115.362000f,111.874000f,92.656600f,105.703000f,102.961000f,100.417000f,84.329800f,72.029000f,63.364400f,57.056000f,52.086100f,48.164700f,45.001100f,38.610300f,36.395600f,38.324600f,45.814900f,47.257900f,48.009300f,47.004500f,45.471500f,44.247300f,150.645000f,142.325000f,117.020000f,99.172200f,105.251000f,100.613000f,97.490800f,22.750200f,102.273000f,88.483400f,86.003000f,83.788100f};
    private static float[] cromer_mann_c= {0.001305f,0.006400f,0.037700f,0.038500f,-0.193200f,0.286977f,-11.529000f,0.250800f,0.277600f,0.351500f,0.676000f,0.858400f,1.115100f,1.247070f,1.114900f,0.866900f,-9.557400f,1.444500f,1.422800f,1.375100f,1.332900f,1.280700f,1.219900f,1.183200f,1.089600f,1.036900f,1.011800f,1.034100f,1.191000f,1.304100f,1.718900f,2.131300f,2.531000f,2.840900f,2.955700f,2.825000f,3.487300f,2.506400f,1.912130f,2.069290f,3.755910f,4.387500f,5.404280f,5.378740f,5.328000f,5.265930f,5.179000f,5.069400f,4.939100f,4.782100f,4.590900f,4.352000f,4.071200f,3.711800f,3.335200f,2.773100f,2.146780f,1.862640f,2.058300f,1.984860f,2.028760f,2.209630f,2.574500f,2.419600f,3.583240f,4.297280f,4.567960f,5.920460f,6.756210f,7.566720f,7.976280f,8.581540f,9.243540f,9.887500f,10.472000f,11.000500f,11.472200f,11.688300f,12.065800f,12.608900f,13.174600f,13.411800f,13.578200f,13.677000f,13.710800f,13.690500f,13.724700f,13.621100f,13.526600f,13.431400f,13.428700f,13.396600f,13.357300f,13.211600f,13.359200f,13.288700f,13.275400f,13.267400f};

    
    public static double calcfform_cromer(int atNum, double dsp) {
        double stl2 = 1/(4*dsp*dsp);
        int p = atNum -1; //posicio a la llista
        double sum = 0;
        sum = sum + cromer_mann_a1[p]*FastMath.exp(-cromer_mann_b1[p]*(stl2));
        sum = sum + cromer_mann_a2[p]*FastMath.exp(-cromer_mann_b2[p]*(stl2));
        sum = sum + cromer_mann_a3[p]*FastMath.exp(-cromer_mann_b3[p]*(stl2));
        sum = sum + cromer_mann_a4[p]*FastMath.exp(-cromer_mann_b4[p]*(stl2));
        sum = sum + cromer_mann_c[p];
        return sum;
    }
    
    public static double calcfform_cromer(int atNum, float wave, double tth_deg) {
        double dsp = (float) (wave/(2*FastMath.sin(FastMath.toRadians(tth_deg/2.))));
        return calcfform_cromer(atNum,dsp);
    }
    
    public static double calcfform_cromer(String atSym, float wave, double tth_deg) {
        return calcfform_cromer(getAtomicNumber(atSym), wave, tth_deg);
    }
    
    public static double calcfform_cromer(String atSym, double dsp) {
        return calcfform_cromer(getAtomicNumber(atSym), dsp);
    }
}
