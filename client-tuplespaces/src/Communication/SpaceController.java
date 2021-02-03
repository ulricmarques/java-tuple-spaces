package Communication;

import River.Tuples.ClientList;
import River.Tuples.ClientMessage;
import River.Tuples.Cloud;
import River.Tuples.Root;
import River.Tuples.ServerMessage;
import River.Tuples.ServerMessage.Action;
import River.Tuples.Host;
import River.Tuples.Process;
import River.Tuples.VirtualMachine;
import java.rmi.RemoteException;
import java.util.ArrayList;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

/**
 *
 * @author Ulric
 */
public class SpaceController {
    
    public JavaSpace space;
    
    public SpaceController(){
        System.out.println("Procurando pelo servico JavaSpace...");
        Lookup finder = new Lookup(JavaSpace.class);
        space = (JavaSpace) finder.getService();
        if (space == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
                System.exit(-1);
        } 
        System.out.println("O servico JavaSpace foi encontrado.");
    }
    
    public void Connect(String name) {
    	ClientMessage clientMessage = new ClientMessage(name, null, River.Tuples.ClientMessage.Action.CONNECT);
    	
    	try {
			space.write(clientMessage, null, Lease.FOREVER);
		} catch (RemoteException | TransactionException e) {
			System.out.println("Error: " + e);
		}   	
    }
    
    public void Add(String name, Object content) {
    	ClientMessage clientMessage = new ClientMessage(name, content, River.Tuples.ClientMessage.Action.ADD);
    	try {
			space.write(clientMessage, null, Lease.FOREVER);
		} catch (RemoteException | TransactionException e) {
			System.out.println("Error: " + e);
		}   
    }
    
    public void SendToServer(String name, Object content, River.Tuples.ClientMessage.Action action) {	
    	System.out.println("Cliente " + name + " enviando mensangem...");
    	ClientMessage clientMessage = new ClientMessage(name, content, action);
    	try {
			space.write(clientMessage, null, Lease.FOREVER);
		} catch (RemoteException | TransactionException e) {
			System.out.println("Error: " + e);
		}  
    }
    
}
