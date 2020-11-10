package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Oggetto {

	private Integer id;
	private Integer prof;
	private Integer peso;
	private Map<Integer, Integer> forfeits;
	private Double pOverW;
	private Double forfMedio;
	private Double netProf;	//una volta inserito nella soluzione corrisponde al: suo prof - SUM(penalità con lui) 
	
	public Oggetto(Integer id, Integer prof, Integer peso) {
		this.id = id;
		this.prof = prof;
		this.peso = peso;
		this.forfeits = new HashMap<>();
		this.pOverW = this.prof / Double.valueOf(this.peso);
		this.forfMedio = 0.0;
		this.netProf = 0.0;
	}

	public Integer getId() {
		return id;
	}

	public Integer getProf() {
		return prof;
	}

	public Integer getPeso() {
		return peso;
	}

	public Map<Integer, Integer> getForfeits() {
		return forfeits;
	}
	
	public Double getpOverW() {
		return pOverW;
	}

	public Double getForfMedio() {
		return forfMedio;
	}

	public Double getNetProf() {
		return netProf;
	}

	public void setNetProf(Double netProf) {
		this.netProf = netProf;
	}

	public void addConflict(Oggetto other, Integer penalita) {
		this.forfeits.put(other.id, penalita);
	}
	
	public void calcolaForfeitMedio() {
		Double result = 0.0;
		for(Integer p : this.forfeits.values()) {
			result += p;
		}
		this.forfMedio = result / Double.valueOf(this.forfeits.size());
	}

	@Override
	public String toString() {
		String result =  "X"+this.id+" p="+this.prof+" w="+this.peso;
		return result;
	}
	
	public boolean containsForfeit(Oggetto other) {
		return this.forfeits.containsKey(other.getId());
	}
	
	public void ordinaForfeit() {
	//	Collections.sort(this.forfeits, new ComparatoreForfeits());
	}

	public Double getPenalitaCon(Oggetto o) {
		if(this.forfeits.get(o.id)!=null) {
			return Double.valueOf(this.forfeits.get(o.id));
		}
		return 0.0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Oggetto other = (Oggetto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
}
