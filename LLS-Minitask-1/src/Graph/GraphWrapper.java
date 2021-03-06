/*
 * Authors: Lukas Rothenberger, Pallavi Gutta Ravi
 */

package Graph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;


public class GraphWrapper {
	// Wrapper class for internal jgrapht-graph
	
	public Graph<Node, Edge> internalGraph;
	public HashMap<Long, Node> nodesMap;
	public HashSet<Node> inputNodes;
	public HashSet<Node> outputNodes;
	public GraphModifier graphModifier;
	public BoolFunctions boolFunctions;
	
	/**
	 * Constructor for Wrapper Object for internal jgrapht-graph
	 */
	public GraphWrapper() {
		internalGraph = new DirectedAcyclicGraph<>(Edge.class);
		//internalGraph = new DirectedPseudograph<>(Edge.class);
		nodesMap = new HashMap<Long, Node>();
		inputNodes = new HashSet<Node>();
		outputNodes = new HashSet<Node>();
		// insert constant 0
		Node constantZero = new Node(0, NodeType.VAL, NodeModifier.INTERMEDIATE);
		internalGraph.addVertex(constantZero);
		nodesMap.put((long) 0, constantZero);
		
		this.graphModifier = new GraphModifier(this);
		this.boolFunctions = new BoolFunctions(this);
	}
	
	
	public long getNextFreeId() {
		long maxId = 0;
		for(long id : nodesMap.keySet()) {
			if(id > maxId)
				maxId = id;
		}
		maxId = (maxId % 2 == 0) ? maxId+2 : maxId+1; 
		return maxId;
	}
	
	
	/**
	 * Add input value with given id to the graph.
	 * @param id of the value as defined in .aag file
	 */
	public void addInputNode(long id) {
		Node newNode = new Node(id, NodeType.VAL, NodeModifier.INPUT);
		newNode.input = true;
		internalGraph.addVertex(newNode);
		inputNodes.add(newNode);
		nodesMap.put(id, newNode);
	}
	
	/**
	 * Add output value with given id to the graph.
	 * @param id of the value as defined in .aag file
	 * @throws Exception 
	 */
	public void addOutputNode(long id) throws Exception {
		Node newNode = new Node(id, NodeType.VAL, NodeModifier.OUTPUT);
		newNode.output = true;
		internalGraph.addVertex(newNode);
		outputNodes.add(newNode);
		nodesMap.put(id, newNode);
		if(id % 2 != 0) {
			// output is inverted, create edge from parent
			addEdge(id, id-1);
		}
	}
	
	/**
	 * replaces an edge from source to oldTarget with an edge from source to newTarget.
	 * @param source
	 * @param oldTarget
	 * @param newTarget
	 * @throws Exception
	 */
	public boolean redirectEdge(long source, long oldTarget, long newTarget) throws Exception {
		int success = 0;
		try {
			this.deleteEdge(source, oldTarget);
			success++;
			this.addEdge(source, newTarget);
			success++;
			return true;
		}
		catch(Exception e) {
			//undo successful modifications
			if(success == 0) {
			}
			if(success == 1) {
				this.addEdge(source, oldTarget);
			}
			if(success == 2) {
				//do nothing
			}
			throw e;
		}
	}
	
	/**
	 * Add an edge from node with id source to node with id dest.
	 * @param source
	 * @param dest
	 * @throws Exception 
	 */
	public void addEdge(long source, long dest) throws Exception {
		//check if edge from source to dest already exists
		if(internalGraph.containsEdge(nodesMap.get(source), nodesMap.get(dest))) {
			//increase weight
			internalGraph.getEdge(nodesMap.get(source), nodesMap.get(dest)).weight++;
			return;
			//throw new Exception("test - don't allow double edges");
		}
		Node sourceNode = nodesMap.get(source);
		if(dest % 2 != 0) {
			// dest is an inverted node
			if(! nodesMap.containsKey(dest)) {
				//dest not yet in graph
				Node newNode = new Node(dest, NodeType.INV, NodeModifier.INTERMEDIATE);
				internalGraph.addVertex(newNode);
				nodesMap.put(dest, newNode);
				addEdge(dest, dest-1);
			}
		}
		Node destNode = nodesMap.get(dest);
		Edge newEdge = new Edge(source, dest);
		internalGraph.addEdge(sourceNode, destNode, newEdge);
		
		//validation
		int summedWeight = 0;
		for(Edge e : internalGraph.edgesOf(sourceNode)) {
			if(e.source != sourceNode.id)
				continue;
			summedWeight += e.weight;
		}
		if(summedWeight > 3) {
			System.out.println("INVALID NODE: "+ source+". Rollback creation of edge.");
			//rollback 
			internalGraph.removeEdge(sourceNode, destNode);
		}
	}
	
	/**
	 * Delete an edge from node with id source to node with id dest.
	 * @param source
	 * @param dest
	 */
	public void deleteEdge(long source, long dest) {
		
		for(Edge e : internalGraph.getAllEdges(nodesMap.get(source), nodesMap.get(dest))){
			if(e.weight > 1) {
				internalGraph.getEdge(nodesMap.get(e.source), nodesMap.get(e.dest)).weight--;
				return;
			}
			internalGraph.removeEdge(e);
			return;
		}
		
	}
	
	
	/**
	 * Add a node representing an AND2 Gate to the Graph.
	 * @param id
	 * @param child1
	 * @param child2
	 * @throws Exception
	 */
	public void addAndGate(long id, long child1, long child2) throws Exception {
		if(! nodesMap.containsKey(id)) {
			Node newNode = new Node(id, NodeType.AND, NodeModifier.INTERMEDIATE);
			internalGraph.addVertex(newNode);
			nodesMap.put(id, newNode);
		}
		else {
			nodesMap.get(id).type = NodeType.AND;
		}
		addEdge(id, child1);
		addEdge(id, child2);	
	}
	
	/**
	 * Add a node representing a MAJ3 Gate to the graph.
	 * @param id
	 * @param child1
	 * @param child2
	 * @param child3
	 * @throws Exception
	 */
	public void addMajGate(long id, long child1, long child2, long child3) throws Exception {
		if(! nodesMap.containsKey(id)) {
			Node newNode = new Node(id, NodeType.MAJ, NodeModifier.INTERMEDIATE);
			internalGraph.addVertex(newNode);
			nodesMap.put(id, newNode);
		}
		else {
			nodesMap.get(id).type = NodeType.MAJ;
		}
		addEdge(id, child1);
		addEdge(id, child2);	
		addEdge(id, child3);	
	}
	
	/**
	 * Returns the Node object with the given id by querying nodesMap.
	 * @param id
	 * @return
	 */
	public Node getNode(long id) {
		return nodesMap.get(id);
	}
	
	/**
	 * Iterates over each node in the graph.
	 * If a node is of type And, it is converted into a node of type MAJ by changing the type value
	 * and adding constant zero as a new input.
	 * @throws Exception
	 */
	public void convertAIGtoMAJnodes() throws Exception {
		System.out.println("Converting AND to MAJ nodes...");
		for (Node node : internalGraph.vertexSet()) {
			if(node.type == NodeType.AND) {
				node.type = NodeType.MAJ;
				addEdge(node.id, 0);
			}
		}
		System.out.println("\tDone.");
	}
	
	/**
	 * Print a textual representation of the graph to the console.
	 */
	public void print() {
		System.out.println("VERTICES:");
		for(Node node : internalGraph.vertexSet()) {
			System.out.println(node.toString());
		}
		System.out.println("EDGES:");
		for(Edge edge : internalGraph.edgeSet()) {
			System.out.println(edge.toString());

		}
	}
	
	/**
	 * Create and return a textual representation of the graph in DOT format for visualization.
	 * A nice and easy tool for visualization can be found here:
	 * http://magjac.com/graphviz-visual-editor/
	 * @return DOT representation as String
	 */
	public String toDOTFormat() {
		DOTExporter<Node, Edge> de = new DOTExporter<Node, Edge>(); //(new StringNameProvider<Node>(), null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    OutputStreamWriter osw = new OutputStreamWriter(baos);
	    de.setVertexIdProvider((node) -> {return node.toString();});
	    de.setVertexAttributeProvider((node)->{return node.getDOTAttributes();});
	    de.setEdgeAttributeProvider((edge)->{return edge.getDOTAttributes();});
		de.exportGraph(internalGraph, osw);
		return baos.toString();
	}
	
	/**
	 * Create and return a BLIF representation of the graph.
	 * @return BLIF representation as String
	 * @throws Exception
	 */
	public String toBLIFFormat() throws Exception {
		String blifString = "";
		//append model header
		blifString += ".model output\n";
		//append inputs
		blifString += ".inputs ";
		Node[] sortedInputNodes = new Node[inputNodes.size()];
		int tmp_count = 0; 
		for(Node n : inputNodes) {
			sortedInputNodes[tmp_count] = n;
			tmp_count++;
		} 
		Arrays.sort(sortedInputNodes, new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				return Long.compare(n1.id, n2.id);
			}
		});
		
		for(Node node : sortedInputNodes) {
			blifString += node.toBLIFIdentifier()+" ";
		}
		blifString += "\n";
		//append outputs
		blifString += ".outputs ";
		int count = 0;
		String buffer = "";
		Node[] sortedOutputNodes = new Node[outputNodes.size()];
		tmp_count = 0; 
		for(Node n : outputNodes) {
			sortedOutputNodes[tmp_count] = n;
			tmp_count++;
		} 
		Arrays.sort(sortedOutputNodes, new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				return Long.compare(n1.id, n2.id);
			}
		});
		for(Node node : sortedOutputNodes) {
			blifString += "o"+count+" ";//node.toBLIFIdentifier()+" ";
			buffer += ".names "+node.toBLIFIdentifier()+" o"+count+"\n";
			buffer += "1 1\n";
			count++;
		}
		blifString += "\n";
		//write buffer to map output nodes to their last gates
		blifString += buffer;
		//define constant 0
		blifString += ".names a0\n";
		//append gates
		for(Node node : internalGraph.vertexSet()) {
			if(node.modifier != NodeModifier.INTERMEDIATE || node.id == 0) {
				// skip input / output nodes of type VAL and constant zero
				if(node.type == NodeType.VAL && node.modifier != NodeModifier.OUTPUT)
					continue;
			}
			blifString += node.toBLIF(internalGraph, nodesMap);
		}
		//append model end
		blifString += ".end\n\n";
		//append subcircuit definitions
		blifString += getBasicAndSubcircuitDefinitions();
		return blifString;
	}
	
	/**
	 * Create and return a String containing the definitions for BLIF subcircuits and constants
	 * used by the toBLIFFormat()-Method.
	 * @return String containing the used subcircuits.
	 */
	private String getBasicAndSubcircuitDefinitions() {
		String blifString = "";
		//define subcircuit INV
		blifString += ".model inv\n";
		blifString += ".inputs A\n";
		blifString += ".outputs O\n";
		blifString += ".names A O\n";
		blifString += "0 1\n";
		blifString += ".end\n\n";
		//define subcircuit AND
		blifString += ".model and2\n";
		blifString += ".inputs A B\n";
		blifString += ".outputs O\n";
		blifString += ".names A B O\n";
		blifString += "11 1\n";
		blifString += ".end\n\n";
		//define subcircuit MAJ
		blifString += ".model maj3\n";
		blifString += ".inputs A B C\n";
		blifString += ".outputs O\n";
		blifString += ".names A B C O\n";
		blifString += "011 1\n";
		blifString += "101 1\n";
		blifString += "110 1\n";
		blifString += "111 1\n";
		blifString += ".end\n\n";
		return blifString;
	}
	
	
	/**
	 * prints the internal graph to DOT File and as PNG image.
	 * @param filename should not contain a file ending (example: "unmodifiedGraph")
	 */
	public void exportToDOTandPNG(String filename) {
		try {
		File dotOutputFile = new File("output/"+filename+".dot");
		if(dotOutputFile.exists())
			dotOutputFile.delete();
			dotOutputFile.createNewFile();
			FileWriter fw = new FileWriter(dotOutputFile);
			fw.write(this.toDOTFormat());
			fw.flush();
			fw.close();
			String[] c = {"dot", "-Tpng", "output/"+filename+".dot", "-o", "output/"+filename+".png"};
			Process p = Runtime.getRuntime().exec(c);
			p.waitFor();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Export to DOT and PNG FAILED.\n\tgraphviz installed?\n\tdot command available? -> add to PATH");
		}
	}
	
	
	/**
	 * exports the internal graph to BLIF format.
	 * @param filename should not contain a file ending (example: "unmodifiedGraph")
	 */
	public void exportToBLIF(String filename) {
		File blifOutputFile = new File("output/"+filename+".blif");
		if(blifOutputFile.exists())
			blifOutputFile.delete();
		try {
			blifOutputFile.createNewFile();
			FileWriter fw = new FileWriter(blifOutputFile);
			fw.write(this.toBLIFFormat());
			fw.flush();
			fw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Removes the node with the given Id from the graph.
	 * Deletes all incoming and outgoing edges to this node -> might lead to orphaned nodes!
	 * @param id
	 */
	public void removeNode(long id) {
		Node node = nodesMap.get(id);
		for(Edge e : node.getIncomingEdges(internalGraph, nodesMap)) {
			internalGraph.removeEdge(e);
		}
		for(Edge e : node.getOutgoingEdges(internalGraph, nodesMap)) {
			internalGraph.removeEdge(e);
		}
		this.inputNodes.remove(node);
		this.outputNodes.remove(node);
		this.nodesMap.remove(node.id);
		internalGraph.removeVertex(node);
	}
	
		
	/**
	 * Replace occurrences of victim in the subtree starting from root with replacement.
	 * Return boolean value. True, if a value has been replaced. False, else.
	 * @param root
	 * @param victim
	 * @param replacement
	 * @return
	 * @throws Exception 
	 */
	public boolean replaceInSubtreeRecursive(long root, long victim, long replacement, List<Long> visited) throws Exception {
		if(visited.contains(root)) {
			//looping
			throw new Exception("loop found");
		}
		visited.add(root);
		boolean modificationFound = false;
		for(Edge e : nodesMap.get(root).getOutgoingEdges(internalGraph, nodesMap)) {
			if(e.dest == victim) {
				try {
					this.redirectEdge(e.source, e.dest, replacement);
					modificationFound = true;
				}
				catch(Exception ex) {
				}
			}
			else {
				try {
					replaceInSubtreeRecursive(e.dest, victim, replacement, visited);
					modificationFound = true;
				}
				catch(Exception ex) {
				}
			}
		}	
		return modificationFound;
	}
		
	
	/**
	 * returns the index of the root node of the copy.
	 * @return
	 * @throws Exception 
	 */
	public long copySubtree(long root) throws Exception {
		Node rootNode = this.getNode(root);
		HashMap<Long, Long> nodeToCloneId = new HashMap<Long, Long>();
		//clone all nodes in subtree
		List<Node> cleanedSubtree = new LinkedList<Node>();
		for(Node n : getSubtree(rootNode, new HashMap<Long, Integer>())) {
			if(cleanedSubtree.contains(n))
				continue;
			cleanedSubtree.add(n);
		}	
		
		for(Node nodeToCopy : cleanedSubtree) {
			if(nodeToCopy.modifier == NodeModifier.INTERMEDIATE && nodeToCopy.id > 1) {
				long cloneID = 0;
				if(nodeToCopy.id % 2 == 0) {
					//non-inverted node
					if(nodeToCloneId.keySet().contains(nodeToCopy.id+1)){
						//inverted version already exists
						cloneID = nodeToCloneId.get(nodeToCopy.id+1)-1;
					}
					else {
						cloneID = this.getNextFreeId();
					}
				}
				else {
					//inverted node
					if(nodeToCloneId.keySet().contains(nodeToCopy.id-1)){
						//non-inverted version already exists
						cloneID = nodeToCloneId.get(nodeToCopy.id-1)+1;
					}
					else {
						cloneID = this.getNextFreeId()+1;
					}
				}
				nodeToCopy.cloneNode(this, cloneID);
				//create a key-value mapping: oldId -> cloneIds
				nodeToCloneId.put(nodeToCopy.id, cloneID);
			}
			else {
				//don't clone in/out nodes
				nodeToCloneId.put(nodeToCopy.id, nodeToCopy.id);
			}
		}
		//iterate over all edges in subtree
		for(Node nodeToCopy : cleanedSubtree) {
			for(Edge edgeToCopy : nodeToCopy.getOutgoingEdges(internalGraph, nodesMap)) {
				// add edges with replaced IDs
				long cloneSourceID = nodeToCloneId.get(edgeToCopy.source);
				long cloneDestID = nodeToCloneId.get(edgeToCopy.dest);
				this.addEdge(cloneSourceID, cloneDestID);
			}
		}
		return nodeToCloneId.get(rootNode.id);
	}
	
	
    private List<Node> getSubtree(Node root, HashMap<Long, Integer> visited) throws Exception{
    	if(root.id < 1 || root.modifier != NodeModifier.INTERMEDIATE) {
    		//constants and IN/OUTPUT nodes can not have outgoing edges
    		return new LinkedList<Node>();
    	}
		if(visited.keySet().contains(root.id)) {
			if(visited.get(root.id) > root.getIncomingEdges(internalGraph, nodesMap).length){
				//looping
				throw new Exception("found loop.. for node: "+root);
			}
		}
		if(visited.keySet().contains(root.id)) {
			visited.put(root.id, visited.get(root.id)+1 );
		}
		else {
			visited.put(root.id, 1);
		}
    	List<Node> VisitedNodes = new LinkedList<Node>();
    	VisitedNodes.add(root);
    	for(Node rootnode : root.getChildrenNodes(internalGraph, nodesMap)) {
    		VisitedNodes.add(rootnode);
    		VisitedNodes.addAll(getSubtree(rootnode, visited));
    	}
		return VisitedNodes;
    }
    
    
    public void Remove_UnReachableNodes() throws Exception {
    	for(Node node: this.nodesMap.values()) {
    		if(node.modifier != NodeModifier.INTERMEDIATE) {
    			//don't remove In/Output nodes
    			continue;
    		}
    		try {
    			if(node.getIncomingEdges(internalGraph, nodesMap).length == 0) {
    				//remove node if no incoming edges exist
    				this.removeNode(node.id);
    				Remove_UnReachableNodes();
    				return;
    			}
    		}
    		catch(Exception ex) {
    			// node not in graph anymore
    		}
    	}
    }
		
}
	
