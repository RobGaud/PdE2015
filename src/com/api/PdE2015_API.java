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
		
		// TODO tutte le mail in minuscolo (altrimenti rischio problemi identificazione)
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
			GruppoAperto ga = new GruppoAperto(gruppoBean.getNome());
			ofy().save().entity(ga).now();
		}
		else {
			GruppoChiuso gc = new GruppoChiuso(gruppoBean.getNome());
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
	
	
	//TODO API Partita
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
				PartitaCalcetto pc_1 = new PartitaCalcetto(partitaBean.getDataOraPartita());
				ofy().save().entity(pc_1).now();
				break;
			case CODICE_CALCIOTTO:
				PartitaCalciotto pc_2 = new PartitaCalciotto(partitaBean.getDataOraPartita());
				ofy().save().entity(pc_2).now();
				break;
			case CODICE_CALCIO:
				PartitaCalcio pc_3 = new PartitaCalcio(partitaBean.getDataOraPartita());
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

		ofy().save().entity(partita).now();
		tearDown();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Partita aggiornata con successo!");
		return response;
	}
	
	@ApiMethod(
				name = "partita.getNDisponibili",
				path = "partita/ndisponibili",
				httpMethod = HttpMethod.GET
	          )
	public NDisponibiliBean getNDisponibili(@Named("Id Partita")Long idPartita)
	{
		setUp();
		Partita partita = ofy().load().type(Partita.class).id(idPartita).now();
		if(partita == null) {
			NDisponibiliBean response = new NDisponibiliBean();
			response.setReport("Partita non esistente!");
			response.setnDisponibili(-1);
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
			response.setReport("200 OK");
			response.setnDisponibili(nDisponibili);
			tearDown();
			return response;
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Nessun giocatore ha dato disponibilità per la partita"+partita.getId()+"!");
			NDisponibiliBean response = new NDisponibiliBean();
			response.setReport("Nessun giocatore ha dato disponibilità per la partita!");
			response.setnDisponibili(-1);
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
		//Controllo che il destinatario non faccia già parte del gruppo;
		//devo quindi controllare che non esista un tipoLinkIscritto
		//con questo gruppo e questo giocatore
		List<TipoLinkIscritto> list = ofy().load().type(TipoLinkIscritto.class)
									  .filter("gruppo", gruppo.getId())
									  .filter("giocatore", destinatario.getEmail()).list();
		if( list.size()>0)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Il destinatario fa già parte del gruppo!");
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
	
	//TODO API associazione Disponibile
	@ApiMethod(
				name = "disponibile.inserisciDisponibile",
				path = "disponibile",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean inserisciDisponibile(InfoGestionePartiteBean gestioneBean)
	{
		//TODO controllo se il giocatore appartiene al gruppo?
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore non abbia già dato disponibilità per questa partita
		try {
			TipoLinkDisponibile link = new TipoLinkDisponibile(gestioneBean.getEmailGiocatore(),
										   gestioneBean.getIdPartita(), gestioneBean.getnAmici());
			List<TipoLinkDisponibile> linkList = ofy().load().type(TipoLinkDisponibile.class)
												 .filter("giocatore", gestioneBean.getEmailGiocatore())
												 .filter("partita", gestioneBean.getIdPartita()).list();
			if(linkList.contains(link))
			{
				DefaultBean response = new DefaultBean();
				response.setResult("Il giocatore ha già dato la sua disponibilità per questa partita!");
				tearDown();
				return response;
			}
			//Carico il link sul Datastore
			ofy().save().entity(link).now();
			
			//Aggiorno giocatore e partita
			giocatore.inserisciLinkDisponibile(link.getId());
			partita.inserisciLinkDisponibile(link.getId());
		
		} catch (EccezionePrecondizioni e) {
			DefaultBean response = new DefaultBean();
			response.setResult("Almeno uno dei campi di interesse è null!");
			tearDown();
			return response;
		}
		//Li carico nel datastore
		ofy().save().entity(giocatore).now();
		ofy().save().entity(partita).now();
	
		DefaultBean response = new DefaultBean();
		response.setResult("Disponibilità inserita con successo.");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "disponibile.eliminaDisponibile",
			path = "disponibile/delete",
			httpMethod = HttpMethod.POST
			)
	public DefaultBean eliminaDisponibile(InfoGestionePartiteBean gestioneBean)
	{
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore abbia dato disponibilità per la partita
		List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
										   .filter("giocatore", gestioneBean.getEmailGiocatore())
										   .filter("partita", gestioneBean.getIdPartita()).list();
		if( listLink.size() == 0 )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Il giocatore non ha dato la sua disponibilità per la partita!");
			tearDown();
			return response;
		}
		TipoLinkDisponibile link = listLink.get(0);
		//Elimino il link dal Datastore
		ofy().delete().entity(link).now();
		//Aggiorno giocatore e partita
		giocatore.eliminaLinkDisponibile(link.getId());
		partita.eliminaLinkDisponibile(link.getId());
		//Li carico nel datastore
		ofy().save().entity(giocatore);
		ofy().save().entity(partita);
		
		DefaultBean response = new DefaultBean();
		response.setResult("Disponibilità rimossa non successo.");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "disponibile.modificaDisponibile",
			path = "disponibile",
			httpMethod = HttpMethod.PUT
			)
	public DefaultBean modificaDisponibile(InfoGestionePartiteBean gestioneBean)
	{
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore abbia dato disponibilità per la partita
		List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
											   .filter("giocatore", gestioneBean.getEmailGiocatore())
											   .filter("partita", gestioneBean.getIdPartita()).list();
		if( listLink.size() == 0 )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Il giocatore non ha dato la sua disponibilità per la partita!");
			tearDown();
			return response;
		}
		//Aggiorno il link, e lo ricarico sul Datastore --- E FONDAMENTALE CHE NON CAMBI IL SUO ID
		TipoLinkDisponibile link = listLink.get(0);
		link.setnAmici(gestioneBean.getnAmici());
		ofy().save().entity(link).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Link disponibile aggiornato con successo!");
		tearDown();
		return response;
	}
	
	//TODO API associazione Propone
	@ApiMethod(
				name = "propone.inserisciPropone",
				path = "propone",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean inserisciPropone(InfoGestionePartiteBean gestioneBean) throws EccezionePrecondizioni
	{
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//TODO: vediamola bene sta cosa
		//Controllo che la partita non abbia già un giocatore che l'ha proposta
		if( partita.quantiPropone() == 1 )
		{
			DefaultBean response = new DefaultBean();
			response.setResult("La partita ha già memorizzato un giocatore che l'ha proposta!");
			tearDown();
			return response;
		}
		
		//Controllo che il giocatore figuri tra i disponibili per la partita
		try{
			List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
												   .filter("giocatore", gestioneBean.getEmailGiocatore())
												   .filter("partita", gestioneBean.getIdPartita()).list();
			if( listLink.size() == 0 )
			{
				DefaultBean response = new DefaultBean();
				response.setResult("Il giocatore non ha dato la sua disponibilità per la partita!");
				tearDown();
				return response;
			}
			TipoLinkDisponibile link = listLink.get(0);
			if( !partita.getLinkDisponibile().contains(link.getId()) )
			{
				DefaultBean response = new DefaultBean();
				response.setResult("Chi propone una partita deve figurare automaticamente tra i disponibili!");
				tearDown();
				return response;
			}
		}catch(EccezioneMolteplicitaMinima e)
		{
			log.log(Level.SEVERE, "La partita "+partita.getId()+
					" non ha nessun giocatore disponibile!");
			DefaultBean response = new DefaultBean();
			response.setResult("La partita deve avere almeno un giocatore disponibile!");
			tearDown();
			return response;
		}
		//Aggiorno partita e la ricarico sul Datastore
		partita.inserisciPropone(gestioneBean.getEmailGiocatore());
		ofy().save().entity(partita).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Link propone inserito con successo!");
		tearDown();
		return response;
	}
	
	//Ha senso eliminare chi ha proposto una partita? Forse giusto se il giocatore organizza
	//e poi disdice, e quindi toglie la disponibilità, e allora gli togliamo anche il ruolo
	//di "proponitore"... boh, io l'ho fatta, poi se non serve la togliamo...
	@ApiMethod(
			name = "propone.eliminaPropone",
			path = "propone/delete",
			httpMethod = HttpMethod.POST
			)
	public DefaultBean eliminaPropone(InfoGestionePartiteBean gestioneBean)
	{
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore sia effettivamente chi ha proposto la partita
		try {
			if( !partita.getPropone().equals(gestioneBean.getEmailGiocatore()) )
			{
				DefaultBean response = new DefaultBean();
				response.setResult("Il giocatore non è colui che ha proposto la partita!");
				tearDown();
				return response;
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+partita.getId()+" non ha un link propone!");
		}
		//Aggiorno la partita e la ricarico sul Datastore
		partita.eliminaPropone();
		ofy().save().entity(partita).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("Link propone rimosso con successo.");
		tearDown();
		return response;
	}
	
	//TODO API associazione Gioca
	@ApiMethod(
				name = "gioca.inserisciLinkGioca",
				path = "gioca",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean inserisciLinkGioca(InfoGestionePartiteBean gestioneBean)
	{
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore figuri tra i disponibili per la partita
		try{
			List<TipoLinkDisponibile> listLink = ofy().load().type(TipoLinkDisponibile.class)
												   .filter("giocatore", gestioneBean.getEmailGiocatore())
												   .filter("partita", gestioneBean.getIdPartita()).list();
			if( listLink.size() == 0 )
			{
				DefaultBean response = new DefaultBean();
				response.setResult("Il giocatore non ha dato la sua disponibilità per la partita!");
				tearDown();
				return response;
			}
			TipoLinkDisponibile link = listLink.get(0);
			if( !partita.getLinkDisponibile().contains(link.getId()) )
			{
				DefaultBean response = new DefaultBean();
				response.setResult("Chi propone una partita deve figurare automaticamente tra i disponibili!");
				tearDown();
				return response;
			}
		}catch(EccezioneMolteplicitaMinima e)
		{
			log.log(Level.SEVERE, "La partita "+partita.getId()+
					" non ha nessun giocatore disponibile!");
			DefaultBean response = new DefaultBean();
			response.setResult("La partita deve avere almeno un giocatore disponibile!");
			tearDown();
			return response;
		}
		//Controllo non esistenza link gioca - Non posso fare questo controllo:
		//Al primo inserimento di un giocatore come linkGioca, partita.getLinkGioca()
		//Lancerà sempre una EccezioneMolteplicitaMinima; dobbiamo spostare il test su Partita
		
		//Inserimento link gioca - aggiornamento partita e giocatore
		partita.inserisciLinkGioca(giocatore.getEmail());
		giocatore.inserisciLinkGioca(partita.getId());
		ofy().save().entity(partita).now();
		ofy().save().entity(giocatore).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkGioca inserito con successo.");
		tearDown();
		return response;
	}
	
	@ApiMethod(
				name = "gioca.eliminaLinkGioca",
				path = "gioca/delete",
				httpMethod = HttpMethod.POST
				)
	public DefaultBean eliminaLinkGioca(InfoGestionePartiteBean gestioneBean)
	{
		setUp();
		//Controllo che la partita esista nel Datastore
		Partita partita = ofy().load().type(Partita.class).id(gestioneBean.getIdPartita()).now();
		if(partita==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore esista nel Datastore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestioneBean.getEmailGiocatore()).now();
		if(giocatore==null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo che il giocatore giochi effettivamente la partita
		try {
			if( !partita.getLinkGioca().contains(gestioneBean.getEmailGiocatore()) )
			{
				DefaultBean response = new DefaultBean();
				response.setResult("Il giocatore non gioca la partita!");
				tearDown();
				return response;
			}
		} catch (EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "La partita "+partita.getId()+" non ha nessun LinkGioca!");
		}
		//Aggiorno partita e giocatore e li ricarico sul Datastore
		partita.eliminaLinkGioca(giocatore.getEmail());
		giocatore.eliminaLinkGioca(partita.getId());
		ofy().save().entity(partita).now();
		ofy().save().entity(giocatore).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkGioca rimosso con successo.");
		tearDown();
		return response;
	}
	
	// TODO API Iscritto
	@ApiMethod(
			name = "iscritto.inserisciLinkIscritto",
			path = "iscritto",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkIscritto(InfoIscrittoGestisceBean iscrittoBean) {
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(iscrittoBean.getGiocatore()).now();
		if( giocatore == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Destinatario non esistente!");
			tearDown();
			return response;
		}
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(iscrittoBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo iscrizione unica
		try {
			Iterator<Long> it = gruppo.getGiocatoriIscritti().iterator();
			
			while(it.hasNext()) {
				Long idLink = it.next();
				TipoLinkIscritto link = ofy().load().type(TipoLinkIscritto.class).id(idLink).now();
				if(link.getGiocatore().equals(iscrittoBean.getGiocatore())) {
					DefaultBean response = new DefaultBean();
					response.setResult("Giocatore già iscritto!");
					tearDown();
					return response;
				}
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			log.log(Level.SEVERE, "Il Gruppo "+ gruppo.getId() +
					"non rispetta la molteplicità minima!");
		}
		
		// Creazione TipoLinkIscritto ed salvataggio/aggiornamento
		try {
			TipoLinkIscritto link = new TipoLinkIscritto(iscrittoBean.getGiocatore(), iscrittoBean.getGruppo());
			ofy().save().entity(link).now();
			giocatore.inserisciLinkIscritto(link.getId());
			ofy().save().entity(giocatore).now();
			gruppo.inserisciLinkIscritto(link.getId());
			ofy().save().entity(gruppo).now();
		}
		catch(EccezionePrecondizioni e) {
			log.log(Level.SEVERE, "Almeno uno tra iscrittoBean.getGruppo() e iscrittoBean.getGIocatore() ha restituito null");
			DefaultBean response = new DefaultBean();
			response.setResult("Errore imprevisto!");
			tearDown();
			return response;
		}
		
		DefaultBean response = new DefaultBean();
		response.setResult("TipoLinkIscritto inserito con successo!");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "iscritto.rimuoviLinkIscritto",
			path = "iscritto/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean rimuoviLinkIscritto(InfoIscrittoGestisceBean iscrittoBean) {
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(iscrittoBean.getGiocatore()).now();
		if( giocatore == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(iscrittoBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
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
		
		if(!found) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non iscritto!");
			tearDown();
			return response;
		}
		
		// Rimozione TipoLinkIscritto e salvataggio/aggiornamento
		gruppo.rimuoviLinkIscritto(link.getId());
		ofy().save().entity(gruppo).now();
		giocatore.eliminaLinkIscritto(link.getId());
		ofy().save().entity(giocatore).now();
		ofy().delete().entity(link).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("TipoLinkRimosso con successo!");
		tearDown();
		return response;	
	}
	
	// TODO API Gestito
	@ApiMethod(
			name = "gestito.inserisciLinkGestito",
			path = "gestito",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkGestito(InfoIscrittoGestisceBean gestisceBean) {
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestisceBean.getGiocatore()).now();
		if( giocatore == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gestisceBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
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
					"non rispetta la molteplicità minima!");
		}
		
		if(!found) {
			DefaultBean response = new DefaultBean();
			response.setResult("Il gestore del gruppo deve farne parte!");
			tearDown();
			return response;
		}
		
		// Controllo Unicità del gestore
		if(gruppo.quantiGestito() == 1) {
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo già gestito da un giocatore!");
			tearDown();
			return response;
		}
		
		// Creazione LinkGestisce
		gruppo.inserisciLinkGestito(link.getId());
		ofy().save().entity(gruppo).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkGestisce inserito con successo!");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "gestito.rimuoviLinkGestito",
			path = "gestito/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean rimuoviLinkGestito(InfoIscrittoGestisceBean gestisceBean) {
		setUp();
		//Controllo esistenza giocatore
		Giocatore giocatore = ofy().load().type(Giocatore.class).id(gestisceBean.getGiocatore()).now();
		if( giocatore == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non esistente!");
			tearDown();
			return response;
		}
		//Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(gestisceBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
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
					"non rispetta la molteplicità minima!");
		}
		
		if(!found) {
			DefaultBean response = new DefaultBean();
			response.setResult("Giocatore non iscritto!");
			tearDown();
			return response;
		}
		
		// Rimozione LinkGestisce
		gruppo.rimuoviLinkGestito(link.getId());
		ofy().save().entity(gruppo).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkGestisce rimosso con successo!");
		tearDown();
		return response;
		
	}
	// TODO API Conosce
	@ApiMethod(
			name = "conosce.inserisciLinkConosce",
			path = "conosce",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkConosce(InfoConosceBean conosceBean) {
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(conosceBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(conosceBean.getCampo()).now();
		if( campo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Campo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo campi duplicati
		/*
		Iterator<Long> it = gruppo.getCampiPreferiti().iterator();
		
		while(it.hasNext()) {
			Long idCampo = it.next();
			if(idCampo.equals(conosceBean.getCampo())) {
				DefaultBean response = new DefaultBean();
				response.setResult("Campo già presente!");
				tearDown();
				return response;
			}
		}
		*/
		if(gruppo.getCampiPreferiti().contains(conosceBean.getCampo())) {
			DefaultBean response = new DefaultBean();
			response.setResult("Campo già presente!");
			tearDown();
			return response;
		}
		
		// Inserimento linkConosce e salvataggio/aggiornamento
		gruppo.inserisciCampo(conosceBean.getCampo());
		ofy().save().entity(gruppo).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkConosce inserito con successo!");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "conosce.rimuoviLinkConosce",
			path = "conosce/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean rimuoviLinkConosce(InfoConosceBean conosceBean) {
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(conosceBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(conosceBean.getCampo()).now();
		if( campo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Campo non esistente!");
			tearDown();
			return response;
		}
		
		// Rimozione LinkConosce
		gruppo.rimuoviCampo(conosceBean.getCampo());
		ofy().save().entity(gruppo).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkConosce rimosso con successo!");
		tearDown();
		return response;
		
	}
	
	// TODO API Organizza
	@ApiMethod(
			name = "organizza.inserisciLinkOrganizza",
			path = "organizza",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkOrganizza(InfoOrganizzaBean organizzaBean) {
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(organizzaBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(organizzaBean.getPartita()).now();
		if( partita == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo link duplicati
		// TODO Rivedere bene gli equals!!
		if(gruppo.getPartiteOrganizzate().contains(organizzaBean.getPartita())) {
			DefaultBean response = new DefaultBean();
			response.setResult("Partita già organizzata!");
			tearDown();
			return response;
		}
		
		// inserimento LinkOrganizza e salvataggio/aggiornamento
		partita.inserisciLinkOrganizza(organizzaBean.getGruppo());
		ofy().save().entity(partita).now();
		gruppo.inserisciLinkOrganizza(organizzaBean.getPartita());
		ofy().save().entity(gruppo).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkOrganizza inserito con successo!");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "organizza.rimuoviLinkOrganizza",
			path = "organizza/delete",
			httpMethod = HttpMethod.POST
          )
	public 	DefaultBean rimuoviLinkOrganizza(InfoOrganizzaBean organizzaBean) {
		setUp();
		// Controllo esistenza gruppo
		Gruppo gruppo = ofy().load().type(Gruppo.class).id(organizzaBean.getGruppo()).now();
		if( gruppo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Gruppo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(organizzaBean.getPartita()).now();
		if( partita == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		
		// rimozione LinkOrganizza e salvataggio/aggiornamento
		partita.eliminaLinkOrganizza(organizzaBean.getGruppo());
		ofy().save().entity(partita).now();
		gruppo.rimuoviLinkOrganizza(organizzaBean.getPartita());
		ofy().save().entity(gruppo).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkOrganizza rimosso con successo!");
		tearDown();
		return response;
	}
	
	// TODO API Presso
	@ApiMethod(
			name = "presso.inserisciLinkPresso",
			path = "presso",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean inserisciLinkPresso(InfoPressoBean pressoBean) {
		setUp();
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(pressoBean.getCampo()).now();
		if( campo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Campo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(pressoBean.getPartita()).now();
		if( partita == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo unicità Campo
		// TODO vedere bene per la possibilità di cambiare campo
		if(partita.quantiCampi() == 1) {
			DefaultBean response = new DefaultBean();
			response.setResult("Campo già impostato!");
			tearDown();
			return response;
		}
		
		// inserimento LinkPresso e salvataggio/aggiornamento
		partita.inserisciCampo(pressoBean.getCampo());
		partita.setQuota(campo.getPrezzo()/partita.getNPartecipanti());
		ofy().save().entity(partita).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkPresso inserito con successo!");
		tearDown();
		return response;
	}
	
	@ApiMethod(
			name = "presso.rimuoviLinkPresso",
			path = "presso/delete",
			httpMethod = HttpMethod.POST
          )
	public DefaultBean rimuoviLinkPresso(InfoPressoBean pressoBean) {
		setUp();
		// Controllo esistenza Campo
		Campo campo = ofy().load().type(Campo.class).id(pressoBean.getCampo()).now();
		if( campo == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Campo non esistente!");
			tearDown();
			return response;
		}
		
		// Controllo esistenza partita
		Partita partita = ofy().load().type(Partita.class).id(pressoBean.getPartita()).now();
		if( partita == null)
		{
			DefaultBean response = new DefaultBean();
			response.setResult("Partita non esistente!");
			tearDown();
			return response;
		}
		
		// rimozione LinkPresso e salvataggio/aggiornamento
		partita.eliminaCampo(pressoBean.getCampo());
		ofy().save().entity(partita).now();
		
		DefaultBean response = new DefaultBean();
		response.setResult("LinkPresso rimosso con successo!");
		tearDown();
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
