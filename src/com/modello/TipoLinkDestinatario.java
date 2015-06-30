package com.modello;

import java.util.Date;

import com.googlecode.objectify.annotation.*;

@Entity
public class TipoLinkDestinatario
{
	@Id Long id;
	
	@Index private final String emailDestinatario;
	private final Long idInvito;
	
	public TipoLinkDestinatario(String emailDestinatario, Long idInvito, Date dataIscriz) throws EccezionePrecondizioni
	{
		if( emailDestinatario==null || idInvito==null)
			throw new EccezionePrecondizioni("Gli oggetti devono essere inizializzati!!");
		this.emailDestinatario = emailDestinatario;
		this.idInvito = idInvito;
	}

	public Long getId() {
		return id;
	}

	public String getEmailDestinatario() {
		return emailDestinatario;
	}

	public Long getIdInvito() {
		return idInvito;
	}

	public boolean equals(Object o) {
		if(o==null || !o.getClass().equals(this.getClass()))
			return false;
		TipoLinkDestinatario l = (TipoLinkDestinatario)o;
		return l.emailDestinatario.equals(this.emailDestinatario) && l.idInvito.equals(this.idInvito);
	}

	public int hashCode() {
		return this.emailDestinatario.hashCode() + this.idInvito.hashCode();
	}
}