package main;

import Parser.Input_Parser;
import Parser.ReadFile;

import java.io.File;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) throws Exception {
		/*	String input_file = "data/aiger-set/ascii/aig_0_min.aag";
			// Platform independent file path achieved by using File.separator
			//input_file = input_file.replaceAll("/", File.separator);
			input_file = input_file.replaceAll("//", File.separator);
		
			GraphWrapper graph = Input_Parser.Invoke_Parser(input_file);
			
			//##### Export Graph to BLIF FORMAT #####
			graph.exportToBLIF("unmodifiedGraph");
			
			//#### Export Graph to DOT Format and create PNG image. ####
			graph.exportToDOTandPNG("unmodifiedGraph");
			
			//#### Test conversion to MAJ ####
			graph.convertAIGtoMAJnodes();
			graph.exportToDOTandPNG("majGraph");
			graph.exportToBLIF("majGraph");
			graph.boolFunctions.Associativity(graph.internalGraph, graph.nodesMap);
			//graph.boolFunctions.DistributivityLR(graph.internalGraph, graph.nodesMap);
			//graph.boolFunctions.ComplementaryAssociativity(graph.internalGraph, graph.nodesMap);
			graph.exportToDOTandPNG("majGraph-assoc");
			graph.exportToBLIF("majGraph-assoc");
	
			//#### Perform Equivalence checks:
			//input file <-> created unmodified Graph
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("data/aiger-set/blif/aig_0_min.blif"), new File("output/unmodifiedGraph.blif"));
			//created unmodified Graph <-> MAJ Graph
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/majGraph.blif"), new File("output/unmodifiedGraph.blif"));
			//MAJ Graph <-> majGraph-assoc
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/majGraph.blif"), new File("output/majGraph-assoc.blif"));
			
			//ABC.Statistics.getStatistics(new File("data/aiger-set/blif/aig_0_min.blif"));
			//ABC.Statistics.getStatistics(new File("output/majGraph.blif"));	
	*/
			
			//### modified Fig.2.a example Graph
			GraphWrapper fig2a = new GraphWrapper();
			fig2a.addInputNode(2); //w
			fig2a.addInputNode(4); //x
			fig2a.addInputNode(6); //y
			fig2a.addInputNode(8); //z
			fig2a.addOutputNode(10); //h
			fig2a.addMajGate(20, 2, 4, 6);
			fig2a.addMajGate(14, 8, 5, 8);
			fig2a.addMajGate(18, 6, 9, 4);
			fig2a.addMajGate(16, 18, 8, 20);
			fig2a.addMajGate(10, 16, 4, 14);
			
			fig2a.exportToDOTandPNG("fig2a-mod");
			fig2a.exportToBLIF("fig2a-mod");
			
			//fig2a.boolFunctions.Associativity(fig2a.internalGraph, fig2a.nodesMap);
			//fig2a.boolFunctions.ComplementaryAssociativity(fig2a.internalGraph, fig2a.nodesMap);
			//fig2a.boolFunctions.Relevance(fig2a.internalGraph, fig2a.nodesMap);
			fig2a.boolFunctions.Majority(fig2a.internalGraph, fig2a.nodesMap);
			//fig2a.boolFunctions.DistributivityRL(fig2a.internalGraph, fig2a.nodesMap);
			
			fig2a.exportToDOTandPNG("fig2a-mod-assoc");
			fig2a.exportToBLIF("fig2a-mod-assoc");
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/fig2a-mod.blif"), new File("output/fig2a-mod-assoc.blif"));
	
	
			
			//### Fig.2.a example Graph
/*			GraphWrapper fig2a = new GraphWrapper();
			fig2a.addInputNode(2); //w
			fig2a.addInputNode(4); //x
			fig2a.addInputNode(6); //y
			fig2a.addInputNode(8); //z
			fig2a.addOutputNode(10); //h
			fig2a.addMajGate(14, 8, 4, 6);
			fig2a.addMajGate(12, 2, 4, 9);
			fig2a.addMajGate(10, 12, 4, 14);
			
			fig2a.exportToDOTandPNG("fig2a");
			fig2a.exportToBLIF("fig2a");
			
			fig2a.boolFunctions.Associativity(fig2a.internalGraph, fig2a.nodesMap);
			
			fig2a.exportToDOTandPNG("fig2a-assoc");
			fig2a.exportToBLIF("fig2a-assoc");
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/fig2a.blif"), new File("output/fig2a-assoc.blif"));
	
	*/	
		}
	
}
