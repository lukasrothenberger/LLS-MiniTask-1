package Graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;

public class BoolFunctions {
	private GraphWrapper bf;

	public BoolFunctions(GraphWrapper bf) {
		this.bf = bf;
	}

	public long Commutativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		Node node = bf.nodesMap.get(NodeType.VAL);
		Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
		//IncomingEdges = node.getIncomingEdges(internalGraph, nodesMap);
		//Edge[] resultArray = new Edge[IncomingEdges.length];
		
		for(long nodeID : bf.nodesMap.keySet()) {
			
			//Edge ie = bf.internalGraph.getEdge(arg0, arg1);
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
	
	public void Associativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception{
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = nodesMap.get(nodeID);
			if(node.associativityPossible(internalGraph, nodesMap)) {
				Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
				for(int i = 0; i < outgoingEdges.length; i++) {
					if(nodesMap.get(outgoingEdges[i].dest).type == NodeType.MAJ) {
						int outerOffset =  Math.random() < 0.5 ? 1 : 2; 
						Edge outerInput = outgoingEdges[(i+outerOffset) % outgoingEdges.length];
						Node innerNode = nodesMap.get(outgoingEdges[i].dest);
						int innerOffset = (int) (Math.random() * 3) % 3;
						// select random outgoing edge from inner node
						Edge[] innerInputEdges = innerNode.getOutgoingEdges(internalGraph, nodesMap);
						Edge innerInput = innerInputEdges[innerOffset % innerInputEdges.length];
						// swap selected inner with outer edge
						long buffer = outerInput.dest;
						
						// delete edge from node to outerInput
						bf.deleteEdge(node.id, outerInput.dest);
						// delete edge from innerNode to innerInput
						bf.deleteEdge(innerNode.id, innerInput.dest);
						
						int successfull = 0;
						try {
							// create edge from node to innerInput
							bf.addEdge(node.id, innerInput.dest);
							successfull++;
							// create edge from innerNode to outerInput
							bf.addEdge(innerNode.id, outerInput.dest);
							successfull++;
						}
						catch (Exception e) {
							if(successfull == 0) {
								bf.addEdge(node.id, outerInput.dest);
								bf.addEdge(innerNode.id, innerInput.dest);
							}
							else if(successfull == 1) {
								bf.addEdge(node.id, outerInput.dest);
								bf.addEdge(innerNode.id, innerInput.dest);
								bf.deleteEdge(node.id, innerInput.dest);
							}
							else {
								throw e;
							}
						}
						break;
					}
				}
			}
		}
	}
	
	public long Distributivity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long InvertProp(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long Relevance(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long ComplementaryAssociativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		return a;
	}
	
	public long Substitution(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
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
