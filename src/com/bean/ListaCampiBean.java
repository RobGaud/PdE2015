package com.bean;

import com.modello.*;
import java.util.*;

public class ListaCampiBean {
	LinkedList<Campo> listaCampi;

	public ListaCampiBean() {
		this.listaCampi = new LinkedList<Campo>();
	}
	
	public void addCampo(Campo campo)
	{
		if(campo != null)
			this.listaCampi.add(campo);
	}
	
	public void removeCampo(Campo campo)
	{
		this.listaCampi.remove(campo);
	}
	

	public List<Campo> getListaCampi()
	{
		return (LinkedList<Campo>)this.listaCampi.clone();
	}
	
}
