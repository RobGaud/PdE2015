package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Gruppo;

public class ListaGruppiBean {
	private LinkedList<Gruppo> listaGruppi;
	private String result;
	private String httpCode;
	
	public ListaGruppiBean() {
		this.listaGruppi = new LinkedList<Gruppo>();
	}
	
	//TODO aggiunto setter per ListaGruppi
	public void setListaGruppi(LinkedList<Gruppo> listaGruppi) {
		this.listaGruppi = listaGruppi;
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
