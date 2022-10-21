package dao;

import model.Site;
import java.sql.*;
import java.io.IOException;


public class SiteDAO {
	private Connection conexao;

	
	public SiteDAO() throws IOException {
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

	public boolean add(Site site) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("INSERT INTO sites (url, nome) "
					       + "VALUES ('"+ site.getUrl() + "', '" + site.getNome() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	public Site getSite(String url) {
		Site site = new Site();
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM sites WHERE sites.url = '" + url + "'");
			if(rs.next()) {
				site = new Site(rs.getString("url"), rs.getString("nome"));
			}
	        st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return site;
	}
	
	public Site[] getSites() {
		Site[] sites = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM sites ");		
	         if(rs.next()){
	             rs.last();
	             sites = new Site[rs.getRow()];
	             rs.beforeFirst();

	             for(int i = 0; rs.next(); i++) {
		                sites[i] = new Site(rs.getString("url"), rs.getString("nome"));
	             }
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return sites;
	}
	
	public Site[] getSitesSeguros() {
		Site[] sites = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM recomendar WHERE recomendar.seguro LIKE 1");		
	         if(rs.next()){
	             rs.last();
	             sites = new Site[rs.getRow()];
	             rs.beforeFirst();

	             for(int i = 0; rs.next(); i++) {
		                sites[i] = new Site(rs.getString("url"), rs.getString("nome"));
	             }
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return sites;
	}

	public boolean atualizarSite(Site site) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			String sql = "UPDATE sites SET url = '" + site.getUrl() + "', nome = '"  
				       + site.getNome() + "' WHERE url = " + site.getUrl();
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}

	public boolean excluirSite(String url) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("DELETE FROM sites WHERE url = '" + url + "'");
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
}