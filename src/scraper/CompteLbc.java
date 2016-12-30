package scraper;

public class CompteLbc {
	private String mail;
	private String password;
	private int idAdmin;
	
	public CompteLbc(String mail, String password) {
		super();
		this.mail = mail;
		this.password = password;
		idAdmin = -1;
	}
	public CompteLbc(String mail, String password, int idAdmin) {
		super();
		this.mail = mail;
		this.password = password;
		this.idAdmin = idAdmin;
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

	public int getIdAdmin() {
		return idAdmin;
	}

	public void setIdAdmin(int idAdmin) {
		this.idAdmin = idAdmin;
	}
	
	
	
}
