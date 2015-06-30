package com.modello;

import com.googlecode.objectify.annotation.*;
import com.modello.*;

@Entity
public class VotoUomoPartita
{
	private static final int MIN_LINK_VOTANTEUP = 1;
	private static final int MIN_LINK_VOTATOUP = 1;
	private static final int MIN_LINK_VOTOPERPARTITA = 1;

	private String commento;
	
	@Id Long id;
	private String votante; //Giocatore
	private Long linkVotoPerPartita;	//Partita
	private String votato;	//Giocatore
	
	private VotoUomoPartita(){}
	
	public VotoUomoPartita(String c)
	{
		this.commento = c;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCommento(String c)
	{
		if( c != null )
			this.commento = c;
	}
	
	public String getCommento()
	{
		return this.commento;
	}
	
	// ASSOCIAZIONE votanteUP
	public void setVotanteUP(String idVotante)
	{
		if( idVotante != null) this.votante = idVotante;
	}
	
	public int quantiVotanti()
	{
		if( this.votante == null )
			return 0;
		else
			return 1;
	}
	
	public String getVotanteUP() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiVotanti() < MIN_LINK_VOTANTEUP )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return this.votante;
	}
	
	// ASSOCIAZIONE votatoUP
	public void setVotatoUP(String idVotato)
	{
		if( idVotato != null ) this.votato = idVotato;
	}
	
	public int quantiVotati()
	{
		if( this.votato == null )
			return 0;
		else
			return 1;
	}
	
	public String getVotatoUP() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiVotati() < MIN_LINK_VOTATOUP )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return this.votato;
	}
	
	// ASSOCIAZIONE votoPerPartita
	public void inserisciLinkVotoPerPartita(Long idLink)
	{
		if( idLink != null ) this.linkVotoPerPartita = idLink;
	}
	
	public void rimuoviLinkVotoPerPartita(Long idLink)
	{
		if( idLink != null && idLink.equals(this.linkVotoPerPartita) ) this.linkVotoPerPartita = null;
	}
	
	public int quantiLinkVotoPerPartita()
	{
		if( this.linkVotoPerPartita == null)
			return 0;
		else
			return 1;
	}
	
	public Long getLinkVotoPerPartita() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiLinkVotoPerPartita() < MIN_LINK_VOTOPERPARTITA )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return this.linkVotoPerPartita;
	}

	public boolean equals(Object o)
	{
		if(o == null || !o.getClass().equals(this.getClass()))
			return false;
		
		VotoUomoPartita v = (VotoUomoPartita)o;
		return this.votante.equals(v.votante) && this.votato.equals(v.votato) && this.linkVotoPerPartita.equals(v.linkVotoPerPartita);
	}
	
	public int hashCode()
	{
		return this.votante.hashCode() + this.votato.hashCode() + this.linkVotoPerPartita.hashCode();
	}
}