package scraper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CompteLbc {
	private String mail;
	private String password;
	private int refCompte;
	private Calendar dateDernierControle;
	private int nbAnnoncesEnLigne;
	private String pseudo;
	private boolean redirection;
	private Calendar dateDerniereActivite;
	private Calendar dateAvantPeremption;
	private boolean disabled;
	private Calendar dateOfDisabling;
	private int refClient;
	private boolean packBooster=false;
	private Calendar finPack=null;
	private String prenom="Alexandre";
	

	public CompteLbc(String mail, String password) {
		super();
		this.mail = mail;
		this.password = password;
		refCompte = -1;
	}
	public CompteLbc(String mail, String password, int idAdmin) {
		super();
		this.mail = mail;
		this.password = password;
		this.refCompte = idAdmin;
	}
	public CompteLbc(){
		
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRefCompte() {
		return refCompte;
	}

	public void setRefCompte(int idAdmin) {
		this.refCompte = idAdmin;
	}
	public Calendar getDateDernierControle() {
		return dateDernierControle;
	}
	public int getNbAnnoncesEnLigne() {
		return nbAnnoncesEnLigne;
	}
	public void setDateDernierControle(Calendar dateDernierControle) {
		this.dateDernierControle = dateDernierControle;
	}
	public void setNbAnnoncesEnLigne(int nbAnnoncesEnLigne) {
		this.nbAnnoncesEnLigne = nbAnnoncesEnLigne;
	}
	
	public String getPrintableDateOfLastControl(){
		if(dateDernierControle!=null){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return(sdf.format(this.dateDernierControle.getTime()));
		}else{
			return("Date pas définie");
		}
	}
	
	public String getPrintableEndPack(){
		if(this.finPack!=null){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return(sdf.format(this.finPack.getTime()));
		}else{
			return("Date pas définie");
		}
	}
	
	public String getPrintableDateAvantPeremption(){
		if(dateAvantPeremption!=null){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return(sdf.format(this.dateAvantPeremption.getTime()));
		}else{
			return("Date pas définie");
		}
	}
	public String getPrintableDateDerniereAct(){
		if(dateDerniereActivite!=null){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return(sdf.format(this.dateDerniereActivite.getTime()));
		}else{
			return("Date pas définie");
		}
	}
	
	public boolean isPackBooster() {
		return packBooster;
	}
	public void setPackBooster(boolean packBooster) {
		this.packBooster = packBooster;
	}
	public Calendar getFinPack() {
		return finPack;
	}
	public void setFinPack(Calendar finPack) {
		this.finPack = finPack;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public boolean isRedirection() {
		return redirection;
	}
	public void setRedirection(boolean redirection) {
		this.redirection = redirection;
	}
	public Calendar getdateDerniereActivite() {
		return dateDerniereActivite;
	}
	public void setdateDerniereActivite(Calendar date_derniere_activite) {
		this.dateDerniereActivite = date_derniere_activite;
	}
	public Calendar getDateDerniereActivite() {
		return dateDerniereActivite;
	}
	public void setDateDerniereActivite(Calendar dateDerniereActivite) {
		this.dateDerniereActivite = dateDerniereActivite;
	}
	public Calendar getDateAvantPeremption() {
		return dateAvantPeremption;
	}
	public void setDateAvantPeremption(Calendar dateAvantPeremption) {
		this.dateAvantPeremption = dateAvantPeremption;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public Calendar getDateOfDisabling() {
		return dateOfDisabling;
	}
	public void setDateOfDisabling(Calendar dateOfDisabling) {
		this.dateOfDisabling = dateOfDisabling;
	}
	
	public boolean isDateDernierControleReachTheLimit(){
		Calendar dateLimite = Calendar.getInstance();
		dateLimite.add(Calendar.DAY_OF_MONTH, -30);
		if(this.dateDernierControle.before(dateLimite)){
			return true;
		}else{
			return false;
		}
	}
	public boolean isDateDerniereActiviteReachTheLimit(){
		Calendar dateLimite = Calendar.getInstance();
		dateLimite.add(Calendar.DAY_OF_MONTH, -10);
		if(this.dateDerniereActivite == null){
			return true;
		}
		if(this.dateDerniereActivite.before(dateLimite)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isDateAvantPeremptionReachTheLimit(){
		Calendar dateLimite = Calendar.getInstance();
		if(this.dateAvantPeremption.before(dateLimite)){
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean isThatCompteNeedsAnIntervention(){
		if(this.redirection==false){
			return true;
		}
		if(this.nbAnnoncesEnLigne==0){
			return true;
		}
		if(this.pseudo == null){
			return true;
		}
		if(isDateAvantPeremptionReachTheLimit()){
			return true;
		}
		if(isDateDernierControleReachTheLimit()){
			return true;
		}
		if(isDateDerniereActiviteReachTheLimit()){
			return true;
		}
		return false;
	}
	public int getRefClient() {
		return refClient;
	}
	public void setRefClient(int refClient) {
		this.refClient = refClient;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}	
}
