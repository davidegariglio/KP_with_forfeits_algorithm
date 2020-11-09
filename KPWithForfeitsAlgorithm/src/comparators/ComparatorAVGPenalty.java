package comparators;

import java.util.Comparator;

import model.Oggetto;

public class ComparatorAVGPenalty implements Comparator<Oggetto> {

	@Override
	public int compare(Oggetto o1, Oggetto o2) {
		return o1.getForfMedio().compareTo(o2.getForfMedio());
	}

}
