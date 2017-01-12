package scraper;

public class CompteLbc {
	private String mail;
	private String password;
	private int refCompte;
	
	public CompteLbc(String mail, String password) {
		super();
		this.mail = mail;
		this.password = password;
		refCompte = -1;
	}
	public CompteLbc(String mail, String password, int idAdmin) {
		super();
		this.mail = mail;
		this.password = password;
		this.refCompte = idAdmin;
	}
	

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRefCompte() {
		return refCompte;
	}

	public void setRefCompte(int idAdmin) {
		this.refCompte = idAdmin;
	}
	
	
	
}
