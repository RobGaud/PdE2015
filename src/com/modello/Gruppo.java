package com.modello;

import java.util.*;
import com.googlecode.objectify.annotation.*;


@Entity
public class Gruppo
{	
	@Id public Long id;
	
	@Index private String nome;
	private Date dataCreazione;
	//private LinkedList<TipoLinkIscritto> giocatoriIscritti;
	//private HashSet<TipoLinkOrganizza> partiteOrganizzate;
	//private HashSet<Campo> campiPreferiti;
	
	public static final int MIN_LINK_ISCRITTO = 1;
	
	protected Gruppo(){}
	
	public Gruppo(String n, Date data)
	{
		this.nome = n;
		this.dataCreazione = data;
		//this.giocatoriIscritti = new LinkedList<TipoLinkIscritto>();
		//this.partiteOrganizzate = new HashSet<TipoLinkOrganizza>();
		//this.campiPreferiti = new HashSet<Campo>();
	}
	
	public String getNome()
	{
		return this.nome;
	}
	
	public void setNome(String newName)
	{
		this.nome = newName;
	}
	
	// TODO getData come stringa?
	public Date getDataCreazione()
	{
		return this.dataCreazione;
	}
	
	public void setDataCreazione(Date dataCreazione) {
		if(dataCreazione != null)
			this.dataCreazione = dataCreazione;
	}

	public String toString()
	{
		return this.nome + " " + this.dataCreazione.toString();
	}
	
	public boolean equals(Object o)
	{
		if(o == null || !o.getClass().equals(this.getClass()))
			return false;
		
		Gruppo g = (Gruppo)o;
		return g.nome.equals(this.nome) && g.dataCreazione.equals(this.dataCreazione);
	}
	
	public int hashCode()
	{
		return this.nome.length() + this.dataCreazione.hashCode();
	}
	
	/*
	// ASSOCIAZIONE iscritto
	public int quantiIscritti()
	{
		return this.giocatoriIscritti.size();
	}
	
	public List<TipoLinkIscritto> getGiocatoriIscritti() throws EccezioneMolteplicitaMinima
	{
		if(this.quantiIscritti() < MIN_LINK_ISCRITTO)
			throw new EccezioneMolteplicitaMinima("Cardinalita minima violata!");
		else
			return (LinkedList<TipoLinkIscritto>)this.giocatoriIscritti.clone();
	}
	
	public void inserisciLinkIscritto(TipoLinkIscritto t)
	{
		if( t != null && t.getGruppo().equals(this))
			ManagerIscritto.inserisci(t);
	}
	
	public void rimuoviLinkIscritto(TipoLinkIscritto t)
	{
		if( t != null && t.getGruppo().equals(this))
			ManagerIscritto.elimina(t);
	}
	
	public void inserisciPerManagerIscritto(ManagerIscritto m)
	{
		if(m != null) this.giocatoriIscritti.add(m.getLink());
	}

	public void eliminaPerManagerIscritto(ManagerIscritto m)
	{
		if(m != null) this.giocatoriIscritti.remove(m.getLink());
	}
	
	// ASSOCIAZIONE organizza
	public void inserisciLinkOrganizza(TipoLinkOrganizza t)
	{
		if( t != null && t.getGruppo().equals(this))
			ManagerOrganizza.inserisci(t);
	}
	
	public void rimuoviLinkOrganizza(TipoLinkOrganizza t)
	{
		if( t != null && t.getGruppo().equals(this))
			ManagerOrganizza.rimuovi(t);
	}
	
	public void inserisciPerManagerOrganizza(ManagerOrganizza m)
	{
		if(m != null) this.partiteOrganizzate.add(m.getLink());

	}
	
	public void rimuoviPerManagerOrganizza(ManagerOrganizza m)
	{
		if(m != null) this.partiteOrganizzate.remove(m.getLink());

	}
	
	// ASSOCIAZIONE CONOSCE
	public void inserisciCampo(Campo c)
	{
		if(c != null)
			this.campiPreferiti.add(c);
	}
	
	public void rimuoviCampo(Campo c)
	{
		this.campiPreferiti.remove(c);
	}
	
	public Set<Campo> getCampiPreferiti()
	{
		return (HashSet<Campo>)this.campiPreferiti.clone();
	}
	*/
}
