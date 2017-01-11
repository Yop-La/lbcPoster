package scraper;

public class Texte {
	private String corpsTexteInBase;
	private String corpsTexteOnLbc;
	private String corpsTexteForPublication;
	private TypeTexte typeTexte;
	private int refTexte;
	
	public Texte(){
		
	}
	
	public Texte(String corpsTexteForPublication){
		this.corpsTexteForPublication = corpsTexteForPublication; 
	}
	
	public Texte( String corpsTexteInBase, TypeTexte typeTexte, int refTexte){
		 this.corpsTexteInBase = corpsTexteInBase;
		 this.typeTexte = typeTexte;
		 this.refTexte = refTexte;
	}
	
	public String getCorpsTexteForPublication() {
		return corpsTexteForPublication;
	}

	public void setCorpsTexteForPublication(String corpsTexteForPublication) {
		this.corpsTexteForPublication = corpsTexteForPublication;
	}

	public String getCorpsTexteInBase() {
		return corpsTexteInBase;
	}

	public void setCorpsTexteInBase(String corpsTexteInBase) {
		this.corpsTexteInBase = corpsTexteInBase;
	}

	public String getCorpsTexteOnLbc() {
		return corpsTexteOnLbc;
	}

	public void setCorpsTexteOnLbc(String corpsTexteOnLbc) {
		this.corpsTexteOnLbc = corpsTexteOnLbc;
	}

	public int getRefTexte() {
		return refTexte;
	}

	public void setRefTexte(int refTexte) {
		this.refTexte = refTexte;
	}

	public TypeTexte getTypeTexte() {
		return typeTexte;
	}
	public void setTypeTexte(TypeTexte typeTexte) {
		this.typeTexte = typeTexte;
	}
	
	
}
