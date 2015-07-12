package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class PartitaCalciotto extends Partita{

	private static int nPartecipantiCalciotto = 16;
	
	private PartitaCalciotto() {}
	
	public PartitaCalciotto(Date d) {
		super(d);
		// TODO Auto-generated constructor stub
	}
	
	public int getNPartecipanti()
	{
		return nPartecipantiCalciotto;
	}
}
