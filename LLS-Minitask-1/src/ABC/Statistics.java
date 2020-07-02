/*
 * Authors: Lukas Rothenberger, Pallavi Gutta Ravi
 */

package ABC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sun.net.httpserver.Authenticator.Result;

public class Statistics {

	/**
	 * Retrieve statistics from ABC for given BLIF file.
	 * Prints the results to the console and returns them as a String.
	 * @param blifOne File Object representing the first BLIF file.
	 */
	public static String printStatistics(File blifOne, boolean printTotalOnly, boolean printFileName, boolean printToConsole) {
		String returnString = "";

		if(printFileName) {
			System.out.println("\t"+blifOne.getAbsolutePath());
		}
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
					if(printTotalOnly) {
						if(line.contains("TOTAL")) {
							if(printToConsole)
								System.out.println(line);
							returnString += line+"\n";
						}
					}
					else {
						if(line.contains("TOTAL") || line.contains("Other") || line.contains("Inverter")) {
							if(printToConsole)
								System.out.println(line);
							returnString += line+"\n";
						}
					}
				}
				input.close();
			} catch (IOException e) {
				continue;
			}
			return returnString;
		}
		System.out.println("ERROR: abc statistics generation could not be executed. Check if path to abc executable is contained in Settings.ABC.getABCExecutables()");
		return "ERROR";
	}

	/**
	 * returns an array of the following form: [InverterCount, OtherCount, TotalCount].
	 * @param blifOne
	 * @return
	 */
	public static int[] getIntegerStatistics(File blifOne) {
		int[] returnList = new int[3];
		String statisticsString = printStatistics(blifOne, false, false, false);
		int outerCount = 0; 
		for(Object obj : statisticsString.lines().toArray()) {
			String line = (String) obj;
			int innerCount = 0;
			for(String s : line.split(" ")) {
				//filter out empty Strings
				if(s.equals(""))
					continue;
				if(innerCount == 2) {
					returnList[outerCount] = Integer.parseInt(s);
					break;
				}
				innerCount++;
			}
			outerCount++;
		}	
		return returnList;
	}
}
