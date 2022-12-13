package model.dao;

import java.util.List;

import model.entities.Seller;

public interface SellerDao {
	
	//responsavel por inserir no banco de dados o "obj" enviado como parâmetro de entrada
	void insert(Seller obj);

	//responsavel por atualizar no banco de dados o "obj" enviado como parâmetro de entrada
	void update(Seller obj);
	
	//responsavel por deletar no banco de dados o "id" enviado como parâmetro de entrada
	void deleteById(Integer id);
	
	//responsavel por procurar um objeto no banco de dados com o "id" enviado como parâmetro de entrada, se existir retorna, caso contrário retorna nulo
	Seller findById(Integer id);
	
	//para retornar todos os "seller" usamos uma lista
	List<Seller> findAll();
}