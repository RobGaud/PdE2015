package com.modello;

import com.googlecode.objectify.annotation.*;
import com.modello.*;

@Entity
public class TipoLinkVotoPerPartita
{
	@Id private Long id;
	private Long voto;
	@Index private Long partita;
	
	public TipoLinkVotoPerPartita(Long idVoto, Long idPartita) throws EccezionePrecondizioni
	{
		if( idVoto == null || idPartita == null )
			throw new EccezionePrecondizioni("Gli oggetti devono essere inizializzati!");
		
		this.voto = idVoto;
		this.partita = idPartita;
	}
	
	public Long getVotoUomoPartita()
	{
		return this.voto;
	}
	
	public Long getPartita()
	{
		return this.partita;
	}
	
	public String toString()
	{
		return this.voto.toString() + " " + this.partita.toString();
	}
	
	public boolean equals(Object o)
	{
		if(o == null || !o.getClass().equals(this.getClass()))
			return false;
		
		TipoLinkVotoPerPartita t = (TipoLinkVotoPerPartita)o;
		return t.voto.equals(this.voto) && t.partita.equals(this.partita);
	}
	
	public int hashCode()
	{
		return this.voto.hashCode() + this.partita.hashCode();
	}
}