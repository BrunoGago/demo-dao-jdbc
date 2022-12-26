package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{
	
	
	// 19-23 - Injeção de dependencia usando o connection para fazer a conexão com o MySQL
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES (?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! no rows affected");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		
	}

	@Override
	public void deleteById(Integer id) {
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.id "
					+ "WHERE seller.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			//testar se há resultado do bd
			if(rs.next()) {
				// 88 - Instanciação do departamento e salvamento dos dados nas respectivas variáveis retornadas da pesquisa no BD
				Department dep = instantiateDepartment(rs);
				
				// 76 - Instanciação do seller e salvamento dos dados nas respectivas variáveis retornadas da pesquisa no BD
				Seller obj = instantiateSeller(rs, dep);
				
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	//função para retornar os dados do BD de seller - Obs: não vamos tratar as exceções pois na linha 72 já o fizemos
	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	//função para retornar os dados do BD de department - Obs: não vamos tratar as exceções pois na linha 72 já o fizemos
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name"); 
			
			rs = st.executeQuery();
			
			 //Lista criada para guardar os dados de cada vendedor por departamento
			List<Seller> list = new ArrayList<>();
			
			//Lista em MAP criada para guardar qualquer departamento que instaciar sem repetição
			Map<Integer, Department> map = new HashMap<>();
			
			//testar se há resultado do bd
			while(rs.next()) {
				
				//teste para ver cada departamento que for passar até o WHILE acabar, buscando em get o id do departamento no BD
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				//se o departamento for nulo (ainda nao tivermos visto ele), vamos instanciar
				if(dep == null) {
					// 88 - Instanciação do departamento e salvamento dos dados nas respectivas variáveis retornadas da pesquisa no BD
					dep = instantiateDepartment(rs);
					
					//passagem de dados do Department para o MAP
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				// 76 - Instanciação do seller e salvamento dos dados nas respectivas variáveis retornadas da pesquisa no BD
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name"); 
			
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			 //Lista criada para guardar os dados de cada vendedor por departamento
			List<Seller> list = new ArrayList<>();
			
			//Lista em MAP criada para guardar qualquer departamento que instaciar
			Map<Integer, Department> map = new HashMap<>();
			
			//testar se há resultado do bd
			while(rs.next()) {
				
				//teste para ver cada departamento que for passar até o WHILE acabar, buscando em get o id do departamento no BD
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				//se o departamento for nulo (ainda nao tivermos visto ele), vamos instanciar
				if(dep == null) {
					// 88 - Instanciação do departamento e salvamento dos dados nas respectivas variáveis retornadas da pesquisa no BD
					dep = instantiateDepartment(rs);
					
					//passagem de dados do Department para o MAP
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				// 76 - Instanciação do seller e salvamento dos dados nas respectivas variáveis retornadas da pesquisa no BD
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}