package scraper;

import java.io.File;

public class Add {
	private String title;
	private String texte;
	private String ville;
	private File image;
	
	public Add(String title, String texte, String ville, File image) {
		super();
		this.title = title;
		this.texte = texte;
		this.ville = ville;
		this.image = image;
	}
	
	public String toString(){
		String retour = title + "\n" + texte + "\n" + ville + "\n" + image.getPath();
		return retour;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTexte() {
		return texte;
	}

	public void setTexte(String texte) {
		this.texte = texte;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}
	
	
}

