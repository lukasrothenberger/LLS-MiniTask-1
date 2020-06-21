package Graph;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.util.DoublyLinkedList;

public class BoolFunctions {
	private GraphWrapper bf;
	
	public GraphModifier graphModifier;

	public BoolFunctions(GraphWrapper bf) {
		this.bf = bf;
	}
	
	
	public void Majority(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception {
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = bf.nodesMap.get(nodeID);
			if(node.type != NodeType.MAJ)
				continue;
			Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
			Edge[] incomingEdges = node.getIncomingEdges(internalGraph, nodesMap);
			// much easier implementation possible (check pairwise equivalence
			long replaceByValue = -1;
			if(outgoingEdges.length == 2) {
				replaceByValue = (outgoingEdges[0].weight == 2) ? outgoingEdges[0].dest : outgoingEdges[1].dest; 
			}
			else {
				if(outgoingEdges[0].dest == outgoingEdges[1].dest) {
					replaceByValue = outgoingEdges[0].dest;
				}
				else if(outgoingEdges[0].dest == outgoingEdges[2].dest) {
					replaceByValue = outgoingEdges[2].dest;
				}
				else if(outgoingEdges[1].dest == outgoingEdges[2].dest) {
					replaceByValue = outgoingEdges[1].dest;
				}
			}
			
			if(replaceByValue == -1) {
				//no majority operation possible
				continue;
			}
			else {
				//majority operation is possible
				//delete current node
				bf.removeNode(node.id);
				for(Edge e : incomingEdges) {
					bf.addEdge(e.source, replaceByValue);
				}
			}
		}
	}
	
	
	public void Associativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception{
		Graph<Node, Edge> IG_copy = (Graph<Node, Edge>)((AbstractBaseGraph<Node, Edge>)internalGraph).clone();
		HashMap<Long, Node> NM_copy = new HashMap<Long, Node>();
		//fill NM_copy
		for(Node node : IG_copy.vertexSet()) {
			NM_copy.put(node.id, node);
			
		}
		GraphWrapper GW_copy = new GraphWrapper();
		GW_copy.internalGraph = IG_copy;
		GW_copy.nodesMap = NM_copy;
		//set input/output nodes
		for(Node node : GW_copy.internalGraph.vertexSet()) {
			if(node.modifier == NodeModifier.INPUT) {
				GW_copy.inputNodes.add(node);
			}
			if(node.modifier == NodeModifier.OUTPUT) {
				GW_copy.outputNodes.add(node);
			}
		}
		
		// actual start of Associativity-code
		
		//randomize iteration
		List<Long> keyList = new LinkedList<Long>();
		for(long nodeId : GW_copy.nodesMap.keySet()){
			keyList.add(nodeId);
		}
		Collections.shuffle(keyList);
		
		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		for(long nodeID : keyList) {
			Node node = NM_copy.get(nodeID);
			if(node.associativityPossible(IG_copy, NM_copy)) {
				Edge[] outgoingEdges = node.getOutgoingEdges(IG_copy, NM_copy);
				int i = (int) Math.random()*outgoingEdges.length;
					if(NM_copy.get(outgoingEdges[i].dest).type == NodeType.MAJ) {
						Node innerNode = NM_copy.get(outgoingEdges[i].dest);
						//check if overlap exists in node.children and innerNode.children
						long overlappingInputNode = -1;
						for(Node child : node.getChildrenNodes(IG_copy, NM_copy)) {
							for(Node innerChild : innerNode.getChildrenNodes(IG_copy, NM_copy)) {
								if(child.id == innerChild.id) {
								//	if(child.id != 0)  // don't allow constant zero as shared input
										overlappingInputNode = child.id;
								//	if(overlappingInputNode != 0)
										break;
								}
							}
							if(overlappingInputNode != -1 && overlappingInputNode != 0) {
								break;
							}					
						}
						if(overlappingInputNode == -1) {
							//no overlapping input between inner and outer node found, continue
							continue;
						}
						//overlap found for input with id overlappingInputNode, exclude this node from swapping			
						
						int innerOffset = (int) (Math.random() * 3) % 3;
						Edge[] innerInputEdges = null;
						Edge innerInput = null;

						while(true) {
							innerOffset = (int) (Math.random() * 3);
							// select random outgoing edge from inner node to maj node
							innerInputEdges = innerNode.getOutgoingEdges(IG_copy, NM_copy);
							innerInput = innerInputEdges[innerOffset % innerInputEdges.length];
							
							if(innerInput.dest != overlappingInputNode)
								break;
						}
						if(innerInput == null)
							continue;
						//select outer input node
						Edge[] outerInputEdges = null;
						Edge outerInput = null;
						while(true) {
							int outerOffset = (int) (Math.random()*3);
							outerInputEdges = node.getOutgoingEdges(IG_copy, NM_copy);
							outerInput = outerInputEdges[outerOffset % outerInputEdges.length];
							
							if(outerInput.dest != overlappingInputNode && outerInput.dest != innerNode.id && outerInput != null)
								break;
						}
						if(outerInput == null)
							continue;						
						// swap selected inner with outer edge
						
						// delete edge from node to outerInput
						GW_copy.deleteEdge(node.id, outerInput.dest);
						// delete edge from innerNode to innerInput
						GW_copy.deleteEdge(innerNode.id, innerInput.dest);
						
						int successfull = 0;
						try {
							// create edge from node to innerInput
							GW_copy.addEdge(node.id, innerInput.dest);
							successfull++;
							// create edge from innerNode to outerInput
							GW_copy.addEdge(innerNode.id, outerInput.dest);
							successfull++;
							
						}
						catch (Exception e) {
							e.printStackTrace();
							if(successfull == 0) {
								GW_copy.addEdge(node.id, outerInput.dest);
								GW_copy.addEdge(innerNode.id, innerInput.dest);
							}
							else if(successfull == 1) {
								GW_copy.deleteEdge(node.id, innerInput.dest);
								GW_copy.addEdge(node.id, outerInput.dest);
								GW_copy.addEdge(innerNode.id, innerInput.dest);
							}
							else {
								throw e;
							}
							
						}
						
						break;
					}
			}
		}
		
		//check if applied changes are valid
				bf.exportToBLIF("associativiy-intermediate-1");
				GW_copy.exportToBLIF("associativity-intermediate-2");
				GW_copy.exportToDOTandPNG("associativity-intermediate-2");
				try {
					ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/associativiy-intermediate-1.blif"), new File("output/associativity-intermediate-2.blif"));
					// made changes are valid
					//bf = GW_copy;
					bf.internalGraph = GW_copy.internalGraph;
					bf.nodesMap = GW_copy.nodesMap;
					bf.inputNodes = GW_copy.inputNodes;
					bf.outputNodes = GW_copy.outputNodes;
					bf.graphModifier = GW_copy.graphModifier;
					bf.boolFunctions = GW_copy.boolFunctions;
				}
				catch(Exception ex) {
					// made changes are not valid
					Associativity(internalGraph, nodesMap);
				}
	}
	
	
	public void DistributivityLR(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception {
		//TODO bugged
		
		boolean modificationFound = true;
		while(modificationFound) {
			bf.exportToDOTandPNG("distributivity");
			Thread.sleep(5);
			modificationFound = false;
			for(long nodeID : bf.nodesMap.keySet()) {
				Node node = nodesMap.get(nodeID);	
				if(node.type != NodeType.MAJ)
					continue;
				int[] counts =  node.getCounts(internalGraph, nodesMap);
				System.out.println("counts: "+ counts[0]+ " "+ counts[1] + " "+counts[2]);
				if (counts[0]+counts[2] >= 2) {
					System.out.println("CONDITION TRUE");
				Edge[] outerEdges = node.getOutgoingEdges(internalGraph, nodesMap);
				for(int i = 0; i < outerEdges.length; i++) {
					System.out.println("INNER LOOP");
					if(nodesMap.get(outerEdges[i].dest).type == NodeType.MAJ) {
						System.out.println("MAJ NODE FOUND");
						//get the ID and Edges of the MAJ node					
						Node innerNode = nodesMap.get(outerEdges[i].dest);
						System.out.println("Selecting inner edges");
						Edge[] innerEdges = innerNode.getOutgoingEdges(internalGraph, nodesMap);
						int success = 0;
						// add two new MAJ gates with outer and inner inputs and delete the old one
						bf.deleteEdge(innerNode.id, innerEdges[0].dest); //deleting inputs at 0 and 1 of inner MAJ Gate.
						bf.deleteEdge(innerNode.id, innerEdges[1].dest);
						bf.deleteEdge(node.id, outerEdges[(i+1) % 3].dest);			//deleting inputs at 0 and 1 of outer MAJ Gate.
						bf.deleteEdge(node.id, outerEdges[(i+2) % 3].dest);
						try {
							System.out.println("node.id: "+ node.id);
							System.out.println("innerNode.id: "+ innerNode.id);
							bf.addEdge(innerNode.id, outerEdges[(i+1) % 3].dest);//outer inputs to the existing MAJ gate
							bf.addEdge(innerNode.id, outerEdges[(i+2) % 3].dest);	
							success++;
							bf.addEdge(outerEdges[i].source, innerEdges[0].dest); // adding inner input 0 to outer MAJ gate
							//bf.addEdge(innerEdges[0].dest, ID.id);
							success++;
							long NewID = bf.getNextFreeId();
							bf.addMajGate(NewID, outerEdges[(i+1) % 3].dest, outerEdges[(i+2) % 3].dest, innerEdges[1].dest);//Addition of new MAJ gate
							bf.addEdge(outerEdges[i].source, NewID);
							modificationFound = true;
							success++;
							return;
							//break;
							} catch (Exception e) { // reversing the changes!!!!
							e.printStackTrace();
							if(success == 0) {
								bf.addEdge(node.id, outerEdges[(i+1) % 3].dest); //adding back outer edges
								bf.addEdge(node.id, outerEdges[(i+2) % 3].dest);
								bf.addEdge(innerNode.id, innerEdges[1].dest);
								bf.addEdge(innerNode.id, innerEdges[0].dest);
								}
							else if(success == 1) {
								bf.addEdge(node.id, outerEdges[(i+1) % 3].dest);
								bf.addEdge(node.id, outerEdges[(i+2) % 3].dest);
								bf.deleteEdge(innerNode.id, outerEdges[(i+1) % 3].dest);//deleting new outer to inner connection
								bf.deleteEdge(innerNode.id, outerEdges[(i+2) % 3].dest);
								bf.addEdge(innerNode.id,innerEdges[0].dest);//adding inner edge back
								}
								else if(success == 2) {
								bf.deleteEdge(innerEdges[0].dest, node.id);//deleting the new inner to outer connection
								bf.addEdge(innerNode.id, innerEdges[1].dest);			//adding inner edge	back
								bf.addEdge(innerNode.id, innerEdges[1].dest);
								bf.addEdge(node.id, outerEdges[(i+1) % 3].dest);
								bf.addEdge(node.id, outerEdges[(i+2) % 3].dest);
								}
							else if(success == 3) {
								System.out.println("Distributivity successful");
							}
							}
						}	
					}
					if(modificationFound == true)
						break;
				}
				else
					continue;
			}
		}
	}
	/*
	public void DistributivityRL(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception {
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = nodesMap.get(nodeID);	
			if(node.type != NodeType.MAJ)
				continue;
			System.out.println("Outer Node: " + node.id);
			Edge[] outerEdges = node.getOutgoingEdges(internalGraph, nodesMap);
			Node[] outernode = node.getChildrenNodes(internalGraph, nodesMap); //getting all the outer nodes
			for(int i = 0; i < outerEdges.length; i++) {
				//System.out.println("Outer LOOP");
				Node innerNode = nodesMap.get(outerEdges[i].dest);
				Edge[] innerEdges = innerNode.getOutgoingEdges(internalGraph, nodesMap);
				Edge[] overlappingInputEdges = innerEdges;
				if(innerNode.type == NodeType.MAJ) {
					//System.out.println("MAJ NODE FOUND");
					//get the ID and Edges of the MAJ node										
					System.out.println("MAJ node ID:" + innerNode.id);						
					System.out.println("Node ID --> " + outernode[i].id);
					System.out.println("Edges --> " + innerEdges[i].dest);
					int[] counts =  innerNode.getCounts(internalGraph, nodesMap);		
					if (counts[1] == 2) {
						System.out.println("NODE HAS TWO MAJ  NODES");
						for(int j = 0; j <= innerEdges.length ; j++) {
							Node[] childNode = innerNode.getChildrenNodes(internalGraph, nodesMap);							
							Edge[] sameInput = null ;
							Edge[] diffInput = null;
							for(int k = 1; k< innerEdges.length; k++) {////check this!!!!
								if(nodesMap.get(innerEdges[j].dest).type == NodeType.MAJ) {
									Edge[] CEdge = childNode[j].getOutgoingEdges(internalGraph, nodesMap);
									System.out.println("Child Edges :" + CEdge[j]);
									if(childNode[j].id == overlappingInputEdges[j].source) {
										System.out.println("At the first Maj node");
										System.out.println("Node:" + nodeID);
							  // continue;
									}
									else {
										System.out.println("At second Maj Node for Comparisson");
										for(int l = 0; l < innerEdges.length ; l++) {
											if(overlappingInputEdges[k].dest == CEdge[l].dest) {						
												System.out.println("Chekcing for mathcing input");
												sameInput[k] = CEdge[l];
											}
											else {
									
												System.out.println("Non matching input");
												//diffInput[j] = CEdge[k];
											}
										}		
									}
								}
					/*
					try {
					bf.redirectEdge(sameInput[j].source, sameInput[j].dest, node.id);
					System.out.println("Added common inputs to outer MAJ node");
					graphModifier.convertMAJtoVALnodes();
					bf.redirectEdge(diffInput[j].source, diffInput[j].dest, innernode.id);
					System.out.println("Added diff Inputs to inner MAJ node");
					graphModifier.convertMAJtoVALnodes();
							//bf.deleteEdge(overlappingInputEdges[j].dest, overlappingInputEdges[j].source);
							//bf.deleteEdge(overlappingInputEdges[j].dest, overlappingInputEdges[j].source);
						}
					
					catch(Exception e) {
						e.printStackTrace();
						graphModifier.convertVALtoMAJnodes();
						bf.redirectEdge(overlappingInputEdges[j].source, overlappingInputEdges[j].dest, innernode.id);
						graphModifier.convertVALtoMAJnodes();
						bf.redirectEdge(innerEdges[j].source,innerEdges[j].dest, innernode.id );
						
				}
						}	
					}
				}
			}
	}*/
					
	
	
	/**
	 * does only a single relevance transformation
	 * @param internalGraph
	 * @param nodesMap
	 * @throws Exception
	 */
	public void Relevance(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception {
		Graph<Node, Edge> IG_copy = (Graph<Node, Edge>)((AbstractBaseGraph<Node, Edge>)internalGraph).clone();
		HashMap<Long, Node> NM_copy = new HashMap<Long, Node>();
		//fill NM_copy
		for(Node node : IG_copy.vertexSet()) {
			NM_copy.put(node.id, node);
			
		}
		GraphWrapper GW_copy = new GraphWrapper();
		GW_copy.internalGraph = IG_copy;
		GW_copy.nodesMap = NM_copy;
		//set input/output nodes
		for(Node node : GW_copy.internalGraph.vertexSet()) {
			if(node.modifier == NodeModifier.INPUT) {
				GW_copy.inputNodes.add(node);
			}
			if(node.modifier == NodeModifier.OUTPUT) {
				GW_copy.outputNodes.add(node);
			}
		}
		
		// actual start of Relevance-code
		//randomize iteration
		List<Long> keyList = new LinkedList<Long>();
		for(long nodeId : GW_copy.nodesMap.keySet()){
			keyList.add(nodeId);
		}
		Collections.shuffle(keyList);
		
		for(long nodeId : keyList) {
			Node node = NM_copy.get(nodeId);
			if(node.type != NodeType.MAJ)
				continue;
			Edge[] outgoingEdges = node.getOutgoingEdges(IG_copy, NM_copy);
			if(outgoingEdges.length < 3)
				continue;
			
			int Offset;
			while(true) {
				Offset = (int) (Math.random() * outgoingEdges.length);
				if(outgoingEdges[Offset].dest != 0)
					break;
			}
			//randomization
			int Index_1 = (Math.random() > 0.5) ? 1 : 2;
			int Index_2 = (Index_1 == 1) ? 2 : 1;
			Index_1 = (Offset+Index_1)%outgoingEdges.length;
			Index_2 = (Offset+Index_2)%outgoingEdges.length;
			
			long victim = outgoingEdges[Index_1].dest;
			long replacement = outgoingEdges[Index_2].dest;
			
			if(replacement == 0 || replacement == 1)
				continue;
			
			// invert replacement
			if(replacement == 0) {
				replacement = 1L;
			}
			else if(replacement % 2L == 0 ) {
				replacement = replacement + 1L;
			}
			else {
				replacement = replacement - 1L;
			}
			
			// start replacement on z's children
			Node z =  NM_copy.get(outgoingEdges[Offset].dest);
			Edge[] z_outgoingEdges = z.getOutgoingEdges(IG_copy, NM_copy);
			
			for(Edge e : z_outgoingEdges) {
				if(GW_copy.replaceInSubtreeRecursive(e.dest, victim, replacement)) {
					System.out.println("relevance: done something");
				}
			}
		}
		
		//check if applied changes are valid
		bf.exportToBLIF("relevance-intermediate-1");
		GW_copy.exportToBLIF("relevance-intermediate-2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/relevance-intermediate-1.blif"), new File("output/relevance-intermediate-2.blif"));
			// made changes are valid
			//bf = GW_copy;
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;
		}
		catch(Exception ex) {
			// made changes are not valid
			Relevance(internalGraph, nodesMap);
		}
		
	}

	
	public void ComplementaryAssociativity(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception {
		//TODO not working for inverter on outer node
		
		int count = 0;
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = nodesMap.get(nodeID);
			if(node.type != NodeType.MAJ)
				continue;
		//	if(node.associativityPossible(internalGraph, nodesMap)) {
				Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
				for(int c = 0; c < outgoingEdges.length; c++) {
					int i = (int) Math.random()*outgoingEdges.length;
					if(nodesMap.get(outgoingEdges[i].dest).type == NodeType.MAJ) {
						Node innerNode = nodesMap.get(outgoingEdges[i].dest);
						Edge[] innerEdges = innerNode.getOutgoingEdges(internalGraph, nodesMap);
						//check if overlap exists in node.children and innerNode.children
						long overlappingInputNode = -1;
						for(Node child : node.getChildrenNodes(internalGraph, nodesMap)) {
							for(Node innerChild : innerNode.getChildrenNodes(internalGraph, nodesMap)) {
								if(innerChild.type == NodeType.INV ) {
								//get id which is not inverted
									for(Node Invchild : innerChild.getChildrenNodes(internalGraph, nodesMap)) {
									 for(int x = 0; x < 3; x++) {
										 if(x==i)
											 continue;
										 if(outgoingEdges[x].dest == Invchild.id && Invchild.id != 0) {
											 System.out.println("x: "+ x+ "   -> "+outgoingEdges[x].dest);
											 //shared inverted value between inner and outer node
											overlappingInputNode = Invchild.id;
											for(int j = 0; j < 3; j++) {
												if(innerEdges[j].dest == innerChild.id) {
													for(int y = 0; y < 3; y++) {
														if(y == i) //exclude path to MAJ node
															continue;
														if(outgoingEdges[y].dest == overlappingInputNode)
															continue;
														// outgoingEdges[y] points to remaining value
														bf.redirectEdge(innerEdges[j].source, innerEdges[j].dest, outgoingEdges[y].dest);
														bf.removeNode(innerChild.id);
												  		System.out.println("COMP ASSOC DONE");
													}
												}
											}
										 }
									 }
									  if(child.id != 0)  // don't allow constant ZERO as shared input
										overlappingInputNode = child.id;
									  	for(int x = 0; x < 3; x++) {
									  		if(x == i)
									  			continue;
									  		if(outgoingEdges[x].dest == overlappingInputNode)
									  			continue;
									  		try {
									  			bf.redirectEdge(innerEdges[i].source, innerEdges[i].dest, outgoingEdges[x].dest);
									  		}
									  		catch(Exception ex) {
									  			continue;
									  		}
									  		System.out.println("COMP ASSOC DONE");
									  		//innerEdges[i] = outgoingEdges[x] ;
								  	}
						//			  	 count++;
									if(overlappingInputNode != -1)
										break;
								}
							}
							if(overlappingInputNode != -1 && overlappingInputNode != 0) {
								break;
							}
							
						}
						
			//TODO select outerInput in a way that outerInput and innerInput are not equivalent and not equal to the shared value
						
						// swap selected inner with outer edge
						//bf.exportToBLIF("intermediate-1");
						//bf.exportToDOTandPNG("intermediate-1");
						
			//			System.out.println("node.id: "+node.id);
			//			System.out.println("outerInput.dest: "+ outgoingEdges[i].dest);
			//			System.out.println("innerInput.dest: "+ innerEdges[i].dest);
			//			System.out.println("shared Input: "+overlappingInputNode);
						
						break;
							}
						 	}
					}
			}
	}
	
	
	
	public void Substitution(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = nodesMap.get(nodeID);
		}
	}
		 
	
	
	/*		 
	
	public void InvertProp(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {	 
	}
	*/
	}

