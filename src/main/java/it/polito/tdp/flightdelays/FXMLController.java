package it.polito.tdp.flightdelays;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.flightdelays.model.Airline;
import it.polito.tdp.flightdelays.model.AirportPair;
import it.polito.tdp.flightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtResult;

    @FXML
    private ComboBox<Airline> cmbBoxLineaAerea;

    @FXML
    private Button caricaVoliBtn;
    
    @FXML
    private Button btnSimula;

    @FXML
    private TextField numeroPasseggeriTxtInput;

    @FXML
    private TextField numeroVoliTxtInput;

    @FXML
    void doCaricaVoli(ActionEvent event) {
    	txtResult.clear();
    	Airline airline = this.cmbBoxLineaAerea.getValue();
    	if(airline == null) {
    		txtResult.appendText("Selezionare una linea aerea per procedere con il calcolo!\n");
    		return;
    	}
    	this.model.createGraph(airline);
    	txtResult.appendText("Grafo creato! \n\n");
    	
    	List<AirportPair> worstCases = this.model.worstCases();
    	txtResult.appendText("Linee peggiori per ritardo:\n");
    	for(AirportPair ap : worstCases) {
    		txtResult.appendText(ap.getA1() + " - " + ap.getA2() + " --> " + ap.getWeight() + "\n");
    	}
    	this.btnSimula.setDisable(false);
    }

    @FXML
    void doSimula(ActionEvent event) {
    	txtResult.clear();
    	Integer K;
    	Integer V;
    	try {
    		K = Integer.parseInt(this.numeroPasseggeriTxtInput.getText());
    		V = Integer.parseInt(this.numeroVoliTxtInput.getText());
    	} catch(NumberFormatException e) {
    		txtResult.appendText("Inserire 2 numeri interi positivi negli appositi campi!\n");
    		return;
    	}
    	if(K < 0 || V < 0) {
    		txtResult.appendText("Inserire 2 numeri interi positivi negli appositi campi!\n");
    		return;
    	}
    	this.model.simulate(K, V);
    	Map<Integer, Integer> delays = this.model.getDelays();
    	txtResult.appendText("Simulazione completa. Ritardo cumulato dei passeggeri:\n\n");
    	for(Integer i : delays.keySet()) {
    		txtResult.appendText("Passeggero " + i + ": " + delays.get(i) + "\n");
    	}
    }

    @FXML
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxLineaAerea != null : "fx:id=\"cmbBoxLineaAerea\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert caricaVoliBtn != null : "fx:id=\"caricaVoliBtn\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroPasseggeriTxtInput != null : "fx:id=\"numeroPasseggeriTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroVoliTxtInput != null : "fx:id=\"numeroVoliTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		this.cmbBoxLineaAerea.getItems().addAll(this.model.getAllAirlines());
		this.btnSimula.setDisable(true);
	}
}
