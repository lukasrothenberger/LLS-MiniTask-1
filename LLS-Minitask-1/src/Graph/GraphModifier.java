package Graph;

public class GraphModifier {
	private GraphWrapper gw;
	public GraphModifier(GraphWrapper gw) {
		this.gw = gw;
	}
	
	public void iterationExample() {
		for(long nodeID : gw.nodesMap.keySet()) {
			Node node = gw.nodesMap.get(nodeID);
			if(node.associativityPossible(gw.internalGraph, gw.nodesMap)) {
				// execute associativity for node
			}
		}
	}
}
