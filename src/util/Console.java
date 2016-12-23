package util;


import java.io.*;

public class Console
{
    public static char readChar(String mess)
    {
        boolean erreur;
        String s;
        System.out.println(mess);
        do {
            erreur=false;
            s=readString();
            if(s.length()!=1)
            {
                System.out.println("Erreur de saisie, recommencez");
                erreur=true;
            }
        }while(erreur);
        return s.charAt(0);
    }
    
    public static int readInt(String mess)
    {
        boolean erreur;
        int n=0;
        System.out.println(mess);
        do {
            erreur=false;
            try {
                n=Integer.parseInt(readString());
            }
            catch(NumberFormatException e)
            {
                System.out.println("Erreur de saisie, recommencez");
                erreur=true;
            }
        }while(erreur);
        return n;
    }
    
    public static double readDouble(String mess)
    {
        boolean erreur;
        double n=0;
        System.out.println(mess);
        do {
            erreur=false;
            try {
                n=new Double(readString()).doubleValue();
            }
            catch(NumberFormatException e)
            {
                System.out.println("Erreur de saisie, recommencez");
                erreur=true;
            }
        }while(erreur);
        return n;
    }
    
    public static float readFloat(String mess)
    {
        boolean erreur;
        float n=0;
        System.out.println(mess);
        do {
            erreur=false;
            try {
                n=new Float(readString()).floatValue();
            }
            catch(NumberFormatException e)
            {
                System.out.println("Erreur de saisie, recommencez");
                erreur=true;
            }
        } while(erreur);
        return n;
    }
    
    public static String readString(String mess)
    {
        System.out.println(mess);
        return readString();
    }
    
    public static String readString()
    {
        byte b[]=new byte[256];
        int n=0;
        boolean erreur;
        
        do {
            erreur=false;
            try{
                n=System.in.read(b);
            }
            catch(IOException e)
            {
                System.out.println(e);
                erreur=true;
            }
        } while(erreur);
        String res = new String(b,0,n-1);
        return res.trim();
    }
    
    public static void print(String s) {
        System.out.print(s);
    }
    
    public static void print(int s) {
        System.out.print(s);
    }
    
    public static void print(double s) {
        System.out.print(s);
    }
    
    public static void println(String s) {
        System.out.println(s);
    }
    
    public static void println(int s) {
        System.out.println(s);
    }
    
    public static void println(double s) {
        System.out.println(s);
    }
    
    public static void main(String args[]) {
        println("test Console");
        char c = readChar("entrez un caractere");
        println("j\'ai lu " + c);
        println("-----------------");
        int i1 = readInt("entrez un entier");
        int i2 = readInt("un deuxieme");
        println("la somme est egale a " + (i1 + i2));
        println("-----------------");
        double d1 = readDouble("entrez un double");
        double d2 = readDouble("un deuxieme");
        println("la somme est egale a " + (d1 + d2));
        println("-----------------");
        String s = readString("entrez une chaine de caracteres");
        println("La chaine lue est: " + s);
        println("-----------------");
        println("fin du test");
        
    }
    
	public static void printCentrer(int n, String titre,String symbole,boolean upperCase){
		System.out.println(centrer(n, titre,symbole,upperCase));
	}
	
	public static String centrer(int n, String titre,String symbole,boolean upperCase){
		int longChaine = titre.length();
		int nbEspaceGauche = 0;
		int nbEspaceDroite = 0;
		int nbEspaceTotale = n-longChaine-2;
		if((nbEspaceTotale) % 2 == 0){
			nbEspaceGauche = nbEspaceTotale/2 ;
			nbEspaceDroite = nbEspaceTotale/2;
		}else{
			nbEspaceGauche = (nbEspaceTotale+1)/2 ;
			nbEspaceDroite = ((nbEspaceTotale+1)/2)-1;
		}
		for(int i=0;i<nbEspaceGauche;i++){
			if(i==0){
				titre = " "+titre;
			}
			titre = symbole+titre;
		}
		for(int i=0;i<nbEspaceDroite;i++){
			if(i==0){
				titre = titre+" ";
			}
			titre = titre+symbole;
		}
		if(upperCase){
			return(titre.toUpperCase());
		}
		else{
			return(titre);
		}
	}

	
	public static String toParagraphe(String para, int largeur){
		String mots[] = para.split(" ");
		String retour="";
		String phrase_test="";
		String phrase_final="";
		boolean lastWord=false;
		for(int i=0;i<mots.length;i++){
			String mot=mots[i];
			if(i==(mots.length-1)){
				lastWord=true;
			}
			if(phrase_test.equals("")){
				phrase_test = mot;
			}else{
				phrase_test = phrase_test+" "+mot;
			}
			if(phrase_test.length()>largeur || lastWord){
				
				if(lastWord){
					phrase_final=phrase_test;
				}else{
					phrase_test=mot;
				}
				if(retour.equals("")){
					retour=centrer(36,phrase_final," ",false);
				}else{
					retour=retour+"\n"+centrer(36,phrase_final," ",false);
				}
				phrase_final="";				
			}else{
				phrase_final = phrase_test;
				
			}
		}
		return retour;
	}
	
	public static String[] forReadConsoleInput(String[] possibilities, String otherPossibilitie){
		String[] retour = new String[2];
		String[] allPossibilities = new String[possibilities.length+1];
		if(!otherPossibilitie.equals("")){
			for(int i=0;i<allPossibilities.length-1;i++){
				allPossibilities[i]=possibilities[i];
			}
			allPossibilities[allPossibilities.length-1]=otherPossibilitie;
			possibilities=allPossibilities;
		}
		String retour1 ="^";
		String retour2 = "doit être : ";
		for(int i=0;i<possibilities.length;i++){
			String possibilitie = possibilities[i];
			if(i==possibilities.length-1){
				retour2= retour2+possibilitie+".";
				retour1 = retour1+possibilitie+"$";
			}else{
				retour2= retour2+possibilitie+" ou ";
				retour1 = retour1+possibilitie+"|";
			}
			
		}
		retour[0]=retour1;
		retour[1]=retour2;
		return retour;
	}
}


