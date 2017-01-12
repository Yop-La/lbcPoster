package service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.HomeException;
import fr.doodle.dao.TexteDao;
import fr.doodle.dao.TitreDao;
import scraper.Commune;
import scraper.CompteLbc;
import scraper.ResultsControl;
import scraper.Source;
import scraper.Texte;
import scraper.Title;
import scraper.TypeTexte;
import scraper.TypeTitle;
import util.Console;

public class PrintManager {

	ObjectManager objectManager;

	public PrintManager(ObjectManager manager) {
		this.objectManager = manager;
	}

	public void printResults(){
		ResultsControl results = objectManager.getResults();
		System.out.println("Nb d'annonces refusées : "+results.getNbRefus());
		System.out.println("Nb d'annonces plus en ligne depuis dernier contrôle : "+results.getNbSuppression());
		System.out.println("Nb d'annonces de nouvelles annonces en ligne depuis le dernier contrôle : "+results.getNbNewAddsOnline());
		System.out.println("Nb d'annonces d'annonces toujours en ligne depuis dernier contrôle : "+results.getNbAddStillOnline());
	}

	public boolean toControlTexte() {
		List<Texte> textesPasDansBdd = objectManager.getTextePasDansLabdd();
		if(textesPasDansBdd.size()==0){
			return true;
		}else{
			for(Texte textePasDansBdd : textesPasDansBdd){
				System.out.println(textePasDansBdd);
			}
			return false;
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
				affichage = compte.getRefCompte()+" : "+compte.getMail();
				controleSaisie = "^"+compte.getRefCompte();
			}else{
				affichage = affichage+ "\n" + compte.getRefCompte()+" : "+compte.getMail();
				controleSaisie = controleSaisie+"|"+compte.getRefCompte();
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

	public String chooseTypeTitle() throws HomeException{
		TitreDao titleDao = new TitreDao();
		List<TypeTitle> typeTitles = titleDao.findAllTypeTitle();
		String regex="^";
		String toEnter = "";
		for(int i=0; i<typeTitles.size(); i++){
			TypeTitle typeTile = typeTitles.get(i);

			if(i==typeTitles.size()-1){
				regex = regex+typeTile.toString()+"$";
				toEnter = toEnter+typeTile.toString()+".";
			}else{
				regex = regex+typeTile.toString()+"|";
				toEnter = toEnter+typeTile.toString()+" ou ";
			}
		}
		String retour = readConsoleInput(regex, "Saisir le type de titre à choisir : "+toEnter,
				"Votre réponse", "doit être "+toEnter);
		return retour;
	}

	public String readConsoleInput(String regex, String message, String variableASaisir, String format)
			throws HomeException {
		Pattern p = Pattern.compile(regex);
		String phraseCloseApplication = "Voulez fermez l'application ? (si il y a un travail, il ne sera pas enregistré)";
		boolean continueBoucle = true;
		String input = "";
		while (continueBoucle) {
			input = Console.readString(message);
			if (input.equals(phraseCloseApplication))
				input = "autreChoseQueESCQueHomeQueOUIQueNON";
			switch (input) {
			case "ESC":
				String closeAppli = readConsoleInput("OUI|NON", phraseCloseApplication, "La réponse ",
						"être OUI ou NON.");
				if (closeAppli.equals("OUI")) {
					System.out.println("Vous venez de fermer l'application ! ");
					System.exit(0);
				} else {
					continueBoucle = true;
				}
				break;
			case "HOME":
				throw new HomeException();
			default:
				Matcher m = p.matcher(input);
				boolean b = m.matches();
				if (b) {
					continueBoucle = false;
				} else {
					System.out.println(variableASaisir + " doit " + format);
				}
				break;
			}
		}
		return (input);
	}

	public String chooseTypeTexte() throws HomeException{
		TexteDao texteDao = new TexteDao();
		List<TypeTexte> typeTitles = texteDao.findAllTypeTitle();
		String regex="^";
		String toEnter = "";

		for(int i=0; i<typeTitles.size(); i++){
			TypeTexte typeTexte = typeTitles.get(i);

			if(i==typeTitles.size()-1){
				regex = regex+typeTexte.toString()+"$";
				toEnter = toEnter+typeTexte.toString()+".";
			}else{
				regex = regex+typeTexte.toString()+"|";
				toEnter = toEnter+typeTexte.toString()+" ou ";
			}
		}
		String retour = readConsoleInput(regex, "Saisir le type de texte à choisir : "+toEnter,
				"Votre réponse", "doit être "+toEnter);
		return retour;
	}

	public void doYouWantToSaveAddIndd() throws HomeException{
		String saveAddInBase = readConsoleInput("^oui$|^non$", "Voulez vous sauvegarder les annonces publiés dans la base ?", "Votre réponse ",
				"être oui ou non.");
		if(saveAddInBase.equals("oui")){
			objectManager.setSaveAddToSubmitLbcInBase(true);
		}else{
			objectManager.setSaveAddToSubmitLbcInBase(false);
		}

	}

	public void printBilanPublication() {
		if(objectManager.getNbAddsPublie()==objectManager.getNbAddsToPublish()){
			System.out.println("La publication s'est déroulé sans erreur : "
		+objectManager.getNbAddsToPublish()+ " annonces publiées");
		}else{
			System.out.println("La publication terminé à cause d'une erreur : "
					+objectManager.getNbAddsPublie()+ " annonces publiées");
		}
		
	}

	public void printCompte() {
		int i = 0;
		for(CompteLbc compteLbc : objectManager.getComptes()){
			i++;
			System.out.println("Compte n°"+i+" : "+compteLbc.getMail()+" - password : "+compteLbc.getPassword());
		}
	}




}
