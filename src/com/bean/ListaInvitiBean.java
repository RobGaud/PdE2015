package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Invito;

public class ListaInvitiBean
{
	LinkedList<Invito> listaInviti;
	String httpCode;
	
	public ListaInvitiBean()
	{
		listaInviti = new LinkedList<Invito>();
	}
	
	public void addInvito(Invito invito)
	{
		if(invito != null)
			this.listaInviti.add(invito);
	}
	
	public void removeInvito(Invito Invito)
	{
		this.listaInviti.remove(Invito);
	}
	

	public List<Invito> getlistaInviti()
	{
		return (LinkedList<Invito>)this.listaInviti.clone();
	}


	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}
}
