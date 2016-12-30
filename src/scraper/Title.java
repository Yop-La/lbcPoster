package scraper;

public class Title {
	private String titre;
	private TypeTitle typeTitle;
	
	public Title(String titre, TypeTitle typeTitle) {
		super();
		this.titre = titre;
		this.typeTitle = typeTitle;
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

