package main;

import Parser.Input_Parser;
import Parser.ReadFile;

import java.io.File;

import org.jgrapht.util.DoublyLinkedList;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) throws Exception {
	/*		String input_file = "data/aiger-set/ascii/aig_1_min.aag";
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
			
	//		graph.boolFunctions.Relevance(graph.internalGraph, graph.nodesMap, 0);
	//		graph.exportToDOTandPNG("post_Relevance");
			
		
		//	for(int i = 0; i < 3; i++) {
				System.out.println("#### " + graph.internalGraph.hashCode()+" #### 0");
				graph.boolFunctions.Majority(graph.internalGraph, graph.nodesMap, 0);
				System.out.println("#### " + graph.internalGraph.hashCode()+" #### 1");
				graph.boolFunctions.DistributivityRL(graph.internalGraph, graph.nodesMap, 0);
				System.out.println("#### " + graph.internalGraph.hashCode()+" #### 2");
		//		graph.boolFunctions.Associativity(graph.internalGraph, graph.nodesMap, 0);
		//		graph.boolFunctions.ComplementaryAssociativity(graph.internalGraph, graph.nodesMap, 0);
		//		graph.boolFunctions.Relevance(graph.internalGraph, graph.nodesMap, 0);
		//		graph.boolFunctions.Substitution(graph.internalGraph, graph.nodesMap, 0);
		//		graph.exportToDOTandPNG("graph_visible_changes");
		//		graph.boolFunctions.Majority(graph.internalGraph, graph.nodesMap, 0);
		//		graph.boolFunctions.DistributivityRL(graph.internalGraph, graph.nodesMap, 0);
		//	}
				
			graph.exportToDOTandPNG("majGraph-assoc");
			graph.exportToBLIF("majGraph-assoc");
	
			//#### Perform Equivalence checks:
			//input file <-> created unmodified Graph
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("data/aiger-set/blif/aig_1_min.blif"), new File("output/unmodifiedGraph.blif"));
			//created unmodified Graph <-> MAJ Graph
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/majGraph.blif"), new File("output/unmodifiedGraph.blif"));
			//MAJ Graph <-> majGraph-assoc
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/majGraph.blif"), new File("output/majGraph-assoc.blif"));
			
			//ABC.Statistics.getStatistics(new File("data/aiger-set/blif/aig_0_min.blif"));
			//ABC.Statistics.getStatistics(new File("output/majGraph.blif"));	
	*/
			
			//### modified Fig.2.a example Graph
		GraphWrapper fig2a_mod = new GraphWrapper();
			fig2a_mod.addInputNode(2); //w
			fig2a_mod.addInputNode(4); //x
			fig2a_mod.addInputNode(6); //y
			fig2a_mod.addInputNode(8); //z
			fig2a_mod.addOutputNode(10); //h
			fig2a_mod.addMajGate(20, 2, 0, 6);
			fig2a_mod.addMajGate(14, 8, 4, 4);
			fig2a_mod.addMajGate(18, 6, 0, 2);
			fig2a_mod.addMajGate(16, 18, 8, 20);
			fig2a_mod.addMajGate(10, 16, 4, 14);
			
			fig2a_mod.exportToDOTandPNG("fig2a_mod");
			fig2a_mod.exportToBLIF("fig2a_mod");
			
		//	fig2a_mod.boolFunctions.Majority(fig2a_mod.internalGraph, fig2a_mod.nodesMap,0);
		//	fig2a_mod.exportToDOTandPNG("post_maj");
		//	fig2a_mod.boolFunctions.Associativity(fig2a_mod.internalGraph, fig2a_mod.nodesMap,0);
		//	fig2a_mod.exportToDOTandPNG("post_assoc");
		//	fig2a_mod.boolFunctions.ComplementaryAssociativity(fig2a_mod.internalGraph, fig2a_mod.nodesMap,0);
		//	fig2a_mod.exportToDOTandPNG("post_CA");
		//	fig2a_mod.boolFunctions.Relevance(fig2a_mod.internalGraph, fig2a_mod.nodesMap);
		//	fig2a_mod.boolFunctions.DistributivityRL(fig2a_mod.internalGraph, fig2a_mod.nodesMap,0);
		//	fig2a_mod.Remove_UnReachableNodes();
		//	fig2a_mod.boolFunctions.Substitution(fig2a_mod.internalGraph, fig2a_mod.nodesMap, 0);
			
		//	for(int i = 0; i < 3; i++) {
				fig2a_mod = fig2a_mod.boolFunctions.Majority(0);
				fig2a_mod = fig2a_mod.boolFunctions.DistributivityRL(0);
				//fig2a_mod.Remove_UnReachableNodes();
				//fig2a_mod.exportToDOTandPNG("fig2a_visible_changes");
				fig2a_mod = fig2a_mod.boolFunctions.Associativity(0);
				fig2a_mod = fig2a_mod.boolFunctions.ComplementaryAssociativity(0);
				fig2a_mod = fig2a_mod.boolFunctions.Relevance(0);
				fig2a_mod = fig2a_mod.boolFunctions.Substitution(0);
				fig2a_mod = fig2a_mod.boolFunctions.Majority(0);
				fig2a_mod = fig2a_mod.boolFunctions.DistributivityRL(0);
				fig2a_mod = fig2a_mod.boolFunctions.Majority(0);
				//fig2a_mod.Remove_UnReachableNodes();
			//}
			
	//		fig2a_mod.boolFunctions.Substitution(fig2a_mod.internalGraph, fig2a_mod.nodesMap, 0);
			
			fig2a_mod.exportToDOTandPNG("fig2a_mod-assoc");
			for(Graph.Node n: fig2a_mod.internalGraph.vertexSet()) {
				System.out.println("outside: contained node: "+n.id);
			}
			fig2a_mod.exportToBLIF("fig2a_mod-assoc");
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/fig2a_mod.blif"), new File("output/fig2a_mod-assoc.blif"));
	
	
			
	/*		//### Fig.2.a example Graph
			GraphWrapper fig2a = new GraphWrapper();
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
			
			//fig2a.replaceInSubtree(12, 8, 6);
			//fig2a.redirectEdge(14, 4, 10);
			
			//fig2a.boolFunctions.Majority(fig2a.internalGraph, fig2a.nodesMap);
			//fig2a.boolFunctions.Associativity(fig2a.internalGraph, fig2a.nodesMap);
			//fig2a.boolFunctions.Relevance(fig2a.internalGraph, fig2a.nodesMap);
			fig2a.boolFunctions.DistributivityRL(fig2a.internalGraph, fig2a.nodesMap);
			
			
			fig2a.exportToDOTandPNG("fig2a-assoc");
			fig2a.exportToBLIF("fig2a-assoc");
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/fig2a.blif"), new File("output/fig2a-assoc.blif"));
		
		*/	
		}
	
}
