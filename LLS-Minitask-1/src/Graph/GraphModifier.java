package Graph;

import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.Graph;

public class GraphModifier {
	private GraphWrapper gw;
	public GraphModifier(GraphWrapper gw) {
		this.gw = gw;
	}
	
	public Graph<Node, Edge> internalGraph;
	public HashMap<Long, Node> nodesMap;
	public HashSet<Node> inputNodes;
	public HashSet<Node> outputNodes;
	public BoolFunctions boolFunctions;
	public GraphWrapper graphwrapper;
	
	public void iteration() {
		for(long nodeID : gw.nodesMap.keySet()) {
			Node node = gw.nodesMap.get(nodeID);
			if(node.associativityPossible(gw.internalGraph, gw.nodesMap)) {
				// execute associativity for node
			}
		}
	}
	
	/**
	 * If a node is of type And, it is converted into a node of type MAJ by changing the type value
	 * and adding constant zero as a new input.
	 * @throws Exception
	 */
	public void convertMAJtoVALnodes() throws Exception {
		//Node type = gw.nodesMap.get(NodeType.VAL);
		System.out.println("Converting Maj node to Value node...");
		for (Node node : internalGraph.vertexSet()) {
			if(node.type == NodeType.MAJ) {
				node.type = NodeType.VAL;
			}
		}
		System.out.println("\tDone.");
	}
	
	public void convertVALtoMAJnodes() throws Exception {
		//Node type = gw.nodesMap.get(NodeType.VAL);
		System.out.println("Converting Value node to Maj node...");
		for (Node node : internalGraph.vertexSet()) {
			if(node.type == NodeType.VAL) {
				node.type = NodeType.MAJ;
			}
		}
		System.out.println("\tDone.");
	}
}
