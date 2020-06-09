
import java.io.*;
import java.util.*;

public class Input_Parser {

	public Input_Parser() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)throws IOException {
		/*try  
		{  
		//the file to be opened for reading  
		FileInputStream fis=new FileInputStream("C:\\Users\\Pallavi GR\\git\\LLS-MiniTask-1\\LLS-Minitask-1\\data\\aiger-set\\ascii\\aig_0_min.aag");       
		Scanner sc=new Scanner(fis);    //file to be scanned  
		//returns true if there is another line to read  
		while(sc.hasNextLine())  
		{  
		System.out.println(sc.nextLine());      //returns the line that was skipped  
		}  
		sc.close();     //closes the scanner  
		}  
		catch(IOException e)  
		{  
		e.printStackTrace();  
		}  */
		
		FileReader fr = new FileReader("C:\\Users\\Pallavi GR\\git\\LLS-MiniTask-1\\LLS-Minitask-1\\data\\aiger-set\\ascii\\aig_0_min.aag");
		BufferedReader br = new BufferedReader(fr);
		//int numberOfLines = 3;
		String aLine;
		int numberoflines = 0;
		
		while((aLine = br.readLine())  != null) {
			numberoflines++;
		}
		String[ ] textData = new String[numberOfLines];
		int i;

		for (i=0; i < numberOfLines; i++) {
		textData[ i ] = br.readLine();

		}
		br.close( );

	}

}
