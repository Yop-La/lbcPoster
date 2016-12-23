package ihm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import exception.HomeException;
import scraper.AgentLBC;
import util.Console;

public class MoteurConsole {

	AgentLBC agentLBC;

	public static void main(String[] args) {
		
		MoteurConsole console = new MoteurConsole();
		console.acceuil();
		
	

		

	}


	// Procédure qui permet d'afficher le type de sondage choisi par
	// l'utilisateur
	public void acceuil() {
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
			System.out.println("2 : ...");
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
				System.out.println("nothing ...");
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

	private void publishAdd() throws HomeException{
		String nbAnnonces = readConsoleInput("^[1-9]\\d*$", "Entrez le nb d'annonces à publier",
				"Votre réponse", "doit être un entier positif");
		String path = readConsoleInput("^mine$|client", "Entrez le répertoire des annonces",
				"Votre réponse", "doit être mine ou client");
		String mail = readConsoleInput(".*", "Entrez l'adresse mail du comte",
				"Votre réponse", "doit être un mail");
		String mdp = readConsoleInput(".*", "Entrez le mdp de LBC",
				"Votre réponse", "doit être un mdp LBC");
		
		agentLBC = new AgentLBC(path, Integer.parseInt(nbAnnonces),mail,mdp);
		agentLBC.publish();
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
