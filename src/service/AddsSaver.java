package service;

import java.util.List;

import fr.doodle.dao.CommuneDao;
import fr.doodle.dao.TexteDao;
import fr.doodle.dao.TitreDao;
import scraper.Add;
import scraper.Commune;
import scraper.Texte;
import scraper.Title;

public class AddsSaver {
	private List<Add> addsControled;
	private List<Add> addsReadyTosave;
	private CommuneDao communeDao;
	private TitreDao titreDao;
	private TexteDao texteDao;

	public AddsSaver(List<Add> addsControled) {
		super();
		this.addsControled = addsControled;
		communeDao = new CommuneDao();
		titreDao = new TitreDao();
		texteDao = new TexteDao();
	}

	public List<Add> getAddsControled() {
		return addsControled;
	}

	public void setAddsControled(List<Add> addsControled) {
		this.addsControled = addsControled;
	}

	public void prepareAddsToSaving(){
		for(Add add : addsControled){
			// préparer les communes
			Commune communeComplete = communeDao.findOneWithNomCommune(add.getCommune()); 
			add.setCommune(communeComplete);

			// préparer les titres
			Title titreComplet = titreDao.findOneWithTitre(add.getTitle()); 
			add.setTitle(titreComplet);

			// préparer les textes
			Texte texteComplet = texteDao.findOneWithTexte(add.getTexte()); 
			add.setTexte(texteComplet);
			
			addsReadyTosave = addsControled;
		}
	}

}
