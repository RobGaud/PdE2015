package com.modello;

import java.util.*;
import com.googlecode.objectify.annotation.*;

@Entity
public class Partita {
	
	@Id private Long id;
	
	private Date dataOraPartita;
	private float quota;	// TODO quota è un attributo indipendente o dipendente dal Campo in cui si gioca?
	
	private Long campo;
	private HashSet<Long> elencoVoti;
	private Long gruppo;
	//private Giocatore chiPropone;
	//private LinkedList<TipoLinkDisponibile> elencoDisponibili;
	//private HashSet<TipoLinkGioca> elencoGioca;
	
	public static final int MIN_MAX_LINK_PRESSO = 1;
	public static final int MIN_LINK_GIOCA = 1;
	public static final int MIN_LINK_PROPONE = 1;
	public static final int MIN_LINK_DISPONIBILE = 1;
	public static final int MIN_LINK_ORGANIZZA = 1;
	
	protected Partita(){
		this.elencoVoti = new HashSet<Long>();
	}
	
	public Partita(Date d, float q)
	{
		this.dataOraPartita = d;
		this.quota = q;
		this.elencoVoti = new HashSet<Long>();

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
	
	// ASSOCIAZIONE presso
	public void inserisciCampo(Long c) {
		if(c != null) this.campo = c;
	}
	
	public void eliminaCampo(Long c) {
		if(c != null && c.equals(this.campo)) this.campo = null;
	}
	
	public int quantiCampi()
	{
		if( this.campo == null )
			return 0;
		else
			return 1;
	}
	
	public Long getCampo() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiCampi() != MIN_MAX_LINK_PRESSO )
			throw new EccezioneMolteplicitaMinima("Cardinalita min/max violata!");
		else
			return this.campo;
	}
	
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
		
	// ASSOCIAZIONE organizza
	public void inserisciLinkOrganizza(Long l)
	{
		if( l != null ) this.gruppo = l;
	}

	public void eliminaLinkOrganizza(Long l)
	{
		if( l != null && this.gruppo.equals(l)) this.gruppo = null;
	}
	
	public int quantiOrganizza()
	{
		if( this.gruppo == null )
			return 0;
		else
			return 1;
	}
	
	public Long getLinkOrganizza() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiOrganizza() < MIN_LINK_ORGANIZZA )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return this.gruppo;
	}
	
/*
	// ASSOCIAZIONE propone
	public void inserisciPropone( Giocatore g )
	{
		if( g != null ) this.chiPropone = g;
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
	
	public Giocatore getPropone() throws EccezioneMolteplicitaMinima, EccezioneSubset
	{
		if( this.quantiPropone() < MIN_LINK_PROPONE )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		
		List<TipoLinkDisponibile> disp = this.elencoDisponibili;
		try
		{
			TipoLinkDisponibile t = new TipoLinkDisponibile(this.chiPropone, this, 0);
			if( !disp.contains(t) )
				throw new EccezioneSubset("Vincolo di subset violato!");
			else
				return this.chiPropone;
		}
		catch (EccezionePrecondizioni e)
		{
				e.printStackTrace();
		}	
		//TODO vediamola sta cosa
		return null;
	}
	
	// ASSOCIAZIONE disponibile
	public void inserisciLinkDisponibile(TipoLinkDisponibile t)
	{
		if( t != null && t.getPartita().equals(this) )
			ManagerDisponibile.inserisci(t);
	}
	
	public void rimuoviLinkDisponibile(TipoLinkDisponibile t)
	{
		if( t != null && t.getPartita().equals(this) )
			ManagerDisponibile.elimina(t);
	}
	
	public void inserisciPerManagerDisponibile( ManagerDisponibile m )
	{
		if( m != null ) this.elencoDisponibili.add(m.getLink());
	}
	
	public void eliminaPerManagerDisponibile( ManagerDisponibile m )
	{
		if( m != null ) this.elencoDisponibili.remove(m.getLink());
	}
	
	public int quantiDisponibili()
	{
		return this.elencoDisponibili.size();
	}
	
	public List<TipoLinkDisponibile> getLinkDisponibile() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiDisponibili() < MIN_LINK_DISPONIBILE )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return (LinkedList<TipoLinkDisponibile>)this.elencoDisponibili.clone();
	}
	
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
