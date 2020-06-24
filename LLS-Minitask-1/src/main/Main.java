package main;

import Parser.Input_Parser;
import Parser.ReadFile;

import java.io.File;
import java.io.FileWriter;

import org.jgrapht.util.DoublyLinkedList;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) throws Exception {
			// ### SETTINGS ###
			int[] input_files_list = {0};
			int effort = 10;
			int SubstitutionAfterUnsuccessfulIterations = 5;
			boolean createStatisticsCSV = true;
			int repeatStatisticsGenerationCount = 1;
			// ################
			
			if(! createStatisticsCSV) {
				repeatStatisticsGenerationCount = 1;
			}
			for(int input_file_number : input_files_list) {
				for(int statisticsIteration = 0; statisticsIteration < repeatStatisticsGenerationCount; statisticsIteration++) {
					String input_file = "data/aiger-set/ascii/aig_"+input_file_number+"_min.aag";
					// Platform independent file path achieved by using File.separator
					input_file = input_file.replaceAll("//", File.separator);
					GraphWrapper graph = null;
					
					graph = Input_Parser.Invoke_Parser(input_file);
					
					//### fig 2a 
					graph = new GraphWrapper();
					graph.addInputNode(2); //w
					graph.addInputNode(4); //x
					graph.addInputNode(6); //y
					graph.addInputNode(8); //z
					graph.addOutputNode(10); //h
					graph.addMajGate(14, 8, 4, 6);
					graph.addMajGate(12, 2, 4, 9);
					graph.addMajGate(10, 12, 4, 14);
					
					// ### end fig 2 a 
					
					//##### Export Graph to BLIF FORMAT #####
					graph.exportToBLIF("unmodifiedGraph");
					
					//#### Export Graph to DOT Format and create PNG image. ####
					graph.exportToDOTandPNG("unmodifiedGraph");
					
					//#### Convert AIG to MIG ####
					graph.convertAIGtoMAJnodes();
					graph.exportToDOTandPNG("majGraph");
					graph.exportToBLIF("majGraph");
					
	
					//#### Implementation of the Algorithm ####
					String lastStatisticsString = "";
					int unchangedStatisticsCount = 0;
					File statisticsCSVFile = new File("output/statistics-file-"+input_file_number+"-iteration-"+statisticsIteration+".csv");
					FileWriter statisticsWriter = null;
					if(createStatisticsCSV) {
						if(statisticsCSVFile.exists()) {
							statisticsCSVFile.delete();
						}
						statisticsCSVFile.createNewFile();
						statisticsWriter = new FileWriter(statisticsCSVFile);
						String columnNames = "Iteration,Step,Inverter,Other,Total\n";
						statisticsWriter.write(columnNames);
						int[] startStatistics = ABC.Statistics.getIntegerStatistics(new File("output/majGraph.blif"));
						statisticsWriter.write("0,None,"+startStatistics[0]+","+startStatistics[1]+","+startStatistics[2]+"\n");
					}
					//#### Actual Algorithm ####
					for(int i = 1; i < effort+1; i++) {
						System.out.println("##### Iteration: "+i+" #####");
						System.out.println("\t1");
						graph = graph.boolFunctions.InverterPropagationLR(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(""+i+",InvProp,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t2");
						graph = graph.boolFunctions.TrivialReplacements(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",TrivRep,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t3");
						graph = graph.boolFunctions.Majority(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",Maj-1,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t4");
						graph = graph.boolFunctions.DistributivityRL(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",Dist-1,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t5");
						graph = graph.boolFunctions.Associativity(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",Assoc,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t6");
						graph = graph.boolFunctions.ComplementaryAssociativity(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",CompAss,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t7");
						graph = graph.boolFunctions.Relevance(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",Relev,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						if(unchangedStatisticsCount >= SubstitutionAfterUnsuccessfulIterations) {
							System.out.println("\t8");
							graph = graph.boolFunctions.Substitution(0);
						}
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",Subst,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t9");
						graph = graph.boolFunctions.Majority(0);
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",Maj-2,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}
						System.out.println("\t10");
						graph = graph.boolFunctions.DistributivityRL(0);
						graph.Remove_UnReachableNodes();
						if(createStatisticsCSV) {
							graph.exportToBLIF("stats");
							int[] stats = ABC.Statistics.getIntegerStatistics(new File("output/stats.blif"));
							statisticsWriter.write(",Dist-2,"+stats[0]+","+stats[1]+","+stats[2]+"\n");
						}			
						//generate and handle statistics for local minimum escaping
						graph.exportToBLIF("intermediate-statistics");
						String statisticsResult = ABC.Statistics.printStatistics(new File("output/intermediate-statistics.blif"), false, false, false);
						if(statisticsResult.equals(lastStatisticsString)) {
							unchangedStatisticsCount++;
						}
						else {
							unchangedStatisticsCount = 0;
						}
						lastStatisticsString = statisticsResult;
						if(createStatisticsCSV) {
							statisticsWriter.flush();
						}
					}
					//#### End of Actual Algorithm
					if(createStatisticsCSV) {
						statisticsWriter.flush();
						statisticsWriter.close();
					}
					
					ABC.Statistics.getIntegerStatistics(new File("output/intermediate-statistics.blif"));
					
					graph.exportToDOTandPNG("majGraph-assoc");
					graph.exportToBLIF("majGraph-assoc");
			
					//#### Perform Equivalence checks:
					//input file <-> created unmodified Graph
					ABC.EquivalenceCheck.performEquivalenceCheckWithConsolePrint(new File("data/aiger-set/blif/aig_"+input_file_number+"_min.blif"), new File("output/unmodifiedGraph.blif"));
					//created unmodified Graph <-> MAJ Graph
					ABC.EquivalenceCheck.performEquivalenceCheckWithConsolePrint(new File("output/majGraph.blif"), new File("output/unmodifiedGraph.blif"));
					//MAJ Graph <-> majGraph-assoc
					ABC.EquivalenceCheck.performEquivalenceCheckWithConsolePrint(new File("output/majGraph.blif"), new File("output/majGraph-assoc.blif"));
					
					ABC.Statistics.printStatistics(new File("data/aiger-set/blif/aig_"+input_file_number+"_min.blif"), false, true, true);
					ABC.Statistics.printStatistics(new File("output/majGraph.blif"), false, true, true);
					ABC.Statistics.printStatistics(new File("output/majGraph-assoc.blif"), false, true, true);
				}
			}
	
			
			//### modified Fig.2.a example Graph
	/*	GraphWrapper fig2a_mod = new GraphWrapper();
			fig2a_mod.addInputNode(2); //w
			fig2a_mod.addInputNode(4); //x
			fig2a_mod.addInputNode(6); //y
			fig2a_mod.addInputNode(8); //z
			fig2a_mod.addOutputNode(10); //h
			fig2a_mod.addMajGate(20, 6, 0, 8);
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
