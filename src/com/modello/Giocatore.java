package com.modello;

import java.util.*;
import com.googlecode.objectify.annotation.*;

@Entity
public class Giocatore {
	
	@Id private String email;
	@Index private String nome;
	private String telefono;
	private String ruoloPreferito;
	private String fotoProfilo;
	
	private HashSet<Long> elencoDisponibile; 
	/*TODO: l'associazione Gioca che senso ha dal punto di vista del Giocatore?
	 *		sono le partite che ha giocato da sempre, o solo quelle che deve giocare?
	 *		Se sono le partite giocate da sempre, allora dobbiamo tenere traccia, 
	 *		per sempre, anche del fatto che i giocatori erano disponibili per quelle partite,
	 *		per garantire il vincolo di subset. 
	 *		Secondo me conviene fare che sono le partite da giocare, e poi eliminiamo i link 
	 *		di disponibilità e di giocata dopo la fine della partita.
	 */
	private HashSet<Long> partiteDaGiocare;
	//private HashSet<TipoLinkIscritto> eIscritto;
	private HashSet<Long> linkDestinatario;
	
	private Giocatore()
	{
		this.linkDestinatario = new HashSet<Long>();
		this.elencoDisponibile = new HashSet<Long>();
		this.partiteDaGiocare = new HashSet<Long>();
	}
	
	public Giocatore(String nome, String email, String telefono,
			String ruoloPreferito, String fotoProfilo) {
		this.nome = nome;
		this.email = email;
		this.telefono = telefono;
		this.ruoloPreferito = ruoloPreferito;
		this.fotoProfilo = fotoProfilo;
		this.partiteDaGiocare = new HashSet<Long>();
		//this.eIscritto = new HashSet<TipoLinkIscritto>();
		this.linkDestinatario = new HashSet<Long>();
		this.elencoDisponibile = new HashSet<Long>();

	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getRuoloPreferito() {
		return ruoloPreferito;
	}

	public void setRuoloPreferito(String ruoloPreferito) {
		this.ruoloPreferito = ruoloPreferito;
	}

	public String getFotoProfilo() {
		return fotoProfilo;
	}

	public void setFotoProfilo(String fotoProfilo) {
		this.fotoProfilo = fotoProfilo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		if(nome != null)
			this.nome = nome;
	}

	public String toString() {
		return this.nome + " " + this.email + " " + this.telefono + " " +
	           this.ruoloPreferito + " " + this.fotoProfilo;
	}
	
	
	// ASSOCIAZIONE DISPONIBILE
	
	public void inserisciLinkDisponibile(Long link)
	{
		if( link != null ) this.elencoDisponibile.add(link);
	}
	
	public void eliminaLinkDisponibile(Long link)
	{
		if(link != null ) this.elencoDisponibile.remove(link);
	}
	
	public Set<Long> getElencoDisponibile() {
		return (HashSet<Long>)this.elencoDisponibile.clone();
	}
	
	// ASSOCIAZIONE GIOCA

	public void inserisciLinkGioca(Long idPartita) {
		if(idPartita != null ) this.partiteDaGiocare.add(idPartita);
	}

	public void eliminaLinkGioca(Long idPartita) {
		if(idPartita != null ) this.partiteDaGiocare.remove(idPartita);
	}

	public Set<Long> getLinkGioca() throws EccezioneSubset
	{
		//Nota: controllo subset spostato sulle API
		return (HashSet<Long>)this.partiteDaGiocare.clone();
	}
/*	
	// ASSOCIAZIONE ISCRITTO

	public void inserisciLinkIscritto(TipoLinkIscritto l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerIscritto.inserisci(l);
	}

	public void eliminaLinkIscritto(TipoLinkIscritto l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerIscritto.elimina(l);;
	}

	public Set<TipoLinkIscritto> getEIscritto() {
		return (HashSet<TipoLinkIscritto>)eIscritto.clone();
	}
	
	public void inserisciPerManagerIscritto(ManagerIscritto m) {
		if(m!=null)
			this.eIscritto.add(m.getLink());
		
	}

	public void eliminaPerManagerIscritto(ManagerIscritto m) {
		if(m!=null)
			this.eIscritto.remove(m.getLink());
		
	}
*/
	// ASSOCIAZIONE DESTINATARIO

	public void inserisciLinkDestinatario(Long idLink)
	{
		if(idLink != null) this.linkDestinatario.add(idLink);
	}

	public void eliminaLinkDestinatario(Long idLink)
	{
		if(idLink != null) this.linkDestinatario.remove(idLink);
	}

	public Set<Long> getLinkDestinatario() {
		return (HashSet<Long>)linkDestinatario.clone();
	}
}
