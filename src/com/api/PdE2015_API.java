package com.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.*;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import static com.persistence.OfyService.ofy;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.modello.*;
import com.bean.*;

import javax.inject.Named;

//enum tipoPartita {CALCIO, CALCIOTTO, CALCETTO};

@Api(
	 	name = "pdE2015",
		version = "v1",
		description = "PdE2015_API"
	)
public class PdE2015_API
{
	public static final int CODICE_CALCETTO = 1;
	public static final int CODICE_CALCIOTTO = 2;
	public static final int CODICE_CALCIO = 3;

	
	Closeable session;
	private static final Logger log = Logger.getLogger(PdE2015_API.class.getName()); 
	
	@ApiMethod(
				name = "campo.inserisciCampo",
				path = "campo",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean inserisciCampo(InfoCampoBean campoBean)
	{
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).filter("indirizzo", campoBean.getIndirizzo())
													  .filter("citta", campoBean.getCitta()).list();
		
		if(l.size() > 0) {
			DefaultBean response = new DefaultBean();
			response.setResult("Campo già esistente!");
			tearDown();
			return response;
		}
			
		Campo campo = new Campo(campoBean.getNome(), campoBean.getTelefono(), campoBean.getIndirizzo(), campoBean.getCitta(), campoBean.getPrezzo());
		if (!campoBean.getGiorniChiusura().isEmpty()) {
			Set<String> giorniChiusura = campoBean.getGiorniChiusura();
			Iterator<String> it = giorniChiusura.iterator();
			while( it.hasNext() )
			{
				campo.addGiornoChiusura(it.next());
			}
		}
		
		//log.log(Level.INFO, "test message");
		//log.log(Level.WARNING, "CampoBean.indirizzo:"+campoBean.getIndirizzo());
		//log.log(Level.WARNING, "Campo.indirizzo:"+campo.getIndirizzo());
		
		// Use Objectify to save the campo and now() is used to make the call synchronously as we
	    // will immediately get a new page using redirect and we want the data to be present.
		ofy().save().entity(campo).now();
		tearDown();
		DefaultBean response = new DefaultBean();
		response.setResult("Campo inserito con successo");
		return response;
	}
	
	@ApiMethod(
			name = "campo.listaCampi",
			path = "campo",
			httpMethod = HttpMethod.GET
          )
	public ListaCampiBean listaCampi() {
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).list();
		tearDown();
		
		ListaCampiBean lc = new ListaCampiBean();
		Iterator<Campo> it = l.iterator();
		
		while(it.hasNext())
			lc.addCampo(it.next());
		
		return lc;
	}
	
	@ApiMethod(
			name = "campo.modificaCampo",
			path = "campo",
			httpMethod = HttpMethod.PUT
          )
	public DefaultBean modificaCampo(InfoCampoBean ic) {
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).filter("indirizzo", ic.getIndirizzo())
				 									.filter("citta", ic.getCitta()).list();
		
		if(l.size() <= 0) {
			DefaultBean response = new DefaultBean();
			response.setResult("Campo non esistente!");
			tearDown();
			return response;
		}
		
		Campo campo = l.get(0);
		
		if(ic.getNome() != null)
			campo.setNome(ic.getNome());
		if(ic.getPrezzo() >= 0.0)
			campo.setPrezzo(ic.getPrezzo());
		if(ic.getTelefono() != null)
			campo.getTelefono();
		
		Iterator<String> it = ic.getGiorniChiusura().iterator();
		
		while(it.hasNext())
			campo.addGiornoChiusura(it.next());
		
		ofy().save().entity(campo).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Campo aggiornato con successo!");
		return response;
	}
	
	@ApiMethod(
			name = "campo.eliminaCampo",
			path = "campo/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaCampo(InfoCampoBean campoBean) {
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).filter("indirizzo", campoBean.getIndirizzo())
				 									.filter("citta", campoBean.getCitta()).list();
		
		if(l.size() <= 0) {
			DefaultBean response = new DefaultBean();
			response.setResult("Campo non esistente!");
			tearDown();
			return response;
		}
		
		Campo campo = l.get(0);
		ofy().delete().entity(campo).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Campo eliminato con successo!");
		return response;
		
	}
	
	//TODO API Giocatore
	@ApiMethod(
			name = "giocatore.inserisciGiocatore",
			path = "giocatore",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciGiocatore(InfoGiocatoreBean giocatoreBean) {
		setUp();
		List<Giocatore> l = ofy().load().type(Giocatore.class).filter("email", giocatoreBean.getEmail()).list();
		
		if(l.size() > 0) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore già esistente!");
			tearDown();
			return response;
		}
		
		Giocatore g = new Giocatore(giocatoreBean.getNome(), giocatoreBean.getEmail(), giocatoreBean.getTelefono(),
									giocatoreBean.getRuoloPreferito(), giocatoreBean.getFotoProfilo());
		ofy().save().entity(g).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Giocatore inserito con successo!");
		return response;
	}
	
	@ApiMethod(
				name = "giocatore.listaGiocatori",
				path = "giocatore",
				httpMethod = HttpMethod.GET
	          )
	public ListaGiocatoriBean listaGiocatori() {
		setUp();
		List<Giocatore> l = ofy().load().type(Giocatore.class).list();
		tearDown();
		
		ListaGiocatoriBean lg = new ListaGiocatoriBean();
		Iterator<Giocatore> it = l.iterator();
		
		while(it.hasNext())
			lg.addGiocatore(it.next());
		
		return lg;
	}
	
	@ApiMethod(
				name = "giocatore.modificaGiocatore",
				path = "giocatore",
				httpMethod = HttpMethod.PUT
	          )
	public DefaultBean modificaGiocatore(InfoGiocatoreBean ig) {
		setUp();
		List<Giocatore> l = ofy().load().type(Giocatore.class)
							.filter("email", ig.getEmail()).list();
		
		if(l.size() <= 0) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		
		Giocatore g = l.get(0);
		
		if(ig.getNome() != null)
			g.setNome(ig.getNome());
		if(ig.getRuoloPreferito() != null)
			g.setRuoloPreferito(ig.getRuoloPreferito());
		if(ig.getTelefono() != null)
			g.setTelefono(ig.getTelefono());
		if(ig.getFotoProfilo() != null)
			g.setFotoProfilo(ig.getFotoProfilo());
		
		
		ofy().save().entity(g).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Giocatore aggiornato con successo!");
		return response;
	}
	
	@ApiMethod(
			name = "giocatore.eliminaGiocatore",
			path = "giocatore/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaGiocatore(InfoGiocatoreBean giocatoreBean) {
		setUp();
		List<Giocatore> l = ofy().load().type(Giocatore.class).filter("email", giocatoreBean.getEmail()).list();
		
		if(l.size() <= 0) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		
		Giocatore giocatore = l.get(0);
		ofy().delete().entity(giocatore).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Giocatore eliminato con successo!");
		return response;
		
	}
	
	//TODO API Gruppo
	
	@ApiMethod(
				name = "gruppo.inserisciGruppo",
				path = "gruppo",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean inserisciGruppo(InfoGruppoBean gruppoBean) {
		// NB: Il gruppo viene identificato attraverso l'id
		setUp();
		if(gruppoBean.isAperto()){
			GruppoAperto ga = new GruppoAperto(gruppoBean.getNome(), gruppoBean.getDataCreazione());
			ofy().save().entity(ga).now();
		}
		else {
			GruppoChiuso gc = new GruppoChiuso(gruppoBean.getNome(), gruppoBean.getDataCreazione());
			ofy().save().entity(gc).now();
		}
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Gruppo inserito con successo!");
		return response;
		
	}
	
	@ApiMethod(
				name = "gruppo.listaGruppi",
				path = "gruppo",
				httpMethod = HttpMethod.GET
	          )
	public ListaGruppiBean listaGruppi(@Named("aperto") boolean aperto) {
		ListaGruppiBean lg = new ListaGruppiBean();
		setUp();
		if(aperto) {
			List<GruppoAperto> l = ofy().load().type(GruppoAperto.class).list();
			Iterator<GruppoAperto> it =  l.iterator();
			
			while(it.hasNext())
				lg.addGruppo(it.next());
			
		}
		else {
			List<GruppoChiuso> l = ofy().load().type(GruppoChiuso.class).list();
			Iterator<GruppoChiuso> it =  l.iterator();
			
			while(it.hasNext())
				lg.addGruppo(it.next());
		}
		tearDown();
		return lg;
		
	}
	
	@ApiMethod(
				name = "gruppo.modificaGruppo",
				path = "gruppo",
				httpMethod = HttpMethod.PUT
	          )
	public DefaultBean modificaGruppo(InfoGruppoBean gruppoBean) {
		setUp();
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gruppoBean.getId()).now();
		
		if(gruppo == null) {
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
		if(gruppoBean.getNome() != null)
			gruppo.setNome(gruppoBean.getNome());
		
		ofy().save().entity(gruppo).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Gruppo aggiornato con successo!");
		return response;	
	}
	
	@ApiMethod(
				name = "gruppo.eliminaGruppo",
				path = "gruppo/delete",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean eliminaGruppo(InfoGruppoBean gruppoBean) {
		setUp();
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gruppoBean.getId()).now();
		
		if(gruppo == null) {
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
		ofy().delete().type(Gruppo.class).id(gruppoBean.getId()).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Gruppo eliminato con successo!");
		return response;
	}
	
	
	//API Partita
	@ApiMethod(
				name = "partita.inserisciPartita",
				path = "partita",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean inserisciPartita(InfoPartitaBean partitaBean)
	{
		setUp();
		switch(partitaBean.getTipo()) {
			case CODICE_CALCETTO:
				PartitaCalcetto pc_1 = new PartitaCalcetto(partitaBean.getDataOraPartita(), partitaBean.getQuota());
				ofy().save().entity(pc_1).now();
				break;
			case CODICE_CALCIOTTO:
				PartitaCalciotto pc_2 = new PartitaCalciotto(partitaBean.getDataOraPartita(), partitaBean.getQuota());
				ofy().save().entity(pc_2).now();
				break;
			case CODICE_CALCIO:
				PartitaCalcio pc_3 = new PartitaCalcio(partitaBean.getDataOraPartita(), partitaBean.getQuota());
				ofy().save().entity(pc_3).now();
				break;
			default:
				tearDown();
				DefaultBean response = new DefaultBean();
				response.setResult("Tipo partita non previsto!");
				return response;	
		}
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Partita inserita con successo!");
		return response;
	}
	
	@ApiMethod(
				name = "partita.listaPartite",
				path = "partita",
				httpMethod = HttpMethod.GET
	          )
	public ListaPartiteBean listaPartite(/*InfoPartitaBean partitaBean*/ @Named("tipo")int tipo) {
		setUp();
		//log.log(Level.WARNING,"tipo: "+partitaBean.getTipo());
		ListaPartiteBean lg = new ListaPartiteBean();
		switch(/*partitaBean.getTipo()*/ tipo) {
			case 1:
				List<PartitaCalcetto> l1 = ofy().load().type(PartitaCalcetto.class).list();
				Iterator<PartitaCalcetto> it1 =  l1.iterator();
				log.log(Level.WARNING,"CALCETTO");
				while(it1.hasNext())
					lg.addPartita(it1.next());
				break;
			case 2:
				List<PartitaCalciotto> l2 = ofy().load().type(PartitaCalciotto.class).list();
				Iterator<PartitaCalciotto> it2 =  l2.iterator();
				log.log(Level.WARNING,"CALCIOTTO");
				while(it2.hasNext())
					lg.addPartita(it2.next());
				break;
			case 3:
				List<PartitaCalcio> l3 = ofy().load().type(PartitaCalcio.class).list();
				Iterator<PartitaCalcio> it3 =  l3.iterator();
				log.log(Level.WARNING,"CALCIO");
				while(it3.hasNext())
					lg.addPartita(it3.next());
				break;
		}
		
		tearDown();
		return lg;
	}
	
	
	@ApiMethod(
			name = "partita.eliminaPartita",
			path = "partita/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaPartita(InfoPartitaBean partitaBean) {
		setUp();
		Partita partita = ofy().load().type(Partita.class).id(partitaBean.getId()).now();
		
		if(partita == null) {
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		
		ofy().delete().type(Partita.class).id(partitaBean.getId()).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Partita eliminata con successo!");
		return response;
	}
	
	@ApiMethod(
			name = "partita.modificaPartita",
			path = "partita",
			httpMethod = HttpMethod.PUT
          )
	public DefaultBean modificaPartita(InfoPartitaBean partitaBean) {
		setUp();
		Partita partita = ofy().load().type(Partita.class).id(partitaBean.getId()).now();
		
		if(partita == null) {
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		
		if(partitaBean.getDataOraPartita() != null)
			partita.setDataOraPartita(partitaBean.getDataOraPartita());
		if(partitaBean.getTipo() != 0)
		{
			/*	TODO: dovremmo eliminare l'oggetto nel Datastore
			 * 	e rimpiazzarlo con uno del nuovo tipo.
			 * 	PROBLEMA: cambia l'id: come influisce con la gestione
			 * 	delle Partite a cui un utente si è iscritto?
			 */
		}
		if(partitaBean.getQuota() != 0.0f)
			partita.setQuota(partitaBean.getQuota());

		ofy().save().entity(partita).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Partita aggiornata con successo!");
		return response;
		
	}
	
	@BeforeMethod
    private void setUp() {
        session = ObjectifyService.begin();
    }

    @AfterMethod
    private void tearDown() {
        session.close();
    }

}
