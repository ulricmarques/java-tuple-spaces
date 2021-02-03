package River.Tuples;

import net.jini.core.entry.Entry;

public class ProcessMessage implements Entry {
	
	public String destination;
	public Process origin;
	public String text;
	
	public ProcessMessage() {
		
	}
	
	public ProcessMessage(String destination, Process origin, String text) {
		this.destination = destination;
		this.origin = origin;
		this.text = text;		
	}

}
