/*
 * Authors: Lukas Rothenberger, Pallavi Gutta Ravi
 */

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
	 * @throws Exception 
	 */
	public static void performEquivalenceCheck(File blifOne, File blifTwo) throws Exception {;
	//build abc Script
	File tmp_eq_check_script = new File("temp/tmp_compare_script");
	if(tmp_eq_check_script.exists())
		tmp_eq_check_script.delete();
	try {
		tmp_eq_check_script.createNewFile();
		FileWriter fw = new FileWriter(tmp_eq_check_script);
		fw.write("cec ");
		fw.write("\""+blifOne.getAbsolutePath()+"\" ");
		fw.write("\""+blifTwo.getAbsolutePath()+"\" ");
		fw.flush();
		fw.close();
	}
	catch (IOException e) {
		e.printStackTrace();
	}

	for(String abcExecutablePath : Settings.ABC.getABCExecutables()) {
		String[] c = {abcExecutablePath, "-f", "temp/tmp_compare_script"};
		boolean containsSuccessMessage = false;
		try {
			Process p = Runtime.getRuntime().exec(c);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				if(line.contains("Networks are equivalent"))
					containsSuccessMessage = true;
			}
			input.close();
		} catch (IOException e) {
			continue;
		}
		if(! containsSuccessMessage) {
			throw new Exception("Networks not equivalent! : ");
		}
		return;
	}
	System.out.println("ERROR: abc equivalence check could not be executed. Check if path to abc executable is contained in Settings.ABC.getABCExecutables()");
	}


	public static void performEquivalenceCheckWithConsolePrint(File blifOne, File blifTwo) throws Exception {;
	//build abc Script
	File tmp_eq_check_script = new File("temp/tmp_compare_script");
	if(tmp_eq_check_script.exists())
		tmp_eq_check_script.delete();
	try {
		tmp_eq_check_script.createNewFile();
		FileWriter fw = new FileWriter(tmp_eq_check_script);
		fw.write("cec ");
		fw.write("\""+blifOne.getAbsolutePath()+"\" ");
		fw.write("\""+blifTwo.getAbsolutePath()+"\" ");
		fw.flush();
		fw.close();
	}
	catch (IOException e) {
		e.printStackTrace();
	}

	for(String abcExecutablePath : Settings.ABC.getABCExecutables()) {
		String[] c = {abcExecutablePath, "-f", "temp/tmp_compare_script"};
		boolean containsSuccessMessage = false;
		try {
			Process p = Runtime.getRuntime().exec(c);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				if(line.contains("Networks are equivalent"))
					containsSuccessMessage = true;
			}
			input.close();
		} catch (IOException e) {
			continue;
		}
		if(! containsSuccessMessage) {
			System.out.println("Networks not equivalent!");
			System.out.println("\t"+blifOne.getAbsolutePath());
			System.out.println("\t"+blifTwo.getAbsolutePath()+"");
			throw new Exception("Networks not equivalent! : ");
		}
		return;
	}
	System.out.println("ERROR: abc equivalence check could not be executed. Check if path to abc executable is contained in Settings.ABC.getABCExecutables()");
	}
}
