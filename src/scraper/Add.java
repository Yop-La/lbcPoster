package scraper;

import java.io.File;

public class Add {
	private Title title;
	private Texte texte;
	private Commune commune;
	private File image;
	
	public Add(Title title, Texte texte, Commune commune, File image) {
		super();
		this.title = title;
		this.texte = texte;
		this.commune = commune;
		this.image = image;
	}
	
	public String toString(){
		String retour = title.getTitre() + "\n" + texte + "\n" + commune.getNomCommune() + "\n" + image.getPath();
		return retour;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public Texte getTexte() {
		return texte;
	}

	public void setTexte(Texte texte) {
		this.texte = texte;
	}

	public Commune getCommune() {
		return commune;
	}

	public void setCommune(Commune commune) {
		this.commune = commune;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	
	
	
}

