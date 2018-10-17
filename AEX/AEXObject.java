package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class AEXObject {
	public String datum;
	public double open, high, low, close, volume;
	
	public AEXObject(String datum, double open, double high, double low, double close, double volume) {
		this.datum = datum;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}
	
	public static ArrayList<AEXObject> laadAEXBestand(String locatie) {
		ArrayList<AEXObject> objecten = new ArrayList<>();
		double max = 0, min = 1000;
		double maxVolume = 0, minVolume = Double.POSITIVE_INFINITY;
		
		try {
			BufferedReader lezer = new BufferedReader(new FileReader(locatie));
			
			System.out.println(lezer.readLine());
			
			String regel = lezer.readLine();
			while (regel != null) {
				String[] waarden = regel.split(",");
				
				try {
					objecten.add(new AEXObject(waarden[0], Double.parseDouble(waarden[1]), Double.parseDouble(waarden[2]), Double.parseDouble(waarden[3]), Double.parseDouble(waarden[4]), Double.parseDouble(waarden[6])));
					
					if (objecten.get(objecten.size() - 1).close > max)
						max = objecten.get(objecten.size() - 1).close;
					
					if (objecten.get(objecten.size() - 1).close < min)
						min = objecten.get(objecten.size() - 1).close;
					
					if (objecten.get(objecten.size() - 1).volume > maxVolume)
						maxVolume = objecten.get(objecten.size() - 1).volume;
					
					if (objecten.get(objecten.size() - 1).volume < minVolume)
						minVolume = objecten.get(objecten.size() - 1).volume;
					
				} catch (Exception e) {}
				
				regel = lezer.readLine();
			}
			
			lezer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (AEXObject o : objecten) {
			o.open -= min;
			o.high -= min;
			o.low -= min;
			o.close -= min;
			
			o.open /= (max - min);
			o.high /= (max - min);
			o.low /= (max - min);
			o.close /= (max - min);
			
			o.volume -= minVolume;
			o.volume /= (maxVolume - minVolume);
		}
		
		System.out.println("Max index: " + max + ", " + "Min index: " + min);
		System.out.println("Max volume: " + maxVolume + ", " + "Min volume: " + minVolume);
		objecten.add(new AEXObject("", max, min, 0, minVolume, maxVolume));
		
		return objecten;
	}
}
