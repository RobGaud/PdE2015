package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class PartitaCalcetto extends Partita {
	
	private static int nPartecipantiCalcetto = 10;

	private PartitaCalcetto(){}

	public PartitaCalcetto(Date d) {
		super(d);
		// TODO Auto-generated constructor stub
	}
	
	public int getNPartecipanti()
	{
		return nPartecipantiCalcetto;
	}

}
