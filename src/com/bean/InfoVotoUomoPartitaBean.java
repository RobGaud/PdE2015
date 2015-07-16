package com.bean;


public class InfoVotoUomoPartitaBean
{
	private String commento;
	private String votante; //Giocatore
	private Long linkVotoPerPartita;	//Partita
	private String votato;	//Giocatore
	
	public String getCommento() {
		return commento;
	}
	public void setCommento(String commento) {
		this.commento = commento;
	}
	public String getVotante() {
		return votante;
	}
	public void setVotante(String votante) {
		this.votante = votante;
	}
	public Long getLinkVotoPerPartita() {
		return linkVotoPerPartita;
	}
	public void setLinkVotoPerPartita(Long linkVotoPerPartita) {
		this.linkVotoPerPartita = linkVotoPerPartita;
	}
	public String getVotato() {
		return votato;
	}
	public void setVotato(String votato) {
		this.votato = votato;
	}
}
