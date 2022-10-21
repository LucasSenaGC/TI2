package model;

import java.io.Serializable;

public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nome;
	private String senha;
	private String email;

	
	public Usuario() {
		nome = "";
		senha = "";
		email = "";
	}

	public Usuario(String nome, String senha, String email) {
		setNome(nome);
		setSenha(senha);
		setEmail(email);
	}		
	

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Metodo sobreposto da classe Object. E' executado quando um objeto precisa
	 * ser exibido na forma de String.
	 */
	@Override
	public String toString() {
		return "Nome: " + nome + "   Senha: " + senha + "   Email: " + email;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (this.getNome() == ((Usuario) obj).getNome());
	}
}
