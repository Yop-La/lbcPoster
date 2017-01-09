package service;


import java.util.List;

import fr.doodle.dao.CompteLbcDao;
import scraper.AddsGenerator;
import scraper.AgentLbc;
import scraper.Commune;
import scraper.CompteLbc;
import scraper.CritereSelectionTitre;
import scraper.CriteresSelectionVille;
import scraper.PathToAdds;
import scraper.Source;
import scraper.Texte;
import scraper.Title;

public class ObjectManager {

	private int nbAddsToPublish;

	private AgentLbc agentLbc;
	private AddsGenerator addsGenerator;

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


	private PathToAdds pathToAdds; 

	private List<Title> titres;

	public void lancerControlCompte() {
		// TODO Auto-generated method stub
		
		agentLbc.setUp();
		agentLbc.connect();
		agentLbc.controlCompte();
	}

	public void lancerPublication() {
		// génération des annonces
		addsGenerator.setImage();
		addsGenerator.generateAdds();
		agentLbc.setAddsToPublish(addsGenerator.getaddsProduced());
		agentLbc.setUp();
		agentLbc.connect();
		agentLbc.goToFormDepot();
		agentLbc.publish();
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
		addsGenerator = new AddsGenerator(nbAddsToPublish);
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
			if(compte.getIdAdmin() == identifiant){
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
		agentLbc = new AgentLbc(compteInUse, nbAddsToPublish);
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

	public void setCritSelectTitre(CritereSelectionTitre critSelectTitre) {
		this.critSelectTitre = critSelectTitre;
	}

	public PathToAdds getPathToAdds() {
		return pathToAdds;
	}

	public void setPathToAdds(PathToAdds pathToAdds) {
		this.pathToAdds = pathToAdds;
	}

	public List<Title> getTitres() {
		return titres;
	}

	public void setTitres(List<Title> titres) {
		this.titres = titres;
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















}
