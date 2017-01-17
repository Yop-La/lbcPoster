package service;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import exception.HomeException;
import fr.doodle.dao.CommuneDao;
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
import scraper.Texte;
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
		System.out.println("Nb d'annonces de nouvelles annonces qui était pas dans la bdd (pas ref pendant la publication) : "+results.getNbNewAddsOnline());
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
	public void gererCorrepondanceCommunes(List<Add> addsWithCommuneOnlineNotReferenced) {
		// pour lier la commune en ligne à la bdd (pas de mise à jour du code postale sauf pour linsert)
		int indiceAdd=1;
		for(Add addWithCommuneOnlineNotReferenced : addsWithCommuneOnlineNotReferenced){
			System.out.println();
			System.out.println("Commune de l'add n°"+indiceAdd+" de ref n° "+addWithCommuneOnlineNotReferenced);
			System.out.println();
			CaseOfMatching caseOfMAtch = toReferencesOnlineCommune(addWithCommuneOnlineNotReferenced);
			CommuneLink communeLink = addWithCommuneOnlineNotReferenced.getCommuneLink();
			// mise à jour code postal de submit et de online
			miseAJourDesCodesPostaux(caseOfMAtch, communeLink);
			System.out.println("Comparaison des communes après mise à jour des codes postaux ");
			communeLink.printComparaison();
			System.out.println();
			System.out.println();
			indiceAdd++;
		}
	}

	// le para passé peut être la liste des communes avant modération construite lors de la publication
	// ou la liste des annonces non référencés sans commune référencés lors du controle des annonces
	//ou la liste des annonces sauvegardés lors du controle des annonces
	public CaseOfMatching toReferencesOnlineCommune(Add addWithCommuneOnlineNotReferenced){
		CommuneLink communeLink = addWithCommuneOnlineNotReferenced.getCommuneLink();
		CaseOfMatching caseOfMAtch = communeLink.getCaseOfMatch();
		OperationToSolveMatching opToSolveMatching=null;
		System.out.println("Comparaison des communes avant gestion des correspondances");
		communeLink.printComparaison();
		switch (caseOfMAtch){
		case perfectMatch:
			printCasePerfectMatch();
			break;
		case sameNameOneMatch:
			printCaseSameNameOneMatch(communeLink);
			break;
		case differentNameOneMatch:
			printCaseDifferentNameOneMatch(communeLink);
			break;
		case sameNameSeveralMatch:
			printCaseSameNameSeveralMatch(communeLink);
			opToSolveMatching=findTheOperationToResolveMatching(communeLink);
			break;
		case differentNameSeveralMatch:
			printCaseDifferentNameSeveralMatch(communeLink);
			opToSolveMatching=findTheOperationToResolveMatching(communeLink);
			break;
		case differentNameNoMatch:
			printCaseDifferentNameNoMatch(communeLink);
			opToSolveMatching=findTheOperationToResolveMatching(communeLink);
			break;
		case unknowCase:
			System.out.println("Normalement impossible d'arriver là");
			break;
		}
		// pour vérifier que tout est cohérent ( si submit a un code postal alors il doit y avoir un perfect match)
		if(!caseOfMAtch.equals(CaseOfMatching.perfectMatch)){
			if((!communeLink.submit.getCodePostal().equals(""))){
				System.out.println("Problème ... car submit a un code postal mais pas de perfect match ...");
				System.out.println("Il y a une incohérence entre le nom et le code postal de commune soumise et la base ...");
			}	
		}
		// bloc des opérations à faire pour faire trouver la correspondance de la commune online
		// on en profite aussi pour mettre à jour le code postal de la commune online
		if(opToSolveMatching!=null){
			Commune futurCommuneOnLine=null;
			if(opToSolveMatching.equals(OperationToSolveMatching.addCommuneOnlineToBdd)){
				futurCommuneOnLine = saisirCommunesToInsert();
			}
			if(opToSolveMatching.equals(OperationToSolveMatching.linkCommuneOnLineToAnotherCommuneInBase)){
				int refCommuneSelected = selectAcommuneFromTheResults();
				futurCommuneOnLine = new Commune();
				futurCommuneOnLine.setRefCommune(refCommuneSelected);
			}
			communeLink.makeOperationToSolveMatching(opToSolveMatching,futurCommuneOnLine);
		}else{// on arrive ici si perfectMatch, differentNameOneMatch, sameNameOneMatch
			// perfectMatch et sameNameOneMacth vont se gérer de la même manière
			// dans ce cas la commune online et la même que la comune submit
			switch (caseOfMAtch){
			case perfectMatch:
				communeLink.onLine=communeLink.submit;
				break;
			case sameNameOneMatch:
				communeLink.codePostalGoToSubmit();
				communeLink.onLine=communeLink.submit;
				break;
			case differentNameOneMatch:
				String codePostalOnline = communeLink.onLine.getCodePostal();
				CommuneDao comDao = new CommuneDao();
				communeLink.onLine = comDao.findOneWithNomCommune(communeLink.onLine);
				communeLink.onLine.setCodePostal(codePostalOnline);
				break;
			default:
				System.out.println("Impossible d'arriver là !!!!");
			}

		}
		System.out.println("Comparaison des communes après gestion des correspondances");
		communeLink.printComparaison();
		return caseOfMAtch;
	}	



	private void miseAJourDesCodesPostaux(CaseOfMatching caseOfMAtch, 
			CommuneLink communeLink) {
		if(communeLink.submit != null){
			if(caseOfMAtch.equals(CaseOfMatching.perfectMatch)){

				if(communeLink.submit.getCodePostal().equals("")){
					System.out.println("Problème ... car submit n'a pas de code postal mais perfect match ...");
				}


			}
		}

		if(communeLink.onLine.getCodePostal().equals("")){
			System.out.println("Mise à jour du code postal de la commune en ligne");
			communeLink.onLine.setCodePostal(saisirCodePostal());
			communeLink.onLine.updateCodePostal();

		}
		// peut être null quand on veut référencer les communes non référencéss
		// des adds non référencés après contrôles des annonces
		if(caseOfMAtch.equals(CaseOfMatching.sameNameOneMatch)){
			System.out.println("Mise à jour du code postal de la commune soumise");
			communeLink.submit.updateCodePostal();
		}
		if(caseOfMAtch.equals(CaseOfMatching.differentNameOneMatch)){
			System.out.println("Mise à jour du code postal de la commune online");
			communeLink.onLine.updateCodePostal();
		}

		if(communeLink.submit!=null){
			if(communeLink.submit.getCodePostal().equals("")){
				System.out.println("Mise à jour du code postal de la commune soumise");
				communeLink.submit.setCodePostal(saisirCodePostal());
				communeLink.submit.updateCodePostal();
			}
		}

	}

	private void printCaseDifferentNameNoMatch(CommuneLink communeLink) {
		System.out.println("Commune soumise et en ligne ont des noms différents");
		System.out.println("De plus, aucunes commune en base correspondante à celle en ligne");
		System.out.println("À vous de trouver la commune en bdd correspondante ou de l'ajouter");
		
	}

	private void printCaseDifferentNameSeveralMatch(CommuneLink communeLink) {
		System.out.println("Commune soumise et en ligne ont des noms différents");
		System.out.println("De plus, plusieurs noms de commune en base correspondante à celle en ligne");
		System.out.println("À vous de choisir la correspondante");

	}

	private void printCaseSameNameSeveralMatch(CommuneLink communeLink) {
		System.out.println("Commune soumise et en ligne ont le même nom");
		System.out.println("De plus, plusieurs noms de commune en base correspondante à celle en ligne");
		System.out.println("À vous de choisir la correspondante");
	}

	private void printCasePerfectMatch() { 
		System.out.println(" C'est un perfect match !");
		System.out.println(" Le nom de la commune et le code postal sont identiques");
		System.out.println(" Il n'y a donc pas de problème de correspondance");
	}

	private void printCaseDifferentNameOneMatch(CommuneLink communeLink) {
		System.out.println("Commune soumise et en ligne ont des noms différents");
		System.out.println("De plus, un seul nom de commune en base correspondante à celle en ligne");
		System.out.println(" Problème de correspondance qui va être résolu automatiquement");

	}

	private void printCaseSameNameOneMatch(CommuneLink communeLink) {
		System.out.println("Commune soumise et en ligne ont le même non");
		System.out.println("De plus, une unique commune en base correspondante à celle en ligne");
		System.out.println(" Il n'y a donc pas de problème de correspondance");

	}

	private OperationToSolveMatching findTheOperationToResolveMatching(CommuneLink communeLink) {
		String choixOpe;
		do{
			rechercheCommuneInbdd();
			choixOpe = choixPourFaireLaCorrespondance();
		}while(choixOpe.equals("recherche"));
		return OperationToSolveMatching.valueOf(choixOpe);

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

	private void rechercheCommuneInbdd() {
		try{
			String elementsRequete = readConsoleInput("^((\\S*)|(0)),((\\d*)|(0)),((\\d*)|(0))$", 
					"Entrez votre requête pour rechercher la commune correspondante à celle du bon coin dans la base"
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
		}catch(HomeException excep){
			System.out.println("Terminer le traitement en cours avant de revenir au menu");
			rechercheCommuneInbdd();
		}

	}



	public void printCompte() {
		int i = 0;
		for(CompteLbc compteLbc : objectManager.getComptes()){
			i++;
			System.out.println("Compte n°"+i+" : "+compteLbc.getMail()+" - password : "+compteLbc.getPassword());
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

	public void toSolveAddsNotReferencedWithCommuneNotReferenced() {
		System.out.println("---- Affichage des annonces non référencés ayant des communes sans référence ----");
		List<Add> adds = objectManager.getAddsSaver().getAddsNotReferencedWithCommuneNotReferenced();

		for(Add add : adds){
			CommuneLink communeLink =add.getCommuneLink();
			communeLink.printCommuneOnLine();


		}

	}


}





