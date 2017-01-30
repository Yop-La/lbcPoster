package scraper;

public class Client {
	private int refClient;
	private String nomClient;
	private String prenomClient;
	
	public int getRefClient() {
		return refClient;
	}
	public void setRefClient(int ref_client) {
		this.refClient = ref_client;
	}
	public String getNomClient() {
		return nomClient;
	}
	public void setnomClient(String nom_client) {
		this.nomClient = nom_client;
	}
	public String getPrenomClient() {
		return prenomClient;
	}
	public void setPrenomClient(String prenom_client) {
		this.prenomClient = prenom_client;
	}
	
}
