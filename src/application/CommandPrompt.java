package application;

import java.io.File;

public class CommandPrompt {
	public static void execute(String command, String path) {
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
			builder.directory(new File(path));
			
			Process p = builder.start();

			p.waitFor();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void execute(String command[], String path) {
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c");
			builder.command(command);
			builder.directory(new File(path));
			
			Process p = builder.start();

			p.waitFor();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static String executeReturn(String command[], String path) {
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c");
			builder.command(command);
			builder.directory(new File(path));
			
			Process p = builder.start();

			p.waitFor();
			String rjesenje = new String(p.getInputStream().readAllBytes(), "UTF8");
			return rjesenje;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String executeError(String command[], String path) {
		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c");
			builder.command(command);
			builder.directory(new File(path));
			
			Process p = builder.start();

			p.waitFor();
			String error = new String(p.getErrorStream().readAllBytes(), "UTF8");
			return error;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
