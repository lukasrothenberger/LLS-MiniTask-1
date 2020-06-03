package main;

import Graph.GraphWrapper;

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
		
		
	}

}
