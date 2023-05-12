package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.TextArea;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RepozitorijumController {
	@FXML
	private Button button_Prikazi;
	@FXML
	private AnchorPane pane1;
	@FXML
	private TextField field_Fajl;
	
	@FXML
	private Button button_OK;
	@FXML
	private AnchorPane pane2;
	@FXML
	private TextArea field_Text;
	@FXML
	private Button buttonUnesi;
	private String ime;
	private String lozinka;
	@FXML
    private Label label_Ime;
	private int N;
	@FXML
    private ListView<Fajl> list;
	
	private ObservableList<Fajl> fajlovi;
	@FXML
	private void prikazi(ActionEvent event) {
		List<File> sviFajloviKorisnika = new ArrayList<File>();
		fajlovi = FXCollections.observableArrayList();
		for(int i = 1; i <= 4; i++) {
			File folder = new File(Path.RepozitorijumPath + i);
			for(File fileEntry : folder.listFiles()) {
				if(fileEntry.getName().startsWith(ime))
					sviFajloviKorisnika.add(fileEntry);
			}
		}
		HashMap<String, ArrayList<Fajl>> mapa = new HashMap<String, ArrayList<Fajl>>();
		for (File file : sviFajloviKorisnika) {
			String filename[] = file.getName().split("_");
			ArrayList<Fajl> fajlovi = new ArrayList<Fajl>();
			if(mapa.get(filename[1])!=null) {
				for(Fajl f : mapa.get(filename[1]))
				fajlovi.add(f);
			}
			
			
			Fajl noviFajl = new Fajl();
			noviFajl.setImeFajla(filename[1]);
			noviFajl.setRedniBroj(Integer.parseInt(filename[2].replaceAll(".txt", "")));
			try {
				FileInputStream fis = new FileInputStream(file);
				String dekrip = dekriptuj(new String(fis.readAllBytes(),"UTF8"));
				noviFajl.setTekst(dekrip);
				fis.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			fajlovi.add(noviFajl);
			Collections.sort(fajlovi, new Comparator<Fajl>() {
				@Override
				public int compare(Fajl f1, Fajl f2) {
					return f1.getRedniBroj() - f2.getRedniBroj();
				}
			});
			mapa.put(noviFajl.getImeFajla(), fajlovi);
			
		}
		
		for (HashMap.Entry<String, ArrayList<Fajl>> entry : mapa.entrySet()) {
		    String key = entry.getKey();
		    ArrayList<Fajl> list = entry.getValue();
		    String tekst="";
		    for(Fajl f : list)
		    	tekst += f.getTekst();
		    Fajl noviFajl = new Fajl();
		    noviFajl.setImeFajla(key);
		    noviFajl.setTekst(tekst);
		    noviFajl.setImeKorisika(ime);
		    boolean verify = provjeriPotpis(noviFajl);
		    if(verify == false)
		    	{
		    	noviFajl.setTekst("Desila se neovlaštena izmjena fajla!");
		    	noviFajl.setImeFajla(key + " - ERROR");
		    	}
		    this.fajlovi.add(noviFajl);
		}
		
		list.setItems(this.fajlovi);
		list.setCellFactory(fajlListView -> new FajlCell());
		
		
	}
	
	private class FajlCell extends ListCell<Fajl>{
		
		@FXML
	    private Label label_ImeFoldera;
		@FXML
	    private Button button_Otvori;
	    @FXML
	    private AnchorPane pane;

	    
	    @Override
	    protected void updateItem(Fajl fajl, boolean empty) {
	    	super.updateItem(fajl, empty);
	    	
	    	if(empty || fajl == null) {

	            setText(null);
	            setGraphic(null);

	        }
	    	
	    	else {
	    		FXMLLoader mLLoader = new FXMLLoader(getClass().getResource("/application/cell/FileCell.fxml"));
                mLLoader.setController(this);

                try {
                	mLLoader.load();
                } 
                catch (IOException e) {
                e.printStackTrace();
                }
                
                label_ImeFoldera.setText(String.valueOf(fajl.getImeFajla()));
                
                button_Otvori.setOnAction(e -> {
    	    		try {
    	    		
    	    			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PrikazFajla.fxml")); 
    	    			
    	    	        Stage stage = new Stage();
    	    	        stage.initOwner(button_Otvori.getScene().getWindow());
    	    	        stage.setScene(new Scene((Parent) loader.load()));
    	    	        PrikazFajlaController controller = loader.getController();
    	    	        controller.setFields(fajl.getImeFajla() + ".txt",fajl.getTekst());
    	    	        
    	    	        stage.showAndWait();
    	    		} catch(Exception ex) {
    	    			
    	    		}
                });
                
    	    	setText(null);
    	    	setGraphic(pane);
                }
	    	}
	    }
	
	private boolean provjeriPotpis(Fajl fajl) {
		try {
			File f1 = new File(Path.RepozitorijumPath + "potpisi\\temp1.txt");
			File f2 = new File(Path.RepozitorijumPath + "potpisi\\" + fajl.getImeKorisika() + "_" + fajl.getImeFajla() + ".txt");
			FileWriter fw = new FileWriter(f1.getAbsolutePath());
			fw.write(fajl.getTekst());
			fw.close();
			String[] command = {Path.OpenSSLPath, "dgst", "-sha256", "-prverify", Path.SertifikatiPath + "private\\"
					+ ime + ".key", "-signature", f2.getAbsolutePath(), f1.getAbsolutePath()};
			
			String result = CommandPrompt.executeReturn(command, Path.OsnovniPath);
			
			f1.delete();
			if(result.equals("Verified OK\n"))
				return true;
			else if(result.equals("Verification Failure\n"))
				return false;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	private String dekriptuj(String text) {
		try {
			File f1 = new File(Path.RepozitorijumPath + "temp1.txt");
			FileWriter fw = new FileWriter(f1.getAbsolutePath());
			fw.write(text);
			fw.close();
			f1.setReadable(true);
			File f2 = new File(Path.RepozitorijumPath + "temp2.txt");
				
			String command1[]= {Path.OpenSSLPath, "enc", "-aes-256-cbc", "-d", "-base64", "-in", f1.getAbsolutePath(), "-out",
					f2.getAbsolutePath(), "-pass" , "pass:" + lozinka};
			
			CommandPrompt.execute(command1, Path.RepozitorijumPath);
				
			FileInputStream fis = new FileInputStream(f2.getAbsolutePath());
			String deciphered = new String(fis.readAllBytes(), "UTF8");
			
			fis.close();
			f1.delete();
			f2.delete();
			return deciphered;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setFields(String ime, String lozinka) {
		this.ime=ime;
		label_Ime.setText("Repozitorijum korisnika " + ime);
		this.lozinka = lozinka;
	}

	// Event Listener on Button[#button_OK].onAction
	@FXML
	public void goToPane2(ActionEvent event) {
		pane1.setVisible(false);
		pane2.setVisible(true);
	}
	// Event Listener on Button[#buttonUnesi].onAction
	@FXML
	public void napraviFajl(ActionEvent event) {
		pane1.setVisible(true);
		pane2.setVisible(false);
		Random rand = new Random();
		N = rand.nextInt(10) + 4;
		try {
			
			dodajPotpis(ime, field_Fajl.getText(), field_Text.getText());
			byte[] data = field_Text.getText().getBytes("UTF8");
			int blockSize = data.length / N;
			int blockCount = (data.length + blockSize - 1) / blockSize;

			byte[] range = null;
			List<byte[]> result = new ArrayList<byte[]>();

			for (int i = 1; i < blockCount; i++) {
					int idx = (i - 1) * blockSize;
					range = Arrays.copyOfRange(data, idx, idx + blockSize);
					result.add(range);
			}

			int end = -1;
			if (data.length % blockSize == 0) {
					end = data.length;
			} else {
					end = data.length % blockSize + blockSize * (blockCount - 1);
			}
					
			range = Arrays.copyOfRange(data, (blockCount - 1) * blockSize, end);
			result.add(range);
			
			int i, j;
			for(i = 1, j = 0; j < result.size(); i++,j++) {
				if (i == 5) i = 1;
				
				File f1 = new File(Path.RepozitorijumPath + "temp1.txt");
				FileWriter fw = new FileWriter(f1.getAbsolutePath());
				fw.write(new String(result.get(j), "UTF8"));
				
				fw.close();
				String filename = i + "\\" + ime + "_" + field_Fajl.getText() + "_" + j + ".txt";
				File newFile = new File(Path.RepozitorijumPath + filename);
				
				String command1[]= {Path.OpenSSLPath, "enc", "-aes-256-cbc","-base64", "-in", f1.getAbsolutePath(), "-out",
						newFile.getAbsolutePath(), "-pass" , "pass:" + lozinka};
				
				
				CommandPrompt.execute(command1, Path.RepozitorijumPath);
			}
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		field_Text.setText("");
		field_Fajl.setText("");
		
	}
	
	public void dodajPotpis(String ime, String fajl, String tekst) {
		try {
			File f1 = new File(Path.RepozitorijumPath + "potpisi\\temp1.txt");
			File f2 = new File(Path.RepozitorijumPath + "potpisi\\" + ime + "_" + fajl + ".txt");
			FileWriter fw = new FileWriter(f1.getAbsolutePath());
			fw.write(tekst);
			fw.close();
			String[] command = {Path.OpenSSLPath, "dgst", "-sha256", "-sign", Path.SertifikatiPath + "private\\"
					+ ime + ".key", "-out", f2.getAbsolutePath(), f1.getAbsolutePath()};
			
			System.out.println();
			CommandPrompt.execute(command, Path.OsnovniPath);
			
			f1.delete();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
