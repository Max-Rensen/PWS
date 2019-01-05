package netwerk;

public class Activatie {
	
	// Een lijst van alle beschikbare acitvatie functies
	public static enum FUNCTIE {
		SIGMOÏDE, TANH, STAP, RELU
	};

	// De huidige activatie functie, deze kan aangepast worden
	public static FUNCTIE huidig = FUNCTIE.TANH;
	
	// Activeert x met de huidige activatie functie
	public static double activeer(double x) {
		switch (huidig) {
		case TANH:
			return Math.tanh(x);
		case SIGMOÏDE:
			return sigmoïde(x);
		case STAP:
			return x < 0.0 ? 0.0 : 1.0;
		default:
			return x < 0.0 ? 0.0 : x;
		}
	}
	
	// Activeert x met de afgeleide van de huidige activatie functie
	public static double afgeleideActiveer(double x) {
		switch (huidig) {
		case TANH:
			return 1.0 - Math.tanh(x) * Math.tanh(x);
		case SIGMOÏDE:
			return sigmoïde(x) * (1.0 - sigmoïde(x));
		case STAP:
			return x != 0.0 ? 0.0 : Double.POSITIVE_INFINITY;
		default:
			return x < 0.0 ? 0.0 : 1.0;
		}
	}
	
	private static double sigmoïde(double x) {
		return 1.0 / (1.0 + Math.exp(-x));
	}
}
