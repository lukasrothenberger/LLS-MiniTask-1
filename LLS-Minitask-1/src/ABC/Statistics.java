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
	public static void printStatistics(File blifOne) {
		System.out.println("\t"+blifOne.getAbsolutePath());
		
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
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String abcExecutablePath : Settings.ABC.getABCExecutables()) {
			String[] c = {abcExecutablePath, "-f", "temp/tmp_statistics_script"};
			try {
				Process p = Runtime.getRuntime().exec(c);
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = input.readLine()) != null) {
					if(line.contains("TOTAL") || line.contains("Other") || line.contains("Inverter"))
						System.out.println(line);
				}
				input.close();
			} catch (IOException e) {
				continue;
			}
			return;
		}
	   System.out.println("ERROR: abc statistics generation could not be executed. Check if path to abc executable is contained in Settings.ABC.getABCExecutables()");
	}
}
