package mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;



import dao.CompteLbcDao;
import scraper.Client;
import scraper.CompteLbc;

public class testMail {

	public static void main(String args[]) {
		CompteLbcDao compteLbcDao = new CompteLbcDao();
		Client client = new Client();
		client.setRefClient(2);
		Map<Integer,CompteLbc> comptes = compteLbcDao.findAll(client);
		Set<String> allMailsFromLbcWithAnswer = new HashSet<String>();
		Set<String> allMailsFromLbcWithoutAnswer = new HashSet<String>();
		for(CompteLbc compte : comptes.values()){
			Set<String> mailsFromLbc  = new HashSet<String>();
			Set<String> mailsWithAnswers = new HashSet<String>();




			System.out.println("-----------"+compte.getMail().toString()+"----------");
			mailsFromLbc = getMailsOfLbc(compte.getMail(),compte.getPassword());
			mailsWithAnswers = getAllSent(compte.getMail(),compte.getPassword());

			
			int nbMailsFromLbcWithAnswer = 0;
			for(String mailAnsweringToAdds : mailsFromLbc){
				if(mailsWithAnswers.contains(mailAnsweringToAdds)){
					allMailsFromLbcWithAnswer.add(mailAnsweringToAdds);
					System.out.println("mail avec rep : "+mailAnsweringToAdds);
					nbMailsFromLbcWithAnswer++;
				}else{
					allMailsFromLbcWithoutAnswer.add(mailAnsweringToAdds);
					System.out.println("mail sans rep : "+mailAnsweringToAdds);
				}
			}
			System.out.println("Nb de mails de répondants à annonces : "+mailsFromLbc.size());
			System.out.println("Nb de mails de répondants à annonces avec réponse : "+nbMailsFromLbcWithAnswer);

		}
		System.out.println(" --- bilan --- ");
		System.out.println("nb mails du bon coin sans rép : " + allMailsFromLbcWithoutAnswer.size());
		System.out.println("nb mails du bon coin avec rép : " + allMailsFromLbcWithAnswer.size());
		writeMailsToFile(allMailsFromLbcWithAnswer,"withAnswer.txt");
		writeMailsToFile(allMailsFromLbcWithoutAnswer,"withoutAnswer.txt");

	}

	static public void writeMailsToFile(Set<String> mails, String nomFichier){
		try{
			PrintWriter writer = new PrintWriter("D:\\Dropbox\\HelloMentor\\Gestion Annonces\\mails du bon coin\\"+nomFichier, "UTF-8");
			for(String mail : mails){
				writer.println(mail);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public Set<String> getMailsOfLbc(String adresseClient, String password){
		Set<String> mails = new HashSet<String>();
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");

			// IMAP host for yahoo
			store.connect("imap.mail.yahoo.com", adresseClient, "motdepassethor");

			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_ONLY);

			BufferedReader optionReader = new BufferedReader(new InputStreamReader(System.in));
			//System.out.println("Press (U) to get only unread mails OR Press (A) to get all mails:");
			try {
				//char answer = (char) optionReader.read();
				//if(answer=='A' || answer=='a'){
				mails = getAllMailsFromLbc(inbox);
				//}else if(answer=='U' || answer=='u'){
				//	showUnreadMails(inbox);
				//

				optionReader.close();
			} catch (IOException e) {
				System.out.println(e);
			}

		} catch (NoSuchProviderException e) {
			System.out.println(e.toString());
			System.exit(1);
		} catch (MessagingException e) {
			System.out.println(e.toString());
			System.exit(2);
		}
		return mails;
	}

	static public Set<String> getAllSent(String adresseClient, String password){
		Set<String> mails = new HashSet<String>();
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");

			// IMAP host for yahoo
			store.connect("imap.mail.yahoo.com", adresseClient, "motdepassethor");

			Folder inbox = store.getFolder("Sent");
			inbox.open(Folder.READ_ONLY);

			BufferedReader optionReader = new BufferedReader(new InputStreamReader(System.in));
			//System.out.println("Press (U) to get only unread mails OR Press (A) to get all mails:");
			try {
				//char answer = (char) optionReader.read();
				//if(answer=='A' || answer=='a'){
				mails = getAllSent(inbox);
				//}else if(answer=='U' || answer=='u'){
				//	showUnreadMails(inbox);
				//

				optionReader.close();
			} catch (IOException e) {
				System.out.println(e);
			}

		} catch (NoSuchProviderException e) {
			System.out.println(e.toString());
			System.exit(1);
		} catch (MessagingException e) {
			System.out.println(e.toString());
			System.exit(2);
		}
		return mails;
	}



	private static Set<String> getAllSent(Folder inbox) {

		Set<String> mailsWithAnswer = new HashSet<String>();

		try {
			Message msg[] = inbox.getMessages();
			System.out.println("MAILS: "+msg.length);

			// première boucle pour récupérer les mails répondant aux annonces
			for(Message message:msg) {
				try {
					String recipient = message.getAllRecipients()[0].toString();
					// pour récupérer le mail de from
					Pattern p = Pattern.compile(".*<(.+)>.*");
					Matcher m = p.matcher(recipient);
					// lancement de la recherche de toutes les occurrences
					boolean b = m.matches();
					// si recherche fructueuse
					if(b) {
						String mailFromLbc = m.group(1);
						mailsWithAnswer.add(mailFromLbc);
					}	
				}
				catch (Exception e) {
					System.out.println("No Information");
				}
			}
		} catch (MessagingException e) {
			System.out.println(e.toString());
		}

		return mailsWithAnswer;

	}

	static public void showUnreadMails(Folder inbox){        
		try {
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			Message msg[] = inbox.search(ft);
			System.out.println("MAILS: "+msg.length);
			for(Message message:msg) {
				try {
					System.out.println("DATE: "+message.getSentDate().toString());
					System.out.println("FROM: "+message.getFrom()[0].toString());            
					System.out.println("SUBJECT: "+message.getSubject().toString());
					System.out.println("CONTENT: "+message.getContent().toString());

					System.out.println("******************************************");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("No Information");
				}
			}
		} catch (MessagingException e) {
			System.out.println(e.toString());
		}
	}

	static public Set<String> getAllMailsFromLbc(Folder inbox){
		Set<String> mailsAnsweringToAdds = new HashSet<String>();

		try {
			Message msg[] = inbox.getMessages();
			System.out.println("MAILS: "+msg.length);

			// première boucle pour récupérer les mails répondant aux annonces
			for(Message message:msg) {
				try {
					Pattern pattern1 = Pattern.compile("a répondu à votre annonce"); // contenu du titre du mail
					Pattern pattern2 = Pattern.compile("no.reply@leboncoin.fr"); // expéditeur du mail
					String subject = message.getSubject().toString();
					Matcher matcher1 = pattern1.matcher(subject);
					String from = message.getFrom()[0].toString();
					Matcher matcher2 = pattern2.matcher(from);
					if(matcher1.find() & matcher2.find()){
						// pour récupérer le mail de from
						Pattern p = Pattern.compile(".*<(.+)>.*");
						String replyTo = message.getReplyTo()[0].toString();
						Matcher m = p.matcher(replyTo);
						// lancement de la recherche de toutes les occurrences
						boolean b = m.matches();
						// si recherche fructueuse
						if(b) {
							String mailFromLbc = m.group(1);
							mailsAnsweringToAdds.add(mailFromLbc);
						}	
					}

				} catch (Exception e) {
					System.out.println("No Information");
				}
			}

			/*	// boucle pour transformer la liste de mails répondants aux adds en reg

			Iterator<String> it = mailsAnsweringToAdds.iterator();
			String regexToIdentifyMail="a{200,300}";
			if(it.hasNext())
				regexToIdentifyMail=it.next();
			while(it.hasNext()){
				regexToIdentifyMail = regexToIdentifyMail+"|"+it.next(); 
			}
			System.out.println("regex : "+regexToIdentifyMail);*/

		} catch (MessagingException e) {
			System.out.println(e.toString());
		}

		return mailsAnsweringToAdds;
	}


	public static void sendMail(){
		// Sender's email ID needs to be mentioned
		String from = "methodethor01@yahoo.com";
		String pass ="motdepassethor";
		// Recipient's email ID needs to be mentioned.
		String to = "alex.guillemine@gmail.com";
		String host = "smtp.mail.yahoo.com";

		// Get system properties
		Properties properties = System.getProperties();
		// Setup mail server
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", from);
		properties.put("mail.smtp.password", pass);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try{
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(to));

			// Set Subject: header field
			message.setSubject("This is the Subject Line!");

			// Now set the actual message
			message.setText("This is actual message");

			// Send message
			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			System.out.println("Sent message successfully....");
		}catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
