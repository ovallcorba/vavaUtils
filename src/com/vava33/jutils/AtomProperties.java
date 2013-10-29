package com.vava33.jutils;

import java.awt.Color;

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
            //hi ha algo pero cap numero, sera que és 1 i no l'han posat
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
                //mirem l'últim caracter escrit a spacedFormula
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
    
    
}
