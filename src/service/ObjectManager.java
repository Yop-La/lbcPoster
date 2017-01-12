package service;


import java.util.List;
import java.util.ListIterator;

import exception.AgentLbcFailPublicationException;
import fr.doodle.dao.CommuneDao;
import fr.doodle.dao.CompteLbcDao;
import scraper.Add;
import scraper.AddsGenerator;
import scraper.AgentLbc;
import scraper.Commune;
import scraper.CompteLbc;
import scraper.CritereSelectionTitre;
import scraper.CriteresSelectionTexte;
import scraper.CriteresSelectionVille;
import scraper.PathToAdds;
import scraper.ResultsControl;
import scraper.Source;
import scraper.Texte;
import scraper.Title;
import scraper.TypeTexte;
import scraper.TypeTitle;

public class ObjectManager {

	private int nbAddsToPublish;
	private int nbAddsPublie;

	private AgentLbc agentLbc;
	private AddsGenerator addsGenerator;
	private AddsSaver addsSaver;

	private List<CompteLbc> comptes;
	private CompteLbc compteInUse;

	private Source titleSourceType;
	private Source texteSourceType;
	private Source communeSourceType;

	private List<Title> titleSource;
	private List<Texte> texteSource;
	private List<Commune> communeSource;

	private CritereSelectionTitre critSelectTitre;
	private CriteresSelectionVille critSelectVille;
	private CriteresSelectionTexte critSelectTexte;

	private PathToAdds pathToAdds; 

	private List<Add> addsFromLbc;
	ListIterator<Add> itOnAddReadyTosave;
	private boolean preparationOk;
	private boolean savingOk; 
	private boolean saveAddToSubmitLbcInBase;
	// DAO
	private CommuneDao comDao = new CommuneDao();
	// résultats contrôle
	private ResultsControl results;
	
	public ResultsControl getResults() {
		return results;
	}

	public List<Texte> getTextePasDansLabdd(){
		return(addsSaver.getTextesPasDansLaBdd());
	}

	public void saveCodePostalAndNomCommuneNoCorrep(String idCommuneCorrespo) {
		int idCommuneCorresp = Integer.parseInt(idCommuneCorrespo);
		Commune commune = itOnAddReadyTosave.previous().getCommune();
		Commune communeCorresp = comDao.findOne(idCommuneCorresp);
		/* on met à jour le code postal */
		communeCorresp.setCodePostal(commune.getCodePostal());
		comDao.updateCodePostal(communeCorresp);
		/* on met à jour le nom */
		communeCorresp.setNomCommuneInBase(commune.getNomCommuneOnLbc());
		comDao.updateNomCommune(communeCorresp);
		itOnAddReadyTosave.next();
	}

	public void saveCodePostal(){
		Commune commune = itOnAddReadyTosave.previous().getCommune();
		if(commune.getCodePostal() ==null){
			System.out.println("dedans");
			comDao.updateCodePostal(commune);
		}
		itOnAddReadyTosave.next();
	}

	public List<Commune> search(String nameCommuneInBdd){
		List<Commune> communes = comDao.findAll(nameCommuneInBdd);
		return communes;
	}

	public void lancerControlCompte() {
		// TODO Auto-generated method stub

		agentLbc.setUp();
		agentLbc.connect();
		this.addsFromLbc = agentLbc.controlCompte(); // pour récupérer les annonces controlés
		addsSaver = new AddsSaver(addsFromLbc, compteInUse); // pour faire le liene entres les annonces Lbc et la bdd (mettre à jour les ref)
		preparationOk = addsSaver.prepareAddsToSaving(compteInUse);
		itOnAddReadyTosave = addsFromLbc.listIterator();
		if(preparationOk){
			savingOk = addsSaver.saveAnnonceFromLbcInBdd();
			if(savingOk){
				results = addsSaver.getResults();
			}
		}
	}
	
	// pour itérer sur les communes des adds récupéres du bon coin et prête à être sauvegarder
	public Title nextTitleReadyTosave(){
		return itOnAddReadyTosave.next().getTitle();
	}
	
	public Title previousTitleReadyTosave() {
		return itOnAddReadyTosave.previous().getTitle();
	}
	
	public Commune nextCommuneReadyTosave(){
		return itOnAddReadyTosave.next().getCommune();
	}

	public boolean hasNextAddReadyTosave(){	
		boolean retour = itOnAddReadyTosave.hasNext();
		if(!retour){
			itOnAddReadyTosave = addsFromLbc.listIterator();
		}
		return retour;
	}

	public void lancerPublication() {
		// génération des annonces
		addsGenerator.setImage();
		addsGenerator.generateAdds();
		agentLbc.setAddsToPublish(addsGenerator.getaddsProduced());
		agentLbc.setUp();
		agentLbc.connect();
		agentLbc.goToFormDepot();
		try{
			agentLbc.publish();
			this.nbAddsPublie = this.nbAddsToPublish;
		}catch(AgentLbcFailPublicationException excep){
			this.nbAddsPublie = excep.getIndiceAnnonceDechec()-1;
		}
		
	}

	public void setcommunes() {
		addsGenerator.setCommuneSource();
		communeSource = addsGenerator.getCommuneSource();
	}

	public CriteresSelectionVille getCritSelectVille() {
		return critSelectVille;
	}

	public void setCritSelectVille(int borneInf, int bornSup) {
		this.critSelectVille = new CriteresSelectionVille();
		critSelectVille.setBornInfPop(borneInf);
		critSelectVille.setBornSupPop(bornSup);
		addsGenerator.setCritSelectVille(this.critSelectVille);
	}


	public void createAddsGenerator(){
		addsGenerator = new AddsGenerator(nbAddsToPublish, this.compteInUse);
		addsGenerator.saveTexteXlsxInBdd();
	}

	public void setTextes() {
		addsGenerator.setTexteSource();
		texteSource = addsGenerator.getTexteSource();
	}

	public void setTitres(){
		addsGenerator.setTitleSource();
		titleSource = addsGenerator.getTitleSource();
	}

	// pour récupérer tous les comptes
	public void setComptes(){
		CompteLbcDao compteDao = new CompteLbcDao();
		comptes = compteDao.findAll(); 
	}

	public void setCompte(int identifiant){
		for(CompteLbc compte : comptes){
			if(compte.getRefCompte() == identifiant){
				compteInUse = compte;
				return;
			}
		}
	}




	public void setPathToAdds(String pathToAdds) {
		this.pathToAdds = PathToAdds.valueOf(pathToAdds);
		addsGenerator.setPathToAddsDirectory(this.pathToAdds.getPath());
	}

	public int getNbAddsToPublish() {
		return nbAddsToPublish;
	}

	public void setNbAddsToPublish(int nbAddsToPublish) {
		this.nbAddsToPublish = nbAddsToPublish;
	}

	public void createAgentLbc(int nbAddsToPublish){
		agentLbc = new AgentLbc(compteInUse, nbAddsToPublish, saveAddToSubmitLbcInBase);
		setNbAddsToPublish(nbAddsToPublish);
	}

	public void createAgentLbc(){
		agentLbc = new AgentLbc(compteInUse);
	}

	public CompteLbc getCompteInUse() {
		return compteInUse;
	}

	public List<CompteLbc> getComptes() {
		return comptes;
	}

	public Source getTitleSourceType() {
		return titleSourceType;
	}

	public void setTitleSourceType(String titleSourceType) {
		this.titleSourceType = Source.valueOf(titleSourceType);
		addsGenerator.setTypeSourceTitles(this.titleSourceType);
	}

	public Source getTexteSourceType() {
		return texteSourceType;
	}

	public void setTexteSourceType(String texteSourceType) {
		this.texteSourceType = Source.valueOf(texteSourceType);
		addsGenerator.setTypeSourceTextes(this.texteSourceType);
	}

	public Source getCommuneSourceType() {
		return communeSourceType;
	}

	public void setCommuneSourceType(String communeSourceType) {
		this.communeSourceType = Source.valueOf(communeSourceType);
		addsGenerator.setTypeSourceCommunes(this.communeSourceType);
	}

	public AgentLbc getAgentLbc() {
		return agentLbc;
	}

	public void setAgentLbc(AgentLbc agentLbc) {
		this.agentLbc = agentLbc;
	}

	public AddsGenerator getAddsGenerator() {
		return addsGenerator;
	}

	public void setAddsGenerator(AddsGenerator addsGenerator) {
		this.addsGenerator = addsGenerator;
	}

	public List<Title> getTitleSource() {
		return titleSource;
	}

	public void setTitleSource(List<Title> titleSource) {
		this.titleSource = titleSource;
	}

	public List<Texte> getTexteSource() {
		return texteSource;
	}

	public void setTexteSource(List<Texte> texteSource) {
		this.texteSource = texteSource;
	}

	public List<Commune> getCommuneSource() {
		return communeSource;
	}

	public void setCommuneSource(List<Commune> communeSource) {
		this.communeSource = communeSource;
	}

	public CritereSelectionTitre getCritSelectTitre() {
		return critSelectTitre;
	}

	public void setCritSelectTitre(String typeTitle) {
		this.critSelectTitre = new CritereSelectionTitre(TypeTitle.valueOf(typeTitle));
		addsGenerator.setCritSelectTitre(this.critSelectTitre);
	}
	
	public void setCritSelectTexte(String typeTexte) {
		this.critSelectTexte = new CriteresSelectionTexte(TypeTexte.valueOf(typeTexte));
		addsGenerator.setCritSelectTexte(this.critSelectTexte);
	}

	public PathToAdds getPathToAdds() {
		return pathToAdds;
	}

	public void setPathToAdds(PathToAdds pathToAdds) {
		this.pathToAdds = pathToAdds;
	}


	public void setComptes(List<CompteLbc> comptes) {
		this.comptes = comptes;
	}

	public void setCompteInUse(CompteLbc compteInUse) {
		this.compteInUse = compteInUse;
	}

	public void setTitleSourceType(Source titleSourceType) {
		this.titleSourceType = titleSourceType;
	}

	public void setTexteSourceType(Source texteSourceType) {
		this.texteSourceType = texteSourceType;
	}

	public void setCommuneSourceType(Source communeSourceType) {
		this.communeSourceType = communeSourceType;
	}

	public boolean isPreparationOk() {
		return preparationOk;
	}

	public void setPreparationOk(boolean preparationOk) {
		this.preparationOk = preparationOk;
	}

	public boolean isSavingOk() {
		return savingOk;
	}

	public void setSavingOk(boolean savingOk) {
		this.savingOk = savingOk;
	}

	public boolean isSaveAddToSubmitLbcInBase() {
		return saveAddToSubmitLbcInBase;
	}

	public void setSaveAddToSubmitLbcInBase(boolean saveAddToSubmitLbcInBase) {
		this.saveAddToSubmitLbcInBase = saveAddToSubmitLbcInBase;
	}

	public int getNbAddsPublie() {
		return nbAddsPublie;
	}

	public void setNbAddsPublie(int nbAddsPublie) {
		this.nbAddsPublie = nbAddsPublie;
	}
	
	


















}
