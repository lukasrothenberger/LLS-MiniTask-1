package Graph;

import org.jgrapht.graph.DefaultEdge;

public class InvertableEdge extends DefaultEdge {
	public boolean inverted;
	public long source;
	public long dest;
	
	public InvertableEdge(long source, long dest, boolean inverted) {
		super();
		this.inverted = inverted;
		this.source = source;
		this.dest = dest;
	}
	
	
	public void invert() {
		this.inverted = !this.inverted;
	}
	
	public String toString() {
		return "Edge: "+source+" --> "+dest+"  Inverted:"+inverted;
	}
}
