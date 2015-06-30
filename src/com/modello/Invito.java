package com.modello;

import java.util.Date;

import com.googlecode.objectify.annotation.*;

@Entity
public class Invito
{
	@Id Long id;
	
	private String emailMittente;
	@Index private Long idLinkDestinatario;
	private Long idGruppo;
	private Date dataInvio;
	
	private static final int MIN_LINK_MITTENTE = 1;
	private static final int MIN_LINK_DESTINATARIO = 1;
	private static final int MIN_LINK_GRUPPO = 1;
	
	private Invito(){}
	
	public Invito(String emailMittente, Long idGruppo) {
		this.emailMittente = emailMittente;
		this.idGruppo = idGruppo;
		this.dataInvio = new Date();	//Nota: new Date() scrive la data e l'ora di quell'istante
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	
	
	int quantiMittente() {
		if( this.emailMittente == null )
			return 0;
		else
			return 1;
	}
	
	int quantiDestinatario() {
		if( this.idLinkDestinatario == null )
			return 0;
		else
			return 1;
	}
	
	int quantiGruppo() {
		if( this.idGruppo == null )
			return 0;
		else
			return 1;
	}
	
	public String getEmailMittente() throws EccezioneMolteplicitaMinima {
		if (this.quantiMittente() < this.MIN_LINK_MITTENTE)
			throw new EccezioneMolteplicitaMinima("Cardinalita' min/max violata");
		return emailMittente;
	}

	public Long getGruppo() throws EccezioneMolteplicitaMinima {
		if (this.quantiGruppo() < this.MIN_LINK_GRUPPO)
			throw new EccezioneMolteplicitaMinima("Cardinalita' min/max violata");
		return idGruppo;
	}

	public Date getDataInvio() {
		return dataInvio;
	}
	
	public void inserisciLinkDestinatario(Long idLink)
	{
		if(idLink != null) this.idLinkDestinatario = idLink;
	}
	
	public void eliminaLinkDestinatario(Long idLink)
	{
		if(idLink != null && idLink.equals(this.idLinkDestinatario))
			this.idLinkDestinatario = null;
	}
	
	public Long getLinkDestinatario() throws EccezioneMolteplicitaMinima {
		if(this.quantiDestinatario() < this.MIN_LINK_DESTINATARIO)
			throw new EccezioneMolteplicitaMinima("Molteplicita min/max violata!!");
		return idLinkDestinatario;
	}
	
	public boolean equals(Object o) {
		if(o==null || !o.getClass().equals(this.getClass()))
			return false;
		Invito i = (Invito)o;
		return i.emailMittente.equals(this.emailMittente) && i.idLinkDestinatario.equals(this.idLinkDestinatario) &&
			   i.idGruppo.equals(this.idGruppo);
	}
	
	public int hashCode() {
		return emailMittente.hashCode() + idLinkDestinatario.hashCode() + idGruppo.hashCode();
	}
}
