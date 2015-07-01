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
	//private HashSet<TipoLinkGioca> haGiocato;
	//private HashSet<TipoLinkIscritto> eIscritto;
	private HashSet<Long> linkDestinatario;
	
	private Giocatore()
	{
		this.linkDestinatario = new HashSet<Long>();
		this.elencoDisponibile = new HashSet<Long>();
	}
	
	public Giocatore(String nome, String email, String telefono,
			String ruoloPreferito, String fotoProfilo) {
		this.nome = nome;
		this.email = email;
		this.telefono = telefono;
		this.ruoloPreferito = ruoloPreferito;
		this.fotoProfilo = fotoProfilo;
		//this.haGiocato = new HashSet<TipoLinkGioca>();
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
/*	
	// ASSOCIAZIONE GIOCA

	public void inserisciLinkGioca(TipoLinkGioca l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerGioca.inserisci(l);
	}

	public void eliminaLinkGioca(TipoLinkGioca l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerGioca.elimina(l);;
	}

	public Set<TipoLinkGioca> getHaGiocato() throws EccezioneSubset{
		
		Set<TipoLinkDisponibile> s = this.eDisponibile;
		Iterator<TipoLinkGioca> i = this.haGiocato.iterator();
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
		}
		return (HashSet<TipoLinkGioca>)haGiocato.clone();
	}
	
	public void inserisciPerManagerGioca(ManagerGioca m) {
		if(m!=null)
			this.haGiocato.add(m.getLink());
		
	}

	public void eliminaPerManagerGioca(ManagerGioca m) {
		if(m!=null)
			this.haGiocato.remove(m.getLink());
		
	}
	
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
