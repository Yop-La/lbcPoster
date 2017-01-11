package scraper;

public class Title {
	private String titre;
	private TypeTitle typeTitle;
	private int refTitre;
	
	
	
	public Title(String titre, TypeTitle typeTitle, int refTitre) {
		super();
		this.titre = titre;
		this.typeTitle = typeTitle;
		this.refTitre = refTitre;
	}

	public int getRefTitre() {
		return refTitre;
	}

	public void setRefTitre(int refTitre) {
		this.refTitre = refTitre;
	}

	public Title(String titre) {
		super();
		this.titre = titre;
	}
	
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public TypeTitle getTypeTitle() {
		return typeTitle;
	}
	public void setTypeTitle(TypeTitle typeTitle) {
		this.typeTitle = typeTitle;
	}
	
	public String toString(){
		return titre;
	}
	
}

