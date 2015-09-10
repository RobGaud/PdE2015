package com.modello;

import com.googlecode.objectify.annotation.*;
import java.util.*;

@Entity
public class Campo
{
	@Id public Long id;
	
	@Index private String nome;	
	private String telefono;
	@Index private String indirizzo;
	@Index private String citta;
	private float prezzo;
	private HashSet<String> giorniChiusura;
	
	private Campo(){
		this.giorniChiusura = new HashSet<String>();
	}
	
	/***
	 * 
	 * @param n
	 * @param t
	 * @param i
	 * @param c
	 * @param p
	 */
	public Campo(String n, String t, String i, String c, float p)
	{
		this.nome = n;
		this.telefono = t;
		this.indirizzo = i;
		this.citta = c;
		this.prezzo = p;
		this.giorniChiusura = new HashSet<String>();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome()
	{
		return this.nome;
	}
	
	public void setNome(String newName)
	{
		if( newName != null)
			this.nome = newName;
	}
	
	/*** METODO PER GET DI telefono ***
	 * 
	 * @return
	 */
	public String getTelefono()
	{
		return this.telefono;
	}
	
	/*** METODO PER SET DI telefono ***
	 * 
	 * @param newTel
	 */
	public void setTelefono(String newTel)
	{
		if( newTel != null)
			this.telefono = newTel;
	}
	
	/***
	 * 
	 * @return
	 */
	public String getIndirizzo()
	{
		return this.indirizzo;
	}
	
	/***
	 * 
	 * @param newInd
	 */
	public void setIndirizzo(String newInd)
	{
		if( newInd != null)
			this.indirizzo = newInd;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getPrezzo()
	{
		return this.prezzo;
	}
	
	/**
	 * 
	 * @param newPrice
	 */
	public void setPrezzo(float newPrice)
	{
		if( newPrice >= 0)	// Vedi Prete
			this.prezzo = newPrice;
	}
	
	/***
	 * 
	 * @param citta
	 */
	public void setCitta(String citta) {
		if(citta != null)
			this.citta = citta;
	}
	
	/***
	 * 
	 * @return
	 */
	public String getCitta() {
		return this.citta;
	}
	
	/**
	 * 
	 * @param giorno
	 */
	public void addGiornoChiusura(String giorno)
	{
		if(giorno != null)
			this.giorniChiusura.add(giorno);
	}
	
	/**
	 * 
	 * @param giorno
	 */
	public void removeGiornoChiusura(String giorno)
	{
		this.giorniChiusura.remove(giorno);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getGiorniChiusura()
	{
		return (HashSet<String>)this.giorniChiusura.clone();
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		String toString = this.nome + " " + this.indirizzo + " " + this.citta + " " + this.telefono + " (";
		
		Iterator<String> i = this.giorniChiusura.iterator();
		while(i.hasNext())
		{
			toString = toString + i.next() + ",";
		}
		toString = toString + ")";
		
		return toString;
	}
	
	public boolean equals(Object o)
	{
		if(o == null || !o.getClass().equals(this.getClass()))
			return false;
		Campo c = (Campo)o;
		return c.id.equals(this.id);
		
		/*
		boolean semiResult = c.nome.equals(this.nome) && c.telefono.equals(this.telefono) && c.indirizzo.equals(this.indirizzo)
							 && c.prezzo == this.prezzo && this.giorniChiusura.size() == c.giorniChiusura.size()
							 && c.citta.equals(this.citta);
		
		if(!semiResult)
			return false;
		
		Iterator<String> iC = c.giorniChiusura.iterator();
		while(iC.hasNext())
		{
			semiResult = semiResult && this.giorniChiusura.contains(iC.next());
			if(!semiResult)
				return false;
		}
		
		return true;
		*/
	}
	
	public int hashCode()
	{
		return this.id.intValue();
		/*
		return this.nome.length() + this.telefono.length() + this.indirizzo.length() +
			   this.citta.length() + this.giorniChiusura.size() + (int)this.prezzo;
		*/
	}
}