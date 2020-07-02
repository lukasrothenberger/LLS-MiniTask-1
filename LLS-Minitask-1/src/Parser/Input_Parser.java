/*
 * Authors: Lukas Rothenberger, Pallavi Gutta Ravi
 */

package Parser;

import java.io.*;
import java.util.*;

import Graph.GraphWrapper;

public class Input_Parser {

	@SuppressWarnings("null")
	public static GraphWrapper Invoke_Parser(String file_name)throws Exception {
		FileReader fr = new FileReader(file_name);
		BufferedReader br = new BufferedReader(fr);
		Scanner s = new Scanner(br);

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
		s.close();
		return graph;
	}
}




