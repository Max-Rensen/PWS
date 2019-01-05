package ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import netwerk.Activatie;
import netwerk.Netwerk;

public class Venster extends Application {

	public Stage stadium;
	public ArrayList<Stage> popupStadia;
	public InhoudBeheer beheer;
	public BorderPane houder;
	public Netwerk netwerk;
	public double gemiddeldeError;
	public int breedte;
	
	public ArrayList<String> mogelijkheden;
	
	public void lanceer(String args[]) {
		launch(args);
	}
	
	public void start(Stage stadium) throws Exception {
		this.stadium = stadium;
		this.stadium.setTitle("Kunstmatige Neuraal Netwerk Prototype");
		
		breedte = 28;
		mogelijkheden = new ArrayList<>();
		
		Activatie.huidig = Activatie.FUNCTIE.SIGMOÏDE;
		netwerk = new Netwerk(new int[]{ breedte * breedte, 0, mogelijkheden.size() }, true);
		
		popupStadia = new ArrayList<>();
	
		initialiseer();
	}
	
	private void initialiseer() {
		try {
			FXMLLoader lader = new FXMLLoader();
			lader.setLocation(Venster.class.getResource("Venster.fxml"));
			houder = lader.load();

			stadium.setScene(new Scene(houder));
			stadium.show();

			MenuBeheer beheer = lader.getController();
			beheer.initialiseerVenster(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			FXMLLoader lader = new FXMLLoader();
			lader.setLocation(Venster.class.getResource("Inhoud.fxml"));
			houder.setCenter((AnchorPane) lader.load());
			
			beheer = lader.getController();
			beheer.initialiseerVenster(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void weergeefTrainVenster() {
		TrainVensterBeheer beheer = weergeefVenster("TrainVenster.fxml", "Train venster").getController();
		beheer.initialiseerVenster(this);
	}
	
	public void weergeefTrainData(ArrayList<Pair<double[], Integer>> data) {
        TrainDataBeheer beheer = weergeefVenster("TrainData.fxml", "Train data").getController();
        beheer.initialiseerVenster(this, data);
	}
	
	public void weergeefMogelijkheidVenster(TrainVensterBeheer beheer) {
		MogelijkhedenBeheer mbeheer = weergeefVenster("Mogelijkheid.fxml", "Voeg mogelijkheid toe").getController();
		mbeheer.initialiseerVenster(this, beheer);
	}
	
	public FXMLLoader weergeefVenster(String fxml, String titel) {
		FXMLLoader lader = new FXMLLoader();
		lader.setLocation(Venster.class.getResource(fxml));
		AnchorPane pagina = null;
		
		try {
			pagina = (AnchorPane) lader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Stage popup = new Stage();
		popup.setTitle(titel);
		popup.initModality(Modality.WINDOW_MODAL);
		popup.initOwner(stadium);
        Scene scene = new Scene(pagina);
        popup.setScene(scene);
        popup.show();
        
        popupStadia.add(popup);

        return lader;
	}
}
