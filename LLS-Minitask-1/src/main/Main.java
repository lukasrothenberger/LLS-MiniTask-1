package main;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;

import Graph.GraphWrapper;
import Graph.Node;

public class Main {

	public static void main(String[] args) throws Exception {
		GraphWrapper graph = new GraphWrapper();
		
		// DEBUG (equivalent to aig_0_min.aag)
		graph.addInputNode(2);
		graph.addInputNode(4);
		graph.addInputNode(6);
		graph.addInputNode(8);
		graph.addInputNode(10);
		graph.addInputNode(12);

		graph.addOutputNode(40);
		
		asdf
		fdsa
		blub
		
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
		
		System.out.println("###### DOT FORMAT - can be pasted into DOT viewer ######");
	    System.out.println(graph.toDOTFormat());
		
		System.out.println("##### BLIF FORMAT #####");
		System.out.println(graph.toBLIFFormat());
		
	}

}
