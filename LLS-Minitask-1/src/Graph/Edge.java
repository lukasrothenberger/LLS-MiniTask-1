package Graph;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;

public class Edge extends DefaultEdge {
	public long source;
	public long dest;
	public int weight;

	/**
	 * Constructor for an Edge from source to dest.
	 * Extends jgrapht.DefaultEdge.
	 * @param source
	 * @param dest
	 * @param inverted
	 */
	public Edge(long source, long dest) {
		super();
		this.source = source;
		this.dest = dest;
		this.weight = 1;
	}

	/**
	 * Create and Return a textual representation of an edge for printing.
	 */
	public String toString() {
		return "Edge: "+source+" --> "+dest+"  weight: "+this.weight;
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
	 * get edge attributes for DOT representation (e.g. edge color, weight etc)
	 * @return
	 */
	public Map<String, Attribute> getDOTAttributes() {
		Map<String, Attribute> DOTAttributesMap = new HashMap<String, Attribute>();
		if(this.weight > 1 ) {
			DOTAttributesMap.put("penwidth", new DOTAttribute("3"));
		}
		return DOTAttributesMap;
	}
}
