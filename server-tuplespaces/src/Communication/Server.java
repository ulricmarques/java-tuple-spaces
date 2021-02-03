package Communication;

import River.ProcessThread;
import River.SpaceController;
import River.Tuples.ClientMessage;
import River.Tuples.ClientMessage.Action;
import River.Tuples.Cloud;
import River.Tuples.Root;
import River.Tuples.Host;
import River.Tuples.Migration;
import River.Tuples.VirtualMachine;
import River.Tuples.Process;
import River.Tuples.ProcessMessage;

/**
 *
 * @author Ulric
 */
public class Server implements Runnable {
       
    public SpaceController spaceController;
    
    public Server() {
        spaceController = new SpaceController();
        spaceController.addTuple();
        spaceController.addTuple("nuvem1");
        spaceController.addTuple("nuvem1", "host1");
        spaceController.addTuple("nuvem1", "host1", "vm1");
        spaceController.addTuple("nuvem2");
        spaceController.addTuple("nuvem2", "host1");
        spaceController.addTuple("nuvem2", "host1", "vm1");
        
        spaceController.printSpace();
        
        spaceController.createClientList();
    }
      
    
    @Override
    public void run() {
        this.startListener();
    }
    
    public void startListener(){
        while (true) {
            try {
            	ClientMessage template = new ClientMessage();
                ClientMessage message = (ClientMessage) spaceController.space.take(template, null, Long.MAX_VALUE);
                if (message == null) {
                    System.out.println("Timeout error.");
                }
                else{
                	System.out.println("Recebeu mensagem do cliente: " + message.name + " | Action: " + message.action);
                	Action currentAction = (Action) message.action;
                	if(currentAction.equals(Action.CONNECT)) {
                		String clientName = message.name;
                		spaceController.addClient(clientName);
                		spaceController.printClients();
                	} 
                	else if(currentAction.equals(Action.UPDATE)) {
                		Root cloudList = spaceController.getCloudList();
                		spaceController.sendToClient(message.name, cloudList, River.Tuples.ServerMessage.Action.UPDATE);
                	} 
                	else if(currentAction.equals(Action.ADD)) {
                		if(message.content instanceof Cloud) {
                			Cloud cloud = (Cloud) message.content;
                			spaceController.addTuple(cloud.name);
                		}
                		if(message.content instanceof Host) {              			  			
                			Host host = (Host) message.content;              
                			spaceController.addTuple(host.parent.name, host.name);                			
                		}
                		if(message.content instanceof VirtualMachine) {
                			VirtualMachine virtualMachine = (VirtualMachine) message.content;
                			spaceController.addTuple(virtualMachine.parent.parent.name, virtualMachine.parent.name,
                					virtualMachine.name);               		
                		}
                		if(message.content instanceof Process) {               			
                			Process process = (Process) message.content;
                			spaceController.addTuple(process.parent.parent.parent.name, process.parent.parent.name,
                					process.parent.name, process.name );
                			ProcessThread processThread = new ProcessThread(message.name, process, spaceController);
                    		new Thread(processThread).start();
                		}              		             		
                		Root cloudList = spaceController.getCloudList();
                		spaceController.sendToClient(message.name, cloudList, River.Tuples.ServerMessage.Action.UPDATE);
                		spaceController.printSpace();
                	} 
                	else if(currentAction.equals(Action.REMOVE)) {
                		if(message.content instanceof Cloud) {
        					Cloud cloud = (Cloud) message.content;
                			spaceController.removeTuple(cloud.name);
                		}
                		if(message.content instanceof Host) {              			  			
                			Host host = (Host) message.content;              
                			spaceController.removeTuple(host.parent.name, host.name);                			
                		}
                		if(message.content instanceof VirtualMachine) {
                			VirtualMachine virtualMachine = (VirtualMachine) message.content;
                			spaceController.removeTuple(virtualMachine.parent.parent.name, virtualMachine.parent.name,
                					virtualMachine.name);               		
                		}
                		if(message.content instanceof Process) {                			              			
                			Process process = (Process) message.content;               			
                			spaceController.removeTuple(process.parent.parent.parent.name, process.parent.parent.name,
                					process.parent.name, process.name );   		
                		}              		             		
                		Root cloudList = spaceController.getCloudList();
                		spaceController.sendToClient(message.name, cloudList, River.Tuples.ServerMessage.Action.UPDATE);
                		spaceController.printSpace();
                	}
                	else if(currentAction.equals(Action.MESSAGE)) {
                		ProcessMessage processMessage = (ProcessMessage) message.content;               		
                		spaceController.sendMessage(processMessage);              		
                	}
                	else if(currentAction.equals(Action.MIGRATE_HOST)) {
                		Migration migration = (Migration) message.content;
                		Cloud cloud = (Cloud) migration.destination;
                		Host host = (Host) migration.origin;
                		spaceController.migrateHost(cloud, host, migration.originalName);
                		
                		Root cloudList = spaceController.getCloudList();
                		spaceController.sendToClient(message.name, cloudList, River.Tuples.ServerMessage.Action.UPDATE);
                		spaceController.printSpace();
                	}  
                	else if(currentAction.equals(Action.MIGRATE_VIRTUAL_MACHINE)) {
                		Migration migration = (Migration) message.content;
                		Host host = (Host) migration.destination;
                		VirtualMachine virtualMachine = (VirtualMachine) migration.origin;
                		spaceController.migrateVirtualMachine(host, virtualMachine , migration.originalName);
                		
                		System.out.println("Host: " + host.name + " | Cloud: " + host.parent.name);
                		System.out.println("VM: " + virtualMachine.name + " | Host: " + virtualMachine.parent.name +
                				" | Cloud: " + virtualMachine.parent.parent.name);
                		
                		Root cloudList = spaceController.getCloudList();
                		spaceController.sendToClient(message.name, cloudList, River.Tuples.ServerMessage.Action.UPDATE);
                		spaceController.printSpace();
                	}
                	else if(currentAction.equals(Action.MIGRATE_PROCESS)) {
                		Migration migration = (Migration) message.content;
                		VirtualMachine virtualMachine = (VirtualMachine) migration.destination;
                		Process process = (Process) migration.origin;
                		spaceController.migrateProcess(virtualMachine, process, migration.originalName);
                		
                		Root cloudList = spaceController.getCloudList();
                		spaceController.sendToClient(message.name, cloudList, River.Tuples.ServerMessage.Action.UPDATE);
                		spaceController.printSpace();
                	}
                }           	
            } catch (Exception e) {
            	
            }
        }
    }

}
