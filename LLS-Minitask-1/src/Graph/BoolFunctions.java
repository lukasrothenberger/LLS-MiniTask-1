package Graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;

class BoolFunctions {
	private GraphWrapper bf;
	long id;
	NodeType type;
	NodeModifier modifier;
	boolean input = false;
	boolean output = false;
	private Node node;

	public BoolFunctions() {
		this.bf = bf;
		this.id = id;
		this.type = type;
		this.modifier = modifier;
		this.node = node;
	}

	public long Commutativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		Node node = bf.nodesMap.get(NodeType.VAL);
		Edge[]  = node.getOutgoingEdges(internalGraph, nodesMap);
		//IncomingEdges = node.getIncomingEdges(internalGraph, nodesMap);
		//Edge[] resultArray = new Edge[IncomingEdges.length];
		
		for(long nodeID : bf.nodesMap.keySet()) {
			
			//InvertableEdge ie = bf.internalGraph.getEdge(arg0, arg1);
		}
		return a;
	}
	
	public long Majority(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long Associativity(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long Distributivity(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long InvertProp(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long Relevance(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long ComplementaryAssociativity(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long Substitution(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	
	
}
