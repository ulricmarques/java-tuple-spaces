package River.Tuples;

import net.jini.core.entry.Entry;

public class Migration implements Entry {
	
	public Object destination;
	public Object origin;
	public String originalName;

	public Migration () {
		
	}
	
	public Migration (Object destination, Object origin, String originalName) {
		this.destination = destination;
		this.origin = origin;
		this.originalName = originalName;
	}

}
