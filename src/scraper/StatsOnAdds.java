package scraper;
import java.util.HashMap;
import java.util.List;

import dao.AddDao;
import dao.CommuneDao;

public class StatsOnAdds {
	private List<Add> clientAddsOnline;
	private HashMap<Integer, StatsOnCompte> statsOnClientAddsOnline = new HashMap<Integer, StatsOnCompte>();

	public StatsOnAdds(Client clientInUse) {
		AddDao addDao = new AddDao();
		clientAddsOnline = addDao.findAll(clientInUse);
	}


	public HashMap<Integer, StatsOnCompte> getStatsOnClientAddsOnline() {
		CommuneDao comDao = new CommuneDao();
		for(Add add : clientAddsOnline){
			int refCompte = add.getCompteLbc().getRefCompte();
			if(!statsOnClientAddsOnline.containsKey(refCompte)){
				StatsOnCompte statsOnCompte = new StatsOnCompte();
				statsOnClientAddsOnline.put(refCompte , statsOnCompte);
			}
			float pop = comDao.findOne(add.getCommuneLink().onLine.getRefCommune()).getPopTotale();
			StatsOnCompte statsOnCompte = statsOnClientAddsOnline.get(refCompte);
			statsOnCompte.addPopMoyenneAddOnline(pop);
			int nbJourOnline = 60 - add.getNbJoursRestants();
			statsOnCompte.addNbClicMoyenParJourOnline(add.getNbClickTel()/(float)nbJourOnline);
			statsOnCompte.addNbMailMoyenParJourOnline(add.getNbMailsRecus()/(float)nbJourOnline);
			statsOnCompte.addNbTotaleClickOnline(add.getNbClickTel());
			statsOnCompte.addNbTotaleMailOnline(add.getNbMailsRecus());
			statsOnCompte.addNbVuesMoyenParJourOnline(add.getNbVues()/(float)nbJourOnline);
			statsOnCompte.addNbJourMoyenOnline(nbJourOnline);
			statsOnCompte.incrementNbAddsOnline();
		}
		return statsOnClientAddsOnline;
	}

}
