package scraper;

public enum AddCategory {

	CoursParticuliers("Cours particuliers"),
	Prestations("Prestations de services");
	private String category;

	private AddCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}
	
}
