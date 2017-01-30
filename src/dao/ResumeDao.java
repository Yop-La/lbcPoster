package dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import scraper.Client;
import scraper.Commune;
import scraper.StatsOnCommune;

public class ResumeDao extends JdbcRepository {

	public List<StatsOnCommune> getRepeatedOnlineCommune(Client client) {
		List<StatsOnCommune> retour = new ArrayList<StatsOnCommune>();
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement(""
							+ "select ref_commune, "
									+ "nom_commune, "
									+ "nb_fois_online "
									+ "from "
										+"(select ref_commune, "
											+ "nom_commune, "
											+ "count(ref_commune) as nb_fois_online "
											+ "from "
												+"(select adds_lbc.ref_commune, "
												+ "nom_commune from adds_lbc, "
												+ "communes "
								+"where adds_lbc.ref_commune = communes.ref_commune "
									+ "and etat like 'onLine' and ref_compte in (select ref_compte from compte_lbc where ref_client = ?)) as addsOnline "
						+"group by ref_commune, "
						+ "	nom_commune order by nb_fois_online desc) as nb "
								+"where nb_fois_online>1")){
				statement.setInt(1, client.getRefClient());
				ResultSet rs = statement.executeQuery();
				while(rs.next()) {
					StatsOnCommune statsOnCommune = new StatsOnCommune();
					Commune commune = new Commune();
					commune.setRefCommune(rs.getInt(1));
					commune.setNomCommune(rs.getString(2));
					statsOnCommune.setCommune(commune);
					statsOnCommune.setNbFoisEnLigne(rs.getInt(3));
					
					retour.add(statsOnCommune);
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public List findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findOne(Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Serializable id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Serializable id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Object entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	
	
	}
