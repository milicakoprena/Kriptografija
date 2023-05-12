package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PorukaRegistracijaController {
	@FXML
	private Label label_Putanja;
	@FXML
	private Button button_OK;
	private String ime;
	private String lozinka;

	public void setFields(String ime, String lozinka) {
		this.ime = ime;
		label_Putanja.setText(Path.SertifikatiPath + "certs\\" + ime + ".crt");
		this.lozinka = lozinka;
	}
	// Event Listener on Button[#button_OK].onAction
	@FXML
	public void goToRepozitorijum(ActionEvent event) {
		try {
			button_OK.getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Repozitorijum.fxml"));

			Stage stage = new Stage();
			stage.setScene(
				new Scene(loader.load())
			);

			RepozitorijumController controller = loader.getController();
			controller.setFields(ime, lozinka);

			stage.show();
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
	}
}
