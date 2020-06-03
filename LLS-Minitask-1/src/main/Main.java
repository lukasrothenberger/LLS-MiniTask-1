package main;

import Graph.GraphWrapper;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphWrapper graph = new GraphWrapper();
		graph.addInputNode(0);
		graph.addInputNode(1);
		graph.addEdge(0, 1, true);
		System.out.println("####test graph print###");
		graph.print();
		System.out.println("###test edge####");
		long target_id = graph.internal_graph.edgesOf(graph.getNode(0)).iterator().next().dest;
		System.out.println("target id: "+target_id);
		
		
	}

}
