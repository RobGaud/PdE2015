package com.bean;

import java.util.HashSet;

import com.attivita.SessioneUtente.StatoSessione;

public class ListaStatiBean {

	private HashSet<StatoSessione> statiSuccessivi;
	private String httpCode;
	
	public ListaStatiBean(){
		statiSuccessivi = new HashSet<StatoSessione>();
	}
	
	public void addStatoSessione(StatoSessione s)
	{
		if( s != null ) this.statiSuccessivi.add(s);
	}
	
	public void removeStatoSessione(StatoSessione s)
	{
		if( s != null ) this.statiSuccessivi.remove(s);
	}

	public void setHttpCode(String code)
	{
		if( code != null ) this.httpCode = code;
	}
	
	public String getHttpCode()
	{
		return this.httpCode;
	}
}
