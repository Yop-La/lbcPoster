package scraper;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.net.UrlChecker.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencsv.CSVReader;

import csv.CSVReaderBis;
import csv.XlstoCSV;

public class AgentLBC{

	private CompteLBC compteLBC;
	private WebDriver driver;
	private List<Add> addsToPublish;
	private String pathToAddsDirectory;
	private int nbAddsToPublish;
	private int nbTitles;
	private int nbTextes;
	private int nbImages;
	private int nbTowns;


	// constructeur pour publier des annonces
	public AgentLBC(String pathToAddsDirectory, int nbAddsToPublish, String mail, String mdp) {
		setUp();

		/* pour sélectionner le bon répertoire contenant les annonces */
		if(pathToAddsDirectory.equals("mine")){
			this.pathToAddsDirectory="D:\\Dropbox\\HelloMentor\\Gestion Annonces\\automatisation dépôt annonces\\documents pour robots\\";
		}else if(pathToAddsDirectory.equals("client")){
			this.pathToAddsDirectory="D:\\Dropbox\\HelloMentor\\Gestion Annonces\\automatisation dépôt annonces\\documents pour robots pour clients\\";
		}

		// création du compte
		compteLBC = new CompteLBC(mail, mdp);

		// récupération des annonces
		this.nbAddsToPublish = nbAddsToPublish;
		addsToPublish = readAddCSV();
	}

	// constructeur pour parcourir un compte
	public AgentLBC(CompteLBC compteLBC){
		this.compteLBC = compteLBC;
	}

	public void setUp(){
		try{
			driver = new FirefoxDriver();
		}catch(Exception excep){
			System.out.println("Problème au moment du setup");
		}
	}


	public void testConnetFillForm() throws Exception {

	}



	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	// pour se connecter à un compte LBC
	public void connect(){
		try{
			driver.get("https://www.leboncoin.fr/");
			waitForWebElementToRespectCondition(By.xpath("//header[@id='header']/section/section/aside/button"),2).click();

			waitForWebElementToRespectCondition(By.name("st_username"),1).click();
			waitForWebElementToRespectCondition(By.name("st_username"),1).clear();
			waitForWebElementToRespectCondition(By.name("st_username"),1).sendKeys(compteLBC.getMail());
			waitForWebElementToRespectCondition(By.name("st_passwd"),1).clear();
			waitForWebElementToRespectCondition(By.name("st_passwd"),1).sendKeys(compteLBC.getPassword());
			clickUntilCondition(By.xpath("//input[@value='Se connecter']"), By.linkText("Déposer une annonce"));
		
		}catch(Exception exception){
			System.out.println("Erreur au niveau de la connexion au compte");
			exception.printStackTrace();
		}
	}

	public void goToFormDepot(){

		try{
			// on se rend sur le menu "mon compte" de la page perso
			waitForWebElementToRespectCondition(By.linkText("Déposer une annonce"),1).click();

		}catch(Exception excep){
			System.out.println("Erreur pour se rendre sur le page de dépôt");
			excep.printStackTrace();
		}

	}

	public void publish(){
		connect();
		goToFormDepot();

		int indexAddPublication = 0;
		try{ 

			do{
				System.out.println("Annonce "+(indexAddPublication+1)+" en cours de publication");
				Add addInPublication = addsToPublish.get(indexAddPublication);
				publishOneAdd(addInPublication);
				indexAddPublication++;
			}while(indexAddPublication!=nbAddsToPublish-1);
			System.out.println("-- Publication terminé --");


		}catch(Exception exception){
			System.out.println("Erreur lors de la publication de l'annonce n°"+indexAddPublication+1);
			exception.printStackTrace();
		}
	}

	private void publishOneAdd(Add addInPublication) throws Exception{
		waitForWebElementToRespectCondition(By.cssSelector("div.grid-2 > div"),1).click();
		new Select(waitForWebElementToRespectCondition(By.id("category"),1)).selectByVisibleText("Cours particuliers");
		waitForWebElementToRespectCondition(By.id("subject"),1).clear();
		waitForWebElementToRespectCondition(By.id("subject"),1).sendKeys(addInPublication.getTitle());
		waitForWebElementToRespectCondition(By.id("body"),1).clear();
		waitForWebElementToRespectCondition(By.id("body"),1).sendKeys(addInPublication.getTexte());

		waitForWebElementToRespectCondition(By.id("image0"),3).clear();
		waitForWebElementToRespectCondition(By.id("image0"),3).sendKeys(addInPublication.getImage().getAbsolutePath());


		waitForWebElementToRespectCondition(By.id("location_p"),3).clear();
		waitForWebElementToRespectCondition(By.id("location_p"),3).sendKeys(addInPublication.getVille());
		waitForWebElementToRespectCondition(By.id("location_p"),3).sendKeys(Keys.LEFT);
		waitForWebElementToRespectCondition(By.cssSelector("ul.location-list.visible"),1);

		waitForWebElementToRespectCondition(By.id("location_p"),1).sendKeys(Keys.ENTER);
		
		waitForWebElementToRespectCondition(By.id("newadSubmit"),1).click();

		waitForWebElementToRespectCondition(By.cssSelector("h2.title.toggleElement"),1).click();
		Thread.sleep(10000); // pour avoir le temps de vérifier l'annonce manuellement
		waitForWebElementToRespectCondition(By.id("accept_rule"),1).click();
		waitForWebElementToRespectCondition(By.id("lbc_submit"),1).click();

		System.out.println();
		waitForWebElementToRespectCondition(By.linkText("Déposer une annonce"),1).click();
		
	}


	public void controlCompte(){

	}
	WebElement waitForWebElementToRespectCondition(By by, int expectedCondition ){
		WebElement myDynamicElement=null;
		switch(expectedCondition){
		case 1:
			myDynamicElement = (new WebDriverWait(driver, 30))
			.until(ExpectedConditions.elementToBeClickable(by));	
			break;
		case 2:
			myDynamicElement = (new WebDriverWait(driver, 30))
			.until(ExpectedConditions.visibilityOfElementLocated(by));
			break;
		case 3:
			myDynamicElement = (new WebDriverWait(driver, 30))
			.until(ExpectedConditions.presenceOfElementLocated(by));
			break;
		}
		return myDynamicElement;
	}

	void clickUntilCondition(By byToClick, By byToWait){
		int timeToWait = 5000;
		for (int second = 0;; second++) {
			try { 
				driver.findElement(byToClick).click();
				Thread.sleep(timeToWait);
				timeToWait+=1000;
				if (isElementPresent(byToWait)){
					return;
				}
					 
			}catch (Exception e) {
				if(second>60){
					e.printStackTrace();
					return;
				}
				
			}

		}
	}

	// partie récupérationd des annonces à partir des fichiers CSV uniquement


	private List<Add> readAddCSV(){
		List<String[]> titles = readTitleCSV();
		List<String[]> textes = readTexteCSV();
		List<String[]> towns = readTownCSV();
		File[] images = readImages();
		List<Add> adds = new ArrayList<Add>();
		for(int i=0;i<nbAddsToPublish;i++){
			int indiceImage = 0, indiceTexte = 0, indiceTitle = 0, indiceTown = 0;
			if(i!=0){
				indiceTitle = i%nbTitles;
				indiceTexte = i%nbTextes;
				indiceTown = i%nbTowns;
				indiceImage = i%nbImages;
			}
			adds.add(new Add(titles.get(indiceTitle)[0], 
					textes.get(indiceTexte)[0], 
					towns.get(indiceTown)[0], images[indiceImage]));
		}
		return adds;
	}


	private List<String[]> readTitleCSV(){
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

			System.out.println("Nb de titres : "+ nbTitles);
			return retour;
		}catch(Exception exception){
			System.out.println("Problème avec le fichier titre");
			exception.printStackTrace();
			return null;
		}
	}

	// on part du principe que les textes sont déjà générés pour le moment
	private List<String[]> readTexteCSV(){
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
			nbTextes = retour.size();
			reader.close();
			System.out.println("Nb de textes : "+ nbTextes);
			return retour;
		}catch(Exception exception){
			System.out.println("Problème avec le fichier titre");
			exception.printStackTrace();
			return null;
		}
	}

	private List<String[]> readTownCSV(){
		try{
			List<String[]> retour = new ArrayList<String[]>();
			File inputFile = new File(pathToAddsDirectory+"towns.xlsx");
			File outputFile = new File(pathToAddsDirectory+"towns.csv");
			XlstoCSV.xls(inputFile, outputFile);
			CSVReader reader = new CSVReaderBis(new FileReader(pathToAddsDirectory+"towns.csv"));
			Iterator<String[]> it = reader.iterator();
			while(it.hasNext()){
				String[] line = it.next();
				if(!line[0].equals("")){
					retour.add(line);
				}
			}

			reader.close();
			nbTowns = retour.size();
			System.out.println("Nb de villes : "+ retour.size());
			return retour;
		}catch(Exception exception){
			System.out.println("Problème avec le fichier titre");
			exception.printStackTrace();
			return null;
		}
	}

	private File[] readImages(){
		File imageDirectory = new File(pathToAddsDirectory+"images");
		File[] children = imageDirectory.listFiles();
		nbImages = children.length;
		System.out.println("Nb d'images : "+ nbImages);
		return children;
	}






}
