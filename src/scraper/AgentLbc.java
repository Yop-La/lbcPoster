package scraper;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import dao.AddDao;
import exception.AddSumitException;
import exception.AgentLbcFailPublicationException;
import exception.EchecSoumissionException;
import exception.NoAddsOnlineException;
import exception.NotRecognizedCommuneException;
import ihm.MoteurConsole;


public class AgentLbc{
	private static int waitingTime = 1000;
	private CompteLbc compteLBC;
	private WebDriver driver;
	private List<Add> addsToPublish;
	private List<Add> addsControled;
	private List<Add> addsWithCommuneNotRecognised = new ArrayList<Add>();
	private List<Add> beforeModeration = new ArrayList<Add>();
	private boolean saveAddToSubmitLbcInBase;
	public ParametresPublication paras;


	// constructeur pour publier des annonces à partir fichiers CSV
	public AgentLbc(CompteLbc compteLbc, boolean saveAddToSubmitLbcInBase, ParametresPublication paras) {
		this.compteLBC = compteLbc;
		this.saveAddToSubmitLbcInBase = saveAddToSubmitLbcInBase;
		this.paras = paras;
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


	public void setUp(){
		try{
			driver = new FirefoxDriver();
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		}catch(Exception excep){
			System.out.println("Problème au moment du setup");
			excep.printStackTrace();
		}
	}

	public void becomeInvisible(){
		effacerCookies();
		rebootBox();
		effacerCookies();
	}
	
	public void effacerCookies(){
		File cookies = new File("C:\\Users\\alexandre\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\jt1gu6k.selenium\\cookies.sqlite");
		if(cookies.delete()){
			System.out.println("Cookies supprimés");
		}
		
	}

	public void rebootBox(){
		setUp();
		allerSurCeLien("http://alexandreguillemine.fr/");
		wait(3000);
		allerSurCeLien("http://192.168.1.1");
		wait(3000);
		driver.findElement(By.id("PopupPassword")).sendKeys("15AEB2EA");
		driver.findElement(By.id("bt_authenticate")).click();
		allerSurCeLien("http://192.168.1.1/advConfigAccessType.html");
		wait(3000);
		if(driver.findElement(By.id("wan_username")).getAttribute("value").equals("")){
			driver.findElement(By.id("wan_username")).sendKeys("cxd3whr");
		}
		if(driver.findElement(By.id("wan_password")).getAttribute("value").equals("")){
			driver.findElement(By.id("wan_password")).sendKeys("99uwprp");
		}
		driver.findElement(By.id("bt_refresh")).click();
		String statut;
		Long start = System.currentTimeMillis();
		Long duree= new Long(0);
		do{
			statut = driver.findElement(By.id("msgbox_title")).getAttribute("innerHTML");
			duree=System.currentTimeMillis()-start;
			if(duree>=60000){
				System.out.println("dedans");
				driver.findElement(By.xpath("//*[@id=\"logout_link\"]/span")).click();
				driver.quit();
				rebootBox();
				return;
			}
		}while(!statut.equals("connecté"));
		// déconnection
		driver.findElement(By.xpath("//*[@id=\"logout_link\"]/span")).click();	
		driver.quit();
	}

	// pour se connecter à un compte LBC
	public void connect(){
		allerSurCeLien("https://www.leboncoin.fr/");
		wait(2000);
		driver.findElement(By.xpath("//header[@id='header']/section/section/aside/button")).click();
		driver.findElement(By.name("st_username")).click();
		driver.findElement(By.name("st_username")).sendKeys(compteLBC.getMail());
		driver.findElement(By.name("st_passwd")).sendKeys(compteLBC.getPassword());
		driver.findElement(By.xpath("//input[@value='Se connecter']")).click(); 
		wait(2000);
	}

	private void allerSurCeLien(String lien) {
		boolean surLeLien = false;
		do{
			try{
				driver.get(lien);
				surLeLien=true;
			}catch(Exception ex){
				surLeLien=false;
			}
		}while(!surLeLien);

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
			boolean formulaireSoumis=false;
			boolean communeUnknowByLbc =false;
			while(!formulaireSoumis){
				communeUnknowByLbc =false;
				try{ 
					publishOneAdd(addInPublication);
					formulaireSoumis=true;
				}catch(Exception excep){
					communeUnknowByLbc =false;
					if(excep instanceof EchecSoumissionException | excep instanceof org.openqa.selenium.NoSuchElementException){
						System.out.println("erreur au moment de la publication de n°"+indexAddPublication);
						System.out.println("Relancement de la publication");
						excep.printStackTrace();
						formulaireSoumis=false;
						//pour revenir sur le formulaire
						allerSurCeLien("https://www.leboncoin.fr/");
						driver.findElement(By.cssSelector("#header > section > section > nav > ul > li:nth-child(2) > a")).click();
					}else if(excep  instanceof AddSumitException){
						System.out.println("Annonce bien soumise mais parvient pas à cliquer sur dépôt annonce");
						formulaireSoumis=true;
						driver.findElement(By.cssSelector(".headerNav_main > li:nth-child(2) > a:nth-child(1)")).click();
					}else if(excep  instanceof NotRecognizedCommuneException){
						System.out.println("Cette commune n'est pas reconnue par lbc -> on passe donc à la suivante");
						addsWithCommuneNotRecognised.add(addInPublication);
						communeUnknowByLbc = true;
						formulaireSoumis=true;
					}else{
						System.out.println("Plantage du posteur d'annonces");
						System.out.println("Screen shot et exception  enregistré dans c:\\tmp");
						File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
						// Now you can do whatever you need to do with it, for example copy somewhere
						try{
							FileUtils.copyFile(scrFile, new File("C:\\temp\\planteposter.png"));
						}catch(Exception exe){
							System.out.println("Impossible de sauvegarder le screen shot");
						}
						excep.printStackTrace(MoteurConsole.ps);
						throw new AgentLbcFailPublicationException(indexAddPublication, addsToPublish);
					}
				} 
			}
			indexAddPublication++;
			if(!communeUnknowByLbc){
				this.beforeModeration.add(addInPublication);
				if(saveAddToSubmitLbcInBase){
					addInPublication.setCompteLbc(this.compteLBC);
					addInPublication.setEtat(EtatAdd.enAttenteModeration);
					addDao.save(addInPublication,false);
				}
			}else{

			}
		}
		System.out.println("-- Publication terminé --");
		System.out.println(indexAddPublication+" adds publiés !");
		return(beforeModeration);
	}

	private void publishOneAdd(Add addInPublication) throws Exception{

		wait(waitingTime);
		clearForm();

		// sélection de la catégorie
		new Select(driver.findElement(By.id("category"))).selectByVisibleText(paras.getAddCategory().getCategory());

		// saisie du titre
		driver.findElement(By.id("subject")).click();
		wait(waitingTime);
		driver.findElement(By.id("subject")).sendKeys(addInPublication.getTitle().getTitre());
		// saisie du texte
		driver.findElement(By.id("subject")).click();
		wait(waitingTime);
		setValue(driver.findElement(By.id("body")), addInPublication.getTexte().getCorpsTexteForPublication());

		// saisie de l'image
		wait(waitingTime);
		driver.findElement(By.id("image0")).sendKeys(addInPublication.getImage().getAbsolutePath());
		// saisie du lieu
		wait(waitingTime);
		boolean saisieCommuneFaite = false;
		int nbTentativeSoumission = 0;
		while(!saisieCommuneFaite){
			try {
				saisirCommune(addInPublication);
				saisieCommuneFaite = true;
			} catch (org.openqa.selenium.NoSuchElementException exec) {
				saisieCommuneFaite = false;
				nbTentativeSoumission++;
				if(nbTentativeSoumission==4){
					throw new NotRecognizedCommuneException();
				}
			}
		}


		wait(waitingTime);
		driver.findElement(By.id("phone")).clear();
		driver.findElement(By.id("phone")).sendKeys(paras.getNumTelephone());
		if(!paras.isAfficherNum()){
			do{
				driver.findElement(By.id("phone_hidden")).click();
			}while(!driver.findElement(By.id("phone_hidden")).isSelected());
		}

		// soumission de l'annonce pour vérification
		wait(waitingTime);
		driver.findElement(By.id("newadSubmit")).click();
		wait(waitingTime);

		// check pour vérification visuelle (à revoir)
		do{
			driver.findElement(By.cssSelector("h2.title.toggleElement")).click();// pour controler qu'on est sur la bonne page
		}while(!driver.findElement(By.cssSelector("h2.title.toggleElement")).getAttribute("class").equals("title toggleElement active"));
		wait(waitingTime);
		do{
			driver.findElement(By.id("accept_rule")).click();
		}while(!driver.findElement(By.id("accept_rule")).isSelected());
		System.out.println("Le formulaire a bien été soumis");

		// validation finale de l'annonce

		driver.findElement(By.id("lbc_submit")).click();
		System.out.println("Annonce définitivement soumise");
		// retour à la page de dépôt des annonces
		wait(5000);
		driver.findElement(By.cssSelector("a.button-blue:nth-child(2)")).click();

		try{
			driver.findElement(By.id("subject"));
		}catch(org.openqa.selenium.NoSuchElementException exec){
			throw new AddSumitException();
		}

	}

	private void clearForm() {
		if(!driver.findElement(By.id("subject")).getAttribute("value").equals(""))
			driver.findElement(By.id("subject")).clear();
		if(!driver.findElement(By.id("body")).getAttribute("value").equals(""))
			driver.findElement(By.id("body")).clear();
		if(!driver.findElement(By.id("address")).getAttribute("value").equals(""))
			driver.findElement(By.id("address")).clear();
	}

	private void saisirCommune(Add addInPublication) {
		driver.findElement(By.id("location_p")).clear();

		String communeSubmited="";
		Commune communeToPublish = addInPublication.getCommuneLink().submit;
		communeSubmited=communeToPublish.getNomCommune();
		if(!communeToPublish.getCodePostal().equals("")){
			communeSubmited = communeSubmited+" "+communeToPublish.getCodePostal();
		}

		driver.findElement(By.id("location_p")).sendKeys(communeSubmited);

		driver.findElement(By.id("location_p")).sendKeys(Keys.LEFT);
		wait(waitingTime);
		driver.findElement(By.id("location_p")).sendKeys(Keys.LEFT);
		wait(waitingTime);
		driver.findElement(By.id("location_p")).sendKeys(Keys.RIGHT);
		wait(waitingTime);
		driver.findElement(By.id("location_p")).sendKeys(Keys.RIGHT);
		wait(waitingTime);
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
		System.out.println("Ville online entrer : "+comuneOnline);

	}

	private void setValue(WebElement element, String value) {
		((JavascriptExecutor)driver).executeScript("arguments[0].value = arguments[1]", element, value);
	}

	public List<Add> scanAddsOnLbc() throws NoAddsOnlineException{

		if(driver.findElement(By.cssSelector(".value")).getAttribute("innerHTML").equals("0")){
			throw new NoAddsOnlineException();
		}

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

				String texte = driver.findElement(By.cssSelector("p.value")).getText();
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

	public List<Add> getAddsWithCommuneNotRecognised() {
		return addsWithCommuneNotRecognised;
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	


}









