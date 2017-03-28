package dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.postgresql.Driver;
import org.postgresql.ds.PGPoolingDataSource;

import exception.RepositoryException;
import ihm.MoteurConsole;
import scraper.Add;

public abstract class JdbcRepository<T, ID extends Serializable> {

    private static PGPoolingDataSource dataSource;

    private static final String ID = "jjnfqovi"; // (Login)

    private static final String PASSWORD = "PQ_UYR_ZoyWRJ7ddz7MxgdzaYF8zzq4X"; // (Mot de passe)
    
    private static int nb_data_source = 0;

    // URL = "jdbc:postgresql://10.165.120.75:5432/" + ID;
	// protected final static String URL = "jdbc:postgresql://sgbd-eleves.domensai.ecole:5432/"+ID;
    static {
    	connectToDataBase();

    }
   
    public static void connectToDataBase(){
        Driver.setLogLevel(Driver.DEBUG);
        nb_data_source++;
        if(dataSource != null)
        	dataSource.close();	
        dataSource = new PGPoolingDataSource();
        dataSource.setDataSourceName("A Data Source ");
        dataSource.setServerName("horton.elephantsql.com");
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
