package service;

import java.util.ArrayList;
import java.util.List;

import dao.AddDao;
import dao.CommuneDao;
import dao.TexteDao;
import dao.TitreDao;
import scraper.Add;
import scraper.CommuneLink;
import scraper.CompteLbc;
import scraper.EtatAdd;
import scraper.ResultsControl;
import scraper.Texte;
import scraper.Title;

public class AddsSaver {
	private List<Add> addsToControle;
	private List<Add> addsReadyToSave = new ArrayList<Add>();
	private TitreDao titreDao;
	private TexteDao texteDao;
	private AddDao addDao;
	private CompteLbc compteLbc;
	private List<Add> addsRefused;
	private List<Add> addsNotOnlineAnymore;
	private ResultsControl results;
	private List<Add> newAddsOnline = new ArrayList<Add>();
	private List<Add> addsStillOnline = new ArrayList<Add>();

	// pour contrôler les adds online sur lbc
	private List<Add> addsOnLineWithTexteAndTitleInBdd = new ArrayList<Add>();
	//private List<Add> addsOnLineWithoutRefForCommune = new ArrayList<Add>();
	private List<Add> addsOnLineWithoutRefForTitre = new ArrayList<Add>();
	private List<Add> addsOnLineWithoutRefForTexte = new ArrayList<Add>();
	private List<Add> addsOnLineWithMultipleRef = new ArrayList<Add>();
	private List<Add> addsOnLineNotReferenced = new ArrayList<Add>();
	private List<Add> addsOnLineWithOneRef = new ArrayList<Add>();
	private List<Add> addsUpdated = new ArrayList<Add>();

	// utiliser pour sauvegarder les annonces étant en ligne sur lbc
	public AddsSaver(List<Add> addsControled, CompteLbc compteLbc) {
		super();
		this.addsToControle = addsControled;
		titreDao = new TitreDao();
		texteDao = new TexteDao();
		this.compteLbc = compteLbc;

	}



	public void setSubmitCommuneAndRefAddForAddsOnlineReferenced(){
		AddDao addDao = new AddDao();
		for(Add addReferencedOnce : addsOnLineWithOneRef){
			addReferencedOnce = addDao.setSubmitCommuneAndRefAdd(addReferencedOnce);
			addsReadyToSave.add(addReferencedOnce);
		}
	}


	public void saveAddsOnlineNotReferenced(){
		for(Add addOnLineNotRerenced : addsOnLineNotReferenced){
			AddDao addDao = new AddDao();
			addOnLineNotRerenced= addDao.saveAddsNotReferenced(addOnLineNotRerenced);
			addsReadyToSave.add(addOnLineNotRerenced);
		}
	}

	public boolean isReadyToSave(){
		return(addsReadyToSave.size() == addsToControle.size());
	}

	// en entrée : addsOnLineWithTexteAndTitleInBdd
	// c'est la liste des adds online
	// 1°) certaines sont référencées et vont matchés à la bdd grâce au titre et au texte
	// 2°) d'autres sont  pas référencées car pas d'enregistrement au moment du dépôt
	// pour les premières, on va pouvoir retrouver la commune soumise dans la méthode ci dessous 
	// pour les secondes, on pourra pas retrouver la commune soumise. On va enregistrer ces annonces ci dessous
	public void classifyAddsOnlineWithTitleAndTextReferenced(){
		addsOnLineWithMultipleRef = new ArrayList<Add>();
		addsOnLineNotReferenced = new ArrayList<Add>();
		addsOnLineWithOneRef = new ArrayList<Add>();
		addDao = new AddDao();
		// pour mettre à jour les annonces encore en ligne et insérer les nouvelles annonces en ligne
		for(Add addOnLine : addsOnLineWithTexteAndTitleInBdd){
			int numberOfMatch = addDao.countNumberOfRef(addOnLine);
			switch (numberOfMatch) {
			case 0:
				addsOnLineNotReferenced.add(addOnLine);
				break;
			case 1:
				addsOnLineWithOneRef.add(addOnLine);
				break;
			default:
				addsOnLineWithMultipleRef.add(addOnLine);
				break;
			}
		}
	}



	public void updateAddsFromLbc() {
		addDao = new AddDao();
		// pour mettre à jour les annonces encore en ligne et insérer les nouvelles annonces en ligne
		for(Add addReferencedOnce : addsReadyToSave){
			CommuneLink communeLink = addReferencedOnce.getCommuneLink();
			AddDao addDoa = new AddDao();
			// si annonce pas enregistré au moment de la publication
			if(communeLink.submit == null){
				addReferencedOnce.setEtat(EtatAdd.onLine);
				addDao.update(addReferencedOnce);
				newAddsOnline.add(addReferencedOnce);
			}else{// sinon on la met à jour
				addReferencedOnce.setNbControle(addReferencedOnce.getNbControle()+1);
				addReferencedOnce.setEtat(EtatAdd.onLine);
				addDao.update(addReferencedOnce);
				addsStillOnline.add(addReferencedOnce);
			}
			addsUpdated.add(addReferencedOnce);
		}
		// mettre à jour les annonces qui ont été refusés par la modération
		addsRefused = addDao.findAddsWithHerState(new Add(EtatAdd.enAttenteModeration, compteLbc));
		for(Add addRefused :  addsRefused){
			addRefused.setEtat(EtatAdd.refused);
			addDao.updateStateAndNbControlAndDateControl(addRefused);
		}
		// mettre à jour les annonces qui ont périmés et ont été supprimés après mise en ligne
		addsNotOnlineAnymore = addDao.findAddsNotOnlineAnymore(new Add(compteLbc));
		for(Add addNotOnlineAnymore :  addsNotOnlineAnymore){
			addNotOnlineAnymore.setEtat(EtatAdd.notOnLineAnymore);
			addDao.updateStateAndNbControlAndDateControl(addNotOnlineAnymore);
		}
	}

	public List<Add> getAddsControled() {
		return addsToControle;
	}

	public void setAddsControled(List<Add> addsControled) {
		this.addsToControle = addsControled;
	}

	public boolean isTexteAndTitleOnlineReferenced(){
		addsOnLineWithTexteAndTitleInBdd = new ArrayList<Add>();
		// pas de contrôle sur les communes car on est pas sûr qu'elle soit référencé en base
		// et car un contrôle sur les textes et les titres suffit pour faire
		// correspondre les annonnces en ligne aux annonces en bdd
		//addsOnLineWithoutRefForCommune = new ArrayList<Add>();
		addsOnLineWithoutRefForTitre = new ArrayList<Add>();
		addsOnLineWithoutRefForTexte = new ArrayList<Add>();
		// repère et isole les annonces dont le titre ou le texte ne trouve pas de correspondance en bdd
		// et lie les titres et les textes des adds en ligne à la bdd 
		for(Add add : addsToControle){
			boolean titleReferenced = false;
			//boolean communeReferenced = false;
			boolean texteReferenced = false;
			add.setCompteLbc(this.compteLbc);

			/*
			// lier les communes du bon coin en ligne à la bdd
			Commune communeFrBdd = communeDao.findOneWithNomCommuneOnLbc(add.getCommuneLink().onLine);
			if(communeFrBdd!=null){
				communeReferenced = true;
			}*/

			// lier les titres du bon coin en ligne à la bdd
			Title titreFrBdd = titreDao.findOneWithTitre(add.getTitle());
			if(titreFrBdd!=null){
				titleReferenced=true;
			}

			// lier les textes du bon coin en ligne à la bdd
			List<Texte> textesCorrespondant;
			Texte texteFrBdd=null;
			int nbRecherche = 0;
			boolean continueRecherche = true;
			try{
				do{
					nbRecherche=1;
					Texte texteLbc = add.getTexte();
					textesCorrespondant = texteDao.findWithCorpsTexte(texteLbc);
					int levelCorresp = texteLbc.getLevelCorrespondance();
					int nbCorrespon = textesCorrespondant.size();
					if(nbCorrespon==0){
						texteLbc.setLevelCorrespondance(levelCorresp-1);
						System.out.println("Pas de correspondance de texte à la recherche n°"+nbRecherche);
					}else if(nbCorrespon>=2){
						texteFrBdd = textesCorrespondant.get(0);
						// on parcourt les textes de la bbd pour prendre celui avec la plus petite distance de Levenshtein
						for(Texte texteCorresp : textesCorrespondant){
							if(texteCorresp.getLevenshteinDistanceBetweenLbcAndBdd()<=texteFrBdd.getLevenshteinDistanceBetweenLbcAndBdd()){
								texteFrBdd=texteCorresp;
							}
						}
						System.out.println("Plus de 2 correspondance de texte à la recherche n°"+nbRecherche);
						continueRecherche = false;
					}else{
						texteFrBdd = textesCorrespondant.get(0);
						continueRecherche = false;
					}
					nbRecherche++;
				}while(continueRecherche);
			}catch(Exception exec){
				
			}
			if(texteFrBdd.getLevenshteinDistanceBetweenLbcAndBdd()<=30){
				texteReferenced=true;
			}
			if(texteReferenced & titleReferenced){
				//add.getCommuneLink().onLine.setRefCommune(communeFrBdd.getRefCommune());
				add.getTitle().setRefTitre(titreFrBdd.getRefTitre());
				add.getTexte().setRefTexte(texteFrBdd.getRefTexte());
				addsOnLineWithTexteAndTitleInBdd.add(add);
			}
			if(!texteReferenced){
				addsOnLineWithoutRefForTexte.add(add);
			}
			if(!titleReferenced){
				addsOnLineWithoutRefForTitre.add(add);
			}
			/*if(!communeReferenced){
				addsOnLineWithoutRefForCommune.add(add);
			}*/
		}
		if(addsOnLineWithTexteAndTitleInBdd.size()==addsToControle.size()){
			return true;// on est sûr que chaque élément (sauf les communes 
			// car pour gérer le problème de correspondance des communes
			// il faut que les adds en ligne soit lié à la base
		}else{
			return false;
		}
	}

	public List<Add> getAddsRefused() {
		return addsRefused;
	}

	public void setAddsRefused(List<Add> addsRefused) {
		this.addsRefused = addsRefused;
	}

	public List<Add> getAddsNotOnlineAnymore() {
		return addsNotOnlineAnymore;
	}

	public void setAddsNotOnlineAnymore(List<Add> addsNotOnlineAnymore) {
		this.addsNotOnlineAnymore = addsNotOnlineAnymore;
	}

	public ResultsControl getResults() {
		results = new ResultsControl(addsRefused.size(), addsNotOnlineAnymore.size(), newAddsOnline.size(), addsStillOnline.size());
		return results;
	}

	public List<Add> getAddsReadyTosave() {
		return addsReadyToSave;
	}

	public void setAddsReadyTosave(List<Add> addsReadyTosave) {
		this.addsReadyToSave = addsReadyTosave;
	}

	/*
	public List<Add> getAddsOnLineWithoutRefForCommune() {
		return addsOnLineWithoutRefForCommune;
	}*/

	public List<Add> getAddsOnLineWithoutRefForTitre() {
		return addsOnLineWithoutRefForTitre;
	}

	public List<Add> getAddsOnLineWithoutRefForTexte() {
		return addsOnLineWithoutRefForTexte;
	}

	public List<Add> getAddsOnLineWithMultipleRef() {
		return addsOnLineWithMultipleRef;
	}

	public List<Add> getAddsStillOnline() {
		return addsStillOnline;
	}

	public boolean hasAddsWithMultipleReferenced() {
		return(this.addsOnLineWithMultipleRef.size()!=0);
	}

	public List<Add> getAddsUpdated() {
		return addsUpdated;
	}










}
