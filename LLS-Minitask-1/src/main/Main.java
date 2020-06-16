package main;

import Parser.Input_Parser;
import Parser.ReadFile;

import java.io.File;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String input_file = "data/aiger-set/ascii/aig_0_min.aag";
		// Platform independent file path achieved by using File.separator
		input_file = input_file.replaceAll("/", File.separator);
		
		GraphWrapper graph = Input_Parser.Invoke_Parser(input_file);
		
		//##### Export Graph to BLIF FORMAT #####
		graph.exportToBLIF("unmodifiedGraph");
		
		//#### Export Graph to DOT Format and create PNG image. ####
		graph.exportToDOTandPNG("unmodifiedGraph");
		
		//#### Test conversion to MAJ ####
		graph.convertAIGtoMAJnodes();
		graph.exportToDOTandPNG("majGraph");
		graph.exportToBLIF("majGraph");

		//#### Perform Equivalence checks:
		//input file <-> created unmodified Graph
		ABC.EquivalenceCheck.performEquivalenceCheck(new File("data/aiger-set/blif/aig_0_min.blif"), new File("output/unmodifiedGraph.blif"));
		//created unmodified Graph <-> MAJ Graph
		ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/majGraph.blif"), new File("output/unmodifiedGraph.blif"));
		
		ABC.Statistics.getStatistics(new File("data/aiger-set/blif/aig_0_min.blif"));
		ABC.Statistics.getStatistics(new File("output/majGraph.blif"));
	}
	
}
