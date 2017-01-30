package scraper;

import java.util.List;

import dao.CommuneDao;

public class CommuneLink {
	public Commune onLine;
	public Commune submit;
	public CaseOfMatching caseOfMatch;

	public boolean isNameEquals(){
		if(submit != null)
			return(onLine.getNomCommune().equals(submit.getNomCommune()));
		else
			return false;
	}


	public void printCommuneOnLine() {
		System.out.println("  Description de la Commune Online : ");
		onLine.printCommune();
	}
	public void printCommuneSubmit() {
		System.out.println("Description de la Commune soumise à lbc : ");
		if(submit!=null){
			submit.printCommune();
		}else{
			System.out.println("La commune soumise n'a pas été enregistré en base");
		}

	}
	public void printComparaison(){
		printCommuneOnLine();
		printCommuneSubmit();
	}
	public void codePostalGoToSubmit(){
		submit.setCodePostal(onLine.getCodePostal());
	}
	public void nomCommuneGoToSubmit(){
		submit.setNomCommune(onLine.getNomCommune());
	}

	public void setCaseCaseOfMatching(){
		// regardons le nb de correspondance en bdd de la commune en ligne
		CommuneDao comDao = new CommuneDao();
		String codeDep  = onLine.getCodePostal().substring(0, 2);
		if(codeDep.substring(0, 1).equals("0")){
			codeDep=codeDep.substring(1);
		}
		if(codeDep.equals("97")){
			codeDep=onLine.getCodePostal().substring(0, 3);
		}
		onLine.setCodeDep(codeDep);
		List<Commune> communesInBdd = comDao.findAllWithNameAndCodeDep(this.onLine);
		int nbCorrespondance = communesInBdd.size(); 
		if(nbCorrespondance==1){
			// si c'est une annonce contrôlé après modération, pas enregistré au moment de la publicatio
			if(submit==null){ 
				caseOfMatch = CaseOfMatching.noSubmitOneMatch;
			}else{//si c'est une annonce avec un submit en base
				if(isNameEquals()){ // nom égaux
					if(isCodePostEquals()){
						caseOfMatch = CaseOfMatching.perfectMatch;
					}else{
						caseOfMatch = CaseOfMatching.sameNameOneMatch;
					}
				}else{// nom pas égaux
					// c'est ce que la commune soumise au bon coin a donné une commune en ligne différence et ayant une seule ref en base
					// onLine and Submit sont donc différentes
					// Exemple : je tape commune1, c'est commune2 qui est retenu et elle n'a qu'une ref en base
					caseOfMatch = CaseOfMatching.differentNameOneMatch;
				}
			}
		}else if(nbCorrespondance>1){
			// si c'est une annonce contrôlé après modération, pas enregistré au moment de la publicatio
			if(submit==null){ 
				caseOfMatch = CaseOfMatching.noSubmitSeveralMatch;
				//si c'est une annonce avec un submit en base
			}else{
				if(isNameEquals()){
					// commune soumise au bon coin a donné une commune en ligne avec le même nom
					// onLine and Submit peuvent donc être différentes
					// mais commune en ligne a plusieurs correspondances en base
					// il faut donc relier commune en ligne à une de ses correspondances
					caseOfMatch = CaseOfMatching.sameNameSeveralMatch;
				}else{
					// commune soumise au bon coin a donné une commune différente en ligne
					//onLine and Submit sont donc différentes
					// mais commune en ligne a plusieurs correspondances en base
					caseOfMatch =  CaseOfMatching.differentNameSeveralMatch;
				}
			}
		}else if(nbCorrespondance==0){
			// si c'est une annonce contrôlé après modération, pas enregistré au moment de la publicatio
			if(submit==null){ 
				caseOfMatch = CaseOfMatching.noSubmitNoMatch;
			}else{
				// commune soumise au bon coin n'a pas ref en bdd
				// onLine and Submit sont donc différentes
				// mais commune en ligne n'a aucune correspondances en base
				caseOfMatch = CaseOfMatching.differentNameNoMatch;
			}
		}
	}


	public CaseOfMatching getCaseOfMatch() {
		setCaseCaseOfMatching();
		return caseOfMatch;
	}


	public boolean isNameAndCodePostEquals(){
		return(isCodePostEquals() & isNameEquals());
	}
	public boolean isCodePostEquals(){
		if(submit!=null)
			return(onLine.getCodePostal().equals(submit.getCodePostal()));
		else
			return false;
	}

}
