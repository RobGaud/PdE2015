package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.Gruppo;
import com.modello.GruppoAperto;

public class ListaGruppiBean {
	private LinkedList<InfoGruppoBean> listaGruppi;
	private String result;
	private String httpCode;
	
	public ListaGruppiBean() {
		this.listaGruppi = new LinkedList<InfoGruppoBean>();
	}
	
	//TODO aggiunto setter per ListaGruppi
	public void setListaGruppi(LinkedList<InfoGruppoBean> listaGruppi) {
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
		{
			InfoGruppoBean infogruppo = convertiGruppo(Gruppo);
			this.listaGruppi.add(infogruppo);
		}
	}
	
	public void removeGruppo(Gruppo Gruppo)
	{
		this.listaGruppi.remove(Gruppo);
	}
	

	public List<InfoGruppoBean> getlistaGruppi()
	{
		return (LinkedList<InfoGruppoBean>)this.listaGruppi.clone();
	}
	
	public static InfoGruppoBean convertiGruppo(Gruppo g)
	{
		InfoGruppoBean infogruppo = new InfoGruppoBean();
		infogruppo.setCitta(g.getCitta());
		infogruppo.setNome(g.getNome());
		infogruppo.setId(g.getId());
		if( g.getClass().equals(GruppoAperto.class) ) infogruppo.setAperto(true);
		else infogruppo.setAperto(false);
		
		return infogruppo;
		
	}
}
