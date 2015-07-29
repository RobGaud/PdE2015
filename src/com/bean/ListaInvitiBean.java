package com.bean;

import java.util.LinkedList;
import java.util.List;

import com.modello.EccezioneMolteplicitaMinima;
import com.modello.Invito;

public class ListaInvitiBean
{
	LinkedList<InfoInvitoBean> listaInviti;
	String httpCode;
	
	public ListaInvitiBean()
	{
		listaInviti = new LinkedList<InfoInvitoBean>();
	}
	
	public void addInvito(Invito invito, String nomeGruppo, Long idInvito)
	{
		if(invito != null){
			InfoInvitoBean invitoBean = new InfoInvitoBean();
			try {
				invitoBean.setEmailMittente(invito.getEmailMittente());
				invitoBean.setIdGruppo(invito.getGruppo());
				invitoBean.setIdInvito(idInvito);
			} catch (EccezioneMolteplicitaMinima e){}
			
			invitoBean.setNomeGruppo(nomeGruppo);
			
			this.listaInviti.add(invitoBean);
		}
	}
	
	public void removeInvito(InfoInvitoBean Invito)
	{
		this.listaInviti.remove(Invito);
	}
	

	public List<InfoInvitoBean> getlistaInviti()
	{
		return (LinkedList<InfoInvitoBean>)this.listaInviti.clone();
	}


	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}
}
