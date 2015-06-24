package com.bean;

import java.util.HashSet;
import java.util.Set;

public class InfoCampoBean
{
	private String nome;	
	private String telefono;
	private String indirizzo;
	private float prezzo;
	private String citta;
	private HashSet<String> giorniChiusura;
	
	public InfoCampoBean() {
		this.giorniChiusura = new HashSet<String>();
		this.prezzo = -1.0f;
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
	
	public String getTelefono()
	{
		return this.telefono;
	}
	
	
	public void setTelefono(String newTel)
	{
		if( newTel != null )
			this.telefono = newTel;
	}
	
	
	public String getIndirizzo()
	{
		return this.indirizzo;
	}
	
	public void setIndirizzo(String newAddr)
	{
		if( newAddr != null )
			this.indirizzo = newAddr;
	}
	
	
	public float getPrezzo()
	{
		return this.prezzo;
	}
	
	
	public void setPrezzo(float newPrice)
	{
		if( newPrice >= 0)	// Vedi Prete
			this.prezzo = newPrice;
	}
	
	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		if(citta != null)
			this.citta = citta;
	}

	public void addGiornoChiusura(String giorno)
	{
		if(giorno != null)
			this.giorniChiusura.add(giorno);
	}
	
	
	public void removeGiornoChiusura(String giorno)
	{
		this.giorniChiusura.remove(giorno);
	}
	
	
	public Set<String> getGiorniChiusura()
	{
		return (HashSet<String>)this.giorniChiusura.clone();
	}
	
}
