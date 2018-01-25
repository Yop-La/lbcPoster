package scraper;

public class ParametresPublication {
	private boolean afficherNum;
	private String numTelephone;
	private int nbDannoncesAPublier;
	private boolean prix;
	public boolean isPrix() {
		return prix;
	}
	public void setPrix(boolean prix) {
		this.prix = prix;
	}
	private AddCategory addCategory;
	
	public boolean isAfficherNum() {
		return afficherNum;
	}
	public void setAfficherNum(boolean afficherNum) {
		this.afficherNum = afficherNum;
	}
	public String getNumTelephone() {
		return numTelephone;
	}
	public void setNumTelephone(String numTelephone) {
		this.numTelephone = numTelephone;
	}
	public int getNbDannoncesAPublier() {
		return nbDannoncesAPublier;
	}
	public void setNbDannoncesAPublier(int nbDannoncesAPublier) {
		this.nbDannoncesAPublier = nbDannoncesAPublier;
	}
	public AddCategory getAddCategory() {
		return addCategory;
	}
	public void setAddCategory(AddCategory addCategory) {
		this.addCategory = addCategory;
	}
	
	
}
