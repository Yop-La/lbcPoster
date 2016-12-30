package scraper;

public enum PathToAdds {
	
	MINE("D:\\Dropbox\\HelloMentor\\Gestion Annonces\\automatisation dépôt annonces\\documents pour robots\\"),  
	CLIENT("D:\\Dropbox\\HelloMentor\\Gestion Annonces\\automatisation dépôt annonces\\clients\\");
	private String path;
	
	private PathToAdds(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	
}
