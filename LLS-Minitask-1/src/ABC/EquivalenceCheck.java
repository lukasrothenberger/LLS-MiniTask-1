package ABC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class EquivalenceCheck {
	
	/**
	 * Perform abc equivalence check for the two given BLIF files.
	 * Prints the results to the console and returns them as a String.
	 * @param blifOne File Object representing the first BLIF file.
	 * @param blifTwo File Object representing the second BLIF file.
	 */
	public static void performEquivalenceCheck(File blifOne, File blifTwo) {
		System.out.println("Executing equivalence check for:");
		System.out.println("\t"+blifOne.getAbsolutePath());
		System.out.println("\t"+blifTwo.getAbsolutePath()+"");
		
		//build abc Script
		File tmp_eq_check_script = new File("temp/tmp_compare_script");
		if(tmp_eq_check_script.exists())
			tmp_eq_check_script.delete();
		try {
			tmp_eq_check_script.createNewFile();
			FileWriter fw = new FileWriter(tmp_eq_check_script);
			fw.write("cec ");
			fw.write(blifOne.getAbsolutePath()+" ");
			fw.write(blifTwo.getAbsolutePath()+" ");
			fw.flush();
			fw.close();
			System.out.println("\t\tcreated tmp_compare_script for abc equivalence checking.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		for(String abcExecutablePath : Settings.ABC.getABCExecutables()) {
			String[] c = {abcExecutablePath, "-f", "temp/tmp_compare_script"};
			try {
				System.out.println("Output of equivalence check:");
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
	   System.out.println("ERROR: abc equivalence check could not be executed. Check if path to abc executable is contained in Settings.ABC.getABCExecutables()");
	}
}
