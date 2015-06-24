package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Giocatore;

public class ListaGiocatoriBean {
	LinkedList<Giocatore> listaGiocatori;

	public ListaGiocatoriBean() {
		this.listaGiocatori = new LinkedList<Giocatore>();
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
