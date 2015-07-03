package com.bean;

public class InfoGiocatoreBean {
	
	private String nome;
	private String email;
	private String telefono;
	private String ruoloPreferito;
	private String fotoProfilo;
	
	public InfoGiocatoreBean() {}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		if(nome != null)
			this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		if(email != null)
			this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		if(telefono != null)
			this.telefono = telefono;
	}
	public String getRuoloPreferito() {
		return ruoloPreferito;
	}
	public void setRuoloPreferito(String ruoloPreferito) {
		if(ruoloPreferito != null)
			this.ruoloPreferito = ruoloPreferito;
	}
	public String getFotoProfilo() {
		return fotoProfilo;
	}
	public void setFotoProfilo(String fotoProfilo) {
		if(fotoProfilo != null)
			this.fotoProfilo = fotoProfilo;
	}

}
