package Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

//import org.jinternal_grapht.*;
//import org.jinternal_grapht.internal_graph.DefaultEdge;
//import org.jinternal_grapht.internal_graph.SimpleGraph;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

public class GraphWrapper {
	// Wrapper class for JGraphT
	
	public Graph<Node, InvertableEdge> internal_graph;
	public HashMap<Long, Node> nodesMap;
	public HashSet<Node> inputNodes;
	public HashSet<Node> outputNodes;
	
	
	public GraphWrapper() {
		internal_graph = new SimpleGraph<>(InvertableEdge.class);
		nodesMap = new HashMap<Long, Node>();
		inputNodes = new HashSet<Node>();
		outputNodes = new HashSet<Node>();
	}
	
	
	public void addInputNode(long id) {
		Node newNode = new Node(id, NodeType.VALUE);
		newNode.input = true;
		internal_graph.addVertex(newNode);
		inputNodes.add(newNode);
		nodesMap.put(id, newNode);
		System.out.println("created IN Node: "+newNode.toString());
	}
	
	
	public void addOutputNode(long id) {
		Node newNode = new Node(id, NodeType.VALUE);
		newNode.output = true;
		internal_graph.addVertex(newNode);
		outputNodes.add(newNode);
		nodesMap.put(id, newNode);
		System.out.println("created OUT Node: "+newNode.toString());
		if(id % 2 != 0) {
			// output is inverted, create inverted edge from parent
			addEdge(id-1, id, true);
			System.out.println("\tcreated inverted edge from parent: "+(id-1));
		}
	}
	
	
	public void addEdge(long source, long dest, boolean inverted) {
		Node source_node = nodesMap.get(source);
		Node dest_node = nodesMap.get(dest);
		InvertableEdge newEdge = new InvertableEdge(source, dest, inverted);
		internal_graph.addEdge(source_node, dest_node, newEdge);
		System.out.println("created "+ newEdge.toString());
	}
	
	
	public Node getNode(long id) {
		return nodesMap.get(id);
	}
	
	
	public void addAndGate(long id, Node child1, Node child2) throws Exception {
		//TODO implement addAndGate
		throw new Exception("TODO");
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
