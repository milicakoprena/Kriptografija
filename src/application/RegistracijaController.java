package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;


import javafx.event.ActionEvent;

import javafx.scene.control.PasswordField;

public class RegistracijaController {

	@FXML
	private TextField textField_KorisnickoIme;
	@FXML
	private PasswordField passwordField_Lozinka;
	@FXML
	private Button buttonRegistracija;

	// Event Listener on Button[#buttonRegistracija].onAction
	@FXML
	public void goToRepozitorijum(ActionEvent event) {
		napravisertifikat();
		
		boolean result = registracija();
		if(result) {
			try {
				buttonRegistracija.getScene().getWindow().hide();
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PorukaRegistracija.fxml")); 
				
		        Stage stage = new Stage();
		        stage.initOwner(buttonRegistracija.getScene().getWindow());
		        stage.setScene(new Scene((Parent) loader.load()));
		        PorukaRegistracijaController controller = loader.getController();
		        controller.setFields(textField_KorisnickoIme.getText(), passwordField_Lozinka.getText());
		        stage.showAndWait();

				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				Stage greska = new Stage();
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("GreskaRegistracija.fxml"));
			Scene scene = new Scene(root);
			
			greska.setScene(scene);
			greska.show();
			greska.setResizable(false);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	boolean registracija() {
		
		boolean result = Funkcije.vecSeNalazi(textField_KorisnickoIme.getText(), passwordField_Lozinka.getText());
		if(result) return false;
		else {
			try {
				
				String command1[]= {Path.OpenSSLPath, "passwd", "-1", "-salt", "12345678", passwordField_Lozinka.getText()};
				
				String passwordHash = CommandPrompt.executeReturn(command1, Path.OsnovniPath).substring(12);
				
				String info = textField_KorisnickoIme.getText() + "," + passwordHash;
				
				File f1 = new File(Path.KorisniciPath + "temp1.txt");
				FileWriter fw = new FileWriter(f1.getAbsolutePath());
				fw.write(info);
				fw.close();
				File f2 = new File(Path.KorisniciPath + "temp2.txt");
				
				String command2[]= {Path.OpenSSLPath, "enc", "-aes-256-cbc","-base64", "-in", f1.getAbsolutePath(), "-out",
						f2.getAbsolutePath(), "-pass" , "pass:" + passwordField_Lozinka.getText()};
				
				
				CommandPrompt.execute(command2, Path.KorisniciPath);
				
				
				FileInputStream fis = new FileInputStream(f2.getAbsolutePath());
				String cipher = new String(fis.readAllBytes(), "UTF8");
				
				
				
				fis.close();
				
				
				f1.delete();
				f2.delete();
				
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(Path.KorisniciPath + "Korisnici.txt", true)); 
				bw.append(cipher);
				bw.close();
				return true;
				}
			catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}
		
		
			
		 
		
	}
	
	void napravisertifikat() {
		try {
			
			String command1=Path.OpenSSLPath + " genrsa -out " + "private\\" + textField_KorisnickoIme.getText() + ".key 2048";
			CommandPrompt.execute(command1,Path.SertifikatiPath);
			
			String command2[]= {Path.OpenSSLPath,"req", "-new", "-key", "private\\" + textField_KorisnickoIme.getText() + ".key",  "-config",
					"openssl.cnf" , "-out",  "requests\\" + textField_KorisnickoIme.getText() + ".csr",
							"-subj", "\"/C=BA/ST=RS/L=BL/OU=ETF/CN=" + textField_KorisnickoIme.getText() + "\""};
			
			CommandPrompt.execute(command2, Path.SertifikatiPath);
			
			String command3[] = {Path.OpenSSLPath, "ca", "-batch","-config", "openssl.cnf", "-passin", "pass:sigurnost", "-out", "certs\\" + textField_KorisnickoIme.getText()
					+ ".crt", "-infiles", "requests\\" + textField_KorisnickoIme.getText() + ".csr"};
			
			CommandPrompt.execute(command3, Path.SertifikatiPath);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
        
	}
}
