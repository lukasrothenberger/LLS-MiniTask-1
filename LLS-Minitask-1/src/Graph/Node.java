package Graph;

import java.util.HashMap;
import java.util.Iterator;

import org.jgrapht.Graph;

public class Node {
	long id;
	NodeType type;
	NodeModifier modifier;
	boolean input = false;
	boolean output = false;
	
	
	public Node(long id, NodeType type, NodeModifier modifier) {
		this.id = id;
		this.type = type;
		this.modifier = modifier;
	}
	
	
	public String toString(){
		if(input) {
			return "IN_"+type.name()+"_"+id;
		}
		if(output) {
			return "OUT_"+type.name()+"_"+id;
		}
		return type.name()+"_"+id;
	}
	
	
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
}
