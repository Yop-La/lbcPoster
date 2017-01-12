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
import scraper.EtatAdd;

public class AddDao extends JdbcRepository<Add, Integer> {

	@Override
	public List<Add> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	// pour sauvegarder les annonce soumises au bon coin et celles en ligne sur le bon coin
	public Add save(Add entity) {
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
				int nbControl = 0;
				if(dateMiseEnLigne!=null){// si sauvegardes annonces en ligne sur lbc
					dateMiseEnligneSql = new java.sql.Date(entity.getDateMiseEnLigne().getTime().getTime());
					nbControl = 1;
				}
				statement.setDate(4, dateMiseEnligneSql);
				statement.setInt(5, entity.getNbJoursRestants());
				statement.setInt(6, entity.getCommune().getRefCommune());
				statement.setInt(7, entity.getTitle().getRefTitre());
				statement.setInt(8, entity.getCompteLbc().getRefCompte());
				statement.setInt(9, entity.getTexte().getRefTexte());
				statement.setInt(10, nbControl);
				Date dateOfTheDay = new Date();
				statement.setDate(11, new java.sql.Date(dateOfTheDay.getTime()));
				statement.setString(12, entity.getEtat().toString());
				statement.executeUpdate();
				ResultSet rs = statement.getGeneratedKeys();

				return entity;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return entity;
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

	// est utilisé pour récupérer une add dans la bdd à partir de cette même add du bon coin (cas d'un deuxième contrôle)
	public Add findOneAddFromLbc(Add addToFind) throws MultipleCorrespondanceAddException{
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("select ref_add, nb_controls from adds_lbc where "
							+ " ref_commune = ? AND"
							+ " ref_titre = ? AND"
							+ " ref_compte = ? AND"
							+ " ref_texte = ? AND"
							+ " etat IN (?,?)")){
				statement.setInt(1, addToFind.getCommune().getRefCommune());
				statement.setInt(2, addToFind.getTitle().getRefTitre());
				statement.setInt(3, addToFind.getCompteLbc().getRefCompte());
				statement.setInt(4, addToFind.getTexte().getRefTexte());
				statement.setString(5, EtatAdd.enAttenteModeration.toString());
				statement.setString(6, EtatAdd.onLine.toString());
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					addToFind.setRefAdd(rs.getInt(1));
					addToFind.setNbControle(rs.getInt(2));
					if(rs.next()){
						throw new MultipleCorrespondanceAddException();// si il y a plus de 2 annonces correspondantes
					}
					return addToFind;
				}else{
					return new Add(); // on retourne une add vide si pas de correspondance
				}
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
					maConnection.prepareStatement("select ref_add from adds_lbc where "
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
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public void updateState(Add addToUpdate) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update adds_lbc"
							+ " set etat = ?"
							+ " where ref_add = ?")){
				statement.setString(1, addToUpdate.getEtat().toString());
				statement.setInt(2, addToUpdate.getRefAdd());
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
					maConnection.prepareStatement("select ref_add from adds_lbc where "
							+ " ref_compte = ? AND"
							+ " etat = 'onLine' AND"
							+ " date_controle < ?")){
				statement.setInt(1, add.getCompteLbc().getRefCompte());
				Date dateOfTheDay = new Date();
				statement.setDate(2, new java.sql.Date(dateOfTheDay.getTime()));
				ResultSet rs = statement.executeQuery();
				while(rs.next()) {
					Add addFromBdd = new Add();
					addFromBdd.setRefAdd(rs.getInt(1));
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
