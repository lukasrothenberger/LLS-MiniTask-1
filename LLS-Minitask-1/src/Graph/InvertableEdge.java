package Graph;

import org.jgrapht.graph.DefaultEdge;

public class InvertableEdge extends DefaultEdge {
	public boolean inverted;
	public long source;
	public long dest;
	
	/**
	 * Constructor for an Invertable Edge from source to dest.
	 * Extends jgrapht.DefaultEdge.
	 * @param source
	 * @param dest
	 * @param inverted
	 */
	public InvertableEdge(long source, long dest, boolean inverted) {
		super();
		this.inverted = inverted;
		this.source = source;
		this.dest = dest;
	}
	
	/**
	 * Invert the "inverted"-value of an edge.
	 * Equivalent to "adding" resp. "removing" an inverter.
	 */
	public void invert() {
		this.inverted = !this.inverted;
	}
	
	/**
	 * Create and Return a textual representation of an edge for printing.
	 */
	public String toString() {
		return "Edge: "+source+" --> "+dest+"  Inverted:"+inverted;
	}
}
