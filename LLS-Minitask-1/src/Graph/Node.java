package Graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;

public class Node {
	long id;
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
	public String toBLIF(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) throws Exception{
		String result = "";
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
						for(InvertableEdge e : internalGraph.edgesOf(this)) {
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
						for(InvertableEdge e : internalGraph.edgesOf(this)) {
							if(e.source != this.id)
								continue;
							if(count == 0)
								child1 = nodesMap.get(e.dest);
							if(count == 1)
								child2 = nodesMap.get(e.dest);
							if(count == 2)
								child3 = nodesMap.get(e.dest);
							if(count > 2)
								throw new Exception("Incorrect number of children for MAJ node: "+ this.id);
							count++;
						}
						return ".subckt maj3 A="+child1.toBLIFIdentifier()+" B="+child2.toBLIFIdentifier()+" C="+child3.toBLIFIdentifier()+" O="+this.toBLIFIdentifier()+"\n";
					}
					case INV:{
						// .subckt inv A=x O=j
						int count = 0;
						Node child1 = null;
						for(InvertableEdge e : internalGraph.edgesOf(this)) {
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

	
	private int[] getCounts(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		// counts[0] == val_count
		// counts[1] == maj_count
		// counts[2] == inv_count
		int counts[] = {0,0,0};
		for(InvertableEdge ie : internalGraph.edgesOf(this)) {
			if(ie.dest == this.id)
				continue;
			Node child = nodesMap.get(ie.dest);
			if(child.type == NodeType.VAL)
				counts[0]++;
			if(child.type == NodeType.MAJ)
				counts[1]++;
			if(child.type == NodeType.INV)
				counts[2]++;
		}
		return counts;
	}
	
	
	public boolean associativityPossible(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		int[] counts = getCounts(internalGraph, nodesMap);
		if(counts[0] == 2 && counts[1] == 1) {
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
	public Node[] getChildrenNodes(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		List<Node> resultList = new LinkedList<Node>();
		for(InvertableEdge ie : this.getOutgoingEdges(internalGraph, nodesMap)) {
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
	public InvertableEdge[] getIncomingEdges(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		List<InvertableEdge> resultList = new LinkedList<InvertableEdge>();
		for(InvertableEdge ie : internalGraph.edgesOf(this)) {
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
		InvertableEdge[] resultArray = new InvertableEdge[resultList.size()];
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
	public InvertableEdge[] getOutgoingEdges(Graph<Node, InvertableEdge> internalGraph, HashMap<Long, Node> nodesMap) {
		List<InvertableEdge> resultList = new LinkedList<InvertableEdge>();
		for(InvertableEdge ie : internalGraph.edgesOf(this)) {
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
		InvertableEdge[] resultArray = new InvertableEdge[resultList.size()];
		resultArray = resultList.toArray(resultArray);
		return resultArray;
	}
}
