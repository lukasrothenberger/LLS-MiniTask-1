package Graph;


public class Node {
	long id;
	NodeType type;
	boolean input = false;
	boolean output = false;
	
	
	public Node(long id, NodeType type) {
		this.id = id;
		this.type = type;
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
}
