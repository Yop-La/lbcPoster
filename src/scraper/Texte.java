package scraper;

import org.apache.commons.lang3.StringUtils;

public class Texte {
	private String corpsTexteInBase;
	private String corpsTexteOnLbc;
	private String corpsTexteForPublication;
	private TypeTexte typeTexte;
	private int refTexte;
	private int levenshteinDistanceBetweenLbcAndBdd;

	// cette entier permet de rechercher le texte du bon coin dans la bdd
	// il correspond au nombre de premiers caractères des phrases du texte lbc utilisés pour la recherche
	private int critereCorrespondance=10;

	public Texte(){

	}

	public Texte(int refTexte){
		this.refTexte =refTexte;
	}

	public Texte(String corpsTexteForPublication){
		this.corpsTexteForPublication = corpsTexteForPublication; 
	}

	public Texte( String corpsTexteInBase, TypeTexte typeTexte, int refTexte){
		this.corpsTexteInBase = corpsTexteInBase;
		this.typeTexte = typeTexte;
		this.refTexte = refTexte;
	}

	public int getLevenshteinDistanceBetweenLbcAndBdd() {
		return levenshteinDistanceBetweenLbcAndBdd;
	}

	public void setLevenshteinDistanceBetweenLbcAndBdd(){
		levenshteinDistanceBetweenLbcAndBdd = StringUtils.getLevenshteinDistance(corpsTexteInBase, corpsTexteOnLbc);
	}

	public int getLevelCorrespondance() {
		return critereCorrespondance;
	}

	public void setLevelCorrespondance(int levelCorrespondance) {
		this.critereCorrespondance = levelCorrespondance;
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
		if(this.corpsTexteInBase!=null)
			setLevenshteinDistanceBetweenLbcAndBdd();
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

	public String toString(){
		return corpsTexteForPublication;
	}


}
