package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.postgresql.Driver;
import org.postgresql.ds.PGPoolingDataSource;

import com.gargoylesoftware.htmlunit.javascript.host.file.FileReader;

import exception.RepositoryException;
import ihm.MoteurConsole;
import scraper.Add;

public abstract class JdbcRepository<T, ID extends Serializable> {

	private static PGPoolingDataSource dataSource;
	
	private static List<String> identifiants = setPasswordAndId();

	private static final String ID = identifiants.get(0);

	private static final String PASSWORD = identifiants.get(1);
	
	private static final String serverName = identifiants.get(2);

	private static int nb_data_source = 0;

	static {
		connectToDataBase();

	}

	public static List<String> setPasswordAndId(){
		List<String> retour = new ArrayList<String>();
		try (Stream<String> stream = Files.lines(Paths.get("./identifiants_database.txt"))) {
			stream.forEach(line->{
				retour.add(line);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retour;
	}

	public static void connectToDataBase(){
		Driver.setLogLevel(Driver.DEBUG);
		nb_data_source++;
		if(dataSource != null)
			dataSource.close();	
		dataSource = new PGPoolingDataSource();
		dataSource.setDataSourceName("A Data Source ");
		dataSource.setServerName(serverName);
		dataSource.setPortNumber(5432);
		dataSource.setDatabaseName(ID);
		dataSource.setUser(ID);
		dataSource.setPassword(PASSWORD);
		dataSource.setMaxConnections(200);
		dataSource.setSocketTimeout(40);
	}

	public JdbcRepository() {
		super();
	}

	protected Connection getConnection() {
		//System.out.println("Le nombre de co est : "+getNbConnections());
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RepositoryException("Enable to connection to the database", e);
		}
	}

	public int getNbConnections(){
		int retour=0;
		try(Connection maConnection = dataSource.getConnection()){	
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("select count(*) from pg_stat_activity where usename like 'jjnfqovi'")){	
				ResultSet rs = selectStatement.executeQuery();
				if (rs.next()) {
					retour = rs.getInt(1)+1;
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			e.printStackTrace(MoteurConsole.ps);
			return retour;
		}
	}

	/**
	 * Returns all instances of the type.
	 * 
	 * @return all entities
	 */
	public abstract List<T> findAll();

	/**
	 * Saves a given entity.
	 * Use the returned instance for further operations as the save operation might have changed
	 * the entity instance completely.
	 * 
	 * @param entity
	 * @return the saved entity
	 */

	public abstract T findOne(ID id);

	/**
	 * Returns whether an entity with the given id exists.
	 * 
	 * @param id
	 *            must not be {@literal null}.
	 * @return true if an entity with the given id exists, {@literal false} otherwise
	 * @throws IllegalArgumentException
	 *             if {@code id} is {@literal null}
	 */
	public abstract boolean exists(ID id);

	/**
	 * Returns the number of entities available.
	 * 
	 * @return the number of entities
	 */
	public abstract long count();

	/**
	 * Deletes the entity with the given id.
	 * 
	 * @param id
	 *            must not be {@literal null}.
	 * @throws IllegalArgumentException
	 *             in case the given {@code id} is {@literal null}
	 */
	public abstract void delete(ID id);

	/**
	 * Deletes a given entity.
	 * 
	 * @param entity
	 * @throws IllegalArgumentException
	 *             in case the given entity is {@literal null}.
	 */
	public abstract void delete(T entity);

	/**
	 * Deletes all entities managed by the repository.
	 */
	public abstract void deleteAll();



}
