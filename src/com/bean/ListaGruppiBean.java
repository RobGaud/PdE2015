package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Gruppo;

public class ListaGruppiBean {
	LinkedList<Gruppo> listaGruppi;

	public ListaGruppiBean() {
		this.listaGruppi = new LinkedList<Gruppo>();
	}
	
	public void addGruppo(Gruppo Gruppo)
	{
		if(Gruppo != null)
			this.listaGruppi.add(Gruppo);
	}
	
	public void removeGruppo(Gruppo Gruppo)
	{
		this.listaGruppi.remove(Gruppo);
	}
	

	public List<Gruppo> getlistaGruppi()
	{
		return (LinkedList<Gruppo>)this.listaGruppi.clone();
	}
}
