package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ihm.MoteurConsole;
import scraper.CompteLbc;
import scraper.CritereSelectionTitre;
import scraper.Title;
import scraper.TypeTitle;

public class TitreDao extends JdbcRepository<Title, Integer>{

	@Override
	public List<Title> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Title save(Title entity) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("INSERT INTO titres(titre, type_titre)"
							+ " VALUES(?, ?)",Statement.RETURN_GENERATED_KEYS)){	
				statement.setString(1, entity.getTitre());
				statement.setString(2, entity.getTypeTitle().toString());
				statement.executeUpdate();
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next()) {
					entity.setRefTitre(rs.getInt(1));
				}
				return entity;
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return entity;
		}
	}


	@Override
	public Title findOne(Integer refTitre) {
		Title retour=null;
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("SELECT "
							+ " ref_titre,"
							+ " titre,"
							+ " type_titre "
							+ "from titres where ref_titre = ?")){	
				statement.setInt(1, refTitre);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					retour = new Title(rs.getString(2), TypeTitle.valueOf(rs.getString(3)), rs.getInt(1));
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return retour;
		}
	}

	// retourne null si pas de retour unique ou si exception
	public Title findOneWithTitre(Title titre){
		Title retour=null;
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("SELECT ref_titre from titres where titre like ?")){	
				selectStatement.setString(1, titre.getTitre());
				ResultSet rs = selectStatement.executeQuery();
				if (rs.next()) {
					retour = findOne(rs.getInt(1));
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return retour;
		}
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
	public void delete(Title entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public List<Title> findWithSelection(CritereSelectionTitre critSelectTitre, CompteLbc compteLbc) {
		List<Title> titles = new ArrayList<Title>();
		String requete ="SELECT ref_titre, titre, type_titre from titres"
				+ " where ref_titre not in (select ref_titre from adds_lbc where "
				+ " etat in('onLine', 'enAttenteModeration') and ref_compte = ?) and type_titre = ?;";
		// TODO faire les autres if pour prendre en compte les régions à cibler ou à éviter
		try(Connection maConnection = getConnection()){			
			try(PreparedStatement statement = 
					maConnection.prepareStatement(requete)){
				statement.setInt(1, compteLbc.getRefCompte());
				statement.setString(2, critSelectTitre.getTypeTitle().toString());
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						Title titleInBdd = new Title(results.getString(2), TypeTitle.valueOf(results.getString(3)), results.getInt(1));
						titles.add(titleInBdd);
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return null;
		}
		return titles;
	}

	public List<TypeTitle> findAllTypeTitle() {
		List<TypeTitle> typeTitles = new ArrayList<TypeTitle>();
		String requete ="SELECT distinct type_titre from titres";
		try(Connection maConnection = getConnection()){			
			try(PreparedStatement statement = 
					maConnection.prepareStatement(requete)){
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						TypeTitle typeTitleInBdd = TypeTitle.valueOf(results.getString(1));
						typeTitles.add(typeTitleInBdd);
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return null;
		}
		return typeTitles;
	}

}
