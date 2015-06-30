package com.modello;

import java.util.Date;

import com.googlecode.objectify.annotation.*;

@Entity
public class Invito
{
	@Id Long id;
	
	private final String emailMittente;
	private TipoLinkDestinatario destinatario;
	private final Long gruppo;
	private final Date dataInvio;
	
	private static final int MIN_LINK_MITTENTE = 1;
	private static final int MIN_LINK_DESTINATARIO = 1;
	private static final int MIN_LINK_GRUPPO = 1;
	
	public Invito(String emailMittente, Gruppo gruppo, Date dataInvio) {
		this.mittente = mittente;
		this.gruppo = gruppo;
		this.dataInvio = dataInvio;
	}
	
	int quantiMittente() {
		if( this.mittente == null )
			return 0;
		else
			return 1;
	}
	
	int quantiDestinatario() {
		if( this.destinatario == null )
			return 0;
		else
			return 1;
	}
	
	int quantiGruppo() {
		if( this.gruppo == null )
			return 0;
		else
			return 1;
	}
	
	public Giocatore getMittente() throws EccezioneMolteplicitaMinima {
		if (this.quantiMittente() < this.MIN_LINK_MITTENTE)
			throw new EccezioneMolteplicitaMinima("Cardinalita' min/max violata");
		return mittente;
	}

	public Gruppo getGruppo() throws EccezioneMolteplicitaMinima {
		if (this.quantiGruppo() < this.MIN_LINK_GRUPPO)
			throw new EccezioneMolteplicitaMinima("Cardinalita' min/max violata");
		return gruppo;
	}

	public Date getDataInvio() {
		return dataInvio;
	}
	
	public void inserisciLinkDestinatario(TipoLinkDestinatario l) {
		if(l != null && l.getInvito().equals(this))
			ManagerDestinatario.inserisci(l);
	}
	public void eliminaLinkDestinatario(TipoLinkDestinatario l) {
		if(l != null && l.getInvito().equals(this))
			ManagerDestinatario.elimina(l);
	}
	
	public TipoLinkDestinatario getDestinatario() throws EccezioneMolteplicitaMinima {
		if(this.quantiDestinatario() < this.MIN_LINK_DESTINATARIO)
			throw new EccezioneMolteplicitaMinima("Molteplicita min/max violata!!");
		return destinatario;
	}

	public void inserisciPerManagerDestinatario(ManagerDestinatario m) {
		if(m!=null)
			destinatario = m.getLink();
		
	}

	public void eliminaPerManagerDestinatario(ManagerDestinatario m) {
		if(m!=null)
			destinatario = null;	
	}
	
	public boolean equals(Object o) {
		if(o==null || !o.getClass().equals(this.getClass()))
			return false;
		Invito i = (Invito)o;
		return i.mittente.equals(this.mittente) && i.destinatario.equals(this.destinatario) &&
			   i.gruppo.equals(this.gruppo);
	}
	
	public int hashCode() {
		return mittente.hashCode() + destinatario.hashCode() + gruppo.hashCode();
	}
}
