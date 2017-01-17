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

	public List<Commune> findAll(String nameCommune) {
		List<Commune> retour= new ArrayList<Commune>();
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("SELECT ref_commune from communes where lower(nom_commune) like lower(?)")){	
				selectStatement.setString(1, "%"+nameCommune.toLowerCase()+"%");
				ResultSet rs = selectStatement.executeQuery();
				while(rs.next()) {
					Commune communeRetour = findOne(rs.getInt(1));
					retour.add(communeRetour);
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return retour;
		}
	}


	public Commune save(Commune entity) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("INSERT INTO communes("
							+" code_dep, "
							+ " code_commune,"
							+ " nom_commune,"
							+ " pop_totale,"
							+ " code_reg,"
							+ " nom_reg,"
							+ " code_postal)"
							+" VALUES (?, ?, ?, ?, ?, ?, ?, ?),",Statement.RETURN_GENERATED_KEYS)){;
							statement.setString(1, entity.getCodeDep());
							statement.setString(2, entity.getCodeCommune());
							statement.setString(3, entity.getNomCommune());
							statement.setFloat(4, entity.getPopTotale());//
							statement.setString(5, entity.getCodeReg());
							statement.setString(6, entity.getNomReg());
							statement.setString(7, entity.getCodePostal());
							statement.executeUpdate();
							ResultSet rs = statement.getGeneratedKeys();
							if(rs.next()){
								entity.setRefCommune(rs.getInt(1));
							}
							return entity;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return entity;
		}
	}

	public Commune updateNomCommune(Commune entity) {
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update communes set nom_commune = ? "
							+ " where ref_commune = ?")){;
							statement.setString(1, entity.getNomCommune());
							statement.setInt(2, entity.getRefCommune());
							statement.executeUpdate();
							return entity;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return entity;
		}

	}

	public Commune updateCodePostal(Commune entity){
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement statement = 
					maConnection.prepareStatement("update communes set code_postal = ? "
							+ " where ref_commune = ?")){;
							statement.setString(1, entity.getCodePostal());
							statement.setInt(2, entity.getRefCommune());
							statement.executeUpdate();
							return entity;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return entity;
		}
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
							+ " ref_commune,"
							+ " code_postal "
							+ "from communes where ref_commune = ?")){	
				addAdminStatement.setInt(1, refCommune);
				ResultSet rs = addAdminStatement.executeQuery();
				if (rs.next()) {
					retour = new Commune(rs.getString(1), rs.getString(2), rs.getString(3), 
							rs.getFloat(4), rs.getString(5), rs.getString(6), 
							rs.getInt(7),rs.getString(8));
					if(retour.getCodePostal()==null){
						retour.setCodePostal("");
					}
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return retour;
		}
	}

	// retourne null si pas un unique retour ou si exception
	public Commune findOneWithNomCommune(Commune commune){
		Commune retour=null;
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("SELECT ref_commune from communes where lower(nom_commune) = ?")){	
				selectStatement.setString(1, commune.getNomCommune().toLowerCase());
				ResultSet rs = selectStatement.executeQuery();
				if(rs.next()) {
					retour = findOne(rs.getInt(1));
				}else{
					return null;
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
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
		String requete ="SELECT code_dep, code_commune, nom_commune, pop_totale, code_reg, nom_reg, ref_commune, code_postal from communes"
				+ " where ref_commune not in (select ref_commune from adds_lbc where etat in('onLine', 'enAttenteModeration'))";
		if(critSelecVille.getBornInfPop()!=-1){
			requete = requete + " and pop_totale >= ?";
		}
		if(critSelecVille.getBornSupPop()!=-1){
			requete = requete + " and pop_totale <= ?";
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
						Commune commune = new Commune(results.getString(1), results.getString(2), results.getString(3), results.getFloat(4), results.getString(5), results.getString(6), results.getInt(7), results.getString(8));
						if(commune.getCodePostal()==null){
							commune.setCodePostal("");
						}
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

	public List<Commune> searchWithNameCodeDepAndCodeComm(String ElementsRequete) {
		List<Commune> communes = new ArrayList<Commune>();
		String[] elementsRequete = ElementsRequete.split(",");
		String nomCommune = elementsRequete[0].toLowerCase();
		String codeDep = elementsRequete[1];
		String codeCommune = elementsRequete[2];
		if(nomCommune.equals("0")){
			nomCommune = "%";
		}
		if(codeDep.equals("0")){
			codeDep = "%";
		}
		if(codeCommune.equals("0")){
			codeCommune = "%";
		}

		// TODO faire les autres if pour prendre en compte les régions à cibler ou à éviter
		try(Connection maConnection = getConnection()){		
			String requete = "SELECT code_dep, code_commune, nom_commune, pop_totale, code_reg, nom_reg, ref_commune, code_postal from communes"
					+ " where code_dep like ? and code_commune like ? and lower(nom_commune) like ?";
			try(PreparedStatement statement = 
					maConnection.prepareStatement(requete)){
				statement.setString(1, codeDep);
				statement.setString(2, codeCommune);
				statement.setString(3, nomCommune.toLowerCase());
				try(ResultSet results = statement.executeQuery()){
					while(results.next()){
						Commune commune = new Commune(results.getString(1), results.getString(2), results.getString(3), results.getFloat(4), results.getString(5), results.getString(6), results.getInt(7), results.getString(8));
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

	public void moveToNotInLbc(Commune communeFromLbc) {
		try(Connection maConnection = getConnection()){
			
			
			String requete = "INSERT INTO communesnotinlbc(code_dep, code_commune, nom_commune, pop_totale, code_reg, nom_reg, ref_commune, code_postal) " 
					+"SELECT code_dep, code_commune, nom_commune, pop_totale, code_reg, nom_reg, ref_commune, code_postal "
					+"FROM communes "
					+"WHERE ref_commune = ?";

			try(PreparedStatement statement = 
					maConnection.prepareStatement(requete)){
				statement.setInt(1, communeFromLbc.getRefCommune());
				statement.executeUpdate();

			}
			requete = "DELETE FROM communes where ref_commune = ?";
			try(PreparedStatement statement = 
					maConnection.prepareStatement(requete)){
				statement.setInt(1, communeFromLbc.getRefCommune());
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public boolean isCodePostalNull(Commune commune) {//pour savoir si cette commune est en ligne sur lbc
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("SELECT code_postal from communes "
							+ " where ref_commune = ?")){	
				selectStatement.setInt(1, commune.getRefCommune());
				ResultSet rs = selectStatement.executeQuery();
				if(rs.next()) {
					if(rs.getString(1)==null)
						return true;
				}
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return true;
		}
		
	}
	
	public List<Commune> findAllWithNameAndCodeDep(Commune commune) {
		List<Commune> retour= new ArrayList<Commune>();
		try(Connection maConnection = getConnection()){	
			try(PreparedStatement selectStatement = 
					maConnection.prepareStatement("SELECT ref_commune from communes where "
										+ "lower(nom_commune) like lower(?) and lower(code_dep) like ?")){	
				selectStatement.setString(1, commune.getNomCommune().toLowerCase());
				selectStatement.setString(2, "%"+commune.getCodeDep().toLowerCase()+"%");
				ResultSet rs = selectStatement.executeQuery();
				while(rs.next()) {
					Commune communeRetour = findOne(rs.getInt(1));
					retour.add(communeRetour);
				}
				return retour;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return retour;
		}
	}


}
