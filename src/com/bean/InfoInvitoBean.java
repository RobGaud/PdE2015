package com.bean;

public class InfoInvitoBean
{
	private String emailMittente;
	private String emailDestinatario;
	private String nomeGruppo;
	private Long idInvito;
	private Long idGruppo;
	
	public String getEmailMittente() {
		return emailMittente;
	}

	public void setEmailMittente(String emailMittente) {
		this.emailMittente = emailMittente;
	}
	
	public String getEmailDestinatario() {
		return emailDestinatario;
	}

	public void setEmailDestinatario(String emailDestinatario) {
		this.emailDestinatario = emailDestinatario;
	}
	
	public String getNomeGruppo() {
		return nomeGruppo;
	}

	public void setNomeGruppo(String nomeGruppo) {
		this.nomeGruppo = nomeGruppo;
	}

	public Long getIdGruppo() {
		return idGruppo;
	}

	public void setIdGruppo(Long idGruppo) {
		this.idGruppo = idGruppo;
	}

	public Long getIdInvito() {
		return idInvito;
	}

	public void setIdInvito(Long idInvito) {
		this.idInvito = idInvito;
	}

}
