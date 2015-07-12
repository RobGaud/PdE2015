package com.attivita;

import java.util.HashSet;

import com.bean.PayloadBean;
import com.googlecode.objectify.annotation.*;
import com.modello.Partita.Stato;

@Entity
public class SessioneUtente {
	
	public static enum StatoSessione	{LOGIN_E_REGISTRAZIONE, LOGIN, REGISTRAZIONE,
										 PRINCIPALE, GRUPPO, PROFILO, MODIFICA_PROFILO,
										 RICERCA_GRUPPO, INVITO, ISCRITTI_GRUPPO, STORICO,
										 PARTITE_PROPOSTE, CREA_PARTITA, PARTITA, 
										 VOTO, RICERCA_CAMPO, DISPONIBILE_PER_PARTITA,
										 CAMPO, CREA_CAMPO, CREA_GRUPPO, EXIT
										};
	
	@Id private Long id;
	private String emailUtente;
	private StatoSessione statoCorrente;
	private StatoSessione statoPrecedente;
	//private HashSet<StatoSessione> listaStati;
	
	public SessioneUtente(){
		this.statoCorrente = StatoSessione.LOGIN_E_REGISTRAZIONE;
		this.statoPrecedente = null;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StatoSessione getStatoCorrente() {
		return statoCorrente;
	}

	public void setStatoCorrente(StatoSessione statoCorrente) {
		this.statoCorrente = statoCorrente;
	}

	public StatoSessione getStatoPrecedente() {
		return statoPrecedente;
	}

	public void setStatoPrecedente(StatoSessione statoPrecedente) {
		this.statoPrecedente = statoPrecedente;
	}

	public void setEmailUtente(String emailUtente) {
		this.emailUtente = emailUtente;
	}

	public String getEmailUtente() {
		return emailUtente;
	}
	public StatoSessione getStatoSessioneCorrente() {
		return statoCorrente;
	}

	public void aggiornaStato(StatoSessione nuovoStato)
	{
		switch(nuovoStato)
		{
			//Se dalla schermata principale l'utente preme il tasto "Indietro"
			//Si esce dall'applicazione, rimanendo loggati.
			case PRINCIPALE:
				this.statoPrecedente = StatoSessione.EXIT;
				break;
			//Se la transizione è crea_gruppo -> gruppo, devo cambiare lo stato precedente.	
			case GRUPPO:
				if(this.statoCorrente == StatoSessione.CREA_GRUPPO)
					this.statoPrecedente = StatoSessione.PRINCIPALE;
				else
					this.statoPrecedente = this.statoCorrente;
				break;
			case PARTITA:
				if(this.statoCorrente == StatoSessione.CREA_PARTITA)
					this.statoPrecedente = StatoSessione.GRUPPO;
				else
					this.statoPrecedente = this.statoCorrente;
				break;
			case CAMPO:
				if(this.statoCorrente == StatoSessione.CREA_CAMPO)
					this.statoPrecedente = StatoSessione.RICERCA_CAMPO;
				else
					this.statoPrecedente = this.statoCorrente;
				break;
			default:
				this.statoPrecedente = this.statoCorrente;
		}
		this.statoCorrente = nuovoStato;
	}
	
	public StatoSessione getStatoSessionePrecedente() {
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
				//TODO controllo appartenenza gruppo
				statiSuccessivi.add(StatoSessione.ISCRITTI_GRUPPO);
				statiSuccessivi.add(StatoSessione.INVITO);
				statiSuccessivi.add(StatoSessione.STORICO);
				statiSuccessivi.add(StatoSessione.CREA_PARTITA);
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
		}
		return statiSuccessivi;
	}
*/	
	
	
}
