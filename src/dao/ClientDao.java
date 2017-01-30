package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import scraper.Client;
import scraper.Commune;
public class ClientDao extends JdbcRepository<Client, Integer> {

	@Override
	public List<Client> findAll() {
		List<Client> retour= new ArrayList<Client>();
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("SELECT "
							+ "ref_client, "
							+ "nom_client, "
							+ "prenom_client "
								+ " from client")){	

				ResultSet rs = selectStatement.executeQuery();
				while(rs.next()) {
					Client client = new Client();
					client.setRefClient(rs.getInt(1));
					client.setnomClient(rs.getString(2));
					client.setPrenomClient(rs.getString(3));
					retour.add(client);
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return retour;
		}
	}

	public Client save(Client entity) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("INSERT INTO client("
							+" nom_client, "
							+" prenom_client)"
							+" VALUES (?, ?)",Statement.RETURN_GENERATED_KEYS)){;
							statement.setString(1, entity.getNomClient());
							statement.setString(2, entity.getPrenomClient());
							statement.executeUpdate();
							ResultSet rs = statement.getGeneratedKeys();
							if(rs.next()){
								entity.setRefClient(rs.getInt(1));
							}
							return entity;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return entity;
		}
	}
	
	
	@Override
	public Client findOne(Integer id) {
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
	public void delete(Client entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

}
