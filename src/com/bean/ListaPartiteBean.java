package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Partita;

public class ListaPartiteBean {

	LinkedList<Partita> listaPartite;

	public ListaPartiteBean() {
		this.listaPartite = new LinkedList<Partita>();
	}
	
	public void addPartita(Partita Partita)
	{
		if(Partita != null)
			this.listaPartite.add(Partita);
	}
	
	public void removePartita(Partita Partita)
	{
		this.listaPartite.remove(Partita);
	}
	

	public List<Partita> getlistaPartite()
	{
		return (LinkedList<Partita>)this.listaPartite.clone();
	}

}
