package ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import netwerk.Activatie;
import netwerk.Netwerk;

public class TrainVensterBeheer {
	@FXML
	private Button voegLaagToe = new Button();
	@FXML
	private Button voegDataToe = new Button();
	@FXML
	private Button train = new Button();
	@FXML
	private Button trainHuidig = new Button();
	@FXML
	private TextField leergraad = new TextField();
	@FXML
	private TextField neuronen = new TextField();
	@FXML
	private SplitMenuButton activatieFunctie = new SplitMenuButton();	
	@FXML
	private ListView<Integer> lagenLijst = new ListView<Integer>();
	@FXML
	private ListView<String> mogelijkhedenLijst = new ListView<String>(); 
	
	private ArrayList<Pair<double[], Integer>> data; 
	
	private ObservableList<Integer> lagen;
	private ObservableList<String> mogelijkheden;
	
	private Venster venster;
	
	public void initialiseerVenster(Venster venster) {
		this.venster = venster;
		venster.mogelijkheden.clear();
		
		data = new ArrayList<>();
		
		lagen = FXCollections.observableArrayList();
		mogelijkheden = FXCollections.observableArrayList();
		lagenLijst.setItems(lagen);
		mogelijkhedenLijst.setItems(mogelijkheden);
		
		venster.popupStadia.get(venster.popupStadia.size() - 1).setOnCloseRequest(e -> {
			if (e.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST)
				venster.popupStadia.remove(venster.popupStadia.size() - 1);
		});
		
		train.setOnAction(e -> {
			int layout[] = new int[lagen.size() + 2];
			layout[0] = venster.breedte * venster.breedte;
			
			for (int i = 0; i < lagen.size(); i++)
				layout[i + 1] = lagen.get(i);
			
			layout[layout.length - 1] = venster.mogelijkheden.size();
			
			venster.netwerk = new Netwerk(layout, true);
			venster.netwerk.leergraad = Double.parseDouble(leergraad.getText());
			venster.popupStadia.remove(venster.popupStadia.size() - 1).close();
			venster.weergeefTrainData(data);
		});
		
		List<String> functies = Arrays.asList("Sigmoïde", "Tanh", "Stap", "RELU");
		activatieFunctie.getItems().forEach(item -> {
			item.setOnAction(e -> {
				Activatie.huidig = Activatie.FUNCTIE.values()[functies.indexOf(item.getText())];
				activatieFunctie.setText(activatieFunctie.getItems().get(functies.indexOf(item.getText())).getText());
			});
		});
		
		trainHuidig.setOnAction(e -> {
			venster.popupStadia.remove(venster.popupStadia.size() - 1).close();
			venster.weergeefTrainData(data);
		});
		
		voegLaagToe.setOnAction(e -> {
			lagen.add(Integer.parseInt(neuronen.getText()));
		});
		
		voegDataToe.setOnAction(e -> {
			venster.weergeefMogelijkheidVenster(this);
		});
	}
	
	public void voegMogelijkheidToe(String mogelijkheid, int aantal, String bestandsLocatie) {
		venster.mogelijkheden.add(mogelijkheid);
		mogelijkheden.add(mogelijkheid);
		
		try {
			BufferedReader lezer = new BufferedReader(new FileReader(bestandsLocatie));
			
			for (int i = 0; i < aantal; i++) {
				double pixels[] = new double[venster.breedte * venster.breedte];
				String[] elementen = lezer.readLine().split(" ");
				for (int j = 0; j < venster.breedte; j++)
					for (int k = 0; k < venster.breedte; k++)
						pixels[k * venster.breedte + j] = (double) Integer.parseInt(elementen[k * venster.breedte + j]) / 255.0;
			
				data.add(new Pair<double[], Integer>(pixels, venster.mogelijkheden.size() - 1));
			}
			
			lezer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
