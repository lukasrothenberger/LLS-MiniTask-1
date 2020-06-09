package Parser;

import java.io.*;
import java.util.*;

import it.unimi.dsi.fastutil.io.TextIO;
import Graph.GraphWrapper;

public class Input_Parser {

	@SuppressWarnings("null")
	public static GraphWrapper Invoke_Parser(String file_name)throws Exception {
		FileReader fr = new FileReader(file_name);
		BufferedReader br = new BufferedReader(fr);
		Scanner s = new Scanner(br);
		StringBuilder sb = new StringBuilder();
		String strLine = "";
		List<String> list = new ArrayList<String>();
		
			// converts the input to the contents of the array			
				String test = s.nextLine();
				String[] split = test.split(" ");
				
				int input = Integer.parseInt(split[2]);
				int latch = Integer.parseInt(split[3]);
				int output = Integer.parseInt(split[4]);
				int and_gate = Integer.parseInt(split[5]);
				int counter = 0;
				GraphWrapper graph = new GraphWrapper();
				latch = input + latch;
				output = latch + output;
				and_gate = output + and_gate;
				while (s.hasNextLine()) {
					if(counter < input) 
					{
						long ip = Long.parseLong(s.nextLine());
						graph.addInputNode(ip);
					}
					else if(counter >= latch && counter < output) {
						long op = Long.parseLong(s.nextLine());
						graph.addOutputNode(op);
					}
					else if(counter >= output && counter < and_gate) {
						String[] splt = s.nextLine().split(" ");
						long id = Long.parseLong(splt[0]);
						long c1 = Long.parseLong(splt[1]);
						long c2 = Long.parseLong(splt[2]);
						graph.addAndGate(id, c1, c2);
					}
					else {
						s.nextLine();
					}
					
					counter++;						
				}
				return graph;
				//System.out.println(graph.toBLIFFormat());
				//System.out.println(a);   //// gives the first line as string
				//char[] ch = a.toCharArray();  ///convert to char array
				//int l = ch.length;
				//String[] split = a.split(" ");				
		//		System.out.println(input);
		//		for(int i=0;i<split.length;i++){    
		//			System.out.println(" At index " +i+" is: " + split[i]);
		//			}
			//	String[] yourArray = sb.toString().split(" ");---not used
				//read the next line or so--- not sure!!!
			/*	strLine = br.readLine();
                sb.append(strLine);
                sb.append(System.lineSeparator());
                strLine = br.readLine();
                if (strLine==null)
                break;*/
            
/*			System.out.println(Arrays.toString(list.toArray()));
			s.close();
			
			
		ReadFile file = new ReadFile(file_name);   //object of ReadFile
			String[] aryLines = file.OpenFile();
			int i;
			for(i=0;i<aryLines.length;i++)
			{	
				if (i<7) 
					System.out.println(aryLines[i]);
				//else if (i==7) 
				//	System.out.println(aryLines[i]);
				//else if (i>7 && i<22) 
					//System.out.println(aryLines[i]);
			}
			
		}
*/
}
}
		



