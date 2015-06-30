package com.persistence;

import com.modello.*;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
 
/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {
 
	static {
		ObjectifyService.register(Campo.class);
		ObjectifyService.register(Giocatore.class);
		ObjectifyService.register(Gruppo.class);
		ObjectifyService.register(GruppoAperto.class);
		ObjectifyService.register(GruppoChiuso.class);
		ObjectifyService.register(Partita.class);
		ObjectifyService.register(PartitaCalcetto.class);
		ObjectifyService.register(PartitaCalciotto.class);
		ObjectifyService.register(PartitaCalcio.class);
		ObjectifyService.register(Invito.class);
		ObjectifyService.register(TipoLinkDestinatario.class);

	}
	 
	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}
	 
	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}