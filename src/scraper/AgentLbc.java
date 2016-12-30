package scraper;
import java.util.List;
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
	private int nbAddsToPublish;

	private int numDepart=1;

	
	// constructeur pour publier des annonces à partir fichiers CSV
	public AgentLbc(CompteLbc compteLbc, int nbAddsToPublish) {
		this.compteLBC = compteLbc;
		this.nbAddsToPublish = nbAddsToPublish;
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

		clickAgainUntilExpectedCondition(
				By.xpath("//header[@id='header']/section/section/aside/button"), 
				By.name("st_username"),
				1000, 
				1000);
		waitForWebElementToRespectCondition(By.name("st_username"),1).click();
		waitForWebElementToRespectCondition(By.name("st_username"),1).sendKeys(compteLBC.getMail());
		waitForWebElementToRespectCondition(By.name("st_passwd"),1).sendKeys(compteLBC.getPassword());
		clickAgainUntilExpectedCondition(
				By.xpath("//input[@value='Se connecter']"), 
				By.linkText("Déposer une annonce"),
				4000, 
				1000);
	}

	public void goToFormDepot(){
		clickAgainUntilExpectedCondition(
				By.linkText("Déposer une annonce"), 
				By.cssSelector("div.grid-2 > div"),
				4000, 
				1000);
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
