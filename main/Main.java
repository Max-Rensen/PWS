package main;

import java.util.Scanner;

import netwerk.Netwerk;
import ui.Venster;

public class Main {

	public static void main(String args[]) {
		Venster venster = new Venster();
		venster.lanceer(args);
		
		System.exit(0);
		
		Scanner sc = new Scanner(System.in);

		double[] ingangen = new double[2];
		ingangen[0] = 0.0;
		ingangen[1] = 0.0;
		
		Netwerk netwerk = new Netwerk(new int[]{2, 4, 4, 1}, true);
		
		netwerk.leergraad = 0.3;
		
		double[] uitgangsWaarden = netwerk.uitgangsWaarden();
		double[] verwachtteUitgangen = new double[uitgangsWaarden.length];
		verwachtteUitgangen[0] = 0.0;
		
		double gemiddeldeError = 0.0;
		int errorVerzachting = 50;
		
		int maxIteraties = 10000;

		for (int i = 0; i < maxIteraties; i++) {
			ingangen[0] = (double) Math.round(Math.random());
			ingangen[1] = (double) Math.round(Math.random());
			verwachtteUitgangen[0] = ((ingangen[0] == 1.0 && ingangen[1] == 0.0) || (ingangen[0] == 0.0 && ingangen[1] == 1.0)) ? 1.0 : 0.0;
			
			netwerk.propageerVoorwaarts(ingangen);
			netwerk.propageerAchterwaarts(verwachtteUitgangen);
			
			uitgangsWaarden = netwerk.uitgangsWaarden();
			
			gemiddeldeError += Math.abs(verwachtteUitgangen[0] - uitgangsWaarden[0]);
			
			if (i % errorVerzachting == 0) {
				gemiddeldeError /= errorVerzachting;
				System.out.println(gemiddeldeError);
				gemiddeldeError = 0.0;
			}
			
			if (i == maxIteraties - 1) {
				while (true) {
					System.out.println("Test: ");
					double ingang1 = sc.nextDouble();
					double ingang2 = sc.nextDouble();
					
					ingangen[0] = ingang1;
					ingangen[1] = ingang2;
					
					System.out.println("Verwacht: " + (((ingangen[0] == 1.0 && ingangen[1] == 0.0) || (ingangen[0] == 0.0 && ingangen[1] == 1.0)) ? 1.0 : 0.0));
					
					netwerk.propageerVoorwaarts(ingangen);
					
					uitgangsWaarden = netwerk.uitgangsWaarden();
					for (double d : uitgangsWaarden)
						System.out.println(d);
				}
			}
		}
		
		sc.close();
	}
}
