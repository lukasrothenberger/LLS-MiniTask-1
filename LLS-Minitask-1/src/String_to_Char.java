/*
 * Authors: Lukas Rothenberger, Pallavi Gutta Ravi
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class String_to_Char {

	public static void main(String[] args) throws FileNotFoundException {
		String file_name = "C:\\Users\\Pallavi GR\\git\\LLS-MiniTask-1\\LLS-Minitask-1\\data\\aiger-set\\ascii\\aig_0_min.aag";
		
		FileReader fr = new FileReader(file_name);
		BufferedReader br = new BufferedReader(fr);
		Scanner s = new Scanner(br);		
		StringBuilder sb = new StringBuilder();
		List<String> list = new ArrayList<String>();
		
			String strLine = null;
			try { 
			// converts the input to the contents of the array
			while (strLine != null )
	        {
				while (s.hasNextLine()) {
				list.add(s.nextLine());
				}
				String a = list.get(0);
				System.out.println(a);    
		char[] ch = a.toCharArray();    
		for(int i=0;i<ch.length;i++){    
		System.out.println("char at "+i+" index is: "+ch[i]);
		}
	}
	}finally {};
}
}
