/*
 * Authors: Lukas Rothenberger, Pallavi Gutta Ravi
 */

package Graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;

public class Node {
	public long id;
	NodeType type;
	NodeModifier modifier;
	boolean input = false;
	boolean output = false;

	/**
	 * Constructor for a Node in the Graph.
	 * @param id
	 * @param type
	 * @param modifier
	 */
	public Node(long id, NodeType type, NodeModifier modifier) {
		this.id = id;
		this.type = type;
		this.modifier = modifier;
	}


	/**
	 * Textual representation of a node for printing.
	 */
	public String toString(){
		if(input) {
			return "IN_"+type.name()+"_"+id;
		}
		if(output) {
			return "OUT_"+type.name()+"_"+id;
		}
		return type.name()+"_"+id;
	}

	/**
	 * Translates node modifier and id into a value identifier in BLIF style (e.g. "i4", "o6" or "a8").
	 * @return node identifier
	 * @throws Exception
	 */
	public String toBLIFIdentifier() throws Exception {
		//returns blif "id" of node, e.g. a42
		switch(this.modifier) {
		case INPUT:{
			return "i"+this.id;
		}
		case OUTPUT:{
			return "o"+this.id;
		}
		case INTERMEDIATE:{
			return "a"+this.id;
		}
		default:{
			throw new Exception("Erroneous modifier: "+this.modifier+ " for node: "+this.id);
		}
		}
	}

	/**
	 * Creates a BLIF representation for the node.
	 * Returns a BLIF-Identifier (e.g. "i4"), if the node is a value node.
	 * Returns a BLIF subcircuit call (e.g. .subckt and2 ...), if the node represents a Gate.
	 * @param internalGraph
	 * @param nodesMap
	 * @return String representation of the node for use in BLIF file.
	 * @throws Exception
	 */
	public String toBLIF(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception{
		switch(this.modifier) {
		case INPUT:{
			if(this.type == NodeType.VAL){	
				return this.toBLIFIdentifier();
			}
			else {
				throw new Exception("Invalid Modifier VAL for INPUT node! node: "+this.id);
			}
		}
		case OUTPUT:
			switch(this.type) {
			case VAL:{
				if(this.getChildrenNodes(internalGraph, nodesMap).length == 1) {
					return ".names "+this.getChildrenNodes(internalGraph, nodesMap)[0].toBLIFIdentifier()+" "+this.toBLIFIdentifier()+"\n1 1\n";
				}
				else {
					return this.toBLIFIdentifier();
				}
			}
			}
		case INTERMEDIATE:{
			switch(this.type){
			case VAL:{
				return this.toBLIFIdentifier();
			}
			case AND:{
				// .subckt and2 A=v1 B=v2 O=x
				int count = 0;
				Node child1 = null;
				Node child2 = null;
				for(Edge e : internalGraph.edgesOf(this)) {
					if(e.source != this.id)
						continue;
					if(count == 0)
						child1 = nodesMap.get(e.dest);
					if(count == 1)
						child2 = nodesMap.get(e.dest);
					if(count > 1)
						throw new Exception("Incorrect number of children for AND node: "+ this.id);
					count++;
				}
				return ".subckt and2 A="+child1.toBLIFIdentifier()+" B="+child2.toBLIFIdentifier()+" O="+this.toBLIFIdentifier()+"\n";
			}
			case MAJ:{
				// .subckt maj3 A=v1 B=v2 C=v3 O=x
				int count = 0;
				Node child1 = null;
				Node child2 = null;
				Node child3 = null;
				for(Edge e : internalGraph.edgesOf(this)) {
					if(e.source != this.id)
						continue;
					int tmpWeight = e.weight;
					while(tmpWeight > 0) {
						if(count == 0)
							child1 = nodesMap.get(e.dest);
						if(count == 1)
							child2 = nodesMap.get(e.dest);
						if(count == 2)
							child3 = nodesMap.get(e.dest);
						if(count > 2) {
							throw new Exception("Incorrect number of children for MAJ node: "+ this.id);
						}
						count++;
						tmpWeight--;
					}
				}

				return ".subckt maj3 A="+child1.toBLIFIdentifier()+" B="+child2.toBLIFIdentifier()+" C="+child3.toBLIFIdentifier()+" O="+this.toBLIFIdentifier()+"\n";
			}
			case INV:{
				// .subckt inv A=x O=j
				int count = 0;
				Node child1 = null;
				for(Edge e : internalGraph.edgesOf(this)) {
					if(e.source != this.id)
						continue;
					if(count == 0)
						child1 = nodesMap.get(e.dest);
					if(count > 0)
						throw new Exception("Incorrect number of children for INV node: "+ this.id);
					count++;
				}
				return ".subckt inv A="+child1.toBLIFIdentifier()+" O="+this.toBLIFIdentifier()+"\n";
			}
			}
			break;
		}
		}
		throw new Exception("Something went wrong.");
	}


	/**
	 * used in getDOTAttributes only
	 */
	private class DOTAttribute implements Attribute{
		String value;
		private DOTAttribute(String value) {
			super();
			this.value = value;
		}
		@Override
		public AttributeType getType() {
			return AttributeType.STRING;
		}
		@Override
		public String getValue() {
			return this.value;
		}

	}

	/**
	 * get node attributes for DOT representation (e.g. node color, shape etc)
	 * @return
	 */
	public Map<String, Attribute> getDOTAttributes() {	
		Map<String, Attribute> DOTAttributesMap = new HashMap<String, Attribute>();
		if(this.type == NodeType.INV) {
			DOTAttributesMap.put("fillcolor", new DOTAttribute("lightgrey"));
		}
		else {
			if(this.modifier == NodeModifier.INPUT) {
				DOTAttributesMap.put("fillcolor", new DOTAttribute("green"));
			}
			else if(this.modifier == NodeModifier.OUTPUT) {
				DOTAttributesMap.put("fillcolor", new DOTAttribute("orange"));
			}
			else {
				DOTAttributesMap.put("fillcolor", new DOTAttribute("white"));
			}
		}
		DOTAttributesMap.put("style", new DOTAttribute("filled"));
		return DOTAttributesMap;
	}

	/**
	 * returns the amounts of connected children nodes by type. <br>
	 * 	counts[0] := value count <br>
	 *	counts[1] := majority count <br>
	 *	counts[2] := inverter count 
	 * @param internalGraph
	 * @param nodesMap
	 * @return
	 */
	public int[] getCounts(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		int counts[] = {0,0,0};
		for(Edge ie : internalGraph.edgesOf(this)) {
			if(ie.dest == this.id)
				continue;
			Node child = nodesMap.get(ie.dest);
			if(child.type == NodeType.VAL)
				counts[0]++;
			else if(child.type == NodeType.MAJ)
				counts[1]++;
			else if(child.type == NodeType.INV)
				counts[2]++;
		}
		return counts;
	}


	public boolean associativityPossible(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		int[] counts = getCounts(internalGraph, nodesMap);
		if(counts[1]+counts[0]+counts[2] == 3 && counts[1] > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Create and return an array filled with the connected input nodes of a gate represented by the respective node object.
	 * @param internalGraph
	 * @param nodesMap
	 * @return
	 */
	public Node[] getChildrenNodes(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		List<Node> resultList = new LinkedList<Node>();
		for(Edge ie : this.getOutgoingEdges(internalGraph, nodesMap)) {
			resultList.add(nodesMap.get(ie.dest));
		}
		//convert List of arbitrary size to array
		Node[] resultArray = new Node[resultList.size()];
		resultArray = resultList.toArray(resultArray);
		return resultArray;
	}

	/**
	 * Creates and returns an array of incoming InvertableEdges for the node.
	 * Incoming edges are those that connect the current node as an input to another node.
	 * @param internalGraph
	 * @param nodesMap
	 * @return
	 */
	public Edge[] getIncomingEdges(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		List<Edge> resultList = new LinkedList<Edge>();
		for(Edge ie : internalGraph.edgesOf(this)) {
			if(ie.dest == this.id) {
				// incoming edge
				resultList.add(ie);
			}
			else {
				//outgoing edge
				continue;
			}
		}
		//convert list of arbitrary length to array
		Edge[] resultArray = new Edge[resultList.size()];
		resultArray = resultList.toArray(resultArray);
		return resultArray;
	}

	/**
	 * Creates and returns an array of outgoing InvertableEdges for the node.
	 * Outgoing edges are those connecting the current node to it's inputs.
	 * @param internalGraph
	 * @param nodesMap
	 * @return
	 */
	public Edge[] getOutgoingEdges(Graph<Node, Edge> internalGraph, HashMap<Long, Node> nodesMap) {
		List<Edge> resultList = new LinkedList<Edge>();
		for(Edge ie : internalGraph.edgesOf(this)) {
			if(ie.dest == this.id) {
				// incoming edge
				continue;
			}
			else {
				//outgoing edge
				resultList.add(ie);
			}
		}
		//convert list of arbitrary length to array
		Edge[] resultArray = new Edge[resultList.size()];
		resultArray = resultList.toArray(resultArray);
		return resultArray;
	}


	public long cloneNode(GraphWrapper GW, long cloneID) throws Exception {
		Node newNode = new Node(cloneID, this.type, this.modifier);
		newNode.input = this.input;
		newNode.output = this.output;
		GW.internalGraph.addVertex(newNode);
		GW.nodesMap.put(cloneID, newNode);
		return cloneID;
	}
}
