package ABC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Statistics {
	
	/**
	 * Retrieve statistics from ABC for given BLIF file.
	 * Prints the results to the console and returns them as a String.
	 * @param blifOne File Object representing the first BLIF file.
	 */
	public static void getStatistics(File blifOne) {
		System.out.println("Retrieving Statistics for:");
		System.out.println("\t"+blifOne.getAbsolutePath()+"\n");
		
		//build abc Script
		File tmp_statistics_script = new File("temp/tmp_statistics_script");
		if(tmp_statistics_script.exists())
			tmp_statistics_script.delete();
		try {
			tmp_statistics_script.createNewFile();
			FileWriter fw = new FileWriter(tmp_statistics_script);
			fw.write("read_blif "+blifOne.getAbsolutePath()+"\n");
			fw.write("print_gates\n");
			fw.write("print_level\n");
			fw.flush();
			fw.close();
			System.out.println("\t\tcreated tmp_statistics_script for abc statistics generation.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String abcExecutablePath : Settings.ABC.getABCExecutables()) {
			String[] c = {abcExecutablePath, "-f", "temp/tmp_statistics_script"};
			try {
				System.out.println("ABC Statistics for "+blifOne.getAbsolutePath()+ ":");
				Process p = Runtime.getRuntime().exec(c);
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = input.readLine()) != null) {
				  System.out.println(line);
				}
				input.close();
			} catch (IOException e) {
				continue;
			}
			System.out.println("\tDone.");
			return;
		}
	   System.out.println("ERROR: abc statistics generation could not be executed. Check if path to abc executable is contained in Settings.ABC.getABCExecutables()");
	}
}
