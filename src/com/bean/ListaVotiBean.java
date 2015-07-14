package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.VotoUomoPartita;

public class ListaVotiBean
{
	LinkedList<VotoUomoPartita> listaVoti;
	String httpCode;
	
	public ListaVotiBean()
	{
		listaVoti = new LinkedList<VotoUomoPartita>();
	}
	
	public void addVoto(VotoUomoPartita Voto)
	{
		if(Voto != null)
			this.listaVoti.add(Voto);
	}
	
	public void removeVoto(VotoUomoPartita Voto)
	{
		this.listaVoti.remove(Voto);
	}
	

	public List<VotoUomoPartita> getlistaVoti()
	{
		return (LinkedList<VotoUomoPartita>)this.listaVoti.clone();
	}

	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}
}
