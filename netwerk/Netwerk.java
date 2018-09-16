package netwerk;

import java.util.ArrayList;

public class Netwerk {
	
	public ArrayList<ArrayList<Neuron>> neuronen;
	public boolean bias;
	
	public double leergraad = 0.2;

	// Maakt een nieuw neuraal netwerk met de gegeven layout en een mogelijke bias neuron voor elke laag
	// De layout bestaat minimaal uit één ingangslaag, één tussenlaag en één uitgangslaag
	// De layout kan oneindig veel tussenlagen bevatten
	// Voorbeeld:
	// new Netwerk(new int[] { 3, 1, 4, 1, 5 }, false);
	// Uitleg:
	// Deze code initialiseert een nieuw netwerk met een ingangslaag bestaande uit 3 neuronen,
	// 3 tussenlagen bestaand uit 1, 4 en 1 neuronen en een uitgangslaag bestaande uit 5 neuronen
	// Dit netwerk bevat geen bias neuronen
	public Netwerk(int layout[], boolean bias) {
		this.bias = bias;
		
		neuronen = new ArrayList<>();
		
		// Itereert door de layout heen
		for (int i = 0; i < layout.length; i++) {
			
			// Maakt een nieuwe laag aan voor elke laag uit de layout
			ArrayList<Neuron> laag = new ArrayList<>();
			
			// Voegt het totaal aantal neuronen uit de layout aan de huidige laag toe
			for (int j = 0; j < layout[i] + (bias ? 1 : 0); j++)
				
				// Maakt een nieuw neuron met een index en het aantal neuronen van de volgende laag,
				// de uitgangslaag heeft geen synapsen, omdat er geen neuronen na de uitgangslaag komen
				laag.add(new Neuron(j, i + 1 < layout.length ? layout[i + 1] : 0));
			
			// Voegt de huidige laag aan het netwerk toe
			neuronen.add(laag);
		}
	}
	
	public void propageerVoorwaarts(double ingangsWaarden[]) {
		
		// Itereert door alle ingangswaarden en tegelijkertijd ingangsneuronen heen
		for (int i = 0; i < ingangsWaarden.length; i++) {
			
			// Stelt n gelijk aan de huidige neuron
			Neuron n = neuronen.get(0).get(i);
			
			// Stelt de uitgangswaarde en de geactiveerde uitgangswaarden van de huidige ingangsneuron gelijk aan de huidige gegeven ingangswaarde
			n.uitgang = ingangsWaarden[i];
			n.geactiveerdeUitgang = ingangsWaarden[i];
		}
		
		// Itereert door de tussenlagen en de uitgangslaag heen
		for (int i = 1; i < neuronen.size(); i++)
			
			// Itereert door alle neuronen in de huidige laag heen
			for (int j = 0; j < neuronen.get(i).size() - (bias ? 1 : 0); j++)
				
				// Voert voorwaartse propagatie uit op de huidige neuron
				neuronen.get(i).get(j).propageerVoorwaarts(neuronen.get(i - 1));
	}
	
	public void propageerAchterwaarts(double verwachteUitgangen[]) {
		
		// Notitie:
		// Alle for-loops die door neuronen heen itereren, itereren van achter naar voren,
		// omdat de achterwaartse propagatie van achter naar voor wordt uitgevoerd
		
		// Itereert door alle uitgangsneuronen heen
		for (int i = 0; i < neuronen.get(neuronen.size() - 1).size() - (bias ? 1 : 0); i++) {
			
			// Stelt n gelijk aan de huidige neuron
			Neuron n = neuronen.get(neuronen.size() - 1).get(i);
			
			// Bepaalt de error van de huidige uitgangsneuron
			double error = verwachteUitgangen[i] - n.geactiveerdeUitgang;
			
			// Berekent de delta uitgang van de huidige uitgangsneuron
			n.deltaUitgang = Activatie.afgeleideActiveer(n.uitgang) * error;
			
			// Voert actherwaartse propagatie uit op de huidige uitgansneuron
			n.propageerAchterwaarts(neuronen.get(neuronen.size() - 2));
		}
		
		// Notitie:
		// De achterwaartse propagatie van uitgangsneuronen en tussenneuronen worden in andere for-loops uitegevoerd,
		// omdat ze een andere berekening voor de delta uitgang hebben
		
		// Itereert door alle tussenlagen in het netwerk heen
		for (int i = neuronen.size() - 2; i > 0; i--) {
			
			// Itereert door alle neuronenen van de tussenlagen heen, behalve de bias neuron, 
			// want deze heeft geen synapsen aan zich verbonden om achterwaartse propagatie op uit te voeren
			for (int j = 0; j < neuronen.get(i).size() - (bias ? 1 : 0); j++) {
				
				// Stelt n gelijk aan de huidige neuron
				Neuron n = neuronen.get(i).get(j);
				
				// Berekent de delta uitgang van de huidige neuron
				n.deltaUitgang = Activatie.afgeleideActiveer(n.uitgang) * n.uitgangsSom(neuronen.get(i + 1));
				
				// Voert achterwaartse propagatie uit op de huidige neuron
				n.propageerAchterwaarts(neuronen.get(i - 1));
			}
		}
		
		// Itereert door alle lagen in het netwerk heen
		for (int i = neuronen.size() - 1; i >= 0; i--) {
			
			// Itereert door alle neuronenen van de lagen in het netwerk heen, behalve de bias neuron, 
			// want deze heeft geen synapsen aan zich verbonden om de wegingen van aan te passen
			for (int j = 0; j < neuronen.get(i).size() - (bias ? 1 : 0); j++) {
				
				// Stelt n gelijk aan de huidige neuron
				Neuron n = neuronen.get(i).get(j);
				
				// Past de delta wegingen toe op de wegingen van de huidige neuronen
				n.pasWegingenToe(leergraad);
			}
		}
	}
	
	public double[] uitgangsWaarden() {
		
		// Maakt een nieuwe reeks aan voor alle uitgangswaarden van de uitgangsneuronen, behalve de bias neuron, 
		// want deze is alleen een bijeffect van de initialisatie en maakt geen onderdeel uit van het netwerk
		double waarden[] = new double[neuronen.get(neuronen.size() - 1).size() - (bias ? 1 : 0)];
		
		// Itereert door alle uitgangsneuronen heen
		for (int i = 0; i < waarden.length; i++)
			
			// Stelt de huidige waarde gelijk aan de geactiveerde uitgang van de huidige uitgangsneuron
			waarden[i] = neuronen.get(neuronen.size() - 1).get(i).geactiveerdeUitgang;
		
		return waarden;
	}
}
