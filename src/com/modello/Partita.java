package com.modello;

import java.util.*;
import com.googlecode.objectify.annotation.*;



@Entity
public abstract class Partita {
	
	public static enum Stato {PROPOSTA, CONFERMATA, GIOCATA};
	
	@Id private Long id;
	
	private Date dataOraPartita;
	private float quota;
	
	private Long campo;
	private HashSet<Long> elencoVoti; 			//Id dei TipoLinkVotoPerPartita
	@Index private Long gruppo;					//Id del gruppo
	private String chiPropone;					//Email del proponitore
	private LinkedList<Long> elencoDisponibili;	//Id dei TipoLinkDisponibile
	private HashSet<String> elencoGioca;		//Email di chi gioca
	
	@Index private Stato statoCorrente;
	
	public static final int MIN_MAX_LINK_PRESSO = 1;
	public static final int MIN_LINK_GIOCA = 0;
	public static final int MIN_LINK_PROPONE = 1;
	public static final int MIN_LINK_DISPONIBILE = 1;
	public static final int MIN_LINK_ORGANIZZA = 1;
	public static final float SENTINELLA = -1.0f;
	
	protected Partita(){
		this.elencoVoti = new HashSet<Long>();
		this.elencoDisponibili = new LinkedList<Long>();
		this.elencoGioca = new HashSet<String>();
		this.statoCorrente = Stato.PROPOSTA;
	}
	
	public Partita(Date d)
	{
		this.dataOraPartita = d;
		this.quota = SENTINELLA;
		this.elencoVoti = new HashSet<Long>();
		this.elencoDisponibili = new LinkedList<Long>();
		this.elencoGioca = new HashSet<String>();
		this.statoCorrente = Stato.PROPOSTA;

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

	//OVERRIDE in sottoclassi
	public float getQuota()
	{
		return this.quota;
	}

	public void setQuota(float quota) {
		if(quota >= 0.0f)
			this.quota = quota;
	}
	
	public abstract int getNPartecipanti();

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
	
	public String getPropone() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiPropone() < MIN_LINK_PROPONE )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		
		return this.chiPropone;
	}
	
	// ASSOCIAZIONE disponibile
	public void inserisciLinkDisponibile(Long link)
	{
		if( link != null && !this.elencoDisponibili.contains(link) ) this.elencoDisponibili.add(link);
	}
	
	public void eliminaLinkDisponibile(Long link)
	{
		if( link != null ) this.elencoDisponibili.remove(link);
	}
	
	public int quantiDisponibili()
	{
		return this.elencoDisponibili.size();
	}
	
	public List<Long> getLinkDisponibile() throws EccezioneMolteplicitaMinima
	{
		if( this.quantiDisponibili() < MIN_LINK_DISPONIBILE )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		else
			return (LinkedList<Long>)this.elencoDisponibili.clone();
	}
	
	// ASSOCIAZIONE gioca
	public void inserisciLinkGioca(String giocatore)
	{
		if( giocatore!=null && !this.elencoGioca.contains(giocatore) ) this.elencoGioca.add(giocatore);
	}
	
	public void eliminaLinkGioca(String giocatore)
	{
		if( giocatore != null ) this.elencoGioca.remove(giocatore);
	}
	
	public int quantiGioca()
	{
		return this.elencoGioca.size();
	}
	
	public Set<String> getLinkGioca() throws EccezioneMolteplicitaMinima /* EccezioneSubset */
	{
		if( this.quantiGioca() < MIN_LINK_GIOCA )
			throw new EccezioneMolteplicitaMinima("Cardinalità minima violata!");
		
		//Controllo subset spostato sulle API
		return (HashSet<String>)this.elencoGioca.clone();
	}

	public Stato getStatoCorrente() {
		return statoCorrente;
	}

	public void setStatoCorrente(Stato statoCorrente) {
		this.statoCorrente = statoCorrente;
	}
}
