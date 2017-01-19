package scraper;

import org.apache.commons.lang3.StringUtils;

import fr.doodle.dao.CommuneDao;
// 36639 communes
public class Commune {
	private String codeDep;
	private String codeCommune;
	private String nomCommune;
	private Float popTotale;
	private String codeReg;
	private String nomReg;
	private String codePostal="";
	private int refCommune=-1;
	
	public Commune(){
	}
	
	public Commune(String codeDep, String codeCommune, String nomCommune, Float popTotale, String codeReg,
			String nomReg, int refCommune, String codePostal) {
		super();
		this.codeDep = codeDep;
		this.codeCommune = codeCommune;
		this.nomCommune = nomCommune;
		this.popTotale = popTotale;
		this.codeReg = codeReg;
		this.nomReg = nomReg;
		this.refCommune = refCommune;
		this.codePostal = codePostal;
	}

	public String toString(){
		String retour="";
		retour = "In base : "+nomCommune;
		return retour;
	}

	public Commune(String nomCommune) {
		super();
		this.nomCommune = nomCommune;
	}
	

	public String getCodeDep() {
		return codeDep;
	}
	public void setCodeDep(String codeDep) {
		this.codeDep = codeDep;
	}
	public String getCodeCommune() {
		return codeCommune;
	}
	public void setCodeCommune(String codeCommune) {
		this.codeCommune = codeCommune;
	}
	public Float getPopTotale() {
		return popTotale;
	}
	public void setPopTotale(Float popTotale) {
		this.popTotale = popTotale;
	}
	public String getCodeReg() {
		return codeReg;
	}
	public void setCodeReg(String codeReg) {
		this.codeReg = codeReg;
	}
	public String getNomReg() {
		return nomReg;
	}
	public void setNomReg(String nomReg) {
		this.nomReg = nomReg;
	}



	public String getNomCommune() {
		return nomCommune;
	}

	public void setNomCommune(String nomCommune) {
		this.nomCommune = nomCommune;
	}

	public String getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public int getRefCommune() {
		return refCommune;
	}

	public void setRefCommune(int refCommune) {
		this.refCommune = refCommune;
	}
	
	public void printCommune() {
		System.out.println("  ---- ref commune : "+getRefCommune()+" ---- ");
		System.out.println("    - nom commune : "+getNomCommune());
		System.out.println("    - code dep : "+getCodeDep());
		System.out.println("    - code commune : "+getCodeCommune());
		System.out.println("    - population : "+getPopTotale());
		System.out.println("    - code reg : "+getCodeReg());
		System.out.println("    - nom reg : "+getNomReg());
		System.out.println("    - code postal : "+getCodePostal());
	}

	public void updateCodePostal() {
		CommuneDao communeDao = new CommuneDao();
		communeDao.updateCodePostal(this);
	}

	public void updateNom() {
		CommuneDao communeDao = new CommuneDao();
		communeDao.updateNomCommune(this);
		
	}

	
	
}
