package scraper;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class Add {
	private Title title;
	private Texte texte;
	private Commune commune;
	private File image;
	private Calendar dateMiseEnLigne;
	int nbJoursRestants;
	int nbVues;
	int nbMailsRecus;
	int nbClickTel;
	
	public Add(Title title, Texte texte, Commune commune, File image) {
		super();
		this.title = title;
		this.texte = texte;
		this.commune = commune;
		this.image = image;
	}
	
	public Add() {
		super();
	}
	
	public String toString(){
		String retour = commune.getNomCommuneInBase() +" : "+ commune.getCodePostal() +"\n"
							+ " nb vues : " + nbVues + " | nb mails " + nbMailsRecus + " | nb clics " + nbClickTel
							+ title.getTitre() + "\n" 
							+ texte.getCorpsTexteOnLbc(); 
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

	public Calendar getDateMiseEnLigne() {
		return dateMiseEnLigne;
	}

	public void setDateMiseEnLigne(Calendar dateMiseEnLigne) {
		this.dateMiseEnLigne = dateMiseEnLigne;
	}

	public int getNbJoursRestants() {
		return nbJoursRestants;
	}

	public void setNbJoursRestants(int nbJoursRestants) {
		this.nbJoursRestants = nbJoursRestants;
	}

	public int getNbVues() {
		return nbVues;
	}

	public void setNbVues(int nbVues) {
		this.nbVues = nbVues;
	}

	public int getNbMailsRecus() {
		return nbMailsRecus;
	}

	public void setNbMailsRecus(int nbMailsRecus) {
		this.nbMailsRecus = nbMailsRecus;
	}

	public int getNbClickTel() {
		return nbClickTel;
	}

	public void setNbClickTel(int nbClickTel) {
		this.nbClickTel = nbClickTel;
	}

	
	
}

