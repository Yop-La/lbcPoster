package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import scraper.Client;
import scraper.CompteLbc;

public class CompteLbcDao extends JdbcRepository<CompteLbc, Integer> {

	@Override
	public List<CompteLbc> findAll() {
		return null;
	}

	public CompteLbc save(CompteLbc entity) {
		if(entity.getRefCompte() < 0){ // si l'admin n'exite pas en base, on l'insère
			try(Connection maConnection = getConnection()){	
				try(PreparedStatement statemennt = 
						maConnection.prepareStatement("INSERT INTO compte_lbc("
								+ "mail, "
								+ "password, "
								+ "nb_annonces_online, "
								+ "date_dernier_control, "
								+ "pseudo, "
								+ "redirection, "
								+ "date_derniere_activite, "
								+ "ref_client) "
								+ "values(?,?,0,NULL,NULL,FALSE,NULL,?)",Statement.RETURN_GENERATED_KEYS)){	
					statemennt.setString(1, entity.getMail());
					statemennt.setString(2, entity.getPassword());
					statemennt.setInt(3, entity.getRefClient());
					statemennt.executeUpdate();
					ResultSet rs = statemennt.getGeneratedKeys();
					if (rs.next()){
						int ref_compte = rs.getInt(1);
						entity.setRefCompte(ref_compte);
					}
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}else{ // pour mettre à jour l'adresse mail de l'admin
			try(Connection maConnection = getConnection()){	
				try(PreparedStatement updateAdminStatement = 
						maConnection.prepareStatement("UPDATE compte_lbc set email_admin = ?, password = ? where id_admin= ?")){
					updateAdminStatement.setString(1, entity.getMail());
					updateAdminStatement.setString(2, entity.getPassword());
					updateAdminStatement.setInt(3, entity.getRefCompte());
					updateAdminStatement.executeUpdate();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}

		}
		return entity;
	}

	@Override
	public CompteLbc findOne(Integer id) {
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
	public void delete(CompteLbc entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public void updatePseudo(CompteLbc compteInUse) {
		try(Connection maConnection = getConnection()){
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update compte_lbc "
							+ " set pseudo = ?"
							+ " where ref_compte = ?")){
				statement.setString(1, compteInUse.getPseudo());
				statement.setInt(2, compteInUse.getRefCompte());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public void updateDateDerniereActivite(CompteLbc compteInUse) {
		try(Connection maConnection = getConnection()){
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update compte_lbc "
							+ " set date_derniere_activite = ?"
							+ " where ref_compte = ?")){
				statement.setDate(1, new java.sql.Date(compteInUse.getDateDerniereActivite().getTime().getTime()));
				statement.setInt(2, compteInUse.getRefCompte());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public void updateDateDernierControl(CompteLbc compteInUse) {
		try(Connection maConnection = getConnection()){
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update compte_lbc "
							+ " set date_dernier_control = ?"
							+ " where ref_compte = ?")){
				Date today = new Date();
				statement.setDate(1, new java.sql.Date(today.getTime()));
				statement.setInt(2, compteInUse.getRefCompte());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public void updateRedirection(CompteLbc compteInUse) {
		try(Connection maConnection = getConnection()){
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update compte_lbc "
							+ " set redirection = ?"
							+ " where ref_compte = ?")){
				statement.setBoolean(1, compteInUse.isRedirection());
				statement.setInt(2, compteInUse.getRefCompte());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

	}

	public void updateEnabled(CompteLbc compteInUse) {
		try(Connection maConnection = getConnection()){
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update compte_lbc "
							+ " set disabled = ?, "
							+ " date_of_disabling = ? "
							+ " where ref_compte = ?")){
				statement.setBoolean(1, compteInUse.isDisabled());
				statement.setDate(2, new java.sql.Date(compteInUse.getDateOfDisabling().getTime().getTime()));
				statement.setInt(3, compteInUse.getRefCompte());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}

	}

	public HashMap<Integer, CompteLbc> findAll(Client clientInUse) {
		HashMap<Integer, CompteLbc> retour = new HashMap<Integer, CompteLbc>();
		try(Connection maConnection = getConnection()){
			ArrayList<Integer> refs_compte = new ArrayList<Integer>();
			// on sélectionne toutes les ref_comptes
			try(PreparedStatement statement = 
					maConnection.prepareStatement("SELECT ref_compte from compte_lbc "
							+ " where ref_client = ? ")){
				statement.setInt(1, clientInUse.getRefClient());
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						refs_compte.add(results.getInt(1));
					}
				}
			}
			// on met à jour le nb d'annonces en ligne et la date de dernier contrôle de chaque compte
			for(Integer ref_compte : refs_compte){
				// pour récupérer le nb d'annonces en ligne
				int nbAddsOnline = 0;
				try(PreparedStatement statement = 
						maConnection.prepareStatement("SELECT count(*) from adds_lbc "
								+ " where ref_compte = ? and etat = 'onLine'")){
					statement.setInt(1, ref_compte);
					try(ResultSet results = statement.executeQuery()){
						if(results.next()){
							nbAddsOnline = results.getInt(1);
						}
					}
				}
				// pour récupérer la date de dernier contrôle
				java.sql.Date dateLastControl = null;
				java.sql.Date dateLastControlBis = null;
				try(PreparedStatement statement = 
						maConnection.prepareStatement("SELECT max(date_controle) from adds_lbc "
								+ " where ref_compte = ?")){
					statement.setInt(1, ref_compte);
					try(ResultSet results = statement.executeQuery()){
						if(results.next()){
							dateLastControl = results.getDate(1);
						}
					}
				}

				try(PreparedStatement statement = 
						maConnection.prepareStatement("SELECT date_dernier_control from compte_lbc"
								+ " where ref_compte = ?")){
					statement.setInt(1, ref_compte);
					try(ResultSet results = statement.executeQuery()){
						if(results.next()){
							dateLastControlBis = results.getDate(1);
						}
					}
				}
				if(dateLastControl == null){
					dateLastControl=dateLastControlBis;
				}else{
					if(dateLastControlBis!=null){
						if(dateLastControlBis.getTime()>=dateLastControl.getTime()){
							dateLastControl=dateLastControlBis;
						}
					}
				}



				// pour récupérer la date de péremption
				java.sql.Date lessRecentDateOfMiseEnLigne = null;
				try(PreparedStatement statement = 
						maConnection.prepareStatement("SELECT min(date_mise_en_ligne) from adds_lbc "
								+ " where ref_compte = ? and etat = 'onLine'")){
					statement.setInt(1, ref_compte);
					try(ResultSet results = statement.executeQuery()){
						if(results.next()){
							lessRecentDateOfMiseEnLigne = results.getDate(1);
						}
					}
				}
				Calendar dateOfPeremption = Calendar.getInstance();
				if(lessRecentDateOfMiseEnLigne!=null){
					dateOfPeremption.setTime(lessRecentDateOfMiseEnLigne);
					dateOfPeremption.add(Calendar.MONTH,2);
					dateOfPeremption.add(Calendar.DAY_OF_MONTH,-5);
				}

				// pour mettre à jour le compte correspondat
				try(PreparedStatement statement = 
						maConnection.prepareStatement("update compte_lbc "
								+ " set nb_annonces_online = ?,"
								+ " date_dernier_control = ?,"
								+ "  date_avant_peremption = ?"
								+ " where ref_compte = ?")){
					statement.setInt(4, ref_compte);
					statement.setInt(1, nbAddsOnline);
					statement.setDate(2, dateLastControl);
					if(lessRecentDateOfMiseEnLigne!=null){
						statement.setDate(3, new java.sql.Date(dateOfPeremption.getTime().getTime()));
					}
					else{
						statement.setDate(3, null);
					}
					statement.executeUpdate();
				}

			}

			try(PreparedStatement statement = 
					maConnection.prepareStatement("SELECT "
							+ "ref_compte, "
							+ "mail, "
							+ "date_dernier_control, "
							+ "nb_annonces_online, "
							+ "password, "
							+ "pseudo, "
							+ "redirection, "
							+ "date_derniere_activite, "
							+ "date_avant_peremption,  "
							+ "disabled,  "
							+ "date_of_disabling,  "
							+ "ref_client  "
							+ "from compte_lbc "
							+ " where ref_client = ?")){
				statement.setInt(1, clientInUse.getRefClient());
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						CompteLbc compteLbc = new CompteLbc();
						compteLbc.setRefCompte(results.getInt(1));
						compteLbc.setMail(results.getString(2));

						Calendar dateDernierControle = Calendar.getInstance();
						if(results.getDate(3)!=null){
							dateDernierControle.setTime(results.getDate(3));
							compteLbc.setDateDernierControle(dateDernierControle);
						}

						compteLbc.setNbAnnoncesEnLigne(results.getInt(4));
						compteLbc.setPassword(results.getString(5));
						compteLbc.setPseudo(results.getString(6));
						compteLbc.setRedirection(results.getBoolean(7));

						Calendar dateDerniereActivite = Calendar.getInstance();
						if(results.getDate(8)!=null){
							dateDernierControle.setTime(results.getDate(8));
							compteLbc.setdateDerniereActivite(dateDerniereActivite);
						}

						Calendar dateAvantPeremption = Calendar.getInstance();
						if(results.getDate(9)!=null){
							dateAvantPeremption .setTime(results.getDate(9));
							compteLbc.setDateAvantPeremption(dateAvantPeremption);
						}
						compteLbc.setDisabled(results.getBoolean(10));

						Calendar dateOfDisabling = Calendar.getInstance();
						if(results.getDate(11)!=null){
							dateOfDisabling.setTime(results.getDate(11));
							compteLbc.setDateOfDisabling(dateOfDisabling);
						}
						compteLbc.setRefClient(clientInUse.getRefClient());
						retour.put(compteLbc.getRefCompte(),compteLbc);
					}
				}
			}
			return retour;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
}



