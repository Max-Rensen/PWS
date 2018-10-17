package main;

import java.util.ArrayList;

import netwerk.Activatie;
import netwerk.Netwerk;

public class Main {

	private static double[] AEXWaarden(AEXObject o) {
		return new double[] { o.open, o.high, o.low, o.close, o.volume };
	}
	
	// Bepalen van slotkoers AEX 31 december 2018 (close)
	public static void main(String args[]) {
		Netwerk netwerk = new Netwerk(new int[] { 5, 64, 64, 5 }, true);
		Activatie.huidig = Activatie.FUNCTIE.SIGMOÏDE;
		
		ArrayList<AEXObject> objecten = AEXObject.laadAEXBestand("res/^AEX.csv");
		
		double[] uitgangsWaarden = AEXWaarden(objecten.get(0));
		int tijdperk = 0;
		int maxTijdperken = 10000;
		
		for (int i = 0; i < objecten.size() - 2; i++) {
			netwerk.propageerVoorwaarts(uitgangsWaarden);
			
			netwerk.propageerAchterwaarts(AEXWaarden(objecten.get(i + 1)));
			uitgangsWaarden = netwerk.uitgangsWaarden();
			
			if (tijdperk < maxTijdperken && i == objecten.size() - 3) {
				i = 0;
				tijdperk++;
				if (tijdperk % 100 == 0) 
					System.out.println(tijdperk);
			}
		}
		
		uitgangsWaarden = AEXWaarden(objecten.get(objecten.size() - 2));
		AEXObject vermenigvuldigers = objecten.get(objecten.size() - 1);
		
		int maanden = 2;
		for (int i = 0; i < maanden; i++) {
			netwerk.propageerVoorwaarts(uitgangsWaarden);
			
			uitgangsWaarden = netwerk.uitgangsWaarden();
			
			for (int j = 0; j < uitgangsWaarden.length - 1; j++)
				System.out.print(uitgangsWaarden[j] * (vermenigvuldigers.open - vermenigvuldigers.high) + vermenigvuldigers.high + ", ");
			System.out.print(uitgangsWaarden[uitgangsWaarden.length - 1] * (vermenigvuldigers.volume - vermenigvuldigers.close) + vermenigvuldigers.close);
			
			System.out.println();
		}
	}
}
