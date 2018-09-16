package netwerk;

import java.util.ArrayList;

public class Neuron {

	private int index;
	
	public Synaps synapsen[];
	public double error;
	public double uitgang;
	public double deltaUitgang;
	public double geactiveerdeUitgang;
	
	public Neuron(int index, int ingangsSynapsen) {
		this.index = index;
		
		// Stelt de uitgang en de geactiveerde uitgang gelijk aan 1 voor als deze neuron een bias neuron is
		uitgang = 1.0;
		geactiveerdeUitgang = 1.0;
		
		synapsen = new Synaps[ingangsSynapsen];
		
		// Itereert door alle synapsen van deze neuron heen en initialiseert ze met een willekeurige weging
		for (int i = 0; i < ingangsSynapsen; i++)
			synapsen[i] = new Synaps();
	}
	
	public void propageerVoorwaarts(ArrayList<Neuron> voorgaandeLaag) {
		
		// Stelt de uitgang gelijk aan 0, zodat de nieuwe uitgang berekent kan worden
		uitgang = 0.0;
		
		// Itereert door alle neuronen van de voorgaande laag heen
		for (int i = 0; i < voorgaandeLaag.size(); i++)
			
			// Voegt de geactiveerde uitgang van de huidige neuron uit de voorgaande laag,
			// vermenigvuldigd met de bijbehorende synaps, aan de uitgang van deze neuron toe
			uitgang += voorgaandeLaag.get(i).geactiveerdeUitgang * voorgaandeLaag.get(i).synapsen[index].weging;
		
		// Activeert de uitgangswaarde
		geactiveerdeUitgang = Activatie.activeer(uitgang);
	}
	
	public double uitgangsSom(ArrayList<Neuron> volgendeLaag) {
		double som = 0.0;
		
		// Itereert door alle neuronen van de volgende laag heen
		for (int i = 0; i < volgendeLaag.size() - 1; i++)
			
			// Voegt de delta uitgang aan de som toe
			som += volgendeLaag.get(i).deltaUitgang / synapsen[i].weging;
		
		return som;
	}
	
	public void propageerAchterwaarts(ArrayList<Neuron> voorgaandeLaag) {
		
		// Itereert door alle neuronen in de voorgande laag heen
		for (Neuron n : voorgaandeLaag)
			
			// Berekent de delta weging voor de huidige neuron van de voorgaande laag
			n.synapsen[index].deltaWeging = deltaUitgang * n.geactiveerdeUitgang;
	}
	
	public void pasWegingenToe(double leergraad) {
		
		// Itereert door alle synapsen van deze neuron heen
		for (Synaps s : synapsen)
			
			// Past de delta weging vermenigvuldigd met de leergraad op elke weging toe
			s.weging += s.deltaWeging * leergraad;
	}
}
