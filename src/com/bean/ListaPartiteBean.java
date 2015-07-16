package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Partita;

public class ListaPartiteBean {

	private LinkedList<Partita> listaPartite;
	private String result;
	private String httpCode;
	
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
	

	public List<Partita> getListaPartite()
	{
		return (LinkedList<Partita>)this.listaPartite.clone();
	}
	
	public void setListaPartite(LinkedList<Partita> listaPartite) {
		this.listaPartite = listaPartite;
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

}
