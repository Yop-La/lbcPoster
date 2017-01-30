package exception;

import java.util.List;

import scraper.Add;

public class AgentLbcFailPublicationException extends Exception {

	private int indiceAnnonceDechec;
	private List<Add> addsToPublish;
	

	public AgentLbcFailPublicationException(int indexAddPublication, List<Add> addsToPublish) {
		this.addsToPublish = addsToPublish;
		this.indiceAnnonceDechec = indexAddPublication;
	}

	public int getIndiceAnnonceDechec() {
		return indiceAnnonceDechec;
	}

	public List<Add> getAddsToPublish() {
		return addsToPublish;
	}
	
	
	
}
