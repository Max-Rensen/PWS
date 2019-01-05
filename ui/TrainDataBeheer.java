package ui;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

public class TrainDataBeheer {
	@FXML
	private Label iteratieLabel = new Label();
	@FXML
	private Label errorLabel = new Label();
	@FXML
	private Label leergraadLabel = new Label();
	@FXML
	private Button stop = new Button();
	@FXML
	private Button trainVerder = new Button();
	@FXML
	private Canvas errorCanvas = new Canvas();
	
	private ArrayList<Pair<double[], Integer>> data;
	private ArrayList<Double> errors;
	private Venster venster;
	private int huidigeIteratie;
	private int errorVerzachting;
	private int maxPunten;
	private int huidigeDrempelScore;
	private double gemiddeldeError;
	private double maxError;
	private GraphicsContext g;
	
	final private int maxDrempelScore = 5;
	final private double drempelError = 0.05;
	
	public void initialiseerVenster(Venster venster, ArrayList<Pair<double[], Integer>> data) {
		this.data = data;
		this.venster = venster;
		
		g = errorCanvas.getGraphicsContext2D();
		errors = new ArrayList<>();
		errorVerzachting = 50;
		maxPunten = 50;
		
		leergraadLabel.setText(Double.toString(venster.netwerk.leergraad));
		
		venster.popupStadia.get(venster.popupStadia.size() - 1).setOnCloseRequest(e -> {
			if (e.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST)
				stop.fire();
		});
		
		stop.setOnAction(e -> {
			venster.beheer.initialiseerVenster(venster);
			venster.gemiddeldeError = gemiddeldeError;
			venster.popupStadia.remove(venster.popupStadia.size() - 1).close();
		});
		
		trainVerder.setOnAction(e -> {
			huidigeIteratie = 0;
			trainNetwerk();
		});
		
		trainNetwerk();
	}
	
	private void trainNetwerk() {
		new AnimationTimer() {
            public void handle(long t) {
				int index = (int) (Math.random() * data.size());
			
				double[] uitgangsWaarden = venster.netwerk.uitgangsWaarden();
				double[] verwachtteUitgangen = new double[uitgangsWaarden.length];
				
				verwachtteUitgangen[data.get(index).getValue()] = 1.0;
				
				venster.netwerk.propageerVoorwaarts(data.get(index).getKey());
				venster.netwerk.propageerAchterwaarts(verwachtteUitgangen);
					
				uitgangsWaarden = venster.netwerk.uitgangsWaarden();
				
				for (int i = 0; i < uitgangsWaarden.length; i++)
					gemiddeldeError += Math.abs(verwachtteUitgangen[i] - uitgangsWaarden[i]);
				
				if ((huidigeIteratie + 1) % errorVerzachting == 0) {
					for (int i = 0; i < uitgangsWaarden.length; i++)
						System.out.print(verwachtteUitgangen[i] + " ");
						System.out.println();
					for (int i = 0; i < uitgangsWaarden.length; i++)
						System.out.print(uitgangsWaarden[i] + " ");
					System.out.println();
					System.out.println();
					
					gemiddeldeError /= errorVerzachting;
					voegErrorToe(gemiddeldeError);
					if (gemiddeldeError <= drempelError) 
						huidigeDrempelScore++;
					gemiddeldeError = 0.0;
				}
				
				iteratieLabel.setText(Integer.toString(huidigeIteratie++));
				
				if (huidigeDrempelScore >= maxDrempelScore)
					this.stop();
            }
		}.start();
	}
	
	private void plotErrors() {
		double afstand = errorCanvas.getWidth() / errors.size();
		double hoogte = errorCanvas.getHeight() / maxError;
		
		g.clearRect(0, 0, errorCanvas.getWidth(), errorCanvas.getHeight());
		
		g.beginPath();
		g.moveTo(0, errorCanvas.getHeight() - hoogte * errors.get(0));
		
		for (int i = 1; i < errors.size(); i++)
			g.lineTo(afstand * i, errorCanvas.getHeight() - hoogte * errors.get(i));
		
		g.stroke();
	}
	
	private void voegErrorToe(double error) {
		if (error > maxError) maxError = error;
		
		if (errors.size() % maxPunten == 0) {
			maxError += error * 0.1;
			maxError /= 1.1;
		}
		
		errors.add(error);
		errorLabel.setText(String.format("%.11f", error).replace(',', '.'));
		plotErrors();
	}
}
