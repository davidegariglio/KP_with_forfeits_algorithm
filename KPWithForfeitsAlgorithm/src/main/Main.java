package main;

import model.Model;

public class Main {

	//TODO: Fare ciclo for per eseguire nome1, nome2, nome3, ..., nomeN 
	public static void main(String[] args) {

		run();
		
	}

	private static void run() {
		
		Model model = new Model();
		model.execute("prova.txt");
		
	}

}
