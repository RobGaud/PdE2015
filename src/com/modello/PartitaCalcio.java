package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class PartitaCalcio extends Partita {

	private PartitaCalcio() {}

	public PartitaCalcio(Date d, float q) {
		super(d,q);
	}

}
