package main;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;

import Graph.GraphWrapper;
import Graph.Node;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphWrapper graph = new GraphWrapper();
		graph.addInputNode(0);
		graph.addInputNode(2);
		graph.addOutputNode(4);
		graph.addOutputNode(1);
		System.out.println("####test graph print###");
		graph.print();
		
		
		System.out.println("\n###### DOT FILE - can be pasted into DOT viewer ######");
		DOTExporter de = new DOTExporter(new StringNameProvider<Node>(), null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    OutputStreamWriter osw = new OutputStreamWriter(baos);
		de.export(osw, graph.internal_graph);
	    System.out.println(baos.toString());
		
	}

}
