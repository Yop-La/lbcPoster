package service;

import java.util.List;

import fr.doodle.dao.CommuneDao;
import scraper.Add;
import scraper.Commune;

public class AddsSaver {
	private List<Add> addsControled;
	private List<Add> addsReadyTosave;
	private CommuneDao communeDao;

	public AddsSaver(List<Add> addsControled) {
		super();
		this.addsControled = addsControled;
		communeDao = new CommuneDao();
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
			Commune commune = add.getCommune();
			commune = communeDao.findOneWithNomCommune(commune);

			// préparer les titres

			// préparer les textes
			
			addsReadyTosave = addsControled;
		}
	}

}
