package fr.doodle.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public Commune findOne(Integer id) {
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
		String requete ="SELECT code_dep, code_commune, nom_commune, pop_totale, code_reg, nom_reg 	from communes where true";
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
						Commune commune = new Commune(results.getString(1), results.getString(2), results.getString(3), results.getFloat(4), results.getString(1), results.getString(5));
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
