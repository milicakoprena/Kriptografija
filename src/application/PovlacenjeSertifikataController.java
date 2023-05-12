package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

import javafx.event.ActionEvent;

import javafx.scene.control.PasswordField;

public class PovlacenjeSertifikataController {
	@FXML
	private TextField textField_KorisnickoIme;
	@FXML
	private Button button_Prijava;
	@FXML
	private Button button_Registracija;
	@FXML
	private PasswordField passwordField_Lozinka;

	// Event Listener on Button[#button_Prijava].onAction
	@FXML
	public void goToPrijavaUspjesna(ActionEvent event) {
		boolean sucess = Funkcije.prijava(textField_KorisnickoIme.getText(), passwordField_Lozinka.getText());
		
		if (sucess) {
			reaktivirajSertifikat();
			try {
				button_Prijava.getScene().getWindow().hide();
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PrijavaUspjesna.fxml")); 
				
		        Stage stage = new Stage();
		        stage.initOwner(button_Prijava.getScene().getWindow());
		        stage.setScene(new Scene((Parent) loader.load()));
		        PrijavaUspjesnaController controller = loader.getController();
		        controller.setFields(textField_KorisnickoIme.getText(),passwordField_Lozinka.getText());
		        stage.showAndWait();

				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PrijavaNeuspjesna.fxml")); 
				
		        Stage stage = new Stage();
		        stage.initOwner(button_Prijava.getScene().getWindow());
		        stage.setScene(new Scene((Parent) loader.load()));
		        stage.showAndWait();

				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	// Event Listener on Button[#button_Registracija].onAction
	@FXML
	public void goToRegistracija(ActionEvent event) {
		try {
			button_Registracija.getScene().getWindow().hide();
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
	
	public void reaktivirajSertifikat() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(Path.SertifikatiPath + "index.txt"));
			String line = br.readLine();
			String newLine = line;
			while(line!=null) {
				if(line.startsWith("R") && line.endsWith("CN="+textField_KorisnickoIme.getText())){
					String array[] = line.split("\\s+");
					newLine = "V" + "	" + array[1] + "		" + array[3] + 
							"	" + array[4] + " " + array[5];
					break;
				}
				
				line=br.readLine();
			}
			br.close();
			
			Scanner sc = new Scanner(new File(Path.SertifikatiPath + "index.txt"));
			StringBuffer buffer = new StringBuffer();
			while (sc.hasNextLine()) {
		         buffer.append(sc.nextLine()+System.lineSeparator());
		    }
			String fileContents = buffer.toString();
			sc.close();
			
			fileContents = fileContents.replaceAll(line, newLine);
			FileWriter writer = new FileWriter(Path.SertifikatiPath + "index.txt");
			writer.append(fileContents);
			writer.flush();
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
