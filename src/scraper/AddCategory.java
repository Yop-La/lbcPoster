package scraper;

public enum AddCategory {

	CoursParticuliers("Cours particuliers"),
	Animaux("Animaux"),
	Prestations("Prestations de services"),
	AccessoiresBagagerie("Accessoires & Bagagerie");
	private String category;

	private AddCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}
	
}
