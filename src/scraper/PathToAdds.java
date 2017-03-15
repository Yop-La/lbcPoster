package scraper;

public enum PathToAdds {
	
	MINE("C:\\Users\\robot\\git\\lbcPoster\\documents pour robots\\"),  
	CLIENT("C:\\Users\\robot\\git\\lbcPoster\\documents pour robots pour clients\\");
	private String path;
	
	private PathToAdds(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	
}
