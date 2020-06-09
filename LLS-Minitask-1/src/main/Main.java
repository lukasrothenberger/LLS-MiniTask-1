package main;

import Parser.Input_Parser;
import Parser.ReadFile;

import java.io.File;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) throws Exception {
		
		Input_Parser.Invoke_Parser();
		GraphWrapper graph = new GraphWrapper();
		
		// DEBUG (equivalent to aig_0_min.aag)
		graph.addInputNode(2);
		graph.addInputNode(4);
		graph.addInputNode(6);
		graph.addInputNode(8);
		graph.addInputNode(10);
		graph.addInputNode(12);

		graph.addOutputNode(40);
		
		graph.addAndGate(14, 13, 6);
		graph.addAndGate(16, 12, 9);
		graph.addAndGate(18, 16, 7);
		graph.addAndGate(20, 19, 15);
		graph.addAndGate(22, 21, 11);
		graph.addAndGate(24, 17, 6);
		graph.addAndGate(26, 13, 8);
		graph.addAndGate(28, 11, 7);
		graph.addAndGate(30, 28, 27);
		graph.addAndGate(32, 25, 19);
		graph.addAndGate(34, 32, 31);
		graph.addAndGate(36, 35, 23);
		graph.addAndGate(38, 5, 3);
		graph.addAndGate(40, 38, 37);
		
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
		ABC.EquivalenceCheck.performEquivalenceCheck(new File("data/aiger-set/aig_0_min.blif"), new File("output/unmodifiedGraph.blif"));
		//created unmodified Graph <-> MAJ Graph
		ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/majGraph.blif"), new File("output/unmodifiedGraph.blif"));
	}
	
}
