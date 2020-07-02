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
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.util.DoublyLinkedList;

public class BoolFunctions {
	public GraphWrapper bf;

	public GraphModifier graphModifier;

	public BoolFunctions(GraphWrapper bf) {
		this.bf = bf;
	}


	public GraphWrapper Majority(int recursionCount) throws Exception {
		if(recursionCount > 10) {
			//do nothing
			return bf;
		}
		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}

		// actual start of Majority-code
		boolean modificationFound = true;
		while(modificationFound) {
			modificationFound = false;
			//randomize iteration
			List<Long> keyList = new LinkedList<Long>();
			for(long nodeId : GW_copy.nodesMap.keySet()){
				keyList.add(nodeId);
			}
			Collections.shuffle(keyList);

			for(long nodeID : keyList) {
				try {
					Node node = GW_copy.nodesMap.get(nodeID);
					if(node.type != NodeType.MAJ)
						continue;
					//	if(node.modifier == NodeModifier.OUTPUT)
					//		continue;
					Edge[] outgoingEdges = node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
					Edge[] incomingEdges = node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
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
						if(node.modifier != NodeModifier.OUTPUT) {
							//majority operation is possible
							//delete current node
							GW_copy.removeNode(node.id);
							for(Edge e : incomingEdges) {
								GW_copy.addEdge(e.source, replaceByValue);
							}
							modificationFound = true;
							System.out.println("Majority: done something 1");
							break;
						}
						else {
							//replace Type of output node with Value
							//delete input edges
							for(Edge e : node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.internalGraph.removeEdge(e); 
							}
							//change node Type to Value
							node.type = NodeType.VAL;
							//connect to new input node
							GW_copy.addEdge(node.id, replaceByValue);
							GW_copy.Remove_UnReachableNodes();
							modificationFound = true;
							System.out.println("Majority: done something 2");
							break;
						}
					}
				}
				catch(Exception ex) {
					continue;
				}
			}
		}

		//check if applied changes are valid
		bf.exportToBLIF("majority-intermediate-1");
		GW_copy.exportToBLIF("majority-intermediate-2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/majority-intermediate-1.blif"), new File("output/majority-intermediate-2.blif"));
			// made changes are valid
			//bf = GW_copy;
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;
			if(Math.random() > 0.6) {
				return GW_copy.boolFunctions.Majority(4);
			}
			else {
				return GW_copy;
			}
			//return GW_copy;
		}
		catch(Exception ex) {
			// made changes are not valid
			ex.printStackTrace();
			System.exit(0);
			return Majority(recursionCount+1);
		}
	}


	public GraphWrapper Associativity(int recursionCount) throws Exception{
		if(recursionCount > 5) {
			return bf;
		}

		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}

		// actual start of Associativity-code

		//loop at all nodes
		// get the inputs(edges) and do M(x,y,z) = M(y,z,x) = M(z,x,y)
		int loop_count = 0;
		boolean modificationFound = true;
		while(modificationFound && loop_count < GW_copy.nodesMap.keySet().size()) {
			loop_count++;
			modificationFound = false;

			//randomize iteration
			List<Long> keyList = new LinkedList<Long>();
			for(long nodeId : GW_copy.nodesMap.keySet()){
				keyList.add(nodeId);
			}
			Collections.shuffle(keyList);

			for(long nodeID : keyList) {
				Node node = GW_copy.nodesMap.get(nodeID);
				if(node.associativityPossible(GW_copy.internalGraph, GW_copy.nodesMap)) {
					Edge[] outgoingEdges = node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
					int i = (int) Math.random()*outgoingEdges.length;
					if(GW_copy.nodesMap.get(outgoingEdges[i].dest).type == NodeType.MAJ) {
						Node innerNode = GW_copy.nodesMap.get(outgoingEdges[i].dest);
						//check if overlap exists in node.children and innerNode.children
						long overlappingInputNode = -1;
						for(Node child : node.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap)) {
							for(Node innerChild : innerNode.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap)) {
								if(child.id == innerChild.id) {
									//	if(child.id != 0)  // don't allow constant zero as shared input
									overlappingInputNode = child.id;
									//	if(overlappingInputNode != 0)
									break;
								}
							}
							if(overlappingInputNode != -1 ) {//&& overlappingInputNode != 0) {
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
							innerInputEdges = innerNode.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
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
							outerInputEdges = node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
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
							modificationFound = true;
							//System.out.println("assoc: done something");
						}
						catch (Exception e) {
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
		}

		//check if applied changes are valid
		bf.exportToBLIF("associativiy-intermediate-1");
		GW_copy.exportToBLIF("associativity-intermediate-2");
		//GW_copy.exportToDOTandPNG("associativity-intermediate-2");
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
			if(Math.random() > 0.6) {
				return GW_copy.boolFunctions.Associativity(0);
			}
			else {
				return GW_copy;
			}
			//return GW_copy;
		}
		catch(Exception ex) {
			// made changes are not valid
			return Associativity(recursionCount+1);
		}
	}


	public GraphWrapper DistributivityRL(int recursionCount) throws Exception {
		//randomize iteration
		if(recursionCount > 5) {
			return bf;
		}

		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}
		boolean modificationFound = true;
		while(modificationFound) {
			modificationFound = false;
			List<Long> keyList = new LinkedList<Long>();
			for(long nodeId : GW_copy.nodesMap.keySet()){
				keyList.add(nodeId);
			}
			Collections.shuffle(keyList);

			/// Distributivity logic
			for(long nodeID : keyList) {
				Node node = GW_copy.nodesMap.get(nodeID);	
				if(node.type != NodeType.MAJ)
					continue;
				int[] counts = node.getCounts(GW_copy.internalGraph, GW_copy.nodesMap);
				if(counts[1] < 2) {
					continue;
				}	
				Edge[] outerEdges = node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);			
				Node[] outerchild = node.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap); //getting all the outer nodes
				//randomize
				int tmp_random = (int)Math.round(Math.random());
				Node innerNode1 = GW_copy.nodesMap.get(outerEdges[tmp_random].dest);
				tmp_random = (tmp_random == 0) ? 1 : 0;
				Node innerNode2 = GW_copy.nodesMap.get(outerEdges[tmp_random].dest);
				while(true) {
					int innerOffset = (int) (Math.random() * 3);
					innerNode1 = outerchild[innerOffset % outerchild.length];			
					if(innerNode1.type == NodeType.MAJ)
						break;
				}
				while(true) {
					int innerOffset = (int) (Math.random() * 3);
					innerNode2 = outerchild[innerOffset % outerchild.length];			
					if(innerNode2.type == NodeType.MAJ && innerNode1.id != innerNode2.id)
						break;
				}
				Node unUsedOuterChild = null;
				for(Node unUsed: outerchild) {
					if(unUsed != innerNode1 && unUsed != innerNode2) {
						unUsedOuterChild = unUsed;
					}
				}

				List<Node> overlappingInput = new LinkedList<Node>();
				List<Node> NonoverlappingInput = new LinkedList<Node>();
				for(Node innerChild1 : innerNode1.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap)){
					for(Node innerChild2 : innerNode2.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap)) {
						if(innerChild1.id != innerChild2.id) {		
							if(NonoverlappingInput.contains(innerChild2)) {}
							else NonoverlappingInput.add(innerChild2);
							if(NonoverlappingInput.contains(innerChild1)) {}
							else	NonoverlappingInput.add(innerChild1);
						}
						else {
							if(overlappingInput.contains(innerChild2)) {}
							else
								//if(innerChild2.id != 0)
								overlappingInput.add(innerChild2);
						}
					}
				}		
				NonoverlappingInput.removeAll(overlappingInput); // removing repetitive values
				//redirecting 
				if(NonoverlappingInput.isEmpty())
					continue;
				if(overlappingInput.size() != 2)
					continue;
				if(NonoverlappingInput.size() == 1 && NonoverlappingInput.get(0).id == 0) {
					NonoverlappingInput.add(NonoverlappingInput.get(0));
				}
				try {
					for(Edge removeOuterEdges : outerEdges) {
						GW_copy.deleteEdge(node.id, removeOuterEdges.dest);
					}
					for(Node createEdge : overlappingInput) {
						GW_copy.addEdge(node.id, createEdge.id);
					}

					Long NextFreeID = GW_copy.getNextFreeId();
					//M(u,v,z)
					GW_copy.addMajGate(NextFreeID, NonoverlappingInput.get(0).id, NonoverlappingInput.get(1).id, unUsedOuterChild.id);
					GW_copy.addEdge(node.id, NextFreeID);
					GW_copy.Remove_UnReachableNodes();
					System.out.println("Distributivity: done something");
					modificationFound = true;
					break;
				}
				catch(Exception e) {
					e.printStackTrace();
					return DistributivityRL(recursionCount+1);
				}
			}
		}

		//check if applied changes are valid
		bf.exportToBLIF("Distributivity-intermediate-1");
		GW_copy.exportToBLIF("Distributivity-intermediate-2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/Distributivity-intermediate-1.blif"), new File("output/Distributivity-intermediate-2.blif"));
			// made changes are valid
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;
			if(Math.random() > 0.6) {
				return GW_copy.boolFunctions.DistributivityRL(0);
			}
			else {
				return GW_copy;
			}
		}
		catch(Exception ex) {
			// made changes are not valid
			return DistributivityRL(recursionCount+1);
		}
	}


	/**
	 * does only a single relevance transformation
	 * @param internalGraph
	 * @param nodesMap
	 * @return 
	 * @throws Exception
	 */
	public GraphWrapper Relevance(int recursionCount) throws Exception {
		if(recursionCount > 5) {
			return bf;
		}

		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}


		List<Long> keyList = new LinkedList<Long>();
		for(long nodeId : GW_copy.nodesMap.keySet()){
			keyList.add(nodeId);
		}
		Collections.shuffle(keyList);

		boolean loopBreaker = false;
		for(long nodeId : keyList) {
			if(loopBreaker)
				break;
			Node node = GW_copy.nodesMap.get(nodeId);
			if(node.type != NodeType.MAJ)
				continue;
			Edge[] outgoingEdges = node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
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
			Node z =  GW_copy.nodesMap.get(outgoingEdges[Offset].dest);
			Edge[] z_outgoingEdges = z.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);

			for(Edge e : z_outgoingEdges) {
				if(GW_copy.replaceInSubtreeRecursive(e.dest, victim, replacement, new LinkedList<Long>())) {
					System.out.println("relevance: done something");
					loopBreaker = true;
					break;
				}
			}
		}

		//check if applied changes are valid
		try {
			bf.exportToBLIF("relevance-intermediate-1");
			GW_copy.exportToBLIF("relevance-intermediate-2");
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/relevance-intermediate-1.blif"), new File("output/relevance-intermediate-2.blif"));
			// made changes are valid
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;
			if(Math.random() > 0.6) {
				return GW_copy.boolFunctions.Relevance(0);
			}
			else {
				return GW_copy;
			}
		}
		catch(Exception ex) {
			// made changes are not valid
			return Relevance(recursionCount+1);
		}

	}


	public GraphWrapper ComplementaryAssociativity(int recursionCount) throws Exception {
		if(recursionCount > 5) {
			// do nothing
			return bf;
		}	
		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}

		// DO STUFF
		int loop_count = 0;
		boolean modificationFound = true;
		while(modificationFound || loop_count < GW_copy.nodesMap.keySet().size()) {
			loop_count++;
			modificationFound = false;

			//randomize iteration
			List<Long> keyList = new LinkedList<Long>();
			for(long nodeId : GW_copy.nodesMap.keySet()){
				keyList.add(nodeId);
			}
			Collections.shuffle(keyList);
			for(long nodeId : keyList) {
				Node node = GW_copy.nodesMap.get(nodeId);
				Edge[] outgoingEdges = node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
				if(node.type != NodeType.MAJ)
					continue;
				if(node.getCounts(GW_copy.internalGraph, GW_copy.nodesMap)[1] < 1) {
					//no child MAJ node
					continue;
				}
				//get index of M(y,u',z)
				int index_outer_M = -1;
				while(true) {
					index_outer_M = (int) (Math.random()*outgoingEdges.length);
					if(GW_copy.nodesMap.get(outgoingEdges[index_outer_M].dest).type == NodeType.MAJ) {
						break;
					}
				}		
				//get overlap
				long outerOverlappingValue = -1;
				long innerOverlappingValue = -1;
				//randomize iteration
				List<Edge> outerOutEdges = new LinkedList<Edge>();
				for(Edge e : node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap)){
					outerOutEdges.add(e);
				}
				Collections.shuffle(outerOutEdges);		
				for(Edge outerEdge: outerOutEdges) {
					//randomize iteration
					List<Edge> innerOutEdges = new LinkedList<Edge>();
					for(Edge e : GW_copy.nodesMap.get(outgoingEdges[index_outer_M].dest).getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap)){
						innerOutEdges.add(e);
					}
					Collections.shuffle(innerOutEdges);		
					for(Edge innerEdge: innerOutEdges) {
						//check for overlap
						if(outerEdge.dest - 1 == innerEdge.dest && innerEdge.dest % 2L == 0) {
							// -> outer value inverted
							// get index of x
							int index_outer_x = -1;
							while(true) {
								index_outer_x = (int) (Math.random()*outgoingEdges.length);
								if(index_outer_x != index_outer_M) {
									if(outgoingEdges[index_outer_x].dest != outerEdge.dest) {
										break;
									}
								}
							}
							// replace inner u' with x
							try {
								GW_copy.redirectEdge(innerEdge.source, innerEdge.dest, outgoingEdges[index_outer_x].dest);
							}
							catch(Exception ex) {
								//restart
								return ComplementaryAssociativity(recursionCount+1);
							}			
						}
						else if(innerEdge.dest - 1 == outerEdge.dest && outerEdge.dest % 2L == 0) {
							// -> innver value inverted
							// get index of x
							int index_outer_x = -1;
							while(true) {
								index_outer_x = (int) (Math.random()*outgoingEdges.length);
								if(index_outer_x != index_outer_M) {
									if(outgoingEdges[index_outer_x].dest != outerEdge.dest) {
										break;
									}
								}
							}
							// replace inner u' with x
							try {
								GW_copy.redirectEdge(innerEdge.source, innerEdge.dest, outgoingEdges[index_outer_x].dest);
								modificationFound = true;
								break;
							}
							catch(Exception ex) {
								//restart
								return ComplementaryAssociativity(recursionCount+1);
							}
						}
						else {
							// -> no overlap
							continue;
						}
					}
					if(modificationFound)
						break;
				}	
				if(modificationFound)
					break;
			}
		}
		// END DO STUFF

		//check if applied changes are valid
		bf.exportToBLIF("compAssoc-intermediate-1");
		GW_copy.exportToBLIF("compAssoc-intermediate-2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/compAssoc-intermediate-1.blif"), new File("output/compAssoc-intermediate-2.blif"));
			// made changes are valid
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;

			System.out.println("\t-> changes not discarded!");
			if(Math.random() > 0.6) {
				return GW_copy.boolFunctions.ComplementaryAssociativity(0);
			}
			else {
				return GW_copy;
			}
			//return GW_copy;
		}
		catch(Exception ex) {
			// made changes are not valid
			return ComplementaryAssociativity(recursionCount+1);
		}
	}


	public GraphWrapper Substitution(int recursionCount) throws Exception {
		if(recursionCount > 5) {
			// do nothing
			return bf;
		}	
		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}


		List<Long> keyList = new LinkedList<Long>();
		for(long nodeId : GW_copy.nodesMap.keySet()){
			keyList.add(nodeId);
		}
		Collections.shuffle(keyList);	
		// DO STUFF
		for(long nodeId : keyList) {
			Node node = GW_copy.nodesMap.get(nodeId);
			if(node.type != NodeType.MAJ)
				continue;
			if(node.modifier != NodeModifier.INTERMEDIATE) // In/Out nodes not copyable
				continue;
			Node[] node_children = node.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap);
			if(node_children.length != 3)
				continue;
			int index_x = (int)(Math.random()*3);
			//get x
			long id_x = node_children[index_x].id;
			int tmp_random = (int) Math.round(Math.random()) + 1;
			//get y
			long id_y = node_children[(index_x+tmp_random)%node_children.length].id;
			//get z
			tmp_random = (tmp_random == 1) ? 2 : 1;
			long id_z = node_children[(index_x+tmp_random)%node_children.length].id;
			//get v
			long id_v = id_x;
			//get v'
			long id_v_inv = (id_v % 2 == 0) ? id_v+1 : id_v-1;  
			//get u'
			long id_u_inv = id_z;
			//get u
			long id_u = (id_u_inv % 2 == 0) ? id_u_inv+1 : id_u_inv-1;

			try {
				//construct left inner MAJ node;
				//left copy subtree
				long left_copy_subtree_id = GW_copy.copySubtree(node.id);
				//replace v/u in subtree
				GW_copy.replaceInSubtreeRecursive(left_copy_subtree_id, id_v, id_u, new LinkedList<Long>());
				long id_left_inner_maj = GW_copy.getNextFreeId();
				GW_copy.addMajGate(id_left_inner_maj, id_v_inv, left_copy_subtree_id, id_u);	

				//construct right inner MAJ node
				//right copy subtree
				long right_copy_subtree_id = GW_copy.copySubtree(node.id);
				//replace v/u' in subtree
				GW_copy.replaceInSubtreeRecursive(right_copy_subtree_id, id_v, id_u_inv, new LinkedList<Long>());
				long id_right_inner_maj = GW_copy.getNextFreeId();
				GW_copy.addMajGate(id_right_inner_maj, id_v_inv, right_copy_subtree_id, id_u_inv);

				//construct outer Maj Gate
				long id_outer_maj = GW_copy.getNextFreeId();
				GW_copy.addMajGate(id_outer_maj, id_v, id_left_inner_maj, id_right_inner_maj);	

				//replace node with the created MAJ node
				for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
					GW_copy.redirectEdge(e.source, e.dest, id_outer_maj);
				}
				GW_copy.Remove_UnReachableNodes();
				System.out.println("substitution: done something");
				break;
			}
			catch(Exception e) {
				return Substitution(recursionCount+1);
			}
		}
		// END DO STUFF

		//check if applied changes are valid
		bf.exportToBLIF("Substitution-intermediate-1");
		GW_copy.exportToBLIF("Substitution-intermediate-2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/Substitution-intermediate-1.blif"), new File("output/Substitution-intermediate-2.blif"));
			// made changes are valid
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;
			if(Math.random() > 0.9)
				return GW_copy.boolFunctions.Substitution(4);
			else
				return GW_copy;
			//return GW_copy;
		}
		catch(Exception ex) {
			//	ex.printStackTrace();
			// made changes are not valid
			return Substitution(recursionCount+1);
		}
	}


	public GraphWrapper InverterPropagationLR(int recursionCount) throws Exception {
		if(recursionCount > 5) {
			// do nothing
			return bf;
		}	
		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}


		//DO STUFF
		boolean modificationFound = true;
		while(modificationFound) {
			modificationFound = false;

			List<Long> keyList = new LinkedList<Long>();
			for(long nodeId : GW_copy.nodesMap.keySet()){
				keyList.add(nodeId);
			}
			Collections.shuffle(keyList);	

			for(long nodeId : keyList) {
				try {
					Node node = GW_copy.nodesMap.get(nodeId);
					if(node.type != NodeType.MAJ || node.modifier != NodeModifier.INTERMEDIATE) {
						continue;
					}
					Edge[] incomingEdges = node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
					if(incomingEdges.length > 1) {
						continue;
					}
					//check if node is connected to inverter
					if(GW_copy.nodesMap.get(incomingEdges[0].source).type != NodeType.INV) {
						continue;
					}
					Edge[] outgoingEdges = node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
					for(int i = 0; i < outgoingEdges.length; i++) {
						//Invert edges
						if(outgoingEdges[i].dest % 2 == 0) {
							// add inverter
							GW_copy.redirectEdge(outgoingEdges[i].source, outgoingEdges[i].dest, outgoingEdges[i].dest + 1);
						}
						else {
							// remove inverter
							GW_copy.redirectEdge(outgoingEdges[i].source, outgoingEdges[i].dest, outgoingEdges[i].dest - 1);
						}
					}
					// redirect edge (exclude parent inverter)
					Node parentInverter = GW_copy.nodesMap.get(incomingEdges[0].source);
					for(Edge e : parentInverter.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
						GW_copy.redirectEdge(e.source, e.dest, node.id);
						//System.out.println("invProp: done something");
					}
					GW_copy.Remove_UnReachableNodes();
					modificationFound = true;
					break;
				}
				catch(Exception ex) {
					return InverterPropagationLR(recursionCount+1);
				}
			}
		}
		// END DO STUFF

		//check if applied changes are valid
		bf.exportToBLIF("InvertProp-intermediate-1");
		GW_copy.exportToBLIF("InvertProp-intermediate-2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/InvertProp-intermediate-1.blif"), new File("output/InvertProp-intermediate-2.blif"));
			// made changes are valid
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;
			if(Math.random() > 0.6)
				return GW_copy.boolFunctions.InverterPropagationLR(0);
			else
				return GW_copy;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			// made changes are not valid
			return InverterPropagationLR(recursionCount+1);
		}
	}


	public GraphWrapper TrivialReplacements(int recursionCount) throws Exception {
		if(recursionCount > 5) {
			// do nothing
			return bf;
		}	
		//create working copy of internal Graph
		GraphWrapper GW_copy = new GraphWrapper();
		for(Node n : bf.internalGraph.vertexSet()) {
			if(n.modifier == NodeModifier.INPUT) {
				GW_copy.addInputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else if(n.modifier == NodeModifier.OUTPUT) {
				GW_copy.addOutputNode(n.id);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
			else {
				Node newNode = new Node(n.id, n.type, n.modifier);
				GW_copy.internalGraph.addVertex(newNode);
				GW_copy.nodesMap.put(n.id, newNode);
				GW_copy.getNode(n.id).input = n.input;
				GW_copy.getNode(n.id).output = n.output;
				GW_copy.getNode(n.id).type = n.type;
			}
		}
		for(Edge e : bf.internalGraph.edgeSet()) {
			for(int i = 0; i < e.weight; i++)
				GW_copy.addEdge(e.source, e.dest);
		}


		//DO STUFF
		List<Long> alreadySeen = new LinkedList<Long>();
		boolean modificationFound = true;
		while(modificationFound) {
			modificationFound = false;

			List<Long> keyList = new LinkedList<Long>();
			for(long nodeId : GW_copy.nodesMap.keySet()){
				if(!alreadySeen.contains(nodeId))
					keyList.add(nodeId);
			}
			Collections.shuffle(keyList);	

			for(long nodeId : keyList) {
				Node node = GW_copy.nodesMap.get(nodeId);

				// 1. replacement: replace MAJ(x,x',y) by y
				if(node.type == NodeType.MAJ) {
					//ignore weighted outgoing edges here
					if(node.getOutgoingEdges(GW_copy.internalGraph, GW_copy.nodesMap).length != 3)
						continue;

					Node[] children = node.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap);
					if(children[0].id % 2 == 0) {
						//regular 0
						//check 0<->1
						if(children[0].id == children[1].id-1) {
							//replace node by 2
							for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.redirectEdge(e.source, e.dest, children[2].id);
							}
							modificationFound = true;
						}
						if(children[0].id == children[2].id-1) {
							//replace node by 1
							for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.redirectEdge(e.source, e.dest, children[1].id);
							}
							modificationFound = true;
						}
					}
					else {
						//inverted 0
						if(children[0].id == children[1].id+1) {
							//replace node by 2
							for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.redirectEdge(e.source, e.dest, children[2].id);
							}
							modificationFound = true;
						}
						if(children[0].id == children[2].id+1) {
							//replace node by 1
							for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.redirectEdge(e.source, e.dest, children[1].id);
							}
							modificationFound = true;
						}
					}
					if(children[1].id % 2 == 0) {
						//regular 1
						//check 1<->2
						if(children[1].id == children[2].id-1) {
							//replace node by 0
							for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.redirectEdge(e.source, e.dest, children[0].id);
							}
							modificationFound = true;
						}
					}
					else {
						//inverted 1
						//check 1<->2
						if(children[1].id == children[2].id+1) {
							//replace node by 0
							for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.redirectEdge(e.source, e.dest, children[0].id);
							}
							modificationFound = true;
						}
					}
					if(modificationFound) {
						System.out.println("trivRep: replaced MAJ(x,x',y) by y");
						modificationFound = true;
						alreadySeen.add(node.id);
						break;
					}
				}

				//2. replacement: Inv(Inv(x)) by x
				if(node.type == NodeType.INV) {
					Node[] children = node.getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap);
					if(children[0].type == NodeType.INV) {
						Node targetNode = children[0].getChildrenNodes(GW_copy.internalGraph, GW_copy.nodesMap)[0];
						//replace node by targetNode
						for(Edge e : node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
							GW_copy.redirectEdge(e.source, e.dest, targetNode.id);
						}
						modificationFound = true;
					}
					if(modificationFound) {
						System.out.println("trivRep: replaced Inv(Inv(x)) by x");
						modificationFound = true;
						alreadySeen.add(node.id);
						break;
					}
				}

				if(node.type != NodeType.MAJ || node.modifier != NodeModifier.INTERMEDIATE) {
					continue;
				}
				// 1. replacement : check for multiple inverters, only use a single on
				Edge[] incomingEdges = node.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap);
				if(incomingEdges.length > 1) {
					for(Edge in_edge : incomingEdges) {
						Node sourceNode = GW_copy.getNode(in_edge.source);
						if(sourceNode.type == NodeType.INV && sourceNode.id != node.id + 1) {
							//replace sourcenode with the proper inverter
							for(Edge e : sourceNode.getIncomingEdges(GW_copy.internalGraph, GW_copy.nodesMap)) {
								GW_copy.redirectEdge(e.source, e.dest, node.id+1);
							}
							System.out.println("trivRep: applied Inverter combination");
							modificationFound = true;
							alreadySeen.add(node.id);
						}
					}
					if(modificationFound) {
						break;
					}
				}


			}
		}
		// END DO STUFF

		//check if applied changes are valid
		bf.exportToBLIF("TrivRep-intermediate-1");
		GW_copy.exportToBLIF("TrivRep-intermediate-2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/TrivRep-intermediate-1.blif"), new File("output/TrivRep-intermediate-2.blif"));
			// made changes are valid
			bf.internalGraph = GW_copy.internalGraph;
			bf.nodesMap = GW_copy.nodesMap;
			bf.inputNodes = GW_copy.inputNodes;
			bf.outputNodes = GW_copy.outputNodes;
			bf.graphModifier = GW_copy.graphModifier;
			bf.boolFunctions = GW_copy.boolFunctions;
			if(Math.random() > 0.6)
				return GW_copy.boolFunctions.TrivialReplacements(0);
			else
				return GW_copy;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			// made changes are not valid
			return TrivialReplacements(recursionCount+1);
		}
	}

}

