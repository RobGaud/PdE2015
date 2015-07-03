package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class PartitaCalcio extends Partita {

	private static int nPartecipantiCalcio = 22;
	
	private PartitaCalcio() {}

	public PartitaCalcio(Date d) {
		super(d);
	}

	public int getNPartecipanti()
	{
		return nPartecipantiCalcio;
	}
}
