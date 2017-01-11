package service;

import java.util.List;
import org.apache.commons.lang3.StringUtils; 
import scraper.Commune;
import scraper.CompteLbc;
import scraper.Source;
import scraper.Texte;
import scraper.Title;

public class PrintManager {

	ObjectManager objectManager;

	public PrintManager(ObjectManager manager) {
		this.objectManager = manager;
	}
	
	public boolean toCompareTextes() {
		Texte texteReadyToSave = objectManager.nextTexteReadyTosave();
		if(texteReadyToSave.getTypeTexte()==null){// si pas de correspondance entre le titre du bon coin et la bdd
			System.out.println("Le texte du bon coin ne correspond à aucune titre de la bdd");
			System.out.println("Voilà le texte en question du bon coin pas dans la bbd : "+texteReadyToSave);
			return false;
		}else{
			String stringA = texteReadyToSave.getCorpsTexteInBase();
			String stringB = texteReadyToSave.getCorpsTexteOnLbc();
			System.out.println(StringUtils.getLevenshteinDistance(stringA, stringB));
			return true;
		}
	}

	public boolean toCompareTitles(){ // retourne false si pas de correspondance
			Title titleReadyToSave = objectManager.nextTitleReadyTosave();
			if(titleReadyToSave.getTypeTitle()==null){// si pas de correspondance entre le titre du bon coin et la bdd
				System.out.println("Le titre du bon coin ne correspond à aucune titre de la bdd");
				System.out.println("Voilà le titre en question du bon coin pas dans la bbd : "+titleReadyToSave);
				return false;
			}else{
				return true;
			}
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

	// pour afficher les communes dont le nom sur lbc n'a pas été trouvé dans la bdd
	public boolean toCompareCommunes(Commune commune){
		String affichage = "";
		String pasCorresp=""; 
		if(commune.getNomCommuneInBase()!=null){
			pasCorresp=commune.getNomCommuneInBase();
		}else{
			pasCorresp = "pas de correspondance";
		}
		affichage = "Commune sur LBC : "+commune.getNomCommuneOnLbc()+" (code postal on LBC: "+commune.getCodePostal()+" ) vs commune dans la bdd : "+pasCorresp;
		boolean pasDeCorrespEntreLbcEtBdd = (commune.getCodeCommune() == null);
		if(pasDeCorrespEntreLbcEtBdd){
			System.out.println("La commune du bon con ne correspond à aucune commune de la bdd");
			System.out.println("Faîte une recherche dans la bdd afin de trouver la commune correspondante ");
			System.out.println(affichage);
		}
		return pasDeCorrespEntreLbcEtBdd;

	}


	public void searchResults(String nomCom){
		List<Commune> communes = objectManager.search(nomCom);
		for(Commune commune : communes){
			System.out.println("ref_commune : "+commune.getRefCommune()+" - Nom commune dans bdd : "+commune.getNomCommuneInBase());
		}
	}





}
