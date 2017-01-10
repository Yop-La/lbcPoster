package fr.doodle.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import scraper.Commune;
import scraper.CriteresSelectionVille;

public class CommuneDao extends JdbcRepository<Commune, Integer>{

	@Override
	public List<Commune> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Commune save(Commune entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Commune findOne(Integer refCommune) {
		Commune retour=null;
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement addAdminStatement = 
					maConnection.prepareStatement("SELECT "
							+ " code_dep,"
							+ " code_commune,"
							+ " nom_commune,"
							+ " pop_totale,"
							+ " code_reg,"
							+ " nom_reg,"
							+ " ref_commune "
							+ "from titres where ref_commune = ?")){	
				addAdminStatement.setInt(1, refCommune);
				ResultSet rs = addAdminStatement.executeQuery();
				if (rs.next()) {
					retour = new Commune(rs.getString(1), rs.getString(2), rs.getString(3), 
										rs.getFloat(4), rs.getString(5), rs.getString(6), 
										rs.getInt(7));
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return retour;
		}
	}
	
	// retourne null si pas correspondance ou si exception
	public Commune findOneWithNomCommune(Commune commune){
		Commune retour=null;
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement addAdminStatement = 
					maConnection.prepareStatement("SELECT ref_commune from communes where nom_commune = ?")){	
				addAdminStatement.setString(1, commune.getNomCommuneOnLbc());
				ResultSet rs = addAdminStatement.executeQuery();
				if (rs.next()) {
					retour = findOne(rs.getInt(1));
					retour.setNomCommuneOnLbc(commune.getNomCommuneOnLbc());
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
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
	public void delete(Commune entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	// TODO finir ici pour prendre en compte les régions à cibler ou à éviter 
	public List<Commune> findWithSelection(CriteresSelectionVille critSelecVille){
		List<Commune> communes = new ArrayList<Commune>();
		int indexPointDinterrogation = 1;
		String requete ="SELECT code_dep, code_commune, nom_commune, pop_totale, code_reg, nom_reg, ref_commune from communes where true";
		if(critSelecVille.getBornInfPop()!=-1){
			requete = requete + "and pop_totale >= ?";
		}
		if(critSelecVille.getBornSupPop()!=-1){
			requete = requete + "and pop_totale <= ?";
		}
		// TODO faire les autres if pour prendre en compte les régions à cibler ou à éviter
		try(Connection maConnection = getConnection()){			
			try(PreparedStatement getVillesWithSelection = 
					maConnection.prepareStatement(requete)){

				if(critSelecVille.getBornInfPop()!=-1){
					getVillesWithSelection.setInt(indexPointDinterrogation, critSelecVille.getBornInfPop());
					indexPointDinterrogation++;
				}
				if(critSelecVille.getBornSupPop()!=-1){
					getVillesWithSelection.setInt(indexPointDinterrogation, critSelecVille.getBornSupPop());
					indexPointDinterrogation++;
				}
				try(ResultSet results = getVillesWithSelection.executeQuery()){
					while(results.next()){
						Commune commune = new Commune(results.getString(1), results.getString(2), results.getString(3), results.getFloat(4), results.getString(5), results.getString(6), results.getInt(7));
						communes.add(commune);
					}
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
		return communes;
	}

}
