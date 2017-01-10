package scraper;

public class Commune {
	private String codeDep;
	private String codeCommune;
	private String nomCommuneInBase;
	private String nomCommuneOnLbc;
	private Float popTotale;
	private String codeReg;
	private String nomReg;
	private String CodePostal;
	private int refCommune;
	
	public Commune(){
	}
	
	public Commune(String codeDep, String codeCommune, String nomCommuneInBase, Float popTotale, String codeReg,
			String nomReg, int refCommune) {
		super();
		this.codeDep = codeDep;
		this.codeCommune = codeCommune;
		this.nomCommuneInBase = nomCommuneInBase;
		this.popTotale = popTotale;
		this.codeReg = codeReg;
		this.nomReg = nomReg;
		this.refCommune = refCommune;
	}

	public Commune(String nomCommuneOnLbc) {
		super();
		this.nomCommuneOnLbc = nomCommuneOnLbc;

	}
	
	
	public int getRefCommune() {
		return refCommune;
	}

	public void setRefCommune(int refCommune) {
		this.refCommune = refCommune;
	}

	public String getCodePostal() {
		return CodePostal;
	}

	public void setCodePostal(String codePostal) {
		CodePostal = codePostal;
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

	public String getNomCommuneInBase() {
		return nomCommuneInBase;
	}

	public void setNomCommuneInBase(String nomCommuneInBase) {
		this.nomCommuneInBase = nomCommuneInBase;
	}

	public String getNomCommuneOnLbc() {
		return nomCommuneOnLbc;
	}

	public void setNomCommuneOnLbc(String nomCommuneOnLbc) {
		this.nomCommuneOnLbc = nomCommuneOnLbc;
	}
	
	
	
}
