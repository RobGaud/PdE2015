package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class PartitaCalcetto extends Partita {
	
	private PartitaCalcetto(){}

	public PartitaCalcetto(Date d, float q) {
		super(d,q);
		// TODO Auto-generated constructor stub
	}

}
