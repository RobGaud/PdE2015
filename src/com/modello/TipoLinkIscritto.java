package com.modello;

import java.util.*;
import com.googlecode.objectify.annotation.*;

@Entity
public class TipoLinkIscritto {
	
	@Id Long id;
	
	@Index private String giocatore;
	@Index private Long gruppo;
	// Indicizzato per query sul primo iscritto (Non si sa mai)
	@Index private Date dataIscriz;
	
	private TipoLinkIscritto() {
		this.dataIscriz = new Date();
	}
	
	public TipoLinkIscritto(String giocatore, Long gruppo) throws EccezionePrecondizioni{
		if(giocatore==null || gruppo==null)
			throw new EccezionePrecondizioni("Gli oggetti devono essere inizializzati!!");
		this.giocatore = giocatore;
		this.gruppo = gruppo;
		this.dataIscriz = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataIscriz() {
		return dataIscriz;
	}

	public void setDataIscriz(Date dataIscriz) {
		this.dataIscriz = dataIscriz;
	}

	public String getGiocatore() {
		return giocatore;
	}
	
	public void setGiocatore(String giocatore) {
		this.giocatore = giocatore;
	}

	public Long getGruppo() {
		return gruppo;
	}
	
	public void setGruppo(Long gruppo) {
		this.gruppo = gruppo;
	}

	public boolean equals(Object o) {
		if(o==null || !o.getClass().equals(this.getClass()))
			return false;
		TipoLinkIscritto l = (TipoLinkIscritto)o;
		return l.giocatore.equals(this.giocatore) && l.gruppo.equals(this.gruppo);
	}

	public int hashCode() {
		return this.giocatore.hashCode() + this.gruppo.hashCode();
	
	}
	
}
