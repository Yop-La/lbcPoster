package service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.apache.poi.util.SystemOutLogger;

import exception.HomeException;
import fr.doodle.dao.AddDao;
import fr.doodle.dao.CommuneDao;
import fr.doodle.dao.CompteLbcDao;
import fr.doodle.dao.ResumeDao;
import fr.doodle.dao.TexteDao;
import fr.doodle.dao.TitreDao;
import scraper.Add;
import scraper.CaseOfMatching;
import scraper.Commune;
import scraper.CommuneLink;
import scraper.CompteLbc;
import scraper.OperationToSolveMatching;
import scraper.ResultsControl;
import scraper.Source;
import scraper.StatsOnCommune;
import scraper.Texte;
import scraper.TexteAndTitleManager;
import scraper.Title;
import scraper.TypeTexte;
import scraper.TypeTitle;
import util.Console;

public class PrintManager extends JPanel{

	ObjectManager objectManager;
	final JFileChooser fc = new JFileChooser();

	public PrintManager(ObjectManager manager) {
		this.objectManager = manager;
	}

	public void printResults(){
		ResultsControl results = objectManager.getResults();
		System.out.println("Nb d'annonces refusées par la modération : "+results.getNbRefus());
		System.out.println("Nb d'annonces plus en ligne depuis dernier contrôle (périmé ou supprimé) : "+results.getNbSuppression());
		System.out.println("Nb de nouvelles annonces qui était pas dans la bdd (pas ref pendant la publication) : "+results.getNbNewAddsOnline());
		System.out.println("Nb d'annonces d'annonces toujours en ligne depuis dernier contrôle : "+results.getNbAddStillOnline());
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
	public void afficherNbTitleForPublication(){
		System.out.println("Le nombre de titres retenus pour la publication est de : "+objectManager.getTitleSource().size());
	}
	public void afficherNbTexteForPublication(){
		System.out.println("Le nombre de textes retenus pour la publication est de : "+objectManager.getTexteSource().size());
	}
	public void afficherNbCommunesForPublication(){
		System.out.println("Le nombre de communes retenus pour la publication est de : "+objectManager.getCommuneSource().size());
	}

	public void searchResults(String nomCom){
		List<Commune> communes = objectManager.search(nomCom);
		if(communes.size()==0){
			System.out.println("Aucune commune ne correspond à ce résultat. Changer les termes de votre recherche");
		}else{
			for(Commune commune : communes){
				System.out.println("ref_commune : "+commune.getRefCommune()+" - Nom commune dans bdd : "+commune.getNomCommune()+" code dep : "+commune.getCodeDep());
			}
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
				toEnter = toEnter+typeTile.toString()+"\n";
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
		TypeTexte[] typeTitles = TypeTexte.values();
		String regex="^";
		String toEnter = "";

		for(int i=0; i<typeTitles.length; i++){
			TypeTexte typeTexte = typeTitles[i];

			if(i==typeTitles.length-1){
				regex = regex+typeTexte.toString()+"$";
				toEnter = toEnter+typeTexte.toString()+".";
			}else{
				regex = regex+typeTexte.toString()+"|";
				toEnter = toEnter+typeTexte.toString()+"\n";
			}
		}
		String retour = readConsoleInput(regex, "Saisir le type de texte à choisir : \n"+toEnter,
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
			System.out.println("La publication s'est terminé prématurément à cause d'une erreur : "
					+objectManager.getNbAddsPublie()+ " annonces publiées");
		}
	}

	// cette méthode est utilisé après publication des annonces sur les annonces soumises à la modération
	// ces annonces ont des commmunesLink constitutés
	// d'une commune online qui ont un nom_commune et code_postal soumis
	// d'une commune submit qui ont une ref_commune un nom_commune et peut être un code_postal 
	// le but de cette méthode est de : 
	//    - mettre à jour code_postal soumis quand il n'est pas connu
	//    - mettre à jour ref_commune, nom_commune et code_postal sur lbc
	//    - et enfin de mettre à jour la ref_commune de la table adds des annonces soumises
	//		car il se peut qu'elle référence une commune en base qui n'a rien à voir avec celle en ligne

	// pour que commune online soit référencée et à jour dans la bdd
	// pour que commune submit à jour dans la bdd (code postal)
	public void gererCorrepondanceCommunes(List<Add> adds) {
		// pour lier la commune en ligne à la bdd (pas de mise à jour du code postale sauf pour linsert)
		int indiceAdd=1;
		for(Add add : adds){
			System.out.println();
			System.out.println("Commune de l'add n°"+indiceAdd+" de ref n° "+add);
			System.out.println();
			toReferencesOnlineCommune(add);
			CommuneLink communeLink = add.getCommuneLink();
			// mise à jour code postal de submit et de online
			miseAJourDuCodePostalDeSubmit(communeLink);
			System.out.println("Comparaison des communes après mise à jour du code postal de submit ");
			communeLink.printComparaison();
			System.out.println();
			System.out.println();
			indiceAdd++;
			valider("Voulez vous passer à la commune suivante ? ");

		}
	}
	// cette méthode reçoit en entrée :
	// - cas 1 : la liste des annonces après publication
	// - cas 2 : ou la liste des annonces scannés sur lbc après modération

	// cas 1 : pour chaque annonce, on connaît submit et online
	// cas 2 : il y a des annoncs où on ne connaît pas submit mais on connaît toujours online

	// on doit dans tous les cas relier online à la bdd
	// mettre à jour la ref_commune de la commune en base pour qu'elle corresponde à celle online
	// mettre à jour le code postal de onlin et de submit ( si submit existe et est différent de online)

	public CaseOfMatching toReferencesOnlineCommune(Add add){
		CommuneLink communeLink = add.getCommuneLink();
		CaseOfMatching caseOfMAtch = communeLink.getCaseOfMatch();
		OperationToSolveMatching opToSolveMatching=null;
		System.out.println("Comparaison des communes avant gestion des correspondances");
		communeLink.printComparaison();
		System.out.println();
		switch(caseOfMAtch){
		case perfectMatch:
			handlePerfectMatch(add);
			break;
		case sameNameOneMatch:
			handleSameNameOneMatch(add);
			break;
		case differentNameOneMatch:
			handleDifferentNameOneMatch(add);
			break;
			// peu de chance que ça arrive car les couples nom_commune et code_dep sont uniques
		case sameNameSeveralMatch:
			handleSameNameSeveralMatch(add);
			break;
			// peu de chance que ça arrive car les couples nom_commune et code_dep sont uniques
		case differentNameSeveralMatch://code postal de submit à mettre à jour
			handleDifferentNameSeveralMatch(add);
			break;
		case differentNameNoMatch://code postal de submit à mettre à jour si insertion 
			//(code postal de submit automatiquement mis à jour si pas changement de nom
			handleDifferentNameNoMatch(add);
			break;
		case noSubmitNoMatch:
			handleNoSubmitNoMatch(add);
			break;
		case noSubmitOneMatch:
			handleNoSubmitOneMatch(add);
			break;
		case noSubmitSeveralMatch:
			handleNoSubmitSeveralMatch(add);
			break;
		}
		System.out.println("Comparaison des communes après gestion des correspondances");
		communeLink.printComparaison();
		System.out.println();
		return caseOfMAtch;
	}	

	private void miseAJourDuCodePostalDeSubmit(CommuneLink communeLink) {
		if(communeLink.submit == null){
			System.out.println("Pas de code postal de submit à enregistrer \n"
					+ "car pas de commune soumise enregistré au moment de la publication");
		}else{
			if(communeLink.submit.getCodePostal().equals("")){
				System.out.println("Mise à jour du code postal de la commune soumise");
				communeLink.submit.setCodePostal(saisirCodePostal());
				communeLink.submit.updateCodePostal();
			}else{
				System.out.println("Pas de code postal de submit à enregistrer \n"
						+ "car soit perfect match, soit autre chose");
			}
		}
	}

	private boolean valider(String message) {
		try{
			String confirmation = readConsoleInput("^o|n$", 
					message,
					"Votre réponse", 
					" être o ou n");
			if(confirmation.equals("oui")){
				return true;
			}else{
				return false;
			}
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(isOnlinCorrespondToSubmit());
		}
	}


	private void handleDifferentNameNoMatch(Add add) {
		System.out.println("Commune soumise et en ligne ont des noms différents");
		System.out.println("De plus, aucunes commune en base correspondante à celle en ligne");
		System.out.println("Dans ce cas, commune soumise et en ligne peuvent êtres les mêmes "
				+ "\n à une différence minime de nom près.");
		System.out.println("À vous : "
				+ "\n de trouver la commune en bdd correspondante (si le nom est lègerement, il sera updaté)"
				+ "\n  ou de l'ajouter");

		CommuneLink communeLink = add.getCommuneLink();
		CommuneDao comDao = new CommuneDao();

		searchTheCorrespondingCommune();
		String choixOperation = choixOperationPourNoMatch();
		if(choixOperation.equals("addNewCommune")){
			Commune communeToInsert = saisirCommunesToInsert();
			communeToInsert.setCodePostal(communeLink.onLine.getCodePostal());
			communeToInsert.setNomCommune(communeLink.onLine.getNomCommune());
			Commune communeInserted =comDao.save(communeToInsert);
			communeLink.onLine=communeInserted;

		}else if(choixOperation.equals("selectCommuneNameInBase")){
			System.out.println("Saisir l'id de la commune en base correspondante à celle en ligne"
					+ " dont le nom sera remplacé par celui online");
			int refCommuneSelected = selectAcommuneFromTheResults();
			Commune communeSelected = comDao.findOne(refCommuneSelected);
			communeSelected.setNomCommune(communeLink.onLine.getNomCommune());
			communeSelected.setCodePostal(communeLink.onLine.getCodePostal());

			System.out.println("Mise à jour du code postal de la commune correspondante et de son nom");
			communeSelected.updateCodePostal();
			communeSelected.updateNom();
			communeLink.onLine = communeSelected;	
		}
		AddDao addDao = new AddDao();
		System.out.println("Mise à jour de la ref commune de l'add");
		addDao.updateRefCommune(add);	
		if(isOnlinCorrespondToSubmit()){
			communeLink.submit = communeLink.onLine;
		}



	}

	private boolean isOnlinCorrespondToSubmit() {
		try{
			String confirmation = readConsoleInput("^oui|non$", 
					"Est ce que la commune soumise correspondait à la commune online (excepté quelques différences de nom) ?",
					"Votre réponse", 
					" oui ou non");
			if(confirmation.equals("oui")){
				return true;
			}else{
				return false;
			}
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(isOnlinCorrespondToSubmit());
		}
	}

	private String choixOperationPourNoMatch() {
		try{
			String choixOperation = readConsoleInput("^addNewCommune|selectCommuneNameInBase$", 
					"Quelle opération choissisez vous pour résoudre la correspondance ?"
							+ "Choisir addNewCommune si la commune correspondante n'est clairement pas en base"
							+ "Choisir selectCommuneNameInBase si la commune apparaît en base (même avec un nom différent)",
							"Votre réponse", 
					" addNewCommune ou selectCommuneNameInBase");
			return choixOperation;
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(choixOperationPourNoMatch());
		}
	}

	private void handleDifferentNameSeveralMatch(Add add) {
		System.out.println("Commune soumise et en ligne ont des noms différents");
		System.out.println("De plus, plusieurs noms de commune en base correspondante à celle en ligne");
		System.out.println("Dans ce cas, commune soumise et en ligne sont différentes");
		System.out.println("À vous de choisir celle qui correspond");
		System.out.println("Ce genre de cas ne devrait pas arriver car les couples nom commune et code dep sont uniques");

		CommuneLink communeLink = add.getCommuneLink();
		searchTheCorrespondingCommune();
		int refCommuneSelected = selectAcommuneFromTheResults();
		Commune communeSelected = new Commune();
		communeSelected.setRefCommune(refCommuneSelected);
		CommuneDao comDao = new CommuneDao();
		communeSelected = comDao.findOne(communeSelected.getRefCommune());
		if(communeSelected.getCodePostal().equals("")){
			communeSelected.setCodePostal(communeLink.onLine.getCodePostal());
			System.out.println("Mise à jour du code postal de la commune correspondante");
			comDao.updateCodePostal(communeLink.onLine);
		}else{
			System.out.println("Pas de besoin de mettre à jour code postal de la commune correspondante"
					+ " car La commune en base correspondante à celle online avait déjà un code postal");
		}
		communeLink.onLine=communeSelected;


		System.out.println("Mise à jour de la ref commune de l'add");
		AddDao addDao = new AddDao();
		addDao.updateRefCommune(add);

	}

	private void handleSameNameSeveralMatch(Add add) {
		System.out.println("Commune soumise et en ligne ont le même nom");
		System.out.println("De plus, plusieurs noms de commune en base correspondante à celle en ligne");
		System.out.println("Dans ce cas, commune soumise et en ligne peuvent être les mêmes");
		System.out.println("À vous de choisir celle qui correspond");
		System.out.println("Ce genre de cas ne devrait pas arriver car les couples nom commune et code dep sont uniques");

		CommuneLink communeLink = add.getCommuneLink();
		searchTheCorrespondingCommune();
		int refCommuneSelected = selectAcommuneFromTheResults();
		Commune communeSelected = new Commune();
		communeSelected.setRefCommune(refCommuneSelected);
		CommuneDao comDao = new CommuneDao();
		communeSelected = comDao.findOne(communeSelected.getRefCommune());
		if(communeSelected.getCodePostal().equals("")){
			communeSelected.setCodePostal(communeLink.onLine.getCodePostal());
			System.out.println("Mise à jour du code postal de la commune correspondante");
			comDao.updateCodePostal(communeLink.onLine);
		}else{
			System.out.println("Pas de besoin de mettre à jour code postal de la commune correspondante"
					+ " car La commune en base correspondante à celle online avait déjà un code postal");
		}
		communeLink.onLine=communeSelected;

		System.out.println("Mise à jour de la ref commune de l'add");
		AddDao addDao = new AddDao();
		addDao.updateRefCommune(add);
		if(isOnlinCorrespondToSubmit()){
			communeLink.submit = communeLink.onLine;
		}
	}

	private void handlePerfectMatch(Add add) { 
		System.out.println(" C'est un perfect match !");
		System.out.println(" Le nom de la commune et le code postal sont identiques");
		System.out.println("Dans ce cas, commune soumise et en ligne sont les mêmes");
		System.out.println(" Il n'y a donc pas de problème de correspondance");
		CommuneLink communeLink = add.getCommuneLink();
		communeLink.onLine=communeLink.submit;
		System.out.println("Tout est ok ! Pas de mise à jour à faire");
	}

	private void handleDifferentNameOneMatch(Add add) {
		System.out.println("Commune soumise et en ligne ont des noms différents");
		System.out.println("De plus, un seul nom de commune en base correspondante à celle en ligne");
		System.out.println(" Problème de correspondance qui va être résolu automatiquement");
		System.out.println("Dans ce cas, commune soumise et en ligne sont différentes");
		System.out.println(" On va récupérer la ref de la commune online "
				+ "\net updater avec cette ref commune la ref de l'add");

		// récupération de la commune online en base et mise à jour de son code postal
		CommuneLink communeLink = add.getCommuneLink();
		String codePostalOnline = communeLink.onLine.getCodePostal();
		CommuneDao comDao = new CommuneDao();
		communeLink.onLine = comDao.findOneWithNomCommuneAndCodeDep(communeLink.onLine);
		if(communeLink.onLine.getCodePostal().equals("")){
			System.out.println("Mise à jour du code postal de la commune correspondante");
			communeLink.onLine.setCodePostal(codePostalOnline);
			communeLink.onLine.updateCodePostal();
		}else{
			System.out.println("Pas de besoin de mettre à jour code postal de la commune correspondante"
					+ " car La commune en base correspondante à celle online avait déjà un code postal");
		}

		System.out.println("Mise à jour de la ref commune de l'add");
		AddDao addDao = new AddDao();
		addDao.updateRefCommune(add);

	}

	private void handleSameNameOneMatch(Add add) {
		System.out.println("Commune soumise et en ligne ont le même non");
		System.out.println("De plus, une unique commune en base correspondante à celle en ligne");
		System.out.println("Ce problème de correspondance va être géré automatiquement");
		System.out.println("Dans ce cas, commune soumise et en ligne sont les mêmes");
		System.out.println("Mise à jour du code postal de online (qui est aussi submit)");

		CommuneLink communeLink = add.getCommuneLink();
		communeLink.codePostalGoToSubmit();
		communeLink.onLine=communeLink.submit;
		System.out.println("Mise à jour du code postal de la commune correspondante");
		communeLink.onLine.updateCodePostal();
	}

	private void handleNoSubmitSeveralMatch(Add add) {
		System.out.println("La commune soumise n'existe pas ! ");
		System.out.println("C'est une annonce pas enregistré pendant la publication");
		System.out.println("La commune en ligne a plusieurs communes correspondante en base");
		System.out.println("À vous de choisir la commune correspondante à celle en ligne");
		System.out.println("Ce genre de cas ne devrait pas arriver car les couples nom commune et code dep sont uniques");

		CommuneLink communeLink = add.getCommuneLink();
		searchTheCorrespondingCommune();
		int refCommuneSelected = selectAcommuneFromTheResults();
		Commune communeSelected = new Commune();
		communeSelected.setRefCommune(refCommuneSelected);
		CommuneDao comDao = new CommuneDao();
		communeSelected = comDao.findOne(communeSelected.getRefCommune());
		if(communeSelected.getCodePostal().equals("")){
			communeSelected.setCodePostal(communeLink.onLine.getCodePostal());
			System.out.println("Mise à jour du code postal de la commune correspondante");
			comDao.updateCodePostal(communeLink.onLine);
		}else{
			System.out.println("Pas de besoin de mettre à jour code postal de la commune correspondante"
					+ " car La commune en base correspondante à celle online avait déjà un code postal");
		}
		communeLink.onLine=communeSelected;

		System.out.println("Mise à jour de la ref commune de l'add");
		AddDao addDao = new AddDao();
		addDao.updateRefCommune(add);
	}

	private void handleNoSubmitOneMatch(Add add) {
		System.out.println("La commune soumise n'existe pas ! ");
		System.out.println("C'est une annonce pas enregistré pendant la publication");
		System.out.println("La commune en ligne a une unique communes correspondante en base");
		System.out.println("Cette correspondance va être géré automatiquement");

		CommuneLink communeLink = add.getCommuneLink();
		String codePostalOnline = communeLink.onLine.getCodePostal();
		CommuneDao comDao = new CommuneDao();
		communeLink.onLine= comDao.findOneWithNomCommuneAndCodeDep(communeLink.onLine);

		if(communeLink.onLine.getCodePostal().equals("")){
			System.out.println("Mise à jour du code postal de la commune correspondante");
			communeLink.onLine.setCodePostal(codePostalOnline);
			communeLink.onLine.updateCodePostal();
		}else{
			System.out.println("Pas de besoin de mettre à jour code postal de la commune correspondante"
					+ " car La commune en base correspondante à celle online avait déjà un code postal");
		}
		System.out.println("Mise à jour de la ref commune de l'add");
		AddDao addDao = new AddDao();
		addDao.updateRefCommune(add);



	}

	private void handleNoSubmitNoMatch(Add add) {
		System.out.println("La commune soumise n'existe pas ! ");
		System.out.println("C'est une annonce pas enregistré pendant la publication");
		System.out.println("La commune en ligne n'a pas de communes correspondante en base");
		System.out.println("À vous  : "
				+ "\n de trouver la commune en bdd correspondante (si le nom est lègerement, il sera updaté)"
				+ "\n  ou de l'ajouter");

		CommuneLink communeLink = add.getCommuneLink();
		CommuneDao comDao = new CommuneDao();

		searchTheCorrespondingCommune();
		String choixOperation = choixOperationPourNoMatch();
		if(choixOperation.equals("addNewCommune")){
			Commune communeToInsert = saisirCommunesToInsert();
			communeToInsert.setCodePostal(communeLink.onLine.getCodePostal());
			communeToInsert.setNomCommune(communeLink.onLine.getNomCommune());
			Commune communeInserted =comDao.save(communeToInsert);
			communeLink.onLine=communeInserted;

		}else if(choixOperation.equals("selectCommuneNameInBase")){
			System.out.println("Saisir l'id de la commune en base correspondante à celle en ligne"
					+ " dont le nom sera remplacé par celui online");
			int refCommuneSelected = selectAcommuneFromTheResults();
			Commune communeSelected = comDao.findOne(refCommuneSelected);
			communeSelected.setNomCommune(communeLink.onLine.getNomCommune());
			communeSelected.setCodePostal(communeLink.onLine.getCodePostal());

			System.out.println("Mise à jour du code postal de la commune correspondante et de son nom");
			communeSelected.updateCodePostal();
			communeSelected.updateNom();
			communeLink.onLine = communeSelected;	
		}
		AddDao addDao = new AddDao();
		System.out.println("Mise à jour de la ref commune de l'add");
		addDao.updateRefCommune(add);	
	}



	private void searchTheCorrespondingCommune() {
		String refaire;
		do{
			refaire = rechercheCommuneInbdd();
		}while(refaire.equals("oui"));
	}		

	private int selectAcommuneFromTheResults() {
		String elementsRequete="";
		try{
			elementsRequete = readConsoleInput("^\\d+$", 
					"Entrez l'id de la commune correspondante", 
					"La réponse ",
					"être un entier");
		}catch(HomeException homExecp){
			System.out.println("Terminer le traitement en cours avant de revenir au menu");
			return(selectAcommuneFromTheResults());
		}
		return(Integer.parseInt(elementsRequete));
	}

	private void insertANewCommuneFromLbcInBdd(CommuneLink CommuneLink) {
		Commune communeToInsert = saisirCommunesToInsert();

	}

	private Commune saisirCommunesToInsert() {
		Commune retour = new Commune();
		String codeDep = saisirCodeDep();
		String codeReg = saisirCodeReg();
		String nomReg = saisirNomReg();
		String popTot = saisirPopTot();
		String codeCommune = saisirCodeCommune();
		retour.setCodeDep(codeDep);
		retour.setCodeReg(codeReg);
		retour.setNomReg(nomReg);
		retour.setPopTotale(Float.parseFloat(popTot));
		retour.setCodeCommune(codeCommune);
		return retour;
	}

	private String choixPourFaireLaCorrespondance(){
		try{
			String choix = readConsoleInput("^recherche$|^addCommuneOnlineToBdd$|^changeTheNameOfCommuneInBase$|^linkCommuneOnLineToAnotherCommuneInBase$", 
					"Pour renouvellez la recherche tapez recherche. "
							+ "	\nOu tapez addCommuneOnlineToBdd pour ajouter la commune online qui est pas dans la bdd"
							+ "	\n(faire ce choix si commune du bon coin apparait clairement pas dans la base)"
							+ "	\nOu tapez changeTheNameOfCommuneInBase pour changer légèrement le nom de la commune soumise dans la bdd"
							+ "	\n(faire ce choix si vous il y a juste une légère différence de nom entre la commune soumise et celle du bon coin)"
							+ "	\nOu tapez linkCommuneOnLineToAnotherCommuneInBase pour faire correspondre la commune existante dans la bdd à celle du bon coin"
							+ "	\n(faire ce choix si vous avez trouvez une commune correspondante dans la liste des résultats à celle du bonc coin)",
							"Votre réponse", 
					" être recherche ou addCommuneOnlineToBdd ou changeTheNameOfCommuneInBase ou linkCommuneOnLineToAnotherCommuneInBase" );
			return choix;
		}catch(HomeException home){
			System.out.println("Terminer le traitement en cours avant de revenir au menu");
			return "recherche";
		}
	}

	private String rechercheCommuneInbdd() {
		try{
			String elementsRequete = readConsoleInput("^((\\S*)|(0)),((\\d*)|(0)),((\\d*)|(0))$", 
					"Entrez votre requête pour rechercher la commune correspondante à celle du bon coin dans la base "
							+ " arrêter la recherche si vous l'avez trouver ou pas."
							+ "\nrequête de la forme : commune,codedep,codecommune"
							+ "\nexemple de requête : toulou,0,0", 
							"La réponse ",
					"être de la forme commune,codedep,codecommune");
			CommuneDao communeDao = new CommuneDao();
			List<Commune> communesResults = communeDao.searchWithNameCodeDepAndCodeComm(elementsRequete);
			if(communesResults.size() == 0){
				System.out.println("Aucun résultat");
			}
			for(Commune communeResult : communesResults){
				communeResult.printCommune();
			}
			String refaire = readConsoleInput("^oui|non$", 
					"Voulez vous refaire une recherche ? ", 
					"La réponse ",
					"être oui ou non");
			return refaire;
		}catch(HomeException excep){
			System.out.println("Terminer le traitement en cours avant de revenir au menu");
			return(rechercheCommuneInbdd());
		}

	}



	public void printComptes() {
		for(CompteLbc compteLbc : objectManager.getComptes()){
			printCompte(compteLbc, false);
		}
	}

	private void printCompte(CompteLbc compteLbc, boolean details) {

		Calendar dateLimite = Calendar.getInstance();
		System.out.print("Compte n°"+compteLbc.getRefCompte()+" : "+compteLbc.getMail()+" - pseudo : "+compteLbc.getPseudo());
		if(compteLbc.getPseudo()==null){
			System.out.print(" <-----");
		}
		System.out.println();
		if(compteLbc.isDisabled()){
			System.out.println("    password : "+compteLbc.getPassword());
			System.out.println("Ce compte est désactivé ! ");
			System.out.println();
		}else{

			System.out.print("    Nb d'annonces en ligne : "+compteLbc.getNbAnnoncesEnLigne());
			if(compteLbc.getNbAnnoncesEnLigne()==0){
				System.out.print(" <-----");
			}
			System.out.println();
			if(compteLbc.isThatCompteNeedsAnIntervention()){
				dateLimite.add(Calendar.DAY_OF_MONTH, -10);
				System.out.print("    date dernière activité : "+compteLbc.getPrintableDateDerniereAct());
				if(compteLbc.getDateDerniereActivite()==null){			
					System.out.print(" <-----");
				}else{
					if(compteLbc.getDateDerniereActivite().before(dateLimite)){
						System.out.print(" <-----");
					}
				}
				System.out.println();

				System.out.print("    Redirection : "+compteLbc.isRedirection());
				if(compteLbc.isRedirection()==false){
					System.out.print(" <-----");
				}
				System.out.println();


				dateLimite.add(Calendar.DAY_OF_MONTH, 10);
				System.out.print("    date de péremption : "+compteLbc.getPrintableDateAvantPeremption());
				if(compteLbc.getDateAvantPeremption()==null){
					System.out.print(" <-----");
				}else{
					if(compteLbc.getDateAvantPeremption().before(dateLimite)){
						System.out.print(" <-----");
					}
				}
				System.out.println();


				dateLimite.add(Calendar.DAY_OF_MONTH, -30);
				System.out.print("    Dernier contrôle : "+compteLbc.getPrintableDateOfLastControl());
				if(compteLbc.getDateDernierControle()==null){
					System.out.print(" <-----");
				}else{
					if(compteLbc.getDateDernierControle().before(dateLimite)){
						System.out.print(" <-----");
					}
				}
				System.out.println();


				if(details){
					System.out.println("    password : "+compteLbc.getPassword());
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	public File selectFileWithTexte() {
		int returnVal = fc.showOpenDialog(PrintManager.this);
		boolean fileNotSelected= true;
		do{

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//This is where a real application would open the file.
				return file;
			} else {
				fileNotSelected= true;
			}
			returnVal = fc.showOpenDialog(PrintManager.this);
		}while(fileNotSelected);
		return null;
	}

	public String saisirCodeDep(){
		try{
			String ret = readConsoleInput("^\\d{2,3}$", 
					"Saisir le code dep de la commune à insérer dans la bdd",
					"Votre réponse ", 
					"doit être un entier de 2 ou 3 chiffres");
			return ret;
		}catch(Exception excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(saisirCodeDep());
		}
	}

	public String saisirPopTot(){
		try{
			String ret = readConsoleInput("^\\d+", 
					"Saisir la population de la commune à insérer",
					"Votre réponse", 
					"doit être un entier");
			return ret;
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(saisirPopTot());
		}
	}
	public String saisirCodeCommune(){
		try{
			String ret = readConsoleInput("^\\d{2,3}", 
					"Saisir le code commune de la commune à insérer",
					"Votre réponse", 
					" être un entier de 2 à 3 caractères");
			return ret;
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(saisirCodeCommune());
		}
	}
	public String saisirCodeReg(){
		try{
			String ret = readConsoleInput("^\\d{2,3}", 
					"Saisir le code région de la commune à insérer",
					"Votre réponse", 
					" être un entier de 2 à 3 caractères");
			return ret;
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(saisirCodeReg());
		}
	}
	public String saisirCodePostal(){
		try{
			String ret = readConsoleInput("^\\d{5}", 
					"Saisir le code postal de la commune à insérer",
					"Votre réponse", 
					" être un entier de 5 caractères");
			return ret;
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(saisirCodePostal());
		}
	}


	public String saisirNomReg(){
		try{
			String ret = readConsoleInput("^\\S.+$", 
					"Saisir le nom de la région de la commune à insérer",
					"Votre réponse", 
					" être une chaîne de caractères ne commencant pas par un espace");
			return ret;
		}catch(HomeException excep){
			System.out.println("Impossible de terminer le traitement en cours");
			return(saisirNomReg());
		}
	}

	// est appelé pendant la phase de préparation à la sauvegarde des annonces récupérés sur lbc
	// pour lié les éléments des annonces en ligne qu'on a pas pu faire automatiquement
	public void toLinkTexteAndTitleWhitoutRef() {
		toLinkTitleWhitoutRef();
		toLinkTexteWhitoutRef();
		//	toLinkCommuneWhitoutRef();
	}


	private void toLinkTexteWhitoutRef() {
		System.out.println("---- Affichage des textes pas référencés ----");
		List<Add> addsWithoutRefForTexte = objectManager.getAddsSaver().getAddsOnLineWithoutRefForTexte();
		if(addsWithoutRefForTexte.size()==0){
			System.out.println("Toutes les textes sont bien référencés");
			return;
		}
		for(Add add : addsWithoutRefForTexte){
			Texte texteNotReferenced = add.getTexte();

			System.out.println("Ce texte n'est pas référencé : "+texteNotReferenced.getCorpsTexteOnLbc());
			System.out.println("C'est le texte de l'add n°"+add);
			try{
				readConsoleInput("^OK$", 
						"En attente d'une intervention manuelle ! Mettez à jour la bdd. Tapez OK pour reprendre",
						"Votre réponse", 
						" être OK");
			}catch(HomeException excep){
				System.out.println("Impossible de terminer le traitement en cours");
			}
		}
	}

	private void toLinkTitleWhitoutRef() {
		System.out.println("---- Affichage des titres pas référencés ----");
		List<Add> addsWithoutRefForTitre = objectManager.getAddsSaver().getAddsOnLineWithoutRefForTitre();
		if(addsWithoutRefForTitre.size()==0){
			System.out.println("Toutes les titres sont bien référencés");
			return;
		}
		for(Add add : addsWithoutRefForTitre){
			Title titleNotReferenced = add.getTitle();
			System.out.println("Ce titre n'est pas référencé : "+titleNotReferenced.getTitre());
			System.out.println("C'est le titre l'add n°"+add);
			try{
				readConsoleInput("^OK$", 
						"En attente d'une intervention manuelle ! Mettez à jour la bdd. Tapez OK pour reprendre",
						"Votre réponse", 
						" être OK");
			}catch(HomeException excep){
				System.out.println("Impossible de terminer le traitement en cours");
			}
		}
	}

	public void toSolveMultipleAddMatch(){
		System.out.println("---- Affichage des annonces référencés plus de 2 fois en bdd ----");
		List<Add> addsWithMultipleRef = objectManager.getAddsSaver().getAddsOnLineWithMultipleRef();
		if(addsWithMultipleRef.isEmpty()){
			System.out.println("Toutes les annonces en ligne sont bien référencés une seule fois");
		}
		for(Add add : addsWithMultipleRef){
			System.out.println("Cette annonce est référencée plusieurs fois : "+add
					+ " En effet, plusieurs annonces en base correspondent à ces titres et à ces textes"
					+ "\n     ref_titre : "+add.getTitle().getRefTitre()
					+ "\n     ref_texte : "+add.getTexte().getRefTexte());
			try{
				readConsoleInput("^OK$", 
						"En attente d'une intervention manuelle ! Mettez à jour la bdd. Tapez OK pour reprendre",
						"Votre réponse", 
						" être OK");
			}catch(HomeException excep){
				System.out.println("Impossible de terminer le traitement en cours");
			}
		}
	}

	public void menuGestionDesComptes() throws HomeException{

		boolean continueBoucle = true;
		while (continueBoucle) {
			System.out.println("---------- MENU DE GESTION DES COMPTES -----------");
			System.out.println();
			printCompte(objectManager.getCompteInUse(), true);
			System.out.println("1 : Mettre à jour le pseudo");
			System.out.println("2 : Enregistrer une activité du compte");
			System.out.println("3 : Confirmez la redirection");
			System.out.println("4 : Désactivez ou réactivez un compte");
			System.out.println("5 : Revenir au menu d'acceuil");
			System.out.println();
			String saisie = readConsoleInput("^1|2|3|4|5$",
					"Que voulez vous faire ? ",
					"Votre réponse", "1,2, 3 ou 4");
			// Enregistrement du choix de l'utilisateur dans numéro
			switch (saisie) {
			// si le numéro, on va créer un doodle
			case "1":
				updatePseudo();
				break;
			case "2":
				saveActivity();
				break;
			case "3":
				confirmRedirection();
				break;
			case "4":
				disableOrEnable();
				break;
			case "5":
				throw new HomeException();
			default:
				System.out.println("Erreur de saisie");
				break;
			}
		}
	}

	private void disableOrEnable() throws HomeException{
		String confirmation = readConsoleInput("^activer|desactiver$",
				"Voulez vous activer ou désactiver le compte "+objectManager.getCompteInUse().getMail()+" :",
				"Votre réponse", 
				" être activer ou desactiver");
		CompteLbcDao compteDao = new CompteLbcDao();
		CompteLbc compteInUse = objectManager.getCompteInUse();
		if(confirmation.equals("activer")){
			compteInUse.setDisabled(false);
		}else{
			compteInUse.setDisabled(true);
			compteInUse.setDateOfDisabling(Calendar.getInstance());
		}
		compteDao.updateEnabled(compteInUse);
	}

	private void updatePseudo() throws HomeException{
		String pseudo = readConsoleInput("^\\S{3,}$",
				"Entrez le nouveau pseudo pour le compte "+objectManager.getCompteInUse().getMail()+" :",
				"Votre réponse", " faire plus de 3 caractères");
		CompteLbcDao compteDao = new CompteLbcDao();
		CompteLbc compteInUse = objectManager.getCompteInUse();
		compteInUse.setPseudo(pseudo);
		compteDao.updatePseudo(compteInUse);
	}

	private void saveActivity() throws HomeException{
		String confirmation = readConsoleInput("^oui|non$",
				"Confirmez vous une activité pour le compte "+objectManager.getCompteInUse().getMail()+" :",
				"Votre réponse", 
				" être oui ou non");
		if(confirmation.equals("oui")){
			CompteLbcDao compteDao = new CompteLbcDao();
			CompteLbc compteInUse = objectManager.getCompteInUse();
			compteInUse.setDateDerniereActivite(Calendar.getInstance());
			compteDao.updateDateDerniereActivite(compteInUse);			
		}
	}

	private void confirmRedirection() throws HomeException{
		String confirmation = readConsoleInput("^oui|non$",
				"Confirmez que la redirection est en place pour le compte "+objectManager.getCompteInUse().getMail()+" :",
				"Votre réponse", 
				" être oui ou non");
		CompteLbcDao compteDao = new CompteLbcDao();
		CompteLbc compteInUse = objectManager.getCompteInUse();
		if(confirmation.equals("oui")){
			compteInUse.setRedirection(true);
		}else{
			compteInUse.setRedirection(false);
		}
		compteDao.updateRedirection(compteInUse);

	}


	public void menuSummary() throws HomeException{

		boolean continueBoucle = true;
		while (continueBoucle) {
			System.out.println("---------- RESUME DES ANNONCES -----------");
			System.out.println();
			System.out.println("1 : Afficher le nombre de communes différentes avec des annonces.");
			System.out.println("2 : Revenir au menu d'acceuil");
			System.out.println();
			String saisie = readConsoleInput("^1|2$",
					"Que voulez vous faire ? ",
					"Votre réponse", " être 1 ou 2");
			// Enregistrement du choix de l'utilisateur dans numéro
			switch (saisie) {
			// si le numéro, on va créer un doodle
			case "1":
				printNonUnicityOfCommunes();
				break;
			case "2":
				throw new HomeException();
			default:
				System.out.println("Erreur de saisie");
				break;
			}
		}

	}

	private void printNonUnicityOfCommunes() {
		ResumeDao resumeDao = new ResumeDao();
		List<StatsOnCommune> statsOnCommunes = resumeDao.getRepeatedOnlineCommune();
		int nbCommunesDistinctesRepete = statsOnCommunes.size();
		int nbAddsWithCommuneRepeted=0;
		for(StatsOnCommune statsOnCommune : statsOnCommunes){
			String nomCommune = statsOnCommune.getCommune().getNomCommune();
			int refCommune = statsOnCommune.getCommune().getRefCommune();
			int nbFoisOnline = statsOnCommune.getNbFoisEnLigne();
			System.out.println(nomCommune+" est présente "+nbFoisOnline+" fois en ligne (ref add : "+refCommune+")");
			nbAddsWithCommuneRepeted=nbAddsWithCommuneRepeted+nbFoisOnline;
		}
		System.out.println();
		System.out.println();
		AddDao addDao = new AddDao();
		int nbAddsOnline = addDao.getNumberOfAddsOnline();
		int nbAddsOnlineNotRepeted = nbAddsOnline - nbAddsWithCommuneRepeted;
		System.out.println("Il y a "+nbAddsOnline+" annonces en ligne");
		System.out.println("Parmi ces annonces, "+nbAddsOnlineNotRepeted+" sont présentes dans des communes différentes.");
		System.out.println(" Il y a "+nbCommunesDistinctesRepete+" communes distinces avec plusieurs annonces");
		System.out.println(" Soi un total de : "+nbAddsWithCommuneRepeted+" annonces mentionnant une commune ayant déjà une annonce");
		System.out.println();
	}

	public void menuAddTextesTitre() throws HomeException{
		boolean continueBoucle = true;
		while (continueBoucle) {
			System.out.println("---------- MENU DE GESTION DES TITRES ET DES TEXTES -----------");
			System.out.println();
			System.out.println("1 : Ajouter des titres");
			System.out.println("2 : Ajouter des textes");
			System.out.println();
			String saisie = readConsoleInput("^1|2$",
					"Que voulez vous faire ? ",
					"Votre réponse", " être 1 ou 2");
			// Enregistrement du choix de l'utilisateur dans numéro
			switch (saisie) {
			// si le numéro, on va créer un doodle
			case "1":
				addNewTitreInBdd();
				break;
			case "2":
				addNewTextInBdd();
				break;
			default:
				System.out.println("Erreur de saisie");
				break;
			}
		}

	}


	private void addNewTitreInBdd() {
		// TODO Auto-generated method stub

	}

	private void addNewTextInBdd() throws HomeException{
		System.out.println("------    AJOUT DE TEXTES À LA BDD   ------");
		String choix = readConsoleInput("^generer|ajouter$",
				"Voulez vous générer puis ajouter des textes ou juste ajouter des textes ? ",
				"Votre réponse", " être generer ou ajouter");
		System.out.println("Sélectionnez le fichier xlsx des textes");
		File path = selectFileWithTexte();
		TexteAndTitleManager texteManager = new TexteAndTitleManager();
		List<List<String>> textes=null;
		if(choix.equals("generer")){
			textes = texteManager.getContenuXlsx(path);
			String nbTextesToGenerate = readConsoleInput("^\\d+$",
					"Saisir le nombre de textes à générer",
					"Votre réponse", " être un entier");
			String confirmation="non";
			do{
				textes = texteManager.generateTextes(textes, Integer.parseInt(nbTextesToGenerate));
			confirmation = readConsoleInput("^oui|non$",
					"Est ce que la génération dex textes vous convient ?",
					"Votre réponse", " être oui ou non");
			}while(confirmation.equals("non"));
		}else if(choix.equals("ajouter")){
			textes = texteManager.getContenuXlsx(path);
		}	
		String typeTexte = readConsoleInput("^\\S{3,}$",
				"Saisir le type de texte à ajouter",
				"Votre réponse", " faire au moins 3 caractères sans espace");
		try{
			TypeTexte.valueOf(typeTexte);
		}catch(Exception exec){
			System.out.println("Ajouter le type texte à la classe TypeTexte");
			throw new HomeException();
		}
		texteManager.saveTexteFromXlsx(textes, typeTexte);
		System.out.println("Textes bien enregistrés dans la bdd");
	}

}








