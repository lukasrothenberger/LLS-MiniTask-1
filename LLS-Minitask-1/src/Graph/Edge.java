package Graph;

import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {
	public long source;
	public long dest;
	public long edgeId;
	
	/**
	 * Constructor for an Edge from source to dest.
	 * Extends jgrapht.DefaultEdge.
	 * @param source
	 * @param dest
	 * @param dest2 
	 * @param inverted
	 */
	public Edge(long edgeId, long source, long dest) {
		super();
		this.source = source;
		this.dest = dest;
		this.edgeId = edgeId;
	}
	
	/**
	 * Create and Return a textual representation of an edge for printing.
	 */
	public String toString() {
		return "Edge: "+source+" --> "+dest;
	}
}
