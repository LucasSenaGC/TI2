package model;

import java.io.Serializable;

public class Site implements Serializable {
	private static final long serialVersionUID = 1L;
	private String url;
	private String nome;

	
	public Site() {
		url = "";
		nome = "";
	}

	public Site(String url, String nome) {
		setUrl(url);
		setNome(nome);
	}		
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * Metodo sobreposto da classe Object. E' executado quando um objeto precisa
	 * ser exibido na forma de String.
	 */
	@Override
	public String toString() {
		return "Url: " + url + "   Nome: " + nome;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (this.getUrl() == ((Site) obj).getUrl());
	}	
}