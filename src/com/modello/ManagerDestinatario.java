package com.modello;

public final class ManagerDestinatario
{
	private TipoLinkDestinatario link;

	private ManagerDestinatario(TipoLinkDestinatario link) {
		this.link = link;
	}

	public TipoLinkDestinatario getLink() {
		return link;
	}
	
	public static void inserisci(TipoLinkDestinatario l) {
		try {
			if(l!=null && l.getInvito().getDestinatario()==null) {
				ManagerDestinatario m = new ManagerDestinatario(l);
				m.getLink().getGiocatore().inserisciPerManagerDestinatario(m);
				m.getLink().getInvito().inserisciPerManagerDestinatario(m);
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			System.out.println(e);
		}
	}
	
	public static void elimina(TipoLinkDestinatario l){
		try {
			if(l!=null && l.getInvito().getDestinatario().equals(l)) {
				ManagerDestinatario m = new ManagerDestinatario(l);
				m.getLink().getGiocatore().eliminaPerManagerDestinatario(m);
				m.getLink().getInvito().eliminaPerManagerDestinatario(m);
			}
		}
		catch(EccezioneMolteplicitaMinima e) {
			System.out.println(e);
		}
	}
}