package scraper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.ActionMapUIResource;

import org.dom4j.datatype.DatatypeDocumentFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class AgentLbc{

	private CompteLbc compteLBC;
	private WebDriver driver;
	private List<Add> addsToPublish;
	private List<Add> addsControled;
	private int nbAddsToPublish;

	private int numDepart=1;


	// constructeur pour publier des annonces à partir fichiers CSV
	public AgentLbc(CompteLbc compteLbc, int nbAddsToPublish) {
		this.compteLBC = compteLbc;
		this.nbAddsToPublish = nbAddsToPublish;
	}

	public AgentLbc(CompteLbc compteLbc) {
		this.compteLBC = compteLbc;
		addsControled = new ArrayList<Add>();
	}

	public List<Add> getAddsToPublish() {
		return addsToPublish;
	}

	public void setAddsToPublish(List<Add> addsToPublish) {
		this.addsToPublish = addsToPublish;
	}

	public CompteLbc getCompteLBC() {
		return compteLBC;
	}
	public void setCompteLBC(CompteLbc compteLBC) {
		this.compteLBC = compteLBC;
	}
	public int getNbAddsToPublish() {
		return nbAddsToPublish;
	}
	public void setNbAddsToPublish(int nbAddsToPublish) {
		this.nbAddsToPublish = nbAddsToPublish;
	}
	public int getNumDepart() {
		return numDepart;
	}
	public void setNumDepart(int numDepart) {
		this.numDepart = numDepart;
	}

	public void setUp(){
		try{
			driver = new FirefoxDriver();
			driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		}catch(Exception excep){
			System.out.println("Problème au moment du setup");
		}
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

		driver.get("https://www.leboncoin.fr/");

		driver.findElement(By.xpath("//header[@id='header']/section/section/aside/button")).click();

		driver.findElement(By.name("st_username")).click();
		driver.findElement(By.name("st_username")).sendKeys(compteLBC.getMail());
		driver.findElement(By.name("st_passwd")).sendKeys(compteLBC.getPassword());
		driver.findElement(By.xpath("//input[@value='Se connecter']")).click(); 
	}

	public void goToFormDepot(){
		driver.findElement(By.linkText("Déposer une annonce")).click(); 
	}

	public void publish(){

		int indexAddPublication = numDepart-1;
		try{ 

			do{
				System.out.println("Annonce "+(indexAddPublication+1)+" en cours de publication");
				Add addInPublication = addsToPublish.get(indexAddPublication);
				publishOneAdd(addInPublication);
				indexAddPublication++;
			}while(indexAddPublication!=nbAddsToPublish);
			System.out.println("-- Publication terminé --");


		}catch(Exception exception){
			System.out.println("Erreur lors de la publication de l'annonce n°"+indexAddPublication+1);
			exception.printStackTrace();
		}
	}

	private void publishOneAdd(Add addInPublication){

		// sélection de la catégorie
		waitForWebElementToRespectCondition(By.cssSelector("div.grid-2 > div"),1).click();
		new Select(waitForWebElementToRespectCondition(By.id("category"),1)).selectByVisibleText("Cours particuliers");

		// saisie du titre
		waitForWebElementToRespectCondition(By.id("subject"),1).clear();
		waitForWebElementToRespectCondition(By.id("subject"),1).sendKeys(addInPublication.getTitle().getTitre());

		// saisie du texte
		waitForWebElementToRespectCondition(By.id("body"),1).clear();
		waitForWebElementToRespectCondition(By.id("body"),1).sendKeys(addInPublication.getTexte().getCorpsTexte());

		// saisie de l'image
		waitForWebElementToRespectCondition(By.id("image0"),3).sendKeys(addInPublication.getImage().getAbsolutePath());

		// saisie du lieu 
		waitForWebElementToRespectCondition(By.id("location_p"),3).clear();
		waitForWebElementToRespectCondition(By.id("location_p"),3).sendKeys(addInPublication.getCommune().getNomCommune());
		waitForWebElementToRespectCondition(By.id("location_p"),3).sendKeys(Keys.LEFT);
		waitForWebElementToRespectCondition(By.cssSelector("ul.location-list.visible"),1);
		waitForWebElementToRespectCondition(By.id("location_p"),1).sendKeys(Keys.ENTER);

		// soumission de l'annonce pour vérification
		waitForWebElementToRespectCondition(By.id("address"),1).clear();
		waitForWebElementToRespectCondition(By.id("newadSubmit"),1).click();



		// check pour vérification visuelle
		try{
			clickAgainUntilExpectedCondition(
					By.cssSelector("h2.title.toggleElement"), 
					By.cssSelector("h2.title.toggleElement.active"),
					4000, 
					1000);			
		}catch(TimeoutException timeOut){// si time out exception généré c'est que l'annonce n'a surement pas été validé
			waitForWebElementToRespectCondition(By.id("address"),1).clear();
			waitForWebElementToRespectCondition(By.id("newadSubmit"),1).click();
		}

		try{
			Thread.sleep(10000); // pour avoir le temps de vérifier l'annonce manuellement
		}catch(Exception exception){
			exception.printStackTrace();
		}

		// acceptation des CG

		WebElement checkBox = waitForWebElementToRespectCondition(By.id("accept_rule"),1);
		do{
			checkBox.click();
		}while(!checkBox.isSelected());

		// validation finale de l'annonce

		waitForWebElementToRespectCondition(By.id("lbc_submit"),1).click();

		// retour à la page de dépôt des annonces

		clickAgainUntilExpectedCondition(
				By.linkText("Déposer une annonce"), 
				By.cssSelector("div.grid-2 > div"),
				4000, 
				1000);

	}


	public List<Add> controlCompte(){



		boolean allAddsControled = false;
		List<Add> addsControled = new ArrayList<Add>();
		int indicePageInControl = 1;

		while(!allAddsControled){ // pour boucler sur les pages des annonces 
			int indicePage;

			List<String> addLinks = new ArrayList<String>();
			List<WebElement> listeAdds = driver.findElements(By.cssSelector("div.element"));
			List<Add> addsOnPageInControl = new ArrayList<Add>(); // rassemble toutes les adds d'une page mon compte
			// on parcoure les infos de la liste d'annonces (nb clique, date de mise en ligne, nb mails )
			for(WebElement enteteAdd : listeAdds){

				Add addInControl = new Add();

				// on récupère la date de mise en ligne, le nb de clics tel, de vue, etc des annonces de la liste 

				String date = enteteAdd.findElement(By.cssSelector("div.date")).getText();
				String hour = enteteAdd.findElement(By.cssSelector("div.hour")).getText();
				addInControl.setNbJoursRestants(Integer.parseInt(enteteAdd.findElement(By.cssSelector("div.nb")).getText()));
				addInControl.setNbVues(Integer.parseInt(enteteAdd.findElement(By.xpath("div[4]/div[1]/span")).getAttribute("innerHTML")));
				addInControl.setNbMailsRecus(Integer.parseInt(enteteAdd.findElement(By.xpath("div[4]/div[2]/span")).getAttribute("innerHTML")));
				addInControl.setNbClickTel(Integer.parseInt(enteteAdd.findElement(By.xpath("div[4]/div[3]/span")).getAttribute("innerHTML")));

				// conversion de la date de mise en ligne
				try{
					SimpleDateFormat sdf = new SimpleDateFormat("d MMM hh:mm");
					String dateInString = date+" "+hour;
					Calendar dateDepot = Calendar.getInstance();
					dateDepot.setTime(sdf.parse(dateInString));
					Calendar dateOfTheDay = Calendar.getInstance();
					if(dateOfTheDay.get(Calendar.MONTH)<dateDepot.get(Calendar.MONTH)){
						dateDepot.set(Calendar.YEAR, dateOfTheDay.get(Calendar.YEAR)-1);
					}else{
						dateDepot.set(Calendar.YEAR, dateOfTheDay.get(Calendar.YEAR));
					}
					addInControl.setDateMiseEnLigne(dateDepot);
				}catch(Exception exec){
					System.out.println("date impossible à convertir !");
				}
				addsOnPageInControl.add(addInControl);
				// on se rend sur chacune des annonces pour récupérer la ville d'origine de l'annonce, le texte et le titre
				//System.out.println(web.findElement(By.cssSelector("a"));

				String addLink = enteteAdd.findElement(By.cssSelector("a")).getAttribute("href");
				addLinks.add(addLink);
			}

			// on parcoure ensuite les annonces une à une pour récupérer titre, textes et ville
			for(int i=0;i<addLinks.size();i++){

				String addLink = addLinks.get(i);
				Add add = addsOnPageInControl.get(i); 
				driver.get(addLink);
				String nomCommuneComplet = driver.findElement(By.cssSelector("span.value")).getText();
				String[] nomCommune = nomCommuneComplet.split(" ");
				Commune commune = new Commune();
				commune.setNomCommune(nomCommune[0]);
				commune.setCodePostal(nomCommune[1]);
				add.setCommune(commune);
				String title = driver.findElement(By.cssSelector("h1.no-border")).getText();
				add.setTitle(new Title(title));
				String texte = driver.findElement(By.id("description")).getText();
				add.setTexte(new Texte(texte));
				System.out.println("---- Add n°"+(i+1+(indicePageInControl-1)*30)+" controlé -----");
			}
			addsControled.addAll(addsOnPageInControl);
			driver.get("https://compteperso.leboncoin.fr/account/index.html");
			try{
				for(int i=0;i<indicePageInControl;i++){
					driver.findElement(By.linkText(">")).click();
					do{
						WebElement actualPage = driver.findElement(By.cssSelector("#dashboard_pagging > li.selected"));
						indicePage = Integer.parseInt(actualPage.getText());
					}while(indicePage!=(i+1));
					Thread.sleep(10000);
				}
			}catch(Exception exception){
				allAddsControled =true;
			}
			indicePageInControl ++;
		}
		return addsControled;
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

	public void clickAgainUntilExpectedCondition(By elementToClick, By elementToWait,int timeToWait, int pas){
		for (int second = 0;; second++) {
			waitForWebElementToRespectCondition(elementToClick,2).click();
			try{
				Thread.sleep(timeToWait);
				waitForWebElementToRespectCondition(elementToWait,1);
				break;
			}catch(Exception exception){
				timeToWait+=pas;
				System.out.println("Condition attendu pas valide. Temps d'attente prochain : "+timeToWait);
				if(timeToWait>20000){
					System.out.println(" Temps dépassé ! impossible de respecter la condition ! ");
				}
			}
		}
	}








}
