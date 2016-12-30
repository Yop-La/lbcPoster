package ihm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import exception.HomeException;
import fr.doodle.dao.CompteLbcDao;
import scraper.AgentLbc;
import scraper.CompteLbc;
import scraper.Source;
import service.ObjectManager;
import service.PrintManager;
import util.Console;

public class MoteurConsole {

	ObjectManager manager;
	PrintManager printManager;

	public static void main(String[] args) {

		MoteurConsole console = new MoteurConsole();
		console.acceuil();


	}


	// Procédure qui permet d'afficher le type de sondage choisi par
	// l'utilisateur
	public void acceuil() {
		manager = new ObjectManager();
		printManager = new PrintManager(manager);
		System.out.println("------------------------------------");
		System.out.println("------- BIENVENUE ADDS MANAGER ------");
		System.out.println("------------------------------------");
		System.out.println();
		System.out.println("---------    COMMANDES    ----------");
		System.out.println("ESC : pour quitter l'appli");
		System.out.println("HOME : pour revenir à l'acceuil");
		System.out.println();
		boolean continueBoucle = true;
		while (continueBoucle) {
			System.out.println("----------    ACCUEIL    -----------");
			System.out.println();
			System.out.println("1 : Publier des annonces");
			System.out.println("2 : Ajouter un nouveau compte LBC");
			System.out.println();
			String saisie = Console.readString("Que voulez vous faire ?");
			// Enregistrement du choix de l'utilisateur dans numéro
			switch (saisie) {
			// si le numéro, on va créer un doodle
			case "1":
				try {
					publishAdd();
				} catch (HomeException homeException) {
					continueBoucle = true;
				}
				break;
			case "2":
				try {
					addNewCompteLbc();
				} catch (HomeException homeException) {
					continueBoucle = true;
				}
				break;

			case "ESC":
				System.out.println("Fermeture de l'application ");
				return;
			case "HOME":
				System.out.println("C'est déjà le menu d'acceuil ! ");
				break;
			default:
				System.out.println("Erreur de saisie");
				break;
			}
		}
	}

	private void addNewCompteLbc() throws HomeException {
		String mail = readConsoleInput("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", "Entrez le mail du compte LBC à ajouter",
				"Votre réponse", "doit être une adresse mail");
		String password = readConsoleInput(".{3,}", "Entrez le password du compte LBC à ajouter",
				"Votre réponse", "doit être faire plus de 3 caractères");
		CompteLbc compteToAdd = new CompteLbc(mail, password);
		CompteLbcDao compteLbcDao = new CompteLbcDao();
		compteLbcDao.save(compteToAdd);

	}


	private void publishAdd() throws HomeException{
		System.out.println("------    MENU DE PUBLICATION DES ANNONCES   ------");
		choixDunCompte();
		String nbAnnonces = readConsoleInput("^[1-9]\\d*$", "Entrez le nb d'annonces à publier",
				"Votre réponse", "doit être un entier positif");
		manager.createAgentLbc(Integer.parseInt(nbAnnonces));
		manager.createAddsGenerator();
		selectionTitres();
		selectionTextes();
		selectionCommunes();
		
		System.out.println("Démarrage de la publication ...");
		manager.lancerPublication();

		//String numDepart = readConsoleInput("^[1-9]\\d*$", "Entrez le numéro de l'annonce de départ",
		//		"Votre réponse", "doit être un mdp LBC");
	}



	private void selectionCommunes() throws HomeException{
		String renouvellez;
		do{
			String strTypeSource = selectSource("communes");
			manager.setCommuneSourceType(strTypeSource);

			switch (manager.getCommuneSourceType()) {
			case SQL:
				selectionCommuneSql();
				break;
			case XLSX:
				selectionCommuneXlsx();	
				break;
			}
			manager.setcommunes();
			// on affiche les titres choisies pour vérification de la part de l'utilisateur 
			System.out.println(printManager.communeToString());
			renouvellez = readConsoleInput("^oui|non", "Est ce bien les communes ci dessus que vous voulez utiliser ?",
					"Votre réponse", "doit être oui ou non");
		}while(renouvellez.equals("non"));

	}


	private void selectionCommuneXlsx() {
		// TODO Auto-generated method stub
		
	}


	private void selectionCommuneSql() throws HomeException{
		String renouvellez;
		int bornInf;
		int bornSup ;
		do{
			bornInf = Integer.parseInt(readConsoleInput("[0-9]\\d*", "Saisir la borne inférieur de population des communes à choisir :",
					"Votre réponse", "doit être un entier positive"));
			bornSup = Integer.parseInt(readConsoleInput("[0-9]\\d*", "Saisir la borne supérieure de population des communes à choisir :",
					"Votre réponse", "doit être un entier positive"));
			if(bornSup>bornInf){
				renouvellez = readConsoleInput("^oui|non", "Vous confirmez votre choix : "
						+ "born inf : "+ bornInf+
						" born sup : "+ bornSup,
						"Votre réponse", "doit être oui ou non");
			}else{
				System.out.println("Veuillez renouvellez votre saisie afin que la borne sup soit plus"
						+ "grande que la borne inf");
				renouvellez="non";
			}
			
		}while(renouvellez.equals("non"));
		manager.setCritSelectVille(bornInf, bornSup);
	}


	private String selectSource(String objectRelated)throws HomeException{
		String renouvellez;
		String strTypeSource;
		do{
			String[] pourAffichageEtSaisieDesSourceType = this.printManager.typeSourcesToString();
			System.out.println();
			System.out.println("Choisir un type de source à utiliser pour les "+objectRelated+" : ");
			System.out.println(pourAffichageEtSaisieDesSourceType[0]);
			System.out.println("Saisir le type de source à utiliser pour les "+objectRelated+" : ");
			strTypeSource = readConsoleInput(pourAffichageEtSaisieDesSourceType[1], "Entrez le type de source choisi : ",
					"Votre réponse", "doit être un des types sources");
			renouvellez = readConsoleInput("^oui|non", "Est ce bien ce type de source : "+ strTypeSource +""
					+ " que vous voulez utiliser ? ",
					"Votre réponse", "doit être oui ou non");
		}while(renouvellez.equals("non"));
		return strTypeSource;
	}

	private String selectPath()throws HomeException{
		String renouvellez;
		String path;
		do{
			path = readConsoleInput("^MINE$|CLIENT", "Saisir le répertoire des annonces à utiliser",
					"Votre réponse", "doit être MINE ou CLIENT");
			renouvellez = readConsoleInput("^oui|non", "Est ce bien ce répertoire : "+ path +""
					+ " que vous voulez utiliser ? ",
					"Votre réponse", "doit être oui ou non");
		}while(renouvellez.equals("non"));
		return path;
	}


	private void selectionTextes() throws HomeException {
		String renouvellez;
		do{
			String strTypeSource = selectSource("textes");
			manager.setTexteSourceType(strTypeSource);

			switch (manager.getTexteSourceType()) {
			case SQL:
				selectionTexteSql();
				break;
			case XLSX:
				selectionTextesXlsx();	
				break;
			}
			manager.setTextes();
			// on affiche les titres choisies pour vérification de la part de l'utilisateur 
			System.out.println(printManager.texteToString());
			renouvellez = readConsoleInput("^oui|non", "Est ce bien les textes ci dessus que vous voulez utiliser ?",
					"Votre réponse", "doit être oui ou non");
		}while(renouvellez.equals("non"));

	}


	private void selectionTextesXlsx() throws HomeException{
		String path = selectPath();
		manager.setPathToAdds(path);	
	}


	private void selectionTexteSql() {
		// TODO Auto-generated method stub

	}


	private void selectionTitres() throws HomeException{
		String renouvellez;
		do{
			String strTypeSource = selectSource("titres");
			manager.setTitleSourceType(strTypeSource);

			switch (manager.getTitleSourceType()) {
			case SQL:
				selectionTitresSql();
				break;
			case XLSX:
				selectionTitresXlsx();	
				break;
			}
			manager.setTitres();
			// on affiche les titres choisies pour vérification de la part de l'utilisateur 
			System.out.println(printManager.titreToString());
			renouvellez = readConsoleInput("^oui|non", "Est ce bien les titres ci dessus que vous voulez utiliser ?",
					"Votre réponse", "doit être oui ou non");
		}while(renouvellez.equals("non"));


	}




	private void selectionTitresXlsx() throws HomeException{
		String path = selectPath();
		manager.setPathToAdds(path);
	}


	private void selectionTitresSql() {
		// TODO Auto-generated method stub

	}


	private void choixDunCompte() throws HomeException{

		manager.setComptes();
		String[] pourAffichageEtSaisieDesComptes = this.printManager.comptestoString();
		String renouvellez;
		String idCompte ;
		System.out.println();
		do{


			System.out.println("Choisir un compte à utiliser : ");
			System.out.println(pourAffichageEtSaisieDesComptes[0]);
			System.out.println("Saisir le compte à utiliser : ");
			idCompte = readConsoleInput(pourAffichageEtSaisieDesComptes[1], "Entrez l'identifiant du compte choisi : ",
					"Votre réponse", "doit être un des identifiants");
			manager.setCompte(Integer.parseInt(idCompte));
			//		System.out.println("Vous avez choisi le compte : "+manager.getCompteInUse().getMail());
			renouvellez = readConsoleInput("^oui|non", "Est ce bien ce compte : "+ manager.getCompteInUse().getMail() +""
					+ " que vous voulez utiliser ? ",
					"Votre réponse", "doit être oui ou non");
		}while(renouvellez.equals("non"));
		manager.setCompte(Integer.parseInt(idCompte));
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


}
