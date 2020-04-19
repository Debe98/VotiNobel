package it.polito.tdp.nobel.model;

import it.polito.tdp.nobel.db.*;
import java.util.*;

public class Model {
	EsameDAO dao = new EsameDAO();
	PopulateDB newDB = new PopulateDB();
	Set <Esame> migliori = new HashSet<Esame>();
	Set <Esame> esami = new HashSet<Esame>();

	//IO SONO STUPIDO E NON VADO USATO!
	public Set<Esame> calcolaSottoinsiemeEsami(int numeroCrediti) {
		migliori.clear();
		esami.clear();
		List <Esame> tuttiEsami = new ArrayList<Esame> (dao.getTuttiEsami());
		
		trovaCombinazioni(numeroCrediti, tuttiEsami);
		
		return migliori;
	}
	
	//IO SONO STUPIDO E NON VADO USATO!
	private void trovaCombinazioni (int crediti, List <Esame> tutti) {
		int creditiAttuali = sommaCrediti(esami);
		if (creditiAttuali > crediti) {
			return;
		}
		else if (creditiAttuali == crediti) {
			if (migliori.isEmpty() || mediaVoti(esami) > mediaVoti(migliori)) {
				migliori = new HashSet<Esame> (esami);
			}
			return;
		}
		else {
			for (Esame e : tutti) {
				if (!esami.contains(e)) {
					esami.add(e);
					trovaCombinazioni(crediti, tutti);
					esami.remove(e);
				}
			}
		}
		
	}
	
	//IO SONO INTELLIGENTE E VADO USATO!
	public Set<Esame> calcolaSottoinsiemeEsamiIntelligente(int numeroCrediti) {
		migliori.clear();
		esami.clear();
		List <Esame> tuttiEsami = new ArrayList<Esame> (dao.getTuttiEsami());
		
		Map<Integer, List <Esame>> esamiOrdinatiXCrediti = new HashMap<Integer, List<Esame>>();
		
		for (Esame e : tuttiEsami) {
			if (esamiOrdinatiXCrediti.containsKey(e.getCrediti())) {
				esamiOrdinatiXCrediti.get(e.getCrediti()).add(e);
			}
			else {
				esamiOrdinatiXCrediti.put(e.getCrediti(), new LinkedList<Esame>());
				esamiOrdinatiXCrediti.get(e.getCrediti()).add(e);
			}
		}
		
		
		for (Integer crediti : esamiOrdinatiXCrediti.keySet()) {
			esamiOrdinatiXCrediti.get(crediti).sort(new Comparator<Esame>() {
				@Override
				public int compare(Esame e1, Esame e2) {
					return -(e1.getVoto()-e2.getVoto());
				}
			});
		}
		
		trovaCombinazioniIntelligente(numeroCrediti, esamiOrdinatiXCrediti, 0);
		
		return migliori;
	}
	
	//IO SONO INTELLIGENTE E VADO USATO!
	private void trovaCombinazioniIntelligente (int crediti, Map <Integer, List <Esame>> esamiDivisi, int maxCredito) {
		int creditiAttuali = sommaCrediti(esami);
		if (creditiAttuali > crediti) {
			return;
		}
		else if (creditiAttuali == crediti) {
			if (migliori.isEmpty() || mediaVoti(esami) > mediaVoti(migliori)) {
				migliori = new HashSet<Esame> (esami);
			}
			return;
		}
		else {
			for (Integer cr : esamiDivisi.keySet()) {
				
				if (cr < maxCredito || esamiDivisi.get(cr).isEmpty())
					continue;
				Esame esameMigliore = esamiDivisi.get(cr).get(0);
				esami.add(esameMigliore);
				esamiDivisi.get(cr).remove(0);
				trovaCombinazioniIntelligente(crediti, esamiDivisi, cr);
				esami.remove(esameMigliore);
				esamiDivisi.get(cr).add(0, esameMigliore);
			}
		}
	}

	private int sommaCrediti (Collection <Esame> c) {
		int somma = 0;
		for (Esame e : c) {
			somma += e.getCrediti();
		}
		return somma;
	}
	
	public double mediaVoti (Collection <Esame> c) {
		int somma = 0;
		for (Esame e : c) {
			somma += e.getVoto()*e.getCrediti();
		}
		return (double)somma/sommaCrediti(c);
	}

}
