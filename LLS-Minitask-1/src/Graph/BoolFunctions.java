package Graph;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;

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
					
	
	/*

	public void Relevance(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {	
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = bf.nodesMap.get(nodeID);
			if(node.type != NodeType.MAJ)
				continue;
			Edge[] outgoingEdges = node.getOutgoingEdges(internalGraph, nodesMap);
			int Offset = (int) Math.random()*3;
			if(outgoingEdges[Offset].dest != outgoingEdges[(Offset+1)%3].dest && outgoingEdges[Offset].dest != outgoingEdges[(Offset+2)%3].dest) {
				//check if replacement is already inverted
				if(outgoingEdges[(Offset+1%3)].dest % 2 == 0) {
					//replacement is not an inverted value
					bf.replaceInSubtree(outgoingEdges[Offset].dest, outgoingEdges[(Offset+1)%3].dest, outgoingEdges[(Offset+1)%3].dest + 1);
				}
				else {
					//replacement is an inverted value
					bf.replaceInSubtree(outgoingEdges[Offset].dest, outgoingEdges[(Offset+1)%3].dest, outgoingEdges[(Offset+1)%3].dest - 1);
				}		
				System.out.println("RELEVANCE OPERATION");
			} 
			else
				continue;					
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
				for(int i = 0; i < outgoingEdges.length; i++) {
					if(nodesMap.get(outgoingEdges[i].dest).type == NodeType.MAJ) {
						System.out.println("FOUND MAJ");
						Node innerNode = nodesMap.get(outgoingEdges[i].dest);
						Edge[] innerEdges = innerNode.getOutgoingEdges(internalGraph, nodesMap);
						//check if overlap exists in node.children and innerNode.children
						long overlappingInputNode = -1;
						for(Node child : node.getChildrenNodes(internalGraph, nodesMap)) {
							System.out.println("1");
							for(Node innerChild : innerNode.getChildrenNodes(internalGraph, nodesMap)) {
								System.out.println("2");
								if(innerChild.type == NodeType.INV ) {
									System.out.println("INV NODE FOUND");
									System.out.println("node: "+node.id);
									System.out.println("innerNode: "+innerNode.id);
									System.out.println("child: "+ child.id);
									System.out.println("innerChild: "+ innerChild.id);
								//get id which is not inverted
									for(Node Invchild : innerChild.getChildrenNodes(internalGraph, nodesMap)) {
										System.out.println("InvChild: "+ Invchild.id);
						//			 if(Invchild.id == innerChild.id){
						//				 System.out.println("INNER 1");
						//				if(child.id == innerChild.id){
						//					System.out.println("INNER 2");
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
						/*			  	for(int x = 0; x < 3; x++) {
									  		if(x == i)
									  			continue;
									  		if(outgoingEdges[x].dest == overlappingInputNode)
									  			continue;
									  		bf.redirectEdge(innerEdges[i].source, innerEdges[i].dest, outgoingEdges[x].dest);
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
	
	
	
	public void Substitution(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		for(long nodeID : bf.nodesMap.keySet()) {
			Node node = nodesMap.get(nodeID);
		}
		 
	
	
	/*		 
	
	public void InvertProp(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {	 
	}
	*/
	}

