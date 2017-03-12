package scraper;

public class StatsOnCompte {
	private float nbClicMoyenParJourOnline=0;
	private float nbMailMoyenParJourOnline=0;
	private float nbVuesMoyenParJourOnline=0;
	
	private int nbTotaleClickOnline=0;
	private int nbTotaleMailOnline=0;
	
	
	private float nbJourMoyenOnline=0;
	private int nbAddsOnline=0;
	
	private float popMoyenneAddOnline=0;
	
	
	public StatsOnCompte() {
		super();
	}
	
	public void addNbTotaleClickOnline(float nbClickOnline) {
		this.nbTotaleClickOnline += nbClickOnline;
	}

	public float getNbTotaleClickOnline() {
		return nbTotaleClickOnline;
	}
	public void addNbTotaleMailOnline(float nbMailOnline) {
		this.nbTotaleMailOnline += nbMailOnline;
	}

	public float getNbTotaleMailOnline() {
		return nbTotaleMailOnline;
	}
	
	public void addPopMoyenneAddOnline(float pop) {
		this.popMoyenneAddOnline += pop;
	}

	public float getPopMoyenneAddOnline() {
		return popMoyenneAddOnline/(float)nbAddsOnline;
	}
	
	
	
	public float getNbClicMoyenParJourOnline() {
		return nbClicMoyenParJourOnline/(float)nbAddsOnline*100;
	}

	public void addNbClicMoyenParJourOnline(float nbClicMoyenParJourOnline) {
		this.nbClicMoyenParJourOnline += nbClicMoyenParJourOnline;
	}

	public float getNbMailMoyenParJourOnline() {
		return nbMailMoyenParJourOnline/(float)nbAddsOnline*100;
	}

	public void addNbMailMoyenParJourOnline(float nbMailMoyenParJourOnline) {
		this.nbMailMoyenParJourOnline += nbMailMoyenParJourOnline;
	}

	public float getNbVuesMoyenParJourOnline() {
		return nbVuesMoyenParJourOnline/(float)nbAddsOnline*100;
	}

	public void addNbVuesMoyenParJourOnline(float nbVuesMoyenParJourOnline) {
		this.nbVuesMoyenParJourOnline += nbVuesMoyenParJourOnline;
	}

	public void addNbJourMoyenOnline(int nbJourOnline) {
		this.nbJourMoyenOnline += nbJourOnline;
	}
	
	public float getNbJourMoyenOnline() {
		return nbJourMoyenOnline/(float)nbAddsOnline;
	}
	
	public void incrementNbAddsOnline() {
		nbAddsOnline++;
	}

	public int getNbAddsOnline() {
		return nbAddsOnline;
	}
	
	
	
	
	
}

