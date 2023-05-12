package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class PocetnaController {
	@FXML
	private Button buttonRegistracija;
	@FXML
	private Button buttonPrijava;

	// Event Listener on Button[#buttonRegistracija].onAction
	@FXML
	public void goToRegistracija(ActionEvent event) {
		try {
			buttonRegistracija.getScene().getWindow().hide();
			Stage registracija = new Stage();
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Registracija.fxml"));
			Scene scene = new Scene(root);
			
			registracija.setScene(scene);
			registracija.show();
			registracija.setResizable(false);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	// Event Listener on Button[#buttonPrijava].onAction
	@FXML
	public void goToPrijava(ActionEvent event) {
		try {
			buttonPrijava.getScene().getWindow().hide();
			Stage prijava = new Stage();
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Prijava.fxml"));
			Scene scene = new Scene(root);
			
			prijava.setScene(scene);
			prijava.show();
			prijava.setResizable(false);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
