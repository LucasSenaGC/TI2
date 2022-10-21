package model;

import java.io.Serializable;

public class Recomendar implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sites_url;
	private String usuario_nome;
	private int seguro;

	
	public Recomendar() {
		sites_url = "";
		usuario_nome = "";
		seguro = 0;
	}

	public Recomendar(String sites_url, String usuario_nome, int seguro) {
		setSitesUrl(sites_url);
		setUsuarioNome(usuario_nome);
		setSeguro(seguro);
	}		
	

	public String getSitesUrl() {
		return sites_url;
	}

	public void setSitesUrl(String sites_url) {
		this.sites_url = sites_url;
	}
	
	public String getUsuarioNome() {
		return usuario_nome;
	}

	public void setUsuarioNome(String usuario_nome) {
		this.usuario_nome = usuario_nome;
	}
	
	public int getSeguro() {
		return seguro;
	}

	public void setSeguro(int seguro) {
		this.seguro = seguro;
	}

	/**
	 * Metodo sobreposto da classe Object. E' executado quando um objeto precisa
	 * ser exibido na forma de String.
	 */
	@Override
	public String toString() {
		return "SitesUrl: " + sites_url + "   UsuarioNome: " + usuario_nome + "   Seguro: " + seguro;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (this.getSitesUrl() == ((Recomendar) obj).getSitesUrl());
	}
}
