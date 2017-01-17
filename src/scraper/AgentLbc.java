package scraper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import exception.AgentLbcFailPublicationException;
import fr.doodle.dao.AddDao;


public class AgentLbc{

	private CompteLbc compteLBC;
	private WebDriver driver;
	private List<Add> addsToPublish;
	private List<Add> addsControled;
	private List<Add> beforeModeration = new ArrayList<Add>();
	private int nbAddsToPublish;
	private boolean saveAddToSubmitLbcInBase;
	JavascriptExecutor jse;


	// constructeur pour publier des annonces à partir fichiers CSV
	public AgentLbc(CompteLbc compteLbc, int nbAddsToPublish, boolean saveAddToSubmitLbcInBase) {
		this.compteLBC = compteLbc;
		this.nbAddsToPublish = nbAddsToPublish;
		this.saveAddToSubmitLbcInBase = saveAddToSubmitLbcInBase;
	}

	public AgentLbc(CompteLbc compteLbc) {
		this.compteLBC = compteLbc;
		addsControled = new ArrayList<Add>();
	}



	public List<Add> getAddsControled() {
		return addsControled;
	}

	public void setAddsControled(List<Add> addsControled) {
		this.addsControled = addsControled;
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

	public void setUp(){
		try{
			driver = new FirefoxDriver();
			jse = (JavascriptExecutor)driver;
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
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
		try{
			driver.get("https://www.leboncoin.fr/");
		}catch(Exception ex){
			driver.get("https://www.leboncoin.fr/");
		}
		wait(2000);
		driver.findElement(By.xpath("//header[@id='header']/section/section/aside/button")).click();
		driver.findElement(By.name("st_username")).click();
		driver.findElement(By.name("st_username")).sendKeys(compteLBC.getMail());
		driver.findElement(By.name("st_passwd")).sendKeys(compteLBC.getPassword());
		driver.findElement(By.xpath("//input[@value='Se connecter']")).click(); 
		wait(2000);
	}

	public void goToFormDepot(){
		driver.findElement(By.cssSelector(".value"));
		driver.findElement(By.linkText("Déposer une annonce")).click();
		wait(2000);
	}

	public List<Add> publish() throws AgentLbcFailPublicationException{
		AddDao addDao = new AddDao();
		int indexAddPublication = 0;


		for(Add addInPublication : addsToPublish){
			System.out.println("Annonce "+(indexAddPublication+1)+" en cours de publication");
			try{ 
				publishOneAdd(addInPublication);
				this.beforeModeration.add(addInPublication);
			}catch(Exception exception){
				exception.printStackTrace();
				AgentLbcFailPublicationException excep = new AgentLbcFailPublicationException(indexAddPublication, beforeModeration);
				throw excep;
			}
			indexAddPublication++;
			if(saveAddToSubmitLbcInBase){
				addInPublication.setCompteLbc(this.compteLBC);
				addInPublication.setEtat(EtatAdd.enAttenteModeration);
				addDao.save(addInPublication,false);
			}
		}
		System.out.println("-- Publication terminé --");
		return(beforeModeration);
	}

	private void publishOneAdd(Add addInPublication){
		wait(2000);
		driver.findElement(By.id("subject")).click();
		// sélection de la catégorie
		new Select(driver.findElement(By.id("category"))).selectByVisibleText("Cours particuliers");
		// saisie du titre
		driver.findElement(By.id("subject")).click();
		driver.findElement(By.id("subject")).clear();
		driver.findElement(By.id("subject")).sendKeys(addInPublication.getTitle().getTitre());
		// saisie du texte
		driver.findElement(By.id("body")).clear();
		//System.out.println("salut");
		//System.out.println("document.getElementById('body').setAttribute('value', \""+addInPublication.getTexte().getCorpsTexteForPublication()+"\")");
		//jse.executeScript("document.getElementById('body').setAttribute('value', \""+addInPublication.getTexte().getCorpsTexteForPublication()+"\")");
		//driver.findElement(By.id("body")).sendKeys(addInPublication.getTexte().getCorpsTexteForPublication());
		setValue(driver.findElement(By.id("body")), addInPublication.getTexte().getCorpsTexteForPublication());
		
		// saisie de l'image

		driver.findElement(By.id("image0")).sendKeys(addInPublication.getImage().getAbsolutePath());
		// saisie du lieu 
		driver.findElement(By.id("location_p")).clear();

		String communeSubmited="";
		Commune communeToPublish = addInPublication.getCommuneLink().submit;
		communeSubmited=communeToPublish.getNomCommune();
		if(!communeToPublish.getCodePostal().equals("")){
			communeSubmited = communeSubmited+" "+communeToPublish.getCodePostal();
		}
		jse.executeScript("window.scrollBy(0,250)", "");
		driver.findElement(By.id("location_p")).sendKeys(communeSubmited);

		driver.findElement(By.id("location_p")).sendKeys(Keys.LEFT);
		wait(500);
		driver.findElement(By.id("location_p")).sendKeys(Keys.LEFT);
		wait(500);
		driver.findElement(By.id("location_p")).sendKeys(Keys.ENTER);
		driver.findElement(By.cssSelector("#map_newad > div.layout:not(.hidden)"));
		driver.findElement(By.cssSelector("#map_newad > div.layout.hidden"));
		//driver.findElement(By.id("location_p")).getText();
		String nomCommuneOnLbc = driver.findElement(By.cssSelector(".location-container > div:nth-child(1) > input:nth-child(6)")).getAttribute("value");
		String CodePostalOnLbc =driver.findElement(By.cssSelector(".location-container > div:nth-child(1) > input:nth-child(5)")).getAttribute("value"); //code postal

		Commune comuneOnline = new Commune();
		comuneOnline.setNomCommune(nomCommuneOnLbc);
		comuneOnline.setCodePostal(CodePostalOnLbc);
		addInPublication.getCommuneLink().onLine=comuneOnline;

		driver.findElement(By.id("address")).clear();

		//driver.findElement(By.id("phone")).clear();
		//driver.findElement(By.id("phone")).sendKeys("0668332764");

		// soumission de l'annonce pour vérification
		driver.findElement(By.id("address")).clear();
		wait(5000);
		driver.findElement(By.id("newadSubmit")).click();
		wait(2000);

		// check pour vérification visuelle
		try{
			driver.findElement(By.cssSelector("h2.title.toggleElement")).click();// pour controler qu'on est sur la bonne page

		}catch(TimeoutException timeOut){// si time out exception généré c'est que l'annonce n'a surement pas été validé
			driver.findElement(By.id("address")).clear();

			driver.findElement(By.id("newadSubmit")).click();
		}
		if(!driver.findElement(By.id("accept_rule")).isSelected())
			driver.findElement(By.id("accept_rule")).click();
		wait(4000);
		// validation finale de l'annonce

		driver.findElement(By.id("lbc_submit")).click();
		wait(5000);
		// retour à la page de dépôt des annonces

		driver.findElement(By.cssSelector(".headerNav_main > li:nth-child(2) > a:nth-child(1)")).click();
	}

	private void setValue(WebElement element, String value) {
		((JavascriptExecutor)driver).executeScript("arguments[0].value = arguments[1]", element, value);
	}

	/*
	private void selectTheRightCommune(Commune communeSubmited) {
		driver.findElement(By.cssSelector("ul.location-list.visible"));
		Commune mostSimilarCommune = findTheMostSimilarCommune(communeSubmited);
		boolean isMostSimilarCommuneOnline 
					= mostSimilarCommune.isThisCommuneOnLine(mostSimilarCommune);

	}

	private Commune findTheMostSimilarCommune(Commune communeSubmited) {
		List<String> communesInLbc = getCommunesFromLocationList();
		Commune mostSimilarCommune = new Commune();
		mostSimilarCommune.setNomCommuneInBase(communeSubmited.getNomCommuneInBase());
		mostSimilarCommune.setNomCommuneOnLbc(communesInLbc.get(0));
		mostSimilarCommune.setLevenshteinDistanceBetweenLbcAndBdd();

		for(int i=1;i<communesInLbc.size();i++){
			Commune communeCandidate = new Commune();
			communeCandidate.setNomCommuneInBase(communeSubmited.getNomCommuneInBase());
			communeCandidate.setNomCommuneOnLbc(communesInLbc.get(i));
			communeSubmited.setLevenshteinDistanceBetweenLbcAndBdd();

			if(mostSimilarCommune.getLevenshteinDistanceBetweenLbcAndBdd()>=
					communeCandidate.getLevenshteinDistanceBetweenLbcAndBdd()){
				mostSimilarCommune=communeCandidate;
			}
		}
		return mostSimilarCommune;
	}

	private List<String> getCommunesFromLocationList() {
		List<WebElement> results = driver.findElements(By.cssSelector(".location-list > li"));
		List<String> retour = new ArrayList<String>();
		int indiceLi = 1;
		for(WebElement communeInList : results){
			String nomCommune = communeInList.findElement(
					By.cssSelector("li:nth-child("+indiceLi+") > span:nth-child(1)"))
					.getAttribute("title");
			System.out.println(nomCommune);
			retour.add(nomCommune);
		}
		return retour;
	}*/

	public List<Add> scanAddsOnLbc(){



		boolean allAddsControled = false;
		List<Add> addsControled = new ArrayList<Add>();
		int indicePageInControl = 1;

		while(!allAddsControled){ // pour boucler sur les pages des annonces 
			int indicePage;

			List<String> addLinks = new ArrayList<String>();
			List<WebElement> listeAdds = driver.findElements(By.cssSelector("div.element"));
			List<Add> addsOnPageInControl = new ArrayList<Add>(); // rassemble toutes les adds d'une page mon compte
			// on parcoure les infos de la liste d'annonces (nb clique, date de mise en ligne, nb mails )
			int indexAddToControl = 0; 
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
				indexAddToControl++;
				/*if(indexAddToControl==3)
					break;*/
			}

			// on parcoure ensuite les annonces une à une pour récupérer titre, textes et ville
			for(int i=0;i<addLinks.size();i++){

				String addLink = addLinks.get(i);
				Add add = addsOnPageInControl.get(i); 
				try{
					driver.get(addLink);
				}catch(Exception e){
					driver.get(addLink);
				}

				String nomCommuneComplet = driver.findElement(By.cssSelector("span.value")).getText();

				// pour séparer le code postal de la commune
				Pattern p = Pattern.compile("^(\\S\\D+)\\s(\\d{5})$");
				// création d'un moteur de recherche
				Matcher m = p.matcher(nomCommuneComplet);
				// lancement de la recherche de toutes les occurrences
				boolean b = m.matches();

				String nomCommune = m.group(1);
				String codePostal = m.group(2);
				System.out.println(m.groupCount()+" : "+nomCommune+" : "+codePostal);

				Commune communeOnLine = new Commune();
				communeOnLine.setNomCommune(nomCommune);
				communeOnLine.setCodePostal(codePostal);
				add.getCommuneLink().onLine=communeOnLine;
				String title = driver.findElement(By.cssSelector("h1.no-border")).getText();
				add.setTitle(new Title(title));
				String texte = driver.findElement(By.id("description")).getText();
				Texte texteOnLbc = new Texte();
				texteOnLbc.setCorpsTexteOnLbc(texte);
				add.setTexte(texteOnLbc);
				System.out.println("---- Add n°"+(i+1+(indicePageInControl-1)*30)+" controlé -----");
				/*if(indexAddToControl==2){
					break;
				}*/
			}
			addsControled.addAll(addsOnPageInControl);
			//pour se rendre sur le page n° indicePage des annonces
			try{
				driver.get("https://compteperso.leboncoin.fr/account/index.html");
			}catch(Exception e){
				driver.get("https://compteperso.leboncoin.fr/account/index.html");
			}
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
			/*	allAddsControled =true;*/

		}
		this.addsControled = addsControled;
		return addsControled;
	}
	private void wait(int ms){
		try{
			Thread.sleep(ms); // pour avoir le temps de vérifier l'annonce manuellement
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public List<Add> getBeforeModeration() {
		return beforeModeration;
	}


}









