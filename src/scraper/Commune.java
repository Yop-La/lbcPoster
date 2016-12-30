package scraper;

public class Commune {
	private String codeDep;
	private String codeCommune;
	private String nomCommune;
	private Float popTotale;
	private String codeReg;
	private String nomReg;
	public Commune(String codeDep, String codeCommune, String nomCommune, Float popTotale, String codeReg,
			String nomReg) {
		super();
		this.codeDep = codeDep;
		this.codeCommune = codeCommune;
		this.nomCommune = nomCommune;
		this.popTotale = popTotale;
		this.codeReg = codeReg;
		this.nomReg = nomReg;
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
	public String getNomCommune() {
		return nomCommune;
	}
	public void setNomCommune(String nomCommune) {
		this.nomCommune = nomCommune;
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
	public String toString(){
		return this.nomCommune;
	}
	
}
