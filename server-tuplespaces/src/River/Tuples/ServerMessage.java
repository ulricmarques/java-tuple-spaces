package River.Tuples;

import net.jini.core.entry.Entry;

/**
*
* @author Ulric
*/
public class ServerMessage implements Entry {
	
	
	public String clientName;
	public Object content;
    public Action action;
    
    public ServerMessage() {
    	
    }
    
    public ServerMessage(String clientName, Object content, Action action) {
    	this.clientName = clientName;
    	this.content = content;
    	this.action = action;
    }
       
    public enum Action {
    	CONNECT, UPDATE, ADD, REMOVE,
    	MIGRATE_HOST, MIGRATE_VIRTUAL_MACHINE, 
    	MIGRATE_PROCESS, MESSAGE
    }

}
