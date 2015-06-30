package com.modello;

public class EccezionePrecondizioni extends Exception
{
	private String messaggio;
	public EccezionePrecondizioni(String m)
	{
		messaggio = m;
	}
	public EccezionePrecondizioni()
	{
		messaggio = "Si e’ verificata una violazione delle precondizioni";
	}
	public String toString()
	{
		return messaggio;
	}
}