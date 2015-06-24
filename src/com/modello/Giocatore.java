package com.modello;

import java.util.*;

import com.googlecode.objectify.annotation.*;

@Entity
public class Giocatore {
	
	@Id public Long id;
	
	@Index private String nome;
	@Index private String email;
	private String telefono;
	private String ruoloPreferito;
	private String fotoProfilo;
	//private HashSet<TipoLinkDisponibile> eDisponibile;
	//private HashSet<TipoLinkGioca> haGiocato;
	//private HashSet<TipoLinkIscritto> eIscritto;
	//private HashSet<TipoLinkDestinatario> eDestinatario;
	
	private Giocatore(){}
	
	/**
	 * 
	 * @param nome
	 * @param email
	 * @param telefono
	 * @param ruoloPreferito
	 * @param fotoProfilo
	 */
	public Giocatore(String nome, String email, String telefono,
			String ruoloPreferito, String fotoProfilo) {
		this.nome = nome;
		this.email = email;
		this.telefono = telefono;
		this.ruoloPreferito = ruoloPreferito;
		this.fotoProfilo = fotoProfilo;
		//this.eDisponibile = new HashSet<TipoLinkDisponibile>();
		//this.haGiocato = new HashSet<TipoLinkGioca>();
		//this.eIscritto = new HashSet<TipoLinkIscritto>();
		//this.eDestinatario = new HashSet<TipoLinkDestinatario>();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @return
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * 
	 * @param telefono
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * 
	 * @return
	 */
	public String getRuoloPreferito() {
		return ruoloPreferito;
	}

	/**
	 * 
	 * @param ruoloPreferito
	 */
	public void setRuoloPreferito(String ruoloPreferito) {
		this.ruoloPreferito = ruoloPreferito;
	}

	/**
	 * 
	 * @return
	 */
	public String getFotoProfilo() {
		return fotoProfilo;
	}

	/**
	 * 
	 * @param fotoProfilo
	 */
	public void setFotoProfilo(String fotoProfilo) {
		this.fotoProfilo = fotoProfilo;
	}

	/**
	 * 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		if(nome != null)
			this.nome = nome;
	}

	/**
	 * 
	 */
	public String toString() {
		return this.nome + " " + this.email + " " + this.telefono + " " +
	           this.ruoloPreferito + " " + this.fotoProfilo;
	}
	
	/*
	// ASSOCIAZIONE DISPONIBILE
	
	public void inserisciLinkDisponibile(TipoLinkDisponibile l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerDisponibile.inserisci(l);
	}
	
	public void eliminaLinkDisponibile(TipoLinkDisponibile l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerDisponibile.elimina(l);;
	}
	
	public Set<TipoLinkDisponibile> getEDisponibile() {
		return (HashSet<TipoLinkDisponibile>)eDisponibile.clone();
	}

	public void inserisciPerManagerDisponibile(ManagerDisponibile m) {
		if(m!=null)
			this.eDisponibile.add(m.getLink());
	}

	public void eliminaPerManagerDisponibile(ManagerDisponibile m) {
		if(m!=null)
			this.eDisponibile.remove(m.getLink());
	}
	
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
	
	// ASSOCIAZIONE DESTINATARIO

	public void inserisciLinkDestinatario(TipoLinkDestinatario l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerDestinatario.inserisci(l);
	}

	public void eliminaLinkDestinatario(TipoLinkDestinatario l) {
		if(l != null && l.getGiocatore().equals(this))
			ManagerDestinatario.elimina(l);;
	}

	public Set<TipoLinkDestinatario> getEDestinatario() {
		return (HashSet<TipoLinkDestinatario>)eDestinatario.clone();
	}

	public void inserisciPerManagerDestinatario(ManagerDestinatario m) {
		if(m!=null)
			this.eDestinatario.add(m.getLink());
		
	}

	public void eliminaPerManagerDestinatario(ManagerDestinatario m) {
		if(m!=null)
			this.eDestinatario.remove(m.getLink());
		
	}
	*/
}
