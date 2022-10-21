package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Usuario;

public class UsuarioDAO {
	private Connection conexao;

	
	public UsuarioDAO() throws IOException {
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

	public boolean add(Usuario usuario) {
		boolean status = false;
		try {  
			Statement st = conexao.createStatement();
			st.executeUpdate("INSERT INTO usuario (nome, senha, email) "
					       + "VALUES ('"+usuario.getNome()+ "', '" + usuario.getSenha() + "', '" + usuario.getEmail() + "');");
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
	
	public Usuario getUsuario(String nomeOUemail, String senha) {
		//Usuario usuario = new Usuario();
		Usuario usuario = null;
		
		// Medida preventiva contra sql injection
		nomeOUemail.replace("'", "");
		senha.replace("'", "");
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM usuario WHERE (usuario.nome = '" + nomeOUemail + "'" +
										   " OR usuario.email = '" + nomeOUemail + "') AND usuario.senha = '" + senha + "'");		
	         if(rs.next()){
	        	 usuario = new Usuario(rs.getString("nome"), rs.getString("senha"), rs.getString("email"));
	         }
	         st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return usuario;
	}
	
	public Usuario getUsuarioNoPassword(String nomeOUemail) {
		//Usuario usuario = new Usuario();
		Usuario usuario = null;
		
		// Medida preventiva contra sql injection
		nomeOUemail.replace("'", "");
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM usuario WHERE usuario.nome = '" + nomeOUemail + "'" +
										   " OR usuario.email = '" + nomeOUemail + "'");		
	         if(rs.next()){
	        	 usuario = new Usuario(rs.getString("nome"), rs.getString("senha"), rs.getString("email"));
	         }
	         st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return usuario;
	}
	
	public Usuario[] getUsuarios() {
		Usuario[] usuarios = null;
		
		try {
			Statement st = conexao.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = st.executeQuery("SELECT * FROM usuario ");		
	         if(rs.next()){
	             rs.last();
	             usuarios = new Usuario[rs.getRow()];
	             rs.beforeFirst();

	             for(int i = 0; rs.next(); i++) {
		                usuarios[i] = new Usuario(rs.getString("nome"), rs.getString("senha"), rs.getString("email"));
	             }
	          }
	          st.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return usuarios;
	}

	public boolean atualizarUsuario(String nomeOUemail, String senha, Usuario usuario_atualizado) {
		boolean status = false;
		
		// Medida preventiva contra sql injection
		nomeOUemail.replace("'", "");
		senha.replace("'", "");
		usuario_atualizado.getNome().replace("'", "");
		usuario_atualizado.getSenha().replace("'", "");
		usuario_atualizado.getEmail().replace("'", "");
		
		try {  
			Statement st = conexao.createStatement();
			String sql = "UPDATE usuario SET nome = '" + usuario_atualizado.getNome() + "', senha = '"  
				        + usuario_atualizado.getSenha() + "', email = '" + usuario_atualizado.getEmail() + "' WHERE (nome = '" + nomeOUemail + "'"
				        + " OR email = '" + nomeOUemail + "'" + ") AND senha = '" + senha + "'";
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}

	public boolean excluirUsuario(String nomeOUemail, String senha) {
		boolean status = false;
		
		// Medida preventiva contra sql injection
		nomeOUemail.replace("'", "");
		senha.replace("'", "");
		
		try {  
			Statement st = conexao.createStatement();
			String sql = "DELETE FROM usuario WHERE senha = '" + senha + "'" + " AND (nome = '" + nomeOUemail + "'" + 
						 " OR email = '" + nomeOUemail + "')";
			st.executeUpdate(sql);
			st.close();
			status = true;
		} catch (SQLException u) {  
			throw new RuntimeException(u);
		}
		return status;
	}
}
