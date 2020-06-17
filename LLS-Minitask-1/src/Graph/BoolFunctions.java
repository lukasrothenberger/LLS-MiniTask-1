package Graph;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;

public class BoolFunctions {
	private GraphWrapper bf;

	public BoolFunctions(GraphWrapper bf) {
		this.bf = bf;
	}
/*
	public void Commutativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		@SuppressWarnings("unlikely-arg-type")
		Node node = bf.nodesMap.get(NodeType.VAL);		
		for(long nodeID : bf.nodesMap.keySet()) {
			Node ID = nodesMap.get(nodeID);
			Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
			int[] counts = node.getCounts(internalGraph, nodesMap);
			if(counts[0]>=2) {
			Edge temp;
			
			int outerOffset =  Math.random() < 0.5 ? 1 : 2;
			
			if(outerOffset > 0 & outerOffset <3) {
			temp = outgoingEdges[outerOffset] ;
		    outgoingEdges[outerOffset] = outgoingEdges[0];
		    outgoingEdges[0] = temp;	
			}
			else {
				Edge temp1;	
				temp1 = outgoingEdges[1] ;
			    outgoingEdges[1] = outgoingEdges[2];
			    outgoingEdges[2] = temp1;				
			}
			// should we add new edges after removing the old ones????
			}
		}	
	}
	
	public void Majority(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		Edge majValue;
		int counter = 0;
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);		
			for(long nodeID : bf.nodesMap.keySet()) {
				Node ID = nodesMap.get(nodeID);
				Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
				Edge[] incomingEdge = node.getIncomingEdges(internalGraph, nodesMap);
				for(int i = 1; i < outgoingEdges.length; i++) {
					int Offset =  Math.random() < 0.5 ? 1 : 2;
					Edge Input = outgoingEdges[(i+Offset) % outgoingEdges.length];
					if(outgoingEdges[Offset] == outgoingEdges[Offset-1]) {
						counter++;
						}
					else if(outgoingEdges[Offset] == outgoingEdges[0])
						counter++;
					else
						continue;
					if (counter >=1)
						majValue = outgoingEdges[Offset];
				
				incomingEdge = majValue;	//check this!	
				bf.deleteEdge(node.id, Input.dest);
				int successfull = 0;
				try {
					// create edge from node to next node and remove node
					bf.addEdge(node.id, Input.dest);
					successfull++;
					//add logic to remove MAJ node
					}
				catch (Exception e) {
					if(successfull == 0) {
						bf.addEdge(node.id, Input.dest);
						//add logic to remove MAJ node
						}
					else if(successfull == 1) {
						bf.addEdge(node.id,Input.dest);
						bf.deleteEdge(node.id,Input.dest);
						//add logic to remove MAJ node
						}
					else {
						throw e;
						}
					}
				break;
				}
			}
		
	}
*/	
	public void Associativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception{
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = nodesMap.get(nodeID);
			if(node.associativityPossible(internalGraph, nodesMap)) {
				Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
				for(int i = 0; i < outgoingEdges.length; i++) {
					if(nodesMap.get(outgoingEdges[i].dest).type == NodeType.MAJ) {
						Node innerNode = nodesMap.get(outgoingEdges[i].dest);
						//check if overlap exists in node.children and innerNode.children
						long overlappingInputNode = -1;
						for(Node child : node.getChildrenNodes(internalGraph, nodesMap)) {
							for(Node innerChild : innerNode.getChildrenNodes(internalGraph, nodesMap)) {
								if(child.id == innerChild.id) {
									if(child.id != 0)  // don't allow constant zero as shared input
										overlappingInputNode = child.id;
									if(overlappingInputNode != 0)
										break;
								}
							}
							if(overlappingInputNode != -1 && overlappingInputNode != 0) {
								break;
							}
							
						}
						if(overlappingInputNode == -1) {
							//no overlapping input between inner and outer node found, continue
							System.out.println("NO OVERLAP FOUND, SKIPPING");
							continue;
						}
						//overlap found for input with id overlappingInputNode, exclude this node from swapping			
						
						int innerOffset = (int) (Math.random() * 3) % 3;
						Edge[] innerInputEdges = null;
						Edge innerInput = null;
						for(innerOffset = 0; innerOffset < 3; innerOffset++) {		// TODO randomize?
							// select random outgoing edge from inner node to maj node
							innerInputEdges = innerNode.getOutgoingEdges(internalGraph, nodesMap);
							innerInput = innerInputEdges[innerOffset % innerInputEdges.length];
							
							//if(nodesMap.get(innerInput.dest).type == NodeType.MAJ)
							if(innerInput.dest != overlappingInputNode)
								break;
						}
						if(innerInput == null)
							continue;
						//select outer input node
						Edge[] outerInputEdges = null;
						Edge outerInput = null;
						for(int outerOffset = 0; outerOffset < 3; outerOffset++) {
							//outerInput = outgoingEdges[(i+outerOffset) % outgoingEdges.length];
							outerInputEdges = node.getOutgoingEdges(internalGraph, nodesMap);
							outerInput = outerInputEdges[outerOffset % outerInputEdges.length];
							
							if(outerInput.dest != overlappingInputNode && outerInput.dest != innerNode.id && outerInput != null)
								break;
						}
						if(outerInput == null)
							continue;
						System.out.println("FOUND INNER AND OUTER INPUT!");
						
						//int outerOffset =  Math.random() < 0.5 ? 1 : 2; 
						//TODO select outerInput in a way that outerInput and innerInput are not equivalent and not equal to the shared value
						
						// swap selected inner with outer edge
						bf.exportToBLIF("intermediate-1");
						bf.exportToDOTandPNG("intermediate-1");
						
						System.out.println("node.id: "+node.id);
						System.out.println("outerInput.dest: "+ outerInput.dest);
						System.out.println("innerInput.dest: "+ innerInput.dest);
						System.out.println("shared Input: "+overlappingInputNode);
						
						// delete edge from node to outerInput
						bf.deleteEdge(node.id, outerInput.dest);
						// delete edge from innerNode to innerInput
						bf.deleteEdge(innerNode.id, innerInput.dest);
						
			//			bf.exportToDOTandPNG("intermediate-1-post-delete");
						
						int successfull = 0;
						try {
					//		bf.redirectEdge(node.id, outerInput.dest, innerInput.dest);
					//		bf.redirectEdge(innerNode.id, innerInput.dest, outerInput.dest);
							
							// create edge from node to innerInput
							bf.addEdge(node.id, innerInput.dest);
							successfull++;
							// create edge from innerNode to outerInput
							bf.addEdge(innerNode.id, outerInput.dest);
				//			bf.exportToDOTandPNG("intermediate-1-post-add1");
							successfull++;
				//			bf.exportToBLIF("intermediate-3");
				//			bf.exportToDOTandPNG("intermediate-3");
				//			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/intermediate-1.blif"), new File("output/intermediate-3.blif"));
							
						}
						catch (Exception e) {
							e.printStackTrace();
							if(successfull == 0) {
								bf.addEdge(node.id, outerInput.dest);
								bf.addEdge(innerNode.id, innerInput.dest);
							}
							else if(successfull == 1) {
								bf.deleteEdge(node.id, innerInput.dest);
								bf.addEdge(node.id, outerInput.dest);
								bf.addEdge(innerNode.id, innerInput.dest);
							}
							else {
								throw e;
							}
							
						}
						
						bf.exportToBLIF("intermediate-2");
						bf.exportToDOTandPNG("intermediate-2");
						
						ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/intermediate-1.blif"), new File("output/intermediate-2.blif"));
						
						break;
					}
				}
			}
		}
	}
/*	
	public void Distributivity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		Node node = bf.nodesMap.get(NodeType.VAL);		
		for(long nodeID : bf.nodesMap.keySet()) {
			Node ID = nodesMap.get(nodeID);
			Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
			for(int i = 0; i < outgoingEdges.length; i++) {
				System.out.println("INNER LOOP");
				if(nodesMap.get(outgoingEdges[i].dest).type == NodeType.MAJ) {
					System.out.println("MAJ NODE FOUND");
					Node innerNode = nodesMap.get(outgoingEdges[i].dest);
					System.out.println("selecting inner edges");
					//int innerOffset = (int) (Math.random() * 3) % 3;
					// select random outgoing edge from inner node
					Edge[] innerInputEdges = innerNode.getOutgoingEdges(internalGraph, nodesMap);
					//Edge innerInput = innerInputEdges[innerOffset % innerInputEdges.length];
		}
		}
	
		 
	
	public void InvertProp(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		 
	}
	
	public void Relevance(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		 
	}
	
	public void ComplementaryAssociativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		 
	}
	
	public void Substitution(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		
		long a = 0,b = 0,c = 0;
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : bf.nodesMap.keySet()) {
			@SuppressWarnings("unlikely-arg-type")
			Node node = bf.nodesMap.get(NodeType.VAL);
		}
		 
	}
	
	*/
	
}
