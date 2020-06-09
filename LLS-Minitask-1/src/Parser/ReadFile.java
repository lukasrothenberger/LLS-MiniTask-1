package Parser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadFile {

	private String Path;
	
	public ReadFile(String file_path) {
		Path = file_path;
	}
	
	public String[] OpenFile() throws IOException{
		FileReader fr = new FileReader(Path);
		BufferedReader br = new BufferedReader(fr);
		
		//Gives the number of Lines.
		int numberOfLines = readLines();
		String[ ] textData = new String[numberOfLines];
		String [] arr = new String[numberOfLines];
		int i;

		for (i=0; i < numberOfLines; i++) {
		arr[ i ] = br.readLine();
		//textData[i] = textData[i] + arr[i];
		//System.out.println(textData[i]);
		}
		br.close( );
		//return textData;
		return arr;

	}
//NUMBER OF LINES
	int readLines() throws IOException{
		FileReader ftr = new FileReader(Path);
		BufferedReader bf = new BufferedReader(ftr);
		
		String aLine;
		int numberofLines = 0;
		
		while (( aLine = bf.readLine()) != null) {
			numberofLines++;
		}
		bf.close();
		return numberofLines;  // returns number of lines
	}
}
	
