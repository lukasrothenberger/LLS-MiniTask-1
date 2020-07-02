package main;

import Parser.Input_Parser;

import java.io.File;
import java.io.FileWriter;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) throws Exception {
		// ### SETTINGS ###
		int[] input_files_list = {0};  //which data/aiger-set/ascii files to use as input
		int effort = 3;  // amount of iterations to be done
		int SubstitutionAfterUnsuccessfulIterations = 20;  // threshold for local minimum detection
		boolean createStatisticsCSV = true;  
		int repeatStatisticsGenerationCount = 1;  // repeat process n times for statistics generation
		boolean exportPNGEndOfIteration = true;  // generate PNG images at the end of each iteration (gets overwritten)
		// ################

		//check if output and temp folder exist
		File directory = new File("output");
		if (! directory.exists()){
			directory.mkdir();
		}
		directory = new File("temp");
		if(! directory.exists()) {
			directory.mkdir();
		}


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

				/*					//### fig 2 a example graph 
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
				 */			

				/*					//#### Substitution example
					graph.exportToDOTandPNG("pre-subst");
					graph.exportToBLIF("pre-subst");
					ABC.Statistics.printStatistics(new File("output/pre-subst.blif"), false, false, true);
					graph = graph.boolFunctions.Substitution(0);
					graph.exportToDOTandPNG("post-subst");
					//### end Substitution example
				 */					

				//##### Export Graph to BLIF FORMAT #####
				graph.exportToBLIF("unmodifiedGraph");

				//#### Export Graph to DOT Format and create PNG image. ####
				graph.exportToDOTandPNG("unmodifiedGraph");

				//#### Convert AIG to MIG ####
				graph.convertAIGtoMAJnodes();
				graph.exportToDOTandPNG("majGraph");
				graph.exportToBLIF("majGraph");


				//#### Implementation of the Algorithm ####
				int[] lastStatistics =  {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
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
						unchangedStatisticsCount = 0;
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
					//String statisticsResult = ABC.Statistics.printStatistics(new File("output/intermediate-statistics.blif"), false, false, false);
					int[] statisticsResult = ABC.Statistics.getIntegerStatistics(new File("output/intermediate-statistics.blif"));
					boolean optimizationFound = false;
					for(int x = 0; x < 3; x++) {
						if(lastStatistics[x] > statisticsResult[x]) {
							optimizationFound = true;
							lastStatistics[x] = statisticsResult[x];
						}
					}

					if(! optimizationFound) {
						unchangedStatisticsCount++;
					}
					else {
						unchangedStatisticsCount = 0;
					}
					if(createStatisticsCSV) {
						statisticsWriter.flush();
					}
					if(exportPNGEndOfIteration) {
						graph.exportToDOTandPNG("end_of_iteration");
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
	}
}
