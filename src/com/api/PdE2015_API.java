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
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(giocatoreBean.getEmail()).now();
		
		if(giocatore != null) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore già esistente!");
			tearDown();
			return response;
		}
		
		giocatore = new Giocatore(giocatoreBean.getNome(), giocatoreBean.getEmail(), giocatoreBean.getTelefono(),
									giocatoreBean.getRuoloPreferito(), giocatoreBean.getFotoProfilo());
		ofy().save().entity(giocatore).now();
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
		Giocatore g = ofy().load().type(Giocatore.class).id(ig.getEmail()).now();
		
		if(g == null) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		
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
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(giocatoreBean.getEmail()).now();
		
		if(giocatore == null) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		
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
		if(partitaBean.getQuota() != 0.0f)
			partita.setQuota(partitaBean.getQuota());

		ofy().save().entity(partita).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Partita aggiornata con successo!");
		return response;
	}
	
	
	//TODO API Invito
	@ApiMethod(
			name = "invito.inserisciInvito",
			path = "invito",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciInvito(InfoInvitoBean invitoBean)
	{
		//TODO controllo se il destinatario non fa già parte del gruppo
		
		if( invitoBean.getEmailDestinatario().equals(invitoBean.getEmailMittente()))
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Mittente e destinatario coincidenti!");
			tearDown();
			return response;
		}
		
		setUp();
		//Controllo se il mittente esiste nel Datastore
		Giocatore mittente = ofy().load().type(Giocatore.class).id(invitoBean.getEmailMittente()).now();
		if( mittente == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Mittente non esistente!");
			tearDown();
			return response;
		}
		//Controllo se il destinatario esiste nel Datastore
		Giocatore destinatario = ofy().load().type(Giocatore.class).id(invitoBean.getEmailDestinatario()).now();
		if( destinatario == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Destinatario non esistente!");
			tearDown();
			return response;
		}
		//Controllo se il gruppo esiste nel Datastore
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(invitoBean.getIdGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
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
				{
					DefaultBean response = new DefaultBean();
					response.setResult("Il destinatario è già stato invitato nel gruppo!");
					tearDown();
					return response;
				}
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "L'invito "+i.getId()+
						" non ha memorizzato l'id di un gruppo!");
				continue;
			}
		}
		//Carico preventivamente l'invito sul Datastore per ottenere un id
		Invito invito = new Invito(invitoBean.getEmailMittente(), invitoBean.getIdGruppo());
		ofy().save().entity(invito).now();
		destinatario.inserisciLinkDestinatario(invito.getId());
		invito.inserisciLinkDestinatario(destinatario.getEmail());
		ofy().save().entity(invito).now();
		ofy().save().entity(destinatario).now();

	/*	//Controllo che il destinatario non sia già stato invitato nel gruppo
		
		Iterator<Long> it = destinatario.getLinkDestinatario().iterator();
		while(it.hasNext())
		{
			Long idLink = it.next();
			TipoLinkDestinatario link = ofy().load().type(TipoLinkDestinatario.class).id(idLink).now();
			if( link == null )
			{	
				log.log(Level.SEVERE, "Il destinatario "+destinatario.getEmail()+
						" ha memorizzato l'id di un linkDestinario che non esiste!");
				continue;
			}
			Invito i = ofy().load().type(Invito.class).id(link.getIdInvito()).now();
			if( i == null )
			{
				log.log(Level.SEVERE, "Il linkDestinatario "+link.getId()+
						" ha memorizzato l'id di un Invito che non esiste!");
				continue;
			}
			try {
				if(i.getGruppo().equals(gruppo.getId()))
				{
					DefaultBean response = new DefaultBean();
					response.setResult("Il destinatario è già stato invitato nel gruppo!");
					tearDown();
					return response;
				}
			} catch (EccezioneMolteplicitaMinima e) {
				log.log(Level.SEVERE, "L'invito "+i.getId()+
						" non ha memorizzato l'id di un gruppo!");
				continue;
			}
		}
		//Carico preventivamente l'invito sul Datastore per ottenere un id
		Invito invito = new Invito(invitoBean.getEmailMittente(), invitoBean.getIdGruppo());
		ofy().save().entity(invito).now();
		invito = ofy().load().entity(invito).now();
		//TODO ricordati di vedere il fatto dell'id dopo il caricamento
		//Creazione TipoLinkDestinatario
		try {
			TipoLinkDestinatario link = new TipoLinkDestinatario(invitoBean.getEmailDestinatario(), invito.getId());
			ofy().save().entity(link).now();
			destinatario.inserisciLinkDestinatario(link.getId());
			invito.inserisciLinkDestinatario(link.getId());
			ofy().save().entity(invito).now();
			ofy().save().entity(destinatario).now();
		} catch (EccezionePrecondizioni e)
		{
			ofy().delete().entity(invito).now();
			log.log(Level.SEVERE, "Uno tra invito.getId() e invitoBean.getEmailDestinatario() ha restituito null");
			DefaultBean response = new DefaultBean();
			response.setResult("Errore imprevisto!");
			tearDown();
			return response;
		}
	*/	
		DefaultBean response = new DefaultBean();
		response.setResult("Invito inserito con successo!");
		tearDown();
		return response;
	
	}
	
	@ApiMethod(
				name = "invito.eliminaInvito",
				path = "invito/delete",
				httpMethod = HttpMethod.POST
	          )
	public DefaultBean eliminaInvito(@Named("emailDestinatario")String emailDestinatario,
									 @Named("idInvito")Long idInvito)
	{
		setUp();
		//Controllo se l'invito esiste nel Datastore
		Invito invito = ofy().load().type(Invito.class).id(idInvito).now();
		if( invito == null )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Invito non esistente!");
			tearDown();
			return response;
		}
		//Controllo se il destinatario esiste nel Datastore
		Giocatore destinatario= ofy().load().type(Giocatore.class).id(emailDestinatario).now();
		if( destinatario == null )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Destinatario non esistente!");
			tearDown();
			return response;
		}
		//Rimozione idLink da destinatario.linkDestinatario
		destinatario.eliminaLinkDestinatario(idInvito);
		ofy().save().entity(destinatario).now();
		//Rimozione invito dal Datastore
		ofy().delete().type(Invito.class).id(idInvito).now();
		
	/*	
		Long idLink;
		try {
			idLink = invito.getLinkDestinatario();
			//Rimozione idLink da destinatario.linkDestinatario
			destinatario.eliminaLinkDestinatario(idLink);
			ofy().save().entity(destinatario).now();
			//Rimozione tipolink dal Datastore
			ofy().delete().type(TipoLinkDestinatario.class).id(idLink).now();
			//Rimozione invito dal Datastore
			ofy().delete().type(Invito.class).id(idInvito).now();
		
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "L'invito"+invito.getId()+" non ha memorizzato l'id di un linkDestinatario!");
			DefaultBean response = new DefaultBean();
			response.setResult("Errore non previsto!");
			tearDown();
			return response;
		}
	*/	
		DefaultBean response = new DefaultBean();
		response.setResult("Invito rimosso con successo!");
		tearDown();
		return response;
	}
	
	//TODO API VotoUomoPartita
	@ApiMethod(
				name = "voto.inserisciVotoUomoPartita",
				path = "voto",
				httpMethod = HttpMethod.PUT
	          )
	public DefaultBean inserisciVotoUomoPartita(InfoVotoUomoPartitaBean votoBean)
	{
		setUp();
		//Controllo che il votante esista nel Datastore
		Giocatore votante = ofy().load().type(Giocatore.class).id(votoBean.getVotante()).now();
		if( votante == null )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Votante non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il votato esista nel Datastore
		Giocatore votato = ofy().load().type(Giocatore.class).id(votoBean.getVotato()).now();
		if( votato == null )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Votato non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il votato esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(votoBean.getLinkVotoPerPartita()).now();
		if( partita == null )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che non esista già un voto da parte dello stesso votante per la stessa partita
		Set<Long> idVoti = partita.getLinkPerPartita();
		Iterator<Long> it = idVoti.iterator();
		while(it.hasNext())
		{
			VotoUomoPartita v = ofy().load().type(VotoUomoPartita.class).id(it.next()).now();
			try {
				log.log(Level.WARNING, "v.getVotante()="+v.getVotanteUP()+", votante.getEmail()="+votante.getEmail());
				if( v.getVotanteUP().equals(votante.getEmail()) )
				{
					DefaultBean response = new DefaultBean();
					response.setResult("Il votante ha già votato per questa partita!");
					tearDown();
					return response;
				}
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
		
		DefaultBean response = new DefaultBean();
		response.setResult("Voto inserito con successo!");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "voto.eliminaVotoUomoPartita",
			path = "voto/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean eliminaVotoUomoPartita(@Named("emailVotante")String emailVotante,
											  @Named("idVoto")Long idVoto)
	{
		setUp();
		//Controllo se il voto esiste nel Datastore
		VotoUomoPartita voto = ofy().load().type(VotoUomoPartita.class).id(idVoto).now();
		if( voto == null )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Voto non esistente!");
			tearDown();
			return response;
		}
		//Controllo se il votante esiste nel Datastore
		Giocatore votante= ofy().load().type(Giocatore.class).id(emailVotante).now();
		if( votante == null )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Votante non esistente!");
			tearDown();
			return response;
		}
		
		try {
			//Rimozione idVoto da partita.idVoti
			Partita partita = ofy().load().type(Partita.class).id(voto.getLinkVotoPerPartita()).now();
			partita.eliminaLinkVotoPerPartita(idVoto);
			ofy().save().entity(partita).now();
			//Rimozione voto dal Datastore
			ofy().delete().type(VotoUomoPartita.class).id(idVoto).now();
			
			DefaultBean response = new DefaultBean();
			response.setResult("Voto rimosso con successo!");
			tearDown();
			return response;
		} catch (EccezioneMolteplicitaMinima e) {
			DefaultBean response = new DefaultBean();
			response.setResult("Il voto "+idVoto+"non ha memorizzato l'id della partita!");
			tearDown();
			return response;
		}
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
