package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import netwerk.Netwerk;
import netwerk.Neuron;
import netwerk.Synaps;

public class MenuBeheer {

	@FXML
	private MenuItem importeer = new MenuItem();
	@FXML
	private MenuItem exporteer = new MenuItem();
	@FXML
	private MenuItem train = new MenuItem();
	
	private Venster venster;
	
	public void initialiseerVenster(Venster venster) {
		this.venster = venster;
		
		importeer.setOnAction(e -> {
			importeerData();
		});
	
		exporteer.setOnAction(e -> {
			exporteerData();
		});

		train.setOnAction(e -> {
			venster.weergeefTrainVenster();
		});
	}
	
	/* BESTAND FORMAAT:
	
	Mogelijkheid_1 Mogelijkheid_2
	bias leergraad gemiddelde_error
	input hidden_1 hidden_2 ... hidden_n output
	wegingen input
	wegingen hidden_1
	wegingen hidden_2
	...
	wegingen hidden_n
	
	*/
	/* VOORBEELD:
	
	Banaan Appel Peer
	1 0.2 0.012345
	784 512 512 3
	3.141592 0.653589 0.793238 0.462643 ...
	2.718281 0.828459 0.045235 0.360287 ...
	1.414213 0.562373 0.095048 0.801688 ...

	*/
	
	private void importeerData() {
		try {
			FileChooser kiezer = new FileChooser();
			
			kiezer.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt bestand (*.txt)", "*.txt"));
			
			File file = kiezer.showOpenDialog(venster.stadium);
			
			BufferedReader lezer = new BufferedReader(new FileReader(file));
			String mogelijkheden[] = lezer.readLine().split(" ");
			for (String mogelijkheid : mogelijkheden)
				venster.mogelijkheden.add(mogelijkheid);
			
			String parameters[] = lezer.readLine().split(" ");
			boolean bias = Integer.parseInt(parameters[0]) == 1;
			double leergraad = Double.parseDouble(parameters[1]);
			venster.gemiddeldeError = Double.parseDouble(parameters[2]);
			
			String lagen[] = lezer.readLine().split(" ");
			
			int layout[] = new int[lagen.length];
			for (int i = 0; i < lagen.length; i++)
				layout[i] = Integer.parseInt(lagen[i]);
			
			venster.netwerk = new Netwerk(layout, bias);
			venster.netwerk.leergraad = leergraad;
			
			String regel = lezer.readLine();
			int index = 0;
			while (regel != null && index < venster.netwerk.neuronen.size() - 1) {
				String wegingen[] = regel.split(" ");
				
				for (int j = 0; j < venster.netwerk.neuronen.get(index).size(); j++)
					for (int k = 0; k < venster.netwerk.neuronen.get(index).get(j).synapsen.length; k++)
						venster.netwerk.neuronen.get(index).get(j).synapsen[k].weging = Double.parseDouble(wegingen[j * venster.netwerk.neuronen.get(index).get(j).synapsen.length + k]);
				
				index++;
				regel = lezer.readLine();
			}
			
			lezer.close();
		} catch (Exception e) {
			System.out.println("Fix het formaat");
			e.printStackTrace();
		}

		venster.beheer.initialiseerVenster(venster);
	}
	
	private void exporteerData() {
		try {
			FileChooser kiezer = new FileChooser();
			
			kiezer.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt bestand (*.txt)", "*.txt"));
			
			File file = kiezer.showSaveDialog(venster.stadium);
			
			PrintWriter printer = new PrintWriter(file);
			
			for (String mogelijkheid : venster.mogelijkheden)
				printer.print(mogelijkheid + " ");
			
			printer.println();
			
			printer.print((venster.netwerk.bias ? 1 : 0) + " ");
			printer.print(venster.netwerk.leergraad + " ");
			printer.print(venster.gemiddeldeError + " ");
			
			printer.println();
			
			for (int i = 0; i < venster.netwerk.neuronen.size(); i++)
				printer.print(Integer.toString(venster.netwerk.neuronen.get(i).size() - (venster.netwerk.bias ? 1 : 0)) + " ");
			
			printer.println();
	
			for (int i = 0; i < venster.netwerk.neuronen.size() - 1; i++) {
				for (Neuron n : venster.netwerk.neuronen.get(i))
					for (Synaps s : n.synapsen)
						printer.print(Double.toString(s.weging) + " ");
				
				printer.println();
			}
			
			printer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
