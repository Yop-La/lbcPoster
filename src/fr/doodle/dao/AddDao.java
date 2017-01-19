package fr.doodle.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import exception.MultipleCorrespondanceAddException;
import scraper.Add;
import scraper.Commune;
import scraper.CompteLbc;
import scraper.EtatAdd;

public class AddDao extends JdbcRepository<Add, Integer> {

	@Override
	public List<Add> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	// pour sauvegarder les annonce soumises au bon coin et celles en ligne sur le bon coin
	public Add save(Add entity, boolean saveCommuneOnline) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("insert into adds_lbc("
							+ "	nb_vues,"
							+ " nb_clic_tel,"
							+ " nb_mails,"
							+ " date_mise_en_ligne,"
							+ "	nb_jours_restants,"
							+ " ref_commune,"
							+ " ref_titre,"
							+ " ref_compte,"
							+ " ref_texte,"
							+ " nb_controls,"
							+ " date_controle, "
							+ " etat)"
							+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS)){
				statement.setInt(1, entity.getNbVues());
				statement.setInt(2, entity.getNbClickTel());
				statement.setInt(3, entity.getNbMailsRecus());
				Calendar dateMiseEnLigne = entity.getDateMiseEnLigne();
				java.sql.Date dateMiseEnligneSql=null;
				java.sql.Date dateOfTheDay = null;
				int nbControl = 0;
				if(dateMiseEnLigne!=null){// si sauvegardes annonces en ligne sur lbc
					dateMiseEnligneSql = new java.sql.Date(entity.getDateMiseEnLigne().getTime().getTime());
					nbControl = 1;
					Date today = new Date();
					dateOfTheDay = new java.sql.Date(today.getTime());
				}
				statement.setDate(4, dateMiseEnligneSql);
				statement.setInt(5, entity.getNbJoursRestants());
				if(saveCommuneOnline){
					statement.setInt(6, entity.getCommuneLink().onLine.getRefCommune());
				}else{
					statement.setInt(6, entity.getCommuneLink().submit.getRefCommune());
				}


				statement.setInt(7, entity.getTitle().getRefTitre());
				statement.setInt(8, entity.getCompteLbc().getRefCompte());
				statement.setInt(9, entity.getTexte().getRefTexte());
				statement.setInt(10, nbControl);

				statement.setDate(11, dateOfTheDay); // vaut null si sauvegarde d'une annonce soumise à la modération
				statement.setString(12, entity.getEtat().toString());
				statement.executeUpdate();
				ResultSet rs = statement.getGeneratedKeys();
				if(rs.next()){
					entity.setRefAdd(rs.getInt(1));
					entity.setAddNotReferenced(false);
				}
				return entity;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return entity;
		}
	}

	public Add saveAddsNotReferenced(Add addToSave){
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("insert into adds_lbc("
							+ "	nb_vues,"
							+ " nb_clic_tel,"
							+ " nb_mails,"
							+ "	nb_jours_restants,"
							+ " ref_titre,"
							+ " ref_compte,"
							+ " ref_texte,"
							+ " nb_controls,"
							+ " etat)"
							+ " values(0, 0, 0, 0, ?, ?, ?, 1, 'enAttenteModeration')",Statement.RETURN_GENERATED_KEYS)){
				statement.setInt(1, addToSave.getTitle().getRefTitre());
				statement.setInt(2, addToSave.getCompteLbc().getRefCompte());
				statement.setInt(3, addToSave.getTexte().getRefTexte());
				statement.executeUpdate();
				ResultSet rs = statement.getGeneratedKeys();
				if(rs.next()){
					addToSave.setRefAdd(rs.getInt(1));
					addToSave.setAddNotReferenced(false);
				}
				return addToSave;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return addToSave;
		}
	}

	public void update(Add entity) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update adds_lbc"
							+ "	set nb_vues = ?, "
							+ " nb_clic_tel = ?,"
							+ " nb_mails = ?,"
							+ " date_mise_en_ligne = ?,"
							+ "	nb_jours_restants = ?,"
							+ " nb_controls  = ?, "
							+ " date_controle = ?, "
							+ " etat = ?"
							+ " where ref_add = ?",Statement.RETURN_GENERATED_KEYS)){
				statement.setInt(1, entity.getNbVues());
				statement.setInt(2, entity.getNbClickTel());
				statement.setInt(3, entity.getNbMailsRecus());
				statement.setDate(4, new java.sql.Date(entity.getDateMiseEnLigne().getTime().getTime()));
				statement.setInt(5, entity.getNbJoursRestants());
				statement.setInt(6, entity.getNbControle());
				statement.setString(8, entity.getEtat().toString());
				statement.setInt(9, entity.getRefAdd());
				Date dateOfTheDay = new Date();
				statement.setDate(7, new java.sql.Date(dateOfTheDay.getTime()));
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public void updateRefCommune(Add entity) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update adds_lbc"
							+ "	set ref_commune = ?"
							+ " where ref_add = ?")){
				statement.setInt(1, entity.getCommuneLink().onLine.getRefCommune());
				statement.setInt(2, entity.getRefAdd());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	

	@Override
	public Add findOne(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Add entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}
	public int countNumberOfRef(Add add){
		int nb_corresp=0;
		try(Connection maConnection = getConnection()){	
			// comptons le nb d'adds correspondant
			try(PreparedStatement statement = 
					maConnection.prepareStatement("select count(*) from adds_lbc where "
							+ " ref_titre = ? AND"
							+ " ref_compte = ? AND"
							+ " ref_texte = ? AND"
							+ " etat IN ('enAttenteModeration','onLine')")){
				statement.setInt(1, add.getTitle().getRefTitre());
				statement.setInt(2, add.getCompteLbc().getRefCompte());
				statement.setInt(3, add.getTexte().getRefTexte());
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					nb_corresp=rs.getInt(1);
				}
			}
			return nb_corresp;
		}catch(SQLException e){
			e.printStackTrace();
			return 0;
		}
	}
	// est utilisé pour récupérer une add dans la bdd à partir de cette même add du bon coin (cas d'un deuxième contrôle)
	public Add findOneAddFromLbcAndSetSubmitCommune(Add addToFind) throws MultipleCorrespondanceAddException{
		int nb_corresp=0;
		try(Connection maConnection = getConnection()){	

			// comptons le nb d'adds correspondant
			try(PreparedStatement statement = 
					maConnection.prepareStatement("select count(*) from adds_lbc where "
							+ " ref_titre = ? AND"
							+ " ref_compte = ? AND"
							+ " ref_texte = ? AND"
							+ " etat IN ('enAttenteModeration','onLine')")){
				statement.setInt(1, addToFind.getTitle().getRefTitre());
				statement.setInt(2, addToFind.getCompteLbc().getRefCompte());
				statement.setInt(3, addToFind.getTexte().getRefTexte());
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					nb_corresp=rs.getInt(1);

				}
			}
			switch (nb_corresp) {
			case 0:
				addToFind.setAddNotReferenced(true);
				return addToFind;
			case 1:
				try(PreparedStatement statement = 
				maConnection.prepareStatement("select ref_add, nb_controls, ref_commune from adds_lbc where "
						+ " ref_titre = ? AND"
						+ " ref_compte = ? AND"
						+ " ref_texte = ? AND"
						+ " etat IN ('enAttenteModeration','onLine')")){
					//statement.setInt(1, addToFind.getCommune().getRefCommune());
					statement.setInt(1, addToFind.getTitle().getRefTitre());
					statement.setInt(2, addToFind.getCompteLbc().getRefCompte());
					statement.setInt(3, addToFind.getTexte().getRefTexte());
					ResultSet rs = statement.executeQuery();
					if (rs.next()) {
						addToFind.setRefAdd(rs.getInt(1));
						addToFind.setNbControle(rs.getInt(2));
						CommuneDao comDao = new CommuneDao();
						addToFind.getCommuneLink().submit = comDao.findOne(rs.getInt(3));
						addToFind.setAddNotReferenced(false);
					}
					return addToFind;
				}
			default:
				addToFind.setAddNotReferenced(false);
				throw new MultipleCorrespondanceAddException();// si il y a plus de 2 annonces correspondantes
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}


	}
	public List<Add> findAddsWithHerState(Add addToFind) {
		List<Add> retour = new ArrayList<Add>();
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("select ref_add, nb_controls from adds_lbc where "
							+ " ref_compte = ? AND"
							+ " etat = ?")){
				statement.setInt(1, addToFind.getCompteLbc().getRefCompte());
				statement.setString(2, addToFind.getEtat().toString());
				ResultSet rs = statement.executeQuery();
				while(rs.next()) {
					Add addFromBdd = new Add();
					addFromBdd.setRefAdd(rs.getInt(1));
					addFromBdd.setEtat(addToFind.getEtat());
					addFromBdd.setCompteLbc(addToFind.getCompteLbc());
					addFromBdd.setNbControle(rs.getInt(2));
					retour.add(addFromBdd);
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public void updateStateAndNbControlAndDateControl(Add addToUpdate) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update adds_lbc"
							+ " set etat = ?,"
							+ " nb_controls = ?,"
							+ " date_controle = ?"
							+ " where ref_add = ?")){
				Date today = new Date();
				java.sql.Date dateOfTheDay = new java.sql.Date(today.getTime());
				statement.setString(1, addToUpdate.getEtat().toString());
				statement.setInt(2, addToUpdate.getNbControle()+1);
				statement.setDate(3, dateOfTheDay);
				statement.setInt(4, addToUpdate.getRefAdd());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

	}

	public List<Add> findAddsNotOnlineAnymore(Add add) {
		List<Add> retour = new ArrayList<Add>();
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("select ref_add, nb_controls from adds_lbc where "
							+ " ref_compte = ? AND"
							+ " etat = 'onLine' AND"
							+ " (date_controle < ? or date_controle is NULL)")){
				statement.setInt(1, add.getCompteLbc().getRefCompte());
				Date dateOfTheDay = new Date();
				statement.setDate(2, new java.sql.Date(dateOfTheDay.getTime()));
				ResultSet rs = statement.executeQuery();
				while(rs.next()) {
					Add addFromBdd = new Add();
					addFromBdd.setRefAdd(rs.getInt(1));
					addFromBdd.setNbControle(rs.getInt(2));
					retour.add(addFromBdd);
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public Integer[] putAllsAddsNotOnline(CompteLbc compte) {
		int nbAnnoncesOnline=0; // le nb d'annonces qui était en ligne
		int nbAnnoncesEnAttenteDeModération=0; // le nb d'annonces qui était en attente de modération
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("select nb_controls, ref_add, etat from adds_lbc where "
							+ " etat in ('enAttenteModeration','onLine') AND "
							+ " ref_compte = ? ")){
				statement.setInt(1, compte.getRefCompte());
				ResultSet rs = statement.executeQuery();
				while(rs.next()) {
					if(rs.getString(3).equals("enAttenteModeration")){
						nbAnnoncesEnAttenteDeModération++;
					}else if(rs.getString(3).equals("onLine")){
						nbAnnoncesOnline++;
					}

					try(PreparedStatement statementBis = 
							maConnection.prepareStatement("update adds_lbc "
									+ " set etat = 'notOnLineAnymore',"
									+ " nb_controls = ?,"
									+ " date_controle = ?"
									+ " where ref_add = ?")){
						Date today = new Date();
						java.sql.Date dateOfTheDay = new java.sql.Date(today.getTime());
						statementBis.setInt(1, rs.getInt(1)+1);
						statementBis.setDate(2, dateOfTheDay);
						statementBis.setInt(3, rs.getInt(2));
						statementBis.executeUpdate();
					}
				}
			}
			Integer[] retour =  new Integer[2];
			retour[0] = nbAnnoncesOnline;
			retour[1] = nbAnnoncesEnAttenteDeModération;
			return(retour);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}

	}

	public Add setSubmitCommuneAndRefAdd(Add addReferencedOnce) {
		try(Connection maConnection = getConnection()){	

				try(PreparedStatement statement = 
				maConnection.prepareStatement("select ref_add, nb_controls, ref_commune from adds_lbc where "
						+ " ref_titre = ? AND"
						+ " ref_compte = ? AND"
						+ " ref_texte = ? AND"
						+ " etat IN ('enAttenteModeration','onLine')")){
					//statement.setInt(1, addToFind.getCommune().getRefCommune());
					statement.setInt(1, addReferencedOnce.getTitle().getRefTitre());
					statement.setInt(2, addReferencedOnce.getCompteLbc().getRefCompte());
					statement.setInt(3, addReferencedOnce.getTexte().getRefTexte());
					ResultSet rs = statement.executeQuery();
					if (rs.next()) {
						addReferencedOnce.setRefAdd(rs.getInt(1));
						addReferencedOnce.setNbControle(rs.getInt(2));
						CommuneDao comDao = new CommuneDao();
						addReferencedOnce.getCommuneLink().submit = comDao.findOne(rs.getInt(3));
					}
					return addReferencedOnce;
				}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}

	}

}
