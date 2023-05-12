package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class Funkcije {
	
	public static boolean vecSeNalazi(String ime, String lozinka) {
		boolean result = Funkcije.prijava(ime, lozinka);
		return result;
	}

	public static boolean prijava(String ime, String lozinka) {
		boolean sucess = false;
		try {
			String command1[]= {Path.OpenSSLPath, "passwd", "-1", "-salt", "12345678", lozinka};
				
			String passwordHash = CommandPrompt.executeReturn(command1, Path.OsnovniPath).substring(12);
				
			String info = ime + "," + passwordHash;
				
			BufferedReader br = new BufferedReader(new FileReader(Path.KorisniciPath + "Korisnici.txt"));
			String line = br.readLine();

			while (line != null) {
				File f1 = new File(Path.KorisniciPath + "temp1.txt");
				FileWriter fw = new FileWriter(f1.getAbsolutePath());
				fw.write(line);
				fw.close();
				f1.setReadable(true);
				File f2 = new File(Path.KorisniciPath + "temp2.txt");
					
				String command2[]= {Path.OpenSSLPath, "enc", "-aes-256-cbc", "-d", "-base64", "-in", f1.getAbsolutePath(), "-out",
						f2.getAbsolutePath(), "-pass" , "pass:" + lozinka, "-A"};
					
				CommandPrompt.execute(command2, Path.KorisniciPath);
					
				FileInputStream fis = new FileInputStream(f2.getAbsolutePath());
				String deciphered = new String(fis.readAllBytes(), "UTF8");
				
				fis.close();
				f1.delete();
				f2.delete();
				if (deciphered.equals(info))
				{
					sucess = true;
					break;
				}
				
				line=br.readLine();
			}

			br.close();
			} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return sucess;
	}
}
