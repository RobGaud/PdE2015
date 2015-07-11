package com.modello;

import java.util.*;
import com.googlecode.objectify.annotation.*;


@Entity
public class Gruppo
{	
	@Id private Long id;

	@Index private String nome;
	private Date dataCreazione;
	private LinkedList<Long> giocatoriIscritti;
	private Long eGestito;
	private HashSet<Long> partiteOrganizzate;
	private HashSet<Long> campiPreferiti;
	@Index private String citta;
	
	public static final int MIN_LINK_ISCRITTO = 1;
	public static final int MIN_MAX_LINK_ISCRITTO = 1;
	
	protected Gruppo(){
		this.dataCreazione = new Date();
		this.giocatoriIscritti = new LinkedList<Long>();
		this.partiteOrganizzate = new HashSet<Long>();
		this.campiPreferiti = new HashSet<Long>();
	}
	
	public Gruppo(String n, String c)
	{
		this.nome = n;
		this.citta = c;
		this.dataCreazione = new Date();
		this.giocatoriIscritti = new LinkedList<Long>();
		this.partiteOrganizzate = new HashSet<Long>();
		this.campiPreferiti = new HashSet<Long>();
	}
	
	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome()
	{
		return this.nome;
	}
	
	public void setNome(String newName)
	{
		this.nome = newName;
	}
	
	// TODO getData come stringa?
	public Date getDataCreazione()
	{
		return this.dataCreazione;
	}
	
	public void setDataCreazione(Date dataCreazione) {
		if(dataCreazione != null)
			this.dataCreazione = dataCreazione;
	}

	public String toString()
	{
		return this.nome + " " + this.dataCreazione.toString();
	}
	
	public boolean equals(Object o)
	{
		if(o == null || !o.getClass().equals(this.getClass()))
			return false;
		
		Gruppo g = (Gruppo)o;
		return g.nome.equals(this.nome) && g.dataCreazione.equals(this.dataCreazione);
	}
	
	public int hashCode()
	{
		return this.nome.length() + this.dataCreazione.hashCode();
	}
	
	
	// ASSOCIAZIONE iscritto
	public int quantiIscritti()
	{
		return this.giocatoriIscritti.size();
	}
	
	public List<Long> getGiocatoriIscritti() throws EccezioneMolteplicitaMinima
	{
		if(this.quantiIscritti() < MIN_LINK_ISCRITTO)
			throw new EccezioneMolteplicitaMinima("Cardinalita minima violata!");
		else
			return (LinkedList<Long>)this.giocatoriIscritti.clone();
	}
	
	public void inserisciLinkIscritto(Long l)
	{
		if(l != null) this.giocatoriIscritti.add(l);
	}

	public void eliminaLinkIscritto(Long l)
	{
		if(l != null && giocatoriIscritti.contains(l)) this.giocatoriIscritti.remove(l);
	}
	
	// ASSOCIAZIONE gestisce
	
	public int quantiGestito() {
		if(eGestito != null)
			return 1;
		return 0;
	}
	
	public Long getLinkGestito() throws EccezioneMolteplicitaMinima, EccezioneSubset {
		if(quantiGestito() != MIN_MAX_LINK_ISCRITTO)
			throw new EccezioneMolteplicitaMinima("Violato vincolo di molteplicità min/max");
		
		if(!giocatoriIscritti.contains(eGestito))
			throw new EccezioneSubset("Violato vincolo di subset!");
	
		return eGestito;
	}

	public void inserisciLinkGestito(Long eGestito) {
		if(eGestito != null) this.eGestito = eGestito;
	}
	
	public void eliminaLinkGestito(Long eGestito) {
		if(eGestito != null && eGestito.equals(this.eGestito)) this.eGestito = null;
	}
	
	// ASSOCIAZIONE organizza
	public void inserisciLinkOrganizza(Long l)
	{
		if(l != null) this.partiteOrganizzate.add(l);

	}
	
	public void eliminaLinkOrganizza(Long l)
	{
		if(l != null) this.partiteOrganizzate.remove(l);

	}
	
	public Set<Long> getPartiteOrganizzate()
	{
		return (HashSet<Long>)this.partiteOrganizzate.clone();
	}

	// ASSOCIAZIONE CONOSCE
	public void inserisciCampo(Long c)
	{
		if(c != null) this.campiPreferiti.add(c);
	}
	
	public void eliminaCampo(Long c)
	{
		if(c != null && campiPreferiti.contains(c)) this.campiPreferiti.remove(c);
	}
	
	public Set<Long> getCampiPreferiti()
	{
		return (HashSet<Long>)this.campiPreferiti.clone();
	}
}