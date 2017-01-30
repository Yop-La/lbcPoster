package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.util.SystemOutLogger;

import ihm.MoteurConsole;
import scraper.CompteLbc;
import scraper.CritereSelectionTitre;
import scraper.CriteresSelectionTexte;
import scraper.Texte;
import scraper.Title;
import scraper.TypeTexte;
import scraper.TypeTitle;

public class TexteDao extends JdbcRepository<Texte, Integer>{

	@Override
	public List<Texte> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Texte save(Texte entity) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("INSERT INTO TEXTES(texte, type)"
							+ " VALUES(?, ?)",Statement.RETURN_GENERATED_KEYS)){	
				statement.setString(1, entity.getCorpsTexteInBase());
				statement.setString(2, entity.getTypeTexte().toString());
				statement.executeUpdate();
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next()) {
					entity.setRefTexte(rs.getInt(1));
				}
				return entity;
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return entity;
		}
	}

	@Override
	public Texte findOne(Integer refTexte) {
		Texte retour=null;
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("SELECT "
							+ " ref_texte,"
							+ " texte,"
							+ " type"
							+ " from textes where ref_texte = ?")){	
				statement.setInt(1, refTexte);
				ResultSet rs = statement.executeQuery();
				if (rs.next()) {
					retour = new Texte(rs.getString(2), TypeTexte.valueOf(rs.getString(3)), rs.getInt(1));
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return retour;
		}
	}
	

	// retourne null si pas correspondance ou si exception
	public List<Texte> findWithCorpsTexte(Texte texte){
		List<Texte> retour= new ArrayList<Texte>();
		try(Connection maConnection = getConnection()){	
			String[] lignes = texte.getCorpsTexteOnLbc().split("\n\n");
			String endRequete="";
			// boucle for pour générer la fin de la requête qui va faire une recherche sur les 11 premiers caractères de chaque ligne
			for(int i=0;i<lignes.length;i++){
				if(i!=lignes.length-1){
					endRequete = endRequete + " texte like ? AND";
				}else{
					endRequete = endRequete + " texte like ?";
				}
			}
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("SELECT ref_texte from textes where "+endRequete)){	
				// boucle pour remplir le prepared statement
				for(int i=0;i<lignes.length;i++){
					String ligne = lignes[i];
					int longueur = ligne.length();
					if(longueur>texte.getLevelCorrespondance()){
						ligne=ligne.substring(0, texte.getLevelCorrespondance());
					}
					selectStatement.setString(i+1,"%"+ligne+"%");
				}
				ResultSet rs = selectStatement.executeQuery();
				while(rs.next()) {
					
					Texte texteFromBdd = findOne(rs.getInt(1));
					texteFromBdd.setCorpsTexteOnLbc(texte.getCorpsTexteOnLbc());
					retour.add(texteFromBdd);
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
	public void delete(Texte entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public List<Texte> findWithSelection(CriteresSelectionTexte critSelectTexte, CompteLbc compteLbc) {
		List<Texte> textes = new ArrayList<Texte>();
		String requete ="SELECT ref_texte, texte, type from textes"
				+ " where ref_texte not in (select ref_texte from adds_lbc where "
				+ " etat in('onLine', 'enAttenteModeration') and ref_compte = ?) and type = ?;";
		// TODO faire les autres if pour prendre en compte les régions à cibler ou à éviter
		try(Connection maConnection = getConnection()){			
			try(PreparedStatement statement = 
					maConnection.prepareStatement(requete)){
				statement.setInt(1, compteLbc.getRefCompte());
				statement.setString(2, critSelectTexte.getTypeTexte().toString());
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						Texte texteInBdd = new Texte(results.getInt(1));
						texteInBdd.setCorpsTexteForPublication(results.getString(2));
						texteInBdd.setTypeTexte(TypeTexte.valueOf(results.getString(3)));
						textes.add(texteInBdd);
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return null;
		}
		return textes;
	}

	public List<TypeTexte> findAllTypeTitle() {
		List<TypeTexte> typeTextes = new ArrayList<TypeTexte>();
		String requete ="SELECT distinct type from textes";
		try(Connection maConnection = getConnection()){			
			try(PreparedStatement statement = 
					maConnection.prepareStatement(requete)){
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						TypeTexte typeTexteInBdd = TypeTexte.valueOf(results.getString(1));
						typeTextes.add(typeTexteInBdd);
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace(MoteurConsole.ps);
			return null;
		}
		return typeTextes;
	}

}
