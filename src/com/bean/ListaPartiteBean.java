package com.bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Level;

import com.modello.Partita;
import com.modello.PartitaCalcetto;
import com.modello.PartitaCalcio;
import com.modello.PartitaCalciotto;

public class ListaPartiteBean {

	private LinkedList<PartitaBean> listaPartite;
	private String result;
	private String httpCode;
	
	public ListaPartiteBean() {
		this.listaPartite = new LinkedList<PartitaBean>();
	}
	
	public void addPartita(Partita partita)
	{
		if(partita != null){
			PartitaBean partitaBean = new PartitaBean();
			partitaBean.setPartita(partita);

			String dataString = partita.getDataOra().toString().substring(4);
			
			String giorno, mese, anno, ora, minuti;
			
			giorno = dataString.substring(4, 6);
			mese = dataString.substring(0, 3);
			anno = dataString.substring(20);
			ora = dataString.substring(7, 9);
			minuti = dataString.substring(10, 12);
			
			dataString = ora+":"+minuti+" "+giorno+" "+mese+" "+anno;
			partitaBean.setDataString(dataString);
			
			if( partita.getClass().equals(PartitaCalcetto.class) ) partitaBean.setTipo(1);
			else if( partita.getClass().equals(PartitaCalciotto.class) ) partitaBean.setTipo(2);
			else if( partita.getClass().equals(PartitaCalcio.class)) partitaBean.setTipo(3);
			
			this.listaPartite.add(partitaBean);
		}
	}
	
	public void removePartita(Partita Partita)
	{
		this.listaPartite.remove(Partita);
	}
	

	public List<PartitaBean> getListaPartite()
	{
		return (LinkedList<PartitaBean>)this.listaPartite.clone();
	}
	
	public void setListaPartite(LinkedList<Partita> listaPartite) {
		Iterator<Partita> it = listaPartite.iterator();
		while(it.hasNext()){
			Partita p = it.next();
			this.addPartita(p);
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}

}
