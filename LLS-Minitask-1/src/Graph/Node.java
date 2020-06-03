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
		return type.name()+"_"+id;
	}
}
