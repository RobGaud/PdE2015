package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Giocatore;

public class ClassificaVotiBean
{
	String httpCode;

	LinkedList<Giocatore> elencoGiocatori;
	LinkedList<Integer> elencoNumeroVoti;
	
	public ClassificaVotiBean()
	{
		elencoGiocatori = new LinkedList<Giocatore>();
		elencoNumeroVoti = new LinkedList<Integer>();
	}
	
	public void addGiocatore(Giocatore g)
	{
		if(g != null)
			this.elencoGiocatori.add(g);
	}
	
	public void removeGiocatore(Giocatore g)
	{
		this.elencoGiocatori.remove(g);
	}
	
	public List<Giocatore> getlistaGiocatori()
	{
		return (LinkedList<Giocatore>)this.elencoGiocatori.clone();
	}
	
	public void addNumeroVoti(int n)
	{
		if(n >= 0)
			this.elencoNumeroVoti.add(n);
	}
	
	public void removeNumeroVoti(int n)
	{
		this.elencoNumeroVoti.remove(n);
	}
	
	public List<Integer> getlistaNumeroVoti()
	{
		return (LinkedList<Integer>)this.elencoNumeroVoti.clone();
	}
	
	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}
}
