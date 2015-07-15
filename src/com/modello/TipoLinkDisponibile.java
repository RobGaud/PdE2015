package com.modello;

import com.googlecode.objectify.annotation.*;

@Entity
public class TipoLinkDisponibile
{
	@Id private Long id;
	
	@Index private String giocatore;
	@Index private Long partita;
	private int nAmici;
	
	private TipoLinkDisponibile(){}
	
	public TipoLinkDisponibile(String giocatore, Long partita, int nAmici) throws EccezionePrecondizioni{
		if(giocatore==null || partita==null)
			throw new EccezionePrecondizioni("Gli oggetti devono essere inizializzati!!");
		this.giocatore = giocatore;
		this.partita = partita;
		this.nAmici = nAmici;
	}

	public void setGiocatore(String giocatore) {
		this.giocatore = giocatore;
	}

	public void setPartita(Long partita) {
		this.partita = partita;
	}

	public void setnAmici(int nAmici) {
		this.nAmici = nAmici;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGiocatore() {
		return giocatore;
	}

	public Long getPartita() {
		return partita;
	}

	public int getnAmici() {
		return nAmici;
	}
	
	public boolean equals(Object o) {
		if(o==null || !o.getClass().equals(this.getClass()))
			return false;
		TipoLinkDisponibile l = (TipoLinkDisponibile)o;
		return l.giocatore.equals(this.giocatore) && l.partita.equals(this.partita);
	}

	public int hashCode() {
		return this.giocatore.hashCode() + this.partita.hashCode();
	
	}
	
}
