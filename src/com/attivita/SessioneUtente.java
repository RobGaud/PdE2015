package com.attivita;

import java.util.LinkedList;

//import java.util.HashSet;
import com.googlecode.objectify.annotation.*;

@Entity
public class SessioneUtente {
	
	public static enum StatoSessione	{LOGIN_E_REGISTRAZIONE, /*LOGIN,*/ REGISTRAZIONE,
										 PRINCIPALE, GRUPPO, PROFILO, MODIFICA_PROFILO,
										 RICERCA_GRUPPO, INVITO, ISCRITTI_GRUPPO, STORICO,
										 CREA_PARTITA, PARTITA, 
										 CREA_VOTO, RICERCA_CAMPO, DISPONIBILE_PER_PARTITA,
										 CAMPO, CREA_CAMPO, CREA_GRUPPO, EXIT, ESCI_GRUPPO,
										 ANNULLA_PARTITA, ELENCO_VOTI, MODIFICA_GRUPPO,
										 MODIFICA_PARTITA
										};
	
	@Id private Long id;
	private String emailUtente;
	private LinkedList<StatoSessione> pilaStati; // primo elemento = cima stack;
	
	public SessioneUtente(){
		this.pilaStati = new LinkedList<StatoSessione>();
		//this.pilaStati.add(0, StatoSessione.EXIT);
		this.pilaStati.add(0, StatoSessione.LOGIN_E_REGISTRAZIONE);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StatoSessione getStatoCorrente() {
		return this.pilaStati.get(0);
	}

	public void push(StatoSessione statoCorrente) {
		this.pilaStati.add(0, statoCorrente);
	}

	public void tornaIndietro()
	{
		if( this.getStatoCorrente() != StatoSessione.PRINCIPALE ) this.pop();
	}
	
	private void pop()
	{
		this.pilaStati.removeFirst();
	}
	
	public StatoSessione getStatoPrecedente() {
		return this.pilaStati.get(1);
	}

	public void setEmailUtente(String emailUtente) {
		this.emailUtente = emailUtente;
	}

	public String getEmailUtente() {
		return emailUtente;
	}
/*	public StatoSessione getStatoSessioneCorrente() {
		return statoCorrente;
	}
*/
	public void aggiornaStato(StatoSessione nuovoStato)
	{
		switch(nuovoStato)
		{
			case PRINCIPALE:
				if(this.getStatoCorrente() == StatoSessione.REGISTRAZIONE)
				{
					this.pop();
					this.pop();
					this.push(nuovoStato);
				}
				else if(this.getStatoCorrente() == StatoSessione.LOGIN_E_REGISTRAZIONE )
				{	
					this.pop();
					this.push(nuovoStato);
				}
				break;
			case PROFILO:
				if(this.getStatoCorrente() == StatoSessione.MODIFICA_PROFILO ||
				   this.getStatoCorrente() == StatoSessione.INVITO)
				{
					this.pop();
				}
				else this.push(nuovoStato);
				break;
			case GRUPPO:
				if(this.getStatoCorrente() == StatoSessione.MODIFICA_GRUPPO ||
				   this.getStatoCorrente() == StatoSessione.ANNULLA_PARTITA)
				{
					this.pop();
				}
				else if(this.getStatoCorrente() == StatoSessione.CREA_GRUPPO)
				{
					this.pop();
					this.push(nuovoStato);
				}
				else this.push(nuovoStato);
				break;
			case PARTITA:
				if(this.getStatoCorrente() == StatoSessione.MODIFICA_PARTITA ||
				   this.getStatoCorrente() == StatoSessione.CREA_VOTO ||
				   this.getStatoCorrente() == StatoSessione.DISPONIBILE_PER_PARTITA)
				{
					this.pop();
				}
				else if(this.getStatoCorrente() == StatoSessione.CREA_PARTITA)
				{
					this.pop();
					this.push(nuovoStato);
				}
				else if(this.getStatoCorrente() == StatoSessione.CAMPO)
				{
					this.pop();
					this.pop();
				}
				else this.push(nuovoStato);
				break;
			case CAMPO:
				if(this.getStatoCorrente() == StatoSessione.CREA_CAMPO)
				{
					this.pop();
					this.push(nuovoStato);
				}
				else this.push(nuovoStato);
				break;
				
			default: this.push(nuovoStato);
		}
		
	/*	switch(nuovoStato)
		{
			//Se dalla schermata principale l'utente preme il tasto "Indietro"
			//Si esce dall'applicazione, rimanendo loggati.
			case PRINCIPALE:
				this.statoPrecedente = StatoSessione.EXIT;
				break;
			//Se la transizione è crea_gruppo -> gruppo, o annulla_partita->crea_gruppo,
			//devo cambiare lo stato precedente.	
			case GRUPPO:
				if(this.statoCorrente == StatoSessione.CREA_GRUPPO
				   || this.statoCorrente == StatoSessione.ANNULLA_PARTITA
				   || this.statoCorrente == StatoSessione.MODIFICA_GRUPPO)
				   this.statoPrecedente = StatoSessione.PRINCIPALE;
				else if( this.statoCorrente == StatoSessione.RICERCA_GRUPPO )
				{
					this.statoPrecedente = StatoSessione.RICERCA_GRUPPO;
					this.statoPrePrecedente = StatoSessione.RICERCA_GRUPPO;
				}
				else
					this.statoPrecedente = this.statoCorrente;
				break;
			case PROFILO:
				if(this.statoCorrente == StatoSessione.MODIFICA_PROFILO ||
				   this.statoCorrente == StatoSessione.PRINCIPALE)
					this.statoPrecedente = StatoSessione.PRINCIPALE;
				else if(this.statoCorrente == StatoSessione.ISCRITTI_GRUPPO) {
					this.statoPrecedente = StatoSessione.ISCRITTI_GRUPPO;
					this.statoPrePrecedente = StatoSessione.ISCRITTI_GRUPPO;
				}
				else
					this.statoPrecedente = this.statoCorrente;
				break;
			case PARTITA:
				if(	this.statoCorrente == StatoSessione.CREA_PARTITA 
				   || this.statoCorrente == StatoSessione.MODIFICA_PARTITA
				   || this.statoCorrente == StatoSessione.PARTITE_PROPOSTE)
					this.statoPrecedente = StatoSessione.GRUPPO;
				else if(this.statoCorrente == StatoSessione.STORICO) {
					this.statoPrecedente = StatoSessione.STORICO;
					this.statoPrePrecedente = StatoSessione.STORICO;
				}
				else
					this.statoPrecedente = this.statoCorrente;
				break;
			case CAMPO:
				if(this.statoCorrente == StatoSessione.CREA_CAMPO)
					this.statoPrecedente = StatoSessione.RICERCA_CAMPO;
				else
					this.statoPrecedente = this.statoCorrente;
				break;
			case LOGIN_E_REGISTRAZIONE:
				this.statoPrecedente = StatoSessione.EXIT;
				break;
			default:
				this.statoPrecedente = this.statoCorrente;
		}
		this.statoCorrente = nuovoStato;
	*/
	}
	
/*	public StatoSessione getStatoSessionePrecedente() {
		return statoPrecedente;
	}
/*	
	public HashSet<StatoSessione> getStatiSuccessivi(PayloadBean p)
	{
		HashSet<StatoSessione> statiSuccessivi = new HashSet<StatoSessione>();
		
		switch( this.statoCorrente )
		{
			case LOGIN_E_REGISTRAZIONE:
				statiSuccessivi.add(StatoSessione.LOGIN);
				statiSuccessivi.add(StatoSessione.REGISTRAZIONE);
				break;
			case LOGIN:
				statiSuccessivi.add(StatoSessione.LOGIN);
				statiSuccessivi.add(StatoSessione.PRINCIPALE);
				break;
			case REGISTRAZIONE:
				statiSuccessivi.add(StatoSessione.REGISTRAZIONE);
				statiSuccessivi.add(StatoSessione.PRINCIPALE);
				break;
			case PRINCIPALE:
				statiSuccessivi.add(StatoSessione.PROFILO);
				statiSuccessivi.add(StatoSessione.RICERCA_GRUPPO);
				statiSuccessivi.add(StatoSessione.GRUPPO);
				statiSuccessivi.add(StatoSessione.CREA_GRUPPO);
				break;
			case GRUPPO:
				//TODO controllo appartenenza gruppo: perché non farlo nelle API?
				statiSuccessivi.add(StatoSessione.ISCRITTI_GRUPPO);
				statiSuccessivi.add(StatoSessione.INVITO);
				statiSuccessivi.add(StatoSessione.STORICO);
				statiSuccessivi.add(StatoSessione.CREA_PARTITA);
				statiSuccessivi.add(StatoSessione.PARTITE_PROPOSTE);
				statiSuccessivi.add(StatoSessione.PARTITE_PROPOSTE);
				break;
			case PROFILO:
				statiSuccessivi.add(StatoSessione.MODIFICA_PROFILO);
				statiSuccessivi.add(StatoSessione.LOGIN_E_REGISTRAZIONE);
				break;
			case MODIFICA_PROFILO:
				statiSuccessivi.add(StatoSessione.PROFILO);
				statiSuccessivi.add(StatoSessione.MODIFICA_PROFILO);
				break;
			case RICERCA_GRUPPO:
				statiSuccessivi.add(StatoSessione.GRUPPO);
				statiSuccessivi.add(StatoSessione.RICERCA_GRUPPO);
				break;
			case INVITO:
				statiSuccessivi.add(StatoSessione.GRUPPO);
				//TODO statiSuccessivi.add(StatoSessione.PROFILO);
				break;
			case ISCRITTI_GRUPPO:
				statiSuccessivi.add(StatoSessione.GRUPPO);
				//TODO controllo mail
				statiSuccessivi.add(StatoSessione.PROFILO);
				break;
			case STORICO:
				statiSuccessivi.add(StatoSessione.PARTITA);
				//TODO controllo stato partita
				break;
			case PARTITE_PROPOSTE:
				statiSuccessivi.add(StatoSessione.PARTITA);
				break;
			case CREA_PARTITA:
				//TODO gestire stato precedente
				statiSuccessivi.add(StatoSessione.PARTITA);
				statiSuccessivi.add(StatoSessione.RICERCA_CAMPO);
				statiSuccessivi.add(StatoSessione.CREA_PARTITA);
				break;
			case PARTITA:
				//TODO Controllo stato partita
				statiSuccessivi.add(StatoSessione.RICERCA_CAMPO);
				statiSuccessivi.add(StatoSessione.DISPONIBILE_PER_PARTITA);
				statiSuccessivi.add(StatoSessione.VOTO);
				break;
			case VOTO:
				statiSuccessivi.add(StatoSessione.PARTITA);
				break;
			case RICERCA_CAMPO:
				statiSuccessivi.add(StatoSessione.CAMPO);
				statiSuccessivi.add(StatoSessione.CREA_CAMPO);
				statiSuccessivi.add(this.getStatoSessionePrecedente());
				break;
			case DISPONIBILE_PER_PARTITA:
				statiSuccessivi.add(StatoSessione.PARTITA);
				break;
			case CAMPO:
				break;
			case CREA_CAMPO:
				//TODO gestire stato precedente
				statiSuccessivi.add(StatoSessione.CAMPO);
				statiSuccessivi.add(StatoSessione.CREA_CAMPO);
				break;
			case CREA_GRUPPO:
				statiSuccessivi.add(StatoSessione.CREA_GRUPPO);
				statiSuccessivi.add(StatoSessione.GRUPPO);
				break;
			case ESCI_GRUPPO:
				statiSuccessivi.add(StatoSessione.PRINCIPALE);
				break;
		}
		return statiSuccessivi;
	}
*/	
	
	
}
