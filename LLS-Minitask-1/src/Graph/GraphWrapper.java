package Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

//import org.jinternal_grapht.*;
//import org.jinternal_grapht.internal_graph.DefaultEdge;
//import org.jinternal_grapht.internal_graph.SimpleGraph;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;

public class GraphWrapper {
	// Wrapper class for JGraphT
	
	public Graph<Node, InvertableEdge> internal_graph;
	public HashMap<Long, Node> nodesMap;
	public HashSet<Node> inputNodes;
	public HashSet<Node> outputNodes;
	
	
	public GraphWrapper() {
		internal_graph = new SimpleDirectedGraph<>(InvertableEdge.class);
		nodesMap = new HashMap<Long, Node>();
		inputNodes = new HashSet<Node>();
		outputNodes = new HashSet<Node>();
		// insert constant 0
		Node constantZero = new Node(0, NodeType.VAL);
		internal_graph.addVertex(constantZero);
		nodesMap.put((long) 0, constantZero);
	}
	
	
	public void addInputNode(long id) {
		Node newNode = new Node(id, NodeType.VAL);
		newNode.input = true;
		internal_graph.addVertex(newNode);
		inputNodes.add(newNode);
		nodesMap.put(id, newNode);
	}
	
	
	public void addOutputNode(long id) {
		Node newNode = new Node(id, NodeType.VAL);
		newNode.output = true;
		internal_graph.addVertex(newNode);
		outputNodes.add(newNode);
		nodesMap.put(id, newNode);
		if(id % 2 != 0) {
			// output is inverted, create inverted edge from parent
			addEdge(id, id-1, true);
		}
	}
	
	
	public void addEdge(long source, long dest, boolean inverted) {
		Node source_node = nodesMap.get(source);
		if(dest % 2 != 0) {
			// dest is an inverted node
			if(! nodesMap.containsKey(dest)) {
				//dest not yet in graph
				Node newNode = new Node(dest, NodeType.INV);
				internal_graph.addVertex(newNode);
				nodesMap.put(dest, newNode);
				addEdge(dest, dest-1, true);
			}
		}
		Node dest_node = nodesMap.get(dest);
		InvertableEdge newEdge = new InvertableEdge(source, dest, inverted);
		internal_graph.addEdge(source_node, dest_node, newEdge);
	}
	
	
	public Node getNode(long id) {
		return nodesMap.get(id);
	}
	
	
	public void addAndGate(long id, long child1, long child2) throws Exception {
		if(! nodesMap.containsKey(id)) {
			Node newNode = new Node(id, NodeType.AND);
			internal_graph.addVertex(newNode);
			nodesMap.put(id, newNode);
		}
		else {
			nodesMap.get(id).type = NodeType.AND;
		}
		addEdge(id, child1, false);
		addEdge(id, child2, false);
		
	}
	
	
	public void convert_AIG_to_MAJ_nodes() throws Exception {
		//TODO implement convert_AIG_to_MAJ_nodes
		throw new Exception("TODO");
	}
	
	
	public void print() {
		System.out.println("VERTICES:");
		for(Node node : internal_graph.vertexSet()) {
			System.out.println(node.toString());
		}
		System.out.println("EDGES:");
		for(InvertableEdge edge : internal_graph.edgeSet()) {
			System.out.println(edge.toString());

		}
	}
	
}
