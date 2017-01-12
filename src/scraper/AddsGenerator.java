package scraper;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;

import csv.CSVReaderBis;
import csv.XlstoCSV;
import fr.doodle.dao.CommuneDao;
import fr.doodle.dao.TexteDao;
import fr.doodle.dao.TitreDao;

public class AddsGenerator {

	private String pathToAddsDirectory;
	private int nbAddsToProduce;

	private int nbTitles;
	private int nbTextes;
	private int nbImages;
	private int nbTowns;

	private List<Add> addsProduced;

	private List<Title> titleSource;
	private List<Texte> texteSource;
	private List<Commune> communeSource;
	private File[] imageSource;

	private Source typeSourceTitles;
	private Source typeSourceTextes;
	private Source typeSourceCommunes;

	CritereSelectionTitre critSelectTitre;
	CriteresSelectionVille critSelectVille;
	CriteresSelectionTexte critSelectTexte;

	CompteLbc compteLbc;

	// construteur
	public AddsGenerator(int nbAddsToProduce, CompteLbc compteLbc){
		this.nbAddsToProduce = nbAddsToProduce;
		this.addsProduced = new ArrayList<Add>();
		this.compteLbc = compteLbc;
	}


	public List<Add> getaddsProduced(){
		return addsProduced;
	}

	// pour généner les annonces
	public void generateAdds() {
		for(int i=0;i<nbAddsToProduce;i++){
			int indiceImage = 0, indiceTexte = 0, indiceTitle = 0, indiceTown = 0;
			if(i!=0){
				indiceTitle = i%nbTitles;
				indiceTexte = i%nbTextes;
				indiceTown = i%nbTowns;
				indiceImage = i%nbImages;
			}
			addsProduced.add(new Add(titleSource.get(indiceTitle), 
					texteSource.get(indiceTexte), 
					communeSource.get(indiceTown), 
					imageSource[indiceImage]));
		}
	}

	public void setCommuneSource() {
		switch (typeSourceCommunes) {
		case SQL:
			setCommuneFromSql();
			break;
		case XLSX:
			setCommuneFromXlsx();
			break;
		}
		setNbTowns();

	}

	private void setNbTowns() {
		nbTowns = communeSource.size();
	}

	public void setCommuneFromXlsx() {
		try{
			List<String[]> nomsCommunes = new ArrayList<String[]>();
			File inputFile = new File(pathToAddsDirectory+"towns.xlsx");
			File outputFile = new File(pathToAddsDirectory+"towns.csv");
			XlstoCSV.xls(inputFile, outputFile);
			CSVReader reader = new CSVReaderBis(new FileReader(pathToAddsDirectory+"towns.csv"));
			Iterator<String[]> it = reader.iterator();
			while(it.hasNext()){
				String[] line = it.next();
				if(!line[0].equals("")){
					nomsCommunes.add(line);
				}
			}

			reader.close();

			this.communeSource= new ArrayList<Commune>(); 
			for(String[] nomCommune : nomsCommunes){
				communeSource.add(new Commune(nomCommune[0]));
			}

		}catch(Exception exception){
			System.out.println("Problème avec le fichier titre");
			exception.printStackTrace();
		}

	}

	private void setCommuneFromSql() {
		CommuneDao communeDao = new CommuneDao();
		communeSource = communeDao.findWithSelection(this.critSelectVille);
	}

	public void setTexteSource() {
		switch (typeSourceTextes) {
		case SQL:
			setTexteFromSql();
			break;
		case XLSX:
			setTexteFromXlsx();
			break;
		}
		setNbTextes();

	}

	public void saveTexteXlsxInBdd(){
		pathToAddsDirectory = PathToAdds.MINE.getPath();
		setTexteFromXlsx();
		TexteDao texteDao = new TexteDao();
		for(Texte texte : this.texteSource){
			texte.setTypeTexte(TypeTexte.mes150TextesSoutienParMailScolaire);
			texteDao.save(texte);
		}
	}

	private void setNbTextes() {
		nbTextes = texteSource.size();
	}

	public void setTexteFromXlsx() {
		try{
			List<String[]> retour = new ArrayList<String[]>();
			File inputFile = new File(pathToAddsDirectory+"textes.xlsx");
			File outputFile = new File(pathToAddsDirectory+"textes.csv");
			XlstoCSV.xls(inputFile, outputFile);
			CSVReader reader = new CSVReaderBis(new FileReader(pathToAddsDirectory+"textes.csv"));
			Iterator<String[]> it = reader.iterator();
			while(it.hasNext()){
				String[] line = it.next();
				if(!line[0].equals("")){
					retour.add(line);
				}
			}
			reader.close();
			this.texteSource= new ArrayList<Texte>(); 
			for(String[] texte : retour){
				texteSource.add(new Texte(texte[0]));
			}
		}catch(Exception exception){
			System.out.println("Problème avec le fichier titre");
			exception.printStackTrace();
		}
	}

	public void setTitleSource() {
		switch (typeSourceTitles) {
		case SQL:
			setTitleFromSql();
			break;
		case XLSX:
			setTitleFromXlsx();
			break;
		}
		setNbTitles();

	}

	private void setNbTitles() {
		this.nbTitles = titleSource.size();
	}

	public void setTitleFromXlsx() {
		try{
			List<String[]> retour = new ArrayList<String[]>();
			File inputFile = new File(pathToAddsDirectory+"titles.xlsx");
			File outputFile = new File(pathToAddsDirectory+"titles.csv");
			XlstoCSV.xls(inputFile, outputFile);
			CSVReader reader = new CSVReaderBis(new FileReader(pathToAddsDirectory+"titles.csv"));
			Iterator<String[]> it = reader.iterator();
			while(it.hasNext()){
				String[] line = it.next();
				if(!line[0].equals("")){
					retour.add(line);
				}
			}

			reader.close();
			nbTitles = retour.size();

			this.titleSource= new ArrayList<Title>(); 
			for(String[] title : retour){
				titleSource.add(new Title(title[0]));
			}
		}catch(Exception exception){
			System.out.println("Problème avec le fichier titre");
			exception.printStackTrace();
		}

	}

	private void setTitleFromSql() {
		TitreDao titleDao = new TitreDao();
		titleSource = titleDao.findWithSelection(this.critSelectTitre, compteLbc);
	}

	private void setTexteFromSql() {
		TexteDao texteDao = new TexteDao();
		texteSource = texteDao.findWithSelection(this.critSelectTexte, compteLbc);

	}
	//TODO faire qu'on puisse ajouter 2 images par annonce et peut être créer une classe image ?
	public void setImage(){
		File imageDirectory = new File(pathToAddsDirectory+"images");
		File[] children = imageDirectory.listFiles();
		nbImages = children.length;
		System.out.println("Nb d'images : "+ nbImages);
		imageSource = children;
	}

	// set et getter 
	public Source getTypeSourceTitles() {
		return typeSourceTitles;
	}
	public void setTypeSourceTitles(Source typeSourceTitles) {
		this.typeSourceTitles = typeSourceTitles;
	}
	public Source getTypeSourceTextes() {
		return typeSourceTextes;
	}
	public void setTypeSourceTextes(Source typeSourceTextes) {
		this.typeSourceTextes = typeSourceTextes;
	}
	public Source getTypeSourceCommunes() {
		return typeSourceCommunes;
	}
	public void setTypeSourceCommunes(Source typeSourceCommunes) {
		this.typeSourceCommunes = typeSourceCommunes;
	}

	public CritereSelectionTitre getCritSelectTitre() {
		return critSelectTitre;
	}

	public void setCritSelectTitre(CritereSelectionTitre critSelectTitre) {
		this.critSelectTitre = critSelectTitre;
	}

	public CriteresSelectionVille getCritSelectVille() {
		return critSelectVille;
	}

	public void setCritSelectVille(CriteresSelectionVille critSelectVille) {
		this.critSelectVille = critSelectVille;
	}

	public CriteresSelectionTexte getCritSelectTexte() {
		return critSelectTexte;
	}

	public void setCritSelectTexte(CriteresSelectionTexte critSelectTexte) {
		this.critSelectTexte = critSelectTexte;
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

	public void setPathToAddsDirectory(String pathToAddsDirectory) {
		this.pathToAddsDirectory = pathToAddsDirectory;
	}


}
