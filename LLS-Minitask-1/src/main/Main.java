package main;

import Parser.Input_Parser;
import Parser.ReadFile;

import java.io.File;

import org.jgrapht.util.DoublyLinkedList;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) throws Exception {
			String input_file = "data/aiger-set/ascii/aig_0_min.aag";
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
			
			int effort = 5;
		
			for(int i = 0; i < effort; i++) {
				graph = graph.boolFunctions.Majority(0);
				graph.exportToBLIF("post_maj_1"+"_"+i);
		//		graph.exportToDOTandPNG("majGraph-assoc");
				graph = graph.boolFunctions.DistributivityRL(0);
				graph.exportToBLIF("post_dist_1"+"_"+i);
		//		graph.exportToDOTandPNG("majGraph-assoc");
				graph = graph.boolFunctions.Associativity(0);
				graph.exportToBLIF("post_assoc"+"_"+i);
		//		graph.exportToDOTandPNG("majGraph-assoc");
				graph = graph.boolFunctions.ComplementaryAssociativity(0);
				graph.exportToBLIF("post_compAssoc"+"_"+i);
		//		graph.exportToDOTandPNG("majGraph-assoc");
				graph = graph.boolFunctions.Relevance(0);
				graph.exportToBLIF("post_relev"+"_"+i);
		//		graph.exportToDOTandPNG("majGraph-assoc");
				graph = graph.boolFunctions.InverterPropagationLR(0);
				graph.exportToBLIF("post_invProp"+"_"+i);
				graph.exportToDOTandPNG("pre-trivRep");
				graph = graph.boolFunctions.TrivialReplacements(0);
				graph.exportToBLIF("post_trivRep"+"_"+i);
				graph.exportToDOTandPNG("post-trivRep");
		//		graph.exportToDOTandPNG("majGraph-assoc");
				graph = graph.boolFunctions.Substitution( 0);
				graph.exportToBLIF("post_subst"+"_"+i);
				graph = graph.boolFunctions.Majority(0);
				graph.exportToBLIF("post_maj_2"+"_"+i);
		//		graph.exportToDOTandPNG("majGraph-assoc");
				graph = graph.boolFunctions.DistributivityRL(0);
				graph.exportToBLIF("post_dist_2"+"_"+i);
		//		graph.exportToDOTandPNG("majGraph-assoc");
			}
			
//			graph = graph.boolFunctions.Substitution(0);
			
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
			ABC.Statistics.printStatistics(new File("output/majGraph.blif"), false, true);
			for(int i = 0 ; i < effort; i++ ) {	
				System.out.println("### Iteration "+i+" ###");
				ABC.Statistics.printStatistics(new File("output/post_maj_1"+"_"+i+".blif"), true, false);	
				ABC.Statistics.printStatistics(new File("output/post_dist_1"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_assoc"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_compAssoc"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_relev"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_invProp"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_trivRep"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_subst"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_maj_2"+"_"+i+".blif"), true, false);
				ABC.Statistics.printStatistics(new File("output/post_dist_2"+"_"+i+".blif"), true, false);
			}
			ABC.Statistics.printStatistics(new File("output/majGraph-assoc.blif"), false, true);
		
			
			//### modified Fig.2.a example Graph
		GraphWrapper fig2a_mod = new GraphWrapper();
/*			fig2a_mod.addInputNode(2); //w
			fig2a_mod.addInputNode(4); //x
			fig2a_mod.addInputNode(6); //y
			fig2a_mod.addInputNode(8); //z
			fig2a_mod.addOutputNode(10); //h
			fig2a_mod.addMajGate(20, 6, 0, 6);
			fig2a_mod.addMajGate(14, 8, 4, 4);
			fig2a_mod.addMajGate(18, 6, 0, 2);
			fig2a_mod.addMajGate(16, 18, 8, 20);
			fig2a_mod.addMajGate(10, 17, 4, 14);
			
			fig2a_mod.exportToDOTandPNG("fig2a_mod");
			fig2a_mod.exportToBLIF("fig2a_mod");
			
		//	for(int i = 0; i < 3; i++) {
		//		fig2a_mod = fig2a_mod.boolFunctions.Majority(0);
		//		fig2a_mod = fig2a_mod.boolFunctions.DistributivityRL(0);
				//fig2a_mod.Remove_UnReachableNodes();
				//fig2a_mod.exportToDOTandPNG("fig2a_visible_changes");
		//		fig2a_mod = fig2a_mod.boolFunctions.Associativity(0);
		//		fig2a_mod = fig2a_mod.boolFunctions.ComplementaryAssociativity(0);
		//		fig2a_mod = fig2a_mod.boolFunctions.Relevance(0);
			//	fig2a_mod = fig2a_mod.boolFunctions.InverterPropagationLR(0);
				//fig2a_mod = fig2a_mod.boolFunctions.Substitution(0);
		//		fig2a_mod = fig2a_mod.boolFunctions.Majority(0);
		//		fig2a_mod = fig2a_mod.boolFunctions.DistributivityRL(0);
		//		fig2a_mod = fig2a_mod.boolFunctions.Majority(0);
				//fig2a_mod.Remove_UnReachableNodes();
			//}
			
			fig2a_mod = fig2a_mod.boolFunctions.InverterPropagationLR(0);
			fig2a_mod.exportToDOTandPNG("int");
			fig2a_mod = fig2a_mod.boolFunctions.TrivialReplacements(0);
			
	//		fig2a_mod.boolFunctions.Substitution(fig2a_mod.internalGraph, fig2a_mod.nodesMap, 0);
			
			fig2a_mod.exportToDOTandPNG("fig2a_mod-assoc");
			fig2a_mod.exportToBLIF("fig2a_mod-assoc");
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/fig2a_mod.blif"), new File("output/fig2a_mod-assoc.blif"));
	*/
			
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
