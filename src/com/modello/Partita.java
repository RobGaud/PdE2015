package com.modello;

import java.util.*;
import com.googlecode.objectify.annotation.*;

@Entity
public class Partita {
	
	@Id private Long id;
	
	private Date dataOraPartita;
	private float quota;	// TODO quota è un attributo indipendente o dipendente dal Campo in cui si gioca?
	
	//private Campo campo;
	private HashSet<Long> elencoVoti;
	//private TipoLinkOrganizza gruppo;
	private String chiPropone;
	private LinkedList<String> elencoDisponibili;
	//private HashSet<TipoLinkGioca> elencoGioca;
	
	public static final int MIN_LINK_PRESSO = 1;
	public static final int MIN_LINK_GIOCA = 1;
	public static final int MIN_LINK_PROPONE = 1;
	public static final int MIN_LINK_DISPONIBILE = 1;
	public static final int MIN_LINK_ORGANIZZA = 1;
	
	protected Partita(){
		this.elencoVoti = new HashSet<Long>();
		this.elencoDisponibili = new LinkedList<String>();
	}
	
	public Partita(Date d, float q)
	{
		this.dataOraPartita = d;
		this.quota = q;
		this.elencoVoti = new HashSet<Long>();
		this.elencoDisponibili = new LinkedList<String>();

	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataOra()
	{
		return this.dataOraPartita;
	}
	
	public void setDataOraPartita(Date dataOraPartita) {
		this.dataOraPartita = dataOraPartita;
	}

	public float getQuota()
	{
		return this.quota;
		// TODO quota come operazione e non attributo?
	}

	public void setQuota(float quota) {
		if(quota >= 0.0f)
			this.quota = quota;
	}
	
	/*
	// ASSOCIAZIONE presso
	public void inserisciCampo( Campo c )
	{
		if( c != null )
			this.campo = c;
	}
	
	public void eliminaCampo()
	{
		this.campo = null;
	}
	
	public int quantiCampi()
	{
		if( this.campo == null )
			return 0;
		else
			return 1;
	}
	
	public Campo getCampo() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiCampi() < MIN_LINK_PRESSO )
			throw new EccezioneMolteplicitaMinima("Cardinalita minima violata!");
		else
			return this.campo;
	}
*/
	// ASSOCIAZIONE votoPerPartita
	public void inserisciLinkPerPartita(Long idLink)
	{
		if( idLink != null && !this.elencoVoti.contains(idLink)) this.elencoVoti.add(idLink);
	}
	
	public void eliminaLinkVotoPerPartita(Long idLink)
	{
		if( idLink != null ) this.elencoVoti.remove(idLink);
	}
	
	public Set<Long> getLinkPerPartita()
	{
		return (HashSet<Long>)this.elencoVoti.clone();
	}
	
/*	
	// ASSOCIAZIONE organizza
	public void inserisciLinkOrganizza( TipoLinkOrganizza t )
	{
		if( t != null && t.getPartita().equals(this) )
			ManagerOrganizza.inserisci(t);
	}
	
	public void eliminaLinkOrganizza( TipoLinkOrganizza t )
	{
		if( t != null && t.getPartita().equals(this) )
			ManagerOrganizza.rimuovi(t);
	}
	
	public int quantiOrganizza()
	{
		if( this.gruppo == null )
			return 0;
		else
			return 1;
	}
	
	public TipoLinkOrganizza getLinkOrganizza() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiOrganizza() < MIN_LINK_ORGANIZZA )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return this.gruppo;
	}
*/	
	// ASSOCIAZIONE propone
	public void inserisciPropone( String emailGiocatore )
	{
		if( emailGiocatore != null ) this.chiPropone = emailGiocatore;
	}
	
	public void eliminaPropone()
	{
		this.chiPropone = null;
	}
	
	public int quantiPropone()
	{
		if( this.chiPropone == null )
			return 0;
		else
			return 1;
	}
	
	public String getPropone() throws EccezioneMolteplicitaMinima, EccezioneSubset
	{
		if( this.quantiPropone() < MIN_LINK_PROPONE )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		
		//Verifico che chi ha proposto figuri tra i disponibili
		List<String> disp = this.elencoDisponibili;
		if( !disp.contains(this.chiPropone) )
			throw new EccezioneSubset("Vincolo di subset violato!");
		else
			return this.chiPropone;
	}
	
	// ASSOCIAZIONE disponibile
	public void inserisciLinkDisponibile(String emailGiocatore)
	{
		if( emailGiocatore != null ) this.elencoDisponibili.add(emailGiocatore);
	}
	
	public void eliminaLinkDisponibile(String emailGiocatore)
	{
		if( emailGiocatore != null ) this.elencoDisponibili.remove(emailGiocatore);
	}
	
	public int quantiDisponibili()
	{
		return this.elencoDisponibili.size();
	}
	
	public List<String> getLinkDisponibile() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiDisponibili() < MIN_LINK_DISPONIBILE )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return (LinkedList<String>)this.elencoDisponibili.clone();
	}
/*	
	// ASSOCIAZIONE gioca
	public void inserisciLinkGioca(TipoLinkGioca t)
	{
		if( t != null && t.getPartita().equals(t) )
			ManagerGioca.inserisci(t);
	}
	
	public void rimuoviLinkGioca(TipoLinkGioca t)
	{
		if( t != null && t.getPartita().equals(t) )
			ManagerGioca.elimina(t);
	}
	
	public void inserisciPerManagerGioca(ManagerGioca m)
	{
		if( m != null ) this.elencoGioca.add(m.getLink());
	}
	
	public void eliminaPerManagerGioca(ManagerGioca m)
	{
		if( m != null ) this.elencoGioca.remove(m.getLink());
	}
	
	public int quantiGioca()
	{
		return this.elencoGioca.size();
	}
	
	public List<TipoLinkGioca> getLinkGioca() throws EccezioneMolteplicitaMinima, EccezioneSubset
	{
		if( this.quantiGioca() < MIN_LINK_GIOCA )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		
		List<TipoLinkDisponibile> s = this.elencoDisponibili;
		Iterator<TipoLinkGioca> i = this.elencoGioca.iterator();
		while( i.hasNext() )
		{
			TipoLinkGioca tg = i.next();
			TipoLinkDisponibile t;
			try {
				t = new TipoLinkDisponibile(tg.getGiocatore(), tg.getPartita(), 0);
				if( !s.contains(t) )
					throw new EccezioneSubset("Vincolo di subset violato!");
			}
			catch (EccezionePrecondizioni e) {
					e.printStackTrace();
			}
			// TODO Auto-generated catch block
			
		}
		
		return (LinkedList<TipoLinkGioca>)this.elencoGioca.clone();
	}
	*/
}
