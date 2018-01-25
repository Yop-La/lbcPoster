package scraper;

public enum PathToAdds {
	
	MINE(".\\documents pour robots\\"),  
	CLIENT(".\\documents pour robots pour clients\\"),
	ECOM(".\\documents pour ecom\\");
	private String path;
	
	private PathToAdds(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	
}
