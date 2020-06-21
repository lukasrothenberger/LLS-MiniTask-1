package Graph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

//import org.jinternalGrapht.*;
//import org.jinternalGrapht.internalGraph.DefaultEdge;
//import org.jinternalGrapht.internalGraph.SimpleGraph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.util.DoublyLinkedList;

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
		return maxId+2;
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
	public void redirectEdge(long source, long oldTarget, long newTarget) throws Exception {
		int success = 0;
		try {
			this.deleteEdge(source, oldTarget);
			success++;
			this.addEdge(source, newTarget);
			success++;
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
	}
	
	/**
	 * Delete an edge from node with id source to node with id dest.
	 * @param source
	 * @param dest
	 */
	public void deleteEdge(long source, long dest) {
		
		for(Edge e : internalGraph.getAllEdges(nodesMap.get(source), nodesMap.get(dest))){
			if(e.weight > 1) {
				e.weight--;
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
		for(Node node : inputNodes) {
			blifString += node.toBLIFIdentifier()+" ";
		}
		blifString += "\n";
		//append outputs
		blifString += ".outputs ";
		int count = 0;
		String buffer = "";
		for(Node node : outputNodes) {
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
				if(node.type == NodeType.VAL)
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
		System.out.println("Exporting to DOT Format and PNG Image...");
		File dotOutputFile = new File("output/"+filename+".dot");
		if(dotOutputFile.exists())
			dotOutputFile.delete();
			dotOutputFile.createNewFile();
			FileWriter fw = new FileWriter(dotOutputFile);
			fw.write(this.toDOTFormat());
			fw.flush();
			fw.close();
			String[] c = {"dot", "-Tpng", "output/"+filename+".dot", "-o", "output/"+filename+".png"};
			//String[] c = {"dot", "-?"};
			Process p = Runtime.getRuntime().exec(c);
			System.out.println("\tDone.");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Export to DOT and PNG FAILED.\n\tgraphviz installed?");
		}
	}
	
	
	/**
	 * exports the internal graph to BLIF format.
	 * @param filename should not contain a file ending (example: "unmodifiedGraph")
	 */
	public void exportToBLIF(String filename) {
		System.out.println("Exporting to BLIF Format..."); 
		File blifOutputFile = new File("output/"+filename+".blif");
		if(blifOutputFile.exists())
			blifOutputFile.delete();
		try {
			blifOutputFile.createNewFile();
			FileWriter fw = new FileWriter(blifOutputFile);
			fw.write(this.toBLIFFormat());
			fw.flush();
			fw.close();
			System.out.println("\tDone.");
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
		internalGraph.removeVertex(node);
	}
	
	
	private void __fillNodeQueue(Node rootNode, List<Long> queue) {
		if(! (queue.contains(rootNode.id)))
			queue.add(rootNode.id);
		for(Node node: rootNode.getChildrenNodes(internalGraph, nodesMap)) {
			__fillNodeQueue(node, queue);
		}
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
	public boolean replaceInSubtree(long root, long victim, long replacement) throws Exception {
		System.out.println("root: "+ root);
		System.out.println("victim: "+ victim);
		System.out.println("replacement: "+ replacement);
	//	if(victim < 2) {
	//		return false;
	//	}
		
		this.exportToBLIF("1");
		this.exportToDOTandPNG("1");
		List<Long> Queue = new LinkedList<Long>();
		__fillNodeQueue(nodesMap.get(root), Queue);
		System.out.println("done: "+Queue.size());
		
		System.out.println("Queue: ");
		for(Long q : Queue) {
			System.out.println("\t"+q);
		}
		
		boolean modificationFound = false;
		for(Long queueNode : Queue) {
			Node node = nodesMap.get(queueNode);
			List<Long> victimList = new LinkedList<Long>();
			for(Edge e : node.getOutgoingEdges(internalGraph, nodesMap)) {
				if(e.dest == victim) {
					if(! (victimList.contains(node.id)))
						victimList.add(node.id);
				}
			}
			for(Long nodeId : victimList) {
				System.out.println("nodeId: "+ nodeId + " victim: "+ victim + " replacement: "+ replacement);
				try {
					this.redirectEdge(nodeId, victim, replacement);
					modificationFound = true;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		this.exportToBLIF("2");
		try {
			ABC.EquivalenceCheck.performEquivalenceCheck(new File("output/1.blif"), new File("output/2.blif"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.exportToDOTandPNG("2");
			throw e;
		}
		
		
		return modificationFound;
	}
	
	
	/**
	 * Replace occurrences of victim in the subtree starting from root with replacement.
	 * @param root
	 * @param victim
	 * @param replacement
	 * @throws Exception 
	 */
	/*
	public boolean replaceInSubtree(long root, long victim, long replacement, DoublyLinkedList<Long[]> appliedModifications, boolean outermostCall) throws Exception {
		this.exportToDOTandPNG("pre");
		
		System.out.println("replaceInSubtree");
		System.out.println("\treplace: ");
		System.out.println("\t\troot: "+root);
		System.out.println("\t\tvictim: "+ victim);
		System.out.println("\t\treplacement: "+ replacement);
		if(root == replacement) {
			return false;
		}
		Node rootNode = nodesMap.get(root);
		boolean modificationFound = true;
		while(modificationFound) {
			modificationFound = false;
			for(Edge e : rootNode.getOutgoingEdges(internalGraph, nodesMap)) {
				System.out.println("blub");
				try {
					if(e.dest == victim) {
							this.redirectEdge(root, victim, replacement);
							System.out.println("DONE SOMETHING");
							modificationFound = true;
							appliedModifications.add(new Long[] {root, victim, replacement});
							break;
					}
					else {
							// concatenate lists
							modificationFound = replaceInSubtree(e.dest, victim, replacement, appliedModifications, false);
							System.out.println("ELSE: "+ modificationFound + " "+e.dest+" "+root);
							System.out.println("len: "+appliedModifications.size());
						
					}
				}
				catch(IllegalArgumentException ex) {
					System.out.println("outermost: "+ outermostCall + " applMod: "+ appliedModifications.size());
					//in case that Edges would produce a cycle
					if(outermostCall) {
						if(appliedModifications.size() > 0)
							System.out.println("ROLLING BACK:");
						appliedModifications.invert();
						for(Long[] elem : appliedModifications) {
							System.out.println("\t"+elem[0]+": "+elem[1]+" -> "+ elem[2]);
						}
						for(Long[] elem : appliedModifications) {
							this.redirectEdge(elem[0], elem[2], elem[1]);
							System.out.println("unroll");
						}
						return false;
					}
					else {
						throw ex;
					}
				}
			}
		}	
		if(appliedModifications.size() != 0) {
			System.out.println("Applied: ");
			for(Long[] elem : appliedModifications) {
				System.out.println("\t"+elem[0]+": "+elem[1]+" -> "+ elem[2]);
			}
		}
		return true;
	}
	*/
	
}
