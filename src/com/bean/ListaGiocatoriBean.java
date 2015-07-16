package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Giocatore;

public class ListaGiocatoriBean
{
	private LinkedList<Giocatore> listaGiocatori;
	private String httpCode;
	private String result;
	
	public ListaGiocatoriBean() {
		this.listaGiocatori = new LinkedList<Giocatore>();
	}
	
	public void setListaGiocatori(LinkedList<Giocatore> listaGiocatori) {
		this.listaGiocatori = listaGiocatori;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}

	public void addGiocatore(Giocatore Giocatore)
	{
		if(Giocatore != null)
			this.listaGiocatori.add(Giocatore);
	}
	
	public void removeGiocatore(Giocatore Giocatore)
	{
		this.listaGiocatori.remove(Giocatore);
	}
	

	public List<Giocatore> getListaGiocatori()
	{
		return (LinkedList<Giocatore>)this.listaGiocatori.clone();
	}

}
