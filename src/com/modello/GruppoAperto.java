package com.modello;

import java.util.Date;
import com.googlecode.objectify.annotation.*;

@Subclass(index=true)
public class GruppoAperto extends Gruppo {
	
	private GruppoAperto(){}
	
	public GruppoAperto(String n, String c) {
		super(n, c);
		// TODO Auto-generated constructor stub
	}
	

}
