
import java.io.*;
import java.util.*;

public class Input_Parser {

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
		String str_data = null;
		String file_name = "C:\\Users\\Pallavi GR\\git\\LLS-MiniTask-1\\LLS-Minitask-1\\data\\aiger-set\\ascii\\aig_0_min.aag";
		
			
			StringBuilder sb = new StringBuilder();
		    String strLine = "";
		    List<String> list = new ArrayList<String>();
	  
			FileReader fr = new FileReader(file_name);
			BufferedReader br = new BufferedReader(fr);
			try {  		
	
			// converts the input to the contents of the array
			while (strLine != null )
	        {
				strLine = br.readLine();
                sb.append(strLine);
                sb.append(System.lineSeparator());
                strLine = br.readLine();
                if (strLine==null)
                   break;
                list.add(strLine);
            }
         System.out.println(Arrays.toString(list.toArray()));
	   } catch (FileNotFoundException e) {
	       System.err.println("File not found");
	   } catch (IOException e) {
	       System.err.println("Unable to read the file.");
	   }
			
			
		ReadFile file = new ReadFile(file_name);   //object of ReadFile
			String[] aryLines = file.OpenFile();
			int i;
			for(i=0;i<aryLines.length;i++) {		
				System.out.println(aryLines[i]);	
				}			
		}
}



