package com.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.*;
import com.google.appengine.api.datastore.Link;
import com.google.gwt.dev.ModuleTabPanel.Session;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.attivita.SessioneUtente;
import com.attivita.SessioneUtente.StatoSessione;

import static com.persistence.OfyService.ofy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.modello.*;
import com.modello.Partita.Stato;
import com.attivita.SessioneUtente.StatoSessione;
import com.bean.*;
import com.auth.*;

import javax.annotation.Nullable;
import javax.inject.*;
import javax.naming.PartialResultException;

//enum tipoPartita {CALCIO, CALCIOTTO, CALCETTO};

@Api(
	 	name = "pdE2015",
		version = "v1",
		description = "PdE2015_API",
		scopes = {Constants.EMAIL_SCOPE},
	    clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID},
	    audiences = {Constants.ANDROID_AUDIENCE}
	)
public class PdE2015_API
{
	public static final int CODICE_CALCETTO = 1;
	public static final int CODICE_CALCIOTTO = 2;
	public static final int CODICE_CALCIO = 3;
	
	private static final String CREATED = "201 Created";
	private static final String OK = "200 OK"; 
	private static final String PRECONDITION_FAILED = "412 Precondition Failed";
	private static final String CONFLICT = "409 Conflict";
	private static final String UNAUTHORIZED = "401 Unauthorized";
	private static final String INTERNAL_SERVER_ERROR = "500 Internal Server Error";
	private static final String BAD_REQUEST = "400 Bad Request";
	private static final String NOT_FOUND = "404 Not Found";
	
	Closeable session;
	private static final Logger log = Logger.getLogger(PdE2015_API.class.getName());
	
	
	@ApiMethod(
			name = "api.tearDown",
			path = "api/teardown",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean singleTearDown(@Named("unused")Long unused)
	{
		log.log(Level.SEVERE, "endlessteardown()");
		tearDown();
		return new DefaultBean();
	}
	
	@ApiMethod(
				name = "campo.inserisciCampo",
				path = "campo",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean inserisciCampo(InfoCampoBean campoBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).filter("indirizzo", campoBean.getIndirizzo())
													  .filter("citta", campoBean.getCitta()).list();
		
		if(l.size() > 0) return sendResponse("Campo già esistente!", PRECONDITION_FAILED);
			
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
		
		return sendResponseCreated("Campo inserito con successo.", CREATED, campo.getId());
	}
	
	@ApiMethod(
			name = "campo.listaCampi",
			path = "campo",
			httpMethod = HttpMethod.GET
          )
	public ListaCampiBean listaCampi() {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).list();
		log.log(Level.SEVERE, "faccio tearDown().");
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
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).filter("indirizzo", ic.getIndirizzo())
				 									.filter("citta", ic.getCitta()).list();
		
		if(l.size() <= 0) return sendResponse("Campo non esistente!", PRECONDITION_FAILED);
		
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
		
		return sendResponse("Campo aggiornato con successo!", OK);
	}
	
	@ApiMethod(
			name = "campo.eliminaCampo",
			path = "campo/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaCampo(InfoCampoBean campoBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		List<Campo> l = ofy().load().type(Campo.class).filter("indirizzo", campoBean.getIndirizzo())
				 									.filter("citta", campoBean.getCitta()).list();
		
		if(l.size() <= 0) return sendResponse("Campo non esistente!", PRECONDITION_FAILED);
		
		Campo campo = l.get(0);
		ofy().delete().entity(campo).now();
		
		return sendResponse("Campo eliminato con successo!", OK);
	}
	
	//TODO API Giocatore
	@ApiMethod(
			name = "giocatore.inserisciGiocatore",
			path = "giocatore",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciGiocatore(InfoGiocatoreBean giocatoreBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(giocatoreBean.getEmail()).now();
		
		if(giocatore != null) return sendResponse("Giocatore già esistente!", PRECONDITION_FAILED);
		
		giocatore = new Giocatore(giocatoreBean.getNome(), giocatoreBean.getEmail(), giocatoreBean.getTelefono(),
									giocatoreBean.getRuoloPreferito(), giocatoreBean.getFotoProfilo());
		ofy().save().entity(giocatore).now();
		
		return sendResponse("Giocatore inserito con successo!", CREATED);
	}
	
	@ApiMethod(
				name = "giocatore.listaGiocatori",
				path = "giocatore",
				httpMethod = HttpMethod.GET
	          )
	public ListaGiocatoriBean listaGiocatori() {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		List<Giocatore> l = ofy().load().type(Giocatore.class).list();
		log.log(Level.SEVERE, "faccio tearDown().");
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
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Giocatore g = ofy().load().type(Giocatore.class).id(ig.getEmail()).now();
		
		if(g == null) sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		if(ig.getNome() != null)
			g.setNome(ig.getNome());
		if(ig.getRuoloPreferito() != null)
			g.setRuoloPreferito(ig.getRuoloPreferito());
		if(ig.getTelefono() != null)
			g.setTelefono(ig.getTelefono());
		if(ig.getFotoProfilo() != null)
			g.setFotoProfilo(ig.getFotoProfilo());
		
		
		ofy().save().entity(g).now();
		
		return sendResponse("Giocatore aggiornato con successo!", OK);
	}
	
	@ApiMethod(
			name = "giocatore.eliminaGiocatore",
			path = "giocatore/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaGiocatore(InfoGiocatoreBean giocatoreBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(giocatoreBean.getEmail()).now();
		
		if(giocatore == null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		ofy().delete().entity(giocatore).now();
		
		return sendResponse("Giocatore eliminato con successo!", OK);
	}
	
	//TODO API Gruppo
	
	@ApiMethod(
				name = "gruppo.inserisciGruppo",
				path = "gruppo",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean inserisciGruppo(InfoGruppoBean gruppoBean) {
		// NB: Il gruppo viene identificato attraverso l'id
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Long idGruppo;
		if(gruppoBean.isAperto()){
			GruppoAperto ga = new GruppoAperto(gruppoBean.getNome(), gruppoBean.getCitta());
			ofy().save().entity(ga).now();
			idGruppo = ga.getId();
		}
		else {
			GruppoChiuso gc = new GruppoChiuso(gruppoBean.getNome(), gruppoBean.getCitta());
			ofy().save().entity(gc).now();
			idGruppo = gc.getId();
		}
		
		return sendResponseCreated("Gruppo inserito con successo!", CREATED, idGruppo);
	}
	
	@ApiMethod(
				name = "gruppo.listaGruppi",
				path = "gruppo",
				httpMethod = HttpMethod.GET
	          )
	public ListaGruppiBean listaGruppi(@Named("aperto") boolean aperto, @Named("citta") String citta) {
		ListaGruppiBean lg = new ListaGruppiBean();
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		if(aperto)
		{
			List<GruppoAperto> l;
			if( citta.equals("") ) 
				l = ofy().load().type(GruppoAperto.class).list();
			else l = ofy().load().type(GruppoAperto.class).filter("citta", citta).list();
			Iterator<GruppoAperto> it =  l.iterator();
			
			while(it.hasNext())
				lg.addGruppo(it.next());
		}
		else {
			List<GruppoChiuso> l;
			if( citta.equals("") ) 
				l = ofy().load().type(GruppoChiuso.class).list();
			else l = ofy().load().type(GruppoChiuso.class).filter("citta", citta).list();
			Iterator<GruppoChiuso> it =  l.iterator();
			
			while(it.hasNext())
				lg.addGruppo(it.next());
		}
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		return lg;
	}
	
	@ApiMethod(
				name = "gruppo.modificaGruppo",
				path = "gruppo",
				httpMethod = HttpMethod.PUT
	          )
	public DefaultBean modificaGruppo(InfoGruppoBean gruppoBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gruppoBean.getId()).now();
		
		if(gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		if(gruppoBean.getNome() != null)
			gruppo.setNome(gruppoBean.getNome());
		
		ofy().save().entity(gruppo).now();
		
		return sendResponse("Gruppo aggiornato con successo!", OK);
	}
	
	@ApiMethod(
				name = "gruppo.eliminaGruppo",
				path = "gruppo/delete",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean eliminaGruppo(InfoGruppoBean gruppoBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gruppoBean.getId()).now();
		
		if(gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		ofy().delete().type(Gruppo.class).id(gruppoBean.getId()).now();
		
		return sendResponse("Gruppo eliminato con successo.", OK);
	}
	
	
	//TODO API Partita
	@ApiMethod(
				name = "partita.inserisciPartita",
				path = "partita",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean inserisciPartita(InfoPartitaBean partitaBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Long idPartita;
		switch(partitaBean.getTipo()) {
			case CODICE_CALCETTO:
				PartitaCalcetto pc_1 = new PartitaCalcetto(partitaBean.getDataOraPartita());
				ofy().save().entity(pc_1).now();
				idPartita = pc_1.getId();
				break;
			case CODICE_CALCIOTTO:
				PartitaCalciotto pc_2 = new PartitaCalciotto(partitaBean.getDataOraPartita());
				ofy().save().entity(pc_2).now();
				idPartita = pc_2.getId();
				break;
			case CODICE_CALCIO:
				PartitaCalcio pc_3 = new PartitaCalcio(partitaBean.getDataOraPartita());
				ofy().save().entity(pc_3).now();
				idPartita = pc_3.getId();
				break;
			default:
				log.log(Level.SEVERE, "E' stato tentato l'inserimento di una partita con un tipo non previsto!");
				return sendResponseCreated("Tipo partita non previsto!", PRECONDITION_FAILED, null);	
		}
		
		return sendResponseCreated("Partita inserita con successo!", CREATED, idPartita);
	}
	
	@ApiMethod(
			name = "partita.eliminaPartita",
			path = "partita/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaPartita(InfoPartitaBean partitaBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		
		Partita partita = ofy().load().type(Partita.class).id(partitaBean.getId()).now();
		if(partita == null)
			return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		ofy().delete().type(Partita.class).id(partitaBean.getId()).now();
		
		return sendResponse("Partita eliminata con successo!", OK);
	}
	
	@ApiMethod(
			name = "partita.modificaPartita",
			path = "partita",
			httpMethod = HttpMethod.PUT
          )
	public DefaultBean modificaPartita(InfoPartitaBean partitaBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Partita partita = ofy().load().type(Partita.class).id(partitaBean.getId()).now();
		
		if(partita == null)
			return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		if(partitaBean.getDataOraPartita() != null)
			partita.setDataOraPartita(partitaBean.getDataOraPartita());

		ofy().save().entity(partita).now();
		
		return sendResponse("Partita aggiornata con successo!", OK);
	}
	
	@ApiMethod(
				name = "partita.getNDisponibili",
				path = "partita/ndisponibili",
				httpMethod = HttpMethod.GET
	          )
	public NDisponibiliBean getNDisponibili(@Named("idPartita")Long idPartita)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if(partita == null) {
			NDisponibiliBean response = new NDisponibiliBean();
			response.setResult("Partita non esistente!");
			response.setHttpCode(PRECONDITION_FAILED);
			response.setnDisponibili(-1);
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return response;
		}
		
		int nDisponibili = 0;
		try {
			Iterator<Long> it = partita.getLinkDisponibile().iterator();
			while(it.hasNext())
			{
				TipoLinkDisponibile link = ofy().load().type(TipoLinkDisponibile.class).id(it.next()).now();
				nDisponibili += link.getnAmici()+1;
			}
			NDisponibiliBean response = new NDisponibiliBean();
			response.setResult("Calcolo effettuato con successo!");
			response.setHttpCode(OK);
			response.setnDisponibili(nDisponibili);
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return response;
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Nessun giocatore ha dato disponibilità per la partita"+partita.getId()+"!");
			NDisponibiliBean response = new NDisponibiliBean();
			response.setResult("Nessun giocatore ha dato disponibilità per la partita!");
			response.setHttpCode(INTERNAL_SERVER_ERROR);
			response.setnDisponibili(-1);
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return response;
		}
	}
	
	
	
	//TODO API Invito
	@ApiMethod(
			name = "invito.inserisciInvito",
			path = "invito",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciInvito(InfoInvitoBean invitoBean)
	{
		if( invitoBean.getEmailDestinatario().equals(invitoBean.getEmailMittente()))
			return sendResponse("Mittente e destinatario coincidenti!", PRECONDITION_FAILED);
		
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo se il mittente esiste nel Datastore
		Giocatore mittente = ofy().load().type(Giocatore.class).id(invitoBean.getEmailMittente()).now();
		if( mittente == null) return sendResponse("Mittente non esistente!", PRECONDITION_FAILED);
		
		//Controllo se il destinatario esiste nel Datastore
		Giocatore destinatario = ofy().load().type(Giocatore.class).id(invitoBean.getEmailDestinatario()).now();
		if( destinatario == null) return sendResponse("Destinatario non esistente!", PRECONDITION_FAILED);
		
		//Controllo se il gruppo esiste nel Datastore
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(invitoBean.getIdGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		//TODO controllare che il mittente faccia parte del gruppo, ovviamente.
		//Controllo che il destinatario non faccia già parte del gruppo;
		//devo quindi controllare che non esista un tipoLinkIscritto
		//con questo gruppo e questo giocatore
		List<TipoLinkIscritto> list = ofy().load().type(TipoLinkIscritto.class)
									  .filter("gruppo", gruppo.getId())
									  .filter("giocatore", destinatario.getEmail()).list();
		if( list.size()>0) return sendResponse("Il destinatario fa già parte del gruppo!", PRECONDITION_FAILED);
		
		//Controllo che il destinatario non sia già stato invitato nel gruppo
		//Non deve quindi esistere, in destinatario.linkdestinatario, nessun id
		//di oggetti Invito collegati al gruppo in questione
		Iterator<Long> it = destinatario.getLinkDestinatario().iterator();
		while(it.hasNext())
		{
			Long idInvito = it.next();
			Invito i = ofy().load().type(Invito.class).id(idInvito).now();
			if( i == null )
			{
				log.log(Level.SEVERE, "Il destinatario "+destinatario.getEmail()+
						" ha memorizzato l'id di un Invito che non esiste!");
				continue;
			}
			try {
				if(i.getGruppo().equals(gruppo.getId()))
					return sendResponse("Il destinatario è già stato invitato nel gruppo!", PRECONDITION_FAILED);
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "L'invito "+i.getId()+
						" non ha memorizzato l'id di un gruppo!");
				continue;
			}
		}
		//Carico preventivamente l'invito sul Datastore per ottenere un id
		Invito invito = new Invito(invitoBean.getEmailMittente(), invitoBean.getIdGruppo());
		ofy().save().entity(invito).now();
		//Inserisco il link e ricarico le entità sul Datastore
		destinatario.inserisciLinkDestinatario(invito.getId());
		invito.inserisciLinkDestinatario(destinatario.getEmail());
		ofy().save().entity(invito).now();
		ofy().save().entity(destinatario).now();

		return sendResponseCreated("Invito inserito con successo!", CREATED, invito.getId());
	}
	
	@ApiMethod(
				name = "invito.eliminaInvito",
				path = "invito/delete",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean eliminaInvito(@Named("emailDestinatario")String emailDestinatario,
									 @Named("idInvito")Long idInvito)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo se l'invito esiste nel Datastore
		Invito invito = ofy().load().type(Invito.class).id(idInvito).now();
		if( invito == null ) return sendResponse("Invito non esistente!", PRECONDITION_FAILED);
		
		//Controllo se il destinatario esiste nel Datastore
		Giocatore destinatario= ofy().load().type(Giocatore.class).id(emailDestinatario).now();
		if( destinatario == null ) return sendResponse("Destinatario non esistente!", PRECONDITION_FAILED);
		
		//Rimozione idLink da destinatario.linkDestinatario
		destinatario.eliminaLinkDestinatario(idInvito);
		ofy().save().entity(destinatario).now();
		//Rimozione invito dal Datastore
		ofy().delete().type(Invito.class).id(idInvito).now();
		
		return sendResponse("Invito rimosso con successo!", OK);
	}
	
	
	//TODO API VotoUomoPartita
	@ApiMethod(
				name = "voto.inserisciVotoUomoPartita",
				path = "voto",
				httpMethod = HttpMethod.PUT
	          )
	public DefaultBean inserisciVotoUomoPartita(InfoVotoUomoPartitaBean votoBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo che il votante esista nel Datastore
		Giocatore votante = ofy().load().type(Giocatore.class).id(votoBean.getVotante()).now();
		if( votante == null ) return sendResponse("Votante non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il votato esista nel Datastore
		Giocatore votato = ofy().load().type(Giocatore.class).id(votoBean.getVotato()).now();
		if( votato == null ) return sendResponse("Votato non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il votato esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(votoBean.getLinkVotoPerPartita()).now();
		if( partita == null ) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che non esista già un voto da parte dello stesso votante per la stessa partita
		Set<Long> idVoti = partita.getLinkVotoPerPartita();
		Iterator<Long> it = idVoti.iterator();
		while(it.hasNext())
		{
			VotoUomoPartita v = ofy().load().type(VotoUomoPartita.class).id(it.next()).now();
			try {
				log.log(Level.WARNING, "v.getVotante()="+v.getVotanteUP()+", votante.getEmail()="+votante.getEmail());
				if( v.getVotanteUP().equals(votante.getEmail()) )
					return sendResponse("Il votante ha già votato per questa partita!", PRECONDITION_FAILED);
				
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "Il voto "+v.getId()+
						" non ha memorizzato l'id di un votante!");
				continue;
			}
		}
		//Carico il voto sul Datastore
		VotoUomoPartita voto = new VotoUomoPartita(votoBean.getCommento());
		voto.setVotatoUP(votoBean.getVotato());
		voto.setVotanteUP(votoBean.getVotante());
		voto.inserisciLinkVotoPerPartita(votoBean.getLinkVotoPerPartita());
		ofy().save().entity(voto).now();
		//Aggiorno la partita inserendo l'id del nuovo voto e la ricarico sul Datastore
		partita.inserisciLinkPerPartita(voto.getId());
		ofy().save().entity(partita).now();
		
		return sendResponse("Voto inserito con successo!", CREATED);
	}
	
	@ApiMethod(
			name = "voto.eliminaVotoUomoPartita",
			path = "voto/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaVotoUomoPartita(@Named("emailVotante")String emailVotante,
											  @Named("idVoto")Long idVoto)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo se il voto esiste nel Datastore
		VotoUomoPartita voto = ofy().load().type(VotoUomoPartita.class).id(idVoto).now();
		if( voto == null ) return sendResponse("Voto non esistente!", PRECONDITION_FAILED);
		
		//Controllo se il votante esiste nel Datastore
		Giocatore votante= ofy().load().type(Giocatore.class).id(emailVotante).now();
		if( votante == null ) return sendResponse("Votante non esistente!", PRECONDITION_FAILED);

		
		try {
			//Rimozione idVoto da partita.idVoti
			Partita partita = ofy().load().type(Partita.class).id(voto.getLinkVotoPerPartita()).now();
			partita.eliminaLinkVotoPerPartita(idVoto);
			ofy().save().entity(partita).now();
			//Rimozione voto dal Datastore
			ofy().delete().type(VotoUomoPartita.class).id(idVoto).now();
			
			return sendResponse("Voto rimosso con successo!", OK);
			
		} catch (EccezioneMolteplicitaMinima e)
		{
			return sendResponse("Il voto "+idVoto+"non ha memorizzato l'id della partita!", INTERNAL_SERVER_ERROR);
		}
	}
	
	//TODO API associazione Disponibile
	@ApiMethod(
				name = "disponibile.inserisciDisponibile",
				path = "disponibile",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean inserisciLinkDisponibile(InfoGestionePartiteBean gestioneBean)
	{
		//TODO controllo se il giocatore appartiene al gruppo?
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore non abbia già dato disponibilità per questa partita
		Long idLink = null;
		try {
			TipoLinkDisponibile link = new TipoLinkDisponibile(gestioneBean.getEmailGiocatore(),
										   gestioneBean.getIdPartita(), gestioneBean.getnAmici());
			List<TipoLinkDisponibile> linkList = ofy().load().type(TipoLinkDisponibile.class)
												 .filter("giocatore", gestioneBean.getEmailGiocatore())
												 .filter("partita", gestioneBean.getIdPartita()).list();
			if(linkList.contains(link))
				return sendResponse("Il giocatore ha già dato la sua disponibilità per questa partita!", PRECONDITION_FAILED);
			
			//Carico il link sul Datastore
			ofy().save().entity(link).now();
			idLink = link.getId();
			
			//Aggiorno giocatore e partita
			giocatore.inserisciLinkDisponibile(idLink);
			partita.inserisciLinkDisponibile(idLink);
		
		} catch (EccezionePrecondizioni e) {
			return sendResponse("Almeno uno dei campi di interesse è null!", BAD_REQUEST);
		}
		//Li carico nel datastore
		ofy().save().entity(giocatore).now();
		ofy().save().entity(partita).now();
	
		return sendResponseCreated("Disponibilità inserita con successo.", CREATED, idLink);
	}
	
	@ApiMethod(
			name = "disponibile.eliminaDisponibile",
			path = "disponibile/delete",
			httpMethod = HttpMethod.POST
			)
	public DefaultBean eliminaLinkDisponibile(InfoGestionePartiteBean gestioneBean)
	{
		log.log(Level.SEVERE, "Devo eliminare il giocatore "+gestioneBean.getEmailGiocatore()
							 +" dalla partita "+gestioneBean.getIdPartita());
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore abbia dato disponibilità per la partita
		List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
										   .filter("giocatore", gestioneBean.getEmailGiocatore())
										   .filter("partita", gestioneBean.getIdPartita()).list();
		if( listLink.size() == 0 ) return sendResponse("Il giocatore non ha dato la sua disponibilità per la partita!", INTERNAL_SERVER_ERROR);
		
		TipoLinkDisponibile link = listLink.get(0);
		//Aggiorno giocatore e partita
		giocatore.eliminaLinkDisponibile(link.getId());
		partita.eliminaLinkDisponibile(link.getId());
		//Elimino il link dal Datastore
		ofy().delete().entity(link).now();		
		//Li carico nel datastore
		ofy().save().entity(giocatore).now();
		ofy().save().entity(partita).now();
		
		return sendResponse("Disponibilità rimossa non successo.", OK);
	}
	
	
	//TODO API associazione Propone
	@ApiMethod(
				name = "propone.inserisciPropone",
				path = "propone",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean inserisciLinkPropone(InfoGestionePartiteBean gestioneBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//TODO: vediamola bene sta cosa
		//Controllo che la partita non abbia già un giocatore che l'ha proposta
		if( partita.quantiPropone() == 1 )
			return sendResponse("La partita ha già memorizzato un giocatore che l'ha proposta!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore figuri tra i disponibili per la partita
		try{
			List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
												   .filter("giocatore", gestioneBean.getEmailGiocatore())
												   .filter("partita", gestioneBean.getIdPartita()).list();
			if( listLink.size() == 0 )
				return sendResponse("Il giocatore non ha dato la sua disponibilità per la partita!", PRECONDITION_FAILED);
			
			TipoLinkDisponibile link = listLink.get(0);
			if( !partita.getLinkDisponibile().contains(link.getId()) )
				return sendResponse("Chi propone una partita deve figurare automaticamente tra i disponibili!", PRECONDITION_FAILED);
			
		}catch(EccezioneMolteplicitaMinima e)
		{
			log.log(Level.SEVERE, "La partita "+partita.getId()+
					" non ha nessun giocatore disponibile!");
			return sendResponse("La partita deve avere almeno un giocatore disponibile!", INTERNAL_SERVER_ERROR);
		}
		//Aggiorno partita e la ricarico sul Datastore
		partita.inserisciPropone(gestioneBean.getEmailGiocatore());
		ofy().save().entity(partita).now();
		
		return sendResponse("Link propone inserito con successo!", CREATED);
	}
	
	//Ha senso eliminare chi ha proposto una partita? Forse giusto se il giocatore organizza
	//e poi disdice, e quindi toglie la disponibilità, e allora gli togliamo anche il ruolo
	//di "proponitore"... boh, io l'ho fatta, poi se non serve la togliamo...
	@ApiMethod(
			name = "propone.eliminaPropone",
			path = "propone/delete",
			httpMethod = HttpMethod.POST
			)
	public DefaultBean eliminaLinkPropone(InfoGestionePartiteBean gestioneBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore sia effettivamente chi ha proposto la partita
		try {
			if( !partita.getPropone().equals(gestioneBean.getEmailGiocatore()) )
				return sendResponse("Il giocatore non è colui che ha proposto la partita!", CONFLICT);
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+partita.getId()+" non ha un link propone!");
		}
		//Aggiorno la partita e la ricarico sul Datastore
		partita.eliminaPropone();
		ofy().save().entity(partita).now();
		
		return sendResponse("Link propone rimosso con successo.", OK);
	}
	
	//TODO API associazione Gioca
	@ApiMethod(
				name = "gioca.inserisciLinkGioca",
				path = "gioca",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean inserisciLinkGioca(InfoGestionePartiteBean gestioneBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore figuri tra i disponibili per la partita
		try{
			List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
												   .filter("giocatore", gestioneBean.getEmailGiocatore())
												   .filter("partita", gestioneBean.getIdPartita()).list();
			
			if( listLink.size() == 0 )
				return sendResponse("Il giocatore non ha dato la sua disponibilità per la partita!", INTERNAL_SERVER_ERROR);
			
			TipoLinkDisponibile link = listLink.get(0);
			if( !partita.getLinkDisponibile().contains(link.getId()) )
				return sendResponse("Chi propone una partita deve figurare automaticamente tra i disponibili!", INTERNAL_SERVER_ERROR);
			
		}catch(EccezioneMolteplicitaMinima e)
		{
			log.log(Level.SEVERE, "La partita "+partita.getId()+
					" non ha nessun giocatore disponibile!");
			return sendResponse("La partita deve avere almeno un giocatore disponibile!", INTERNAL_SERVER_ERROR);
		}
		//Controllo non esistenza link gioca - Non posso fare questo controllo:
		//Al primo inserimento di un giocatore come linkGioca, partita.getLinkGioca()
		//Lancerà sempre una EccezioneMolteplicitaMinima; dobbiamo spostare il test su Partita
		
		//Inserimento link gioca - aggiornamento partita e giocatore
		partita.inserisciLinkGioca(giocatore.getEmail());
		giocatore.inserisciLinkGioca(partita.getId());
		ofy().save().entity(partita).now();
		ofy().save().entity(giocatore).now();
		
		return sendResponse("LinkGioca inserito con successo.", CREATED);
	}
	
	@ApiMethod(
				name = "gioca.eliminaLinkGioca",
				path = "gioca/delete",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean eliminaLinkGioca(InfoGestionePartiteBean gestioneBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore giochi effettivamente la partita
		try {
			if( !partita.getLinkGioca().contains(gestioneBean.getEmailGiocatore()) )
				return sendResponse("Il giocatore non gioca la partita!", INTERNAL_SERVER_ERROR);
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+partita.getId()+" non ha nessun LinkGioca!");
		}
		//Aggiorno partita e giocatore e li ricarico sul Datastore
		partita.eliminaLinkGioca(giocatore.getEmail());
		giocatore.eliminaLinkGioca(partita.getId());
		ofy().save().entity(partita).now();
		ofy().save().entity(giocatore).now();
		
		return sendResponse("LinkGioca rimosso con successo.", OK);
	}
	
	// TODO API Iscritto
	@ApiMethod(
			name = "iscritto.inserisciLinkIscritto",
			path = "iscritto",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkIscritto(InfoIscrittoGestisceBean iscrittoBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(iscrittoBean.getGiocatore()).now();
		if( giocatore == null) return sendResponse("Destinatario non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(iscrittoBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo iscrizione unica
		if( gruppo.quantiIscritti() != 0 )
		{
			try {
				Iterator<Long> it = gruppo.getGiocatoriIscritti().iterator();
				
				while(it.hasNext()) {
					Long idLink = it.next();
					TipoLinkIscritto link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
					if(link.getGiocatore().equals(iscrittoBean.getGiocatore()))
						return sendResponse("Giocatore già iscritto!", CONFLICT);
				}
			}
			catch(EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "Il Gruppo "+ gruppo.getId() +
						"non rispetta la molteplicità minima!");
			}
		}
		
		// Creazione TipoLinkIscritto ed salvataggio/aggiornamento
		Long idLink = null;
		try {
			TipoLinkIscritto link = new TipoLinkIscritto(iscrittoBean.getGiocatore(), iscrittoBean.getGruppo());
			ofy().save().entity(link).now();
			idLink = link.getId();
			giocatore.inserisciLinkIscritto(idLink);
			ofy().save().entity(giocatore).now();
			gruppo.inserisciLinkIscritto(idLink);
			ofy().save().entity(gruppo).now();
		}
		catch(EccezionePrecondizioni e) {
			log.log(Level.SEVERE, "Almeno uno tra iscrittoBean.getGruppo() e iscrittoBean.getGIocatore() ha restituito null");
			return sendResponse("Errore imprevisto!", PRECONDITION_FAILED);
		}
		
		return sendResponseCreated("TipoLinkIscritto inserito con successo!", CREATED, idLink);
	}
	
	@ApiMethod(
			name = "iscritto.eliminaLinkIscritto",
			path = "iscritto/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaLinkIscritto(InfoIscrittoGestisceBean iscrittoBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(iscrittoBean.getGiocatore()).now();
		if( giocatore == null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(iscrittoBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza TipoLinkIscritto
		boolean found = false;
		TipoLinkIscritto link = null;
		try {
			Iterator<Long> it = gruppo.getGiocatoriIscritti().iterator();
				
			while(it.hasNext()) {
				Long idLink = it.next();
				link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
				if(link.getGiocatore().equals(iscrittoBean.getGiocatore())) {
					found = true;
					break;
				}
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il Gruppo "+ gruppo.getId() +
					"non rispetta la molteplicità minima!");
		}
		
		if(!found) return sendResponse("Giocatore non iscritto!", BAD_REQUEST);
		
		// Rimozione TipoLinkIscritto e salvataggio/aggiornamento
		gruppo.eliminaLinkIscritto(link.getId());
		ofy().save().entity(gruppo).now();
		giocatore.eliminaLinkIscritto(link.getId());
		ofy().save().entity(giocatore).now();
		ofy().delete().entity(link).now();
		
		return sendResponse("TipoLinkRimosso con successo!", OK);	
	}
	
	@ApiMethod(
			name = "iscritto.estIscritto",
			path = "iscritto/estiscritto",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean estIscritto(InfoIscrittoGestisceBean iscrittoBean)
	{
		// Il metodo estIscritto verifica che il giocatore figuri tra gl iscritti del gruppo;
		// In caso positivo, restituisce nel campo idCreated l'id del TipoLinkIscritto in questione.
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(iscrittoBean.getGiocatore()).now();
		if( giocatore == null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(iscrittoBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza TipoLinkIscritto
		boolean found = false;
		Long idLink = null;
		TipoLinkIscritto link = null;
		try {
			Iterator<Long> it = gruppo.getGiocatoriIscritti().iterator();
				
			while(it.hasNext()) {
				idLink = it.next();
				link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
				if(link.getGiocatore().equals(iscrittoBean.getGiocatore())) {
					found = true;
					break;
				}
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il Gruppo "+ gruppo.getId() +
					"non rispetta la molteplicità minima!");
			return sendResponse("Il gruppo non ha giocatori iscritti!", INTERNAL_SERVER_ERROR);
		}
		
		if(!found) return sendResponse("Giocatore non iscritto!", NOT_FOUND);
		else return sendResponseCreated("Giocatore iscritto al gruppo!", OK, idLink);
	}
	// TODO API Gestito
	@ApiMethod(
			name = "gestito.inserisciLinkGestito",
			path = "gestito",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkGestito(InfoIscrittoGestisceBean gestisceBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestisceBean.getGiocatore()).now();
		if( giocatore == null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gestisceBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza TipoLinkIscritto
		boolean found = false;
		TipoLinkIscritto link = null;
		try {
			Iterator<Long> it = gruppo.getGiocatoriIscritti().iterator();
			
			while(it.hasNext()) {
				Long idLink = it.next();
				link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
				if(link.getGiocatore().equals(gestisceBean.getGiocatore())) {
					found = true;
					break;
				}
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il Gruppo "+ gruppo.getId() +
					" non rispetta la molteplicità minima!");
		}
		
		if(!found) return sendResponse("Il gestore del gruppo deve farne parte!", PRECONDITION_FAILED);
		
		// Controllo Unicità del gestore
		if(gruppo.quantiGestito() == 1) return sendResponse("Gruppo già gestito da un giocatore!", CONFLICT);
		
		// Creazione LinkGestisce
		gruppo.inserisciLinkGestito(link.getId());
		ofy().save().entity(gruppo).now();
		
		return sendResponseCreated("LinkGestisce inserito con successo!", CREATED, link.getId());
	}
	
	@ApiMethod(
			name = "gestito.eliminaLinkGestito",
			path = "gestito/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaLinkGestito(InfoIscrittoGestisceBean gestisceBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestisceBean.getGiocatore()).now();
		if( giocatore == null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gestisceBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza TipoLinkIscritto
		boolean found = false;
		TipoLinkIscritto link = null;
		try {
			Iterator<Long> it = gruppo.getGiocatoriIscritti().iterator();
			
			while(it.hasNext()) {
				Long idLink = it.next();
				link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
				if(link.getGiocatore().equals(gestisceBean.getGiocatore())) {
					found = true;
					break;
				}
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il Gruppo "+gruppo.getId()+" non rispetta la molteplicità minima!");
		}
		
		if(!found) return sendResponse("Giocatore non iscritto!", INTERNAL_SERVER_ERROR);
		
		// Rimozione LinkGestisce
		gruppo.eliminaLinkGestito(link.getId());
		ofy().save().entity(gruppo).now();
		
		return sendResponse("LinkGestisce rimosso con successo!", OK);
	}
/*	// TODO API Conosce
	@ApiMethod(
			name = "conosce.inserisciLinkConosce",
			path = "conosce",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkConosce(InfoConosceBean conosceBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(conosceBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(conosceBean.getCampo()).now();
		if( campo == null) return sendResponse("Campo non esistente!", PRECONDITION_FAILED);
		
		// Controllo campi duplicati
		/*
		Iterator<Long> it = gruppo.getCampiPreferiti().iterator();
		
		while(it.hasNext()) {
			Long idCampo = it.next();
			if(idCampo.equals(conosceBean.getCampo())) {
				DefaultBean response = new DefaultBean();
				response.setResult("Campo già presente!");
				log.log(Level.SEVERE, "faccio tearDown().");
				tearDown();
				return response;
			}
		}
		/*
		if(gruppo.getCampiPreferiti().contains(conosceBean.getCampo()))
			return sendResponse("Campo già presente!", CONFLICT);
		
		// Inserimento linkConosce e salvataggio/aggiornamento
		gruppo.inserisciCampo(conosceBean.getCampo());
		ofy().save().entity(gruppo).now();
		
		return sendResponse("LinkConosce inserito con successo!", CREATED);
	}
	
	@ApiMethod(
			name = "conosce.eliminaLinkConosce",
			path = "conosce/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaLinkConosce(InfoConosceBean conosceBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(conosceBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(conosceBean.getCampo()).now();
		if( campo == null) return sendResponse("Campo non esistente!", PRECONDITION_FAILED);
		
		// Rimozione LinkConosce
		gruppo.eliminaCampo(conosceBean.getCampo());
		ofy().save().entity(gruppo).now();
		
		return sendResponse("LinkConosce rimosso con successo!", OK);
	}
*/	
	// TODO API Organizza
	@ApiMethod(
			name = "organizza.inserisciLinkOrganizza",
			path = "organizza",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkOrganizza(InfoOrganizzaBean organizzaBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(organizzaBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(organizzaBean.getPartita()).now();
		if( partita == null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		// Controllo link duplicati
		// TODO Rivedere bene gli equals!!
		if(gruppo.getPartiteOrganizzate().contains(organizzaBean.getPartita())) 
			return sendResponse("Partita già organizzata!", CONFLICT);
		
		// inserimento LinkOrganizza e salvataggio/aggiornamento
		partita.inserisciLinkOrganizza(organizzaBean.getGruppo());
		ofy().save().entity(partita).now();
		gruppo.inserisciLinkOrganizza(organizzaBean.getPartita());
		ofy().save().entity(gruppo).now();
		
		return sendResponse("LinkOrganizza inserito con successo!", CREATED);
	}
	
	@ApiMethod(
			name = "organizza.eliminaLinkOrganizza",
			path = "organizza/delete",
			httpMethod = HttpMethod.POST
          )
	public 	DefaultBean eliminaLinkOrganizza(InfoOrganizzaBean organizzaBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(organizzaBean.getGruppo()).now();
		if( gruppo == null) return sendResponse("Gruppo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(organizzaBean.getPartita()).now();
		if( partita == null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		// rimozione LinkOrganizza e salvataggio/aggiornamento
		partita.eliminaLinkOrganizza(organizzaBean.getGruppo());
		ofy().save().entity(partita).now();
		gruppo.eliminaLinkOrganizza(organizzaBean.getPartita());
		ofy().save().entity(gruppo).now();
		
		return sendResponse("LinkOrganizza rimosso con successo!", OK);
	}
	
	// TODO API Presso
	@ApiMethod(
			name = "presso.inserisciLinkPresso",
			path = "presso",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkPresso(InfoPressoBean pressoBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(pressoBean.getCampo()).now();
		
		if( campo == null) return sendResponse("Campo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(pressoBean.getPartita()).now();
		
		if( partita == null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		// Controllo unicità Campo
		// TODO vedere bene per la possibilità di cambiare campo
		if(partita.quantiCampi() == 1) return sendResponse("Campo già impostato!", CONFLICT);
		
		// inserimento LinkPresso e salvataggio/aggiornamento
		partita.inserisciCampo(pressoBean.getCampo());
		partita.setQuota(campo.getPrezzo()/partita.getNPartecipanti());
		ofy().save().entity(partita).now();
		
		return sendResponse("LinkPresso inserito con successo!", CREATED);
	}
	
	@ApiMethod(
			name = "presso.eliminaLinkPresso",
			path = "presso/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaLinkPresso(InfoPressoBean pressoBean) {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(pressoBean.getCampo()).now();
		
		if( campo == null) return sendResponse("Campo non esistente!", PRECONDITION_FAILED);
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(pressoBean.getPartita()).now();
		
		if( partita == null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		// rimozione LinkPresso e salvataggio/aggiornamento
		partita.eliminaCampo(pressoBean.getCampo());
		partita.setQuota(Partita.SENTINELLA);
		ofy().save().entity(partita).now();
		
		return sendResponse("LinkPresso rimosso con successo!", OK);
	}
	
	//TODO API Sessione
	
	@ApiMethod(
			name = "sessione.inserisciSessione",
			path = "sessione",
			httpMethod = HttpMethod.POST
          )
	public PayloadBean inserisciSessione()
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		
		SessioneUtente s = new SessioneUtente();
		ofy().save().entity(s).now();
		PayloadBean res = new PayloadBean();
		res.setIdSessione(s.getId());
		return res;
	}
	
	@ApiMethod(
			name = "sessione.eliminaSessione",
			path = "sessione/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaSessione(@Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		
		ofy().delete().entity(s).now();
		return sendResponse("Sessione rimossa con successo.", OK);
	}
	
	@ApiMethod(
			name = "sessione.aggiornaStato",
			path = "sessione",
			httpMethod = HttpMethod.PUT
          )
	public DefaultBean aggiornaStatoSessione(PayloadBean payload)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(payload.getIdSessione()).now();
		if( s == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		s.aggiornaStato(payload.getNuovoStato());
		ofy().save().entity(s).now();
		return sendResponse("Stato sessione aggiornato con successo!", OK);
	}
	
	@ApiMethod(
			name = "sessione.sessioneIndietro",
			path = "sessione/back",
			httpMethod = HttpMethod.PUT
          )
	public PayloadBean sessioneIndietro(PayloadBean payload) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(payload.getIdSessione()).now();
		if( s == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return new PayloadBean();
		}
		
		s.tornaIndietro();
	/*	s.setStatoCorrente(s.getStatoPrecedente());
		switch(s.getStatoPrecedente()) {
			case PRINCIPALE:
				s.setStatoPrecedente(StatoSessione.EXIT);
				break;
			case PROFILO:
			/*	if(!s.getStatoPrePrecedente().equals(StatoSessione.STATO_SENTINELLA)) {
					s.setStatoPrecedente(s.getStatoPrePrecedente());
					s.setStatoPrePrecedente();
				} 
				else /*
					s.setStatoPrecedente(StatoSessione.PRINCIPALE);
			break;
			case GRUPPO:
				s.setStatoPrecedente(StatoSessione.PRINCIPALE);
				break;
			case PARTITA:
			/*	if(!s.getStatoPrePrecedente().equals(StatoSessione.STATO_SENTINELLA)) {
					s.setStatoPrecedente(s.getStatoPrePrecedente());
					s.setStatoPrePrecedente();
				}
				else /*
					s.setStatoPrecedente(StatoSessione.GRUPPO);
				break;
			case EXIT:
				s.setStatoCorrente(StatoSessione.PRINCIPALE);
				s.setStatoPrecedente(StatoSessione.EXIT);
			case RICERCA_GRUPPO:
				s.setStatoPrecedente(StatoSessione.PRINCIPALE);
				s.setStatoPrePrecedente();
				break;
			case ISCRITTI_GRUPPO:
				s.setStatoPrecedente(StatoSessione.GRUPPO);
				s.setStatoPrePrecedente();
				break;
			case STORICO:
				s.setStatoPrecedente(StatoSessione.GRUPPO);
				s.setStatoPrePrecedente();
				break;
		}
	*/
		ofy().save().entity(s).now();
		PayloadBean bean = new PayloadBean();
		bean.setNuovoStato(s.getStatoCorrente());
		return bean;
	}
	
	@ApiMethod(
			name = "sessione.statoAttuale",
			path = "sessione/statoattuale",
			httpMethod = HttpMethod.GET
          )
	public PayloadBean statoAttuale(@Named("idSessione")Long idSessione)
	{
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			PayloadBean p = new PayloadBean();
			p.setNuovoStato(null);
			return p;
		}
		
		tearDown();
		PayloadBean p = new PayloadBean();
		p.setIdSessione(idSessione);
		p.setNuovoStato(s.getStatoCorrente());
		return p;
	}
	
	@ApiMethod(
			name = "sessione.listaStati",
			path = "sessione/list",
			httpMethod = HttpMethod.POST
          )
	public ListaStatiBean listaStati(PayloadBean payload)
	{
		//TODO cambia parametro bean in named nullable
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		ListaStatiBean listaBean = new ListaStatiBean();
		
		Long idSessione = payload.getIdSessione();
		if( idSessione == null ) log.log(Level.SEVERE, "L'id della sessione nel bean è null!");
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null )
		{
			log.log(Level.SEVERE, "IdSessione non registrato!");
			listaBean.setHttpCode(NOT_FOUND);
			return listaBean;
		}
		
		switch( sessione.getStatoCorrente() )
		{
			case LOGIN_E_REGISTRAZIONE:
				//listaBean.addStatoSessione(StatoSessione.LOGIN);
				listaBean.addStatoSessione(StatoSessione.LOGIN_E_REGISTRAZIONE);
				listaBean.addStatoSessione(StatoSessione.REGISTRAZIONE);
				listaBean.addStatoSessione(StatoSessione.PRINCIPALE);
				break;
		/*	case LOGIN:
				listaBean.addStatoSessione(StatoSessione.LOGIN);
				listaBean.addStatoSessione(StatoSessione.PRINCIPALE);
				break;*/
			case REGISTRAZIONE:
				listaBean.addStatoSessione(StatoSessione.REGISTRAZIONE);
				listaBean.addStatoSessione(StatoSessione.PRINCIPALE);
				break;
			case PRINCIPALE:
				listaBean.addStatoSessione(StatoSessione.PROFILO);
				listaBean.addStatoSessione(StatoSessione.RICERCA_GRUPPO);
				listaBean.addStatoSessione(StatoSessione.GRUPPO);
				listaBean.addStatoSessione(StatoSessione.CREA_GRUPPO);
				break;
			case GRUPPO:
				Giocatore utente = ofy().load().type(Giocatore.class).id(sessione.getEmailUtente()).now();
				// Se l'utente è membro del gruppo da visualizzare...
				if( utente.getEIscritto().contains(payload.getIdGruppo()) )
				{
					listaBean.addStatoSessione(StatoSessione.ISCRITTI_GRUPPO);
					listaBean.addStatoSessione(StatoSessione.INVITO);
					listaBean.addStatoSessione(StatoSessione.STORICO);
					listaBean.addStatoSessione(StatoSessione.CREA_PARTITA);
					//listaBean.addStatoSessione(StatoSessione.PARTITE_PROPOSTE);
				}
				//Altrimenti, se il gruppo è aperto...
				else
				{
					GruppoAperto ga = ofy().load().type(GruppoAperto.class).id(payload.getIdGruppo()).now();
					if( ga != null )
						listaBean.addStatoSessione(StatoSessione.GRUPPO); //Iscrizione GruppoAPerto
				}
				break;
			case PROFILO:
				//Se l'utente sta visualizzando il proprio profilo...
				if( payload.getProfiloDaVisitare().equals(sessione.getEmailUtente()) )
				{
					listaBean.addStatoSessione(StatoSessione.MODIFICA_PROFILO);
					listaBean.addStatoSessione(StatoSessione.LOGIN_E_REGISTRAZIONE);	//Logout
				}
				break;
			case MODIFICA_PROFILO:
				listaBean.addStatoSessione(StatoSessione.PROFILO);
				listaBean.addStatoSessione(StatoSessione.MODIFICA_PROFILO);
				break;
			case RICERCA_GRUPPO:
				listaBean.addStatoSessione(StatoSessione.GRUPPO);
				listaBean.addStatoSessione(StatoSessione.RICERCA_GRUPPO);
				break;
			case INVITO:
				listaBean.addStatoSessione(StatoSessione.GRUPPO);
				listaBean.addStatoSessione(StatoSessione.PROFILO);
				break;
			case ISCRITTI_GRUPPO:
				listaBean.addStatoSessione(StatoSessione.GRUPPO);
				listaBean.addStatoSessione(StatoSessione.PROFILO);
				break;
			case STORICO:
				listaBean.addStatoSessione(StatoSessione.PARTITA);
				break;
			case CREA_PARTITA:
				listaBean.addStatoSessione(StatoSessione.PARTITA);
				listaBean.addStatoSessione(StatoSessione.RICERCA_CAMPO);
				listaBean.addStatoSessione(StatoSessione.CREA_PARTITA);
				break;
			case PARTITA:
				Partita partita = ofy().load().type(Partita.class).id(payload.getIdPartita()).now();
				if( partita != null )
				{
					Stato statoPartita = partita.getStatoCorrente();
					switch(statoPartita)
					{
						case PROPOSTA:
							listaBean.addStatoSessione(StatoSessione.RICERCA_CAMPO);
							listaBean.addStatoSessione(StatoSessione.DISPONIBILE_PER_PARTITA);
							break;
						case GIOCATA:
							listaBean.addStatoSessione(StatoSessione.CREA_VOTO);
							listaBean.addStatoSessione(StatoSessione.ELENCO_VOTI);
							break;
						default:
							break;
					}
				}
				else
					log.log(Level.SEVERE, "Nel payload c'è l'id di una partita non presente!");
				
				break;
			case CREA_VOTO:
				listaBean.addStatoSessione(StatoSessione.PARTITA);
				break;
			case RICERCA_CAMPO:
				listaBean.addStatoSessione(StatoSessione.CAMPO);
				listaBean.addStatoSessione(StatoSessione.CREA_CAMPO);
				listaBean.addStatoSessione(sessione.getStatoPrecedente());
				break;
			case DISPONIBILE_PER_PARTITA:
				listaBean.addStatoSessione(StatoSessione.PARTITA);
				break;
			case CAMPO:
				break;
			case CREA_CAMPO:
				listaBean.addStatoSessione(StatoSessione.CAMPO);
				listaBean.addStatoSessione(StatoSessione.CREA_CAMPO);
				break;
			case CREA_GRUPPO:
				listaBean.addStatoSessione(StatoSessione.CREA_GRUPPO);
				listaBean.addStatoSessione(StatoSessione.GRUPPO);
				break;
			case EXIT:
				listaBean.addStatoSessione(StatoSessione.PRINCIPALE);
				break;
			case ESCI_GRUPPO:
				listaBean.addStatoSessione(StatoSessione.PRINCIPALE);
				break;
			case ANNULLA_PARTITA:
				listaBean.addStatoSessione(StatoSessione.GRUPPO);
				listaBean.addStatoSessione(StatoSessione.ANNULLA_PARTITA); //In caso di errore.
				break;
			case MODIFICA_GRUPPO:
				listaBean.addStatoSessione(StatoSessione.MODIFICA_GRUPPO);
				listaBean.addStatoSessione(StatoSessione.GRUPPO);
				break;
			case MODIFICA_PARTITA:
				listaBean.addStatoSessione(StatoSessione.MODIFICA_PARTITA);
				listaBean.addStatoSessione(StatoSessione.PARTITA);
			case ELENCO_VOTI:
				break;
		}
		
		//tearDown();
		return listaBean;
	}
	
	//TODO API alto livello
	@ApiMethod(
			name = "api.login",
			path = "api/login",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean login(@Named("emailUtente")String emailUtente,
							 @Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(emailUtente).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponse("Giocatore non registrato!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.LOGIN_E_REGISTRAZIONE )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato LOGIN!");
			return sendResponse("Impossibile effettuare il login in questo punto!", BAD_REQUEST);
		}	
		s.setEmailUtente(emailUtente);
		s.aggiornaStato(StatoSessione.PRINCIPALE);
		ofy().save().entity(s).now();
		return sendResponse("Login effettuato con successo!", OK);
	}
	
	@ApiMethod(
			name = "api.registrazione",
			path = "api/regitstrazione",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean registrazione(InfoGiocatoreBean giocatoreBean,
									 @Named("idSessione")Long idSessione)
	{
		//Inserimento nuovo Giocatore
		DefaultBean result_inserisci = inserisciGiocatore(giocatoreBean);
		if( !result_inserisci.getHttpCode().equals(CREATED) )
		{
				log.log(Level.SEVERE, "registrazione: errore durante inserimento giocatore!");
				return result_inserisci;
		}
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "registrazione: Sessione "+idSessione+" non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato Sessione
		if( s.getStatoCorrente() != StatoSessione.REGISTRAZIONE)
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+"non è nello stato REGISTRAZIONE!");
			return sendResponse("Impossibile effettuare la registrazione in questo punto!", BAD_REQUEST);
		}	
		s.setEmailUtente(giocatoreBean.getEmail());
		s.aggiornaStato(StatoSessione.PRINCIPALE);
		ofy().save().entity(s).now();
		return sendResponse("Registrazione effettuata con successo!", OK);
	}
	
	@ApiMethod(
			name = "api.logout",
			path = "api/logout",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean logout(@Named("emailUtente")String emailUtente,
							  @Named("idSessione")Long idSessione)
	{
		setUp();
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(emailUtente).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponse("Giocatore non registrato!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.PROFILO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato PROFILO!");
			return sendResponse("Impossibile effettuare il logout in questo punto!", BAD_REQUEST);
		}
		s.setEmailUtente(null);
		s.aggiornaStato(StatoSessione.LOGIN_E_REGISTRAZIONE);
		ofy().save().entity(s).now();
		return sendResponse("Logout effettuato con successo!", OK);
	}
	
	@ApiMethod(
			name = "api.creaPartita",
			path = "api/creapartita",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean creaPartita(InfoPartitaBean partitaBean,
								   @Named("idSessione")Long idSessione,
								   @Named("idGruppo")Long idGruppo)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(idGruppo).now();
		if( gruppo == null )
		{
			return sendResponse("Gruppo non esistente", NOT_FOUND);
		}
		//Controllo esistenza sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null )
		{
			log.log(Level.SEVERE, "creaPartita: la sessione "+idSessione+" non è presente nel Datastore!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.CREA_PARTITA )
		{
			log.log(Level.SEVERE, "creaPartita: la sessione "+idSessione+" non è nello stato CREA_PARTITA!");
			return sendResponse("Impossibile creare una partita in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "creaPartita: la sessione "+idSessione+" non ha memorizzato"
								+ " la mail dell'utente a cui è associata!");
			return sendResponse("Errore durante la gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		//Inserisco la partita
		DefaultBean partialResult = inserisciPartita(partitaBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "creaPartita: errore durante l'inserimento di una partita"
								+ " dalla sessione "+idSessione+"!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		Long idPartita = partialResult.getIdCreated();
		//Inserisco link disponibile
		InfoGestionePartiteBean gestioneBean = new InfoGestionePartiteBean();
		gestioneBean.setEmailGiocatore(sessione.getEmailUtente());
		gestioneBean.setIdPartita(idPartita);
		partialResult = inserisciLinkDisponibile(gestioneBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "creaPartita: errore durante l'inserimento del linkDisponibile"
								+ " per la partita "+idPartita+"!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		//Inserisco link propone
		partialResult = inserisciLinkPropone(gestioneBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "creaPartita: errore durante l'inserimento del linkPropone"
								+ " per la partita "+idPartita+"!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		//Inserisco link organizza
		InfoOrganizzaBean organizzaBean = new InfoOrganizzaBean();
		organizzaBean.setGruppo(idGruppo);
		organizzaBean.setPartita(idPartita);
		partialResult = inserisciLinkOrganizza(organizzaBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "creaPartita: errore durante l'inserimento del linkOrganizza"
								 +" per la partita "+idPartita+"!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		//Aggiorno stato sessione
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.PARTITA);
		partialResult = aggiornaStatoSessione(payload);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "creaPartita: errore durante il cambio di stato"
								 +" della sessione "+idSessione+"+!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		return sendResponseCreated("Partita creata con successo!", CREATED, idPartita);
	}
	
	@ApiMethod(
			name = "api.annullaPartita",
			path = "api/annullapartita",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean annullaPartita(InfoGestionePartiteBean annullaBean,
			   						  @Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( s.getStatoCorrente() != StatoSessione.ANNULLA_PARTITA )
		{
			log.log(Level.SEVERE, "annullaPartita: la sessione "+idSessione
								 +" non è nello stato ANNULLA_PARTITA!");
			return sendResponse("Impossibile annullare una partita in questo punto!", BAD_REQUEST);
		}
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(annullaBean.getIdPartita()).now();
		if( partita == null )
			return sendResponse("Partita non esistente!", NOT_FOUND);
		
		//Controllo giocatore come proponitore
		try {
			if(!partita.getPropone().equals(annullaBean.getEmailGiocatore()))
				return sendResponse("Solo chi propone la partita può confermarla!", UNAUTHORIZED);
			
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "annullaPartita: la partita "+annullaBean.getIdPartita()+" non ha un proponitore!");
			//TODO che famo?
			return sendResponse("Non si ha traccia del giocatore che ha proposto la partita!", INTERNAL_SERVER_ERROR);
		}
		
		//Termino la sessione sul Datastore per chiamare cancellaPartita.
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		
		DefaultBean partialResult = cancellaPartita(annullaBean);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "annullaPartita: errore durante la cancellazione"
								+ " della partita+"+annullaBean.getIdPartita()+"!");
			return partialResult;
		}
		
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Aggiorno stato sessione
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.GRUPPO);
		partialResult = aggiornaStatoSessione(payload);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "annullaPartita: errore durante il cambio di stato"
								 +" della sessione "+idSessione+"+!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		
		//Invio conferma rimozione avvenuta
		return sendResponse("Partita annullata con successo.", OK);
	}
	
	@ApiMethod(
			name = "api.annullaDisponibilita",
			path = "api/annulladisponibilita",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean annullaDisponibilita(InfoGestionePartiteBean gestioneBean,
											@Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null)
		{
			log.log(Level.SEVERE, "annullaDisponibilita: sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( s.getStatoCorrente() != StatoSessione.DISPONIBILE_PER_PARTITA )
		{
			log.log(Level.SEVERE, "annullaDisponibilita: la sessione "+idSessione+
								  " non è nello stato DISPONIBILE_PER_PARTITA!");
			return sendResponse("Impossibile annullare una partita in questo punto!", BAD_REQUEST);
		}
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		
		DefaultBean partialResult = annullaDisponibilita(gestioneBean);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "Errore durante l'annullamento della disponibilità");
			return partialResult;
		}
		
		//Aggiorno stato sessione
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.PARTITA);
		partialResult = aggiornaStatoSessione(payload);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "errore durante l'uscita del giocatore dalla partita "
									+gestioneBean.getIdPartita()+"!");
		}
		return partialResult;
	}
	
	@ApiMethod(
			name = "api.creaCampo",
			path = "api/creacampo",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean creaCampo(@Named("idSessione")Long idSessione,
								 InfoCampoBean campoBean)
	{
		setUp();
		//Controllo esistenza sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null )
		{
			log.log(Level.SEVERE, "creaGruppo: la sessione "+idSessione+" non è presente nel Datastore!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.CREA_CAMPO )
		{
			log.log(Level.SEVERE, "creaGruppo: la sessione "+idSessione+" non è nello stato CREA_CAMPO!");
			return sendResponse("Impossibile creare un gruppo in questo punto!", BAD_REQUEST);
		}
		
		DefaultBean partialResult = inserisciCampo(campoBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			tearDown();
			return partialResult;
		}
		
		//Aggiorno stato sessione
		sessione.aggiornaStato(StatoSessione.CAMPO);
		ofy().save().entity(sessione).now();
		tearDown();
		
		return partialResult;
	}

	@ApiMethod(
			name = "api.creaGruppo",
			path = "api/creagruppo",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean creaGruppo(InfoGruppoBean gruppoBean,
								   @Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null )
		{
			log.log(Level.SEVERE, "creaGruppo: la sessione "+idSessione+" non è presente nel Datastore!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.CREA_GRUPPO )
		{
			log.log(Level.SEVERE, "creaGruppo: la sessione "+idSessione+" non è nello stato CREA_GRUPPO!");
			return sendResponse("Impossibile creare un gruppo in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "creaGruppo: la sessione "+idSessione+
								  " non ha memorizzato la mail dell'utente a cui è associata!");
			return sendResponse("Errore durante la gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		//Inserisco gruppo
		DefaultBean partialResult = inserisciGruppo(gruppoBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "creaGruppo: errore durante la creazione della partita!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		Long idGruppo = partialResult.getIdCreated();
		//Inserisco link iscritto
		InfoIscrittoGestisceBean iscrittoBean = new InfoIscrittoGestisceBean();
		iscrittoBean.setGiocatore(sessione.getEmailUtente());
		iscrittoBean.setGruppo(idGruppo);
		partialResult = inserisciLinkIscritto(iscrittoBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "creaGruppo: errore durante l'inserimento "
								 +"del primo link iscritto nel gruppo "+idGruppo+"!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		//Inserisco link gestisce
		partialResult = inserisciLinkGestito(iscrittoBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "creaGruppo: errore durante l'inserimento "
								 +"del link gestisce nel gruppo "+idGruppo+"!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		
		//Long idGruppoCreato = partialResult.getIdCreated();
		
		//Aggiorno stato sessione
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.GRUPPO);
		partialResult = aggiornaStatoSessione(payload);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "creaGruppo: errore durante il cambio di stato"
								 +" della sessione "+idSessione+"+!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		return sendResponseCreated("Gruppo creato con successo!", CREATED, idGruppo);
	}
	
	
	@ApiMethod(
			name = "api.esciGruppo",
			path = "api/escigruppo",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean esciGruppo(InfoIscrittoGestisceBean iscrittoBean,
									@Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null )
		{
			log.log(Level.SEVERE, "esciGruppo: la sessione "+idSessione+
									" non è presente nel Datastore!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.ESCI_GRUPPO )
		{
			log.log(Level.SEVERE, "esciGruppo: la sessione "+idSessione+
									" non è nello stato ESCI_GRUPPO!");
			return sendResponse("Impossibile uscire da un gruppo in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "esciGruppo: la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			return sendResponse("Errore durante la gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		//Controllo coincidenza email sessione - email in iscrittoBean
		if( !sessione.getEmailUtente().equals(iscrittoBean.getGiocatore()) )
		{
			log.log(Level.SEVERE, "esciGruppo: la sessione "+idSessione+" non ha"
					 			 +"memorizzato la mail diversa da quella dell'iscrittoBean!");
			return sendResponse("Errore durante la gestione della sessione!", BAD_REQUEST);
		}
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(sessione.getEmailUtente()).now();
		if( giocatore == null )
		{
			log.log(Level.SEVERE, "esciGruppo: il giocatore "+sessione.getEmailUtente()+" non esiste!");
			return sendResponse("Giocatore non esistente!", NOT_FOUND);
		}
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(iscrittoBean.getGruppo()).now();
		if( gruppo == null )
		{
			log.log(Level.SEVERE, "esciGruppo: gruppo "+iscrittoBean.getGruppo()
									+" non presente nel Datastore!");
			return sendResponse("Gruppo non esistente!", NOT_FOUND);
		}
		try {
			//Controllo esistenza linkIscritto
			DefaultBean partialResult = estIscritto(iscrittoBean);
			if( partialResult.getHttpCode().equals(NOT_FOUND) )
			{
				log.log(Level.SEVERE, "esciGruppo: il giocatore "+iscrittoBean.getGiocatore()+
								" non risulta iscritto al gruppo "+iscrittoBean.getGruppo()+"!");
				return sendResponse("L'utente non risulta iscritto al gruppo!", NOT_FOUND);
			}
			
			TipoLinkIscritto linkIscritto = ofy().load().type(TipoLinkIscritto.class)
											.id(partialResult.getIdCreated()).now();

			//Controllo se il giocatore giocherà una partita confermata.
			//Se così fosse, devo impedire al giocatore di uscire dal gruppo.
			List<Partita> listaConfermate = ofy().load().type(Partita.class)
								  .filter("gruppo", iscrittoBean.getGruppo())
								  .filter("statoCorrente", Partita.Stato.CONFERMATA).list();
			if( listaConfermate != null && listaConfermate.size()>0 )
			{
				Iterator<Partita> itConfermate = listaConfermate.iterator();
				while( itConfermate.hasNext() )
				{
					Partita confermata = itConfermate.next();
					Set<String> elencoGiocanti = confermata.getLinkGioca();
					if( elencoGiocanti.contains(iscrittoBean.getGiocatore()) )
					{
						return sendResponse("Il giocatore figura tra i giocanti"+
											"per una partita confermata!", UNAUTHORIZED);
					}
				}
			}
			
			//Controllo esistenza linkGestisce(se l'utente è l'amministratore, 
			//bisogna nominarne un altro)
			try {
				if( gruppo.getLinkGestito().equals(linkIscritto.getId()) )
				{
					//Elimino il vecchio linkGestito.
					partialResult = eliminaLinkGestito(iscrittoBean);
					if( !partialResult.getHttpCode().equals(OK) )
					{
						log.log(Level.SEVERE, "esciGruppo: errore durante la rimozione del linkGestito"
											+ " tra "+iscrittoBean.getGiocatore()+
											  " e il gruppo "+iscrittoBean.getGruppo()+"!");
						log.log(Level.SEVERE, "faccio tearDown().");
						tearDown();
						return partialResult;
					}
					
				}
			} catch (EccezioneSubset e) {
				log.log(Level.SEVERE, "esciGruppo: il gruppo "+iscrittoBean.getGruppo()+
									  " non ha un amministratore!");
			}
			
			//Se il giocatore non partecipa a partire confermate, posso procedere con l'eliminazione.
			partialResult = eliminaLinkIscritto(iscrittoBean);
			if( !partialResult.getHttpCode().equals(OK) )
			{
				log.log(Level.SEVERE, "esciGruppo: errore durante la rimozione del linkIscritto tra "
									 +iscrittoBean.getGiocatore()+" e il gruppo "
									 +iscrittoBean.getGruppo()+"!");
				log.log(Level.SEVERE, "faccio tearDown().");
				tearDown();
				return partialResult;
			}
			
			//Controllo ultimo Iscritto: in quel caso bisogna eliminare il gruppo.
			//Devo prendere nuovamente il gruppo dal Datastore, perchè è stato aggiornato.
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			log.log(Level.SEVERE, "faccio setUp().");
			setUp();
			gruppo = ofy().load().type(Gruppo.class).id(iscrittoBean.getGruppo()).now();
			log.log(Level.SEVERE, "quantiIscritti = "+gruppo.quantiIscritti());
			
			if( gruppo.quantiIscritti() == 0 )
			{
				log.log(Level.SEVERE, "Il giocatore era l'ultimo iscritto, elimino il gruppo.");
				//E' necessario prima annullare tutte le partite proposte, 
				//confermate e giocate dal gruppo
				Set<Long> listaPartite = gruppo.getPartiteOrganizzate();
				Iterator<Long> itPartite = listaPartite.iterator();
				while( itPartite.hasNext() )
				{
					Long idPartita = itPartite.next();
					Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
					if( partita.getStatoCorrente() == Partita.Stato.PROPOSTA  )
					{
						InfoGestionePartiteBean annullaBean = new InfoGestionePartiteBean();
						annullaBean.setEmailGiocatore(iscrittoBean.getGiocatore());
						annullaBean.setIdPartita(idPartita);
						partialResult = cancellaPartita(annullaBean);
					}
					else partialResult = rimuoviPartitaGiocataConfermata(idPartita);
						
					if( !partialResult.getHttpCode().equals(OK) )
					{
						log.log(Level.SEVERE, "esciGruppo: errore durante l'annullamento della partita "
											  +idPartita+"!");
						continue;
						//TODO o return partialResult; ?
					}
				}
				//Eliminazione gruppo
				InfoGruppoBean gruppoBean = new InfoGruppoBean();
				gruppoBean.setId(iscrittoBean.getGruppo());
				partialResult = eliminaGruppo(gruppoBean);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "esciGruppo: errore durante l'eliminazione del gruppo "
										 +iscrittoBean.getGruppo()+"!");
					log.log(Level.SEVERE, "faccio tearDown().");
					tearDown();
					return partialResult;
				}
				
				//Se tutto è andato a buon fine, aggiorno lo stato della sessione,
				//E riporto l'utente alla schermata principale.
				PayloadBean payload = new PayloadBean();
				payload.setIdSessione(idSessione);
				payload.setNuovoStato(StatoSessione.PRINCIPALE);
				partialResult = aggiornaStatoSessione(payload);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "esciGruppo: errore durante l'aggiornamento dello stato"
										 +"della sessione "+idSessione+"!");
					log.log(Level.SEVERE, "faccio tearDown().");
					tearDown();
					return partialResult;
				}
				return sendResponse("Uscita dal gruppo effettuata con successo!", OK);
			}
			
			log.log(Level.SEVERE, "Il giocatore non era l'ultimo iscritto.");
			
			//Se invece nel gruppo ci sono ancora altri giocatori, e se l'utente era l'amministratore,
			//bisogna nominare un altro admin.
			log.log(Level.SEVERE, "gruppo.quantiGestito()= "+gruppo.quantiGestito()+".");
			if( gruppo.quantiGestito() == 0 )
			{	
				//Il nuovo amministratore sarà l'utente iscritto dal gruppo da più tempo.
				TipoLinkIscritto nuovoGestore = ofy().load().type(TipoLinkIscritto.class)
												.id(gruppo.getGiocatoriIscritti().get(0)).now();
				InfoIscrittoGestisceBean newAdminBean = new InfoIscrittoGestisceBean();
				newAdminBean.setGruppo(iscrittoBean.getGruppo());
				newAdminBean.setGiocatore(nuovoGestore.getGiocatore());
				partialResult = inserisciLinkGestito(newAdminBean);
				if( !partialResult.getHttpCode().equals(CREATED) )
				{
					log.log(Level.SEVERE, "esciGruppo: errore durante l'inserimento del linkGestito tra "
							 +iscrittoBean.getGiocatore()+" e il gruppo "+iscrittoBean.getGruppo()+"!");
					log.log(Level.SEVERE, "faccio tearDown().");
					tearDown();
					return partialResult;
				}
			}
			
			//E' inoltre necessario rimuovere i linkDisponibile dalle partite proposte,
			//ed eventualmente, se il giocatore ne era anche il proponitore, cambiarne il proponitore.
			//Per le partite giocate, lasciamo tutto.
			List<Partita> listaProposte = ofy().load().type(Partita.class)
										  .filter("gruppo", iscrittoBean.getGruppo())
										  .filter("statoCorrente", Partita.Stato.PROPOSTA).list();
			Iterator<Partita> itProposte = listaProposte.iterator();
			//log.log(Level.SEVERE, "faccio tearDown().");
			//tearDown();
			while( itProposte.hasNext() )
			{
				Partita partita = itProposte.next();
				Long idProposta = partita.getId();
				InfoGestionePartiteBean proponeBean = new InfoGestionePartiteBean();
				proponeBean.setEmailGiocatore(iscrittoBean.getGiocatore());
				proponeBean.setIdPartita(idProposta);
				partialResult = annullaDisponibilita(proponeBean);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "esciGruppo: errore durante l'uscita del giocatore "
										 +"dalla partita "+idProposta+"!");
					continue;
				}
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "esciGruppo: il gruppo "+iscrittoBean.getGruppo()+
									" non ha giocatori iscritti!");
			return sendResponse("Errore durante la gestione delle iscrizioni!", INTERNAL_SERVER_ERROR);
		}
		
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.PRINCIPALE);
		aggiornaStatoSessione(payload);
		return sendResponse("Uscita dal gruppo effettuata con successo!", OK);
	}
	
	@ApiMethod(
			name = "api.getGiocatore",
			path = "api/getGiocatore",
			httpMethod = HttpMethod.GET
          )
	public GiocatoreBean getGiocatore(@Named("emailUtente")String emailUtente,
									  @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(emailUtente).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponseGiocatore(null, "Giocatore non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseGiocatore(null, "Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.PROFILO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato PROFILO!");
			return sendResponseGiocatore(null, "Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		return sendResponseGiocatore(g, "Operazione completata con successo", OK);
	}
	
	@ApiMethod(
			name = "api.getGruppo",
			path = "api/getGruppo",
			httpMethod = HttpMethod.GET
          )
	public GruppoBean getGruppo(@Named("idGruppo")Long idGruppo,
								@Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Gruppo
		Gruppo g = ofy().load().type(Gruppo.class).id(idGruppo).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Gruppo non esistente!");
			return sendResponseGruppo(null, null, "Gruppo non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseGruppo(null, null, "Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.GRUPPO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato GRUPPO!");
			return sendResponseGruppo(null, null, "Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		try{
			//Load mail Admin
			TipoLinkIscritto link = ofy().load().type(TipoLinkIscritto.class).id(g.getLinkGestito()).now();
			if( link == null ){
				log.log(Level.SEVERE, "Il gruppo "+idGruppo+" ha memorizzato un gestore che non esiste!");
				return sendResponseGruppo(null, null, "Admin non trovato!", NOT_FOUND);
			}
			Giocatore admin = ofy().load().type(Giocatore.class).id(link.getGiocatore()).now();
			if( admin == null ){
				log.log(Level.SEVERE, "Il giocatore "+link.getGiocatore()+" non esiste!");
				return sendResponseGruppo(null, null, "Admin non esistente!", NOT_FOUND);
			}
			
			//Invio gruppoBean
			return sendResponseGruppo(g, admin.getEmail(), "Operazione completata con successo", OK);
		
		}catch(EccezioneSubset e){
			log.log(Level.SEVERE, "Il gruppo "+idGruppo+" ha memorizzato un admin che non e' iscritto!");
			return sendResponseGruppo(null, null, "Admin non iscritto!", NOT_FOUND);
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il gruppo "+idGruppo+" non ha memorizzato un admin!");
			return sendResponseGruppo(null, null, "Gruppo senza admin!", NOT_FOUND);
		}
	}
	
	@ApiMethod(
			name = "api.getPartita",
			path = "api/getPartita",
			httpMethod = HttpMethod.GET
          )
	public PartitaBean getPartita(@Named("idPartita")Long idPartita,
								  @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Partita
		Partita p = ofy().load().type(Partita.class).id(idPartita).now();
		if( p == null)
		{
			log.log(Level.SEVERE, "Partita non esistente!");
			return sendResponsePartita(null, "Partita non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponsePartita(null, "Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.PARTITA )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato PARTITA!");
			return sendResponsePartita(null, "Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		return sendResponsePartita(p, "Operazione completata con successo", OK);
	}
	
	@ApiMethod(
			name = "api.setGiocatore",
			path = "api/setGiocatore",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean setGiocatore(InfoGiocatoreBean giocatoreBean,
									@Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(giocatoreBean.getEmail()).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponse("Giocatore non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.MODIFICA_PROFILO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato MODIFICA_PROFILO!");
			return sendResponse("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		//Controllo Giocatore Sessione
		if( !giocatoreBean.getEmail().equals(s.getEmailUtente()) ) {
			log.log(Level.SEVERE, "Il giocatore da modificare non corrisponde con quello della sessione!");
			return sendResponse("Il giocatore da modificare non corrisponde con quello della sessione!", PRECONDITION_FAILED);
		}
		tearDown();
		DefaultBean response = modificaGiocatore(giocatoreBean);
		if(!response.getHttpCode().equals(OK)) {
			log.log(Level.SEVERE, "setGiocatore: Errore durante la modifica"
					+ "del giocatore "+ g.getEmail() +" !");
			tearDown();
			return response;
			
		}
		setUp();
		s.tornaIndietro();
		ofy().save().entity(s).now();
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "api.setGruppo",
			path = "api/setGruppo",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean setGruppo(InfoGruppoBean gruppoBean,
								 @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Gruppo
		Gruppo g = ofy().load().type(Gruppo.class).id(gruppoBean.getId()).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Gruppo non esistente!");
			return sendResponse("Gruppo non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.MODIFICA_GRUPPO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato MODIFICA_GRUPPO!");
			return sendResponse("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		//Controllo Gestore Gruppo
		Long idLink = null;
		try {
			idLink = g.getLinkGestito();
			TipoLinkIscritto l = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
			if( !l.getGiocatore().equals(s.getEmailUtente()) ) {
				log.log(Level.SEVERE, "Il giocatore associato alla sessione non è il gestore del gruppo!");
				return sendResponse("Il giocatore associato alla sessione non è il gestore del gruppo!", PRECONDITION_FAILED);
			}
		}
		catch(Exception e) {
			log.log(Level.SEVERE, "Il TipolinkIscritto "+ idLink + " ha lanciato la"
					+ " seguente eccezione: " + e.getMessage());
			return sendResponse("Gestore del gruppo mancante o non iscritto al gruppo!", INTERNAL_SERVER_ERROR);
			
		}
		tearDown();
		DefaultBean response = modificaGruppo(gruppoBean);
		if(!response.getHttpCode().equals(OK)) {
			log.log(Level.SEVERE, "setGruppo: Errore durante la modifica"
					+ "del gruppo "+ g.getId() +" !");
			tearDown();
			return response;
			
		}
		setUp();
		s.tornaIndietro();
		ofy().save().entity(s).now();
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "api.setPartita",
			path = "api/setPartita",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean setPartita(InfoPartitaBean partitaBean,
								 @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Giocatore
		Partita p = ofy().load().type(Partita.class).id(partitaBean.getId()).now();
		if( p == null)
		{
			log.log(Level.SEVERE, "Partita non esistente!");
			return sendResponse("Partita non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.MODIFICA_PARTITA )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato MODIFICA_PARTITA!");
			return sendResponse("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		//Controllo Proponente Partita
		try {
			if( !p.getPropone().equals(s.getEmailUtente()) ) {
				log.log(Level.SEVERE, "Il giocatore associato alla sessione non ha proposto la partita!");
				return sendResponse("Il giocatore associato alla sessione non ha proposto la partita!", PRECONDITION_FAILED);
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+ p.getId() +" non ha un giocatore che l'ha proposta!");
			return sendResponse("La partita non ha un giocatore che l'ha proposta!", INTERNAL_SERVER_ERROR);
		}
		// Controllo stato partita
		if( !p.getStatoCorrente().equals(Partita.Stato.PROPOSTA) ) {
			log.log(Level.SEVERE, "La partita non può essere modificata in questo stato!");
			return sendResponse("La partita non può essere modificata in questo stato!", PRECONDITION_FAILED);
		}
		tearDown();
		DefaultBean response = modificaPartita(partitaBean);
		if(!response.getHttpCode().equals(OK)) {
			log.log(Level.SEVERE, "setPartita: Errore durante la modifica"
					+ "della partita "+ p.getId() +" !");
			tearDown();
			return response;
			
		}
		setUp();
		s.tornaIndietro();
		ofy().save().entity(s).now();
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "api.listaGruppiIscritto",
			path = "api/listaGruppiIscritto",
			httpMethod = HttpMethod.GET
          )
	public ListaGruppiBean listaGruppiIscritto(//@Named("emailUtente")String emailUtente,
			  								   @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseListaGruppi("Sessione non esistente!", NOT_FOUND, new LinkedList<Gruppo>());
		}
		//Controllo stato sessione
		//TODO E' giusto metterlo?!
		if( s.getStatoCorrente() != StatoSessione.PRINCIPALE )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato PRINCIPALE!");
			return sendResponseListaGruppi("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST, new LinkedList<Gruppo>());
		}
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(/*emailUtente*/s.getEmailUtente()).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponseListaGruppi("Giocatore non registrato!", NOT_FOUND, new LinkedList<Gruppo>());
		}
		
		LinkedList<Gruppo> lg = new LinkedList<Gruppo>();
		Iterator<Long> it = g.getEIscritto().iterator();
		while(it.hasNext()) {
			Long idLink = it.next();
			TipoLinkIscritto link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
			if(link == null) {
				log.log(Level.SEVERE, "Giocatore "+ g.getEmail() + "ha un TipoLinkIscritto con id"
						+ idLink + "non esistente!");
				continue;
			}
			Gruppo gr = ofy().load().type(Gruppo.class).id(link.getGruppo()).now();
			if(gr == null) {
				log.log(Level.SEVERE, "TipolinkIscritto "+ link.getId() + "ha un gruppo con id"
						+ link.getGruppo() + "non esistente!");
				continue;
			}
			lg.add(gr);
		}
		return sendResponseListaGruppi("Operazione completata con successo!", OK, lg);
	}
	
	@ApiMethod(
			name = "api.listaIscrittiGruppo",
			path = "api/listaIscrittiGruppo",
			httpMethod = HttpMethod.GET
          )
	public ListaGiocatoriBean listaIscrittiGruppo(@Named("idGruppo")Long idGruppo,
			   									   @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Gruppo
		Gruppo g = ofy().load().type(Gruppo.class).id(idGruppo).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Gruppo non esistente!");
			return sendResponseListaGiocatori("Gruppo non esistente!", NOT_FOUND, new LinkedList<Giocatore>());
		}
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseListaGiocatori("Sessione non esistente!", NOT_FOUND, new LinkedList<Giocatore>());
		}
		//Controllo stato sessione
		//TODO E' giusto metterlo?!
		if( s.getStatoCorrente() != StatoSessione.ISCRITTI_GRUPPO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato ISCRITTI_GRUPPO!");
			return sendResponseListaGiocatori("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST, new LinkedList<Giocatore>());
		}
		LinkedList<Giocatore> lg = new LinkedList<Giocatore>();
		try {
			Iterator<Long> it = g.getGiocatoriIscritti().iterator();
			while(it.hasNext()) {
				Long idLink = it.next();
				TipoLinkIscritto link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
				if(link == null) {
					log.log(Level.SEVERE, "Gruppo "+ g.getId() + "ha un TipoLinkIscritto con id"
							+ idLink + "non esistente!");
					continue;
				}
				Giocatore gioc = ofy().load().type(Giocatore.class).id(link.getGiocatore()).now();
				if(gioc == null) {
					log.log(Level.SEVERE, "TipolinkIscritto "+ link.getId() + "ha un giocatore con id"
							+ link.getGiocatore() + "non esistente!");
					continue;
				}
				lg.add(gioc);
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il gruppo "+ g.getId() +" non ha alcun giocatore iscritto!");
			return sendResponseListaGiocatori("Il gruppo non ha alcun giocatore iscritto!", INTERNAL_SERVER_ERROR, lg);
		}
		return sendResponseListaGiocatori("Operazione completata con successo!", OK, lg);
	}
	
	@ApiMethod(
			name = "api.creaVoto",
			path = "api/creavoto",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean creaVoto(@Named("idSessione")Long idSessione,
								InfoVotoUomoPartitaBean votoBean)
	{
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.CREA_VOTO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato CREA_VOTO!");
			return sendResponse("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(votoBean.getLinkVotoPerPartita()).now();
		if( partita == null )
		{
			return sendResponse("Partita non esistente!", NOT_FOUND);
		}
		//Controllo stato partita
		if( partita.getStatoCorrente() != Stato.GIOCATA )
		{
			return sendResponse("Impossibile votare l'uomo partita per una partita non terminata!", BAD_REQUEST);
		}	
		//Controllo esistenza votante
		Giocatore votante = ofy().load().type(Giocatore.class).id(votoBean.getVotante()).now();
		if( votante == null )
		{
			return sendResponse("Votante non trovato!", NOT_FOUND);
		}
		//Controllo esistenza votato
		Giocatore votato = ofy().load().type(Giocatore.class).id(votoBean.getVotato()).now();
		if( votato == null )
		{
			return sendResponse("Votato non trovato!", NOT_FOUND);
		}
		
		DefaultBean partialResult = inserisciVotoUomoPartita(votoBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "Errore durante l'inserimento del voto!");
			tearDown();
			return partialResult;
		}
		
		//Aggiorno lo stato della sessione e termino.
		s.aggiornaStato(StatoSessione.PARTITA);
		ofy().save().entity(s).now();
		
		tearDown();
		return partialResult;
	}

	
	@ApiMethod(
			name = "api.elencoVotiUomoPartita",
			path = "api/listavoti",
			httpMethod = HttpMethod.GET
          )
	public ListaVotiBean elencoVotiUomoPartita(@Named("idPartita")Long idPartita,
											   @Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		ListaVotiBean listaVotiBean = new ListaVotiBean();
		//Controllo esistenza sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null )
		{
			log.log(Level.SEVERE, "elencoVotiUomoPartita: la sessione "+idSessione+
									" non è presente nel Datastore!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaVotiBean.setHttpCode(NOT_FOUND);
			return listaVotiBean;
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.ELENCO_VOTI )
		{
			log.log(Level.SEVERE, "elencoVotiUomoPartita: la sessione "+idSessione+
									" non è nello stato ELENCO_VOTI!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaVotiBean.setHttpCode(BAD_REQUEST);
			return listaVotiBean;
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "elencoVotiUomoPartita: la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaVotiBean.setHttpCode(INTERNAL_SERVER_ERROR);
			return listaVotiBean;
		}
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if( partita == null )
		{
			log.log(Level.SEVERE, "elencoVotiUomoPartita: la partita "+idPartita+
									" non è presente nel Datastore!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaVotiBean.setHttpCode(NOT_FOUND);
			return listaVotiBean;
		}
		//Controllo stato partita == GIOCATA
		if( partita.getStatoCorrente() != Stato.GIOCATA )
		{
			log.log(Level.SEVERE, "elencoVotiUomoPartita: la partita "+idPartita+
								  "non è ancora terminata, eppure c'è qualcuno che "+
								  "che sta tentando di visualizzare i votiUomoPartita!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaVotiBean.setHttpCode(NOT_FOUND);
			return listaVotiBean;
		}
		
		//Creazione lista
	/*	ALTERNATIVA
	 * 	Set<Long> elencoIdVoti = partita.getLinkVotoPerPartita();
		Iterator<Long> it = elencoIdVoti.iterator();
		while( it.hasNext() )
		{
			Long idVoto = it.next();
			VotoUomoPartita voto = ofy().load().type(VotoUomoPartita.class).id(idVoto).now();
			if( voto != null ) listaBean.addVoto(voto);
			else log.log(Level.SEVERE, "La partita "+idPartita+" ha un riferimento ad un voto "
									  +idVoto+" che non esiste nel Datastore!");
		}
		//Restituzione lista
		listaVotiBean.setHttpCode(OK);
		return listaVotiBean;
	*/
		List<VotoUomoPartita> listaVoti = ofy().load().type(VotoUomoPartita.class)
										  .filter("linkVotoPerPartita", idPartita).list();
		log.log(Level.SEVERE, "dimensione listavoti = "+listaVoti.size());
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		if( listaVoti == null)
			return listaVotiBean;
		Iterator<VotoUomoPartita> it = listaVoti.iterator();
		while(it.hasNext())
		{
			listaVotiBean.addVoto(it.next());
		}
		//Restituzione lista
		listaVotiBean.setHttpCode(OK);
		return listaVotiBean;
	}
	
	
	@ApiMethod(
			name = "api.classificaVoti",
			path = "api/classificavoti",
			httpMethod = HttpMethod.GET
          )
	public ClassificaVotiBean classificaVoti(@Named("idPartita")Long idPartita,
			   								 @Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		ClassificaVotiBean classificaVotiBean = new ClassificaVotiBean();
		//Controllo esistenza sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null )
		{
			log.log(Level.SEVERE, "classificaVoti: la sessione "+idSessione+
									" non è presente nel Datastore!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			classificaVotiBean.setHttpCode(NOT_FOUND);
			return classificaVotiBean;
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.PARTITA )
		{
			log.log(Level.SEVERE, "classificaVoti: la sessione "+idSessione+
									" non è nello stato PARTITA!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			classificaVotiBean.setHttpCode(BAD_REQUEST);
			return classificaVotiBean;
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "classificaVoti: la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			classificaVotiBean.setHttpCode(INTERNAL_SERVER_ERROR);
			return classificaVotiBean;
		}
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if( partita == null )
		{
			log.log(Level.SEVERE, "classificaVoti: la partita "+idPartita+
									" non è presente nel Datastore!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			classificaVotiBean.setHttpCode(NOT_FOUND);
			return classificaVotiBean;
		}
		//Controllo stato partita == GIOCATA
		if( partita.getStatoCorrente() != Stato.GIOCATA )
		{
			log.log(Level.SEVERE, "classificaVoti: la partita "+idPartita+
								  "non è ancora terminata, eppure c'è qualcuno che "+
								  "che sta tentando di visualizzare la classifica "
								  + "dei votiUomoPartita!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			classificaVotiBean.setHttpCode(NOT_FOUND);
			return classificaVotiBean;
		}
		
		try {
			int nVoti;
			Set<String> elencoEmailGiocanti = partita.getLinkGioca();
			Iterator<String> itGiocanti = elencoEmailGiocanti.iterator();
			while(itGiocanti.hasNext())
			{
				String email = itGiocanti.next();
				Giocatore giocatore = ofy().load().type(Giocatore.class).id(email).now();
				if( giocatore == null )
				{
					log.log(Level.SEVERE, "classificaVoti: la partita "+idPartita+
										  " ha un riferimento al giocatore "+email+
										  " che non esiste!");
					continue;
				}
				classificaVotiBean.addGiocatore(giocatore);
				//Conto il numero di voti
				nVoti = ofy().load().type(VotoUomoPartita.class).filter("partita", idPartita)
														 .filter("votato", email).count();
				classificaVotiBean.addNumeroVoti(nVoti);
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "classificaVoti: la partita "+idPartita+
								  " non è stata giocata da nessuno!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			classificaVotiBean.setHttpCode(INTERNAL_SERVER_ERROR);
			return classificaVotiBean;
		}
		
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		classificaVotiBean.setHttpCode(OK);
		return classificaVotiBean;
	}
	
	@ApiMethod(
			name = "api.invitaGiocatore",
			path = "api/invitagiocatore",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean invitaGiocatore(@Named("idSessione")Long idSessione,
									   InfoInvitoBean invitoBean)
   {
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.INVITO )
		{
			log.log(Level.SEVERE, "invitaGiocatore: la sessione "+idSessione
								 +" non è nello stato INVITO!");
			return sendResponse("Impossibile invitare un giocatore in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "invitaGiocatore: la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			return sendResponse("Errore nella gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		//Creazione invito
		DefaultBean partialResult = inserisciInvito(invitoBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "invitaGiocatore: errore durante la creazione dell'invito!");
			return partialResult;
		}
		
		//Aggiornamento stato sessione
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.GRUPPO);
		aggiornaStatoSessione(payload);
		return partialResult;
   }
	
	@ApiMethod(
			name = "api.rispondiInvito",
			path = "api/rispondiinvito",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean rispondiInvito(//@Named("emailDestinatario")String emailDestinatario,
									  @Named("idInvito")Long idInvito,
									  @Named("risposta") boolean risposta,
									  @Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.PROFILO )
		{
			log.log(Level.SEVERE, "invitaGiocatore: la sessione "+idSessione
								 +" non è nello stato PROFILO!");
			return sendResponse("Impossibile rispondere ad un invito in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "invitaGiocatore: la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			return sendResponse("Errore nella gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		//Controllo se l'invito esiste nel Datastore
		Invito invito = ofy().load().type(Invito.class).id(idInvito).now();
		if( invito == null ) return sendResponse("Invito non esistente!", PRECONDITION_FAILED);
		
		//Controllo se il destinatario esiste nel Datastore
		Giocatore destinatario= ofy().load().type(Giocatore.class).id(sessione.getEmailUtente()).now();
		if( destinatario == null ) return sendResponse("Destinatario non esistente!", PRECONDITION_FAILED);
		
		//Se destinatario accetta l'invito
		if( risposta )
		{
			//inserire linkIscritto in gruppo e destinatario
			InfoIscrittoGestisceBean iscrittoBean = new InfoIscrittoGestisceBean();
			iscrittoBean.setGiocatore(sessione.getEmailUtente());
			try {
				iscrittoBean.setGruppo(invito.getGruppo());
				DefaultBean insertResult = this.inserisciLinkIscritto(iscrittoBean);
				
				if( !insertResult.getHttpCode().equals(CREATED) )
				{
					log.log(Level.SEVERE, "Errore durante l'inserimento del linkIscritto!");
					log.log(Level.SEVERE, "faccio tearDown().");
					tearDown();
					return insertResult;
				}
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "L'invito "+invito.getId()+" non ha memorizzato l'id di un gruppo!");
				return sendResponse("L'invito non ha memorizzato l'id di un gruppo!", PRECONDITION_FAILED);
			}
			//eliminare l'invito
			DefaultBean deleteResult = eliminaInvito(sessione.getEmailUtente(), idInvito);
			
			//Se l'eliminazione dell'invito non va a buon fine, provo a fare rollback.
			if( !deleteResult.getHttpCode().equals(OK) )
			{
				deleteResult = eliminaLinkIscritto(iscrittoBean);
				if( !deleteResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "Rollback fallito durante rispondiInvito!");
				}
				log.log(Level.SEVERE, "faccio tearDown().");
				tearDown();
				return deleteResult;
			}
			
			//Aggiorna stato sessione in GRUPPO
			//Aggiornamento stato sessione
			PayloadBean payload = new PayloadBean();
			payload.setIdSessione(idSessione);
			payload.setNuovoStato(StatoSessione.GRUPPO);
			aggiornaStatoSessione(payload);
			
			return sendResponse("Risposta inviata con successo.", OK);
		}
		else	// L'invito è stato rifiutato
		{
			DefaultBean deleteResult = eliminaInvito(sessione.getEmailUtente(), idInvito);
			if( !deleteResult.getHttpCode().equals(OK) )
			{
				log.log(Level.SEVERE, "Errore durante la rimozione dell'invito durante rispondiInvito!");
				return sendResponse("Errore durante la risposta!", PRECONDITION_FAILED);
			}
			return sendResponse("Risposta inviata con successo.", OK);
		}
	}
	
	@ApiMethod(
			name = "api.confermaPartita",
			path = "partita/confermapartita",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean confermaPartita(@Named("idSessione")Long idSessione,
									   @Named("idPartita")Long idPartita)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.PARTITA )
		{
			log.log(Level.SEVERE, "confermaPartita: la sessione "+idSessione
								 +" non è nello stato PARTITA!");
			return sendResponse("Impossibile confermare una partita in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "confermaPartita: la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			return sendResponse("Errore nella gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(sessione.getEmailUtente()).now();
		if( giocatore == null ) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if(partita == null)
			return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo giocatore come proponitore
		try {
			if(!partita.getPropone().equals(sessione.getEmailUtente()))
				return sendResponse("Solo chi propone la partita può confermarla!", UNAUTHORIZED);
			
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+idPartita+" non ha un proponitore!");
			//TODO che famo?
			return sendResponse("Non si ha traccia del giocatore che ha proposto la partita!", INTERNAL_SERVER_ERROR);
		}
		
		//Controllo scelta campo
		if( partita.quantiCampi() == 0 ) return sendResponse("Non è stato scelto il campo per la partita!", PRECONDITION_FAILED);
		
		//Controllo numero disponibili
		if( getNDisponibili(idPartita).getnDisponibili()<partita.getNPartecipanti())
			return sendResponse("La partita non può essere confermata: numero di partecipanti insufficiente!", PRECONDITION_FAILED);
		
		//Cambio stato partita
		partita.setStatoCorrente(Partita.Stato.CONFERMATA);
		//Ricarico la partita sul datastore
		ofy().save().entity(partita).now();
				
		//tearDown();
		//setUp();
		//Inserimento linkGioca con i giocatori che giocheranno la partita
		int x = 0;
		Iterator<Long> it;
		try {
			it = partita.getLinkDisponibile().iterator();
			while(it.hasNext() && x<partita.getNPartecipanti())
			{
				TipoLinkDisponibile link = ofy().load().type(TipoLinkDisponibile.class).id(it.next()).now();
				//Controllo esistenza giocatore
				Giocatore g = ofy().load().type(Giocatore.class).id(link.getGiocatore()).now();
				if( g == null )
				{
					log.log(Level.SEVERE, "Il giocatore "+link.getGiocatore()+
							", preso dal link "+link.getId()+", non esiste!");
					continue;
				}
				InfoGestionePartiteBean giocaBean = new InfoGestionePartiteBean();
				giocaBean.setEmailGiocatore(link.getGiocatore());
				giocaBean.setIdPartita(link.getPartita());
				DefaultBean result = inserisciLinkGioca(giocaBean);
				if( !result.getHttpCode().equals(CREATED) )
				{
					log.log(Level.SEVERE, "Errore durante inserimento linkGioca alla partita "+partita.getId());
					continue;
				}
				
				x += 1 + link.getnAmici();
			}
			if( x < partita.getNPartecipanti() )
			{
				//Se entro in questo ramo, c'è stato un errore di gestione dei link.
				//Roll-back: rimuovo tutti i linkGioca inseriti.
				it = partita.getLinkDisponibile().iterator();
				while(it.hasNext() && x<partita.getNPartecipanti())
				{
					TipoLinkDisponibile link = ofy().load().type(TipoLinkDisponibile.class).id(it.next()).now();
					//Controllo esistenza giocatore
					Giocatore g = ofy().load().type(Giocatore.class).id(link.getGiocatore()).now();
					if( g == null )
					{
						log.log(Level.SEVERE, "Il giocatore "+link.getGiocatore()+
								", preso dal link "+link.getId()+", non esiste!");
						continue;
					}
					InfoGestionePartiteBean giocaBean = new InfoGestionePartiteBean();
					giocaBean.setEmailGiocatore(link.getGiocatore());
					giocaBean.setIdPartita(link.getPartita());
					DefaultBean result = eliminaLinkGioca(giocaBean);
					if( !result.getHttpCode().equals(OK) )
					{
						log.log(Level.SEVERE, "Errore durante rimozione linkGioca alla partita "+partita.getId());
						continue;
					}
					
					x += 1 + link.getnAmici();
				}
				return sendResponse("Errore durante la conferma della partita!", INTERNAL_SERVER_ERROR);
			}
		} catch (EccezioneMolteplicitaMinima e) {
			//NON ci dovremmo arrivare MAI!
			return sendResponse("La partita non ha giocatori disponibili!", INTERNAL_SERVER_ERROR);
		}
		
		//Per evitare errori
		//setUp();
		return sendResponse("Partita confermata con successo.", OK);
   	}
	
	@ApiMethod(
			name = "api.modificaDisponibile",
			path = "api/modificadisponibile",
			httpMethod = HttpMethod.PUT
			)
	public DefaultBean modificaDisponibile(InfoGestionePartiteBean gestioneBean,
										   @Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.DISPONIBILE_PER_PARTITA )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione
								 +" non è nello stato DISPONIBILE_PER_PARTITA!");
			return sendResponse("Impossibile modificare la propria partecipazione "
								+ "in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			return sendResponse("Errore nella gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo che il giocatore abbia dato disponibilità per la partita
		List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
											   .filter("giocatore", gestioneBean.getEmailGiocatore())
											   .filter("partita", gestioneBean.getIdPartita()).list();
		if( listLink.size() == 0 )
			return sendResponse("Il giocatore non ha dato la sua disponibilità "
								+ "per la partita!", INTERNAL_SERVER_ERROR);
		
		//Aggiorno il link, e lo ricarico sul Datastore --- E' FONDAMENTALE CHE NON CAMBI IL SUO ID
		TipoLinkDisponibile link = listLink.get(0);
		link.setnAmici(gestioneBean.getnAmici());
		ofy().save().entity(link).now();
		
		//Aggiorna stato sessione in PARTITA
		//Aggiornamento stato sessione
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.PARTITA);
		aggiornaStatoSessione(payload);
		
		return sendResponse("Link disponibile aggiornato con successo!", OK);
	}
	
	@ApiMethod(
			name = "api.listaInviti",
			path = "api/listainviti",
			httpMethod = HttpMethod.GET
			)
	public ListaInvitiBean listaInviti(@Named("idSessione")Long idSessione)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		ListaInvitiBean listaInvitiBean = new ListaInvitiBean();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaInvitiBean.setHttpCode(NOT_FOUND);
			return listaInvitiBean;
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.PROFILO )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione
								 +" non è nello stato PROFILO!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaInvitiBean.setHttpCode(BAD_REQUEST);
			return listaInvitiBean;
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			listaInvitiBean.setHttpCode(INTERNAL_SERVER_ERROR);
			return listaInvitiBean;
		}
		
		List<Invito> listaInviti = ofy().load().type(Invito.class)
									.filter("emailDestinatario", sessione.getEmailUtente()).list();
		Iterator<Invito> it = listaInviti.iterator();
		while(it.hasNext())
		{
			Invito invito = it.next();
			Gruppo g;
			try {
				g = ofy().load().type(Gruppo.class).id(invito.getGruppo()).now();
				if( g != null ) listaInvitiBean.addInvito(invito, g.getNome());
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "L'invito "+invito.getId()+" ha memorizzato un gruppo non esistente!");
			}
		}
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		listaInvitiBean.setHttpCode(OK);
		return listaInvitiBean;
	}
	
	
	@ApiMethod(
			name = "api.listaGiocatoriPartitaProposta",
			path = "api/listagiocatoripartitaproposta",
			httpMethod = HttpMethod.GET
			)
	public ListaGiocatoriBean listaGiocatoriPartitaProposta(@Named("idSessione")Long idSessione,
															@Named("idPartita")Long idPartita)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		ListaGiocatoriBean listaGiocatori = new ListaGiocatoriBean();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			listaGiocatori.setHttpCode(NOT_FOUND);
			return listaGiocatori;
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.PARTITA )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione
								 +" non è nello stato PARTITA!");
			listaGiocatori.setHttpCode(BAD_REQUEST);
			return listaGiocatori;
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			listaGiocatori.setHttpCode(INTERNAL_SERVER_ERROR);
			return listaGiocatori;
		}
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if( partita == null )
		{
			log.log(Level.SEVERE, "Partita non esistente!");
			listaGiocatori.setHttpCode(NOT_FOUND);
			return listaGiocatori;
		}
		//Controllo stato partita
		if( partita.getStatoCorrente() != Stato.PROPOSTA)
		{
			log.log(Level.SEVERE, "La partita non è nello stato PROPOSTA!");
			listaGiocatori.setHttpCode(BAD_REQUEST);
			return listaGiocatori;
		}
		
		List<TipoLinkDisponibile> elencoLink = ofy().load().type(TipoLinkDisponibile.class)
												.filter("partita", idPartita).list();
		Iterator<TipoLinkDisponibile> it = elencoLink.iterator();
		while(it.hasNext())
		{
			String email = it.next().getGiocatore();
			Giocatore giocatore = ofy().load().type(Giocatore.class).id(email).now();
			if( giocatore == null )
			{
				log.log(Level.SEVERE, "Il giocatore "+email+" non esiste!");
				continue;
			}
			listaGiocatori.addGiocatore(giocatore);
		}
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		listaGiocatori.setHttpCode(OK);
		return listaGiocatori;
	}
	
	@ApiMethod(
			name = "api.listaGiocatoriPartitaConfermata",
			path = "api/listagiocatoripartitaconfermata",
			httpMethod = HttpMethod.GET
			)
	public ListaGiocatoriBean listaGiocatoriPartitaConfermata(@Named("idSessione")Long idSessione,
															  @Named("idPartita")Long idPartita)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		ListaGiocatoriBean listaGiocatori = new ListaGiocatoriBean();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			listaGiocatori.setHttpCode(NOT_FOUND);
			return listaGiocatori;
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.PARTITA )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione
								 +" non è nello stato PARTITA!");
			listaGiocatori.setHttpCode(BAD_REQUEST);
			return listaGiocatori;
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			listaGiocatori.setHttpCode(INTERNAL_SERVER_ERROR);
			return listaGiocatori;
		}
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if( partita == null )
		{
			log.log(Level.SEVERE, "Partita non esistente!");
			listaGiocatori.setHttpCode(NOT_FOUND);
			return listaGiocatori;
		}
		//Controllo stato partita
		if( partita.getStatoCorrente() != Stato.CONFERMATA)
		{
			log.log(Level.SEVERE, "La partita non è nello stato CONFERMATA!");
			listaGiocatori.setHttpCode(BAD_REQUEST);
			return listaGiocatori;
		}
		
		try {
			Set<String> elencoEmailGiocanti = partita.getLinkGioca();
			Iterator<String> it = elencoEmailGiocanti.iterator();
			while(it.hasNext())
			{
				String email = it.next();
				Giocatore giocatore = ofy().load().type(Giocatore.class).id(email).now();
				if( giocatore == null )
				{
					log.log(Level.SEVERE, "Il giocatore "+email+" non esiste!");
					continue;
				}
				listaGiocatori.addGiocatore(giocatore);
			}	
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita non ha giocatori giocanti!");
			listaGiocatori.setHttpCode(INTERNAL_SERVER_ERROR);
			return listaGiocatori;
		}
		
		listaGiocatori.setHttpCode(OK);
		return listaGiocatori;
	}
	
	@ApiMethod(
			name = "api.inserisciDisponibilita",
			path = "api/inseriscidisponibilita",
			httpMethod = HttpMethod.POST
			)
	public DefaultBean inserisciDisponibilita(@Named("idSessione")Long idSessione,
											  @Named("idPartita")Long idPartita)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non presente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.DISPONIBILE_PER_PARTITA )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione
								 +" non è nello stato DISPONIBILE_PER_PARTITA!");
			return sendResponse("Impossibile inserire la propria partecipazione "
								+ "in questo punto!", BAD_REQUEST);
		}
		//Controllo presenza mail utente in sessione
		if( sessione.getEmailUtente() == null )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione+" non ha"
								 +"memorizzato la mail dell'utente a cui è associata!");
			return sendResponse("Errore nella gestione della sessione!", INTERNAL_SERVER_ERROR);
		}
		
		InfoGestionePartiteBean disponibileBean = new InfoGestionePartiteBean();
		disponibileBean.setEmailGiocatore(sessione.getEmailUtente());
		disponibileBean.setIdPartita(idPartita);
		DefaultBean partialResult = inserisciLinkDisponibile(disponibileBean);
		if( !partialResult.getHttpCode().equals(CREATED))
		{
			log.log(Level.SEVERE, "Errore durante la creazione del linkDisponibile!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		
		//Aggiorna stato sessione in PARTITA
		//Aggiornamento stato sessione
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.PARTITA);
		aggiornaStatoSessione(payload);
		log.log(Level.SEVERE, "faccio tearDown().");		
		tearDown();
		return partialResult;
	}

	@ApiMethod(
			name = "api.campoPartita",
			path = "api/campopartita",
			httpMethod = HttpMethod.GET
			)
	public CampoBean campoPartita(@Named("idSessione")Long idSessione,
									@Named("idPartita")Long idPartita)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		CampoBean result = new CampoBean();
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			result.setHttpCode(NOT_FOUND);
			return result;
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.PARTITA )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione
								 +" non è nello stato PARTITA!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			result.setHttpCode(BAD_REQUEST);
			return result;
		}
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if( partita == null )
		{
			log.log(Level.SEVERE, "La partita "+idPartita+" non esiste!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			result.setHttpCode(NOT_FOUND);
			return result;
		}
		if( partita.quantiCampi() == 0)
		{
			result.setHttpCode(OK);
			return result;
		}
		//La partita ha registrato un campo, lo carico dal Datastore e lo restituisco.
		try {
			Campo campo = ofy().load().type(Campo.class).id(partita.getCampo()).now();
			if( campo == null )
			{
				log.log(Level.SEVERE, "Il campo "+partita.getCampo()+"della partita non esiste!");
				log.log(Level.SEVERE, "faccio tearDown().");
				tearDown();
				result.setHttpCode(NOT_FOUND);
				return result;
			}
			result.setCampo(campo);
			result.setHttpCode(OK);
			return result;
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "quantiCampi restituisce 1, ma la partita "
								 +"non ha registrato nessun campo!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			result.setHttpCode(INTERNAL_SERVER_ERROR);
			return result;
		}
	}
	//TODO impostaCampo
	@ApiMethod(
			name = "api.impostaCampo",
			path = "api/impostacampo",
			httpMethod = HttpMethod.POST
			)
	public DefaultBean impostaCampo(@Named("idSessione")Long idSessione,
									InfoPressoBean pressoBean)
	{
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		DefaultBean partialResult;
		//Controllo esistenza Sessione
		SessioneUtente sessione = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( sessione == null)
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo stato giusto
		if( sessione.getStatoCorrente() != StatoSessione.CAMPO )
		{
			log.log(Level.SEVERE, "la sessione "+idSessione
								 +" non è nello stato CAMPO!");
			return sendResponse("Impossibile impostare un campo da questo punto!", BAD_REQUEST);
			
		}
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(pressoBean.getPartita()).now();
		if( partita == null )
		{
			log.log(Level.SEVERE, "La partita "+pressoBean.getPartita()+" non esiste!");
			return sendResponse("Partita non esistente!", NOT_FOUND);
		}
		//Controllo esistenza campo
		Campo campo = ofy().load().type(Campo.class).id(pressoBean.getCampo()).now();
		if( campo == null )
		{
			log.log(Level.SEVERE, "Il campo "+pressoBean.getCampo()+" non esiste!");
			return sendResponse("Campo non esistente!", NOT_FOUND);
		}
		
		//Se la partita ha già un campo, occorre rimuoverlo prima di inserire quello nuovo.
		if( partita.quantiCampi() != 0 )
		{
			try {
				InfoPressoBean oldPressoBean = new InfoPressoBean();
				oldPressoBean.setCampo(partita.getCampo());
				oldPressoBean.setPartita(partita.getId());
				partialResult = eliminaLinkPresso(oldPressoBean);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "Errore durante la rimozione del linkPresso"
							           	 +" dalla partita "+partita.getId()+"!" );
					return sendResponse("Errore durante il cambio di campo!", INTERNAL_SERVER_ERROR);
				}
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "Per la partita "+partita.getId()+", quantiCampi()==1"
									   +"ma getCampo lancia una eccezione!");
			}
		}
		
		//Inserimento del nuovo linkPresso
		partialResult = inserisciLinkPresso(pressoBean);
		if( !partialResult.getHttpCode().equals(CREATED) )
		{
			log.log(Level.SEVERE, "Errore durante l'inserimento del linkPresso"
					           	 +" dalla partita "+partita.getId()+"!" );
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
		
		//Aggiorna stato sessione in PARTITA
		PayloadBean payload = new PayloadBean();
		payload.setIdSessione(idSessione);
		payload.setNuovoStato(StatoSessione.PARTITA);
		aggiornaStatoSessione(payload);
		
		return sendResponse("Campo impostato con successo!", OK);
	}
	
	@ApiMethod(
			name = "api.listaPartiteProposteConfermate",
			path = "api/listaPartiteProposteConfermate",
			httpMethod = HttpMethod.GET
          )
	public ListaPartiteBean listaPartiteProposteConfermate(@Named("idGruppo")Long idGruppo,
														   @Named("tipo")int tipo,
														   @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseListaPartite("Sessione non esistente!", NOT_FOUND, new LinkedList<Partita>());
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.GRUPPO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato GRUPPO!");
			return sendResponseListaPartite("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST, new LinkedList<Partita>());
		}
		LinkedList<Partita.Stato> list = new LinkedList<Partita.Stato>();
		list.add(Partita.Stato.PROPOSTA);
		list.add(Partita.Stato.CONFERMATA);
		ListaPartiteBean lg = new ListaPartiteBean();
		switch(tipo) {
			case 1:
				List<PartitaCalcetto> l1 = ofy().load().type(PartitaCalcetto.class)
											.filter("gruppo", idGruppo).filter("statoCorrente in", list).list();
				//filtraPartite(l1);
				Iterator<PartitaCalcetto> it1 =  l1.iterator();
				log.log(Level.WARNING,"CALCETTO");
				while(it1.hasNext())
					lg.addPartita(it1.next());
				break;
			case 2:
				List<PartitaCalciotto> l2 = ofy().load().type(PartitaCalciotto.class)/*.filter("gruppo", idGruppo)*/
											.filter("statoCorrente in ", list).list();
				//filtraPartite(l2);
				if( l2.size() == 0 ) log.log(Level.WARNING, "Questo gruppo non sta organizzando nessuna partita di calciotto!");
				Iterator<PartitaCalciotto> it2 =  l2.iterator();
				log.log(Level.WARNING,"CALCIOTTO");
				while(it2.hasNext())
				{
					Partita p = it2.next();
					log.log(Level.WARNING, "Inserendo partita: "+p.getId());
					lg.addPartita(p);
				}
				break;
			case 3:
				List<PartitaCalcio> l3 = ofy().load().type(PartitaCalcio.class).filter("gruppo", idGruppo)
											.filter("statoCorrente in", list).list();
				//Aggiorno la lista rimuovendo le partite terminate
				//filtraPartite(l3);
				Iterator<PartitaCalcio> it3 =  l3.iterator();
				log.log(Level.WARNING,"CALCIO");
				while(it3.hasNext())
					lg.addPartita(it3.next());
				break;
			default:
				List<Partita> l = ofy().load().type(Partita.class).filter("gruppo", idGruppo)
					.filter("statoCorrente in", list).list();
				filtraPartite(l);
				Iterator<Partita> it = l.iterator();
				while(it.hasNext())
					lg.addPartita(it.next());
		}
		
		tearDown();
		lg.setHttpCode(OK);
		lg.setResult("Operazione completata con successo!");
		return lg;
	}
	
	@ApiMethod(
			name = "api.listaPartiteTerminate",
			path = "api/listaPartiteTerminate",
			httpMethod = HttpMethod.GET
          )
	public ListaPartiteBean listaPartiteTerminate(@Named("idGruppo")Long idGruppo, @Named("tipo")int tipo,
			   									  @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseListaPartite("Sessione non esistente!", NOT_FOUND, new LinkedList<Partita>());
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.STORICO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato STORICO!");
			return sendResponseListaPartite("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST, new LinkedList<Partita>());
		}
		ListaPartiteBean lg = new ListaPartiteBean();
		switch(tipo) {
			case 1:
				List<PartitaCalcetto> l1 = ofy().load().type(PartitaCalcetto.class)
											.filter("gruppo", idGruppo).filter("statoCorrente", Partita.Stato.GIOCATA).list();
				//filtraPartite(l1);
				Iterator<PartitaCalcetto> it1 =  l1.iterator();
				log.log(Level.WARNING,"CALCETTO");
				while(it1.hasNext())
					lg.addPartita(it1.next());
				break;
			case 2:
				List<PartitaCalciotto> l2 = ofy().load().type(PartitaCalciotto.class)/*.filter("gruppo", idGruppo)*/
											.filter("statoCorrente", Partita.Stato.GIOCATA).list();
				//filtraPartite(l2);
				if( l2.size() == 0 ) log.log(Level.WARNING, "Questo gruppo non sta organizzando nessuna partita di calciotto!");
				Iterator<PartitaCalciotto> it2 =  l2.iterator();
				log.log(Level.WARNING,"CALCIOTTO");
				while(it2.hasNext())
				{
					Partita p = it2.next();
					log.log(Level.WARNING, "Inserendo partita: "+p.getId());
					lg.addPartita(p);
				}
				break;
			case 3:
				List<PartitaCalcio> l3 = ofy().load().type(PartitaCalcio.class).filter("gruppo", idGruppo)
											.filter("statoCorrente", Partita.Stato.GIOCATA).list();
				//Aggiorno la lista rimuovendo le partite terminate
				//filtraPartite(l3);
				Iterator<PartitaCalcio> it3 =  l3.iterator();
				log.log(Level.WARNING,"CALCIO");
				while(it3.hasNext())
					lg.addPartita(it3.next());
				break;
			default:
				List<Partita> l = ofy().load().type(Partita.class).filter("gruppo", idGruppo)
					.filter("statoCorrente", Partita.Stato.GIOCATA).list();
				//filtraPartite(l);
				Iterator<Partita> it = l.iterator();
				while(it.hasNext())
					lg.addPartita(it.next());
		}
		tearDown();
		lg.setHttpCode(OK);
		lg.setResult("Operazione completata con successo!");
		return lg;
	}
	
	@ApiMethod(
			name = "api.isIscritto",
			path = "api/isIscritto",
			httpMethod = HttpMethod.GET
          )
	public DefaultBean isIscritto(/*@Named("emailUtente")String emailUtente,*/ @Named("idGruppo")Long idGruppo,
								  @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseAnswer("Sessione non esistente!", NOT_FOUND, false);
		}
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(/*emailUtente*/s.getEmailUtente()).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponseAnswer("Giocatore non esistente!", NOT_FOUND, false);
		}
		//Controllo esistenza Gruppo
		Gruppo gr = ofy().load().type(Gruppo.class).id(idGruppo).now();
		if( gr == null)
		{
			log.log(Level.SEVERE, "Gruppo non esistente!");
			return sendResponseAnswer("Gruppo non esistente!", NOT_FOUND, false);
		}
		//Controllo stato sessione
		//TODO Bisogna metterlo?
		if( s.getStatoCorrente() != StatoSessione.GRUPPO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato GRUPPO!");
			return sendResponseAnswer("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST, false);
		}
		try {
			Iterator<Long> it = gr.getGiocatoriIscritti().iterator();
			while(it.hasNext()) {
				if(g.getEIscritto().contains(it.next()))
					return sendResponseAnswer("Il giocatore è iscritto al gruppo!", OK, true);
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il gruppo "+ gr.getId() +" non ha alcun giocatore iscritto!");
			return sendResponseAnswer("Il gruppo non ha alcun giocatore iscritto!", INTERNAL_SERVER_ERROR, false); 
		}
		return sendResponseAnswer("Il giocatore non è iscritto al gruppo!", OK, false);
	}
	
	@ApiMethod(
			name = "api.isDisponibile",
			path = "api/isDisponibile",
			httpMethod = HttpMethod.GET
          )
	public DefaultBean isDisponibile(/*Named("emailUtente"String emailUtente, */@Named("idPartita")Long idPartita,
									 @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseAnswer("Sessione non esistente!", NOT_FOUND, false);
		}
		//Controllo stato sessione
		if( !(s.getStatoCorrente() == StatoSessione.PARTITA ||
		      s.getStatoCorrente() == StatoSessione.DISPONIBILE_PER_PARTITA) )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato PARTITA né in DISPONIBILE_PER_PARTITA!");
			return sendResponseAnswer("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST, false);
		}
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(/*emailUtente*/s.getEmailUtente()).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponseAnswer("Giocatore non esistente!", NOT_FOUND, false);
		}
		//Controllo esistenza Partita
		Partita p = ofy().load().type(Partita.class).id(idPartita).now();
		if( p == null)
		{
			log.log(Level.SEVERE, "Partita non esistente!");
			return sendResponseAnswer("Partita non esistente!", NOT_FOUND, false);
		}
		
		Iterator<Long> it = null;
		
		try {
			it = p.getLinkDisponibile().iterator();
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "la partita "+p.getId()+
					" non ha memorizzato nessun link Disponibile!");
			return sendResponseAnswer("la partita non ha memorizzato alcun link Disponibile!", OK, false);
		}
		
		while(it.hasNext())
		{
			TipoLinkDisponibile l = ofy().load().type(TipoLinkDisponibile.class).id(it.next()).now();
			if( l.getGiocatore().equals(/*emailUtente*/s.getEmailUtente()) )
				return sendResponseAnswer("l'utente ha già dato la disponibilità per questa partita!", OK, true);
		}
		return sendResponseAnswer("l'utente non ha dato la disponibilità per questa partita!", OK, false);
	}
	
	@ApiMethod(
			name = "api.hasVotato",
			path = "api/hasVotato",
			httpMethod = HttpMethod.GET
          )
	public DefaultBean hasVotato(/*@Named("emailUtente")String emailUtente, */@Named("idPartita")Long idPartita,
								 @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponseAnswer("Sessione non esistente!", NOT_FOUND, true);
		}
		//Controllo stato sessione
		if( s.getStatoCorrente() != StatoSessione.PARTITA )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato PARTITA!");
			return sendResponseAnswer("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST, true);
		}
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(/*emailUtente*/s.getEmailUtente()).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponseAnswer("Giocatore non esistente!", NOT_FOUND, true);
		}
		//Controllo esistenza Partita
		Partita p = ofy().load().type(Partita.class).id(idPartita).now();
		if( p == null)
		{
			log.log(Level.SEVERE, "Partita non esistente!");
			return sendResponseAnswer("Partita non esistente!", NOT_FOUND, true);
		}
		Iterator<Long> it = p.getLinkVotoPerPartita().iterator();
		while(it.hasNext())
		{
			VotoUomoPartita v = ofy().load().type(VotoUomoPartita.class).id(it.next()).now();
			try {
				if( v.getVotanteUP().equals(/*emailUtente*/s.getEmailUtente()) )
					return sendResponseAnswer("l'utente ha già votato per questa partita!", OK, true);
				
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "Il voto "+v.getId()+
						" non ha memorizzato l'id di un votante!");
				continue;
			}
		}
		return sendResponseAnswer("l'utente non ha votato per questa partita!", OK, false);
	}
	
	@ApiMethod(
			name = "api.cambiaStatoIscrizione",
			path = "api/cambiaStatoIscrizione",
			httpMethod = HttpMethod.GET
          )
	public DefaultBean cambiaStatoIscrizione(/*@Named("emailUtente")String emailUtente, */@Named("idGruppo")Long idGruppo,
								 @Named("idSessione")Long idSessione) {
		setUp();
		//Controllo esistenza Sessione
		SessioneUtente s = ofy().load().type(SessioneUtente.class).id(idSessione).now();
		if( s == null )
		{
			log.log(Level.SEVERE, "Sessione non esistente!");
			return sendResponse("Sessione non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Giocatore
		Giocatore g = ofy().load().type(Giocatore.class).id(/*emailUtente*/s.getEmailUtente()).now();
		if( g == null)
		{
			log.log(Level.SEVERE, "Giocatore non registrato!");
			return sendResponse("Giocatore non esistente!", NOT_FOUND);
		}
		//Controllo esistenza Gruppo
		Gruppo gr = ofy().load().type(Gruppo.class).id(idGruppo).now();
		if( gr == null)
		{
			log.log(Level.SEVERE, "Gruppo non esistente!");
			return sendResponse("Gruppo non esistente!", NOT_FOUND);
		}
		//Controllo gruppo aperto
		if(!gr.getClass().equals(GruppoAperto.class)) 
		{
			log.log(Level.SEVERE, "Gruppo non aperto!");
			return sendResponse("Gruppo non aperto!", PRECONDITION_FAILED);
		}
		//Controllo stato sessione
		//TODO Bisogna metterlo?
		if( s.getStatoCorrente() != StatoSessione.GRUPPO )
		{
			log.log(Level.SEVERE, "La sessione "+idSessione+" non è nello stato GRUPPO!");
			return sendResponse("Impossibile chiamare il metodo in questo punto!", BAD_REQUEST);
		}
		InfoIscrittoGestisceBean igBean = new InfoIscrittoGestisceBean();
		igBean.setGiocatore(/*emailUtente*/s.getEmailUtente());
		igBean.setGruppo(idGruppo);
		if(!isIscritto(idGruppo, idSessione).isAnswer()) {
			DefaultBean response = inserisciLinkIscritto(igBean);
			if(!response.getHttpCode().equals(CREATED)) {
				log.log(Level.SEVERE, "iscriviti: errore durante l'inserimento "
						 +"del link iscritto nel gruppo "+idGruppo+"!");
				tearDown();
				return response;
			}
		}
		else {
			DefaultBean response = eliminaLinkIscritto(igBean);
			if(!response.getHttpCode().equals(OK)) {
				log.log(Level.SEVERE, "iscriviti: errore durante la rimozione "
						 +"del link iscritto nel gruppo "+idGruppo+"!");
				tearDown();
				return response;
			}
		}
		return sendResponse("Stato iscrizione aggiornato con succcesso!", OK);
	}

	/////////////////////////////////////////
	///////// TODO METODI AUSILIARI /////////
	/////////////////////////////////////////
	
	@BeforeMethod
    private void setUp() {
        session = ObjectifyService.begin();
    }

    @AfterMethod
    private void tearDown() {
        session.close();
    }
    
    private DefaultBean sendResponse(String mex, String code)
    {
    	log.log(Level.SEVERE, "faccio tearDown().");
    	tearDown();
    	DefaultBean response = new DefaultBean();
		response.setResult(mex);
		response.setHttpCode(code);
		return response;
    }
    
    //Metodo aggiunto per farsi restituire l'id degli oggetti creati,
    //necessario per le API di alto livello.
    private DefaultBean sendResponseCreated(String mex, String code, Long idCreated)
    {
    	log.log(Level.SEVERE, "faccio tearDown().");
    	tearDown();
    	DefaultBean response = new DefaultBean();
		response.setResult(mex);
		response.setHttpCode(code);
		response.setIdCreated(idCreated);
		return response;
    }
    
    private DefaultBean sendResponseAnswer(String mex, String code, boolean answer)
    {
    	DefaultBean response = new DefaultBean();
		response.setResult(mex);
		response.setHttpCode(code);
		response.setAnswer(answer);
		tearDown();
		return response;
    }
    
    private GiocatoreBean sendResponseGiocatore(Giocatore g, String mex, String code) {
    	GiocatoreBean response = new GiocatoreBean();
    	response.setGiocatore(g);
    	response.setResult(mex);
    	response.setHttpCode(code);
    	tearDown();
    	return response;
    }
    
    private GruppoBean sendResponseGruppo(Gruppo g, String emailAdmin, String mex, String code)
    {
    	GruppoBean response = new GruppoBean();
    	response.setGruppo(g);
    	response.setEmailAdmin(emailAdmin);
    	response.setResult(mex);
    	response.setHttpCode(code);
    	tearDown();
    	return response;
    }
    
    private PartitaBean sendResponsePartita(Partita p, String mex, String code) {
    	PartitaBean response = new PartitaBean();
    	response.setPartita(p);
    	response.setResult(mex);
    	response.setHttpCode(code);
    	tearDown();
    	return response;
    }
    
    private ListaGruppiBean sendResponseListaGruppi(String mex, String code, LinkedList<Gruppo> l) {
    	ListaGruppiBean response = new ListaGruppiBean();
		response.setResult(mex);
		response.setHttpCode(code);
		LinkedList<InfoGruppoBean> li = new LinkedList<InfoGruppoBean>();
 		Iterator<Gruppo> it = l.iterator();
		while( it.hasNext() )
		{
			Gruppo g = it.next();
			li.add(ListaGruppiBean.convertiGruppo(g));
		}
		response.setListaGruppi(li);
		tearDown();
		return response;
    }
    
    private ListaGiocatoriBean sendResponseListaGiocatori(String mex, String code, LinkedList<Giocatore> l) {
    	ListaGiocatoriBean response = new ListaGiocatoriBean();
		response.setResult(mex);
		response.setHttpCode(code);
		response.setListaGiocatori(l);
		tearDown();
		return response;
    }
    
    private ListaPartiteBean sendResponseListaPartite(String mex, String code, LinkedList<Partita> l) {
    	ListaPartiteBean response = new ListaPartiteBean();
		response.setResult(mex);
		response.setHttpCode(code);
		response.setListaPartite(l);
		tearDown();
		return response;
    }
    
    private DefaultBean rimuoviPartitaGiocataConfermata(Long idPartita)
    {
    	Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
    	if( partita == null )
    		return sendResponse("Partita giocata non esistente!", NOT_FOUND);
    	
    	DefaultBean partialResult;
    	if( partita.getStatoCorrente() == Partita.Stato.GIOCATA )
    	{
    		//Elimina voti
	    	Set<Long> elencoVoti = partita.getLinkVotoPerPartita();
	    	Iterator<Long> itVoti = elencoVoti.iterator();
	    	while( itVoti.hasNext() )
	    	{
	    		Long idVoto = itVoti.next();
	    		VotoUomoPartita voto = ofy().load().type(VotoUomoPartita.class).id(idVoto).now();
	    		if( voto == null )
	    		{
	    			log.log(Level.SEVERE, "rimuoviPartitaGiocata: il voto "+idVoto+
	    									" non è presente nel Datastore!");
	    			continue;
	    		}
	    		else
	    		{
	    			try {
						partialResult = eliminaVotoUomoPartita(voto.getVotanteUP(), voto.getId());
						if( !partialResult.getHttpCode().equals(OK) )
						{
							log.log(Level.SEVERE, "rimuoviPartitaGiocata: errore durante l'eliminazione"
												 +" del voto "+voto.getId()+"!");
							continue;
							//TODO o return partialResult; ?
						}
					} catch (EccezioneMolteplicitaMinima e) {
						log.log(Level.SEVERE, "rimuoviPartitaGiocata: il voto "+voto.getId()+
												" non ha memorizzato la mail del votante!");
						continue;
					}
	    		}
	    	}
    	}
    	//Elimina linkDisponibile
    	try {
			List<Long> elencoDisponibili = partita.getLinkDisponibile();
			Set<String> elencoGiocanti = partita.getLinkGioca();
			Iterator<Long> itDisponibili = elencoDisponibili.iterator();
			while( itDisponibili.hasNext() )
			{
				Long idLink = itDisponibili.next();
				TipoLinkDisponibile link = ofy().load().type(TipoLinkDisponibile.class)
											.id(idLink).now();
				if( link == null )
				{
					log.log(Level.SEVERE, "rimuoviPartitaGiocata: il linkDisponibile "+idLink+" non è presente nel Datastore!");
					continue;
				}
				InfoGestionePartiteBean gestioneBean = new InfoGestionePartiteBean();
				gestioneBean.setEmailGiocatore(link.getGiocatore());
				gestioneBean.setIdPartita(idPartita);
				partialResult = eliminaLinkDisponibile(gestioneBean);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "rimuoviPartitaGiocata: errore durante l'eliminazione del linkDisponibile tra "+link.getGiocatore()+"e "+link.getPartita()+"!");
					continue;
					//TODO o return partialResult; ?
				}
				
				//Elimina linkGioca
				if( elencoGiocanti.contains(link.getGiocatore()) )
				{
					partialResult = eliminaLinkGioca(gestioneBean);
					if( !partialResult.getHttpCode().equals(OK) )
					{
						log.log(Level.SEVERE, "rimuoviPartitaGiocata: errore durante l'eliminazione del linkGioca tra "+link.getGiocatore()+"e "+link.getPartita()+"!");
						continue;
						//TODO o return partialResult; ?
					}
				}
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "rimuoviPartitaGiocata: La partita "+partita.getId()+ "non ha linkDisponibile!" );
		}
    	
    	//Elimina linkPropone
    	try {
    		InfoGestionePartiteBean proponeBean = new InfoGestionePartiteBean();
			proponeBean.setEmailGiocatore(partita.getPropone());
			proponeBean.setIdPartita(idPartita);
	    	partialResult = eliminaLinkPropone(proponeBean);
	    	if( !partialResult.getHttpCode().equals(OK) )
			{
				log.log(Level.SEVERE, "rimuoviPartitaGiocata: errore durante l'eliminazione del linkPropone "
									 +"tra "+partita.getPropone()+"e "+idPartita+"!");
				//TODO o return partialResult; ?
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+idPartita+"non ha memorizzato l'email di chi l'ha proposta!");
		}
    	//Elimina linkOrganizza con gruppo
    	try {
	    	InfoOrganizzaBean organizzaBean = new InfoOrganizzaBean();
	    	organizzaBean.setPartita(idPartita);
			organizzaBean.setGruppo(partita.getLinkOrganizza());
			partialResult = eliminaLinkOrganizza(organizzaBean);
			if( !partialResult.getHttpCode().equals(OK) )
			{
				log.log(Level.SEVERE, "rimuoviPartitaGiocata: errore durante l'eliminazione del linkOrganizza "
									 +"tra "+partita.getLinkOrganizza()+"e "+idPartita+"!");
				//TODO o return partialResult; ?
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+idPartita+"non ha memorizzato "
								 +"l'id del gruppo che l'ha organizzata!");
		}
    	//Elimina partita
    	InfoPartitaBean partitaBean = new InfoPartitaBean();
    	partitaBean.setId(idPartita);
    	partialResult = eliminaPartita(partitaBean);
    	if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "rimuoviPartitaGiocata: errore durante l'eliminazione della "
								 +"partita "+idPartita+"!");
			log.log(Level.SEVERE, "faccio tearDown().");
			tearDown();
			return partialResult;
		}
    	
    	return sendResponse("Partita giocata rimossa con successo.", OK);
    }
    
    private DefaultBean cancellaPartita(InfoGestionePartiteBean annullaBean)
	{	
    	log.log(Level.SEVERE, "Faccio setUp()");
    	setUp();
    	//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(annullaBean.getEmailGiocatore()).now();
		if( giocatore == null ) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(annullaBean.getIdPartita()).now();
		if(partita == null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo stato partita
		if( partita.getStatoCorrente()!=Partita.Stato.PROPOSTA)
			return sendResponse("La partita non può essere annullata, poiché confermata o già giocata!", BAD_REQUEST);
	/*	//Controllo giocatore come proponitore
		try {
			if(!partita.getPropone().equals(annullaBean.getEmailGiocatore()))
				return sendResponse("Solo chi propone la partita può confermarla!", UNAUTHORIZED);
			
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "annullaPartita: la partita "+annullaBean.getIdPartita()+" non ha un proponitore!");
			//TODO che famo?
			return sendResponse("Non si ha traccia del giocatore che ha proposto la partita!", INTERNAL_SERVER_ERROR);
		}
	*/	
		DefaultBean partialResult;
		//Rimozione linkPresso alla partita
		if( partita.quantiCampi()!=0 )
		{
			try {
				InfoPressoBean pressoBean = new InfoPressoBean();
				pressoBean.setCampo(partita.getCampo());
				pressoBean.setPartita(annullaBean.getIdPartita());
				partialResult = eliminaLinkPresso(pressoBean);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "annullaPartita: errore durante l'eliminazione del linkPresso dalla partita "
											+pressoBean.getPartita()+"!");
					log.log(Level.SEVERE, "faccio tearDown().");
					tearDown();
					return partialResult;
				}
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "annullaPartita: la partita non ha memorizzato un campo, eppure quantiCampi() restituisce 1!");
			}
		}
		//Rimozione linkOrganizza alla partita
		if( partita.quantiOrganizza()!=0 )
		{
			try{
				InfoOrganizzaBean organizzaBean = new InfoOrganizzaBean();
				organizzaBean.setGruppo(partita.getLinkOrganizza());
				organizzaBean.setPartita(annullaBean.getIdPartita());
				partialResult = eliminaLinkOrganizza(organizzaBean);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "annullaPartita: errore durante l'eliminazione del linkOrganizza dalla partita "
											+organizzaBean.getPartita()+"!");
					log.log(Level.SEVERE, "faccio tearDown().");
					tearDown();
					return partialResult;
				}
			} catch(EccezioneMolteplicitaMinima e)
			{
				log.log(Level.SEVERE, "La partita non ha un linkOrganizza, ma quantiOrganizza restituisce 1!");
			}
		}
		//Rimozione linkDisponibile alla partita
		if( partita.quantiDisponibili()!=0 )
		{
			try{
				Iterator<Long> it = partita.getLinkDisponibile().iterator();
				while( it.hasNext() )
				{
					TipoLinkDisponibile link = ofy().load().type(TipoLinkDisponibile.class).id(it.next()).now();
					InfoGestionePartiteBean disponibileBean = new InfoGestionePartiteBean();
					disponibileBean.setEmailGiocatore(link.getGiocatore());
					disponibileBean.setIdPartita(annullaBean.getIdPartita());
					partialResult = eliminaLinkDisponibile(disponibileBean);
					if( !partialResult.getHttpCode().equals(OK) )
					{
						log.log(Level.SEVERE, "annullaPartita: errore durante l'eliminazione del linkDisponibile "
											 +"dalla partita "+disponibileBean.getIdPartita()+"!");
						log.log(Level.SEVERE, "faccio tearDown().");
						tearDown();
						return partialResult;
					}
				}
			} catch(EccezioneMolteplicitaMinima e)
			{
				log.log(Level.SEVERE, "La partita non ha nessun linkDisponibile, ma quantiOrganizza() non restituisce 0!");
			}
		}
		//Rimozione linkPropone alla partita
		if( partita.quantiPropone()!=0 )
		{
			InfoGestionePartiteBean proponeBean = new InfoGestionePartiteBean();
			proponeBean.setEmailGiocatore(annullaBean.getEmailGiocatore());
			proponeBean.setIdPartita(annullaBean.getIdPartita());
			partialResult = eliminaLinkPropone(proponeBean);
			if( !partialResult.getHttpCode().equals(OK) )
			{
				log.log(Level.SEVERE, "annullaPartita: errore durante l'eliminazione del linkPropone "
									 +"dalla partita "+proponeBean.getIdPartita()+"!");
				log.log(Level.SEVERE, "faccio tearDown().");
				tearDown();
				return partialResult;
			}
		}
		//Rimozione partita
		InfoPartitaBean partitaBean = new InfoPartitaBean();
		partitaBean.setId(annullaBean.getIdPartita());
		partialResult = eliminaPartita(partitaBean);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "annullaPartita: errore durante l'eliminazione "
								 +"della partita "+partitaBean.getId()+"!");
			
		}
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		return partialResult;
	}
    
    private DefaultBean annullaDisponibilita(InfoGestionePartiteBean gestioneBean)
    {
    	log.log(Level.SEVERE, "devo annullare la disponibilita di "+gestioneBean.getEmailGiocatore()
    							+" nella partita "+gestioneBean.getIdPartita());
    	log.log(Level.SEVERE, "faccio setUp().");
    	setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if( giocatore == null ) return sendResponse("Giocatore non esistente!", PRECONDITION_FAILED);
		
		//Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita == null) return sendResponse("Partita non esistente!", PRECONDITION_FAILED);
		
		//Controllo stato partita
		if( partita.getStatoCorrente()!=Partita.Stato.PROPOSTA)
			return sendResponse("Non è possibile annullare la propria disponibilità, "
								+"poiché la partita è confermata o già giocata!", BAD_REQUEST);
		log.log(Level.SEVERE, "faccio tearDown().");
		tearDown();
		//Elimina LinkDisponibile
		InfoGestionePartiteBean proponeBean = new InfoGestionePartiteBean();
		proponeBean.setEmailGiocatore(gestioneBean.getEmailGiocatore());
		proponeBean.setIdPartita(gestioneBean.getIdPartita());
		DefaultBean partialResult = eliminaLinkDisponibile(proponeBean);
		if( !partialResult.getHttpCode().equals(OK) )
		{
			log.log(Level.SEVERE, "annullaDisponibilita: errore durante l'uscita del giocatore"
								 +" dalla partita "+gestioneBean.getIdPartita()+"!");
			return partialResult;
		}
		
		//Se il giocatore era anche il proponitore, elimino il linkPropone
		try {
			if( partita.getPropone().equals(gestioneBean.getEmailGiocatore()))
			{
				partialResult = eliminaLinkPropone(proponeBean);
				if( !partialResult.getHttpCode().equals(OK) )
				{
					log.log(Level.SEVERE, "annullaDisponibilita: errore durante l'eliminazione"
							 +" del linkPropone dalla partita "+gestioneBean.getIdPartita()+"!");
					return partialResult;
				}
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita non ha un linkPropone!");
		}
		
		//Se la partita non ha più giocatori disponibili, la elimino.
		log.log(Level.SEVERE, "faccio setUp().");
		setUp();
		partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		log.log(Level.SEVERE, "quantiDisponibili= "+partita.quantiDisponibili());
		if( partita.quantiDisponibili() == 0 )
		{
			log.log(Level.SEVERE, "La partita non ha più partecipanti; la elimino.");
			partialResult = cancellaPartita(proponeBean);
			if( !partialResult.getHttpCode().equals(OK) )
			{
				log.log(Level.SEVERE, "annullaDisponibilita: errore durante l'eliminazione"
									 +" della partita "+gestioneBean.getIdPartita()+"!");
				log.log(Level.SEVERE, "faccio tearDown().");
				tearDown();
				return partialResult;
			}
		}
		//Se la partita ha altri giocatori, e se il giocatore era il proponitore,
		//devo nominare un nuovo proponitore.
		else if( partita.quantiPropone()==0)
		{
			TipoLinkDisponibile link;
			try {
				link = ofy().load().type(TipoLinkDisponibile.class)
											.id(partita.getLinkDisponibile().get(0)).now();
				if( link == null )
				{
					log.log(Level.SEVERE, "Errore durante il caricamento del link del nuovo proponitore!");
				}
				else
				{
					proponeBean.setEmailGiocatore(link.getGiocatore());
					partialResult = inserisciLinkPropone(proponeBean);
					if( !partialResult.getHttpCode().equals(CREATED) )
					{
						log.log(Level.SEVERE, "Errore durante l'inserimento del nuovo linkPropone!");
					}
				}
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "quantiDisponibilita!=0, eppure getDisponibili() ha lanciato l'eccezione!");
			}
		}
		
		return sendResponse("Disponibilita rimossa con successo.", OK);
    }
    
    // Metodo privato che data una lista, rimuove le partite Terminate
    private void filtraPartite(List<? extends Partita> listaPartite)
    {
    	if( listaPartite == null ) return;
    	
    	Iterator<? extends Partita> it = listaPartite.iterator();
    	while(it.hasNext())
    	{
    		Partita p = it.next();
    		if(p.getStatoCorrente() == Partita.Stato.CONFERMATA)
    		{
    			p.setStatoCorrente(Partita.Stato.GIOCATA);
    			ofy().save().entity(p).now();
    			it.remove();
    			
    		}
    		else
    		{
	    		Calendar match_date = DateToCalendar(p.getDataOra());
	    		Calendar now = Calendar.getInstance();
	    		//TODO poi vediamo
	    		match_date.add(Calendar.HOUR, 2);
	    		if( now.after(match_date) )
	    		{
	    			p.setStatoCorrente(Partita.Stato.GIOCATA);
	    			ofy().save().entity(p).now();
	    			it.remove();
	    		}
    		}
    	}
    }
    
    private static Calendar DateToCalendar(Date date ) 
    { 
		 Calendar cal = null;
		 try {   
			 //DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss: z yyyy");
			 DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 //DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
			 //DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
			 date = (Date)formatter.parse(date.toString()); 
			 cal=Calendar.getInstance();
			 cal.setTime(date);
		  }
		  catch (ParseException e)
		  {
			  System.out.println("Exception :"+e);  
		  }  
		  return cal;
    }
}