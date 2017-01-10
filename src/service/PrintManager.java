package service;

import java.util.List;

import scraper.Commune;
import scraper.CompteLbc;
import scraper.Source;

public class PrintManager {

	ObjectManager objectManager;

	public PrintManager(ObjectManager manager) {
		this.objectManager = manager;
	}

	
	// pour pouvoir afficher les comptes actifs
	public String[] comptestoString(){
		String[] retour = new String[2];
		String affichage = "";
		String controleSaisie = "";

		for(int i=0; i<objectManager.getComptes().size(); i++){
			CompteLbc compte = objectManager.getComptes().get(i);
			if(i==0){
				affichage = compte.getIdAdmin()+" : "+compte.getMail();
				controleSaisie = "^"+compte.getIdAdmin();
			}else{
				affichage = affichage+ "\n" + compte.getIdAdmin()+" : "+compte.getMail();
				controleSaisie = controleSaisie+"|"+compte.getIdAdmin();
			}
		}
		controleSaisie = controleSaisie + "$";
		retour[0]=affichage;
		retour[1]=controleSaisie;
		return retour;
	}
	
	public String[] typeSourcesToString(){
		String[] retour = new String[2];
		String affichage = "";
		String controleSaisie = "";
		for(int i=0; i<Source.values().length; i++){
			Source typeSource = Source.values()[i];
			if(i==0){
				affichage = typeSource.toString();
				controleSaisie = "^"+typeSource;
			}else{
				affichage = affichage+ "\n" + typeSource;
				controleSaisie = controleSaisie+"|"+typeSource;
			}
		}
		controleSaisie = controleSaisie + "$";
		retour[0]=affichage;
		retour[1]=controleSaisie;
		return retour;
	}
	
	public String listToString(List list){
		String retour = "";
		for(int i=0; i<list.size(); i++){
			Object objet = list.get(i);
			if(i==0){
				retour = objet.toString();
				
			}else{
				retour = retour+ "\n" + objet.toString();
			}
		}
		return retour;
	}
	
	public String titreToString(){
		return listToString(objectManager.getTitleSource());
	}


	public String texteToString() {
		return listToString(objectManager.getTexteSource());
	}


	public String communeToString() {
		return listToString(objectManager.getCommuneSource());
	}

	public String toCompareCommunes(Commune commune){
		String retour = "";
		retour = "Commune sur LBC : "+commune.getNomCommuneOnLbc()+" vs commune dans la bdd : "+commune.getNomCommuneInBase();
		return retour;
	}
}
