package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


import javafx.event.ActionEvent;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;

public class PrijavaController implements Initializable{
	@FXML
	private AnchorPane pane2;
	@FXML
	private TextField textField_KorisnickoIme;
	@FXML
	private PasswordField passwordField_Lozinka;
	@FXML
	private Button buttonPrijava;
	@FXML
	private AnchorPane pane1;
	@FXML
	private Button buttonDalje;
	@FXML
    private TextField textField_Sertifikat;
	@FXML
    private Label label_Unesi;
	private int brojKlikova = 0;


	// Event Listener on Button[#buttonPrijava].onAction
	@FXML
	public void goToRepozitorijum(ActionEvent event) {
		brojKlikova++;
		boolean sucess = false;
		
		sucess = Funkcije.prijava(textField_KorisnickoIme.getText(), passwordField_Lozinka.getText());
		if (sucess && textField_Sertifikat.getText().equals(textField_KorisnickoIme.getText() + ".crt") && brojKlikova < 3) {
			prijavaUspjesna();
		}
		else if (brojKlikova < 3){
			prijavaNeuspjesna();
			textField_KorisnickoIme.setText("");
			passwordField_Lozinka.setText("");
		}
		
	   else povuciSertifikat();
		
	}
	
	public void prijavaUspjesna() {
		try {
			buttonPrijava.getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PrijavaUspjesna.fxml")); 
			
	        Stage stage = new Stage();
	        stage.initOwner(buttonPrijava.getScene().getWindow());
	        stage.setScene(new Scene((Parent) loader.load()));
	        PrijavaUspjesnaController controller = loader.getController();
	        controller.setFields(textField_KorisnickoIme.getText(), passwordField_Lozinka.getText());
	        stage.showAndWait();

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void prijavaNeuspjesna() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PrijavaNeuspjesna.fxml")); 
			
	        Stage stage = new Stage();
	        stage.initOwner(buttonPrijava.getScene().getWindow());
	        stage.setScene(new Scene((Parent) loader.load()));
	        stage.showAndWait();

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void povuciSertifikat() {
	    try {
	    	buttonPrijava.getScene().getWindow().hide();
	    	String command1[]= {Path.OpenSSLPath, "ca", "-revoke", "certs\\" + textField_Sertifikat.getText(),
					"-crl_reason", "cessationOfOperation", "-config", "openssl.cnf", "-passin", "pass:sigurnost"};
	    	Random rand = new Random();	    	
		    CommandPrompt.execute(command1, Path.SertifikatiPath);
		    String command2[]= {Path.OpenSSLPath, "ca", "-gencrl", "-out","crl\\lista" + rand.nextInt(100000) + ".crl",
		    		"-config", "openssl.cnf", "-passin",
		    		"pass:sigurnost"};
		    
		    CommandPrompt.execute(command2, Path.SertifikatiPath);
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PovlacenjeSertifikata.fxml")); 
			
	        Stage stage = new Stage();
	        stage.initOwner(buttonPrijava.getScene().getWindow());
	        stage.setScene(new Scene((Parent) loader.load()));
	        stage.showAndWait();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	// Event Listener on Button[#buttonDalje].onAction
	@FXML
	public void goToPane2(ActionEvent event) {
		String result = "";
		try {
			String command[]= {Path.OpenSSLPath, "verify", "-CAfile", "rootca.pem",
					"certs\\" + textField_Sertifikat.getText()};
			
			result = CommandPrompt.executeReturn(command, Path.SertifikatiPath);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if (result.equals("certs\\" + textField_Sertifikat.getText() + ": OK\r\n")) {
			pane1.setVisible(false);
			pane2.setVisible(true);
		}
		else label_Unesi.setText("Niste unijeli ispravan sertifikat.");
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		pane1.setVisible(true);
		pane2.setVisible(false);
	}
	

	
	
}
