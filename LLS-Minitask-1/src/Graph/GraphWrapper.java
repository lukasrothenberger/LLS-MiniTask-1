package Graph;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

//import org.jinternalGrapht.*;
//import org.jinternalGrapht.internalGraph.DefaultEdge;
//import org.jinternalGrapht.internalGraph.SimpleGraph;

import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;

public class GraphWrapper {
	// Wrapper class for JGraphT
	
	public Graph<Node, InvertableEdge> internalGraph;
	public HashMap<Long, Node> nodesMap;
	public HashSet<Node> inputNodes;
	public HashSet<Node> outputNodes;
	
	
	public GraphWrapper() {
		internalGraph = new SimpleDirectedGraph<>(InvertableEdge.class);
		nodesMap = new HashMap<Long, Node>();
		inputNodes = new HashSet<Node>();
		outputNodes = new HashSet<Node>();
		// insert constant 0
		Node constantZero = new Node(0, NodeType.VAL);
		internalGraph.addVertex(constantZero);
		nodesMap.put((long) 0, constantZero);
	}
	
	
	public void addInputNode(long id) {
		Node newNode = new Node(id, NodeType.VAL);
		newNode.input = true;
		internalGraph.addVertex(newNode);
		inputNodes.add(newNode);
		nodesMap.put(id, newNode);
	}
	
	
	public void addOutputNode(long id) {
		Node newNode = new Node(id, NodeType.VAL);
		newNode.output = true;
		internalGraph.addVertex(newNode);
		outputNodes.add(newNode);
		nodesMap.put(id, newNode);
		if(id % 2 != 0) {
			// output is inverted, create inverted edge from parent
			addEdge(id, id-1, true);
		}
	}
	
	
	public void addEdge(long source, long dest, boolean inverted) {
		Node sourceNode = nodesMap.get(source);
		if(dest % 2 != 0) {
			// dest is an inverted node
			if(! nodesMap.containsKey(dest)) {
				//dest not yet in graph
				Node newNode = new Node(dest, NodeType.INV);
				internalGraph.addVertex(newNode);
				nodesMap.put(dest, newNode);
				addEdge(dest, dest-1, true);
			}
		}
		Node destNode = nodesMap.get(dest);
		InvertableEdge newEdge = new InvertableEdge(source, dest, inverted);
		internalGraph.addEdge(sourceNode, destNode, newEdge);
	}
	
	
	public Node getNode(long id) {
		return nodesMap.get(id);
	}
	
	
	public void addAndGate(long id, long child1, long child2) throws Exception {
		if(! nodesMap.containsKey(id)) {
			Node newNode = new Node(id, NodeType.AND);
			internalGraph.addVertex(newNode);
			nodesMap.put(id, newNode);
		}
		else {
			nodesMap.get(id).type = NodeType.AND;
		}
		addEdge(id, child1, false);
		addEdge(id, child2, false);	
	}
	
	
	public void convertAIGtoMAJnodes() throws Exception {
		//TODO implement convertAIGtoMAJnodes
		throw new Exception("TODO");
	}
	
	
	public void print() {
		System.out.println("VERTICES:");
		for(Node node : internalGraph.vertexSet()) {
			System.out.println(node.toString());
		}
		System.out.println("EDGES:");
		for(InvertableEdge edge : internalGraph.edgeSet()) {
			System.out.println(edge.toString());

		}
	}
	
	
	public String toDOTFormat() {
		DOTExporter de = new DOTExporter(new StringNameProvider<Node>(), null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    OutputStreamWriter osw = new OutputStreamWriter(baos);
		de.export(osw, internalGraph);
		return baos.toString();
	}
	
}
