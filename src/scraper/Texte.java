package scraper;

public class Texte {
	private String corpsTexte;
	private TypeTexte typeTexte;
	
	
	
	public Texte(String corpsTexte) {
		super();
		this.corpsTexte = corpsTexte;
	}
	public String getCorpsTexte() {
		return corpsTexte;
	}
	public void setCorpsTexte(String corpsTexte) {
		this.corpsTexte = corpsTexte;
	}
	public TypeTexte getTypeTexte() {
		return typeTexte;
	}
	public void setTypeTexte(TypeTexte typeTexte) {
		this.typeTexte = typeTexte;
	}
	
	public String toString(){
		return corpsTexte;
	}
	
}
