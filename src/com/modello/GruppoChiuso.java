package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.*;

@Subclass(index=true)
public class GruppoChiuso extends Gruppo {
	
	private GruppoChiuso(){}
	
	public GruppoChiuso(String n, String c) {
		super(n, c);
		// TODO Auto-generated constructor stub
	}
}
