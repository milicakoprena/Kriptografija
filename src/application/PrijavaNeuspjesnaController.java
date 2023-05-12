package application;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.event.ActionEvent;

public class PrijavaNeuspjesnaController {
	@FXML
	private Button button_OK;
    
	// Event Listener on Button[#button_OK].onAction
	@FXML
	public void close(ActionEvent event) {
		button_OK.getScene().getWindow().hide();
	}
}
