package netwerk;

public class Synaps {

	public double weging;
	public double deltaWeging;
	
	public Synaps() {

		// Stelt de weging gelijk aan een willekeurig getal tussen -1 en 1
		weging = Math.random() * 2.0 - 1.0;

		deltaWeging = 0.0;
	}
}
