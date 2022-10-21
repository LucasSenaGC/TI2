package dao;

import model.Recomendar;
import java.sql.*;
import java.io.IOException;

public class RecomendarDAO {
	private Connection conexao;

	
	public RecomendarDAO() throws IOException {
		conexao = null;
	}
	
	public boolean conectar() {
		String driverName = "org.postgresql.Driver";                    
		String serverName = "localhost";
		String mydatabase = "bss";
		int porta = 5432;
		String url = "jdbc:postgresql://" + serverName + ":" + porta +"/" + mydatabase;
		String username = "postgres";
		String password = "aguia1";
		boolean status = false;

		try {
			Class.forName(driverName);
			conexao = DriverManager.getConnection(url, username, password);
			status = (conexao == null);
			System.out.println("Conexao efetuada com o postgres!");
		} catch (ClassNotFoundException e) { 
			System.err.println("Conexao NAO efetuada com o postgres -- Driver nao encontrado -- " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Conexao NAO efetuada com o postgres -- " + e.getMessage());
		}

		return status;
	}
	
	public boolean closeDB() {
		boolean status = false;
		
		try {
			conexao.close();
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}

	public boolean add(Recomendar recomendacao) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("INSERT INTO recomendar (sites_url, usuario_nome, seguro) "
					       + "VALUES ('"+ recomendacao.getSitesUrl() + "', '" + recomendacao.getUsuarioNome() + "', '" + recomendacao.getSeguro() + "');");		
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	public Recomendar getRecomendacao(String sites_url, String usuario_nome) {
		//Recomendar recomendacao = new Recomendar();
		Recomendar recomendacao = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM recomendar WHERE recomendar.sites_url = '" + sites_url + "'" +
										   " AND usuario_nome = '" + usuario_nome + "'");		
	         if(rs.next()){
	        	 recomendacao = new Recomendar(rs.getString("sites_url"), rs.getString("usuario_nome"), rs.getInt("seguro"));
	         }
	         st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return recomendacao;
	}
	
	public Recomendar[] getRecomendacoes() {
		Recomendar[] recomendacoes = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT DISTINCT sites_url FROM recomendar ");		
	         if(rs.next()){
	             rs.last();
	             recomendacoes = new Recomendar[rs.getRow()];
	             rs.beforeFirst();

	             for(int i = 0; rs.next(); i++) {
		                recomendacoes[i] = new Recomendar(rs.getString("sites_url"), "", 1);
	             }
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return recomendacoes;
	}

	public boolean atualizarRecomendacao(Recomendar recomendacao) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			String sql = "UPDATE recomendar SET seguro = " + recomendacao.getSeguro() + " WHERE sites_url = '" + recomendacao.getSitesUrl() + "'" +
				        " AND usuario_nome = '" + recomendacao.getUsuarioNome() + "'";
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}

	public boolean excluirRecomendacao(String sites_url, String usuario_nome) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("DELETE FROM recomendar WHERE sites_url = '" + sites_url + "'" + " AND usuario_nome = '" + usuario_nome + "'");
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	public Recomendar countUrlAppearences(String sites_url) { //Pega todas as ocorrencias de um site (ajuda na hora de calcular a porcentagem)
		Recomendar siteInfo = new Recomendar();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT COUNT(sites_url) AS qtd FROM recomendar WHERE recomendar.sites_url = '" + sites_url + "'");		
	         if(rs.next()){
	        	 siteInfo = new Recomendar(sites_url, "", rs.getInt("qtd")); // Integer.parseInt(rs.getString("qtd"))
	         }
	         st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return siteInfo;
	}
	
	public Recomendar countUrlSecureAppearences(String sites_url) { //Pega todas as ocorrencias seguras de um site (ajuda na hora de calcular a porcentagem)
		Recomendar siteInfo = new Recomendar();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT COUNT(sites_url) AS qtdseguro FROM recomendar WHERE recomendar.sites_url = '" + sites_url + "'" + 
										   " AND recomendar.seguro = 1"); //AND recomendar.seguro LIKE 1	
	         if(rs.next()){
	        	 siteInfo = new Recomendar(sites_url, "", rs.getInt("qtdseguro"));
	         }
	         st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return siteInfo;
	}
}
