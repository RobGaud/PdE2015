package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class PartitaCalciotto extends Partita{

	private PartitaCalciotto() {}
	
	public PartitaCalciotto(Date d, float q) {
		super(d,q);
		// TODO Auto-generated constructor stub
	}

}
