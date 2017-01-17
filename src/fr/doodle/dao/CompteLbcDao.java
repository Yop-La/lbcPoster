package fr.doodle.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import scraper.CompteLbc;

public class CompteLbcDao extends JdbcRepository<CompteLbc, Integer> {

	@Override
	public List<CompteLbc> findAll() {
		List<CompteLbc> retour = new ArrayList<CompteLbc>();
		try(Connection maConnection = getConnection()){
			try(PreparedStatement getOneCommentStatement = 
					maConnection.prepareStatement("SELECT ref_compte, mail, password from compte_lbc ")){
				try(ResultSet results = getOneCommentStatement.executeQuery()){
					while(results.next()){
						retour.add(new CompteLbc(results.getString(2), results.getString(3), results.getInt(1)));
					}
					return retour;
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	public CompteLbc save(CompteLbc entity) {
		if(entity.getRefCompte() < 0){ // si l'admin n'exite pas en base, on l'insère
			try(Connection maConnection = getConnection()){	
				try(PreparedStatement addAdminStatement = 
						maConnection.prepareStatement("INSERT INTO compte_lbc(mail, password) "
								+ "values(?,?)",Statement.RETURN_GENERATED_KEYS)){	
					addAdminStatement.setString(1, entity.getMail());
					addAdminStatement.setString(2, entity.getPassword());
					addAdminStatement.executeUpdate();
					ResultSet rs = addAdminStatement.getGeneratedKeys();
					if (rs.next()) {
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


}
