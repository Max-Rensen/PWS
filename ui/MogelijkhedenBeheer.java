package ui;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class MogelijkhedenBeheer {
	@FXML
	private TextField naam = new TextField();
	@FXML
	private TextField aantal = new TextField();
	@FXML
	private TextField bestandsLocatie = new TextField();
	@FXML
	private Button OK = new Button();
	@FXML
	private Button kiesLocatie = new Button();
	
	public void initialiseerVenster(Venster venster, TrainVensterBeheer beheer) {
		OK.setOnAction(e -> {
			beheer.voegMogelijkheidToe(naam.getText(), Integer.parseInt(aantal.getText()), bestandsLocatie.getText());
			venster.popupStadia.remove(venster.popupStadia.size() - 1).close();
		});
		
		kiesLocatie.setOnAction(e -> {
			FileChooser bestandKiezer = new FileChooser();
			bestandKiezer.setTitle("Kies folder");
			File gekozenFolder = bestandKiezer.showOpenDialog(venster.popupStadia.get(venster.popupStadia.size() - 1));
			bestandsLocatie.setText(gekozenFolder.getPath());
		});
	}
}
