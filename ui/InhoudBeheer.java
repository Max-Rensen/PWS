package ui;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import netwerk.Activatie;

public class InhoudBeheer {

	@FXML
	private Label ingangsneuronen = new Label();
	@FXML
	private Label tussenneuronen = new Label();
	@FXML
	private Label uitgangsneuronen = new Label();
	@FXML
	private Label gemiddeldeError = new Label();
	@FXML
	private Label leergraad = new Label();
	@FXML
	private Label bias = new Label();
	@FXML
	private Label activatie = new Label();
	@FXML
	private Label voorspelling = new Label();
	@FXML
	private Label mogelijkheid = new Label();
	@FXML
	private Button wissen = new Button();
	@FXML
	private Button handmatig = new Button();
	@FXML
	private Canvas tekenvlak = new Canvas();
	@FXML
	private AnchorPane anker = new AnchorPane();
	
	private Venster venster;
	private Canvas onzichtbaarTekenvlak;
	private double pixelGrootte;
	private GraphicsContext g;
	private double pixels[];
	private WritableImage afbeelding;
	private double vorigeX, vorigeY;
	private Label voorspellingen[];
	private ArrayList<Label> mogelijkheden;
	
	@FXML
	private void initialize() {
		g = tekenvlak.getGraphicsContext2D();
		g.setFill(Color.BLACK);
		g.setStroke(Color.BLACK);
		
		voorspellingen = new Label[2];
		mogelijkheden = new ArrayList<>();
		
		vorigeX = -1;
		vorigeY = -1;
		
		wissen.setOnAction(e -> {
			g.clearRect(0, 0, tekenvlak.getWidth(), tekenvlak.getHeight());
		});
		
		handmatig.setOnAction(e -> {
			afbeelding = tekenvlak.snapshot(new SnapshotParameters(), afbeelding);
			onzichtbaarTekenvlak.getGraphicsContext2D().clearRect(0, 0, onzichtbaarTekenvlak.getWidth(), onzichtbaarTekenvlak.getHeight());
			onzichtbaarTekenvlak.getGraphicsContext2D().drawImage(afbeelding, 0, 0, tekenvlak.getWidth(), tekenvlak.getHeight(), 0, 0, 28, 28);
			afbeelding = onzichtbaarTekenvlak.snapshot(new SnapshotParameters(), afbeelding);
			
			byte[] data = new byte[venster.breedte * venster.breedte * 4];
			afbeelding.getPixelReader().getPixels(0, 0, venster.breedte, venster.breedte, PixelFormat.getByteBgraInstance(), data, 0, venster.breedte * 4);
			for (int i = 0; i < venster.breedte; i++)
				for (int j = 0; j < venster.breedte; j++)
					pixels[j * venster.breedte + i] = data[(j * venster.breedte + i) * 4] < 0 ? 0.0 : (1.0 - data[(j * venster.breedte + i) * 4] / 255.0);

			// Tekent pixels in het tekenvlak
//			g.setFill(Color.BLACK);
//			for (int i = 0; i < venster.breedte; i++) {
//				for (int j = 0; j < venster.breedte; j++) {
//					g.setFill(new Color(pixels[j * venster.breedte + i], pixels[j * venster.breedte + i], pixels[j * venster.breedte + i], 1.0));
//					g.fillRect(i * pixelGrootte, j * pixelGrootte, pixelGrootte, pixelGrootte);
//				}
//			}
//			
			venster.netwerk.propageerVoorwaarts(pixels);
			double waarden[] = venster.netwerk.uitgangsWaarden();
			double totaal = 0.0;
			int max[] = new int[waarden.length];
			
			for (int i = 0; i < waarden.length; i++) {
				max[i] = i;
				totaal += waarden[i];
			}
			
			boolean gesorteerd = false;
			while (!gesorteerd) {
				gesorteerd = true;
				
				for (int i = 1; i < waarden.length; i++) {
					if (waarden[max[i]] > waarden[max[i - 1]]) {
						int temp = max[i];
						max[i] = max[i - 1];
						max[i - 1] = temp;
						gesorteerd = false;
					}
				}
			}
			
			for (int i = 0; i < (waarden.length > 2 ? 2 : waarden.length); i++) {
				if (voorspellingen[i] != null)
					anker.getChildren().remove(voorspellingen[i]);
				voorspellingen[i] = new Label(venster.mogelijkheden.get(max[i]) + " " + String.format("%.2f", waarden[max[i]] / totaal * 100.0).replace(',', '.') + "%");
				voorspellingen[i].setLayoutX(voorspelling.getLayoutX());
				voorspellingen[i].setFont(new Font(voorspellingen[i].getFont().getName(), 15));
				voorspellingen[i].setLayoutY(voorspelling.getLayoutY() + 25 * i + 30);
				anker.getChildren().add(voorspellingen[i]);
			}
		});
		
		tekenvlak.setOnMouseDragged(e -> {
			if (e.getButton() != MouseButton.PRIMARY)
				return;
			double x = e.getX();
			double y = e.getY();
			
			if (vorigeX == -1 && vorigeY == -1) {
				vorigeX = x;
				vorigeY = y;
			}
			
			BoxBlur blur = new BoxBlur(8, 8, 1);
			g.setEffect(blur);
			g.setFill(Color.BLACK);
			g.beginPath();
			g.moveTo(vorigeX, vorigeY);
			g.lineTo(x, y);
			g.stroke();
			g.setEffect(null);
			
			vorigeX = x;
			vorigeY = y;
		});
		
		tekenvlak.setOnMouseReleased(e -> {
			if (e.getButton() == MouseButton.PRIMARY)
				vorigeX = vorigeY = -1;
		});
	}
	
	public void initialiseerVenster(Venster venster) {
		this.venster = venster;
		
		pixelGrootte = tekenvlak.getWidth() / venster.breedte;
		pixels = new double[venster.breedte * venster.breedte];
		onzichtbaarTekenvlak = new Canvas(venster.breedte, venster.breedte);
		g.setLineWidth(pixelGrootte * 0.8);
		
		int tussen = 0;
		for (int i = 1; i < venster.netwerk.neuronen.size() - 1; i++)
			tussen += venster.netwerk.neuronen.get(i).size() - 1;
		
		ingangsneuronen.setText(Integer.toString(venster.netwerk.neuronen.get(0).size() - 1));
		tussenneuronen.setText(Integer.toString(tussen));
		uitgangsneuronen.setText(Integer.toString(venster.netwerk.neuronen.get(venster.netwerk.neuronen.size() - 1).size() - (venster.netwerk.bias ? 1 : 0)));
		gemiddeldeError.setText(String.format("%.11f", venster.gemiddeldeError).replace(',', '.'));
		leergraad.setText(Double.toString(venster.netwerk.leergraad));
		bias.setText(venster.netwerk.bias ? "Wel" : "Niet");
		activatie.setText(Activatie.huidig.toString());
		
		// TODO: Fix de oneindige mogelijkheden bug
		for (Label l : mogelijkheden)
			anker.getChildren().remove(l);
		
		mogelijkheden.clear();
		
		for (int i = 0; i < venster.mogelijkheden.size(); i++) {
			mogelijkheden.add(new Label(venster.mogelijkheden.get(i)));
			mogelijkheden.get(i).setLayoutX(mogelijkheid.getLayoutX());
			mogelijkheden.get(i).setFont(new Font(mogelijkheden.get(i).getFont().getName(), 15));
			mogelijkheden.get(i).setLayoutY(mogelijkheid.getLayoutY() + 25 * i + 30);
			anker.getChildren().add(mogelijkheden.get(i));
		}
	}
}