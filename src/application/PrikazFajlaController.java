package application;

import javafx.fxml.FXML;

import javafx.scene.control.Label;

public class PrikazFajlaController {
	@FXML
	private Label label_ImeFajla;
	@FXML
	private Label label_Tekst;

	public void setFields(String ime, String tekst) {
		label_ImeFajla.setText(ime);
		label_Tekst.setText(tekst);
	}
}
