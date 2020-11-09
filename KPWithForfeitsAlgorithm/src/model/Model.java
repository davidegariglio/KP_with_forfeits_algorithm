package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comparators.ComparatorAVGPenalty;

public class Model {

	private int nOggetti;
	private int capacity;
	private int nForfeits;
	private Map<Integer, Oggetto> itemMap;
	private long start;
	private long end;
	
	private List<Oggetto> itemSortedByAVGPenalty;
	
	private List<Oggetto> initialCandidates;

	private Solution opt;
	
	public Model() {
		this.itemMap = new HashMap<>();
	}
	
	public void execute(String fileName) {
		readInput(fileName);
		//Verifica lettura oggetti
		/*
		for(Integer id : this.itemMap.keySet()) {
			System.out.println("X"+id+" prof:"+itemMap.get(id).getProf()+" weight:"+itemMap.get(id).getPeso());
		}
		*/
		this.sortItemsByAVGPenalty();
		/*Controllo ordinamento
		for(Oggetto item : this.itemSortedByAVGPenalty) {
			System.out.println("X"+item.getId()+" "+item.getForfMedio());
		}
		*/
		
		//Controllo superfluo in molti casi. Seleziono sono gli item che possono stare nello zaino!
		this.initialCandidates = new ArrayList<> (cleanCandidates(this.itemSortedByAVGPenalty, this.capacity));
		
		Solution current = buildInitialSolution(this.capacity, this.initialCandidates);
		this.start = System.currentTimeMillis();
		this.end = start + (5*60*1000);
		//Init of current solution as starting one
		this.opt = current;
		// Exploit all computational time
		int i = 0;
		
		// TODO: Mohamed, prepare yourself!!
		while(System.currentTimeMillis() < end) {
			boolean found = false;
			List<Oggetto> leaving = current.getworstItems();
			/*
			if(leaving.isEmpty()) {
				this.opt = new Solution(current);
				System.out.println(opt.getObjFunction()+"\n"+opt.getItemSet());
				return;
			}
			*/
			Solution candidateSolution = new Solution(current);
			candidateSolution.removeItem(leaving.get(i));
			System.out.println(current+"\n"+current.getObjFunction());
			System.out.println("Leaving "+leaving.get(i)+ leaving.get(i).getNetProf());
			
			List<Oggetto> candidates = cleanCandidates(this.initialCandidates, candidateSolution.getResidualCapacity());
			candidates.removeAll(leaving);
			
			//Da aggiustare
			for(Oggetto o : candidates) {
				candidateSolution.addItem(o);

				if(candidateSolution.getObjFunction() > current.getObjFunction()) {
					current = new Solution(candidateSolution);
					System.out.println("Entering "+o);
					System.out.println("New obj. = "+candidateSolution.getObjFunction());
					System.out.println(candidateSolution);
					System.out.println(candidateSolution.getUsedCapacity());
					found = true;
					break;
				}
				else {
					candidateSolution.removeItem(o);
					System.out.println("Candidate not improving obj...");
				}
				
			}
			//finito il for, devo aggiornare i per provare a rimuovere il secondo (o successivi)peggior item
			if(!found && i < leaving.size()) i++;

		}
	}

	private void sortItemsByAVGPenalty() {
		this.itemSortedByAVGPenalty = new ArrayList<>(itemMap.values());
		for(Oggetto item : this.itemSortedByAVGPenalty) {
			item.calcolaForfeitMedio();
		}
		Collections.sort(itemSortedByAVGPenalty, new ComparatorAVGPenalty());
	}
	
	/**
	 * 
	 * @param initialCandidates
	 * @param capacity2
	 */
	private List<Oggetto> cleanCandidates(List<Oggetto> sortedItems, int capacity) {
		List<Oggetto> result = new ArrayList<>();
		for(Oggetto o : sortedItems) {
			if(o.getPeso() <= capacity)
				result.add(o);
		}
		return result;
	}


	private Solution buildInitialSolution(Integer capacity, List<Oggetto> candidates) {
		Solution initial = new Solution(capacity);
		for(Oggetto item : candidates) {
			if(item.getPeso() <= initial.getResidualCapacity()) {
				initial.addItem(item);
			}
		}
		return initial;
	}
	private void readInput(String fileName) {
		
		try {
			
			FileReader f = new FileReader (fileName);
			BufferedReader br = new BufferedReader (f);
			
			System.out.println("Reading data from " + fileName);
			
			String row;
			
			
			Integer nRow = 0;
			Integer nObj = 1;

			while((row = br.readLine()) != null) {
				
				String array[] = row.split(";");
				
				//Prima riga = nOggetti
				if(nRow == 0) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input numero oggetti nel formato errato!");
						return;
					}
					this.nOggetti = Integer.parseInt(array[0]);
				}
				
				//Seconda riga = max capacità
				else if(nRow == 1) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input capcità nel formato errato!");
						return;
					}
					this.capacity= Integer.parseInt(array[0]);
				}
				
				//Terza riga = nForfeits
				else if(nRow == 2) {
					if(array.length != 1) {
						System.out.println("ATTENZIONE! Input numero forfeits nel formato errato!");
						return;
					}
					this.nForfeits = Integer.parseInt(array[0]);
				}
				
				//Acquisizione profitti e pesi degli oggetti
				else if(nRow > 2 && nRow <= 2 + this.nOggetti) {
					if(array.length != 2) {
						System.out.println("ATTENZIONE! Input profitti e pesi oggetti nel formato errato!");
						return;
					}
					Oggetto nuovo = new Oggetto(nObj++, Integer.parseInt(array[0]), Integer.parseInt(array[1]));
					this.itemMap.put(nuovo.getId(), nuovo);
				}
				
				//Se nessuno dei casi precedenti, sono nel "settore" dei forfeits
				else {
					if(array.length != 3 && array[0].compareTo("")!=0) {
						System.out.println(nRow+" ATTENZIONE! Input forfeits nel formato errato!");
						for(int i = 0; i < array.length; i++) {
							
							System.out.println("Cella numero: "+i+":"+array[i]);
						}
						return;
						}
					if(array[0].compareTo("")!=0) {
						Oggetto primo = this.itemMap.get(Integer.parseInt(array[0]));
						Oggetto secondo = this.itemMap.get(Integer.parseInt(array[1]));
						
						//Controllo che il forfeit no sia già stato inserito
						if(!primo.containsForfeit(secondo) && !secondo.containsForfeit(primo)) {
							primo.addConflict(secondo, Integer.parseInt(array[2]));
							secondo.addConflict(primo, Integer.parseInt(array[2]));
						}
						else {
							this.nForfeits--;
						}
					}
				}
				
				nRow++;
			}
			br.close();
			System.out.println("Read completed.");
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
