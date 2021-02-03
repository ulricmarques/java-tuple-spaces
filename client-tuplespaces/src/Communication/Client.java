
package Communication;

import River.Tuples.ClientMessage.Action;
import River.Tuples.ClientMessage;
import River.Tuples.Cloud;
import River.Tuples.Host;
import River.Tuples.Process;
import River.Tuples.ProcessMessage;
import River.Tuples.Root;
import River.Tuples.ServerMessage;
import River.Tuples.VirtualMachine;
import UI.GUI;


/**
 *
 * @author Ulric
 */
public class Client {
    
    public String clientName;
  
    public GUI parentGUI;
    
    public SpaceController spaceController;
    
    public Client(String name, GUI parentGUI ){
        this.parentGUI = parentGUI;
        this.clientName = name;
        
        spaceController = new SpaceController();
        spaceController.Connect(this.clientName);
        
    }
       
    public void send(Object content, Action action) {
        spaceController.SendToServer(this.clientName, content, action);
    }
    
    public void startListener(){
       new Thread(new ListenerSocket()).start();
    }
    
    private class ListenerSocket implements Runnable {

        public ListenerSocket() {
        	
        }

        @Override
        public void run() {
        	while (true) {
                try {
                	ServerMessage template = new ServerMessage();
                	template.clientName = clientName;
                	ServerMessage message = (ServerMessage) spaceController.space.take(template, null, Long.MAX_VALUE);
                    if (message == null) {
                        System.out.println("Timeout error.");
                    }
                    else{
                    	System.out.println("Recebeu mensagem do servidor: " + message.action);
                    	River.Tuples.ServerMessage.Action currentAction = (River.Tuples.ServerMessage.Action) message.action;
                		if(currentAction.equals(River.Tuples.ServerMessage.Action.UPDATE)) {
                			Root currentRoot = (Root) message.content;
                            parentGUI.mainScreen.rebuildTree(currentRoot);
                    	} 
                    	else if(currentAction.equals(River.Tuples.ServerMessage.Action.MESSAGE)) {
                    		ProcessMessage processMessage = (ProcessMessage) message.content;                   		
                    		parentGUI.mainScreen.setDisplayText("(" + 
                    									processMessage.origin.name + ") enviou para (" +
                										processMessage.destination + "): " + 
                    									processMessage.text);                    		
                    	} 
                    }
                	
                } catch (Exception e) {
                	
                }
            }
        }
    }
}