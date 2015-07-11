package com.bean;

import com.attivita.SessioneUtente.StatoSessione;

public class PayloadBean
{
	private Long idSessione;
	
	

	private String profiloDaVisitare;
	private Long idGruppo;
	private Long idPartita;
	private StatoSessione nuovoStato;
	
	public Long getIdSessione() {
		return idSessione;
	}
	public void setIdSessione(Long idSessione) {
		this.idSessione = idSessione;
	}

	public String getProfiloDaVisitare() {
		return profiloDaVisitare;
	}

	public void setProfiloDaVisitare(String profiloDaVisitare) {
		this.profiloDaVisitare = profiloDaVisitare;
	}

	public StatoSessione getNuovoStato() {
		return nuovoStato;
	}

	public void setNuovoStato(StatoSessione nuovoStato) {
		this.nuovoStato = nuovoStato;
	}

	public Long getIdGruppo()
	{
		return this.idGruppo;
	}
	
	public void setIdGruppo(Long idGruppo) {
		this.idGruppo = idGruppo;
	}

	public Long getIdPartita() {
		return idPartita;
	}

	public void setIdPartita(Long idPartita) {
		this.idPartita = idPartita;
	}

	

}
