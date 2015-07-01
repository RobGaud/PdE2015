package com.bean;

public class InfoGestionePartiteBean
{
	private String emailGiocatore;
	private Long idPartita;
	private int nAmici;
	
	public String getEmailGiocatore() {
		return emailGiocatore;
	}
	public void setEmailGiocatore(String emailGiocatore) {
		this.emailGiocatore = emailGiocatore;
	}
	public Long getIdPartita() {
		return idPartita;
	}
	public void setIdPartita(Long idPartita) {
		this.idPartita = idPartita;
	}
	public int getnAmici() {
		return nAmici;
	}
	public void setnAmici(int nAmici) {
		this.nAmici = nAmici;
	}
}